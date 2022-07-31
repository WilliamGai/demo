package com.sonic.website.interceptor;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sonic.website.app.event.EventMsgContext;
import com.sonic.website.app.security.vo.UserVO;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.TimeUtil;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.spring.HttpHeadUtil;


/***
 * 管理单机请求拦截器,未登录的,单例
 * 所有匹配 /mg/*的访问
 * TODO:distributed
 */
public class BootLoginInterceptor implements HandlerInterceptor {
    public final AtomicLong _count = new AtomicLong();// 计数器

    /* 1*/
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object arg2) throws ServletException, IOException {
            String uri = req.getRequestURI();
            String timeStr = TimeUtil.formatTime(System.currentTimeMillis(), "HH:mm:ss");
            Object _user = req.getSession().getAttribute("user");
            if(_user instanceof UserVO){
                UserVO user = (UserVO) _user;
                String msg = Util.format("{}, {} visited {}", timeStr, user.getName(), uri);
                EventMsgContext.inst().putMsg(msg);
                return true;
            }
            String msg = Util.format("{}, {} visit {} prohibit", timeStr, req.getRemotePort(), uri);
            EventMsgContext.inst().putMsg(msg);
            req.setAttribute("redirect_url", uri);
            req.getRequestDispatcher("/login").forward(req, resp);//转发
            LogCore.BASE.debug("{}-------------------find, dispatch to ../login req={}", uri, HttpHeadUtil.getParamsMapLimit(req));
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
