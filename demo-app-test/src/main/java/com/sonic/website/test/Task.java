package com.sonic.website.test;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.sonic.website.core.common.support.LogCore;
@Component
public class Task {
    public static SecureRandom random = new SecureRandom();

    public long doTaskOne(){
        LogCore.BASE.info("{},{}开始做任务一", Thread.currentThread().getName(), Thread.currentThread().getId());
        long start = System.currentTimeMillis();
        sleep(random.nextInt(10000));
        long end = System.currentTimeMillis();
        long interval = end - start; 
        LogCore.BASE.info("{},{}完成任务一，耗时：{}毫秒", Thread.currentThread().getName(), Thread.currentThread().getId(), interval);
        return interval;
    }
    public long doTaskTwo(){
        LogCore.BASE.info("{},{}开始做任务二", Thread.currentThread().getName(), Thread.currentThread().getId());
        long start = System.currentTimeMillis();
        sleep(random.nextInt(10000));
        long end = System.currentTimeMillis();
        long interval = end - start;
        LogCore.BASE.info("{},{}完成任务二，耗时：{}毫秒", Thread.currentThread().getName(), Thread.currentThread().getId(), interval);
        return interval;
    }
    public long doTaskThree(){
        LogCore.BASE.info("{}, {}开始做任务三", Thread.currentThread().getName(), Thread.currentThread().getId());
        long start = System.currentTimeMillis();
        sleep(random.nextInt(10000));
        long end = System.currentTimeMillis();
        long interval = end - start;
        LogCore.BASE.info("{}，{}完成任务三，耗时：{}毫秒", Thread.currentThread().getName(), Thread.currentThread().getId(), interval);
        return interval;
    }
    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
