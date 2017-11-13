package com.sincetimes.website.redis.jedis.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.sincetimes.website.core.common.support.LogCore;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisProxy{

	public static JedisHandle createProxy(String host, int port, int timeout, String psw, int database) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		LogCore.BASE.info("JedisPool init,host={},port={},timeout={},psw={},database={}", host, port, timeout, psw, database);
		JedisPool pool = new JedisPool(poolConfig, host, port, timeout, psw, database);
		return (JedisHandle) Proxy.newProxyInstance(JedisProxy.class.getClassLoader(), new Class[] {JedisHandle.class},
		            new JedisInvocationHandler(pool));
	}

	// public static void main(String args[]) {
	// System.out.println("hei");
	// LogCore.BASE.info("getInterfaces: {}", Arrays.toString(Jedis.class.getInterfaces()));
	// LogCore.BASE.info("getAnnotatedInterfaces: {}", Arrays.toString(Jedis.class.getAnnotatedInterfaces()));
	// LogCore.BASE.info("getInterfaces: {}", Arrays.toString(Jedis.class.getGenericInterfaces()));
	// Jedis jedis;
	// }

	private static class JedisInvocationHandler implements InvocationHandler {
		private JedisPool pool;

		public JedisInvocationHandler(JedisPool pool) {
			this.pool = pool;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try (Jedis jedis = pool.getResource()) {
				return method.invoke(jedis, args);
			} catch (Exception e) {
				LogCore.BASE.error("jedisTemplate excute err 可能是redis关了或者网络不可用，请排查", e);// 断网的情况,恢复网络后正常
				return null;
			}
		}

	}

}
