package com.sincetimes.website.test.interceptor;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.spring.HttpHeadUtil;


/***
 * 管理请求拦截器,未登录的,单例
 * 所有匹配 /mg/*的訪問
 */
public class BootLoginInterceptor implements HandlerInterceptor {
	public final AtomicLong _count = new AtomicLong();// 计数器

	/* 1*/
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object arg2) throws ServletException, IOException {
			String uri = req.getRequestURI();
			Object _old_user = req.getSession().getAttribute("user");
			if(null != _old_user){
				return true;
			}
			req.setAttribute("redirect_url", uri);
			req.getRequestDispatcher("/login").forward(req, resp);//转发
			LogCore.BASE.debug("{}-------------------find, dispatch to ../login req={}", uri, HttpHeadUtil.getParamsMap(req));
			return false;
	}

	/* 3 */
	public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object arg2, Exception arg3)
			throws Exception {
	}

	/* 2 */
	public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object arg2, ModelAndView arg3)
			throws Exception {
	}
}
