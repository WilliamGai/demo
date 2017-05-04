package com.sincetimes.website.core.spring;


import static com.sincetimes.website.core.common.support.TimeTool.formatTime;
import static com.sincetimes.website.core.common.support.TimeTool.isSameDay;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Sys;


@Component
@EnableScheduling
public class TimeSchedule {
	private static long timeBefore =  System.currentTimeMillis();

	@Scheduled(cron = "0/3 * * * * ?")
	public void heartBeats() {
	}
	@Scheduled(cron = "* * * * * ?")
	public void sameDay(){
		long timeNow = System.currentTimeMillis();
		if(!isSameDay(timeBefore, timeNow)){
			timeBefore = timeNow;
			String statsTimeStr = formatTime(System.currentTimeMillis(),"yyyyMMdd");
			LogCore.BASE.info("new day schedule {}",statsTimeStr);
		}
	}
}
