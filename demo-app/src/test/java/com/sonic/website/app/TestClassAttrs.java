package com.sonic.website.app;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonic.website.app.security.vo.RoleVO;
import com.sonic.website.core.common.support.ClassUtil;
import com.sonic.website.core.common.support.ReflectUtil;
import com.sonic.website.core.common.support.Util;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisClusterConnectionHandler;
import redis.clients.jedis.JedisClusterInfoCache;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;

/**
 * 获取某个类的所有属性
 * 
 * @author bao
 * @date 2018年9月8日 下午5:50:55
 */
public class TestClassAttrs {
    public static void main(String args[]) throws JsonProcessingException {
        HostAndPort hp = new HostAndPort("123.56.13.70", 6300);
        Set<HostAndPort> set = new HashSet<>();
        set.add(hp);
        JedisCluster c = new JedisCluster(set);
        
        
        Map<String, Object> fs = ReflectUtil.getFieldsInherit(c, JedisCluster.class);
//        ObjectMapper mapper = new ObjectMapper();  
//         String json = mapper.writeValueAsString(fs);
        System.out.println(fs);
        
        
        //2
        JedisSlotBasedConnectionHandler innerHandler = ClassUtil.getFieldValue(c, "connectionHandler");
        Map<String, Object> fs2 = ReflectUtil.getFieldsInherit(innerHandler, JedisSlotBasedConnectionHandler.class);
        System.out.println(fs2);
        JedisClusterInfoCache cache = ClassUtil.getFieldValue(innerHandler,JedisClusterConnectionHandler.class, "cache");
        Map<String, Object> fs3 = ReflectUtil.getFieldsInherit(cache, JedisClusterInfoCache.class);
        System.out.println(fs3);
        System.out.println(fs3.size());
        for (Map.Entry<String, Object> entry : fs3.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + "--" + value);
        }
        /    est2
        test2();
    }
    public static void test2() {
        JedisPoolConfig c = new JedisPoolConfig();
        Map<String, Object> fs = ReflectUtil.getFieldsInherit(c, JedisPoolConfig.class);
        c.setMax
        System.out.println(JSON.toJSONString(fs, true));
    }
}
