package com.sincetimes.website.core.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.manager.annotation.ManangerOrder;
import com.sincetimes.website.core.common.support.LogCore;

/**
 * 这也看以看作SpringBeanUtil
 */
@Component
public class SpringEventUtil implements ApplicationListener<ContextRefreshedEvent> {
	private ApplicationContext applicationContext = null;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (applicationContext == null) {
			applicationContext = event.getApplicationContext();
		}
		LogCore.BASE.info("springEvent injected! {}", applicationContext);
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
}
