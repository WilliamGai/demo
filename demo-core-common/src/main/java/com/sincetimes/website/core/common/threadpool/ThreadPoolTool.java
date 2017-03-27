package com.sincetimes.website.core.common.threadpool;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sincetimes.website.core.common.extension.threadpool.cached.CachedThreadPool;
import com.sincetimes.website.core.common.extension.threadpool.fixed.FixedThreadPool;
/**
 * 线程资源必须通过线程池提供，不允许在应用中自行显式创建线程
 * @See {@link ThreadPoolTool}
 * @see {@link FixedThreadPool}
 * @see {@link CachedThreadPool}
 * @See {@link LimitedThreadPool}
 */
public class ThreadPoolTool {
	public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());
	
	public static void execute(Runnable command){
		THREAD_POOL_EXECUTOR.execute(command);
	}
}
