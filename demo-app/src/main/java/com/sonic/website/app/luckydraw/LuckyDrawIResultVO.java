package com.sonic.website.app.luckydraw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.common.vo.ToStringAbstract;

/**
 * 大转盘的VO类，主要用于Json之间互转。
 */
public class LuckyDrawIResultVO extends ToStringAbstract implements Serializable {
	private static final long serialVersionUID = -7687955182997833348L;
	public String sn;	//奖品编号	
	public int ordinal; //当时抽奖的下标
	public String code;
	public String pic_url;
	public long time;
	public LuckyDrawIResultVO() {
	}
	
	public LuckyDrawIResultVO(String sn, int ordinal, String code, String pic_url) {
		super();
		this.sn = sn;
		this.ordinal = ordinal;
		this.code = code;
		this.pic_url = pic_url;
		this.time = System.currentTimeMillis();
	}

	/**快捷转json串 */
	public String toJSONString(){
		return JSON.toJSONString(this);
	}
	/**快捷从字符串转对象**/
	public static LuckyDrawIResultVO parseObject(String jsonStr){
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		return JSON.parseObject(jsonStr, LuckyDrawIResultVO.class);
	}
	
	/**
	 * 将json转为Map<String,List>
	 * Map<String, Object> map 即Map<String, JSONArray> map
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, List<LuckyDrawIResultVO>> jsonToMap(String json) {
		Map<String, List<LuckyDrawIResultVO>> _map = new HashMap<>();
		if (Util.isEmpty(json)) {
			return new HashMap<String, List<LuckyDrawIResultVO>>();
		}
		Map<String, Object> map = JSON.parseObject(json, Map.class);
		map.forEach((k,v)->_map.put(k, jsonToList(v.toString())));
		return _map;

	}
	/**
	 * 把Json转换为List,必须有无参构造方法
	 */
	public static List<LuckyDrawIResultVO> jsonToList(String json) {
		if (Util.isEmpty(json)) {
			return new ArrayList<LuckyDrawIResultVO>();
		}
		return JSON.parseArray(json, LuckyDrawIResultVO.class);

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
}
