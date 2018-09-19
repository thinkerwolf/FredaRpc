package com.freda.config.annotation;

import com.freda.common.Constants;
import com.freda.config.spring.annotation.FredaComponentScanRegistrar;
import com.freda.config.spring.annotation.ReferenceAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;

import java.lang.annotation.*;

/**
 * With spring {@link Autowired} and
 * {@link AutowiredAnnotationBeanPostProcessor} as references
 *
 * @author wukai
 * @see ReferenceAnnotationBeanPostProcessor
 * @see FredaComponentScanRegistrar
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Reference {
	/**
	 * service id
	 */
	String id() default "";

	/**
	 * interface full name
	 */
	Class<?> interfaceClass() default void.class;

	/**
	 * cluster
	 */
	String cluster() default Constants.DEFAULT_CLUSTER_TYPE;

	/**
	 * retry nums
	 */
	int retries() default Constants.DEFAULT_RETRY_TIMES;

	/**
	 * load balance
	 */
	String balance() default Constants.DEFAULT_BALANCE_TYPE;

	/**
	 * registry center
	 */
	String registries() default "";

	/**
	 * reference clients
	 */
	String clients() default "";

	/**
	 * is async invoke
	 * @return
	 */
	boolean async() default false;

}
