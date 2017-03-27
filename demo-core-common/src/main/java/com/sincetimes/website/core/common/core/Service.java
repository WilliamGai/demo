package com.sincetimes.website.core.common.core;

import static org.quartz.JobKey.jobKey;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;

import com.sincetimes.website.core.common.core.scheduler.ScheduleJob;
import com.sincetimes.website.core.common.core.scheduler.ScheduleMethod;
import com.sincetimes.website.core.common.core.scheduler.ScheduleTask;
import com.sincetimes.website.core.common.support.LogCore;

/**
 * 服务接口
 * 继承本接口的对象可以通过Proxy被远程调用
 */
public abstract class Service {
	protected Port port;												//所属Port
	
	private final List<Method> scheMethods = new ArrayList<>(); 		//缓存本类的调度队列函数
	private ConcurrentLinkedQueue<ScheduleTask> schedulerList = new ConcurrentLinkedQueue<>(); // 待处理的时间调度队列
	
	/**
	 * 获取服务ID
	 * @return
	 */
	public abstract Object getId() ;
	
	/**
	 * 构造函数
	 * @param port
	 */
	public Service(Port port) {
		this.port = port;
	}
	
	/**
	 * 启动
	 */
	public void startup() {
		//初始化该port上的schedule
		scheduleInit();
	}
	
	/**
	 * 心跳
	 */
	public final void pulse() {
		pulseSchedulers();
		//子类可覆盖心跳
		pulseOverride();
	}
	
	/**
	 * 子类可覆盖心跳
	 */
	public void pulseOverride() {
		
	}
	
	/**
	 * 执行时间调度队列
	 */
	private void pulseSchedulers() {
		while (!schedulerList.isEmpty()) {
			try {
				ScheduleTask task = schedulerList.poll();
				task.execute();
			} catch (Exception e) {
				// 不做任何处理 仅仅抛出异常
				// 避免因为一个任务的出错 造成后续的任务无法继续执行 需要等到下一个心跳
				LogCore.CORE.error("", e);
			}
		}
	}
	
