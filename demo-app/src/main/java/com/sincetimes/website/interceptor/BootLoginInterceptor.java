package com.sincetimes.website.interceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import com.sincetimes.website.app.security.SecurityManager;
import com.sincetimes.website.app.event.EventMsgContext;
import com.sincetimes.website.app.security.interfaces.SecureAccessSupport;
import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.HttpHeadUtil;


/***
 * 管理单机请求拦截器,未登录的,单例
 * 所有匹配 /mg/*的访问
 * 从HandlerInterceptor的实现改为继承WebContentInterceptor
 */
public class BootLoginInterceptor extends WebContentInterceptor implements SecureAccessSupport{

	private static final String LOGIN_TIPMSG = "tipmsg";
	public static final String REDIRECT_URL_TAG = "redirect_url";

	/* 1*/
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object arg2) throws ServletException {
		String uri = req.getRequestURI();
		UserVO user = getSessioUser(req);
		if(null == user){
			failAndForwardLogin(req, resp, uri, null);
			return false;
		}
		if(user.getStatus() != UserVO.USER_STATUS_OK_1){
			failAndForwardLogin(req, resp, uri, "用户被锁,请联系管理员");
			return false;
		}
		if(Util.isEmpty(uri)){
			failAndForwardLogin(req, resp, uri, null);
			return false;
		}
		boolean permissionPass = SecurityManager.inst().passPermission(uri, user.getName());
		if(!permissionPass){
			failAndForwardLogin(req, resp, uri, "没有权限");
			return false;
		}
		String msg = Util.format("{}, {} visited {}", TimeTool.getTimeStr(), user.getName(), uri);
		EventMsgContext.inst().putMsg(msg);
		return true;
		
	}

	/* 2 */
	@Override
	public void postHandle(
			HttpServletRequest req, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		    /*为所有的模板设置用户信息,如果之前model被设置过用户信息则返回*/
		    if(null == modelAndView){
		    	return;
		    }
			if(modelAndView.getModelMap().containsAttribute(SESSION_USER_KEY)){
				return;
			}
			setModelAndViewUser(modelAndView, req);
	}

	/* 3 */
	@Override
	public void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
	
	private void failAndForwardLogin(HttpServletRequest req, HttpServletResponse resp, String uri, String tipmsg) {
		String msg = Util.format("{}, {} visit {} prohibit", TimeTool.getTimeStr(), req.getRemotePort(), uri);
		EventMsgContext.inst().putMsg(msg);
		req.setAttribute(REDIRECT_URL_TAG, uri);
		req.setAttribute(LOGIN_TIPMSG, tipmsg);//tipmsg
		forward(req, resp, "/login");
		LogCore.BASE.debug("{}-------------------find, dispatch to ../login req={}", uri, HttpHeadUtil.getParamsMapLimit(req));
	}
}
