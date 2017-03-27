package com.sincetimes.website.core.common.manager.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** 用来标记manager的注解,此注解会传递给其子类 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ManangerInject {

}
