package com.sincetimes.website.redis.jedis.excample;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.common.threadpool.ThreadTool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class ConcurrentTest {
	@Test
	public void getScoresMap() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(200);  
		poolConfig.setMaxIdle(100); 
//		poolConfig.setLifo(lifo);
//        poolConfig.setMaxWaitMillis(10);  
//        poolConfig.setTestOnBorrow(TEST_ON_BORROW);  
		@SuppressWarnings("resource")
		JedisPool pool = new JedisPool(poolConfig, "123.56.13.70", 6379, 0, "foobared", 13);
//		LinkedBlockingDeque<String> ss;
//		ss.poll(timeout, unit)
//		ss.poll(timeout, unit)
		AtomicInteger n =new AtomicInteger(10);
		String timeStr = TimeTool.formatTime(System.currentTimeMillis(), "yyyy_MM_dd_HH_mm_ss_SSS");
		ThreadTool.sleep(100);
		while(n.decrementAndGet()>0){
			new Thread(()->{
				System.out.print(".");
				try (Jedis jedis = pool.getResource()) {
					Long rst = jedis.incr(timeStr);
					LogCore.BASE.info("test={},max_active={}, watiters={}, idel={}", rst, pool.getNumActive(), pool.getNumWaiters(), pool.getNumIdle());
				} catch (Exception e) {
					LogCore.BASE.info("manager fatal err,", e);
				}
			}).start();
		}
		
		
		ThreadTool.sleep(10000);
		try (Jedis jedis = pool.getResource()) {
			Long rst = jedis.incr(timeStr);
			LogCore.BASE.info("end test={},max_active={}, watiters={}, idel={}", rst, 
					pool.getNumActive(), 
					pool.getNumWaiters(),
					pool.getNumIdle());
		} catch (Exception e) {
			LogCore.BASE.info("manager fatal err,", e);
		}
//		pool.close();
	}
}
