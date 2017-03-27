package com.sincetimes.website.redis.jedis.funciton;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public interface JedisCallBack<T> {
	T doInRedis(Jedis jedis) throws JedisException;
}
