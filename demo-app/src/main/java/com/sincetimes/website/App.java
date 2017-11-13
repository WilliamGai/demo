package com.sincetimes.website;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sincetimes.website.app.article.ArticleManager;
import com.sincetimes.website.app.jdbc.DruidConfig;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Sys;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.AppStarter;
import com.sincetimes.website.core.spring.HttpHeadUtil;
import com.sincetimes.website.core.spring.ioc.DemoBeanFactoryPostProcessor;
import com.sincetimes.website.core.spring.manger.SpringManager;
import com.sincetimes.website.redis.jedis.JedisPoolTemplate;

import de.codecentric.boot.admin.config.EnableAdminServer;

/**
 * 程序启动入口
 * 这个类不可以用Order注解
 */
@EnableAdminServer
@RestController
@SpringBootApplication(scanBasePackages={"com.sincetimes.website"})
public class App extends SpringBootServletInitializer{
	public static String START_TAG = "=================";

	@RequestMapping("/")
	@ResponseBody
	String home(HttpServletResponse resp){
		return "hello  app";
	}

	@RequestMapping("/welcome")
	@ResponseBody
	String method(HttpServletRequest req, HttpServletResponse resp) {
		LogCore.BASE.info("{}", HttpHeadUtil.getParamsMapLimit(req));
		return Util.format("welcome {}, dir is{}", SpringManager.inst().getSysProperty("app_name"), System.getProperty("user.dir"));
	}

	/**  外部tomcat启动的时候首先执行  */
	@Override
	public SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		LogCore.BASE.info(START_TAG + "configure", builder.sources(App.class));// AppConfig
		return builder.sources(App.class);
	}
	@Autowired
	JedisPoolTemplate jedisTemplate;
	
	/** 外部tomcat启动的时不执行此方法 {@link CommandLineRunner#run(String...)}*/
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
		AppStarter.initManagers(context);
		LogCore.BASE.info(START_TAG + "app started!,dir={},os={},args={}", Sys.getAppDir(), Sys.getOs(),
				Arrays.toString(args));
		LogCore.BASE.info(START_TAG + "profiles={}", Arrays.toString(context.getEnvironment().getActiveProfiles()));
		LogCore.BASE.info(START_TAG + "app_name={}", SpringManager.inst().getSysProperty("app_name"));// application.properties
		LogCore.BASE.info(START_TAG + "server.port={}", SpringManager.inst().getSysProperty("server.port"));// application.properties
		LogCore.BASE.info(START_TAG + "upload_path={}", SpringManager.inst().getSysProperty("upload_path"));// application.properties
		LogCore.BASE.info(START_TAG + "http://localhost:{}/login", SpringManager.inst().getSysProperty("server.port"));
		LogCore.BASE.info(START_TAG + "test App={}", context.getBean(App.class));
		LogCore.BASE.info(START_TAG + "test DruidConfig={}", context.getBean(DruidConfig.class));
		LogCore.BASE.info(START_TAG + "test DemoBeanFactoryPostProcessor={}", context.getBean(DemoBeanFactoryPostProcessor.class));
		LogCore.BASE.info(START_TAG + "test ArticleManager={}", context.getBean(ArticleManager.class));

	}
}
