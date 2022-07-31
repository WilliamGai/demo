package com.sonic.website.redis.jedis.annoation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** redis database选择的注解 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RedisDataBaseSelect {
    int value() default 1;
}
