package com.sonic.website.core.common.extension.threadpool.limited;

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
 * 此线程池一直增长，直到上限，增长后不收缩。
 * 
 * @author <a href="https://github.com/WilliamGai/WilliamGai.github.io">gbn</a>
 */
public class LimitedThreadPool implements ThreadPool {

    public Executor getExecutor() {
        String name = ThreadPoolConstants.DEFAULT_THREAD_NAME;
        int cores = ThreadPoolConstants.DEFAULT_CORE_THREADS;
        int threads = ThreadPoolConstants.DEFAULT_THREADS;
        int queues = ThreadPoolConstants.DEFAULT_QUEUES;
        return new ThreadPoolExecutor(cores, threads, Long.MAX_VALUE, TimeUnit.MILLISECONDS, 
        		queues == 0 ? new SynchronousQueue<Runnable>() : 
        			(queues < 0 ? new LinkedBlockingQueue<Runnable>() 
        					: new LinkedBlockingQueue<Runnable>(queues)),
        		new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }

}
