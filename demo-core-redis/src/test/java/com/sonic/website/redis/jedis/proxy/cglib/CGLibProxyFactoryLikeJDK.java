package com.sonic.website.redis.jedis.proxy.cglib;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import com.sonic.website.redis.jedis.proxy.jdk.ProxyFactory;
import com.sonic.website.redis.jedis.proxy.jdk.Student;
import com.sonic.website.redis.jedis.proxy.jdk.StudentInterface;

public class CGLibProxyFactoryLikeJDK implements InvocationHandler{
	private Object obj;
	@SuppressWarnings("unchecked")
	public <T> T createProxy(T o){
		this.obj = o;
		//可以传入Class而不是接口
		return (T)Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[]{obj.getClass()}, this);
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
//	@Override
//	public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
//		// TODO Auto-generated method stub
//		return null;
//	}
	public static void main(String[] args) {
		Student student= new Student();
		ProxyFactory pf = new ProxyFactory();
		StudentInterface st = pf.createProxy(student);//仍然需要返回接口
		System.out.println("calss is"+st.getClass());
		st.sayHello("william");
		
	}

}
