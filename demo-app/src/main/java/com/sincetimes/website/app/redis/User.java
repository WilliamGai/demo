package com.sincetimes.website.app.redis;


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class User{
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
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
