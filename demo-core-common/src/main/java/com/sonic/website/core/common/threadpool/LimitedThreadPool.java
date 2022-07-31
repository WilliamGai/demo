package com.sonic.website.core.common.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sonic.website.core.common.extension.AbortPolicyWithReport;
import com.sonic.website.core.common.extension.NamedThreadFactory;
import com.sonic.website.core.common.extension.threadpool.cached.CachedThreadPool;
import com.sonic.website.core.common.extension.threadpool.fixed.FixedThreadPool;

/**
 * 线程资源必须通过线程池提供，不允许在应用中自行显式创建线程
 * 
 * @See {@link LimitedThreadPool}
 * @see {@link FixedThreadPool}
 * @see {@link CachedThreadPool}
 * @See {@link LimitedThreadPool}
 */
public class LimitedThreadPool {
    static String name = "demo thread pool";
    static int cores = 0;
    static int threads = 200;
    static int queues = 0;

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(cores, threads, Long.MAX_VALUE,
                TimeUnit.MILLISECONDS,
                queues == 0 ? new SynchronousQueue<Runnable>()
                            : (queues < 0 ? new LinkedBlockingQueue<Runnable>()
                                        : new LinkedBlockingQueue<Runnable>(queues)),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));

    public static void execute(Runnable command) {
        THREAD_POOL_EXECUTOR.execute(command);
    }
}
