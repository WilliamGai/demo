package com.sincetimes.website.app.shop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.JSON;
import com.sincetimes.website.core.common.support.Util;

/**
 * VO类，主要用于Json之间互转。
 */
public class ExchangeResultVO implements Serializable {
	private static final long serialVersionUID = -5453577543529861867L;
	public String sn;	
	public String code;
	public String pic_url;
	public String goods_name;
	public String goods_desc;
	public long time;
	public ExchangeResultVO() {
	}
	
	

	public ExchangeResultVO(String sn, String code, String pic_url, String goods_name, String goods_desc) {
		super();
		this.sn = sn;
		this.code = code;
		this.pic_url = pic_url;
		this.goods_name = goods_name;
		this.goods_desc = goods_desc;
		this.time = System.currentTimeMillis();
	}



	/**快捷转json串 */
	public String toJSONString(){
		return JSON.toJSONString(this);
	}
	/**快捷从字符串转对象**/
	public static ExchangeResultVO parseObject(String jsonStr){
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		return JSON.parseObject(jsonStr, ExchangeResultVO.class);
	}
	
	public static List<ExchangeResultVO> jsonToList(String json) {
		if (Util.isEmpty(json)) {
			return new ArrayList<ExchangeResultVO>();
		}
		return JSON.parseArray(json, ExchangeResultVO.class);

	}

	/**
	 * 将List转换为Json
	 */
	public static String toJSONString(Object obj) {
		if (null == obj) {
			return null;
		}
		return JSON.toJSONString(obj);
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
