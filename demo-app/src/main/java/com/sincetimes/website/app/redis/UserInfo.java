package com.sincetimes.website.app.redis;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserInfo implements Serializable {
	private static final long serialVersionUID = -2833266909751486658L;
	public String no;
	public byte gender;
	public String nickName;
	
	public UserInfo() {
		super();
	}

	public UserInfo(String no, byte gender, String nickName) {
		super();
		this.no = no;
		this.gender = gender;
		this.nickName = nickName;
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
