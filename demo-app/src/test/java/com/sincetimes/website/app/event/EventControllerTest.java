package com.sincetimes.website.app.event;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.spring.interfaces.AccessSupport;
@Controller
@Order(value = 8)
public class EventControllerTest implements AccessSupport {
	/* 测试  start*/
	/**
	 * 测试例子
	 * 极坐标实现钟表旋转
	 * <a href="http://demo.williamy.xin/event.html">每2秒推送时间(请用chrome打开)</a>
	 */
	@RequestMapping("/getmsg")
	@ResponseBody
	String getmsg(HttpServletRequest req, HttpServletResponse resp){
		resp.setContentType("text/event-stream");
		resp.setHeader("expires", "-1");
		resp.setHeader("cache-control", "no-cache");
		return "data:"+new Date().toString()+"\n\n";
	}
	/***测试例子<a href="http://demo.williamy.xin/event2.html">每2秒推送时间(请用chrome打开)</a> */
	@RequestMapping("/doevent")
	void doEvent(HttpServletRequest req, HttpServletResponse resp)
	{
		LogCore.BASE.info("{} doevent", req.getSession().getId());
		resp.setContentType("text/event-stream");
		resp.setHeader("expires", "-1");
		resp.setHeader("cache-control", "no-cache");
		
		int i = 0;
		int r = 40;//半径
		int v = 10;//速度
		
		while(true){
			i = (i+1)%(v*2);//i越大 越 
	        double angel = i*Math.PI/v;//i 从   0到2π
				int x = (int)Math.round( r* Math.cos(angel) );
				int y = (int)Math.round( r* Math.sin(angel) );
			String s = "data:"+x+","+y+"\n\n";
			try {
				resp.getOutputStream().write(s.getBytes());
			} catch (Exception e) {
				LogCore.BASE.error(e.getMessage());
				return;
			}
			try {
				resp.flushBuffer();//前端页面关闭或刷新
			} catch (IOException e) {
				LogCore.BASE.error(e.getMessage());
				return;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				LogCore.BASE.error(e.getMessage());
				return;
			}
		}
	}
}
