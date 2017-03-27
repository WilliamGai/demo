package com.sincetimes.website.core.common.support;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;
/**
 * 1.8新的时间类,将Date,Time改为Instant
 */
public class TimeToolNew {
	public static final int HOUR_SECONDS = 3600;
	public static final int DAY_SECONDS = HOUR_SECONDS * 24;
	public static final int WEEK_SECONDS = DAY_SECONDS * 7;
	
	public static boolean isSameWeek(long a, long b){
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(a);
		Calendar cb = Calendar.getInstance();
		cb.setTimeInMillis(b);
		
		if(ca.get(Calendar.YEAR) != cb.get(Calendar.YEAR)){
			return false;
		}
		return ca.get(Calendar.WEEK_OF_YEAR) == cb.get(Calendar.WEEK_OF_YEAR);
	}
	/**
	 * 汉语习惯周日为一周的最后一天
	 * @param time0
	 * @param time1
	 * @return
	 */
	public static boolean isSameSinoWeek(long time0, long time1){
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(time0);
		ca.add(Calendar.DAY_OF_YEAR, -1);
		Calendar cb = Calendar.getInstance();
		cb.setTimeInMillis(time1);
		cb.add(Calendar.DAY_OF_YEAR, -1);
		if(ca.get(Calendar.YEAR) != cb.get(Calendar.YEAR)){
			return false;
		}
		return ca.get(Calendar.WEEK_OF_YEAR) == cb.get(Calendar.WEEK_OF_YEAR);
	}
	public static boolean isSameDay(long ta, long tb) {
		LocalDateTime dta = getLocalDateTime(ta);
		LocalDateTime dtb = getLocalDateTime(ta);
		return dta.isEqual(dtb);
//		return dta.toLocalDate().until(dtb.toLocalDate()).isZero();
	}
	public static String formatTime(long timestamp, String pattern) {
		LocalDateTime ld = getLocalDateTime(timestamp);
        return ld.format(DateTimeFormatter.ofPattern(pattern));
	}
	public static long parseAndGetTime(String s, String pattern) throws ParseException {
        LocalDateTime dt = getLocalDateTime(s, pattern);
		return dt.toInstant(ZoneOffset.UTC).toEpochMilli();
	}
	private static LocalDateTime getLocalDateTime(long timestamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
	}
	
	private static LocalDateTime getLocalDateTime(String s, String pattern){
        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(pattern));
	}
	
	/*j8的sdf线程安全？*/
	public static String getLocalTime(){
		DateTimeFormatter df= DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");
		return df.format(LocalDateTime.now());
	}
}
