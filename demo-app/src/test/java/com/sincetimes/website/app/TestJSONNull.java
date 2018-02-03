package com.sincetimes.website.app;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sincetimes.website.core.common.support.LogCore;

public class TestJSONNull {

	// {"courseIds":[null],"finishType":["AS_SCHEDULED"],"studentId":7773511}
	public static void main(String[] args) {
		Map<Integer, Integer> map = new HashMap<>();
		map.put(null, 2);
		map.put(3, 5);
		System.out.println(JSON.toJSONString(map));
	}

}
