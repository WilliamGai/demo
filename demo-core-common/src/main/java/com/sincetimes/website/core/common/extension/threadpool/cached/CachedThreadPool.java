package com.sincetimes.website.core.common.extension.threadpool.cached;


import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sincetimes.website.core.common.extension.AbortPolicyWithReport;
import com.sincetimes.website.core.common.extension.NamedThreadFactory;
import com.sincetimes.website.core.common.extension.threadpool.ThreadPool;
import com.sincetimes.website.core.common.extension.threadpool.ThreadPoolConstants;

/**
 * 此线程池可伸缩，线程空闲一分钟后回收，新请求重新创建线程，来源于：<code>Executors.newCachedThreadPool()</code>
 * @see java.util.concurrent.Executors#newCachedThreadPool()
 */
public class CachedThreadPool implements ThreadPool {

    public Executor getExecutor() {
        String name = "";
        int cores = ThreadPoolConstants.DEFAULT_CORE_THREADS;//0
        int threads = Integer.MAX_VALUE;
        int queues = ThreadPoolConstants.DEFAULT_QUEUES;//0
        int alive = ThreadPoolConstants.DEFAULT_ALIVE;//6000
        return new ThreadPoolExecutor(cores, threads, alive, TimeUnit.MILLISECONDS, 
        		queues == 0 ? new SynchronousQueue<Runnable>() : 
        			(queues < 0 ? new LinkedBlockingQueue<Runnable>() 
        					: new LinkedBlockingQueue<Runnable>(queues)),
        		new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }

}