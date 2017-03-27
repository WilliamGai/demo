package com.sincetimes.website.app.luckydraw;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.JSON;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.common.support.WeightSupport;

/**
 * 大转盘的VO类，主要用于Json之间互转。
 */
public class LuckyDrawItemVO implements Serializable, WeightSupport<String> {
	private static final long serialVersionUID = -723711346541610282L;
	public String sn;		
	public String goods_name;
	public String goods_desc;
	public String pic_url;//图片地址
	public int weight;//权重
	public int current_turn_sn;	//每一轮抽奖的互斥标记
	public int global_sn;		//全局互斥标记
//	public LuckyDrawItemType goods_type = LuckyDrawItemType.CODE_WITH; 不需要,代码要少
	/* 不参与序列化的部分 **/
	public transient int left_codes_num;//剩余激活码数量,前端显示
	public transient int rmv_codes_num;//领取的激活码数量,前端显示
	public LuckyDrawItemVO() {
	}
	
	
	public LuckyDrawItemVO(String sn, String goods_name, String goods_desc, String pic_url, int weight, int current_turn_sn, int global_sn) {
		super();
		this.sn = sn;
		this.goods_name = goods_name;
		this.goods_desc = goods_desc;
		this.pic_url = pic_url;
		this.weight = weight;
		this.current_turn_sn = current_turn_sn;
		this.global_sn = global_sn;
	}


	public LuckyDrawItemVO init(){
		left_codes_num = LuckyDrawManager.inst().getLeftCodesNum(sn);
		rmv_codes_num = LuckyDrawManager.inst().getRemovedCodesNum(sn);
		return this;
	}
	
	/**快捷转json串 */
	public String toJSONString(){
		return JSON.toJSONString(this);
	}
	/**快捷从字符串转对象**/
	public static LuckyDrawItemVO parseObject(String jsonStr){
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		return JSON.parseObject(jsonStr, LuckyDrawItemVO.class);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public String mkKey() {
		return sn;
	}

	@Override
	public int mkWeight() {
		return this.weight;
	}
}
