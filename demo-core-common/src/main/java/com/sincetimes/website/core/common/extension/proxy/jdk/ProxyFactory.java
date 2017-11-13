package com.sincetimes.website.core.common.extension.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ProxyFactory implements InvocationHandler{
	private Object obj;
	public <T> T createProxy(T o){
		this.obj = o;
		return (T)Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), this);
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object o = null;
		o = method.invoke(obj, args);
		System.out.println("method name="+method.getName());
		System.out.println("proxy="+proxy.getClass().getCanonicalName());
		System.out.println("args="+ Arrays.toString(args));
		return o;
	}

}
