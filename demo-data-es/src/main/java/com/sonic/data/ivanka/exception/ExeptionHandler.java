package com.sonic.data.ivanka.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.sonic.website.core.common.support.LogCore;
/**
 * tips:web容器会输出app的所有的异常,但是无效地址404建议在服务器apache/nginx控制
 */
@ControllerAdvice
public class ExeptionHandler {

	@ExceptionHandler(value = Exception.class)
	public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) {
		LogCore.BASE.error("error occur:", e);
		ModelAndView mv = new ModelAndView();
		mv.addObject("e", e);
		mv.addObject("uri", req.getRequestURI());
		return mv;
	}
}
