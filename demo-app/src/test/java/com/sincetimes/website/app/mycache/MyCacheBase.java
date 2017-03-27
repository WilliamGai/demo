package com.sincetimes.website.app.mycache;

import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.cache.Cache;

/**
 * cache的特点,根据LRU除数据,只要设置好size,cache会自动剔除不常用的<K,V>
 * ①cache.invalidateAll();和cache.asMap().clear();是清除缓存，最后缓存的size为0
 * ②某个键值过期后,cache.size并不减少,而是执行cache.cleanUp();才会让缓存的size实质的减少
 */
public abstract class MyCacheBase<K, V> {
	protected Cache<K, V> cache = null;

	protected BiConsumer<K, V> fnc_consumer_insert_or_update = null;

	protected Function<K, V> fnc_function_select = null;

	public abstract boolean containsKey(K key);

	public abstract boolean containsKeyInMap(K key);

	public abstract V getValue(K key);

	public abstract V putValue(K key, V value);

	public abstract V getValueByCall(K key, Function<K, V> fnc);

	public abstract V putValueWithByCall(K key, V value, BiConsumer<K, V> consumer_insert_or_update);

	public ConcurrentMap<K, V> asMap() {
		return cache.asMap();
	}

	public void clear() {
		cache.invalidateAll();// 不要使用cache.cleanUp();
	}
	public void cleanUp() {
		cache.cleanUp();
	}
	public long size() {
		return cache.size();
	}

}
