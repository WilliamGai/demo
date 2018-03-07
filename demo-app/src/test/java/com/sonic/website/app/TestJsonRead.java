package com.sonic.website.app;

import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sonic.website.core.common.support.FileTool;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;

/***
 * 随机生成车牌号
 * @author bao 
 */
public class TestJsonRead {

	public static void main2(String args[]) throws Exception {
	    
	    JSONArray ja = new JSONArray();  
	    ja.add("a");
	    ja.add("b");
	    ja.add("c");
	    JSONObject json = new JSONObject();
	    json.put("hei", ja.toJSONString());
		LogCore.BASE.info("{}",ja.toJSONString());

		String line = FileTool.readLine("b.txt");
		LogCore.BASE.info("{}",line);
		JSONArray arr = JSONArray.parseArray(line);
		List<Object>list = Arrays.asList(arr.toArray());
		LogCore.BASE.info("{}",Util.prettyJsonStr(list));
		LogCore.BASE.info("{}",json.toString());
		
	}
}
