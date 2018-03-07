package com.sonic.website.app.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.redis.jedis.JedisPoolTemplate;

@Service("StatsServiceJedisImpl")
public class StatsServiceJedisImpl implements StatsService {

	@Autowired
	JedisPoolTemplate jedisTemplate;
	
	@Override
	public boolean isExist(String name) {
		return jedisTemplate.excute((jedis) -> jedis.exists(name));
	}

	@Override
	public long incr(String name) {
		return jedisTemplate.excute((jedis) -> jedis.incr(name));
	}

	@Override
	public long incrBy(String name, long value) {
		return jedisTemplate.excute((jedis) -> jedis.incrBy(name, value));
	}

	@Override
	public Long zadd(String key, double score, String value) {
		return jedisTemplate.excute((jedis) -> jedis.zadd(key, score, value));
	}
	
	@Override
	public Double zincrby(String key, double score, String value) {
		return jedisTemplate.excute((jedis) -> jedis.zincrby(key, score, value));
	}
	
	@Override
	public Double zscore(String key, String member) {
		return jedisTemplate.excute((jedis) -> jedis.zscore(key, member));
	}
	
	@Override
	public Set<String> zRange(String key) {
		return jedisTemplate.excute((jedis) -> jedis.zrange(key, 0, -1));
	}

	@Override
	public long hIncrBy(String key, String field, long value) {
		return jedisTemplate.excute((jedis) -> jedis.hincrBy(key, field, value));
	}

	@Override
	public <T> List<T> hGetAll2List(String key, BiFunction<byte[], byte[], T> f) {
		byte[] _key = Util.getUtfBytes(key);
		List<T> list = new ArrayList<>();
		Map<byte[], byte[]> _map = jedisTemplate.excute((jedis) -> jedis.hgetAll(_key));
		_map.forEach((k, v) -> list.add(f.apply(k, v)));
		return list;
	}

	@Override
	public long addRecordList(String listName, String sn) {
		String jsonStr = JSON.toJSONString((new Record(sn, System.currentTimeMillis())));
		return jedisTemplate.excute((jedis) -> jedis.lpush(listName, jsonStr));
	}

	/**
	 * 这一步比较慢可以异步加载
	 */
	@Override
	public Map<String, Long> getRecordList2Map(String listName, Predicate<? super Record> f) {
		List<byte[]> list = jedisTemplate.excute((jedis) -> jedis.lrange(Util.getUtfBytes(listName), 0, -1));
		return list.parallelStream().map(Record::getRecord).filter(f)
				.collect(Collectors.toConcurrentMap(Record::getSn, Record::getTime, (newValue, oldVlaue) -> newValue));
	}

	/**
	 * 设置新的返回旧的
	 */
	@Override
	public <T> void set(String name, T value) {
		jedisTemplate.excute((jedis) -> jedis.set(name, String.valueOf(value)));
	}

	@Override
	public String get(String name) {
		return jedisTemplate.excute((jedis) -> jedis.get(name));
	}
}
