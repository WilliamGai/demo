package com.sincetimes.website.app.article;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.sincetimes.website.core.common.support.TimeTool;

public class Article {
	private int id;//auto incr
	private String title;
	private String content;//byte[],blob
	private long create_time;
	private int user_id;
	private int read_num;
	private String type_id;
	private String img_url;
	private long update_time;
	private String short_title;
	private String sort; 
	private String link_url;
	//
	private String created_by;
	private String updated_by;
	
	//
	private transient String type_name;
	private transient String create_time_str;
	private transient String update_time_str;
	
	public Article() {
		super();
	}

	public String getCreate_time_str() {
		return create_time_str;
	}

	public void setCreate_time_str(String create_time_str) {
		this.create_time_str = create_time_str;
	}

	public String getUpdate_time_str() {
		return update_time_str;
	}

	public void setUpdate_time_str(String update_time_str) {
		this.update_time_str = update_time_str;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		if(0 != create_time){
			this.setCreate_time_str(TimeTool.formatTime(create_time, "yyyy-MM-dd-HH:mm"));
		}
		this.create_time = create_time;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getRead_num() {
		return read_num;
	}

	public void setRead_num(int read_num) {
		this.read_num = read_num;
	}

	public String getType_id() {
		return type_id;
	}

	public void setType_id(String type_id) {
		this.type_id = type_id;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(long update_time) {
		if(0 != update_time){
			this.update_time_str = TimeTool.formatTime(update_time, "yyyy-MM-dd-HH:mm");
		}
		this.update_time = update_time;
	}

	public String getShort_title() {
		return short_title;
	}

	public void setShort_title(String short_title) {
		this.short_title = short_title;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getLink_url() {
		return link_url;
	}

	public void setLink_url(String link_url) {
		this.link_url = link_url;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
