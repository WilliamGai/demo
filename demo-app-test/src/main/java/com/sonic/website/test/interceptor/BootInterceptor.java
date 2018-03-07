package com.sonic.website.test.interceptor;

import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.spring.HttpHeadUtil;

/***
 * 拦截器,单例
 */
public class BootInterceptor implements HandlerInterceptor {
	public final AtomicLong _count = new AtomicLong();// 计数器
	public final AtomicLong _requestId = new AtomicLong();// 计数器


	// 1
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object arg2) {
		long begin_nao_time = System.nanoTime();
		String realIp = HttpHeadUtil.getRealIpAddr(req);
		if (req.getRequestURI().contains("error")) {
			LogCore.BASE.error("{}-------------------get one error request! {},arg2={}, referer={}", req.getRequestURI(), arg2, req.getHeader("referer"));
			return true;
		}
		if (LogCore.BASE.isDebugEnabled()) {
			LogCore.BASE.debug("{}----------------begin,realip={},req params:{},Origin={}", req.getRequestURI(),
					realIp, HttpHeadUtil.getParamsMapLimit(req), req.getHeader("Origin"));
		}
		req.setAttribute("p_real_ip", realIp);
		req.setAttribute("p_req_id", _requestId.incrementAndGet());
		req.setAttribute("begin_nao_time", begin_nao_time);
		LogCore.BASE.debug("{}--------------begin req,Uri= {}", this.hashCode(), req.getRequestURI());
        LogCore.BASE.info("req begin Uri= {},{},{}, {}, reqId= {}", req.getRequestURI(), Thread.currentThread().getName(), Thread.currentThread().getId(), _requestId.get(), HttpHeadUtil.getParamsMapLimit(req));

        return true;
	}

	/*
	 * 3 会加上下面三个response头 Content-Type=application/json;charset=UTF-8,
	 * Content-Length=30, Date=Thu, 02 Jun 2016 07:42:33 GMT}
	 */
	public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object arg2, Exception arg3)
			throws Exception {
		String uri = req.getRequestURI();
		Object begin_nao_time_str = req.getAttribute("begin_nao_time");
		if(null == begin_nao_time_str){
			LogCore.BASE.warn("{} get null,referer:{}, the uri looks useless " ,uri, req.getHeader("referer"));
			return;
		}
		long begin_nao_time = (Long)begin_nao_time_str ;
		String real_ip = (String) req.getAttribute("p_real_ip");
		long interval = System.nanoTime() - begin_nao_time;
		//以后删掉
        LogCore.BASE.info("req end Uri= {},{},{}, {}, reqId= {}", req.getRequestURI(), Thread.currentThread().getName(), Thread.currentThread().getId(), req.getAttribute("p_req_id"), HttpHeadUtil.getParamsMapLimit(req));

		LogCore.BASE.debug(this.hashCode() + "{}==========={}=========end,id={},params:{}, from:{},e:{}", uri,
				interval / 1000000, _count.getAndIncrement(), HttpHeadUtil.getParamsMapLimit(req), real_ip, arg3);
	}

	/* 2 */
	public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object arg2, ModelAndView arg3)
			throws Exception {
	}
}
