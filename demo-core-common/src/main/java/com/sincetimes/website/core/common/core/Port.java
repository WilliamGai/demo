package com.sincetimes.website.core.common.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import com.sincetimes.website.core.common.core.interfaces.IPortPulse;
import com.sincetimes.website.core.common.core.interfaces.IThreadCase;
import com.sincetimes.website.core.common.support.LogCore;
/**
 * 如果一个集群的节点是一个进程,那么Port代表此进程下的线程
 * spring中用spring的定时器即可,如果脱离spring请参考core包的定时器scheduler
 */
public class Port implements IThreadCase{

	//当前线程的Port实例
	private final static ThreadLocal<Port> portCurrent = new ThreadLocal<>();
	//线程管理类
	private final ThreadHandler thread;
	//当前线程开始时间(毫秒)
	private long timeCurrent = 0; 
	//待处理执行队列
	private final ConcurrentLinkedQueue<IPortPulse> pulseQueues = new ConcurrentLinkedQueue<>();
	//下属服务
	private final Map<Object, Service> services = new ConcurrentHashMap<>();
	//心跳时间秒表,现在只有在debug模式下开启
	private final StepWatch pulseStepWatch = new StepWatch(LogCore.CORE.isDebugEnabled());
	//Port名称
	private final String portId;
	
	public static Logger log  = LogCore.CORE;
	//任务队列调度器
	public Scheduler scheduler; 
	
	public Port(String portId){
		this.portId = portId;
		this.thread = new ThreadHandler(this);
	}
	public String getId() {
		return portId;
	}
	/** 开始 */
	public Port startup() {
		// 初始化quartz相关环境
		try {
			this.scheduler = new StdSchedulerFactory().getScheduler();
			this.scheduler.start();
		} catch (Exception e) {
			throw new SysException(e);
		}
		// 启动独立线程
		this.thread.setName(toString());
		this.thread.startup();
		log.info("启动Port={}", this);
		return this;
	}
	/** 结束 */
	public void stop() {
		if (thread == null)
			return;
		// 停止独立线程
		this.thread.cleanup();
		log.info("结束Port={}", this);
	}
	/** 添加延后队列任务 */
	public void addQueue(IPortPulse msg) {
		pulseQueues.add(msg);
	}
	/** 心跳中执行队列任务 */
	private void pulseQueue() {
		while (!pulseQueues.isEmpty()) {
			try {
				IPortPulse msg = pulseQueues.poll();
				msg.execute(this);
			} catch (Exception e) {
				// 不做任何处理 仅仅抛出异常
				// 避免因为一个任务的出错 造成后续的任务无法继续执行 需要等到下一个心跳
				LogCore.CORE.error("", e);
			}
		}
	}

	/** 心跳操作 */
	public final void pulse() {
		//记录下心跳开始时的时间戳 供之后的操作来统一时间
		timeCurrent = System.currentTimeMillis();
		int countQueue = -1;
		countQueue = pulseQueues.size();
		//计时开始
		StepWatch sw = pulseStepWatch;
		sw.step();
		
		//执行等待任务队列
		pulseQueue();
		sw.logTime("queue");
		
		//记录下时间比较长的心跳操作
		long timeInterval = System.currentTimeMillis() - timeCurrent;
		if(timeInterval >= 100) {
			LogCore.CORE.warn("本次心跳操作总时间较长，达到了{}毫秒。"
							+ "portName={}, countQueue={}, time={}", timeInterval, this.portId,
							countQueue, sw.getLog(false));
		}
	}
	
	/** 获取当前线程的Port实例  */
	@SuppressWarnings("unchecked")
	public static <T extends Port> T getCurrent() {
		return (T) portCurrent.get();
	}
	
	/**  获取系统时间 */
	public static long getTime() {
		return getCurrent().getTimeCurrent();
	}
	/** 当前线程开始时间(毫秒)  */
	public long getTimeCurrent() {
		return timeCurrent;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append("portId=").append(getId()).toString();
	}
	
	@Override
	public void caseStart() {
		portCurrent.set(this);
	}
	@Override
	public void caseStop() {
		portCurrent.set(null);
	}
	@Override
	public void caseRunOnce() {
		pulse();
	}
	
	//
	@SuppressWarnings("unchecked")
	public <T extends Service> T getService(Object id) {
		return (T) services.get(id);
	}
}
