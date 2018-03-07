package com.sonic.website.core.common.support;

/** ::new */
public class DataSimpleVO {
    public String name;
    public Object value;
    public Object value1;

    public DataSimpleVO() {
    }

    public DataSimpleVO(String name, Object value) {
	this.name = name;
	this.value = value;
    }

    @Override
    public String toString() {
	return "DataSimpleVO [name=" + name + ", value=" + value + ", value1=" + value1 + "]";
    }
    
}
