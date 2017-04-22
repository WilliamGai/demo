package com.sincetimes.website.app.security.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sincetimes.website.vo.VOBase;

public class RoleVO extends VOBase implements Serializable{
	private static final long serialVersionUID = 8534234567721694017L;
	private String id;
	private String name; //unique
	private List<Permission> permissions = new ArrayList<>();
	private byte status;// -1已删除
	public RoleVO() {
		super();
	}
	public RoleVO(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	public void addPermission(Permission permission) {
		permissions.add(permission);
		
	}
	public void deletePermission(int index) {
		permissions.remove(index);
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
}
