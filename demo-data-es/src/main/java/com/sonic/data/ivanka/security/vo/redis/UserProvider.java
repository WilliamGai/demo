package com.sonic.data.ivanka.security.vo.redis;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sonic.data.ivanka.security.vo.UserVO;
import com.sonic.website.core.spring.manger.SpringManager;
import com.sonic.website.redis.jedis.interfaces.JedisWrapper;
import com.sonic.website.redis.jedis.spring.JedisWrapperBase;

/***
 * sorted set<name,0>
 * dict <id, user>可用dict id,hash>扩展
 * <br> 
 */
@Component
public class UserProvider extends JedisWrapperBase{
    private static final String USERS_SET = "users^set";
    private static final String USER_LATEST_ID_KEY = "users_latest_id";//用户自增ID键
    
    public static UserProvider inst() {
        return SpringManager.inst().getBean(UserProvider.class);
    }
    
    public void saveOrUpdateUser(UserVO user) {
        zadd(USERS_SET, 0, user.getName());
        set(user.getName(), user.toJSONString());
    }

    public UserVO getUserByName(String name) {
        String json= get(name);
        return UserVO.parseObject(json, UserVO.class);
    }
    /**
     * @see JedisWrapper#zcard
     */
    public Long getUsersNum(){
        return zcard(USERS_SET);
    }
    public Map<String, UserVO> getAllUsers() {
        Set<String> _set = zrange(USERS_SET, 0, -1);
        return _set.stream()
                .map(this::getUserByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(UserVO::getName, Function.identity()));
    }

    public void deleteUser(String name) {
        del(name);
        zrem(USERS_SET, name);
    }

    public Boolean existUserByName(String name) {
        return exist(name);
    }

    public Integer applyNewUserId() {
        return incr(USER_LATEST_ID_KEY).intValue();
    }
    
}
