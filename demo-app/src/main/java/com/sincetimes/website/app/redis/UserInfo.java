package com.sincetimes.website.app.redis;

import java.io.Serializable;

import com.sincetimes.website.core.common.vo.ToStringAbstract;

public class UserInfo extends ToStringAbstract implements Serializable {
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
}
