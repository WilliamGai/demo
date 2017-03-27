package com.sincetimes.website.core.common.support;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 状态结果
 *	1 为成功
 *  0 为失败
 *  静态属性不会发给客户端
 */
public class Result{
	public static final byte SUCCESS = 1;
	public static final byte FAIL =0;
	
	public byte result;
	public String desc;
	
	public Result success(){
		this.result = Result.SUCCESS;
		return this;
	}
	
	public Result failure(){
		this.result = Result.FAIL;
		return this;
	}
	public Result setDesc(String desc){
		this.desc = desc;
		return this;
	}
	/*isSuccess()会传给前端success属性*/
	public boolean isSuccess(){
		return this.result == SUCCESS;
	}
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("result", result).append("desc", desc).toString();
	}
	
}
