package com.sincetimes.website.core.spring.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import com.sincetimes.website.core.common.support.HttpUtil;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.spring.HttpHeadUtil;

public interface ControllerInterface extends CommandLineRunner, FilePathInterface {
	
	default void run(String... args) throws SQLException {
		LogCore.CORE.info("{} init start! the order is {} !!! ", this.getClass().getName(), this.getClass().getAnnotation(Order.class));
	}
	/**跨域*/
	default void allowAccess(HttpServletResponse resp){
		HttpHeadUtil.allowAccess(resp);
	}
	default String urlEncode(String msg){
		return HttpUtil.urlEncode(msg);
	}
	default void redirect(HttpServletResponse resp, String uri){
		try {
			resp.sendRedirect(uri);
		} catch (IOException e) {
	        String className = Thread.currentThread().getStackTrace()[2].getClassName();
	        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
			LogCore.BASE.error("{},{}invoke redirect fail,uri={}", className, methodName, uri);
		}
	}
	default void forward(HttpServletRequest req, HttpServletResponse resp, String uri){
		try {
			req.getRequestDispatcher(uri).forward(req, resp);
		} catch (Exception e) {
			String className = Thread.currentThread().getStackTrace()[2].getClassName();
			String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
			LogCore.BASE.error("{},{} invoke forward fail,uri={}", className, methodName, uri);
		}
	}
	
	@SuppressWarnings("unchecked")
	default <T> Optional<T> getSessionAttr(HttpServletRequest req, String param){
		return Optional.of((T)req.getSession().getAttribute(param));
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
