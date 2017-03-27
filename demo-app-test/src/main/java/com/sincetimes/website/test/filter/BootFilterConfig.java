package com.sincetimes.website.test.filter;

import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootFilterConfig {
//	@Bean
//	public FilterRegistrationBean getBootFilter(){
//		FilterRegistrationBean filterBean = new FilterRegistrationBean();
//		filterBean.setName("testfilter");
//		return filterBean;
//	}
//	
//	@Bean 
//	public RemoteIpFilter getIpFilter(){
//		return new RemoteIpFilter();
//	}
}
