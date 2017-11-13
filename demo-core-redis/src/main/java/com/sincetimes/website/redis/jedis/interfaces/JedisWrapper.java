package com.sincetimes.website.redis.jedis.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.redis.jedis.JedisPoolTemplate;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
/**
 * 方便与spring解耦
 */
public interface JedisWrapper{
	
	JedisPoolTemplate template();
	
	/**
	 * 选择database,思考用什么进行前后的选择数据库
	 */
	default Jedis selectDB(Jedis jedis){
//		int order_value_a = Util.toNullDefalut(this.getClass().getAnnotation(RedisDataBaseSelect.class), RedisDataBaseSelect::value, Integer.MAX_VALUE/2);
		return jedis;
	}
	
	/**可以覆盖此方*/
	default String makeKey(String key) {
		return this.getClass().getCanonicalName().concat(":").concat(key);
	}
	/**扩展*/
	default String makeKey(String scope, String key) {
		return this.getClass().getCanonicalName().concat(":").concat(scope).concat(":").concat(key);
	}
	/** Time complexity: O(1) */
	default Boolean exist(String name) {
		return template().excute(jedis -> jedis.exists(makeKey(name)));
	}
	/*** 查询 **/
	default String get(String name) {
		return template().excute(jedis -> jedis.get(makeKey(name)));
	}

	/*** 设值 **/
	default String set(String name, String value) {
		return template().excute(jedis -> jedis.set(makeKey(name), value));
	}
	
	default String getSet(String name, String value) {
		return template().excute(jedis -> jedis.getSet(makeKey(name), value));
	}
	
	/*** 有过期时间的设值,如果没有则设值,过期时间单位为秒 */
	default String setEX_NX_Exipire(String name, String value, long seconds) {
		return template().excute(jedis -> jedis.set(makeKey(name), value,"NX", "EX", seconds));
	}
	
	default Long ttl(String name) {
		return template().excute(jedis -> jedis.ttl(makeKey(name)));
	}
	
	/** 提倡使用set方法设置ex,nx来代替setex,setnx*/
	@Deprecated
	default long setNx(String name, String value){
		return template().excute(jedis -> jedis.setnx(makeKey(name), value));
	}
	/*** 写入或增加 O(1)返回增加后的值 */
	default Long incr(String name) {
		return template().excute(jedis-> jedis.incr(makeKey(name)));
	}

	/*** 写入或增加 */
	default void incrBy(String name, long value) {
		template().excute(jedis -> jedis.incrBy(makeKey(name), value));
	}

	/** hash */
	/**
	 * Time complexity: O(1)
	 * @return 新建hash字段为1,覆盖已有的返回0
	 */
	default long hset(String name, String field, String value) {
		return template().excute(jedis -> jedis.hset(makeKey(name), field, value));
	}
	/**
	 * 
	 * @param name
	 * @param hash 不可以为空
	 * @return ok或抛异常
	 * @see {@link Jedis#hmset}
	 */
	default String hmset(String name, Map<String, String> hash) {
		return template().excute(jedis -> jedis.hmset(makeKey(name), hash));
	}
	/** 获取所有的key */
	default Set<String> hkeys(String name) {
		return template().excute(jedis -> jedis.hkeys(makeKey(name)));
	}
	
	/** 获取所有的value */
	default List<String> hvals(String name) {
		return template().excute(jedis -> jedis.hvals(makeKey(name)));
	}
	
	/** hsetnx之前不存在才设置,成功返回1,反之0, */
	default long hsetnx(String name, String field, String value) {
		return template().excute(jedis -> jedis.hsetnx(makeKey(name), field, value));
	}
	
	/**时间复杂度 O(1) */
	default Long hget(String name, String field, long value) {
		return template().excute(jedis -> jedis.hincrBy(makeKey(name), field, value));

	}
	/**时间复杂度 O(1) */
	default String hget(String name, String field) {
		return template().excute(jedis -> jedis.hget(makeKey(name), field));

	}
	/**时间复杂度O(N) (with N being the number of fields)<br>当要获取同一个hash多个字段时请用此方法*/
	default List<String> hmget(String name, String... fields) {
		return template().excute(jedis -> jedis.hmget(makeKey(name), fields));

	}
	/**时间复杂度: O(N), where N is the total number of entries*/
	default Map<String, String> hgetall(String name) {
		return template().excute(jedis -> jedis.hgetAll(makeKey(name)));
	}
	
