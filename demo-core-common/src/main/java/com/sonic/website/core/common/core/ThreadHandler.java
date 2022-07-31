package com.sonic.website.core.common.core;

import org.apache.commons.lang3.time.StopWatch;

import com.sonic.website.core.common.core.interfaces.IThreadCase;
import com.sonic.website.core.common.support.LogCore;
/**
 * 每毫秒执行一次
 */
public class ThreadHandler extends Thread {
    private final IThreadCase member;        //被调用的对象
    private volatile boolean running;            //是否在运行中
    private volatile boolean wating = false;            //是否在等待中
    
    //执行计时
    private final StopWatch watch;
    //心跳间隔(毫秒)
    private final int interval = 20;
        
    /**
     * 构造函数
     * @param master
     */
    public ThreadHandler(IThreadCase master) {
        this.member = master;
        this.watch = new StopWatch();
    }
    
    /**
     * 开始
     * @return
     */
    public void startup() {
        //已在运行中 忽略新的运行请求
        if(running) return;
        
        //设置为运行状态
        running = true;
        
        //启动线程
        start();
    }
    
    /**
     * 结束
     * @return
     */
    public void cleanup() {
        //非运行中 忽略结束请求
        if(!running) return;
        
        //设置为停止状态
        running = false;
    }
    
    @Override
    public void run() {
        //开始前的准备
        member.caseStart();
        //运行中就不断轮询
        while(running) {
            try {
                // 如果等待中，就不处理
                if(wating) {
                    Thread.sleep(1);
                    continue;
                }
                
                //执行业务
                watch.start();
                member.caseRunOnce();
                watch.stop();
                
                //记录执行时间
                long timeRunning = watch.getTime();
                
                //尽量保证心跳帧率 每次执行后至少休息1毫秒 避免极端情况下连续执行占用过多系统资源
                if(timeRunning < interval) {
                    Thread.sleep(interval - timeRunning);
                } else {
                    Thread.sleep(1);
                }
            } catch (Exception e) {
                //不做任何处理 仅仅抛出异常
                //避免因为一个任务的出错 造成后续的任务无法继续执行
                LogCore.CORE.error("", e);
                System.exit(10);
            } finally {
                watch.reset();
            }
        }
        //结束时的清理
        member.caseStop();
    }
    
    /**
     * 暂停当前线程
     */
    public void pauseT() {
        //忽略同状态
        if(wating) return;
            
        wating = true;
    }
    
    /**
     * 恢复当前线程
     */
    public void resumeT() {
        //忽略同状态
        if(!wating) return;
            
        wating = false;
    }
}
