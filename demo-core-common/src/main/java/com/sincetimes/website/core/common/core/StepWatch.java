package com.sincetimes.website.core.common.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.time.StopWatch;

/**
 * 统计步骤耗时<br/>
 * 本类不是线程安全的<br/>
 * 这个类是设计用来监控性能的，专为统计多步骤每阶段消耗准备，
 * 如果只需要计算一个总的开始和结束时间，不适合用这个类。<br/>
 * 
 * enable可配合log4j的isDebugEnabled等函数使用。
 */
public class StepWatch {
	//内部用SW来实现业务
	private StopWatch watch;
	//是否启动
	private boolean enable = false;
	//步骤耗时
	private final Map<String, Long> times = new LinkedHashMap<>();
	
	/**
	 * 构造函数
	 * 默认不启动
	 */
	public StepWatch() {
		this(false);
	}
	
	/**
	 * 构造函数
	 * @param enable
	 */
	public StepWatch(boolean enable) {
		if(enable) {
			enable();
		}
	}
	
	/**
	 * 启动
	 */
	public StepWatch enable() {
		//已开启 忽略
		if(enable) return this;
		
		//设置时钟
		watch = new StopWatch();
		watch.start();
		watch.split();
		
		//开启
		enable = true;
		
		return this;
	}
	
	/**
	 * 关闭
	 */
	public StepWatch disable() {
		//已关闭 忽略
		if(!enable) return this;
		//只要没多线程问题 一般不会出现这个情况 先验证一下吧
		if(watch == null) return this;
		
		//关闭时钟
		watch.stop();
		watch.reset();
		watch = null;
		
		//开启
		enable = false;
		
		return this;
	}
	
	/**
	 * 设置本步骤开始时间
	 */
	public StepWatch step() {
		//未开启 直接忽略
		if(!enable) return this;
				
		watch.split();
		
		return this;
	}
	
	/**
	 * 获取本步骤的时间
	 * 当enable为false时，返回时间为-1
	 * @return
	 */
	public long getTime() {
		//未开启 就返回一个默认值
		if(!enable) return -1;
		
		//本步骤耗时
		long time = watch.getTime() - watch.getSplitTime();
		
		//设置下步骤开始时间
		step();
		
		return time;
	}
	
	/**
	 * 获取并记录本步骤的日志信息
	 * @param stepKey
	 * @return
	 */
	public StepWatch logTime(String stepKey) {
		//未开启 直接忽略
		if(!enable) return this;
		
		//记录步骤信息
		times.put(stepKey, getTime());
		
		return this;
	}
	
	/**
	 * 获取并记录本步骤的日志信息
	 * 本操作会清空步骤信息
	 * @return
	 */
	public String getLog() {
		return getLog(false);
	}
	
	/**
	 * 获取并记录本步骤的日志信息
	 * 本操作会清空步骤信息
	 * @param onlyLtZero true=只输出时间大于0的信息 false=输出全部
	 * @return
	 */
	public String getLog(boolean onlyLtZero) {
		//未开启 直接忽略
		if(!enable) return "";
		
		//步骤信息
		StringBuilder sb = new StringBuilder("{");
		for(Entry<String, Long> e : times.entrySet()) {
			//某些时候只输出有时间消耗的步骤
			if(onlyLtZero && e.getValue() <= 0) continue;
			//多余一项是增加连接符
			if(sb.length() > 1) sb.append(", ");
			//设置内容
			sb.append(e.getKey()).append("=").append(e.getValue());
		}
		sb.append("}");

		//清空记录步骤信息
		times.clear();
		
		return sb.toString();
	}
}