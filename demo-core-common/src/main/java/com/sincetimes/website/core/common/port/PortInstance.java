package com.sincetimes.website.core.common.port;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sincetimes.website.core.common.core.Port;
/**
 * Port为进程提供一个不被销毁的线程,并用队列保证任务串行执行。
 * <p>
 * Port所在的子线程创建后不会销毁。可以将各种服务挂在在port下,port内部维护了一个或多个队列,保证任务在子线程中串行执行。
 * <br>
 * 通过port来处理服务/RPC/API/HTTP,共享资源的数据类型不必线程安全。
 * @see ThreadPoolTool
 */
public class PortInstance{
	private static final Map<String, Port> instances = new ConcurrentHashMap<>();
	
	public static Port inst(String name){
		return instances.computeIfAbsent(name, (k)->new Port(k).startup());
	}
	
	public static Port inst(){
		return inst(PortInstance.class.getCanonicalName());
	}
}
