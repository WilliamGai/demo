package com.sincetimes.website.redis.jedis;

import com.sincetimes.website.redis.LogCore;
import com.sincetimes.website.redis.jedis.funciton.JedisCallBack;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * JedisPoolTemplate
 * <br>
 * 1.try-with-resources
 * <br>
 * 2.redis连接池
 * 关闭资源的时候仅仅调用Jedis.close就可以了。
 * <br>
 * @author BAO
 */
public class JedisPoolTemplate {

	private JedisPool pool;
//	private Lock LOCK = new ReentrantLock();//jedis线程不安全
	/**
	 * @param timeout
	 * 此参数表示，连接池建立的时候连接redis的时候，如果超过多时间没有连接成功则返回失败的异常，
	 * 设置为0则不启用此规则，单位应该是毫秒，设置为10的时候报错java.net.SocketTimeoutException:
	 * connect timed out
	 */
	public JedisPoolTemplate(String host, int port, int timeout, String psw, int database) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		LogCore.BASE.info("JedisPool init,host={},port={},timeout={},psw={},database={}", host, port, timeout, psw, database);
		pool = new JedisPool(poolConfig, host, port, timeout, psw, database);
	}

	public <T> T excute(JedisCallBack<T> action){
		try (Jedis jedis = pool.getResource()){
			return action.doInRedis(jedis);
		} catch (Exception e) {
			LogCore.BASE.error("jedisTemplate excute err 可能是redis关了或者网络不可用，请排查", e);//断网的情况,恢复网络后正常
			return null;
		}
	}
	/**redis连接是否可用*/
	public boolean isconnected(){
		try (Jedis jedis = pool.getResource()){
			return jedis.isConnected();
		} catch (Exception e) {
			return false;//断网的情况,恢复网络后正常
		}
	}
	
	
	
	
	public String test(){
		try (Jedis jedis = pool.getResource()){
			return jedis.get("a");
		} catch (Exception e) {
			LogCore.BASE.error("jedisTemplate excute err 可能是redis关了或者网络不可用，请排查", e);//断网的情况,恢复网络后正常
			return null;
		}
	}
}