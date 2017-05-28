package com.sincetimes.website.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sincetimes.website.app.stats.StatsManager;
import com.sincetimes.website.core.common.support.JSONBuilder;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.spring.interfaces.ControllerInterface;
import com.sincetimes.website.core.spring.manger.SpringManager;
import com.sincetimes.website.manager.DataManager;
/**
 * 参考
 */
@RestController
@Order(value = 5)
public class DataControllerExcample implements ControllerInterface {

	@RequestMapping("/get_value")
	public Object get_value(String name) {
		String v = DataManager.inst().get(name);
		return JSONBuilder.creatJsonString("result", 1,"data", v);
	}
	
	@RequestMapping("/get_bivalue")
	public Object get_bivalue(String key) {
		String[] ss =  DataManager.inst().getBiValue(key);
		return JSONBuilder.creatJsonString("result", 1,"key",ss[0], "name",ss[1], "value",ss[2]);
	}

	@RequestMapping("/downloadhits/{os}")
	public String download(HttpServletRequest req, @PathVariable String os,String openid, String ip) {
		int real_ip = req.getRemotePort();
		LogCore.BASE.info("downloadhits openid={},realIp={},os={}",openid, real_ip, os);
		StatsManager.inst().incr(os);
		return os;
	}
	
	@ResponseBody
	@RequestMapping("/lookenv")
	Object lookenv(HttpServletRequest req, HttpServletResponse resp){
		return SpringManager.inst().getEnvironmentJsonStr();
	}
}
