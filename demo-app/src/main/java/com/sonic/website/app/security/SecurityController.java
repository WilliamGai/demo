package com.sonic.website.app.security;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sonic.website.app.file.FileManager;
import com.sonic.website.app.security.interfaces.SecureAccessSupport;
import com.sonic.website.app.security.vo.UserVO;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.ParamResult;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.spring.HttpHeadUtil;
import com.sonic.website.interceptor.BootLoginInterceptor;

@Controller
@Order(value = 7)
public class SecurityController implements SecureAccessSupport {

	private static final String REDIRECT_URL_TAG = BootLoginInterceptor.REDIRECT_URL_TAG;
	private static final String ACCOUNT_PICS_PATH = "account_pics";

	@RequestMapping("/login")
	String login(Model model, String username, String password, HttpServletRequest req, HttpServletResponse rsp) {
		String redirect_url = Objects.toString(req.getAttribute(REDIRECT_URL_TAG), "/secure_user");
		LogCore.BASE.info("redirect_url:{}, user in session:{}", redirect_url, getSessioUser(req));
		model.addAttribute(REDIRECT_URL_TAG, redirect_url);
		ParamResult result = null;
		if (!Util.nonEmpty(username, password)) {
//			model.addAttribute("tipmsg", "用户名或密码不能为空");// 调试用
			model.addAttribute("data", req.getSession().getId() + result + HttpHeadUtil.getParamsMapLimit(req));// 调试用
			return "login";
		}
		result = SecurityManager.inst().pass(username, password);
		if (result.isSuccess()) {
			req.getSession().setAttribute("user", result.get());
			LogCore.BASE.info("login sucess!, redirect to ---->:{}", redirect_url);
			redirect(rsp, redirect_url.toString());
			return null;
		}
		model.addAttribute("tipmsg", "用户名或密码错误");// 调试用
		model.addAttribute("data", req.getSession().getId() + result + HttpHeadUtil.getParamsMapLimit(req));// 调试用
		return "login";
	}

	/**
	 * sign up <br>
	 * 注册页面
	 */
	@RequestMapping("/sign_up")
	String sign_up(Model model, HttpServletRequest req, HttpServletResponse rsp) {
		String ssid = req.getSession().getId();
		model.addAttribute("data", ssid + HttpHeadUtil.getParamsMapLimit(req));// 调试用
		model.addAttribute("tips", "upload your profile image");
		return "sign_up";
	}

	/**
	 * sign up <br>
	 * 注册
	 */
	@RequestMapping("/sign_up_submit")
	String sign_up_submit(Model model, String name, String nickname, String password, String female,
	            StandardMultipartHttpServletRequest freq, HttpServletResponse resp) {
		String pic = FileManager.inst().uploadFileSimple(freq, ACCOUNT_PICS_PATH);
		ParamResult rst = SecurityManager.inst().signUp(name, nickname, password, female, pic);
		if (!rst.isSuccess()) {
			model.addAttribute("tips", rst.get());
			return "sign_up";
		}
		LogCore.BASE.info("sign_in_submit name={}, password={}, female={}, pic={}, rst={}", name, password, female,
		            pic, Util.prettyJsonStr(rst));
		redirect(resp, "login");
		return null;
	}

	/** 用户登录后主界面也可能是管理员查询此页面 */
	@RequestMapping("/secure_user")
	String secure_user(Model model, String user_name, HttpServletRequest req, HttpServletResponse rsp) {
		UserVO user = getSessioUser(req);
		if (null != user) {
			if (!Util.isEmpty(user_name)) {
				user = SecurityManager.inst().getUserAndInit(user_name);
				model.addAttribute("user", user);
				return "secure_user";
			}
			user = SecurityManager.inst().getUserAndInit(user.getName());
			model.addAttribute("user", user);
			model.addAttribute("allowedit", "allow");
			return "secure_user";
		}
		redirect(rsp, "/login");
		return null;
	}

	/* 谁登录的谁修改,只能改自己的密码 */
	@RequestMapping("/change_user_psw")
	String change_user_psw(Model model, String user_name, String user_password, HttpServletRequest req,
	            HttpServletResponse rsp) {
		if (Util.isEmpty(user_name)) {
			return null;
		}
		UserVO user = getSessioUser(req);
		if (null == user) {
			return null;
		}
		if (!Objects.equals(user.getName(), user_name)) {
			return null;
		}
		SecurityManager.inst().changeUserPassword(user_name, user_password);
		redirect(rsp, "/secure_user");
		return null;
	}
}