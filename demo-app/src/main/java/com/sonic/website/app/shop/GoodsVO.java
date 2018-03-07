package com.sonic.website.app.shop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sonic.website.core.common.vo.ToStringAbstract;
/**
 * 无状态的服务可能有人同时操作,因此不能用index来标识list的哪一个
 */
public class GoodsVO extends ToStringAbstract implements Serializable {
	private static final long serialVersionUID = 6821748672785184888L;
	public String sn;		
	public String goods_name;
	public String goods_desc;
	public String pic_url;//图片地址
	public int points_need;
	//
	public transient int left_codes_num;//剩余激活码数量,mg显示
	public transient int rmvd_codes_num;//mg显示
	public GoodsVO(JSONObject jo) {
		this.sn = jo.getString("sn");
	}
	
	public GoodsVO() {
	}
	
	public GoodsVO(String sn, String goodsName, String pic_url, String goodsDesc, int pointsNeed) {
		super();
		this.sn = sn;
		this.goods_name = goodsName;
		this.pic_url = pic_url;
		this.goods_desc = goodsDesc;
		this.points_need = pointsNeed;
	}
	
	public GoodsVO(GoodsVO goods){
		this.sn = goods.sn;
		this.goods_name = goods.goods_name;
		this.pic_url = goods.pic_url;
		this.goods_desc = goods.goods_desc;
		this.points_need = goods.points_need;
	}
	
	public GoodsVO init(){
		left_codes_num = (int) ShopManager.inst().getLeftCodesNum(sn);
		rmvd_codes_num = (int) ShopManager.inst().getRemovedCodesNum(sn);
		return this;
	}
	/**
	 * 把Json转换为List
	 * @param json
	 * @return
	 */
	public static List<GoodsVO> jsonToList(String json) {
		List<GoodsVO> result = new ArrayList<GoodsVO>();
		
		JSONArray ja = JSON.parseArray(json);
		for (int i = 0; i < ja.size(); i++) {
			GoodsVO vo = new GoodsVO(ja.getJSONObject(i));
			result.add(vo);
		}
		
		return result;
	}
	
	/**
	 * 将List转换为Json
	 * @param list
	 * @return
	 */
	public static String listToJson(List<GoodsVO> list){
		JSONArray ja = new JSONArray();
		if (list != null) {
			for (GoodsVO vo : list) {
				JSONObject jo = new JSONObject();
				jo.put("sn", vo.sn);
				ja.add(jo);
			}
		}
		return ja.toJSONString();
	}
}
