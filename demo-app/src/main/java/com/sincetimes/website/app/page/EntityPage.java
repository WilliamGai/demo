package com.sincetimes.website.app.page;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.sincetimes.website.vo.VOBase;
/**
 * dict<id, hash>
 * @author BAO
 */
public class EntityPage extends VOBase{
	private String id;//编号
	private String gameName;//游戏名字
	private String gameDesc;//游戏介绍
	private String homePageLink;//官网连接
	private int readNum;//查询次数
	private String type_id;//平台类型
	private List<String> images;//图片
	private long update_time;
	private String short_title;
	private String sort; 
	private String link_url;
	//
	private String createdBy;
	private String updatedBy;
	
	private transient String typeNames;
	private transient String createTimeStr;
	private transient String updateTimeStr;
	
	public EntityPage() {
		super();
	}


	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
