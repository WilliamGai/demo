package com.sonic.data.ivanka.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sonic.data.ivanka.security.SecurityManager;
import com.sonic.data.ivanka.security.vo.UserVO;
import com.sonic.website.core.common.support.DataVO;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.spring.interfaces.AccessSupport;
/**
 * 参考
 */
@Controller
@Order(value = 6)
@RequestMapping("/mg")
public class MGControllerExcample implements AccessSupport {

    /**
     * 修改变量
     */
    @RequestMapping("/mg")
    String gm(Model model, HttpServletRequest req) {
        List<DataVO> list = new ArrayList<>();
        list.sort((x,y) -> x.name.compareTo(y.name));
        LogCore.BASE.debug("all constants :{}", list);
        
        model.addAttribute("constant_list", list);
        Object _old_user = req.getSession().getAttribute("user");
        if(_old_user instanceof UserVO && "admin".equals(((UserVO)_old_user).getName())){
            Collection<UserVO> user_list = SecurityManager.inst().getAllUsers();
            model.addAttribute("user_list", user_list);
        }
        return "mg";
    }


}
