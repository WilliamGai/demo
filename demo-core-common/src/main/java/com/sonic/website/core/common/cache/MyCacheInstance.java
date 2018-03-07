package com.sonic.website.core.common.cache;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MyCacheInstance{
	
	private static final int CACHE_EXPIRE_DAYS_4 = 4;
	private static final int CACHE_SIZE_DEFAULT_50000 = 50000;
	private static final Map<String, MyCache<?, ?>> instances = new ConcurrentHashMap<>();
	
	public <K,V> MyCache<K, V> inst(String name){
		name = Objects.toString(name, this.getClass().getCanonicalName());
		return getInstance(name, null);
	}
	
	public <K,V> MyCache<K, V> inst(){
		return getInstance(null, null);
	}
	/**
	 * @param name
	 * @param consumer not null
	 */
	@SuppressWarnings("unchecked")
	public <K,V> MyCache<K, V> getInstance(String name, Consumer<String> consumer){
		name = Objects.toString(name, this.getClass().getCanonicalName());
		return (MyCache<K, V>) instances.computeIfAbsent(name, (k)->{
			if(null != consumer){
				consumer.accept(k);
			}
			return new MyCache<>(CACHE_SIZE_DEFAULT_50000, CACHE_EXPIRE_DAYS_4);});
	}
}
