package com.sonic.website.test;

import java.security.SecureRandom;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.sonic.website.core.common.support.LogCore;
@Component
public class TaskAsy {
	public static SecureRandom random = new SecureRandom();

	@Async
    public Future<Long> doTaskOne() throws Exception {
        LogCore.BASE.info("{},{}开始做任务一", Thread.currentThread().getName(), Thread.currentThread().getId());
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(10000));
        long end = System.currentTimeMillis();
        long interval = end - start;
        LogCore.BASE.info("{},{}完成任务一，耗时：{}毫秒", Thread.currentThread().getName(), Thread.currentThread().getId(), interval);
        return new AsyncResult<Long>(interval);
	}
	@Async
    public Future<Long> doTaskTwo() throws Exception {
        LogCore.BASE.info("{},{}开始做任务二", Thread.currentThread().getName(), Thread.currentThread().getId());
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(10000));
        long end = System.currentTimeMillis();
        long interval = end - start;
        LogCore.BASE.info("{},{}完成任务二，耗时：{}毫秒", Thread.currentThread().getName(), Thread.currentThread().getId(), interval);
        return new AsyncResult<Long>(interval);
	}
	@Async
    public Future<Long> doTaskThree() throws Exception {
        LogCore.BASE.info("{}, {}开始做任务三", Thread.currentThread().getName(), Thread.currentThread().getId());
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(10000));
        long end = System.currentTimeMillis();
        long interval = end - start;
        LogCore.BASE.info("{}，{}完成任务三，耗时：{}毫秒", Thread.currentThread().getName(), Thread.currentThread().getId(), interval);
        return new AsyncResult<Long>(interval);
	}
}
