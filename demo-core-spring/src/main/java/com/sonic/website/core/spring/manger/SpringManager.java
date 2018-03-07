package com.sonic.website.core.spring.manger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.core.common.manager.annotation.ManangerOrder;
import com.sonic.website.core.common.support.LogCore;

/**
 * 这也看以看作SpringBeanUtil
 */
@ManangerOrder(value = -1)
public class SpringManager extends ManagerBase implements ApplicationListener<ContextRefreshedEvent> {

	@Value("${upload_path:./}")
	public String upload_path;

	@Autowired
	private Environment env;// 这个类自己没有序列化

	private ApplicationContext applicationContext = null;

	public static SpringManager inst() {
		return ManagerBase.inst(SpringManager.class);
	}

	public Environment getEnvironment() {
		return env;
	}

	public String getSysProperty(String key) {
		return env.getProperty(key);
	}

	public <T> T getSysProperty(String key, Class<T> targetType, T defaultValue) {
		return env.getProperty(key, targetType, defaultValue);
	}

	public Object getEnvironmentJsonStr() {
		return JSON.toJSONString(env, SerializerFeature.PrettyFormat, SerializerFeature.WriteClassName,
				SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (applicationContext == null) {
			applicationContext = event.getApplicationContext();
		}
		LogCore.BASE.info("springManager injected! {}", applicationContext);
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public Object getBean(String beanId) {
		return applicationContext.getBean(beanId);
	}

	public <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}

	@Override
	public void init() {
		LogCore.BASE.info("springManager init! {}", applicationContext);
	}
}
