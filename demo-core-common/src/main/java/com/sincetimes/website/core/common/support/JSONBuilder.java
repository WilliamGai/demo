package com.sincetimes.website.core.common.support;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class JSONBuilder {
	private static final String KEY_SINGLE = "result"; // 当只传入一个数据项时，自动用此KEY值，简化操作
	private final Map<String, Object> datas = new HashMap<>();
	public JSONBuilder(){}
	public JSONBuilder append(String name, Object value){
		datas.put(name, value);
		return this;
	}
	public JSONObject buildJSON(){
		return new JSONObject(datas);
	}
	public static JSONBuilder create(){
		return new JSONBuilder();
	}
	/*直接使用*/
	public static String creatJsonString(Object... params) {
		// 无参 返回空
		if (params == null || params.length == 0) {
			return null;
		}
		JSONObject json = new JSONObject();
		// 当数据仅有一项
		if (params.length == 1) {
			json.put(KEY_SINGLE, params[0]);
		} else { // 处理成对参数
			int len = params.length;
			for (int i = 0; i < len; i += 2) {
				String key = (String) params[i];
				Object val = (Object) params[i + 1];
				json.put(key, val);
			}
		}
		return json.toJSONString();
	}
}
