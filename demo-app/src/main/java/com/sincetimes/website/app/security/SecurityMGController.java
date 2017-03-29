package com.sincetimes.website.app.security;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.app.security.vo.RoleVO;
import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.core.common.support.DataResult;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.ParamResult;
import com.sincetimes.website.core.common.support.Result;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.controller.ControllerInterface;

@Controller
@Order(value = 6)
@RequestMapping("/mg")
public class SecurityMGController implements ControllerInterface {
	
	/**
	 * 用户管理列表
	 */
	@RequestMapping("/secure_users")
	String accounts(Model model, HttpServletRequest req) {
		Collection<UserVO> users = SecurityManager.inst().getAllUsers();
		users.forEach(UserVO::getCreatedBy);
		model.addAttribute("users", users);
		setUser(model, req);
		return "secure_users";
	}
	/**
	 * 权限列表
	 */
	@RequestMapping("/secure_roles")
	String secure_roles(Model model, HttpServletRequest req) {
		Collection<RoleVO> roles = SecurityManager.inst().getAllRoles();
		model.addAttribute("roles", roles);
		setUser(model, req);
		return "secure_roles";
	}
	public void setUser(Model model, HttpServletRequest req) {
		Object _user = req.getSession().getAttribute("user");
		if(_user instanceof UserVO){
			model.addAttribute("user", _user);
		}
	}
	/**  用户登录后主界面 */
	@RequestMapping("/secure_user")
	String secure_user(Model model, String user_name, HttpServletRequest req) {
		if(!Util.isEmpty(user_name)){
			UserVO user = SecurityManager.inst().getUser(user_name);
			if(null != user){
				SecurityManager.inst().initUser(user);
				model.addAttribute("user", user);
				return "secure_user";
			}
		}
		Object _user = req.getSession().getAttribute("user");
		if(_user instanceof UserVO){
			UserVO user = (UserVO) _user;
			SecurityManager.inst().initUser(user);
			model.addAttribute("user", user);
			return "secure_user";
		}
		model.addAttribute("user", new UserVO());
		return "secure_user";
	}
	
	/**  用户登录后主界面 */
	@RequestMapping("/secure_user_config")
	String secure_user_config(Model model, String user_name, HttpServletRequest req) {
		if(!Util.isEmpty(user_name)){
			UserVO user = SecurityManager.inst().getUser(user_name);
			if(null != user){
				SecurityManager.inst().initUser(user);
				model.addAttribute("user", user);
				Collection<RoleVO> roles = SecurityManager.inst().getAllRoles();
				LogCore.BASE.debug("roles={}", roles);
				model.addAttribute("roles", roles);
				return "secure_user_config";
			}
		}
		model.addAttribute("user", new UserVO());
		return "secure_user_config";
	}
	@RequestMapping("/add_user_role")
	void add_user_role(Model model, String user_name, String role_id, HttpServletResponse resp) {
		if(!Util.nonEmpty(user_name, role_id)){
			redirect(resp, "secure_user_config?user_name=" + user_name);
			return;
		}
		SecurityManager.inst().addUserRole(user_name, role_id);
		redirect(resp, "secure_user_config?user_name=" + user_name);
		return;
	}
	
	@RequestMapping("/edit_user")
	@ResponseBody
	Object edit_user(String name, String nickname, String password, String female, String pic) {
		ParamResult rst = SecurityManager.inst().saveOrUpdateUser(name, nickname, password, female, pic);
		return new DataResult(rst.result);
	}
	
	@RequestMapping("/delete_user")
	Object delete_user(String name) {
		SecurityManager.inst().deleteUser(name);
		return new Result().success();
	}
	
	@RequestMapping("/add_role")
	void add_role(String role_id, String role_name, HttpServletResponse resp) {
		if(!Util.nonEmpty(role_id, role_name)){
			redirect(resp, "secure_roles");
			return;
		}
		SecurityManager.inst().addRole(role_id, role_name);
		redirect(resp, "secure_roles");
	}
	
	@RequestMapping("/delete_role")
	void delete_role(String role_id, HttpServletResponse resp) {
		if(Util.isEmpty(role_id)){
			redirect(resp, "secure_roles");
			return;
		}
		SecurityManager.inst().deleteRole(role_id);
		redirect(resp, "secure_roles");
	}
	
	@RequestMapping("/add_role_permission")
	void add_permission(String role_id, String permission_name, String uri, HttpServletResponse resp) {
		if(!Util.nonEmpty(role_id, permission_name, uri)){
			redirect(resp, "secure_role?role_id=" + role_id);
			return;
		}
		SecurityManager.inst().addPermission(role_id, permission_name, uri);
		redirect(resp, "secure_role?role_id=" + role_id);
	}
	
	@RequestMapping("/delete_role_permission")
	void add_permissions(String role_id, int index, HttpServletResponse resp) {
		if(Util.isEmpty(role_id)||index < 0){
			redirect(resp, "secure_role?role_id=" + role_id);
			return;
		}
		SecurityManager.inst().deletePermission(role_id, index);
		redirect(resp, "secure_role?role_id=" + role_id);
	}
	
	@RequestMapping("/secure_role")
	Object secure_role(Model model, String role_id, HttpServletRequest req) {
		setUser(model, req);
		if(Util.isEmpty(role_id)){
			return "secure_role";
		}
		RoleVO role = SecurityManager.inst().getRole(role_id);
		model.addAttribute("role", role);
		return "secure_role";
	}
}