	/**
	 * 获取RPC函数调用
	 * @param funcKey
	 * @return
	 */
	public <T> T getMethodFunction(int funcKey) {
		try {
			//获取对应的代理类
			if(methodFunctionProxy == null) {
				Class<?> cls = Class.forName(getClass().getName()+"Proxy");
				Constructor<?> c = cls.getDeclaredConstructor();
				c.setAccessible(true);
				methodFunctionProxy = (ProxyBase) c.newInstance();
			}
			
			//通过代理类 获取函数引用
			return methodFunctionProxy.getMethodFunction(this, funcKey);
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
	private ProxyBase methodFunctionProxy;		//缓存服务对应的代理类
	
	/**
	 * 初始话该port上的schedule
	 */
	private void scheduleInit() {
		Method[] methods = this.getClass().getMethods();
		for(Method method : methods) {
			if(!method.isAnnotationPresent(ScheduleMethod.class)) continue;
			
			scheMethods.add(method);
		}
		
		for(final Method method : scheMethods) {
			ScheduleMethod anno = method.getAnnotation(ScheduleMethod.class);
			String[] cronStrs = anno.value();
			
			for(String cronStr : cronStrs) {
				scheduleCron(new ScheduleTask("servId", getId()) {
					@Override
					public String getJobGroup() {
						return getId().toString();
					}
					
					@Override
					public void execute() {
						try {
							Port port = Port.getCurrent();
							Service serv = port.getService(getParam().get("servId"));
							method.invoke(serv);
						} catch (Exception e) {
							throw new SysException(e, "service初始化schedule错误，servicId:{}", getParam().get("servId"));
						}
					}
				}, cronStr);
			}
		}
	}
	
	/**
	 * 延迟delay毫秒之后执行任务
	 * 
	 * @param task
	 * @param delay
	 */
	public void scheduleOnce(ScheduleTask task, long delay) {
		task.triggerAt = port.getTimeCurrent() + delay;

		// 定义trigger
		SimpleScheduleBuilder sche = SimpleScheduleBuilder.repeatSecondlyForever();
		// 循环次数 设置为0 代表不多余循环只执行一次
		sche.withRepeatCount(0);

		// 添加任务
		scheduleUtils(task, sche, delay);
	}

	/**
	 * 从指定的delay毫秒延迟之后，开始以重复的速率每period毫秒执行
	 * 
	 * @param task
	 * @param delay
	 * @param period
	 */
	public void schedulePeriod(ScheduleTask task, long delay, long period) {
		task.triggerAt = System.currentTimeMillis() + delay;
		task.triggerPeriod = period;

		// 定义schedule
		SimpleScheduleBuilder sche = SimpleScheduleBuilder.repeatSecondlyForever();
		// 执行间隔
		sche.withIntervalInMilliseconds(period);

		// 添加任务
		scheduleUtils(task, sche, delay);
	}
	
	/**
	 * 从指定的delay毫秒延迟之后，开始以重复的速率每period毫秒执行，执行count次
	 * @param task
	 * @param delay
	 * @param period
	 * @param count
	 */
	public void scheduleRepeatForTotalCount(ScheduleTask task, long delay, long period, int count) {
		task.triggerAt = System.currentTimeMillis() + delay;
		task.triggerPeriod = period;

		// 定义schedule
		SimpleScheduleBuilder sche = SimpleScheduleBuilder.repeatSecondlyForTotalCount(count);
		// 执行间隔
		sche.withIntervalInMilliseconds(period);

		// 添加任务
		scheduleUtils(task, sche, delay);
	}

	/**
	 * 添加时间任务队列 支持cron格式
	 * 
	 * @param task
	 */
	public void scheduleCron(ScheduleTask task, String cronStr) {
		task.triggerCronStr = cronStr;

		// 定义schedule
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronStr);

		// 添加任务
		scheduleUtils(task, scheduleBuilder, 0);
	}

	/**
	 * 时间调度任务
	 * @param task
	 * @param scheduleBuilder
	 * @param delay
	 */
	private void scheduleUtils(ScheduleTask task,
			ScheduleBuilder<?> scheduleBuilder, long delay) {
		try {
			// 开始执行时间
			Date startAt = new Date();
			if (delay > 0) {
				startAt = new Date(System.currentTimeMillis() + delay);
			}

			// 定义时间调度的job内容
			JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(task.jobKey).build();
			jobDetail.getJobDataMap().put("task", task);
			jobDetail.getJobDataMap().put("scheduler", schedulerList);

			// 创建最终trigger
			Trigger trigger = TriggerBuilder.newTrigger().startAt(startAt).withSchedule(scheduleBuilder).build();

			// 绑定job和trigger
			port.scheduler.scheduleJob(jobDetail, trigger);

			// 设置任务信息
			task.state = ScheduleTask.STATE_WAITING;
			task.sched = port.scheduler;
			task.jobKey = jobDetail.getKey();
			task.trigger = trigger;
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
	
	/**
	 * 删除所在port上默认jobGroup中指定jobName的job
	 * @param jobName
	 * @throws SchedulerException
	 */
	protected void deleteSchedulerJob(String jobName) throws SchedulerException {
		port.scheduler.deleteJob(jobKey(jobName, null));
	}
	
	/**
	 * 删除所在port上的指定jobName和jobGroupName的job
	 * @param jobName
	 * @param jobGroupName
	 * @throws SchedulerException
	 */
	protected void deleteSchedulerJob(String jobName, String jobGroupName) throws SchedulerException {
		port.scheduler.deleteJob(jobKey(jobName, jobGroupName));
	}
	
	/**
	 * 通过group名删除所在port上的所有group内的scheduler
	 * @param jobGroupName
	 * @throws SchedulerException
	 */
	protected void deleteSchedulerJobsByGroup(String jobGroupName) throws SchedulerException {
		for (String group : port.scheduler.getJobGroupNames()) {
			if(!group.equals(jobGroupName)) continue;
			for(JobKey jobKey : port.scheduler.getJobKeys(GroupMatcher.jobGroupEndsWith(group))) {
				port.scheduler.deleteJob(jobKey);
			}
		}
	}
}