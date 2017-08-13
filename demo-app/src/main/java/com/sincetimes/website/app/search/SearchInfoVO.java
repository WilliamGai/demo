package com.sincetimes.website.app.search;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.sincetimes.website.core.common.support.TimeUtil;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.common.vo.ToStringAbstract;

/**
 * 搜索配置的VO类，主要用于Json之间互转。
 */
public class SearchInfoVO extends ToStringAbstract implements Serializable {
	private static final long serialVersionUID = -2796572185654770931L;
	
	public String sn;					//主键
	public String name;
	public String desc;
	public int status;					//解锁状态  -1：未解锁   -2：临时解锁   -3：永久解锁   -4：永久开启
	
	public int threds = 1;//线程数目
	
	public long create_time;				//创建时间
	public String create_by;
	/* 不参与序列化的部分 **/
	public transient int left_codes_num;//剩余激活码数量,前端显示
	public transient int rmv_codes_num;//领取的激活码数量,前端显示
	public transient String create_time_str;				
	public SearchInfoVO() {
	}
	
	public SearchInfoVO(String sn, String create_by, String name, String desc, int threads) {
		super();
		this.sn = sn;
		this.name = name;
		this.desc = desc;
		this.create_by = create_by;
		this.create_time = System.currentTimeMillis();
		this.threds = threads;
	}
	
	
	/**快捷转json串 */
	public String toJSONString(){
		return JSON.toJSONString(this);
	}
	/**快捷从字符串转对象**/
	public static SearchInfoVO parseObject(String jsonStr){
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		return JSON.parseObject(jsonStr, SearchInfoVO.class);
	}
}
