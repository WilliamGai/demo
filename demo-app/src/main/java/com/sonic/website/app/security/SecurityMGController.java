package com.sonic.website.app.security;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonic.website.app.security.interfaces.SecureAccessSupport;
import com.sonic.website.app.security.vo.RoleVO;
import com.sonic.website.app.security.vo.UserVO;
import com.sonic.website.core.common.support.DataResult;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.ParamResult;
import com.sonic.website.core.common.support.Util;

@Controller
@Order(value = 6)
@RequestMapping("/mg/secure")
public class SecurityMGController implements SecureAccessSupport {
    private static final String CONFIG_USER_KEY = "config_user";
    @RequestMapping
    void pageTemplate(HttpServletRequest req, HttpServletResponse resp){
        redirect(resp, req.getRequestURL()+"/secure_users");
    }
    /**
     * 用户管理列表
     */
    @RequestMapping("/secure_users")
    String accounts(Model model, HttpServletRequest req) {
        Collection<UserVO> users = SecurityManager.inst().getAllUsers();
        users.forEach(UserVO::getCreatedBy);
        model.addAttribute("users", users);
        return "secure_users";
    }
    /**
     * 权限列表
     */
    @RequestMapping("/secure_roles")
    String secure_roles(Model model, HttpServletRequest req) {
        Collection<RoleVO> roles = SecurityManager.inst().getAllRoles();
        model.addAttribute("roles", roles);
        return "secure_roles";
    }

    /**  用户登录后主界面 */
    @RequestMapping("/secure_user_config")
    String secure_user_config(Model model, String user_name, HttpServletRequest req) {
        UserVO user = null;
        if(!Util.isEmpty(user_name)){
            user = SecurityManager.inst().getUserAndInit(user_name);
        }
        if(null == user){
            user = new UserVO();
        }
        Collection<RoleVO> roles = SecurityManager.inst().getAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute(CONFIG_USER_KEY, user);
        LogCore.BASE.debug("user config user={}, all sys's roles={}", user, roles);
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
    void delete_user(String user_name, HttpServletResponse resp) {
        SecurityManager.inst().deleteUser(user_name);
        redirect(resp, "secure_users");
    }
    
    @RequestMapping("/lock_user")
    void lock_user(String user_name, HttpServletResponse resp) {
        SecurityManager.inst().lockUser(user_name);
        redirect(resp, "secure_users");
    }
    @RequestMapping("/unlock_user")
    void unlock_user(String user_name, HttpServletResponse resp) {
        SecurityManager.inst().unlockUser(user_name);
        redirect(resp, "secure_users");
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
        if(Util.isEmpty(role_id)){
            return "secure_role";
        }
        RoleVO role = SecurityManager.inst().getRole(role_id);
        model.addAttribute("role", role);
        return "secure_role";
    }
    @RequestMapping("/login")
    void login(HttpServletRequest req, HttpServletResponse resp) {
//        forward(req, resp, "/login");
        redirect(resp, "/login");
    }
    
}
