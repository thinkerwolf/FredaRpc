package com.freda.config.spring.annotation;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
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
	private Class<? extends Annotation> referenceAnnotationType = Reference.class;
	
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
						metadata = buildAutowiringMetadata(clazz);
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

	private InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
		LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
		Class<?> targetClass = clazz;
		do {
			final LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();

			ReflectionUtils.doWithLocalFields(targetClass, new ReflectionUtils.FieldCallback() {
				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					AnnotationAttributes ann = findAutowiredAnnotation(field);
					if (ann != null) {
						if (Modifier.isStatic(field.getModifiers())) {
							if (logger.isWarnEnabled()) {
								logger.warn("Autowired annotation is not supported on static fields: " + field);
							}
							return;
						}
						currElements.add(new AutowiredFieldElement(field, ann, true));
					}
				}
			});

			/*ReflectionUtils.doWithLocalMethods(targetClass, new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
					if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
						return;
					}
					AnnotationAttributes ann = findAutowiredAnnotation(bridgedMethod);
					if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
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
						currElements.add(new AutowiredMethodElement(method, true, pd));
					}
				}
			});*/

			elements.addAll(0, currElements);
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);

		return new InjectionMetadata(clazz, elements);
	}
	
	private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
		if (ao.getAnnotations().length > 0) {
				AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, referenceAnnotationType);
				if (attributes != null) {
					return attributes;
				}
		}
		return null;
	}
	
	/**
	 * Class representing injection information about an annotated field.
	 */
	private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {

		private final boolean required;

		private volatile boolean cached = false;

		private volatile Object cachedFieldValue;
		
		private AnnotationAttributes ann;
		
		public AutowiredFieldElement(Field field, AnnotationAttributes ann, boolean required) {
			super(field, null);
			this.required = required;
			this.ann = ann;
		}
		
		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			Field field = (Field) this.member;
			// 生成一个ReferenceBean
			ReferenceBean<?> referenceBean = buildReferenceBean(ann);
			referenceBean.afterPropertiesSet();
			Object value = referenceBean.getRef();
			if (value != null) {
				ReflectionUtils.makeAccessible(field);
				field.set(bean, value);
			}
		}
		
	}
	
	private ReferenceBean<?> buildReferenceBean(AnnotationAttributes ann) {
		Class<?> interfaceClass = ann.getClass("interfaceClass");
		String id = ann.getString("id");
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
					referenceBean.setCluster(ann.getString("cluster"));
					referenceBean.setBalance(ann.getString("balance"));
					referenceBean.setRetries(ann.getNumber("retries"));
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
	 
	private class AutowiredMethodElement extends InjectionMetadata.InjectedElement {

		private final boolean required;

		private volatile boolean cached = false;

		private volatile Object[] cachedMethodArguments;

		public AutowiredMethodElement(Method method, boolean required, PropertyDescriptor pd) {
			super(method, pd);
			this.required = required;
		}

		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			if (checkPropertySkipping(pvs)) {
				return;
			}
			Method method = (Method) this.member;
			Object[] arguments;
			if (this.cached) {
				// Shortcut for avoiding synchronization...
				arguments = resolveCachedArguments(beanName);
			}
			else {
				Class<?>[] paramTypes = method.getParameterTypes();
				arguments = new Object[paramTypes.length];
				DependencyDescriptor[] descriptors = new DependencyDescriptor[paramTypes.length];
				Set<String> autowiredBeans = new LinkedHashSet<String>(paramTypes.length);
				TypeConverter typeConverter = beanFactory.getTypeConverter();
				for (int i = 0; i < arguments.length; i++) {
					MethodParameter methodParam = new MethodParameter(method, i);
					DependencyDescriptor currDesc = new DependencyDescriptor(methodParam, this.required);
					currDesc.setContainingClass(bean.getClass());
					descriptors[i] = currDesc;
					try {
						Object arg = beanFactory.resolveDependency(currDesc, beanName, autowiredBeans, typeConverter);
						if (arg == null && !this.required) {
							arguments = null;
							break;
						}
						arguments[i] = arg;
					}
					catch (BeansException ex) {
						throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(methodParam), ex);
					}
				}
				synchronized (this) {
					if (!this.cached) {
						if (arguments != null) {
							this.cachedMethodArguments = new Object[paramTypes.length];
							for (int i = 0; i < arguments.length; i++) {
								this.cachedMethodArguments[i] = descriptors[i];
							}
							registerDependentBeans(beanName, autowiredBeans);
							if (autowiredBeans.size() == paramTypes.length) {
								Iterator<String> it = autowiredBeans.iterator();
								for (int i = 0; i < paramTypes.length; i++) {
									String autowiredBeanName = it.next();
									if (beanFactory.containsBean(autowiredBeanName)) {
										if (beanFactory.isTypeMatch(autowiredBeanName, paramTypes[i])) {
											this.cachedMethodArguments[i] = new ShortcutDependencyDescriptor(
													descriptors[i], autowiredBeanName, paramTypes[i]);
										}
									}
								}
							}
						}
						else {
							this.cachedMethodArguments = null;
						}
						this.cached = true;
					}
				}
			}
			if (arguments != null) {
				try {
					ReflectionUtils.makeAccessible(method);
					method.invoke(bean, arguments);
				}
				catch (InvocationTargetException ex){
					throw ex.getTargetException();
				}
			}
		}

		private Object[] resolveCachedArguments(String beanName) {
			if (this.cachedMethodArguments == null) {
				return null;
			}
			Object[] arguments = new Object[this.cachedMethodArguments.length];
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = resolvedCachedArgument(beanName, this.cachedMethodArguments[i]);
			}
			return arguments;
		}
	}*/
	
}
