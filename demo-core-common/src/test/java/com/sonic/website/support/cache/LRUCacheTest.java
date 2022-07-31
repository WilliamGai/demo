package com.sonic.website.support.cache;

import org.junit.Test;

import com.sonic.website.core.common.cache.LRUCache;

public class LRUCacheTest {
    @Test
    public void test(){
        System.out.println("test lru");
        LRUCache<String, Object> cache = new LRUCache<>(2);
        cache.put("a", 0);
        cache.put("a1", 1);
        cache.get("a1");
        cache.put("a2", 2);
        cache.get("a1");
        cache.put("a3", 3);
        System.out.println("test lru"+cache);
    }

}
