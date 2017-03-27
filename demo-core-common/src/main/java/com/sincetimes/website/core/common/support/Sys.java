package com.sincetimes.website.core.common.support;

import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class Sys {

	public static String getAppDir(){
		return System.getProperty("user.dir");
	}
	
	public static String getOs(){
		return System.getProperty("os.name");
	}
	
	public static boolean isLinux(){
		String os = System.getProperty("os.name").toLowerCase();
		LogCore.BASE.info("os is {}", os);
		if(!Util.isEmpty(os) && os.contains("windows")){
			return false;
		}
		return true;
	}
	
	public static boolean isWin() {
		return System.getProperty("os.name").startsWith("Windows");
	}
	
	public static Properties getProperties(){
		return System.getProperties();
	}
	
	public static String getPropertiesJSONString(){
		return JSON.toJSONString(Sys.getProperties(),SerializerFeature.PrettyFormat,SerializerFeature.WriteClassName, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
	}
	
	public static String[] getPaths() {
		try {
			String pathStr = System.getProperty("java.class.path");
			if(isWin()) {
				return pathStr.split(";");
			} else {
				return pathStr.split(":");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}
}