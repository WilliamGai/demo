package com.sincetimes.website.test;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.core.common.support.JSONBuilder;
import com.sincetimes.website.core.common.support.MD5;
@Controller
@SpringBootApplication
public class App {
	@RequestMapping("/")
	@ResponseBody
	Object home(HttpServletRequest req) {
		MD5
		return JSONBuilder.create()
				.append("ip", req.getRemoteAddr())
				.append("realIp", req.getAttribute("p_real_ip")+"")
				.buildJSON();
	}
	@RequestMapping("/main")
	String main() {
		return "main";
	}
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(App.class, args);
	}
}