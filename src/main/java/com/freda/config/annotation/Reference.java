package com.freda.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Reference {
	/** service id */
	String id();

	/** interface full name */
	Class<?> interfaceClass();

	/** cluster */
	String cluster();

	/** retry nums */
	int retries();

	/** load balance */
	String balance();

	/** registry center */
	String registry();
}
