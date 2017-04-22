package com.sincetimes.website.app.security.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Permission implements Serializable{
	private static final long serialVersionUID = 6075482255307965611L;
	private String name;
	private String uri;
	
	public Permission() {
		super();
	}
	
	public Permission(String name, String uri) {
		super();
		this.name = name;
		this.uri = uri;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
