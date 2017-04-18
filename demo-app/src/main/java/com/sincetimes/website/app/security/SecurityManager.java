package com.sincetimes.website.app.security;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.sincetimes.website.app.security.vo.Permission;
import com.sincetimes.website.app.security.vo.RoleVO;
import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.app.security.vo.redis.RoleProvider;
import com.sincetimes.website.app.security.vo.redis.UserProvider;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.ParamResult;
import com.sincetimes.website.core.common.support.SHA256;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.common.support.Util;

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
		Long user_num = UserProvider.inst().getUsersNum();
		if(user_num <= 0){
			LogCore.BASE.info("Accounts is empty, an admin account will be inited!, num in sortset={},usermap={} ",user_num );
			UserProvider.inst().saveOrUpdateUser(new UserVO("admin", SHA256.sha256("admin")));
			return;
		}
		/*耗时*/
		Map<String, UserVO> users = UserProvider.inst().getAllUsers();
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
		Boolean exist = UserProvider.inst().existUserByName(name);
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
		UserVO user = UserProvider.inst().getUserByName(name);
		/*创建*/
		if(null == user){
			user = new UserVO(name, SHA256.sha256(password));
			user.setId(UserProvider.inst().applyNewUserId());
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
		UserProvider.inst().saveOrUpdateUser(user);
		return new ParamResult().success().put(user);
	}
	
	/**
	 * @param username
	 * @param password
	 * @param func_passed
	 * @return
	 */
	public ParamResult pass(String name, String password) {
		UserVO user = UserProvider.inst().getUserByName(name);
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
		return UserProvider.inst().getAllUsers().values();
	}

	public void deleteUser(String name) {
		UserProvider.inst().deleteUser(name);
	}

	public ParamResult addRole(String roleId, String roleName) {
		if(!Util.nonEmpty(roleId, roleName)){
			return new ParamResult(PARAM_ERR, "param empty!");
		}
		if(RoleProvider.inst().existRoleId(roleId)){
			return new ParamResult(DUPLICATE_KEY, "roleid exist!");
		}
		RoleVO role = RoleProvider.inst().saveOrUpdateRole(new RoleVO(roleId, roleName));
		return new ParamResult().success().put(role);
	}

	public void deleteRole(String roleId) {
		RoleProvider.inst().deleteRole(roleId);
	}

	public ParamResult addPermission(String roleId, String permissionName, String uri) {
		if(!Util.nonEmpty(roleId, permissionName, uri)){
			return new ParamResult(PARAM_ERR, "param empty!"); 
		}
		RoleVO role = RoleProvider.inst().getRoleById(roleId);
		if(null == role){
			return new ParamResult(ROLE_EXIST, "role not exist!"); 
		}
		role.addPermission(new Permission(permissionName, uri));
		RoleProvider.inst().saveOrUpdateRole(role);
		return new ParamResult().success().put(role);
	}

	public ParamResult deletePermission(String roleId, int index) {
		if(!Util.nonEmpty(roleId)){
			return new ParamResult(PARAM_ERR, "param empty!"); 
		}
		RoleVO role = RoleProvider.inst().getRoleById(roleId);
		if(null == role){
			return new ParamResult(ROLE_EXIST, "role not exist!"); 
		}
		role.deletePermission(index);
		RoleProvider.inst().saveOrUpdateRole(role);
		return new ParamResult().success().put(role);
	}

	public Collection<RoleVO> getAllRoles() {
		return RoleProvider.inst().getAllRoles().values();
	}

	public RoleVO getRole(String roleId) {
		return RoleProvider.inst().getRoleById(roleId);
	}
	
	public UserVO getUser(String userName) {
		return UserProvider.inst().getUserByName(userName);
	}
	
	public UserVO initUser(UserVO user){
		user.setCreateTimeStr(TimeTool.SDF.get().format(new Date(user.getCreateTime())));
		user.setUpdateTimeStr(TimeTool.SDF.get().format(new Date(user.getUpdateTime())));
		user.setLastLoginTimeStr(TimeTool.SDF.get().format(new Date(user.getLastLoginTime())));
		
		List<String> names = RoleProvider.inst().getNamesByIds(user.getRoleIds().toArray(new String[]{}));
		user.setRoleNames(names);
		
		Set<Permission> permissions = RoleProvider.inst().getPermissionsByIds(user.getRoleIds().toArray(new String[]{}));
		user.setPermissions(permissions);
		List<RoleVO> roles = RoleProvider.inst().getRolesByIds(user.getRoleIds().toArray(new String[]{}));
		user.setRoles(roles);
		return user;
	}

	public void addUserRole(String userName, String roleId) {
		UserVO user = getUser(userName);
		if(user.addRoleId(roleId)){
			UserProvider.inst().saveOrUpdateUser(user);
		}
	}

	public void saveOrUpdateUser(UserVO user) {
		if(null == user){
			return;
		}
		UserProvider.inst().saveOrUpdateUser(user);
	}
	
}