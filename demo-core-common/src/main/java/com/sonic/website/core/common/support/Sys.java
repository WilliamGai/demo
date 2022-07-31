package com.sonic.website.core.common.support;

import java.util.HashMap;
import java.util.Map;
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
    /** debug free  */
    public static String getJVMStatus() {
        long maxM = Runtime.getRuntime().maxMemory();
        long totalM = Runtime.getRuntime().totalMemory();
        long freeM = Runtime.getRuntime().freeMemory();
        long usedM = totalM - freeM;
        return Util.format("maxM={}, totalM={}, freeM={}, usedM={}", Util.getM(maxM), Util.getM(totalM),  Util.getM(freeM), Util.getM(usedM));
    }
    public static Map<String, Object> getSysInfos() {
        Map<String, Object> map = new HashMap<>();
        long maxM = Runtime.getRuntime().maxMemory();
        long totalM = Runtime.getRuntime().totalMemory();
        long freeM = Runtime.getRuntime().freeMemory();
        long usedM = totalM - freeM;
        map.put("path", System.getProperty("user.dir"));
        map.put("maxM", Util.getM(maxM));
        map.put("totalM", Util.getM(totalM));
        map.put("freeM", Util.getM(freeM));
        map.put("usedM", Util.getM(usedM));
        return map;
    }
    public static String sysInfosPrettyJson() {
        return Util.prettyJsonStr(getSysInfos());
    }
}