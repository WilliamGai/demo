package com.sincetimes.website.app.article;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ArticleVO {
	public int id;//auto incr
	public String title;
	public String short_title;
	public int read_num;
	
	public String type_id;
	public String type_name;
	public long time;
	
	public ArticleVO() {
		super();
	}
	public ArticleVO(Article ar){
		id = ar.getId();//auto incr
		title = ar.getTitle();
		short_title = ar.getShort_title();
		read_num = ar.getRead_num();
		
		type_id = ar.getType_id();
		type_name = ar.getType_name();

		time = Math.max(ar.getCreate_time(), ar.getUpdate_time());
		//date = new Date(time);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
