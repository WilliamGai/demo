package com.sincetimes.website.app.security;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.core.common.support.DataResult;
import com.sincetimes.website.core.common.support.ParamResult;
import com.sincetimes.website.core.common.support.Result;
import com.sincetimes.website.core.spring.controller.ControllerInterface;

@Controller
@Order(value = 6)
@RequestMapping("/mg")
public class AccountMGController implements ControllerInterface {
	
	/**
	 * 用户管理列表
	 */
	@RequestMapping("/secure_accounts")
	String accounts(Model model, HttpServletRequest req) {
		
		Collection<UserVO> users = AccountManager.inst().getAllUsers();
		model.addAttribute("users", users);
		Object _user = req.getSession().getAttribute("user");
//		if(_old_user instanceof UserVO && "admin".equals(((UserVO)_old_user).getName())){
//		}
		if(_user instanceof UserVO){
			model.addAttribute("user", _user);
			Collection<UserVO> user_list = AccountManager.inst().getAllUsers();
			model.addAttribute("user_list", user_list);
		}
		return "secure_accounts";
	}
	/**
	 * 权限列表
	 */
	@RequestMapping("/secure_roles")
	String secure_roles(Model model, HttpServletRequest req) {
		Collection<UserVO> users = AccountManager.inst().getAllUsers();
		model.addAttribute("users", users);
		Object _user = req.getSession().getAttribute("user");
//		if(_old_user instanceof UserVO && "admin".equals(((UserVO)_old_user).getName())){
//		}
		if(_user instanceof UserVO){
			model.addAttribute("user", _user);
			Collection<UserVO> user_list = AccountManager.inst().getAllUsers();
			model.addAttribute("user_list", user_list);
		}
		return "secure_roles";
	}
	@RequestMapping("/secure_account")
	String secure_account(Model model, HttpServletRequest req) {
		Object _user = req.getSession().getAttribute("user");
		if(_user instanceof UserVO){
			model.addAttribute("user", _user);
		}else{
			model.addAttribute("user", new UserVO());
		}
		return "secure_account";
	}
	
	@RequestMapping("/edit_account")
	@ResponseBody
	Object edit_account(String name, String nickname, String password, String female, String pic) {
		ParamResult rst = AccountManager.inst().saveOrUpdateUser(name, nickname, password, female, pic);
		return new DataResult(rst.result);
	}
	
	@RequestMapping("/delete_user")
	@ResponseBody
	Object delete_user(String name) {
		AccountManager.inst().deleteUser(name);
		return new Result().success();
	}


}
