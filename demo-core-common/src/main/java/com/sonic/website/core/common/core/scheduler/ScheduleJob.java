package com.sonic.website.core.common.core.scheduler;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScheduleJob implements Job {
    private ScheduleTask task;
    private ConcurrentLinkedQueue<ScheduleTask> scheduler = new ConcurrentLinkedQueue<ScheduleTask>();
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        task.state = ScheduleTask.STATE_INQUEUE;
        
        //到触发时间后放入时间调度队列
        scheduler.add(task);
    }

    public ScheduleTask getTask() {
        return task;
    }

    public void setTask(ScheduleTask task) {
        this.task = task;
    }

    public ConcurrentLinkedQueue<ScheduleTask> getScheduler() {
        return scheduler;
    }

    public void setScheduler(ConcurrentLinkedQueue<ScheduleTask> scheduler) {
        this.scheduler = scheduler;
    }
}
