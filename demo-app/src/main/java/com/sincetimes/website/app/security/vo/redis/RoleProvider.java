package com.sincetimes.website.app.security.vo.redis;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sincetimes.website.app.security.vo.RoleVO;
import com.sincetimes.website.core.spring.manger.SpringManager;
import com.sincetimes.website.redis.jedis.spring.JedisWrapperBase;

@Component
public class RoleProvider extends JedisWrapperBase{
	/** hash <name, id> */
	private static final String ROLES_NAME_ID_HASH_SET = "roles^hashset";

	public static UserProvider inst() {
		return SpringManager.inst().getBean(UserProvider.class);
	}
	
	public int saveOrUpdateRole(RoleVO role) {
		hset(ROLES_NAME_ID_HASH_SET, role.getName(), role.getId()+"");
		set(role.getId()+"", role.toJSONString());
		return 1;
	}
	
	public RoleVO  getRoleById(String id){
		String json= get(id);
		return RoleVO.parseObject(json, RoleVO.class);
	}
	public RoleVO getRoleByName(String name) {
		String id = hget(ROLES_NAME_ID_HASH_SET, name);
		return getRoleById(id);
	}

	public void deleteRoleByName(String name) {
		String id = hget(ROLES_NAME_ID_HASH_SET, name);
		hdel(ROLES_NAME_ID_HASH_SET, name);
		del(id);
	}

	public Map<String, RoleVO> getAllRoles() {
		List<String> ids = hvals(ROLES_NAME_ID_HASH_SET);
		return ids.stream()
				.map(this::getRoleById)
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(RoleVO::getName, Function.identity()));
	}

	public Boolean existRoleId(int id) {
		return exist(id+"");
	}
	
	/** 耗时 */
	public Boolean existRoleName(String name) {
		return hexists(ROLES_NAME_ID_HASH_SET, name);
	}
}
