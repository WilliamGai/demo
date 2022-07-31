package com.sonic.website.core.common.core.scheduler;

import org.quartz.JobKey;
import org.quartz.utils.Key;

public abstract class ScheduleTaskExtend extends ScheduleTask{

    private String jobName = null;
    private String JobGroup = null;
    
    @Override
    public String getJobName() {
        return jobName;
    }
    
    /**
     * 获取group名
     * @return
     */
    @Override
    public String getJobGroup() {
        return JobGroup;
    }
    
    public ScheduleTaskExtend() {
        this(null, null);
    }
    
    
    public ScheduleTaskExtend(String jobName) {
        this(jobName, null);
    }
    
    public ScheduleTaskExtend(String jobName, String JobGroup) {
        super();
        this.jobName = jobName;
        this.JobGroup = JobGroup;
        
        if(getJobName() == null) {
            jobKey = new JobKey(Key.createUniqueName(getJobGroup()), getJobGroup());
        } else {
            jobKey = JobKey.jobKey(getJobName(), getJobGroup());
        }
    }
    

}
