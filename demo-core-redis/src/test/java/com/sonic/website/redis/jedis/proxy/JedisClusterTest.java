package com.sonic.website.redis.jedis.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import redis.clients.jedis.Jedis;
/**
 * 抓包
 * @date 2018年12月10日 下午4:03:46
 */
public class JedisClusterTest {
    /** jedis 操作字符串 */
    @Test
    public void test() {
        try (Jedis jedis = new Jedis("123.56.13.70", 6379);) {
            jedis.auth("foobared");
//            jedis.select(3);// 选择数据库
//            jedis.close();
//            Logger.getGlobal().info(jedis.select(3));
//            // 增
            Thread.sleep(5000);
            jedis.set("gao", "tia n");// 每次都会覆盖旧的key <gao,tian>
//            //jedis.append("gao", "yu e");// 追加,<gao,tianyue>
//            jedis.mset("a", "A", "b", "B");// 多个增加
//            // 删
//            Logger.getGlobal().info(jedis.get("gao")+"--"+jedis.del("gao"));
//            // 查
//            Logger.getGlobal().info(jedis.get("a"));
//            // 计数
//            jedis.set("count", "100");
//            Logger.getGlobal().info("count=" + jedis.decrBy(("count"),Long.MIN_VALUE));
//            Logger.getGlobal().info("" + jedis.incr("num"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** jedis 操作List */
    public static void testList() {
        try (Jedis jedis = new Jedis("119.29.52.126", 6377);) {
            jedis.auth("foobared");
            jedis.select(3);// 选择数据库
            Logger.getGlobal().info(jedis.select(3));
            // 删
            jedis.del("list0");
            // 增加，后来的放在第一个
            jedis.lpush("list0", "gao");
            jedis.lpush("list0", "tian");
            jedis.lpush("list0", "yue");
            // 增加，后来的放到最后一个
            jedis.del("list0");
            jedis.rpush("list0", "1");
            jedis.rpush("list0", "3");
            jedis.rpush("list0", "2");
            // 取
            List<String> all = jedis.lrange("list0", 0, -1);
            Logger.getGlobal().info("" + all);
            //只有当list的值都是数字时，才能排序，而且数据库中的原来的list顺序不变
            Logger.getGlobal().info(""+jedis.sort("list0"));
            Logger.getGlobal().info("" + jedis.lrange("list0", 0, -1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** jedis 操作map */
    public static void testMap() {
        try (Jedis jedis = new Jedis("119.29.52.126", 6377);) {
            jedis.auth("foobared");
            jedis.select(3);// 选择数据库
            Logger.getGlobal().info(jedis.select(3));
            // 存
            Map<String, String> map = new HashMap<>();
            map.put("a0", "A0");
            map.put("a1", "A1");
            map.put("a2", "A2");
            jedis.hmset("map0", map);// 多次执行，不会覆盖上一个key下的map,而是相当于对数据库中的一个map做了操作
            // jedis.del("map0");//删掉整个map
            // 删除map中某个键的值
            jedis.hdel("map0", "a2");
            // 取
            List<String> list = jedis.hmget("map0", "a0", "a1", "a2", "a3");// 如果map不存在也不会报错而是返回[null,  null, null, null]

            Logger.getGlobal().info("" + list);// [A0, A1, null, null]
            Logger.getGlobal().info("" + jedis.hmget("map0", "a0"));
            Logger.getGlobal().info("" + jedis.hexists("map0", "a0"));// 判断此map是否有这个键
            Logger.getGlobal().info("" + jedis.hlen("map0"));// key存在的个数
            Logger.getGlobal().info("" + jedis.hkeys("map0"));// 返回所有的key [a1,
                                                                // a0]
            Logger.getGlobal().info("" + jedis.hvals("map0"));// 返回所有的value [A0,
                                                                // A1]

            // 输出所有键值
            jedis.hkeys("map0").forEach(key -> {
                Logger.getGlobal().info(key + "=" + jedis.hmget("map0", key));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** jedis 操作Set */
    public static void testSet() {
        try (Jedis jedis = new Jedis("119.29.52.126", 6377);) {
            jedis.auth("foobared");
            jedis.select(3);// 选择数据库, 返回值是"OK"
            // 删
            jedis.del("set0");
            //增加
            jedis.sadd("set0", "a");
            jedis.sadd("set0", "b");
            jedis.sadd("set0", "c");
            jedis.sadd("set0", "a");
            jedis.sadd("set0", "d");
            jedis.sadd("set0", "e");
            jedis.sadd("set0", "f");
            // 删除某个
            jedis.srem("set0", "e", "f");
            // 取
            Logger.getGlobal().info("" + jedis.sismember("set0", "a"));//是否在set中
            Logger.getGlobal().info("" + jedis.scard("set0"));//所有的set的个数    4
            Logger.getGlobal().info("" + jedis.smembers("set0"));//取出所有的set    [c, d, a, b]
            Logger.getGlobal().info("" + jedis.srandmember("set0", 2));//随机取出2个值
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}