package com.sonic.website.app.stats.flow;

import java.util.HashMap;
import java.util.Map;
public enum ColumnItemType{
    DEFAULT_STRING((byte) 1,    "默认(字符串)"),
    TIME_MILLI_SECODS((byte) 2, "毫秒时间"),
    TIME_SECONDS((byte) 3, "秒时间");

    public final byte id;
    public final String name;
    
    private final static Map<Byte, ColumnItemType> byteEnumMap = new HashMap<>();
    static{
        for(ColumnItemType t:values())
            byteEnumMap.put(t.id, t);
    }
    
    private ColumnItemType(byte id, String desc) {
        this.id = id;
        this.name = desc;
    }
    public static ColumnItemType getType(byte id){
        return byteEnumMap.get(id);
    }
}
