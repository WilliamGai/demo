package com.sonic.website.app.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import com.sonic.website.app.security.vo.UserVO;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;
/**
 * 使用spring的redisTemplate,目前在公众号用户信息中使用
 */
@Service
public class RedisService{
	@Autowired
	RedisTemplate<?, ?> redisTemplate;

	/** 获得客户端列表 */
	public List<?> getClients() {
		return redisTemplate.getClientList();
	}

	/**
	 * redis数据库条数
	 */
	public Long dbSize() {
		return redisTemplate.execute((RedisConnection c) -> c.dbSize());
	}

	public String ping() {
		return redisTemplate.execute((RedisConnection c) -> c.ping());
	}

	/**
	 * 删除所有"指定"数据库的数据
	 */
	public long flushDB() {
		return redisTemplate.execute((RedisConnection c) -> {
			c.flushDb();
			return 1L;
		});
	}

	/** 判断redis数据库是否有对应的key */
	public boolean exist(String key) {
		return redisTemplate.execute((RedisConnection c) -> c.exists(Util.getUtfBytes(key)));
	}

	/** 获得redis数据库所有的key */
	public Set<String> keys(String pattern) {
		return redisTemplate.execute((RedisConnection c) -> c.keys(pattern.getBytes()).stream().map(Util::getUtf)
				.collect(Collectors.toSet()));
	}

	/** 删除键 */
	public long del(String key) {
		return redisTemplate.execute((RedisConnection c) -> c.del(Util.getUtfBytes(key)));
	}

	/** 查找值 */
	public String get(String key) {
		return redisTemplate.execute((RedisConnection c) -> Util.getUtf(c.get(Util.getUtfBytes(key))));
	}

	/** 设置新值返旧值 */
	public String getSet(String key, String value) {
		return redisTemplate
				.execute((RedisConnection c) -> Util.getUtf(c.getSet(Util.getUtfBytes(key), Util.getUtfBytes(value))));
	}

	/** 设置有超时时间的KV */
	public Long set(String key, String value, long seconds) {
		return redisTemplate.execute((RedisConnection c) -> {
			c.set(key.getBytes(), value.getBytes());
			c.expire(key.getBytes(), seconds);
			return 1L;
		});
	}

	/**
	 * 存入不会超时的KV fv
	 */
	public Long set(String key, String value) {
		return redisTemplate.execute((RedisConnection c) -> {
			c.set(key.getBytes(), value.getBytes());
			return 1L;
		});
	}

	public Long incr(String key) {
		return redisTemplate.execute((RedisConnection c) -> c.incr(Util.getUtfBytes(key)));
	}

	public Long decr(String key) {
		return redisTemplate.execute((RedisConnection c) -> c.decr(Util.getUtfBytes(key)));
	}

	public Long incrby(String key, int value) {
		return redisTemplate.execute((RedisConnection c) -> c.incrBy(Util.getUtfBytes(key), value));
	}

	/** 尾部插入元素,适合批量插入 */
	public long rpush(String key, String... value) {
		return redisTemplate.execute((RedisConnection c) -> {
			LogCore.BASE.debug("redis rpush key={},size={}", key, value.length);
			if (Util.isEmpty(value)) {
				return -1L;
			}
			byte[][] _value = new byte[value.length][];
			for (int i = 0; i < value.length; i++) {
				_value[i] = Util.getUtfBytes(value[i]);
			}
			return c.rPush(Util.getUtfBytes(key), _value);
		});
	}

	public long llen(String key) {
		return redisTemplate.execute((RedisConnection c) -> {
			return c.lLen(Util.getUtfBytes(key));
		});
	}

	/**
	 * 将 srcKey列表的尾部的数据弹出一个并插入到dstKey列表中
	 * @param srcKey,此列表不存在会返回nil
	 * @param dstKey,此列表不存在会创建
	 * @return 可能为null
	 */
	public String rPopLpush(String srcKey, String dstKey) {
		return redisTemplate.execute((RedisConnection c) -> {
			byte[] _code = c.rPopLPush(Util.getUtfBytes(srcKey), Util.getUtfBytes(dstKey));
			return Util.getUtf(_code);
		});
	}

	/* 项目相关 */
	/**
	 * 序列化的时间很短
	 * 
	 * @param key
	 * @param map
	 *            要存的map
	 * @return
	 */
	public Long hmset(String key, Map<String, String> map) {
		byte[] _key = Util.getUtfBytes(key);
		Map<byte[], byte[]> hashes = new HashMap<>();
		map.forEach((k, v) -> {
			hashes.put(Util.getUtfBytes(k), Util.getUtfBytes(v));
		});
		return redisTemplate.execute((RedisConnection c) -> {
			c.hMSet(_key, hashes);
			return 1L;
		});
	}

	/** 有序的set */
	public Long zadd(String key, double score, String value) {
		byte[] _key = Util.getUtfBytes(key);
		byte[] _value = Util.getUtfBytes(value);
		LogCore.BASE.debug("zadd:{},{},{}", key,score, value);
		return redisTemplate.execute((RedisConnection c) -> {
			c.zAdd(_key, score, _value);
			return 1L;
		});
	}

