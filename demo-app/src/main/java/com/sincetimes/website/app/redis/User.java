package com.sincetimes.website.app.redis;


import com.sincetimes.website.core.common.vo.ToStringAbstract;

public class User extends ToStringAbstract{
//	private static final long serialVersionUID = 1L;
	public String no;
	public String name;
	public long time;
	public User() {
	}

	public User(String no, String name) {
		this.no = no;
		this.name = name;
		this.time = System.currentTimeMillis();
	}
}
