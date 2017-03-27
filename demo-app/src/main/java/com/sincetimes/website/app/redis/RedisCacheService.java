package com.sincetimes.website.app.redis;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;

/**
 * key="#no+'_'+#name" keyGenerator="smpkeyGenerator"
 */
@Service
public class RedisCacheService {
	/**只有name不为null, 且返回结果不为null的时候才加入缓存*/
	@Cacheable(value = "usercache", keyGenerator = "smpkeyGenerator",condition="#name!=null", unless = "#result==null")
	public User getUser(String no, String name) {
		LogCore.BASE.debug("invoke getUser:{},{}", no, name);
		if (Util.anyNonEmpty(no, name)) {
			return new User(no, name);
		}
		return null;
	}

	/**
	 * allEntries=true 是立即清除所有缓存
	 */
	@CacheEvict(value = "usercache", keyGenerator = "smpkeyGenerator")
	public boolean clearUser(String no, String name) {
		LogCore.BASE.debug("invoke clear:{},{}", no, name);
		return true;
	}

	@CachePut(value = "usercache", keyGenerator = "smpkeyGenerator")
	public User putUser(String no, String name) {
		User usr = new User(no, name);
		return usr;
	}
}
