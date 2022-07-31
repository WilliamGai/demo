package com.sonic.website.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
/**
 * 拦截器
 */
@Configuration
public class BootWebAppConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new BootLoginInterceptor()).addPathPatterns("/mg/**");
        registry.addInterceptor(new BootInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
