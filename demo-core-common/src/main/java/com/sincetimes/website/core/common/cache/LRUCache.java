package com.sincetimes.website.core.common.cache;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * LinkedHashMap为我们提供了removeEldestEntry(Map.Entry<K,V> eldest)来支持缓存实现
 * afterNodeInsertion(boolean evict)来移除最老的
 * afterNodeAccess(Node<K,V> e)来标记不活跃
 * @see LinkedHashMap
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {


	/** 2017年3月10日12:47:26 */
	private static final long serialVersionUID = 3755413498196039249L;

	private static final float DEFAULT_LOAD_FACTOR = 0.75f;

	private static final int DEFAULT_MAX_CAPACITY = 1000;

	private volatile int maxCapacity;

	private final Lock lock = new ReentrantLock();

    public LRUCache() {
    	this(DEFAULT_MAX_CAPACITY);
    }

    public LRUCache(int maxCapacity) {
        super(16, DEFAULT_LOAD_FACTOR, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            lock.lock();
            return super.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        try {
            lock.lock();
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        try {
            lock.lock();
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        try {
            lock.lock();
            return super.remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        try {
            lock.lock();
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            super.clear();
        } finally {
            lock.unlock();
        }
    }

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

}