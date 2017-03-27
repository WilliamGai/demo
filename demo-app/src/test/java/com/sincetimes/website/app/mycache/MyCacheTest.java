package com.sincetimes.website.app.mycache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MyCacheTest {

	@Test
	public void test() throws InterruptedException {
		Cache<Object,Object> cache = CacheBuilder.newBuilder().recordStats().maximumSize(10).expireAfterWrite(4, TimeUnit.SECONDS)
				.build();
		for (int i = 0; i < 10; i++) {
			cache.put(i+"", i+"a");
		}
		for (int i = 0; i < 30; i++) {
			try {
				cache.get(i+"",()->null);
			} catch (Exception e) {
				System.out.print("..");
//				e.printStackTrace();
			}
		}
		for (int i = 0; i < 20; i++) {
			cache.put(i, i+"b");
		}
		System.out.println();
//		cache.p
		cache.cleanUp();
//		cache.invalidateAll();
//		cache.asMap().clear();
		System.out.println(cache.asMap().toString()+"cache.size="+cache.size());
		System.out.println(cache.stats().hitRate());
		System.err.println(cache.stats());
			Thread.sleep(2000);
			System.err.println(cache.getIfPresent(1));
			System.out.println(cache.asMap().toString()+"size="+cache.size());
			Thread.sleep(2000);
//		cache.cleanUp();
		System.out.println(cache.asMap().toString()+"size="+cache.asMap().size());
		Map<String, String> map = new ConcurrentHashMap<>();
		map.put("a", "A");
		map.put("b", "b");
		map.remove("b");
		System.out.println("map.size="+map.size());
		Thread.sleep(2000);
		System.err.println(cache.stats());

	}
}
