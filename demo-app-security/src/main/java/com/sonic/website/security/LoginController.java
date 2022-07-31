package com.sonic.website.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.ParamResult;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.spring.HttpHeadUtil;
import com.sonic.website.core.spring.interfaces.AccessSupport;

@Controller
public class LoginController implements AccessSupport {

    @RequestMapping("/login")
    String login(Model model, HttpServletRequest req, HttpServletResponse rsp) throws IOException {
        Object _old_user = req.getSession().getAttribute("user");
        Object redirect_url = req.getAttribute("redirect_url");
        LogCore.BASE.info("redirect_url:{}", redirect_url);
        LogCore.BASE.info("user recorded by session:{}", _old_user);
        if (Util.isEmpty(redirect_url)) {
            redirect_url = "mg/code";
        }
        model.addAttribute("redirect_url", redirect_url);
        String ssid = req.getSession().getId();
        String password = req.getParameter("password");
        if (Util.isEmpty(password)) {
            return "login";
        }
        ParamResult result = LoginManager.inst().pass(password);
        if (result.isSuccess()) {
            req.getSession().setAttribute("user", result.get());
            LogCore.BASE.info("go -------->:{}", redirect_url);
            redirect(rsp, redirect_url.toString());
            return null;
        }
        model.addAttribute("data", ssid + result + HttpHeadUtil.getParamsMapLimit(req));// 调试用
        model.addAttribute("password", password);
        return "login";
    }
}