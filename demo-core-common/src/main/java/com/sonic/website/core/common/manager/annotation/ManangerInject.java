package com.sonic.website.core.common.manager.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用来标记manager的注解,此注解会传递给其子类<br>
 * 没有将Compoment放进来的原因一个是不希望依赖Spring。另一方面Compoment不是Inherited。而我们的ManagerInject是配置在ManagerBase上
 * 不过这个注解已经不再使用了
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ManangerInject {

}
