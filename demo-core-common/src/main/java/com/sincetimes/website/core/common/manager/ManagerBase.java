package com.sincetimes.website.core.common.manager;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sincetimes.website.core.common.manager.annotation.ManangerInject;
import com.sincetimes.website.core.common.support.ClassUtil;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;

/**
 * 所有继承ManagerBase的已经注册为组件的子类都将注册到本类的instances变量中
 * <br/>
 * tips:如果使用外部tomcat可以用继承一个CommandLineRunner来对所有子类初始化
 */
@ManangerInject
public abstract class ManagerBase{
	public static final Map<String, ManagerBase> instances = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public static <T extends ManagerBase> T inst(Class<? extends ManagerBase> clazz) {
		Object inst = instances.get(clazz.getName());
		if (null == inst) {
			LogCore.BASE.error("{} not exist,all ManagerBase members:{}", clazz.getName(), instances);
			throw new RuntimeException(Util.format("get the Manager instance fail,class:{}", clazz));
		}
		return (T) inst;
	}

	public static void putInst(Class<?> clazz, Object inst) {
		LogCore.BASE.debug("ManagerBase.putInstances:({},{})", clazz, inst);
		if (!ClassUtil.isInstanceof(clazz, ManagerBase.class) || Modifier.isAbstract(clazz.getModifiers())) {// 再次检查
			throw new RuntimeException(Util.format("Manager put inst err,class={}", clazz));
		}
		if(clazz.getName().contains("CGLIB$$")){//被Spring的AOP代理 TODO:manager全部代理
			LogCore.BASE.error("遇到CGLIB动态代理!{},superClass is{}", Arrays.toString(clazz.getName().split("\\u0024\\u0024")),clazz.getSuperclass());
//			instances.put(clazz.getName().split("\\u0024\\u0024")[0], (ManagerBase) inst);
			instances.put(clazz.getSuperclass().getName().split("\\u0024\\u0024")[0], (ManagerBase) inst);
		}
		instances.put(clazz.getName(), (ManagerBase) inst);
	}

	/** 初始化，费时操作 */
	public abstract void init();

}
