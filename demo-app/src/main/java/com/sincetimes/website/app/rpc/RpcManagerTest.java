package com.sincetimes.website.app.rpc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.threadpool.LimitedThreadPool;

/**
 * rpc示例
 * 因为RpcFramework.export(service, port);是阻塞的因此需要另起线程
 */
public class RpcManagerTest extends ManagerBase {
	public Map<String, Object> REMOTE_SERVICE_MAP = new HashMap<>();
	
	public static RpcManagerTest inst() {
		return ManagerBase.inst(RpcManagerTest.class);
	}
	//引用服务
	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> clazz, String ip, int port){
		try {
			return (T) REMOTE_SERVICE_MAP.computeIfAbsent(clazz.getName(), (k)->RpcFramework.refer(HelloRpcService.class, ip, port));
		} catch (Exception e) {
			LogCore.RPC.error("get remote service failed", e);
			return null;
		}
	}
	//暴露服务,使用线程池，不允许显式用Thead线程如果使用请设置线程名称
	@Override
	public void init() {
		HelloRpcService service = new HelloRpcServiceImpl(); 
		LimitedThreadPool.execute(()->export(service, 10086));
	    LogCore.RPC.info("rpc ready");
	}
	public void export(final Object service, int port){
		try {
			RpcFramework.export(service, 10086);//发布服务
			LogCore.BASE.info("{} rpc exported", service.getClass().getSimpleName());	
		} catch (Exception e) {
			LogCore.RPC.error("rpcManager init", e);
		}
	}
}
