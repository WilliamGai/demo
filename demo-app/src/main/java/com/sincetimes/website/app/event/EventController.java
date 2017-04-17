package com.sincetimes.website.app.event;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.handler.PortInfo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.core.common.port.PortInstance;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.threadpool.ThreadPoolTool;
import com.sincetimes.website.core.spring.interfaces.ControllerInterface;
/**
 * 使用serverEvent实现服务器主动给前端推送
 */
@Controller
@Order(value = 8)
public class EventController implements ControllerInterface {
	private Map<Object, ConcurrentLinkedQueue<String>> map = new ConcurrentHashMap<>();
	/**
	 * 推送记录
	 * 方法返回依然输出会出现异常：
	 * <pre>
	 * HTTP Status 500 - Request processing failed; nested exception is
	 *  java.lang.IllegalStateException: getOutputStream() has already been called for this response
	 * </pre>
	 * */
	@RequestMapping("/doevent_trace")
	@ResponseBody
	void doevent_trace(HttpServletRequest req, HttpServletResponse resp){
		resp.setContentType("text/event-stream");
		resp.setHeader("expires", "-1");
		resp.setHeader("cache-control", "no-cache");
		map.computeIfAbsent(req, (k)->new ConcurrentLinkedQueue<>());
		map.put(req, null);
		while(true){
			try {
				Thread.sleep(2000);
//				while(map.get(req).poll())
				String s= "data:"+new Date().toString()+"\n\n";
				resp.getOutputStream().write(s.getBytes());
				LogCore.BASE.debug("servlet={} ,request={},threadId={}, s={}",this.hashCode(), req.hashCode(),Thread.currentThread().getId(), s);
			} catch (Exception e) {
				LogCore.BASE.error("trace err", e);
			}
			try {
				resp.flushBuffer();
			} catch (IOException e) {//前端页面关闭或刷新
				LogCore.BASE.error("{} ,{},threadId={}, end message={}",this.hashCode(), req.hashCode(),Thread.currentThread().getId(), e.getMessage());
				return;//结束
			}
		}
	}
	
	/* 测试  start*/
	/**
	 * 测试例子
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
	public void print(PrintWriter w,String s){
		try {
			w.print(s);
			w.close();
			Thread.sleep(2000);
			LogCore.BASE.info(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/* 测试  end*/
}
