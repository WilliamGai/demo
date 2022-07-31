package com.sonic.website.app;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import com.sonic.website.core.common.support.ReflectUtil;

import redis.clients.jedis.JedisPoolConfig;

/** 获取某个类的所有属性 markdown格式 */
public class TestClassAttrs2Markdown {
    public static void main(String args[]) {
        JedisPoolConfig c = new JedisPoolConfig();
        Predicate<Field> filter = (Field field)->{
            return !Modifier.isStatic(field.getModifiers());
        };
        Map<String, Object> fs = ReflectUtil.getFieldsInherit(c, JedisPoolConfig.class, filter);
        int mdf = ~Modifier.STATIC;
        System.err.println(Long.toBinaryString(25));
        System.err.println(Long.toBinaryString(mdf));
        System.err.println(mdf);
        System.err.println(mdf&25);
        
        List<String> result = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        fs.forEach((k, v) -> {
            result.add(k + "|" + v);
        });
        result.sort(Comparator.comparing(String::toString));
        result.forEach(a -> {
            System.out.println(count.incrementAndGet() + "|" + a);
        });
        //        System.out.println(JSON.toJSONString(fs, true));
    }
    
}
