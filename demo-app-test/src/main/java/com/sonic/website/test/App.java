package com.sonic.website.test;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonic.website.core.common.support.JSONBuilder;
import com.sonic.website.core.spring.AppStarter;
@Controller
@SpringBootApplication(scanBasePackages={"com.sincetimes.website"})
@EnableAsync
public class App {
	@RequestMapping("/")
	@ResponseBody
	Object home(HttpServletRequest req) {
		return JSONBuilder.create()
				.append("ip", req.getRemoteAddr())
				.append("realIp", req.getAttribute("p_real_ip")+"")
				.buildJSON();
	}
	@RequestMapping("/main")
	String main() {
		return "main";
	}
	@RequestMapping("    estattr")
	String test(Model model) {
		TestObject o = new TestObject();
		o.name = "heihie";
		o.map.put("b", "d");
		
		Map<String,Object> map = new HashMap<>();
		map.put("A", "A1");
		map.put("B", "B1");
		model.addAttribute("data", o);
		model.addAttribute("mp", map);
		return "test";
	}
	@RequestMapping("    stmap")
	@ResponseBody
	Object tst() {
		TestObject o = new TestObject();
		o.name = "heihie";
		o.map.put("b", "d");
		
		Map<String,Object> map = new HashMap<>();
		map.put("A", "A1");
		map.put("B", "B1");
		return map;
	}
	public static void main(String[] args) throws Exception {
		AppStarter.initManagers(SpringApplication.run(App.class, args));
	}
}