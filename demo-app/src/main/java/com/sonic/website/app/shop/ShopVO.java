package com.sonic.website.app.shop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Objects;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.common.vo.ToStringAbstract;

/**
 * 商店的VO类，主要用于Json之间互转。
 */
public class ShopVO extends ToStringAbstract implements Serializable {
	private static final long serialVersionUID = -9142050972467351648L;
	public String sn;	
	public String shop_name;
	public String shop_desc;
	public int status;					//解锁状态  -1：未解锁   -2：临时解锁   -3：永久解锁   -4：永久开启
	public long open_time;				//开启时间 待扩展
	public List<GoodsVO> shelfs = new ArrayList<>();		//货架，商品列表
	
	
	public ShopVO() {
	}
	
	public ShopVO(String sn, String shopName, String shopDesc, int status, long openTime, List<GoodsVO> shelfs) {
		super();
		this.sn = sn;
		this.shop_name = shopName;
		this.shop_desc = shopDesc;
		this.status = status;
		this.open_time = openTime;
		this.shelfs = shelfs;
	}
	public ShopVO init(){
		shelfs.forEach(GoodsVO::init);
		return this;
	}
	public GoodsVO getGoods(String sn){
		return shelfs.stream().filter(a->Objects.equal(a.sn, sn)).findFirst().orElse(null);
	}
	public void addGoods(GoodsVO goods){
		shelfs.add(goods);
	}
	public GoodsVO deleteGoods(String sn){
		if(Util.isEmpty(sn)){
			return null;
		}
		Iterator<GoodsVO> iterator = shelfs.iterator();
		while(iterator.hasNext()){
			GoodsVO vo = iterator.next();
			if(sn.equals(vo.sn)){
				iterator.remove();
				return vo;
			}
		}
		return null;
	}
	public void upGoods(String sn){
		if(Util.isEmpty(sn)||shelfs.size() <= 1){
			return;
		}
		GoodsVO vo = getGoods(sn);
		if(null == vo){
			return;
		}
		int index = shelfs.indexOf(vo);//不可能为-1
		if(0 >= index){
			return;
		}
		Collections.swap(shelfs, index, index -1);
	}
	/* 旧的方式 start */
	public ShopVO(JSONObject jo) {
		this.sn = jo.getString("sn");
		this.status = jo.getIntValue("status");
		this.open_time = jo.getLongValue("openTime");
		JSONArray goodsJson = JSON.parseArray(jo.getString("shelfs"));
		shelfs = GoodsVO.jsonToList(goodsJson.toJSONString());
	}
	/**
	 * 把Json转换为List
	 * @param json
	 * @return
	 */
	public static List<ShopVO> jsonToList(String json) {
		List<ShopVO> result = new ArrayList<ShopVO>();
		JSONArray ja = JSON.parseArray(json);
		for (int i = 0; i < ja.size(); i++) {
			ShopVO vo = new ShopVO(ja.getJSONObject(i));
			result.add(vo);
		}
		return result;
	}
	
	/**
	 * 将List转换为Json
	 * @param list
	 * @return
	 */
	public static String listToJson(List<ShopVO> list){
		JSONArray ja = new JSONArray();
		for (ShopVO vo : list) {
			JSONObject jo = new JSONObject();
			jo.put("sn", vo.sn);
			jo.put("status", vo.status);
			jo.put("openTime", vo.open_time);
			jo.put("shelfs", GoodsVO.listToJson(vo.shelfs));
			ja.add(jo);
		}
		return ja.toJSONString();
	}
	/* 旧的方式 end */
	/**快捷转json串 */
	public String toJSONString(){
		return JSON.toJSONString(this);
	}
	/**快捷从字符串转对象**/
	public static ShopVO parseObject(String jsonStr){
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		return JSON.parseObject(jsonStr, ShopVO.class);
	}
	
}
