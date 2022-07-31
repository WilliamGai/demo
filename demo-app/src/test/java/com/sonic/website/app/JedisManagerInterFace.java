package com.sonic.website.app;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import com.sonic.website.redis.jedis.excample.JedisServiceDemo;

/**
 * 控制redis域的接口
 */
public interface JedisManagerInterFace{

    public abstract JedisServiceDemo getJedisService();
    public default String makeKey(String key) {
        return this.getClass().getCanonicalName().concat(":").concat(key);
    }

    /*** 查询 **/
    public default String get(String name) {
        return getJedisService().get(makeKey(name));
    }

    /*** 设值 **/
    public default String set(String name, String value) {
        return getJedisService().set(makeKey(name), value);
    }
    public default void test(){
        Proxy.newProxyInstance(
                   ArrayList.class.getClassLoader(),
                   new Class[]{Collection.class, Cloneable.class,Iterable.class,RandomAccess.class,List.class},
                   new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // TODO Auto-generated method stub
                        return null;
                    }
                });

    }
    
}
