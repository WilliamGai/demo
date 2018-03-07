package com.sonic.website.app.luckydraw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.sonic.website.core.common.support.LogCore;

public class LuckyDrawTest {
	public void test() {
		
		LuckyDrawItemVO vo = new LuckyDrawItemVO();
		System.out.println(vo);
		String json = "{'close_time':1481868000000,'desc':'每周一都会刷新','fresh_type':1,'name':'舰指太平洋每周','open_time':1481868000000,'sn':'jztpy_week','status':0}";
		LuckyDrawItemVO j = LuckyDrawItemVO.parseObject(json);
		System.err.println(j);
		System.err.println(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
		
		Object o = null;
		LuckyDrawItemVO o2 = (LuckyDrawItemVO)o;
		System.err.println("o2="+o2);
	}
	public void testFreshType(){
		for (LuckyDrawFreshType t : LuckyDrawFreshType.values()) {
			System.out.println(t.name());
		}
	}
	@Test
	public void testLuckyDrawIResultVO(){
		LuckyDrawIResultVO vo = new LuckyDrawIResultVO("a", 1,"c","pic_path");
		Map<String, List<LuckyDrawIResultVO>> map= new HashMap<>();
		List<LuckyDrawIResultVO> list = new ArrayList<>();
		list.add(vo);
		map.put("sn", list);		
		String json = JSON.toJSONString(map);
		System.out.println("list="+LuckyDrawIResultVO.toJSONString(list));
		System.out.println("map="+map);
		System.err.println("json="+json);
		Map<String, List<LuckyDrawIResultVO>>  mp = LuckyDrawIResultVO.jsonToMap(json);
		System.err.println("rest="+mp);
		System.err.println("rest="+mp.get("sn"));
		
		String[] _sns = "lucky_draw_01".split("__");
		LogCore.BASE.info("sns={}", Arrays.toString(_sns));
		int num = 5;
		while(num-- >0){
			System.out.println(num);
		}
	}
}
