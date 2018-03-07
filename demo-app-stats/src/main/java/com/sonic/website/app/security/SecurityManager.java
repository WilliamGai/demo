package com.sonic.website.app.security;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.sonic.website.app.security.vo.Permission;
import com.sonic.website.app.security.vo.RoleVO;
import com.sonic.website.app.security.vo.UserVO;
import com.sonic.website.app.security.vo.shared.RoleReference;
import com.sonic.website.app.security.vo.shared.UserReference;
import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.ParamResult;
import com.sonic.website.core.common.support.SHA256;
import com.sonic.website.core.common.support.TimeUtil;
import com.sonic.website.core.common.support.Util;

@Component
public class SecurityManager extends ManagerBase {
	
	public static final byte USER_NOT_EXIST = -1;
	public static final byte WRONG_PASSWORD = -2;
	public static final byte USER_EXIST = -3;
	public static final byte PARAM_ERR = -4;//参数错误
	public static final byte DUPLICATE_KEY = -5;
	public static final byte ROLE_EXIST = -6;
	
	public static SecurityManager inst() {
		return ManagerBase.inst(SecurityManager.class);
	}
	
	/* 
	 * @see com.sincetimes.website.core.common.manager.ManagerBase#init()
	 */
	@Override
	public void init() {
		int user_num = UserReference.inst().getUsersNum();
		if(user_num <= 0){
			LogCore.BASE.info("Accounts is empty, an admin account will be inited!, num in sortset={},usermap={} ",user_num );
			UserReference.inst().saveOrUpdateUser(new UserVO("admin", SHA256.sha256("admin")));
			return;
		}
		/*耗时*/
		Map<String, UserVO> users = UserReference.inst().getAllUsers();
		if(user_num != users.size()){
			LogCore.BASE.error("Accounts data err!, num in sortset={},usermap.size={} ",user_num, users.size());
		}
	}
	
	/**
	 * 注册
	 * @param name
	 * @param password
	 * @param female
	 * @param img_url
	 */
	public ParamResult signUp(String name, String nickname, String password, String female, String pic) {
		if(!Util.nonEmpty(name, password)){
			return new ParamResult(PARAM_ERR, "name or password empty!");
		}
		Boolean exist = UserReference.inst().existUserByName(name);
		if(exist){
			return new ParamResult(USER_EXIST,  "user exist!");
		}
		return saveOrUpdateUser(name, nickname, password, female, pic);
	}
	/**
	 * 没有则创建,有则更改
	 */
	public ParamResult saveOrUpdateUser(String name, String nickname, String password, String female, String pic) {
		if(Util.isEmpty(name)){
			return new ParamResult(PARAM_ERR, "name is empty!");
		}
		if(!Util.anyNonEmpty(password, female, pic)){
			return new ParamResult(PARAM_ERR, "no params!");
		}
		UserVO user = UserReference.inst().getUserByName(name);
		/*创建*/
		if(null == user){
			user = new UserVO(name, SHA256.sha256(password));
			user.setId(UserReference.inst().applyNewUserId());
			user.setCreateTime(System.currentTimeMillis());
			user.setLastLoginTime(System.currentTimeMillis());
		}
		if(!Util.isEmpty(pic)){
			user.setPic(pic);
		}
		if(!Util.isEmpty(nickname)){
			user.setNickname(nickname);
		}
		if(!Util.isEmpty(female)){
			user.setGender(GenderType.FEMALE);
		}else{
			user.setGender(GenderType.MALE);
		}
		UserReference.inst().saveOrUpdateUser(user);
		return new ParamResult().success().put(user);
	}
	
