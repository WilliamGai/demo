package com.sincetimes.website.app.security.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sincetimes.website.app.security.GenderType;
import com.sincetimes.website.core.common.vo.VOBase;
/**
 * TODO
 * <br>
 * 与UserVO分开
 */
public class UserProfileVO extends VOBase implements Serializable{
	private static final long serialVersionUID = 4198397105905294368L;
	private int id;
	private String name;//登录名 unique
	private String password;
	private String nickname;
	private GenderType gender = GenderType.MALE;//性别
	private String pic;
	private List<String> roleIds = new ArrayList<>();
	private long createTime;
	private long updateTime;
	private String createdBy;
	private long lastLoginTime;
	private byte status;// -1已删除
	
	public UserProfileVO() {
		super();
	}

	public UserProfileVO(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}


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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public GenderType getGender() {
		return gender;
	}

	public void setGender(GenderType gender) {
		this.gender = gender;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public List<String> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}
}