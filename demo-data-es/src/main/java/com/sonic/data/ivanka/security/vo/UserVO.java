package com.sonic.data.ivanka.security.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sonic.data.ivanka.security.GenderType;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.common.vo.VOBase;

public class UserVO extends VOBase implements Serializable{
	private static final long serialVersionUID = -12245282415965949L;
	public static final byte USER_STATUS_OK_1 = 1;
	public static final byte USER_STATUS_LOCKED_0 = 0;
	
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
	private long loginTime;
	private byte status = 1;// 1正常,0锁定
	
	private List<RoleVO> roles = new ArrayList<>();
	private Set<Permission> permissions = new HashSet<>();//权限列表,从role取得
	private transient String createTimeStr;
	private transient String updateTimeStr;
	private transient String lastLoginTimeStr;
	private transient String loginTimeStr;
	private transient List<String> roleNames = new ArrayList<>();
	public UserVO() {
		super();
	}

	public UserVO(String name, String password) {
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
	
	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getUpdateTimeStr() {
		return updateTimeStr;
	}

	public void setUpdateTimeStr(String updateTimeStr) {
		this.updateTimeStr = updateTimeStr;
	}

	public String getLastLoginTimeStr() {
		return lastLoginTimeStr;
	}

	public void setLastLoginTimeStr(String lastLoginTimeStr) {
		this.lastLoginTimeStr = lastLoginTimeStr;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public String getLoginTimeStr() {
		return loginTimeStr;
	}

	public void setLoginTimeStr(String loginTimeStr) {
		this.loginTimeStr = loginTimeStr;
	}

	public List<String> getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}

	public List<RoleVO> getRoles() {
		return roles;
	}
	public List<RolePermission> rolePermissionList(){
		List<RolePermission> list = new ArrayList<>();
		if(Util.isEmpty(roles)){
			return list;
		}
		roles.forEach(role->{
			role.getPermissions().stream()
				.map(pms-> new RolePermission(pms, role.getName()))
				.forEach(list::add);
		});
		return list;
	}
	public void setRoles(List<RoleVO> roles) {
		this.roles = roles;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}
	
	/**
	 * @param roleId
	 * @return 已经存在或者roleId为空返回false
	 */
	public boolean addRoleId(String roleId) {
		if(Util.isEmpty(roleId)){
			return false;
		}
		if(roleIds.contains(roleId)){
			return false;
		}
		roleIds.add(roleId);
		return true;
	}
}