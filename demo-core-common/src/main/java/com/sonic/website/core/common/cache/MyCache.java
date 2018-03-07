package com.sonic.website.core.common.cache;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.sonic.website.core.common.support.LogCore;

/**
 * 单机缓存
 * 清空缓存要使用cache.invalidateAll();
 */
public class MyCache<K, V> {
	private Cache<K, V> cache = null;

	/**
	 * @param expire_time
	 *            过期时间,单位为天
	 */
	public MyCache(int max_size, int expire_time) {
		cache = CacheBuilder.newBuilder().recordStats().maximumSize(max_size)
				.expireAfterWrite(expire_time, TimeUnit.DAYS).build();
	}

	/** 如果缓存和数据库中没有或者存的为空值则返回 false **/
	public boolean containsKey(K key, Function<K, V> fnc) {
		return null != getValue(key, fnc);
	}

	/***
	 * 执行的查找函数,此函数可以是从持久化或者网路取得
	 * 允许返回空
	 * @return nullable
	 */
	public V getValue(K key, Function<K, V> fnc_get) {
		try {
			return cache.get(key, () -> fnc_get.apply(key));
		} catch (Exception e) {
			LogCore.BASE.warn("cache get value null, need persistance in other ways,key= {}",key);
			return null;
		}
	}

	public V putValue(K key, V value, BiConsumer<K, V> func_put) {
		System.err.println("invoke cache"+key+","+value);
		func_put.accept(key, value);
		return cache.asMap().put(key, value);
	}
	public V removeValue(K key, Consumer<K> func_del){
		func_del.accept(key);
		return cache.asMap().remove(key);
	}
	public long size() {
		return cache.size();
	}

	public CacheStats getStats() {
		return cache.stats();
	}
	
	public  ConcurrentMap<K, V>  asMap() {
		return cache.asMap();
	}
}
