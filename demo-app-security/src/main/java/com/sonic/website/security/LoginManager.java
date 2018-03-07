package com.sonic.website.security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.ParamResult;

@Component
public class LoginManager extends ManagerBase {
	@Value("${password}")
	public String password;

	public static LoginManager inst() {
		return ManagerBase.inst(LoginManager.class);
	}
	
	@Override
	public void init() {
		LogCore.BASE.info("password={}", password);
	}

	public ParamResult pass(String password2) {
		return null;
	}

}