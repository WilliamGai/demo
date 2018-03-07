package com.sonic.website.redis.jedis.proxy.jdk;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.RandomAccess;

public class ArrayListProxyTest {
    public static void main(String[] args) throws Exception {
        //ArrayList类的代理
        System.out.println("-----创建实例----");
        ArrayList list = new ArrayList();
        Object proxy = Proxy.newProxyInstance(
                ArrayList.class.getClassLoader(),
                new Class[]{ List.class},
                new MyInvocationHandler2(list)
        );
        Object proxy2 = Proxy.isProxyClass(proxy.getClass());
        //Proxy.getProxyClass()
        System.out.println("------PPP "+proxy2);
        System.out.println("------PPP "+proxy.getClass().getSuperclass());
        System.out.println("------PPP "+proxy.getClass());

	        List list2 = (List) proxy;
        System.out.println(".add(\"盖怡超\"); ");
        ((List) proxy).add("盖怡超");

        System.out.print(".remove(\"盖怡超\"); ");
        ((Collection) proxy).remove("盖怡超");

        System.out.print(".add(\"盖保宁\"); ");
        ((List) proxy).add("盖保宁");

        System.out.print(".add(1,\"盖保垒\"); ");
        ((List) proxy).add(1, "盖保垒");

        boolean bool = false;

        System.out.print(".contains(\"盖怡超\"); " + ((List) proxy).contains("盖怡超"));

        System.out.print(".indexOf(\"盖保垒\"); " + ((List) proxy).indexOf("盖保垒"));

        System.out.print(".isEmpty()" + ((List) proxy).isEmpty());

        System.out.println();

        System.out.print(".set(1, \"hellokity\")");
        ((List) proxy).set(1, "hellokity");

        System.out.print(".toArray()");
        Object[] cols = ((List) proxy).toArray();

        System.out.print(".size()");
        ((Collection) proxy).size();

        System.out.println();
        System.out.println("代理的名称" + proxy.getClass().getName());


    }
}

/**
 * 类加载器,并可以计算每个方法运行的时间
 **/
class MyInvocationHandler2 implements InvocationHandler {
    Object target;
    public MyInvocationHandler2(Object target){
        this.target = target;
    }
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object retVal = method.invoke(target, args);
        long endTime = System.currentTimeMillis();
        System.out.println("运行时间为--" + (endTime - beginTime));
        return retVal;
    }
}

