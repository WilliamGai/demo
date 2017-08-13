package com.sincetimes.website.app.luckydraw;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.sincetimes.website.core.common.support.TimeUtil;
public enum LuckyDrawFreshType {
	NEVER((byte) 1,	"永远不"),
	DAY((byte) 2,	"每日"),
	WEEK((byte) 3, 	"每周"),
	MONTH((byte) 4, "每月");
	
	public final byte type;
	public final String name;
	
	private final static Map<Byte, LuckyDrawFreshType> byteEnumMap = new HashMap<>();
	static{
		for(LuckyDrawFreshType t:values())
			byteEnumMap.put(t.type, t);
	}
	
	private LuckyDrawFreshType(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	public static LuckyDrawFreshType getType(byte type){
		return byteEnumMap.get(type);
	}
	/*逻辑部分**/
	public String getNowTimeTag(){
		switch (this) {
		case DAY:
			return TimeUtil.formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
		case WEEK:
			return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)+"";
		case MONTH:
			return TimeUtil.formatTime(System.currentTimeMillis(), "yyyy-MM");
		default:
			return this.name();
		}
	}
}
