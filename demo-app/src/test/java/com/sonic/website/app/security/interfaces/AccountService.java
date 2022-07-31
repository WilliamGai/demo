package com.sonic.website.app.security.interfaces;

import java.util.Map;

import com.sonic.website.app.security.vo.RoleVO;
import com.sonic.website.app.security.vo.UserVO;

public interface AccountService {
    /*user */
    int saveOrUpdateUser(UserVO user);

    UserVO getUserByName(String name);
    
    Boolean existUserByName(String name);

    void deleteUser(String name);
    
    Map<String, UserVO> getAllUsers();
    
    /*role */
    int saveOrUpdateRole(RoleVO role);
    
    RoleVO getRoleByName(String name);
    
    void deleteRole(String name);
    
    Map<String, RoleVO> getAllRoles();
    
    Boolean existRoleById(int id);
    Boolean existRoleByName(String name);

}