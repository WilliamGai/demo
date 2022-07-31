package com.sonic.website.app.event;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sonic.website.core.common.support.LogCore;

@Configuration
public class ServletConfig {
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        LogCore.BASE.info("{} injected", ServletConfig.class.getSimpleName());
        return new ServletRegistrationBean(new MyServlet(), "/servlet");
    }
    @Bean
    public ServletRegistrationBean eventTraces() {
        LogCore.BASE.info("{} injected", TraceServlet.class.getSimpleName());
        return new ServletRegistrationBean(new TraceServlet(), "/event_svlt_traces");
    }
}
