package com.freda.common.annotation;

import java.lang.annotation.*;

/**
 * 暴漏类注解
 * @author wukai
 * 
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Service {
    String value() default "";
}
