package com.sincetimes.website.app.stats;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;


public interface StatsService {
	public String get(String name);
	public <T> void set(String name, T value);
	public boolean isExist(String name);
	public long incr(String name);
	public long incrBy(String name, long value);
	public long addRecordList(String listName, String sn);
	public Map<String, Long> getRecordList2Map(String listName, Predicate<? super Record> filter);
	public Long zadd(String key, double score, String value);
	public Double zincrby(String key, double score, String value);
	public Set<String> zRange(String key);//获取zset使用zrange
	public long hIncrBy(String key, String filed, long delta);
	public <T> List<T> hGetAll2List(String key, BiFunction<byte[], byte[], T> f);
	public Double zscore(String key, String member);
}
