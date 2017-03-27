package com.sincetimes.website.app.security.vo;

import java.io.Serializable;
import java.util.List;

import com.sincetimes.website.vo.JsonVOBase;

public class RoleVO extends JsonVOBase implements Serializable{
	private static final long serialVersionUID = 8534234567721694017L;
	private int id;
	private String name; //unique
	private List<String> permissions;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
}
