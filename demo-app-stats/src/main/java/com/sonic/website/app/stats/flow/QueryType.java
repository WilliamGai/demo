package com.sonic.website.app.stats.flow;

import java.util.HashMap;
import java.util.Map;
public enum QueryType{
    DEFAULT((byte) 1,    "流水"),
    GROUP((byte) 2,    "汇总");
//    DOWNLOAD((byte) 3, "下载");

    public final byte id;
    public final String name;
    
    private final static Map<Byte, QueryType> byteEnumMap = new HashMap<>();
    static{
        for(QueryType t:values())
            byteEnumMap.put(t.id, t);
    }
    
    private QueryType(byte id, String desc) {
        this.id = id;
        this.name = desc;
    }
    public static QueryType getType(byte id){
        return byteEnumMap.getOrDefault(id, DEFAULT);
    }
    
}
