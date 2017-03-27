package com.sincetimes.website.app.event;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sincetimes.website.core.common.support.LogCore;
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/event-stream;charset=utf-8");
		resp.setHeader("expires", "-1");
		resp.setHeader("cache-control", "no-cache");
		String s = "looks";
		s = "data: "+s+"\n\n";
		resp.getOutputStream().write(s.getBytes());
		LogCore.BASE.info(s);
		resp.flushBuffer();
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}
}
