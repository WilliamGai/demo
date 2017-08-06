package com.sincetimes.website.app.security.vo.shared;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.serialize.IOUtil;
import com.sincetimes.website.core.spring.manger.SpringManager;

@Component
public class UserReference extends ManagerBase{
	private static final String USERS_FILE_NAME = "users.os";
	public Map<String, UserVO> USERS = new HashMap<>();
	public static UserReference inst() {
		return SpringManager.inst().getBean(UserReference.class);
	}
	
	@SuppressWarnings("deprecation")
	public void saveOrUpdateUser(UserVO user) {
		USERS.put(user.getName(), user);
		IOUtil.writeObject(USERS, USERS_FILE_NAME);
	}

	public UserVO getUserByName(String name) {
		return USERS.get(name);
	}
	
	public Integer getUsersNum(){
		return USERS.size();
	}
	public Map<String, UserVO> getAllUsers() {
		return USERS;
	}

	@SuppressWarnings("deprecation")
	public void deleteUser(String name) {
		USERS.remove(name);
		IOUtil.writeObject(USERS, USERS_FILE_NAME);
	}

	public Boolean existUserByName(String name) {
		return USERS.containsKey(name);
	}

	public synchronized Integer applyNewUserId() {
		int nowMaxId = USERS.values().parallelStream().mapToInt(UserVO::getId).max().getAsInt();
		return nowMaxId + 1; 
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init() {
		USERS = IOUtil.readObject(USERS_FILE_NAME);
	}
	
}
