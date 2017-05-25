package com.sincetimes.website.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.core.common.support.Sys;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.spring.AppStarter;
import com.sincetimes.website.redis.jedis.excample.JedisServiceDemo;

import de.codecentric.boot.admin.config.EnableAdminServer;
@EnableAdminServer
@Controller
@SpringBootApplication(scanBasePackages={"com.sincetimes.website"})  
public class App {
	@RequestMapping("/p")
	@ResponseBody
	String home() {
		return Sys.getPropertiesJSONString();
	}
	
	@Autowired
	JedisServiceDemo jedisService;
	
	@RequestMapping("/hello")
	@ResponseBody
	String hello() {
		String now = jedisService.getSet("now", TimeTool.getLocalTime());
		return "Hello app security server!"+now;
	}
	@RequestMapping("/main")
	String main() {
		return "main";
	}
	
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
		AppStarter.initManagers(context);
		LogCore.SECURITY.info("app started args={}", Arrays.toString(args));
	}
}