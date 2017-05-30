package com.sincetimes.website.app.lunbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.common.vo.ToStringAbstract;

public class LunboGroupVO extends ToStringAbstract{
	public String group_no;
	public String desc;//描述信息
	public List<LunboVO> lunbos = new ArrayList<>();
	public long time;//创建时间，排序用
	public LunboGroupVO() {
		super();
		this.time = System.currentTimeMillis();
	}
	
	public LunboGroupVO(String group_no, String desc) {
		super();
		this.group_no = group_no;
		this.desc = desc;
		this.time = System.currentTimeMillis();
	}
	public LunboGroupVO checkUrl(){
		this.lunbos.forEach(vo->vo.checkUrl());
		return this;
	}
	public String getInfo(){
		return this.group_no;
	}
	public void upLunboVO(int index){
		if(lunbos.size() <= 1){
			return;
		}
		if(0 >= index){
			return;
		}
		Collections.swap(lunbos, index, index -1);
	}
	/*排序用*/
	public long getTime(){
		return this.time;
	}
	/**快捷转json串 */
	public String toJSONString(){
		return JSON.toJSONString(this);
	}
	/**快捷从字符串转对象**/
	public static LunboGroupVO parseObject(String jsonStr){
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		return JSON.parseObject(jsonStr, LunboGroupVO.class);
	}

	public void deleteLunboPage(Integer ordinal) {
		if(ordinal >= lunbos.size() || ordinal < 0){
			return;
		}
		lunbos.remove(ordinal);
	}

}
