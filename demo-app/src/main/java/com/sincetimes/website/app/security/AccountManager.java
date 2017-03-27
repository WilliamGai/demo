package com.sincetimes.website.app.security;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.app.security.vo.redis.UserProvider;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.ParamResult;
import com.sincetimes.website.core.common.support.SHA256;
import com.sincetimes.website.core.common.support.Util;

@Component
public class AccountManager extends ManagerBase {
	
	public static final byte ACCOUNT_USER_NO_EXIST = -1;
	public static final byte ACCOUNT_AUTHENTICATION_WRONG_PASSWORD = -2;
	public static final byte ACCOUNT_SIGN_UP_USER_NAME_EXIST = -3;
	public static final byte ACCOUNT_SIGN_UP_USER_NAME_PARAM_ERR = -4;//注册参数错误
	
	public static AccountManager inst() {
		return ManagerBase.inst(AccountManager.class);
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
			return new ParamResult(ACCOUNT_SIGN_UP_USER_NAME_PARAM_ERR, "name or password empty!");
		}
		Boolean exist = UserProvider.inst().existUserByName(name);
		if(exist){
			return new ParamResult(ACCOUNT_SIGN_UP_USER_NAME_EXIST,  "user exist!");
		}
		return saveOrUpdateUser(name, nickname, password, female, pic);
	}
	/**
	 * 没有则创建,有则更改
	 */
	public ParamResult saveOrUpdateUser(String name, String nickname, String password, String female, String pic) {
		if(Util.isEmpty(name)){
			return new ParamResult(ACCOUNT_SIGN_UP_USER_NAME_PARAM_ERR, "name is empty!");
		}
		if(!Util.anyNonEmpty(password, female, pic)){
			return new ParamResult(ACCOUNT_SIGN_UP_USER_NAME_PARAM_ERR, "no params!");
		}
		UserVO user = UserProvider.inst().getUserByName(name);
		/*创建*/
		if(null == user){
			user = new UserVO(name, SHA256.sha256(password));
			user.setId(UserProvider.inst().applyNewUserId());
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
	 * 
	 * @param username
	 * @param password
	 * @param func_passed
	 * @return
	 */
	public ParamResult pass(String name, String password) {
		UserVO user = UserProvider.inst().getUserByName(name);
		if(null == user){
			return new ParamResult(ACCOUNT_USER_NO_EXIST, "the user not exist");
		}
		String _password = user.getPassword();
		String _sha256_password = SHA256.sha256(password);
		boolean rst = Objects.equals(_password, _sha256_password);
		LogCore.BASE.info("name:{},password:{}, _sha256_password:{}, passwordneeded:{},reslut:{}", name, password, _sha256_password, _password, rst);
		if (rst) {
			return new ParamResult().success().put(user);
		}
		return new ParamResult(ACCOUNT_AUTHENTICATION_WRONG_PASSWORD, "wrong password");
	}

	public Collection<UserVO> getAllUsers() {
		return UserProvider.inst().getAllUsers().values();
	}

	public void deleteUser(String name) {
		UserProvider.inst().deleteUser(name);
	}
}