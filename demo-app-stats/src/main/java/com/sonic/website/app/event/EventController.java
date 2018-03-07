package com.sonic.website.app.event;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.spring.interfaces.AccessSupport;
/**
 * 使用serverEvent实现服务器主动给前端推送<br>
 * 从http1.1开始是长连接,类似多路复用。同个浏览器多个请求是同一个tcp链接<br>
 * 如果使用nginx等配置反向代理，使用server event和websocket的时候。会出现间歇性的断开。这与proxy_read_timeout参数有关<br>
 * 可以给websocket和server event配置单独的超时时间,比如一个小时的超时时间:<br>
 * <pre>{@code nginx:
 * location ~ /hourlinks/ {
            proxy_http_version 1.1;
            ...
            proxy_connect_timeout 3s;
            proxy_read_timeout 3600s;
            proxy_send_timeout 2s;
            ...
        }
 * }</pre>
 * 对于websocket建议使用心跳来解决。
 * server event如果浏览器出现断开一般是两种情况：<br>
 * 1.服务器的方法返回了,只能浏览器每隔一秒重新请求。实际上应该用while(true)挂起。也不能用子线程处理否则报异常。<br>
 * 2.使用了反向代理并配置了超时时间。浏览器返回结果失败报 net::ERR_INCOMPLETE_CHUNKED_ENCODING<br>
 */
@Controller
@Order(value = 8)
@RequestMapping("/hourlinks")
public class EventController implements AccessSupport {
	/**
	 * 推送记录
	 * 不要让结果返回否则方法返回依然输出会出现异常：
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
		
		while(true){
			try {
				Thread.sleep(500);
				while(!EventMsgContext.inst().getMsgQueue(req).isEmpty()){
					String s= "data:" + EventMsgContext.inst().getMsgQueue(req).poll() + "\n\n";
					resp.getOutputStream().write(s.getBytes());
					if(LogCore.BASE.isDebugEnabled()){
						LogCore.BASE.debug("servlet={} ,request={},threadId={}, s={}",this.hashCode(), req.hashCode(),Thread.currentThread().getId(), s);
					}
				}
			} catch (Exception e) {
				LogCore.BASE.error("trace err", e);
				EventMsgContext.inst().unregist(req);
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
