package com.sincetimes.website.redis.jedis.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sincetimes.website.redis.jedis.JedisPoolTemplate;
import com.sincetimes.website.redis.jedis.interfaces.JedisWrapper;

/**
 * 所有继承此类的实现类都可以以自己的类名作为redis的"域"来操作redis数据<br>
 * 所有继承此类的子类不可以在上线后更改自己的类名
 * <br>
 * 需要初始化加载信息应使用{@link JedisManagerBase}
 */
@Component
public abstract class JedisWrapperBase implements JedisWrapper{

	@Autowired
	public JedisPoolTemplate jedisTemplate;
	
	@Override
	public JedisPoolTemplate template() {
		return jedisTemplate;
	}
}
