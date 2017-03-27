package com.sincetimes.website.core.spring.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoController {
	@Value("${upload_path:./}")
	public String upload_path;
	
	@Value("${server.port:null}")
	public String port;
	
	@RequestMapping("/hellospring")
	@ResponseBody
	String hellospring() {
		return upload_path + port;
	}
	
	@RequestMapping("/hellospringpage")
	String hellospringpage(Model model) {
		model.addAttribute("data", "demodata");
		return "demo";
	}
}
