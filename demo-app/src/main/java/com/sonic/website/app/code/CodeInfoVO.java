package com.sonic.website.app.code;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.sonic.website.core.common.support.TimeUtil;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.common.vo.ToStringAbstract;

/**
 * 激活码的VO类，主要用于Json之间互转。
 */
public class CodeInfoVO extends ToStringAbstract implements Serializable {
	private static final long serialVersionUID = -2796572185654770931L;
	
	public String sn;					//主键
	public String name;
	public String desc;
	public int status;					//解锁状态  -1：未解锁   -2：临时解锁   -3：永久解锁   -4：永久开启
	public long open_time;				//开启时间 待扩展
	public long close_time;				//结束时间时间 待扩展
	public CodeFreshType fresh_type = CodeFreshType.NEVER;//刷新规则
	
	public long create_time;				//创建时间
	public String create_by;
	/* 不参与序列化的部分 **/
	public transient int left_codes_num;//剩余激活码数量,前端显示
	public transient int rmv_codes_num;//领取的激活码数量,前端显示
	public transient String open_time_str;				
	public transient String close_time_str;				
	public transient String create_time_str;				
	public CodeInfoVO() {
	}
	
	public CodeInfoVO(String sn, String create_by, String name, String desc,byte fresh_getType, long open_time, long close_time) {
		super();
		this.sn = sn;
		this.name = name;
		this.desc = desc;
		this.fresh_type = CodeFreshType.getType(fresh_getType);
//		this.status = status;
		this.create_by = create_by;
		this.create_time = System.currentTimeMillis();
		this.open_time = open_time;
		this.close_time = close_time;
	}
	
	public CodeInfoVO init(){
		left_codes_num = CodeManager.inst().getLeftCodesNum(sn);
		rmv_codes_num = CodeManager.inst().getRemovedCodesNum(sn);
		open_time_str = TimeUtil.formatTime(open_time, "yyyy-MM-dd-HH:mm:ss");
		close_time_str = TimeUtil.formatTime(close_time, "yyyy-MM-dd-HH:mm:ss");
		create_time_str = TimeUtil.formatTime(create_time, "yyyy-MM-dd-HH:mm:ss");
		return this;
	}
	
	/**快捷转json串 */
	public String toJSONString(){
		return JSON.toJSONString(this);
	}
	/**快捷从字符串转对象**/
	public static CodeInfoVO parseObject(String jsonStr){
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		return JSON.parseObject(jsonStr, CodeInfoVO.class);
	}
}
