package com.sonic.website.core.common.extension;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonic.website.core.common.support.Util;


/**
 * Abort Policy.
 * Log warn info when abort.
 */
public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {
    
    protected static final Logger logger = LoggerFactory.getLogger(AbortPolicyWithReport.class);
    
    private final String threadName;
    
    
    public AbortPolicyWithReport(String threadName) {
        this.threadName = threadName;
    }
    
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    	String msg = Util.format("Thread pool is EXHAUSTED!" 
    			+ " Thread Name: {}, Pool Size: {} (active: {}, core: {}, max: {}, largest: {}),"
                + " Task: {} (completed: {})," 
    			+ " Executor status:(isShutdown:{}, isTerminated:{}, isTerminating:{})", 
    			threadName, 
    			e.getPoolSize(), 
    			e.getActiveCount(), 
    			e.getCorePoolSize(), 
    			e.getMaximumPoolSize(), 
    			e.getLargestPoolSize(),
                e.getTaskCount(), 
                e.getCompletedTaskCount(), 
                e.isShutdown(), 
                e.isTerminated(), 
                e.isTerminating());
        logger.warn(msg);
        throw new RejectedExecutionException(msg);
    }

}