package com.freda.config.spring.annotation;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FredaComponentScanRegistrar.class)
public @interface FredaComponentScan {
	
	@AliasFor("basePackages")
	String[] value() default {};

	@AliasFor("value")
	String[] basePackages() default {};

	Class<?>[] basePackageClasses() default {};
	
	
}
