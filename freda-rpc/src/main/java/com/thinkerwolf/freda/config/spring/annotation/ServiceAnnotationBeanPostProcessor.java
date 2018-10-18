package com.thinkerwolf.freda.config.spring.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.thinkerwolf.freda.config.annotation.Service;
import com.thinkerwolf.freda.config.spring.ServiceBean;

import java.util.LinkedHashSet;
import java.util.Set;

class ServiceAnnotationBeanPostProcessor
        implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ResourceLoaderAware, BeanClassLoaderAware {
    /**
     * packages to scan
     */
    protected Set<String> scanPackages;

    protected Environment environment;

    protected ClassLoader classLoader;

    protected ResourceLoader resourceLoader;

    public ServiceAnnotationBeanPostProcessor(Set<String> scanPackages) {
        this.scanPackages = scanPackages;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<String> resolvePackagesToScan = resolvePackagesToScan(scanPackages);
        registerBeans(resolvePackagesToScan, registry);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private void registerBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false, environment,
                resourceLoader);
        BeanNameGenerator beanNameGenerator = resolveBeanNameGenerator(registry);
        scanner.setBeanNameGenerator(beanNameGenerator);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        for (String packageToScan : packagesToScan) {

            // Registers @Service Bean first
            scanner.scan(packageToScan);

            // Finds all BeanDefinitionHolders of @Service whether @ComponentScan scans or not.
            Set<BeanDefinitionHolder> beanDefinitionHolders = findBeanDefinitionHolders(scanner, packageToScan,
                    registry, beanNameGenerator);

            if (!CollectionUtils.isEmpty(beanDefinitionHolders)) {
                for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                    registerBean(beanDefinitionHolder, registry, scanner);
                }
            }
        }
    }

    protected void registerBean(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry,
                                ClassPathBeanDefinitionScanner scanner) {
        Class<?> beanClass = resolveClass(beanDefinitionHolder);
        Service service = (Service) AnnotationUtils.findAnnotation(beanClass, Service.class);
        String annotatedServiceBeanName = beanDefinitionHolder.getBeanName();
        String beanName = generateBeanName(service, annotatedServiceBeanName);
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }
        AbstractBeanDefinition beanDefinition = buildServiceBeanDefinition(service, registry, annotatedServiceBeanName);
        if (registry.containsBeanDefinition(beanName)) {
            throw new IllegalStateException("Duplicate spring bean id " + beanName);
        }
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    protected Class<?> resolveClass(BeanDefinitionHolder beanDefinitionHolder) {
        BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
        String beanClassName = beanDefinition.getBeanClassName();
        try {
            return Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("");
        }
    }

    private Set<BeanDefinitionHolder> findBeanDefinitionHolders(ClassPathBeanDefinitionScanner scanner,
                                                                String packageToScan, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);
        Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<BeanDefinitionHolder>(
                beanDefinitions.size());
        for (BeanDefinition beanDefinition : beanDefinitions) {
            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
            BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            beanDefinitionHolders.add(beanDefinitionHolder);
        }
        return beanDefinitionHolders;

    }

    private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {

        BeanNameGenerator beanNameGenerator = null;

        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry singletonBeanRegistry = SingletonBeanRegistry.class.cast(registry);
            beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry
                    .getSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR);
        }

        if (beanNameGenerator == null) {
            beanNameGenerator = new AnnotationBeanNameGenerator();

        }
        return beanNameGenerator;
    }

    private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
        Set<String> resolvedPackagesToScan = new LinkedHashSet<String>(packagesToScan.size());
        for (String packageToScan : packagesToScan) {
            if (StringUtils.hasText(packageToScan)) {
                String resolvedPackageToScan = environment.resolvePlaceholders(packageToScan.trim());
                resolvedPackagesToScan.add(resolvedPackageToScan);
            }
        }
        return resolvedPackagesToScan;
    }

    private String generateBeanName(Service service, String annotatedServiceBeanName) {
        Class<?> interfaceClass = service.interfaceClass();
        String beanName = interfaceClass.getName() + "$" + annotatedServiceBeanName;
        return beanName;
    }

    private AbstractBeanDefinition buildServiceBeanDefinition(Service service, BeanDefinitionRegistry registry,
                                                              String annotatedServiceBeanName) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ServiceBean.class);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        Class<?> interfaceClass = service.interfaceClass();
        String id = service.id();
        if (!StringUtils.hasText(id)) {
            id = annotatedServiceBeanName;
        }
        builder.addPropertyValue("id", id);
        builder.addPropertyValue("interfaceClass", interfaceClass);
        builder.addPropertyValue("servers", service.servers());
        addPropertyReference(builder, "ref", annotatedServiceBeanName);
        return beanDefinition;
    }

    private void addPropertyReference(BeanDefinitionBuilder builder, String propertyName, String beanName) {
        String resolvedBeanName = environment.resolvePlaceholders(beanName);
        builder.addPropertyReference(propertyName, resolvedBeanName);
    }

}
