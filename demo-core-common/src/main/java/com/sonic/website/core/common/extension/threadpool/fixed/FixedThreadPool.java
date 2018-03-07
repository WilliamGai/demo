package com.sonic.website.core.common.extension.threadpool.fixed;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sonic.website.core.common.extension.AbortPolicyWithReport;
import com.sonic.website.core.common.extension.NamedThreadFactory;
import com.sonic.website.core.common.extension.threadpool.ThreadPool;
import com.sonic.website.core.common.extension.threadpool.ThreadPoolConstants;


/**
 * 此线程池启动时即创建固定大小的线程数，不做任何伸缩，来源于：<code>Executors.newFixedThreadPool()</code>
 * 
 * @see java.util.concurrent.Executors#newFixedThreadPool(int)
 * @author william.liangf
 */
public class FixedThreadPool implements ThreadPool {

    public Executor getExecutor() {
        String name = ThreadPoolConstants.DEFAULT_THREAD_NAME;
        int threads = ThreadPoolConstants.DEFAULT_THREADS;
        int queues = ThreadPoolConstants.DEFAULT_QUEUES;
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS, 
        		queues == 0 ? new SynchronousQueue<Runnable>() : 
        			(queues < 0 ? new LinkedBlockingQueue<Runnable>() 
        					: new LinkedBlockingQueue<Runnable>(queues)),
        		new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }

}