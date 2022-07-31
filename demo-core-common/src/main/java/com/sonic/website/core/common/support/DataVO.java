package com.sonic.website.core.common.support;

/** ::new */
public class DataVO  extends DataSimpleVO{
    public Object value2;
    public Object value3;
    public Object value4;
    public Object value5;
    public Object value6;
    public Object value7;
    public Object value8;
    /*排序用,不参与序列化*/
    transient public int weight;
    public DataVO(){}
    public DataVO(String name, Object value) {
        this.name = name;
        this.value = value;
    }
    public DataVO(String name, Object value, Object value2) {
        this(name, value);
        this.value2 = value2;
    }
    public DataVO(String name, Object value, Object value2, Object value3) {
        this(name, value, value2);
        this.value3 = value3;
    }
    public DataVO(String name, Object value, Object value2, Object value3, Object value4) {
        this(name, value, value2, value3);
        this.value4 = value4;
    }
    public DataVO(String name, Object value, Object value2, Object value3, Object value4, Object value5) {
        this(name, value, value2, value3);
        this.value4 = value4;
        this.value5 = value5;
    }
    public DataVO(String name, Object value, Object value2, Object value3, Object value4, Object value5, Object value6, Object value7, Object value8) {
        this(name, value, value2, value3, value4, value5);
        this.value6 = value6;
        this.value7 = value7;
        this.value8 = value8;
    }
}
