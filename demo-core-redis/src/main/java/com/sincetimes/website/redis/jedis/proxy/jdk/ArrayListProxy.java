package com.sincetimes.website.redis.jedis.proxy.jdk;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.RandomAccess;

public class ArrayListProxy {
	 public static void main(String[] args) throws Exception
	    {
	       //ArrayList类的代理
	        System.out.println("-----创建实例----");
	        Object proxy =  Proxy.newProxyInstance(
	               ArrayList.class.getClassLoader(),
	               new Class[]{Collection.class, Cloneable.class,Iterable.class,RandomAccess.class,List.class},
	               new MyInvocationHandler()
	               );
//	        ArrayList list = (ArrayList) proxy;
	        System.out.print(".add(\"盖怡超\"); ");
	        ((List) proxy).add("盖怡超");
	       
	        System.out.print(".remove(\"盖怡超\"); ");
	        ((Collection)proxy).remove("盖怡超");
	       
	        System.out.print(".add(\"盖保宁\"); ");
	        ((List) proxy).add("盖保宁");
	       
	        System.out.print(".add(1,\"盖保垒\"); ");
	        ((List) proxy).add(1,"盖保垒");
	       
	        boolean bool = false;
	       
	        System.out.print(".contains(\"盖怡超\"); "+((List) proxy).contains("盖怡超"));
	       
	        System.out.print(".indexOf(\"盖保垒\"); "+((List) proxy).indexOf("盖保垒"));
	       
	        System.out.print(".isEmpty()"+((List) proxy).isEmpty());
	       
	        System.out.println();
	       
	        System.out.print(".set(1, \"hellokity\")");
	        ((List) proxy).set(1, "hellokity");
	       
	        System.out.print(".toArray()");
	        Object[] cols = ((List) proxy).toArray();
	       
	        System.out.print(".size()");
	        ((Collection) proxy).size();
	       
	        System.out.println();
	        System.out.println("代理的名称"+proxy.getClass().getName());
	       
	       
	       
	        ///////////////////////-----------------///////////////////
	        //得到代理类的字节码
	        Class clazzProx = proxy.getClass();
	       System.out.println("--------打印构造函数列表-------");
	       //得到构造函数
	        Constructor[] constructors = clazzProx.getConstructors();
	        for(Constructor constructor :constructors)
	        {
	           String name = constructor.getName();
	           StringBuilder sBuilder = new StringBuilder(name);
	           sBuilder.append('(');
	           //返回参数数组
	           Class[] clazzParams = constructor.getParameterTypes();
	           for(Class clazzParam : clazzParams)
	           {
	               sBuilder.append(clazzParam.getName()).append(',');
	           }
	           if( clazzParams != null&& clazzParams.length !=0 )
	               sBuilder.deleteCharAt(sBuilder.length()-1);
	           sBuilder.append(')');
	           System.out.println(sBuilder);
	           Collection proxy1 = (Collection)constructor.newInstance(new MyInvocationHandler());
	           proxy1.add("heeo");
	           proxy1.add("heAA");
	        }
	       
	        System.out.println("--------打印函数列表-------");
	        //代理得到方法
	       Method[] methods = clazzProx.getMethods();
	        for(Method method :methods)
	        {
	           String name = method.getName();
	           StringBuilder sBuilder = new StringBuilder(name);
	           sBuilder.append('(');
	           Class[] clazzParams = method.getParameterTypes();
	           for(Class clazzParam : clazzParams)
	           {
	               sBuilder.append(clazzParam.getName()).append(',');
	           }
	           if( clazzParams != null&& clazzParams.length !=0 )
	               sBuilder.deleteCharAt(sBuilder.length()-1);
	           sBuilder.append(')');
	           System.out.println(sBuilder);
	        }
	    }
}
	 /**
	  * 类加载器,并可以计算每个方法运行的时间
	  **/
	 class MyInvocationHandler implements InvocationHandler
	 {
	     //ArrayList也是集合
	     ArrayList target = new ArrayList();
	     public Object invoke(Object proxy, Method method, Object[] args)
	            throws Throwable {
	        //ArrayList target = new ArrayList();
	        long beginTime = System.currentTimeMillis();
	        Object retVal = method.invoke(target, args);
	        long endTime = System.currentTimeMillis();
	        System.out.println("运行时间为"+(endTime-beginTime));
	        return retVal; 
	     }
	 }

