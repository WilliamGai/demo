package com.sincetimes.website.redis.jedis.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.redis.jedis.JedisPoolTemplate;
import com.sincetimes.website.redis.jedis.interfaces.JedisWrapper;

/**
 * 所有继承此类的派生类不可以在上线后更改自己的类名
 */
@Component
public abstract class JedisManagerBase extends ManagerBase implements JedisWrapper{

	@Autowired
	public JedisPoolTemplate jedisTemplate;
	
	@Override
	public JedisPoolTemplate template() {
		return jedisTemplate;
	}
}
