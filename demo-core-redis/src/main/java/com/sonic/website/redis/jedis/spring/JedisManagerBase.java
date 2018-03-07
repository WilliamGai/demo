package com.sonic.website.redis.jedis.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.redis.jedis.JedisPoolTemplate;
import com.sonic.website.redis.jedis.interfaces.JedisWrapper;

/**
 * 为ManagerBase提供的redis操作,所有继承此类的manager都会以自己的类名作为redis的"域"来操作自己的数据<br>
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

