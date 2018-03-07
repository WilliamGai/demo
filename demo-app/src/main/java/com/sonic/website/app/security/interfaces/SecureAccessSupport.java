package com.sonic.website.app.security.interfaces;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.sonic.website.app.security.vo.UserVO;
import com.sonic.website.core.spring.interfaces.AccessSupport;
import com.sonic.website.app.security.SecurityManager;
/**只给过滤器调用就可以*/
public interface SecureAccessSupport extends AccessSupport{
	
	public static final String SESSION_USER_KEY = "user";
	/** 
	 * Controller调用
	 * Model具体实现为{@link BindingAwareModelMap} <br>
	 * 是一个实现了Modle接口的ModelMap实际上为LinkedHashMap<br>
	 * setUser(Model, HttpServletRequest)比 setUser(ModelMap, HttpServletRequest)优先
	 */
	default void setModelUser(Model model, HttpServletRequest req) {
		Object _user = getSessionAttr(req, SESSION_USER_KEY);
		if(_user instanceof UserVO){
			UserVO user = (UserVO)_user; 
			user = SecurityManager.inst().getUserAndInit(user.getName());
			model.addAttribute(SESSION_USER_KEY, user);
		}
	}
	default void setModelUser(Model model, UserVO user) {
		model.addAttribute(SESSION_USER_KEY, user);
	}
	/**过滤器调用*/
	default void setModelAndViewUser(ModelAndView mv, HttpServletRequest req) {
		if(null == mv ||mv.getModelMap().containsAttribute(SESSION_USER_KEY)){
			return;
		}
		UserVO user = getSessioUser(req);
		if(null == user){
			return;
		}
		user = SecurityManager.inst().getUserAndInit(user.getName());
		mv.getModelMap().addAttribute(SESSION_USER_KEY, user);
	}
	default UserVO getSessioUser(HttpServletRequest req){
		return getSessionAttr(req, SESSION_USER_KEY, UserVO.class);
	}
}
