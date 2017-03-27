package com.sincetimes.website.core.common.support;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
/**
 * 当类的存储方式为Map(Param,Cache等)时,扩展一个转化为Json的方法
 */
public interface JSONSupport{
	Map<String,Object> getDatas();
	
	default JSONObject toJsonObj(){
		return new JSONObject(getDatas());
	}
}
