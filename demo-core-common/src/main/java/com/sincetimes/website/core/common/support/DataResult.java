package com.sincetimes.website.core.common.support;

/**
 * 状态结果
 *	1 为成功
 *  0 为失败
 *  静态属性不会发给客户端
 */
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
