package com.sonic.website.app.jdbc;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.sonic.website.core.common.support.LogCore;
/** 数据库连接池druid的配置 */
@Configuration
public class DruidConfig {

	@Bean
	public ServletRegistrationBean druidServlet(){
		return new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
	}
	@Bean
	public DataSource druidDataSource(
			@Value("${spring.datasource.driverClassName}") String driverClass,
			@Value("${spring.datasource.url}") String url,
			@Value("${spring.datasource.removeAbandoned}") boolean removeAbandoned,
			@Value("${spring.datasource.removeAbandonedTimeOut}") int removeAbandonedTimeOut,
			@Value("${spring.datasource.username}") String username,
			@Value("${spring.datasource.password}") String password){
		DruidDataSource ds = new DruidDataSource();
		ds.setDriverClassName(driverClass);
		ds.setUrl(url);
		ds.setRemoveAbandoned(removeAbandoned);
		ds.setRemoveAbandonedTimeout(removeAbandonedTimeOut);
		ds.setUsername(username);
		ds.setPassword(password);
		try {
			ds.setFilters("stat,wall");
		} catch (SQLException e) {
			e.printStackTrace();
			LogCore.BASE.error("druid err:{}", e);
		}
		LogCore.BASE.info("driverClass:{}", driverClass);
		LogCore.BASE.info("mysql (druid) Url:{}", url);
		LogCore.BASE.info("removeAbandoned:{}", removeAbandoned);
		LogCore.BASE.info("removeAbandonedTimeOut:{}", removeAbandonedTimeOut);
		LogCore.BASE.info("username:{}", username);
		LogCore.BASE.info("password:{}", password);
		return ds;
	}
	@Bean 
	public FilterRegistrationBean filterRegistrationBean(){
		FilterRegistrationBean fb = new FilterRegistrationBean();
		fb.setFilter(new WebStatFilter());
		fb.addUrlPatterns("/*");
		fb.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		return fb;
	}
}
