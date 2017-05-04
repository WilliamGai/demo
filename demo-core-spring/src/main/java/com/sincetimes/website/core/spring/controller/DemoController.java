package com.sincetimes.website.core.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Sys;
import com.sincetimes.website.core.common.support.TimeTool;

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
	/**
	 * 观察基本信息
	 * @return
	 */
	@RequestMapping("/hellospringinfo")
	@ResponseBody
	Object hellospringinfo() {
		return Sys.getSysInfos();
	}
	
	@RequestMapping("/hellospringpage")
	String hellospringpage(Model model) {
		model.addAttribute("data", "demodata");
		return "demo";
	}
	@RequestMapping("/hellojvmstatus")
	@ResponseBody
	void doevent_trace(HttpServletRequest req, HttpServletResponse resp){
		resp.setContentType("text/event-stream");
		resp.setHeader("expires", "-1");
		resp.setHeader("cache-control", "no-cache");
		
		while(true){
			try {
				Thread.sleep(500);
				String msg  = Sys.getJVMStatus()+TimeTool.getLocalTime();
				String s= "data:" + msg + "\n\n";
				resp.getOutputStream().write(s.getBytes());
				if(LogCore.BASE.isDebugEnabled()){
					LogCore.BASE.debug("servlet={} ,request={},threadId={}, s={}",this.hashCode(), req.hashCode(),Thread.currentThread().getId(), s);
				}
				resp.flushBuffer();
			} catch (Exception e) {//前端页面关闭或刷新
				LogCore.BASE.error("{} ,{},threadId={}, end message={}",this.hashCode(), req.hashCode(),Thread.currentThread().getId(), e.getMessage());
				return;//结束
			}
		}
	}
}
