package com.sincetimes.website.app.security.interfaces;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.core.spring.interfaces.ControllerInterface;


public interface SecureControllerInterface extends ControllerInterface{
	/** 设置登录用  */
	default void setUser(Model model, HttpServletRequest req) {
		Object _user = req.getSession().getAttribute("user");
		if(_user instanceof UserVO){
			model.addAttribute("user", _user);
		}
	}
	default String getUserName(HttpServletRequest req) {
		return getSessionAttrFncOrElse(req, "user", UserVO::getName, null);
	}
}
