package com.sincetimes.website;


import static com.sincetimes.website.core.common.support.TimeTool.formatTime;
import static com.sincetimes.website.core.common.support.TimeTool.isSameDay;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sincetimes.website.app.stats.flow.DataStatsManager;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Sys;


@Component
@EnableScheduling
public class StatsTimeSchedule {
	private static long timeBefore =  System.currentTimeMillis();
	/*"0 0/10 * * * ?"  "0 0/30 * * * ?"*/
	@Scheduled(cron = "0 0/30 * * * ?")
	public void tenminutes()  {
		LogCore.BASE.info("tenminutes schedule");
		LogCore.RECORD.info("free {}", Sys.sysInfosPrettyJson());
		DataStatsManager.inst().initData();
	}
	@Scheduled(cron = "0/3 0/30 * * * ?")
	public void heartBeats() {
	}
	@Scheduled(cron = "* * * * * ?")
	public void seconds(){
		
		long timeNow = System.currentTimeMillis();
		if(!isSameDay(timeBefore, timeNow)){
			timeBefore = timeNow;
			String statsTimeStr = formatTime(System.currentTimeMillis(),"yyyyMMdd");
			LogCore.BASE.info("new day schedule {}",statsTimeStr);
		}
	}
}
