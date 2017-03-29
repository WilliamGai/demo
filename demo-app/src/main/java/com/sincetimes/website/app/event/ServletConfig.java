package com.sincetimes.website.app.event;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sincetimes.website.core.common.support.LogCore;

@Configuration
public class ServletConfig {
	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		LogCore.BASE.info("{} injected", ServletConfig.class.getSimpleName());
		return new ServletRegistrationBean(new MyServlet(), "/servlet");
	}
}
