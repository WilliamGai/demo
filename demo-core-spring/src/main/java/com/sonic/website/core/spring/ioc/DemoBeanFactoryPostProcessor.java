package com.sonic.website.core.spring.ioc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/***
 * 作用是让ManangerInject注入SpringIOC
 * @author bao
 * @date 2017年10月15日 下午9:29:31
 */
@Component
public class DemoBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {
	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		DemoScanner scanner = new DemoScanner((BeanDefinitionRegistry) beanFactory);
		scanner.setResourceLoader(this.applicationContext);
		scanner.scan("com.sonic");// org.sonic 或者空的双引号
	}
}
