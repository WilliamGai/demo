
package com.sincetimes.website.app.security.vo.shared;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.sincetimes.website.app.security.vo.Permission;
import com.sincetimes.website.app.security.vo.RoleVO;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.manger.SpringManager;
@Component
public class RoleReference extends ManagerBase{
	private static final String ROLES_FILE_NAME = "roles.os";
	public Map<String, RoleVO> ROLES = new HashMap<>();

	public static RoleReference inst() {
		return SpringManager.inst().getBean(RoleReference.class);
	}
	
	public RoleVO saveOrUpdateRole(RoleVO role) {
		ROLES.put(role.getId(), role);
		saveRoles();
		return role;
	}
	
	public RoleVO  getRoleById(String id){
		return ROLES.get(id);
	}

	public Map<String, RoleVO> getAllRoles() {
		return ROLES;
	}

	public Boolean existRoleId(String id) {
		return ROLES.containsKey(id);
	}
	
	public void deleteRole(String roleId) {
		ROLES.remove(roleId);
		saveRoles();
	}

	private void saveRoles() {
		Util.writeObject(ROLES, ROLES_FILE_NAME);
	}
	
	public String getNameById(String id){
		RoleVO role = getRoleById(id);
		if(null == role){
			return null;
		}
		return role.getName();
	}
	public List<Permission> getPermissionById(String id){
		RoleVO role = getRoleById(id);
		if(null == role){
			return new ArrayList<>();
		}
		return role.getPermissions();
	}
	public List<String> getNamesByIds(String ... ids) {
		if(Util.isEmpty(ids)){
			return new ArrayList<>();
		}
		return Stream.of(ids).map(this::getNameById).filter(Objects::nonNull).collect(Collectors.toList());
	}
	/**
	 * 返回顺序不固定
	 * @param ids
	 * @return
	 */
	public Set<Permission> getPermissionsByIds(String ... ids){
		Set<Permission> ps = new HashSet<>();
		if(Util.isEmpty(ids)){
			return ps;
		}
		Stream.of(ids).forEach(id ->{
			ps.addAll(getPermissionById(id));
		});
		LogCore.BASE.info("ids={}, permissions={}", ids, ps);
		return ps;
	}

	public List<RoleVO> getRolesByIds(String ... ids) {
		if(Util.isEmpty(ids)){
			return new ArrayList<>();
		}
		return Stream.of(ids).map(this::getRoleById).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public void init() {
		Map<String, RoleVO> tempMap = Util.readObject(ROLES_FILE_NAME);
		if(!Util.isEmpty(tempMap)){
			ROLES = tempMap;
		}
	}
}
