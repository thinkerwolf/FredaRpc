package com.freda.config.annotation;

import java.lang.annotation.*;

/**
 * @author wukai
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Service {
	/** service id */
	String id();

	/** interface full name */
	Class<?> interfaceClass();

}
