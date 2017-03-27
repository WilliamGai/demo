package com.sincetimes.website.app.lunbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.sincetimes.website.core.common.support.JSONBuilder;

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
		

		System.out.println(lunbos2);
		
		Map<Integer,Object> map = new HashMap<>();
		map.put(0, "ABC");
		map.put(1, lunbos2);
		String ss = JSON.toJSONString(map);
		
		@SuppressWarnings("unchecked")
		Map<Integer,Object> map2=JSON.parseObject(ss, Map.class);
		System.err.println(ss);
		System.err.println(map2);
		System.err.println(map2.get(0));
		
		
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