	/**
	 * @param username
	 * @param password
	 * @param func_passed
	 * @return
	 */
	public ParamResult pass(String name, String password) {
		UserVO user = UserReference.inst().getUserByName(name);
		if(null == user){
			return new ParamResult(USER_NOT_EXIST, "the user not exist");
		}
		String _password = user.getPassword();
		String _sha256_password = SHA256.sha256(password);
		boolean rst = Objects.equals(_password, _sha256_password);
		LogCore.BASE.info("name:{},password:{}, _sha256_password:{}, passwordneeded:{},reslut:{}", name, password, _sha256_password, _password, rst);
		if (rst) {
			user.setLastLoginTime(user.getLoginTime());
			user.setLoginTime(System.currentTimeMillis());
			saveOrUpdateUser(user);
			return new ParamResult().success().put(user);
		}
		return new ParamResult(WRONG_PASSWORD, "wrong password");
	}

	public Collection<UserVO> getAllUsers() {
		return UserReference.inst().getAllUsers().values();
	}

	public void deleteUser(String name) {
		UserReference.inst().deleteUser(name);
	}

	public ParamResult addRole(String roleId, String roleName) {
		if(!Util.nonEmpty(roleId, roleName)){
			return new ParamResult(PARAM_ERR, "param empty!");
		}
		if(RoleReference.inst().existRoleId(roleId)){
			return new ParamResult(DUPLICATE_KEY, "roleid exist!");
		}
		RoleVO role = RoleReference.inst().saveOrUpdateRole(new RoleVO(roleId, roleName));
		return new ParamResult().success().put(role);
	}

	public void deleteRole(String roleId) {
		RoleReference.inst().deleteRole(roleId);
	}

	public ParamResult addPermission(String roleId, String permissionName, String uri) {
		if(!Util.nonEmpty(roleId, permissionName, uri)){
			return new ParamResult(PARAM_ERR, "param empty!"); 
		}
		RoleVO role = RoleReference.inst().getRoleById(roleId);
		if(null == role){
			return new ParamResult(ROLE_EXIST, "role not exist!"); 
		}
		role.addPermission(new Permission(permissionName, uri));
		RoleReference.inst().saveOrUpdateRole(role);
		return new ParamResult().success().put(role);
	}

	public ParamResult deletePermission(String roleId, int index) {
		if(!Util.notEmpty(roleId)){
			return new ParamResult(PARAM_ERR, "param empty!"); 
		}
		RoleVO role = RoleReference.inst().getRoleById(roleId);
		if(null == role){
			return new ParamResult(ROLE_EXIST, "role not exist!"); 
		}
		role.deletePermission(index);
		RoleReference.inst().saveOrUpdateRole(role);
		return new ParamResult().success().put(role);
	}

	public Collection<RoleVO> getAllRoles() {
		return RoleReference.inst().getAllRoles().values();
	}

	public RoleVO getRole(String roleId) {
		return RoleReference.inst().getRoleById(roleId);
	}
	
	public UserVO getUser(String userName) {
		return UserReference.inst().getUserByName(userName);
	}
	
	public UserVO initUser(UserVO user){
		user.setCreateTimeStr(TimeUtil.SDF.get().format(new Date(user.getCreateTime())));
		user.setUpdateTimeStr(TimeUtil.SDF.get().format(new Date(user.getUpdateTime())));
		user.setLastLoginTimeStr(TimeUtil.SDF.get().format(new Date(user.getLastLoginTime())));
		
		List<String> names = RoleReference.inst().getNamesByIds(user.getRoleIds().toArray(new String[]{}));
		user.setRoleNames(names);
		
		Set<Permission> permissions = RoleReference.inst().getPermissionsByIds(user.getRoleIds().toArray(new String[]{}));
		user.setPermissions(permissions);
		List<RoleVO> roles = RoleReference.inst().getRolesByIds(user.getRoleIds().toArray(new String[]{}));
		user.setRoles(roles);
		return user;
	}

	public void addUserRole(String userName, String roleId) {
		UserVO user = getUser(userName);
		if(user.addRoleId(roleId)){
			UserReference.inst().saveOrUpdateUser(user);
		}
	}

	public void saveOrUpdateUser(UserVO user) {
		if(null == user){
			return;
		}
		UserReference.inst().saveOrUpdateUser(user);
	}
	
}