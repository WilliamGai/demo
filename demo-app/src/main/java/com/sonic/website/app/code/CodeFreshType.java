package com.sonic.website.app.code;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.sonic.website.core.common.support.TimeUtil;
/**
 * 序列化的时候为一个简单的字符串,"NEVER",DAY"等<br>
 * 反序列化的情形,既可以从NEVER这样的字符串反序列化出枚举,也会从0,1这样的整形作为下标反序列化出对应的枚举
 */
public enum CodeFreshType {
	NEVER((byte) 1,	"永远不"),
	DAY((byte) 2,	"每日"),
	WEEK((byte) 3, 	"每周"),
	MONTH((byte) 4, "每月");
	
	public final byte type;
	public final String name;
	
	private final static Map<Byte, CodeFreshType> byteEnumMap = new HashMap<>();
	static{
		for(CodeFreshType t:values())
			byteEnumMap.put(t.type, t);
	}
	
	private CodeFreshType(byte type, String name) {
		this.type = type;
		this.name = name;
	}
	public static CodeFreshType getType(byte type){
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
