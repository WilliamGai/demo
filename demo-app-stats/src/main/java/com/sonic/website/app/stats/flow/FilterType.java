package com.sonic.website.app.stats.flow;

import java.util.HashMap;
import java.util.Map;
public enum FilterType{
    STRING_EQUAL((byte) 1,    "字符串相等"),
    TIME_BEFORE((byte) 2, "时间小于"),
    TIME_AFTER((byte) 3,    "时间大于");

    public final byte id;
    public final String name;
    
    private final static Map<Byte, FilterType> byteEnumMap = new HashMap<>();
    static{
        for(FilterType t:values())
            byteEnumMap.put(t.id, t);
    }
    
    private FilterType(byte id, String desc) {
        this.id = id;
        this.name = desc;
    }
    public static FilterType getType(byte id){
        return byteEnumMap.get(id);
    }
    
}
