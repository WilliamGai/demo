package com.sonic.website.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

import com.sonic.website.core.common.manager.annotation.ManangerOrder;



@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@ManangerOrder
public @interface CompomentTest {

	String value() default "";
}