package com.sonic.website.app.lunbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sonic.website.core.common.support.DataSimpleVO;
import com.sonic.website.core.common.support.DataVO;
import com.sonic.website.core.common.support.JSONBuilder;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;

public class LunboVOTest {
	
	@Test
	public void test(){
		LunboGroupVO group = new LunboGroupVO();
		LunboVO vo = new LunboVO("img_url","", "link_url");
		LunboVO vo2 = new LunboVO( "img_url2","", "link_url2");
		List<LunboVO> list = new ArrayList<>();
		list.add(vo);
		list.add(vo2);
		group.lunbos = list;
		
		String s = JSON.toJSONString(group);
		LunboGroupVO lunbos2 = JSON.parseObject(s, LunboGroupVO.class);
		
		/*
		 *序列化一个类，因为类型和属性的类型确定，所以可以任意转换 
		 */
		LogCore.BASE.info("lunboString={}", s);
		LogCore.BASE.info("lunbos2={}", Util.prettyJsonStr(lunbos2));
		LogCore.BASE.info("lunbos2[1]={}", Util.prettyJsonStr(lunbos2.lunbos.get(0)));
		
		Map<Integer,Object> map = new HashMap<>();
		DataSimpleVO v1 = new DataVO("a", "b");
		DataSimpleVO v2 = new DataVO("vo", v1);
		map.put(0, "ABC");
//		map.put(2, v2);
		map.put(1, lunbos2);
//		v1.value=map;
		String ss = JSON.toJSONString(v2,SerializerFeature.WriteClassName);
		
		/*
		 *序列化一个不明确的类型，需要用PrettyJSON才可以
		 */
//		@SuppressWarnings("unchecked")
//		Map<Integer,Object> map2=JSON.parseObject(ss, Map.class);
		v2= JSON.parseObject(ss, DataSimpleVO.class);
		System.err.println(ss);
		System.err.println(v2);
		System.err.println(v2.value.getClass().getSimpleName());//JSONObject
		
		
		List<String> lunbos = new ArrayList<>();
		lunbos.add("a");
		lunbos.add("b");
		lunbos.add("c");
//		lunbos.add("d");
//		lunbos.add("e");//5
//		lunbos.add("f");
//		lunbos.add("g");
//		lunbos.add("h");
		int size = 5;
//		List<String> ll2 = new ArrayList<>(2);
		int diff = lunbos.size() - size;
		if(diff>0){
			while(diff-->0){
				lunbos.remove(lunbos.size()-1);
			}
		}
		else{
			while(diff++<0 ){
				lunbos.add("heiei");
			}
		}
		
//		ll2.addAll(ll);
		System.err.println(lunbos);
		String json_str= JSONBuilder.creatJsonString("result", "a" ,"code", null);
		System.err.println(json_str);
	}
}
