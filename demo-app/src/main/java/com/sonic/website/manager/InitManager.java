package com.sonic.website.manager;

import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.redis.jedis.MonitorThread;
import com.sonic.website.redis.jedis.spring.JedisManagerBase;

public class InitManager extends JedisManagerBase {
	
	public static InitManager inst() {
		return ManagerBase.inst(InitManager.class);
	}
	
	@Override
	public void init() {
		MonitorThread.startMonitor(this::checkJedisConnect, this::alrm);
	}
	public boolean checkJedisConnect(){
//		long time = System.currentTimeMillis();
		boolean yes = template().isconnected();
//		LogCore.BASE.info("check redis server err！ {},  result is {}", System.currentTimeMillis()-time, yes);
		return yes;
	}
	public void alrm(){
		LogCore.BASE.info("connect redis server err！ {}", Thread.currentThread().getName());
	}
}
