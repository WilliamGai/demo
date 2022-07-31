package com.sonic.website.redis.jedis.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;


/**
 * 控制redis域的接口
 * 思考:只所有不选代理
 */
public interface JedisSupport{
    
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
