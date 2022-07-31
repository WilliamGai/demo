package com.sonic.website.app.security.vo.redis;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.sonic.website.app.security.vo.Permission;
import com.sonic.website.app.security.vo.RoleVO;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.spring.manger.SpringManager;
import com.sonic.website.redis.jedis.spring.JedisWrapperBase;

/**
 * 1. getNameById(id) <br>
 * 2. getAllRoles(); <br>
 * 3. getRoleById(id);  <br>
 * 4. deleteById(id) <br>
 * 5. existId(id)  <br>
 * 6. getNamesByIds(ids ...); <br>
 * 7. getNameById(id)
 * TODO:
 * dict<id, roleVo>  <br>
 * hash<name,id>
 * @see {@link UserProvider}
 */
@Component
public class RoleProvider extends JedisWrapperBase{
    /** zset <id, 0> */
    private static final String ROLES_SET = "roles^set";

    public static RoleProvider inst() {
        return SpringManager.inst().getBean(RoleProvider.class);
    }
    
    public RoleVO saveOrUpdateRole(RoleVO role) {
        zadd(ROLES_SET, 0, role.getId());
        set(role.getId(), role.toJSONString());
        return role;
    }
    
    public RoleVO  getRoleById(String id){
        String json= get(id);
        return RoleVO.parseObject(json, RoleVO.class);
    }

    public Map<String, RoleVO> getAllRoles() {
        Set<String> ids = zrange(ROLES_SET, 0, -1);
        return ids.stream()
                .map(this::getRoleById)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(RoleVO::getId, Function.identity()));
    }

    public Boolean existRoleId(String id) {
        return exist(id);
    }
    
    public void deleteRole(String roleId) {
        del(roleId);
        zrem(ROLES_SET, roleId);
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
        Set<Permission> ps = new LinkedHashSet<>();
        if(Util.isEmpty(ids)){
            return ps;
        }
        Stream.of(ids).forEach(id ->{
            ps.addAll(getPermissionById(id));
        });
        return ps;
    }

    public List<RoleVO> getRolesByIds(String ... ids) {
        if(Util.isEmpty(ids)){
            return new ArrayList<>();
        }
        return Stream.of(ids).map(this::getRoleById).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Long getRolesNum() {
        return zcard(ROLES_SET); 
    }
}
