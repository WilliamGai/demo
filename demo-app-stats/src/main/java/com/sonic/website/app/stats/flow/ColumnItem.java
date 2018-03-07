package com.sonic.website.app.stats.flow;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.sonic.website.core.common.support.TimeUtil;
/***
 * 配置用
 */
public class ColumnItem implements Serializable{
	private static final long serialVersionUID = 3341604017783904760L;
	private String columnName;//字段名
	private String desc;//说明
	private ColumnItemType columnType;
	public ColumnItem(){
		
	}
	public ColumnItem(String columnName, String desc, ColumnItemType columnType) {
		super();
		this.columnName = columnName;
		this.desc = desc;
		this.columnType = columnType;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public ColumnItemType getColumnType() {
		return columnType;
	}
	public void setColumnType(ColumnItemType columnType) {
		this.columnType = columnType;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	public String format(String value) {
		if(null == columnType){
			return value;
		}
		switch (columnType) {
		case TIME_MILLI_SECODS:
			return TimeUtil.getTimeString(value, ()->Long.parseLong(value));
		case TIME_SECONDS:
			return TimeUtil.getTimeString(value, ()->Long.parseLong(value)*1000);
		default:
			return value;
		}
	}
	
}
