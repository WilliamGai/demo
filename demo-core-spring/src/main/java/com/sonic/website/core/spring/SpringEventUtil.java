package com.sonic.website.core.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.sonic.website.core.common.support.LogCore;

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
