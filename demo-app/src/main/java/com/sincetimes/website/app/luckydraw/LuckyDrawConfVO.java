package com.sincetimes.website.app.luckydraw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.common.support.Util;

/**
 * 大转盘的VO类，主要用于Json之间互转。
 */
public class LuckyDrawConfVO implements Serializable {
	private static final long serialVersionUID = -2796572185654770931L;
	
	public String sn;					//主键
	public String name;
	public String desc;
	public int status;					//解锁状态  -1：未解锁   -2：临时解锁   -3：永久解锁   -4：永久开启
	public long open_time;				//开启时间 待扩展
	public long close_time;				//结束时间时间 待扩展
	public LuckyDrawFreshType fresh_type = LuckyDrawFreshType.NEVER;//刷新规则
	public List<LuckyDrawItemVO> shelfs = new ArrayList<>();		//货架，商品列表
	public int limit_num;//每个周期的抽奖次数
	
	public long create_time;				//创建时间
	public String create_by;
	/* 不参与序列化的部分 **/
	public transient String open_time_str;				
	public transient String close_time_str;				
	public transient String create_time_str;				
	public LuckyDrawConfVO() {
	}
	
	public LuckyDrawConfVO(String sn, String create_by, String name, String desc,byte fresh_getType, long open_time, long close_time, int limit_num) {
		super();
		this.sn = sn;
		this.name = name;
		this.desc = desc;
		this.fresh_type = LuckyDrawFreshType.getType(fresh_getType);
//		this.status = status;
		this.create_by = create_by;
		this.create_time = System.currentTimeMillis();
		this.open_time = open_time;
		this.close_time = close_time;
		this.limit_num = limit_num;
	}
	
	public LuckyDrawConfVO init(){
		open_time_str = TimeTool.formatTime(open_time, "yyyy-MM-dd-HH:mm:ss");
		close_time_str = TimeTool.formatTime(close_time, "yyyy-MM-dd-HH:mm:ss");
		create_time_str = TimeTool.formatTime(create_time, "yyyy-MM-dd-HH:mm:ss");
		shelfs.forEach(LuckyDrawItemVO::init);
		return this;
	}
	public LuckyDrawItemVO getGoods(String sn){
		return shelfs.stream().filter(a->Objects.equal(a.sn, sn)).findFirst().orElse(null);
	}
	
	public void addGoods(LuckyDrawItemVO goods){
		shelfs.add(goods);
	}
	public LuckyDrawItemVO deleteGoods(String sn){
		if(Util.isEmpty(sn)){
			return null;
		}
		Iterator<LuckyDrawItemVO> iterator = shelfs.iterator();
		while(iterator.hasNext()){
			LuckyDrawItemVO vo = iterator.next();
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
		LuckyDrawItemVO vo = getGoods(sn);
		if(null == vo){
			return;
		}
		int index = shelfs.indexOf(vo);//不可能为-1
		if(0 >= index){
			return;
		}
		Collections.swap(shelfs, index, index -1);
	}
	/**快捷转json串 */
	public String toJSONString(){
		return JSON.toJSONString(this);
	}
	/**快捷从字符串转对象**/
	public static LuckyDrawConfVO parseObject(String jsonStr){
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		return JSON.parseObject(jsonStr, LuckyDrawConfVO.class);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
