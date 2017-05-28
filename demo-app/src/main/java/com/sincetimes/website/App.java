package com.sincetimes.website;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Sys;
import com.sincetimes.website.core.spring.AppStarter;
import com.sincetimes.website.core.spring.HttpHeadUtil;
import com.sincetimes.website.core.spring.manger.SpringManager;

import de.codecentric.boot.admin.config.EnableAdminServer;


/**
 * 程序启动入口
 * 这个类不可以用Order注解
 */
@EnableAdminServer
@RestController
@SpringBootApplication(scanBasePackages={"com.sincetimes.website"})
public class App extends SpringBootServletInitializer implements CommandLineRunner {
	public static String START_TAG = "=================";

	@Value("${app_name:none_name}")
	public String app_name;
//	
	@Value("${server.port}")
	public String server_port;
	
	@Value("${upload_path}")
	public String upload_path;
	
	@RequestMapping("/")
	@ResponseBody
	String home(HttpServletResponse resp){
		return "hello  app";
	}

	@RequestMapping("/welcome")
	@ResponseBody
	String method(HttpServletRequest req, HttpServletResponse resp) {
		LogCore.BASE.info("{}", HttpHeadUtil.getParamsMap(req));
		return app_name + "welcome springboot test" + System.getProperty("user.dir");
	}

	/**  外部tomcat启动的时候首先执行  */
	@Override
	public SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		LogCore.BASE.info(START_TAG + "configure", builder.sources(App.class));// AppConfig
		return builder.sources(App.class);
	}

	/** 外部tomcat启动的时不执行此方法 */
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
		AppStarter.initManagers(context);
		LogCore.BASE.info(START_TAG + "app started!,dir={},os={},args={}", Sys.getAppDir(), Sys.getOs(), Arrays.toString(args));// AppConfig
		LogCore.BASE.info(START_TAG + "profiles={}", Arrays.toString(context.getEnvironment().getActiveProfiles()));// AppConfig
		String appName = SpringManager.inst().getEnvironment().getProperty("app_name");
		LogCore.BASE.info(START_TAG + "app_name={}", appName);// AppConfig

	}

	public void run(String... args) throws Exception {
		LogCore.BASE.info(START_TAG + "app_name={}", app_name);
		LogCore.BASE.info(START_TAG + "upload_path={}", upload_path);
		LogCore.BASE.info(START_TAG + "server_port={}", server_port);
	}
}
