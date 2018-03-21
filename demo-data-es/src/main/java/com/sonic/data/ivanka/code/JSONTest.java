package com.sonic.data.ivanka.code;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.sonic.website.core.common.support.JSONSupport;

public class JSONTest implements JSONSupport{
	final Map<String, Object> datas = new HashMap<>();
	public JSONTest(){};
	JSONTest(String key,String value){
		datas.put(key, value);
	}
	@Test
	public void test() {
		String ss[] = {"a","b", "c"};
		int[] ii = {1,2,3};
		Integer [] II = {4,5,6};
		Stream.of(ss).forEach(System.out::println);
		Stream.of(ii).forEach(System.out::println);
		Stream.of(II).forEach(System.out::println);
		
		
		Map<String, Object> datas = new HashMap<>();
		datas.put("a", 1);
		datas.put("b", "2");
		datas.put("c", null);
		System.err.println(new JSONObject(datas));
	}
	@Override
	public Map<String, Object> getDatas() {
		return datas;
	}

}
