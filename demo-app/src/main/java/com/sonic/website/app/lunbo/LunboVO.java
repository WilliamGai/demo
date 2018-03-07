package com.sonic.website.app.lunbo;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.sonic.website.core.common.support.HttpUtil;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.common.vo.ToStringAbstract;

public class LunboVO extends ToStringAbstract{
	private static final byte STATUS_OK_1 = 1;
	private static final byte STATUS_ERR_0 = 0;
	
	public String img_url;		//上传的图片后生成的URL
	public String desc;	//描述
	public String img_url_cdn;	//待扩展
	public String link_url="";
	public byte status;			//0不可用, 1可用
	
	public LunboVO() {
		super();
	}
	
	public LunboVO(String img_url,String img_url_cdn, String link_url) {
		super();
		
		this.img_url = img_url;
		this.link_url = link_url;
	}
	public LunboVO setStatus(byte status){
		this.status = status;
		return this;
	}
	
	public LunboVO checkUrl(){
		this.status = HttpUtil.isUrlAvailable(link_url)?STATUS_OK_1:STATUS_ERR_0;
		return this;
	}
	
	/**
	 * 把Json转换为List,LunboVO必须有无参构造方法
	 */
	public static List<LunboVO> jsonToList(String json) {
		if (Util.isEmpty(json)) {
			return new ArrayList<LunboVO>();
		}
		return JSON.parseArray(json, LunboVO.class);
	}

	/**
	 * 将List转换为Json
	 */
	public static String listToJson(List<LunboVO> list) {
		if (null == list) {
			return null;
		}
		return JSON.toJSONString(list);
	}
}
