package com.sincetimes.website.app.search;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.thymeleaf.templateresolver.FileTemplateResolver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sincetimes.website.core.common.support.DrawUtil;
import com.sincetimes.website.core.common.support.FileTool;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;

/***
 * 随机生成车牌号
 * @author bao 
 */
public class Test {

	public static void main(String args[]) throws Exception {
	    
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
		Util util;
		
		
	}
}
