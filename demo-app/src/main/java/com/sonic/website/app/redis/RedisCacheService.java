package com.sonic.website.app.redis;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.sonic.website.app.security.vo.UserVO;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;

/**
 * 示例:基于注解的方式使用redis作为缓存
 * key="#name+'_'+#password" keyGenerator="smpkeyGenerator"
 */
@Service
public class RedisCacheService {
    /**只有name不为null, 且返回结果不为null的时候才加入缓存*/
    @Cacheable(value = "usercache", keyGenerator = "smpkeyGenerator",condition="#name!=null", unless = "#result==null")
    public UserVO getUser(String name, String password) {
        LogCore.BASE.debug("invoke getUser:{},{}", name, password);
        if (Util.anyNonEmpty(name, password)) {
            return new UserVO(name, password);
        }
        return null;
    }

    /**
     * allEntries=true 是立即清除所有缓存
     */
    @CacheEvict(value = "usercache", keyGenerator = "smpkeyGenerator")
    public boolean clearUser(String name, String password) {
        LogCore.BASE.debug("invoke clear:{},{}", name, password);
        return true;
    }

    @CachePut(value = "usercache", keyGenerator = "smpkeyGenerator")
    public UserVO putUser(String name, String password) {
        UserVO usr = new UserVO(name, password);
        return usr;
    }
}
