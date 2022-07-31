package com.sonic.website.app.stats.flow;

import java.util.HashMap;
import java.util.Map;
public enum GroupColumnItemType{
    GROUP_DEFAULT((byte) 1,    "正常显式"),
    MAX((byte) 2, "最大值"),
    MIN((byte) 3,    "最小值"),
    SUM((byte) 4,    "求和"),
    COUNT((byte) 5,    "计数");

    public final byte id;
    public final String name;
    
    private final static Map<Byte, GroupColumnItemType> byteEnumMap = new HashMap<>();
    static{
        for(GroupColumnItemType t:values())
            byteEnumMap.put(t.id, t);
    }
    
    private GroupColumnItemType(byte id, String desc) {
        this.id = id;
        this.name = desc;
    }
    public static GroupColumnItemType getType(byte id){
        return byteEnumMap.get(id);
    }
    
}
