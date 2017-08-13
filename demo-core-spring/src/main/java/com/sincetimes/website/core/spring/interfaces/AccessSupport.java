package com.sincetimes.website.core.spring.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import com.sincetimes.website.core.common.support.ClassUtil;
import com.sincetimes.website.core.common.support.HttpUtil;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.HttpHeadUtil;

public interface AccessSupport extends CommandLineRunner, FilePathSupport {
	/**实现CommandLineRunner的run(String[])方法*/
	default void run(String... args) throws SQLException {
		LogCore.CORE.info("{} init start! the order is {}", this.getClass().getName(), Util.toNullDefalut(this.getClass().getAnnotation(Order.class), Order::value,  "null"));
	}
	/**允许跨域*/
	default void allowAccess(HttpServletResponse resp){
		HttpHeadUtil.allowAccess(resp);
	}
	/**urlEnchode*/
	default String urlEncode(String msg){
		return HttpUtil.urlEncode(msg);
	}
	default void redirect(HttpServletResponse resp, String uri){
		try {
			resp.sendRedirect(uri);
		} catch (IOException e) {//输出错误日志,哪个类的哪个方法重定向失败
	        String className = Thread.currentThread().getStackTrace()[2].getClassName();
	        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
			LogCore.BASE.error("{},{}invoke redirect fail,uri={}", className, methodName, uri);
		}
	}
	default void redirectAppend(HttpServletRequest req, HttpServletResponse resp, String uri){
		redirect(resp, req.getRequestURL()+"/"+uri);
	}
	default void forward(HttpServletRequest req, HttpServletResponse resp, String uri){
		try {
			req.getRequestDispatcher(uri).forward(req, resp);
		} catch (Exception e) {//输出错误日志,哪个类的哪个方法转发向失败
			String className = Thread.currentThread().getStackTrace()[2].getClassName();
			String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
			LogCore.BASE.error("{},{} invoke forward fail,uri={}", className, methodName, uri);
		}
	}
	
	@SuppressWarnings("unchecked")
	default <T> T getSessionAttr(HttpServletRequest req, String param){
		return (T)req.getSession().getAttribute(param);
	}
	@SuppressWarnings("unchecked")
	default <T> T getSessionAttr(HttpServletRequest req, String param, Class<T> clazz){
		Object obj = req.getSession().getAttribute(param);
		if(null == obj || !ClassUtil.isInstanceof(obj.getClass(), clazz)){
			return null;
		}
		return (T)obj;
	}
	@SuppressWarnings("unchecked")
	default <T> Optional<T> getSessionAttrOptional(HttpServletRequest req, String param){
		return Optional.ofNullable((T)req.getSession().getAttribute(param));
	}
	@SuppressWarnings("unchecked")
	default <T> T getSessionAttrOrElse(HttpServletRequest req, String param, T other){
		T value = (T)req.getSession().getAttribute(param);
		return value != null ? value : other;
	}
	@SuppressWarnings("unchecked")
	default <T, K> K getSessionAttrFncOrElse(HttpServletRequest req, String param, Function<T, K> fnc, K other){
		T value = (T)req.getSession().getAttribute(param);
		return value != null ? fnc.apply(value) : other;
	}
}
