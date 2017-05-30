package com.sincetimes.website.core.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StopWatch;

import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.manager.annotation.ManangerInject;
import com.sincetimes.website.core.common.manager.annotation.ManangerOrder;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;

public final class AppStarter {
	 /**
     * 初始化managers,程序启动后触发
     */
    public static void initManagers(ApplicationContext context){
		Map<String, Object> map = context.getBeansWithAnnotation(ManangerInject.class);
		StopWatch stopWatch = new StopWatch("App initialize");
		//此方式可以提炼出来
		List<Object> runners = new ArrayList<Object>();
		runners.addAll(map.values());
		runners.sort((a, b)->{
			int order_value_a = Util.toNullDefalut(a.getClass().getAnnotation(ManangerOrder.class), an->an.value(), Integer.MAX_VALUE/2);
			int order_value_b = Util.toNullDefalut(b.getClass().getAnnotation(ManangerOrder.class), an->an.value(), Integer.MAX_VALUE/2);
			return Integer.compare(order_value_a, order_value_b);
		});
		for (Object obj : runners) {
			String className =  obj.getClass().getSimpleName();
			/*Object order = obj.getClass().getAnnotation(ManangerOrder.class);
			LogCore.BASE.info("mananger orders {},{}" ,className, order);*/

			stopWatch.start(className);
			((ManagerBase)obj).init();
			ManagerBase.putInst(obj.getClass(), obj);
			stopWatch.stop();
		}
		LogCore.BASE.info("managers[] init used time:{}" ,stopWatch.prettyPrint());
		LogCore.BASE.info("first mananger is {}" ,runners.get(0));
		LogCore.BASE.info("port is {}" ,context.getEnvironment().getProperty("server.port"));
	}
}