	/** 有序的set, 权重相等 */
	public Long zadd(String key, String value) {
		return zadd(key, 0, value);
	}

	/** 没有则生成 */
	public Long zincrby(String key, double score, String value) {
		byte[] _key = Util.getUtfBytes(key);
		byte[] _value = Util.getUtfBytes(value);
		LogCore.BASE.debug("zincrby:{},{},{}", key, score, value);
		return redisTemplate.execute((RedisConnection c) -> {
			c.zIncrBy(_key, score, _value);
			return 1L;
		});
	}

	/** 有序的set, 获取全部, 可能费时 */
	public Set<String> zrange(String key) {
		Set<byte[]> set = zrangeBytes(key);
		return set.parallelStream().map(Util::getUtf).collect(Collectors.toSet());
	}
	public Set<Tuple> zRangeWithScores(String key) {
		byte[] _key = Util.getUtfBytes(key);
		return redisTemplate.execute((RedisConnection c) -> {
			return c.zRangeWithScores(_key, 0, -1);
		});
	}
	public Set<Tuple> zRevRangeWithScores(String key, long begin, long end) {
		byte[] _key = Util.getUtfBytes(key);
		return redisTemplate.execute((RedisConnection c) -> {
			return c.zRevRangeWithScores(_key, begin, end);
		});
	}
	/* 内部使用 */
	private Set<byte[]> zrangeBytes(String key) {
		return redisTemplate.execute((RedisConnection c) -> {
			return c.zRange(Util.getUtfBytes(key), 0, -1);
		});
	}

	/** 存入hash */
	public Long hset(String key, String field, String value) {
		LogCore.BASE.debug("hset  {}  {} {}", key, field, value);
		return redisTemplate.execute((RedisConnection c) -> {
			c.hSet(Util.getUtfBytes(key), Util.getUtfBytes(field), Util.getUtfBytes(value));
			return 1L;
		});
	}

	/** 删除hash,批量删除field */
	public Long hdel(String key, String... value) {
		if (Util.isEmpty(value)) {
			return -1L;
		}
		byte[][] _value = new byte[value.length][];
		for (int i = 0; i < value.length; i++) {
			_value[i] = Util.getUtfBytes(value[i]);
		}
		return redisTemplate.execute((RedisConnection c) -> {
			return c.hDel(Util.getUtfBytes(key), _value);
		});
	}

	/** 取hash,不存在则返回null */
	public String hget(String key, String field) {
		LogCore.BASE.debug("hget {} {}", key, field);
		return redisTemplate.execute((RedisConnection c) -> {
			byte[] data = c.hGet(Util.getUtfBytes(key), Util.getUtfBytes(field));
			return (null == data) ? null : Util.getUtf(data);
		});
	}

	/** 取hash,不存在则返回null */
	public String hgetByBytes(byte[] key, byte[] field) {
		return redisTemplate.execute((RedisConnection c) -> {
			byte[] data = c.hGet(key, field);
			return (null == data) ? null : Util.getUtf(data);
		});
	}

	/**
	 * 有可能是费时操作,慎用
	 */
	public Map<String, String> hgetall(String key) {
		LogCore.BASE.debug("hgetall {}", key);
		Map<String, String> _map = new HashMap<>();
		return redisTemplate.execute((RedisConnection c) -> {
			c.hGetAll(Util.getUtfBytes(key)).forEach((k, v) -> {
				_map.put(Util.getUtf(k), Util.getUtf(v));
			});
			return _map;
		});
	}

	/** 存入hash, 只有当不存在这个键值的时候才存入 */
	public Long hsetNX(String key, String field, String value) {
		LogCore.BASE.debug("hsetnx {} {} {}", key, field, value);
		return redisTemplate.execute((RedisConnection c) -> {
			c.hSetNX(Util.getUtfBytes(key), Util.getUtfBytes(field), Util.getUtfBytes(value));
			return 1L;
		});
	}

	public Long hincrby(String key, String field, long delta) {
		LogCore.BASE.debug("hincrby {} {} {}", key, field, delta);
		return redisTemplate.execute((RedisConnection c) -> {
			return c.hIncrBy(Util.getUtfBytes(key), Util.getUtfBytes(field), delta);
		});
	}
	/*** redis序列化一个对象示例 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public long save(UserVO user) {
		return redisTemplate.execute((RedisConnection c) -> {
			RedisSerializer key_slz = redisTemplate.getKeySerializer();
			RedisSerializer slz = redisTemplate.getValueSerializer();
			LogCore.BASE.info("key_slz={},slz={}", key_slz.getClass().getSimpleName(), slz.getClass().getSimpleName());
			c.set(key_slz.serialize(user.getClass().getSimpleName() + ":" + user.getId()), slz.serialize(user));
			return 1L;
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public UserVO getUserInfo(String id) {
		return (UserVO) redisTemplate.execute((RedisConnection c) -> {
			RedisSerializer key_slz = redisTemplate.getKeySerializer();
			RedisSerializer slz = redisTemplate.getValueSerializer();
			return slz.deserialize(c.get(key_slz.serialize(UserVO.class.getSimpleName() + ":" + id)));
		});
	}
}
