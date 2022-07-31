package com.sonic.website.core.common.extension.threadqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author g
 * @date 2018年4月20日 下午2:33:22 
 */
public class LockThread extends Thread{
    
    private final static Logger logger = LoggerFactory.getLogger(LockThread.class);

    public BlockingQueue<Call> queues = new LinkedBlockingQueue<>(100);
    
    public LockThread(String name, Boolean isDaemo) {
        super();
        this.setName(name);
        this.setDaemon(isDaemo);
    }

    @Override
    public void run() {
        while(true){
            try {
                queues.take().execute();
                System.out.println("after size---"+queues.size());
//                Thread.sleep(10L);
            } catch (InterruptedException e) {
                logger.error("thread{},{} take queue, err", this.getName(), this.getId(), e);
            }
        }
    };
    
    public void execute(Call call){
        try {
            //queues.put(run); //blocking put InterruptedException
            queues.offer(call);
        } catch (Exception e) {
            logger.error("thread{},{} put queue err", this.getName(), this.getId(), e);
        }
    }
    
    @FunctionalInterface
    public static interface Call {
        public abstract void execute();
    }
}
