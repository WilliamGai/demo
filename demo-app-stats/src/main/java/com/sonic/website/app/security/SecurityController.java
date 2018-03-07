package com.sonic.website.app.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sonic.website.app.file.FileManager;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.ParamResult;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.spring.HttpHeadUtil;
import com.sonic.website.core.spring.interfaces.AccessSupport;

@Controller
@Order(value = 7)
public class SecurityController implements AccessSupport {

	private static final String ACCOUNT_PICS_PATH = "account_pics";
	@RequestMapping("/login")
	String login(Model model, String username, String password, HttpServletRequest req, HttpServletResponse rsp){
		Object _old_user = req.getSession().getAttribute("user");
		Object redirect_url = req.getAttribute("redirect_url");
		LogCore.BASE.info("redirect_url:{}, user in session:{}", redirect_url, _old_user);
		if(Util.isEmpty(redirect_url)){
			redirect_url ="mg/secure_user";
		}
		model.addAttribute("redirect_url",redirect_url);
		ParamResult result = null;
		if(Util.nonEmpty(username, password)){
		    result = SecurityManager.inst().pass(username, password);
			if(result.isSuccess()){
				req.getSession().setAttribute("user", result.get());
				LogCore.BASE.info("login sucess!, redirect to ---->:{}", redirect_url);
				redirect(rsp, redirect_url.toString());
//				forward(req, rsp, redirect_url.toString());
				return null;
			}
		}
		model.addAttribute("data",req.getSession().getId()+result+HttpHeadUtil.getParamsMapLimit(req));//调试用
		model.addAttribute("username",username);
		model.addAttribute("password",password);
		return "login";
	}
	
	/**
	 * sign up
	 * <br>注册页面
	 */
	@RequestMapping("/sign_up")
	String sign_up(Model model, HttpServletRequest req, HttpServletResponse rsp){
		String ssid = req.getSession().getId();
		model.addAttribute("data",ssid + HttpHeadUtil.getParamsMapLimit(req));//调试用
		model.addAttribute("pic","test");
		model.addAttribute("tips", "upload your profile image");
		return "sign_up";
	}
	/**
	 * sign up
	 * <br>注册
	 */
	@RequestMapping("/sign_up_submit")
	void sign_up_submit(
			Model model, 
			String name, 
			String nickname, 
			String password, 
			String female,
			StandardMultipartHttpServletRequest freq,
			HttpServletResponse resp){
		String pic = FileManager.inst().uploadFileSimple(freq, ACCOUNT_PICS_PATH);
		if(!Util.nonEmpty(name, password)){
			model.addAttribute("tips", "名称或密码不可为空");
		}
		SecurityManager.inst().signUp(name, nickname, password, female, pic);
		LogCore.BASE.info("sign_in_submit name={}, password={}, female={}, pic={}", name, password, female, pic);
		redirect(resp, "login");
	}
}