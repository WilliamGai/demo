package com.sonic.website.redis.jedis.excample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sonic.website.redis.jedis.JedisPoolTemplate;
import com.sonic.website.redis.jedis.interfaces.JedisWrapper;
/**
 * 使用示例
 */
@Component
public class JedisServiceDemo implements JedisWrapper{

	@Autowired
	public JedisPoolTemplate jedisTemplate;

	@Override
	public JedisPoolTemplate template() {
		return jedisTemplate;
	}
}