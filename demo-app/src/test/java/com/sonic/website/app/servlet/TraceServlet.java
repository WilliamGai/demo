package com.sonic.website.app.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.threadpool.LimitedThreadPool;
/**
 * 错误例子,会返回异常
 */
public class TraceServlet extends HttpServlet {
	private static final long serialVersionUID = 7813050777381742351L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/event-stream;charset=utf-8");
		resp.setHeader("expires", "-1");
		resp.setHeader("cache-control", "no-cache");
		LimitedThreadPool.execute(()->{
			while(true){
				try {
					Thread.sleep(2000);
					String s= "data:"+new Date().toString()+"\n\n";
					LogCore.BASE.debug("{} ,{}, s={}",this.hashCode(), req.hashCode(), s);
					resp.getOutputStream().write(s.getBytes());
				} catch (Exception e) {
					LogCore.BASE.error("trace err", e);
				}
				try {
					resp.flushBuffer();//前端页面关闭或刷新
				} catch (IOException e) {
					LogCore.BASE.error(e.getMessage());
					return;
				}
			}
		});
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}
}
