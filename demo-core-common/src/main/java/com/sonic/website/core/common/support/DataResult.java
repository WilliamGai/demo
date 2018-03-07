package com.sonic.website.core.common.support;

public class DataResult{
	public byte result;
	public Object data;
	
	public DataResult(int result) {
		super();
		this.result = (byte)result;
	}
	public DataResult(int result, Object data) {
		super();
		this.result = (byte)result;
		this.data = data;
	}
	public DataResult setData(Object desc){
		this.data = desc;
		return this;
	}
}
