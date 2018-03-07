package com.sonic.website.app.security;

import java.util.HashMap;
import java.util.Map;

public enum GenderType {
	MALE((byte)1, "male", "/mg/images/boy.png"),
	FEMALE((byte)2, "female", "/mg/images/girl.png");
	
	public final byte type;
	public final String name;
	public final String defaultPic;//默认头像
	
	private final static Map<Byte, GenderType> byteEnumMap = new HashMap<>();
	static{
		for(GenderType t:values())
			byteEnumMap.put(t.type, t);
	}
	
	private GenderType(byte type, String name, String defaultPic){
		this.type = type;
		this.name = name;
		this.defaultPic = defaultPic;
	}
	
	public static GenderType getType(byte type){
		return byteEnumMap.get(type);
	}
}
