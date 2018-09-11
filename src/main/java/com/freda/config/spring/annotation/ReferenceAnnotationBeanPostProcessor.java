package com.freda.config.spring.annotation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.freda.config.annotation.Reference;
import com.freda.config.spring.ReferenceBean;

public class ReferenceAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
		implements MergedBeanDefinitionPostProcessor, PriorityOrdered, ApplicationContextAware {
	private static final Logger logger = LoggerFactory.getLogger(ReferenceAnnotationBeanPostProcessor.class);
	
	private ApplicationContext context;
	private int order = Ordered.LOWEST_PRECEDENCE - 2;
	private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<String, InjectionMetadata>(256);
	private final Map<String, ReferenceBean<?>> referenceBeanCache = new ConcurrentHashMap<>(256);
	
	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		if (beanType != null) {
			InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
			metadata.checkConfigMembers(beanDefinition);
		}
	}

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
			String beanName) throws BeansException {
		InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
		try {
			metadata.inject(bean, beanName, pvs);
		} catch (BeanCreationException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
		}
		return pvs;
	}

	private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
		// Fall back to class name as cache key, for backwards compatibility
		// with custom callers.
		String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
		// Quick check on the concurrent map first, with minimal locking.
		InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
		if (InjectionMetadata.needsRefresh(metadata, clazz)) {
			synchronized (this.injectionMetadataCache) {
				metadata = this.injectionMetadataCache.get(cacheKey);
				if (InjectionMetadata.needsRefresh(metadata, clazz)) {
					if (metadata != null) {
						metadata.clear(pvs);
					}
					try {
						metadata = buildReferenceMetadata(clazz);
						this.injectionMetadataCache.put(cacheKey, metadata);
					} catch (NoClassDefFoundError err) {
						throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName()
								+ "] for autowiring metadata: could not find class that it depends on", err);
					}
				}
			}
		}
		return metadata;
	}

	private InjectionMetadata buildReferenceMetadata(Class<?> clazz) {
		LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
		Class<?> targetClass = clazz;
		do {
			final LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();

			ReflectionUtils.doWithLocalFields(targetClass, new ReflectionUtils.FieldCallback() {
				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					Reference reference = AnnotationUtils.findAnnotation(field, Reference.class);
					if (reference != null) {
						if (Modifier.isStatic(field.getModifiers())) {
							if (logger.isWarnEnabled()) {
								logger.warn("Autowired annotation is not supported on static fields: " + field);
							}
							return;
						}
						currElements.add(new ReferenceFieldElement(field, reference));
					}
				}
			});

			ReflectionUtils.doWithLocalMethods(targetClass, new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
					if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
						return;
					}
					Reference reference = AnnotationUtils.findAnnotation(bridgedMethod, Reference.class);
					if (reference != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
						if (Modifier.isStatic(method.getModifiers())) {
							if (logger.isWarnEnabled()) {
								logger.warn("Autowired annotation is not supported on static methods: " + method);
							}
							return;
						}
						if (method.getParameterTypes().length == 0) {
							if (logger.isWarnEnabled()) {
								logger.warn("Autowired annotation should only be used on methods with parameters: " +
										method);
							}
						}
						PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
						currElements.add(new ReferenceMethodElement(method, reference, pd));
					}
				}
			});

			elements.addAll(0, currElements);
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);

		return new InjectionMetadata(clazz, elements);
	}
	
	/*private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
		if (ao.getAnnotations().length > 0) {
				AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, referenceAnnotationType);
				if (attributes != null) {
					return attributes;
				}
		}
		return null;
	}*/
	
	/**
	 * Class representing injection information about an annotated field.
	 */
	private class ReferenceFieldElement extends InjectionMetadata.InjectedElement {
		private Reference reference;
		public ReferenceFieldElement(Field field, Reference reference) {
			super(field, null);
			this.reference = reference;
		}
		
		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			Field field = (Field) this.member;
			ReferenceBean<?> referenceBean = buildReferenceBean(member, reference);
			referenceBean.afterPropertiesSet();
			Object value = referenceBean.getRef();
			if (value != null) {
				ReflectionUtils.makeAccessible(field);
				field.set(bean, value);
			}
		}
	}
	
	private ReferenceBean<?> buildReferenceBean(Member member, Reference reference) {
		Class<?> interfaceClass = reference.interfaceClass();
		if (void.class.equals(interfaceClass)) {
			interfaceClass = member.getDeclaringClass();
		}
		String id = reference.id();
		if (!StringUtils.hasText(id)) {
			id = interfaceClass.getName();
		}
		ReferenceBean<?> referenceBean = referenceBeanCache.get(id);
		if (referenceBeanCache.get(id) == null) {
			synchronized (referenceBeanCache) {
				if (referenceBeanCache.get(id) == null) {
					referenceBean = new ReferenceBean<>();
					referenceBean.setId(id);
					referenceBean.setInterface(interfaceClass.getName());
					referenceBean.setApplicationContext(context);
					referenceBean.setCluster(reference.cluster());
					referenceBean.setBalance(reference.balance());
					referenceBean.setRetries(reference.retries());
					try {
						referenceBean.afterPropertiesSet();
					} catch (Exception e) {
						throw new BeanCreationException("reference bean afterPropertiesSet error", e);
					}
					referenceBeanCache.put(id, referenceBean);
				}
			}
		}
		return referenceBean;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
	

	/**
	 * Class representing injection information about an annotated method.
	 */
	private class ReferenceMethodElement extends InjectionMetadata.InjectedElement {
		private Reference reference;

		public ReferenceMethodElement(Method method, Reference reference, PropertyDescriptor pd) {
			super(method, pd);
			this.reference = reference;
		}

		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			if (checkPropertySkipping(pvs)) {
				return;
			}
			Method method = (Method) this.member;
			ReferenceBean<?> referenceBean = buildReferenceBean(member, reference);
			Object value = referenceBean.getRef();
			if (value != null) {
				try {
					ReflectionUtils.makeAccessible(method);
					method.invoke(bean, value);
				} catch (InvocationTargetException ex) {
					throw ex.getTargetException();
				}
			}
		}
	}
	
}
