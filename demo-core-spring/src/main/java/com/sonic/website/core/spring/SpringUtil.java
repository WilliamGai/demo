package com.sonic.website.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.sonic.website.core.common.support.LogCore;

@Component
public class SpringUtil implements ApplicationContextAware {
	private static ApplicationContext context = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
		LogCore.BASE.info("init globle context:{}", context.getClass());
	}

	public static ApplicationContext getContext() {
		return context;
	}

	public Object getBean(String beanId) {
		return context.getBean(beanId);
	}

	public <T> T getBean(Class<T> requiredType) {
		return context.getBean(requiredType);
	}
}
