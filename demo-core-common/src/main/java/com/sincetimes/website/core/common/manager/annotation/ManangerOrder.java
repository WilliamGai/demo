package com.sincetimes.website.core.common.manager.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**manager加载顺序的注解 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ManangerOrder {
	/**
	 * The order value. the smaller the the earlier
	 * <p>Default is Integer.Max
	 */
	int value() default Integer.MAX_VALUE;
}