	default long hdel(String name, String field){
		return template().excute(jedis -> jedis.hdel(makeKey(name), field));
	}
	/**value*/
	default Long hincrBy(String name, String field, final long value){
		return template().excute(jedis -> jedis.hincrBy(makeKey(name), field, value));
	}
	/** Time complexity: O(1) */
	default Boolean hexists(String name, String field){
		return template().excute(jedis -> jedis.hexists(makeKey(name), field));
	}
	
	/** sort set */
	default long zadd(String name, double score, String member) {
		return template().excute(jedis -> jedis.zadd(makeKey(name), score, member));
	}
	
	/**
	 * zrevrange 复杂度是O(logN +M)。M是返回参数个数 
	 * @return nullable
	 */
	default Set<String>  zrange(String name,final long start, final long end) {
		return template().excute(jedis -> jedis.zrange(makeKey(name), start, end));
		
	}
	default Long zrem(String name,String member) {
		return template().excute(jedis -> jedis.zrem(makeKey(name), member));
	}
	
	default int zincrby(String key, double score, String member){
		return template().excute(jedis -> jedis.zincrby(makeKey(key), score, member)).intValue();
	}
	
	/** O(1)*/
	default Double zscore(String key, String member){
		return template().excute(jedis -> jedis.zscore(makeKey(key), member));
	}
	/** Time complexity O(1) <br>获取sort set的大小*/
	default Long zcard(String key){
		return template().excute(jedis -> jedis.zcard(makeKey(key)));
	}
	
	/** O(logN)*/
	default Long zrevrank(String key, String member){
		return template().excute(jedis -> jedis.zrevrank(makeKey(key), member));
	}
	
	default Set<Tuple> zRevRangeWithScores(final String key, final long start, final long end){
		return template().excute(jedis -> jedis.zrevrangeWithScores(makeKey(key), start, end));
	}
	
	/** list */
	default String rpoplpush(String srckey, String dstkey) {
		return template().excute(jedis -> jedis.rpoplpush(makeKey(srckey), makeKey(dstkey)));// rPopLpush
	}

	default long rpush(String key, String... strings) {
		return template().excute(jedis -> jedis.rpush(makeKey(key), strings));

	}
	/** 时间复杂度为O(1)*/
	default long llen(String key) {
		return template().excute(jedis -> jedis.llen(makeKey(key)));
	}

	default long del(String key) {
		return template().excute(jedis -> jedis.del(makeKey(key)));
	}

	default List<String> lrange(String key, long start, long end) {
		return template().excute(jedis -> jedis.lrange(makeKey(key), start, end));
	}

	/**
	 * zset计数器 记录例如页面访问记录等信息
	 * 
	 * @param scope
	 * @param key
	 */
	default <T> int incrCountByDb(String scope, T key) {
		return template().excute(jedis -> jedis.zincrby(makeKey(scope), 1, String.valueOf(key))).intValue();
	}

	default <T> int getCountByDb(String scope, T key) {
		Double score = template().excute(jedis -> jedis.zscore(makeKey(scope), String.valueOf(key)));
		return null == score ? 0 : score.intValue();
	}

	/* 自定义存储 begin  */
	default void setBiValue(String key, String name, String value) {
		zadd("zset", 0, key);
		hset(key, "name", name);
		hset(key, "value", value);
	}

	default String[] getBiValue(String key) {
		return new String[] { hget(key, "name"), hget(key, "value") };
	}
	
	default void delBiValue(String key) {
		zrem("zset", key);
		del(key);
	}
	
	/*长度为3的数组{key, name, value}*/
	default List<String[]> getAllBiValues() {
		Set<String> keys = zrange("zset", 0, -1);
		if (Util.isEmpty(keys)) {
			return null;
		}
		return keys.stream()
				.map((key) -> new String[] { key, hget(key, "name"), hget(key, "value") })
				.collect(Collectors.toList());
	}
	/*  自定义存储end */
	
	
}
