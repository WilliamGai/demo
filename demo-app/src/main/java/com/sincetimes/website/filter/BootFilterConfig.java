package com.sincetimes.website.filter;

import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sincetimes.website.core.spring.HttpHeadUtil;
/** 
 *  example:
 *  <pre class="code">
 *  &#064;Bean
 * 	public FilterRegistrationBean getBootFilter()
 *	{
 *		FilterRegistrationBean filterBean = new FilterRegistrationBean();
 *		filterBean.setName("testfilter");
 *		return filterBean;
 *	}
 *	<pre>
 * @author BAO
 */
@Configuration
public class BootFilterConfig {
	/**
	 *  提供此过滤器后,获取的IP地址都是真实的IP地址,如果要手动控制获取IP地址的方法可以参考
	 *  {@link HttpHeadUtil#getRealIpAddr(request)}中的方法
	 */
	@Bean 
	public RemoteIpFilter getIpFilter(){
		return new RemoteIpFilter();
	}
}
