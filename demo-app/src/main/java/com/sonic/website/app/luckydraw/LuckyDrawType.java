package com.sonic.website.app.luckydraw;

import java.util.HashMap;
import java.util.Map;
/**
 * 是否互斥
 */
public enum LuckyDrawType {
    REPEAT((byte) 1,    "可以重复"),
    NO_REPEAT((byte) 2,    "不可以重复");
    
    public final byte type;
    public final String name;
    
    private final static Map<Byte, LuckyDrawType> byteEnumMap = new HashMap<>();
    static{
        for(LuckyDrawType t:values())
            byteEnumMap.put(t.type, t);
    }
    
    private LuckyDrawType(byte type, String name) {
        this.type = type;
        this.name = name;
    }
    public static LuckyDrawType getType(byte type){
        return byteEnumMap.get(type);
    }
    /*逻辑部分**/
    public String getNowTimeTag(){
        switch (this) {
        default:
            return this.name();
        }
        
    }
}
