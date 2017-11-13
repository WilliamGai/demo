package com.sincetimes.website.redis.jedis;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.threadpool.ThreadUtil;
import com.sincetimes.website.redis.StepMillisWatch;
/***
 * 
 * @author bao
 * 定时执行triger,如果连续到达阈值则触发invoker
 */
public class MonitorThread extends Thread {
	private static final int COUNT_THREHOLD_DEFAUT_5 = 5;//默认阈值
	private static final int INTERVAL_MILLIS_DEFAULT_1000 = 1000;//默认检测时间间隔
	
	private final int countThreadHold;
	private final int intervalMilliseconds;
	private final AtomicInteger COUNT = new AtomicInteger();
	private Supplier<Boolean> triger;
	private Runnable invoker;
	
	private final StepMillisWatch smw = new StepMillisWatch();
	/***
	 * @param supplier 检测的表达式,无参数，返回booolean
	 * @param runnable 到达阈值的时候,触发的表达式
	 */
	public MonitorThread(Supplier<Boolean> supplier, Runnable invoker) {
		this(supplier, invoker, INTERVAL_MILLIS_DEFAULT_1000, COUNT_THREHOLD_DEFAUT_5);
	}
	public MonitorThread(Supplier<Boolean> supplier, Runnable invoker, int heartBeatInterval, int countThrehold) {
		Objects.requireNonNull(supplier);
		Objects.requireNonNull(invoker);
		this.setName(this.getClass().getSimpleName());//给线程命令方便观察
		this.triger = supplier;
		this.invoker = invoker;
		this.countThreadHold = countThrehold;
		this.intervalMilliseconds = heartBeatInterval;	
	}
	@Override
	public void run() {
		for (;;) {
			ThreadUtil.sleep(intervalMilliseconds);
			boolean isConnected = triger.get();
			if (isConnected) {
				COUNT.set(0);
				smw.reset();
				continue;
			}
			if (COUNT.incrementAndGet() >= countThreadHold) {
				invoker.run();
				smw.interval();
				LogCore.BASE.error("isConnected={}, interval time={}", isConnected, smw.prettyPrint());
				COUNT.set(0);
				smw.reset();
			}
		}
	}

	public static void startMonitor(Supplier<Boolean> supplier, Runnable invoker) {
		new MonitorThread(supplier, invoker).start();
	}
	public static void startMonitor(Supplier<Boolean> supplier, Runnable invoker, int heartBeatInterval, int countThrehold) {
		new MonitorThread(supplier, invoker, heartBeatInterval, countThrehold).start();
	}
	
}
