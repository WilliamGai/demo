package com.sincetimes.website.app.stats.flow;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.base.Objects;
import com.sincetimes.website.core.common.support.CloneableSupport;
import com.sincetimes.website.core.common.support.KeySupport;
import com.sincetimes.website.core.common.support.TimeUtil;
import com.sincetimes.website.core.common.support.Util;

public class FilterItem implements KeySupport<String>,CloneableSupport<FilterItem>, Serializable{
	private static final long serialVersionUID = -7422124431829802541L;
	private String columnName;//字段名
	private String desc;//说明
	private FilterType filterType;
	private String inputValue;
	public FilterItem(){
		
	}
	public FilterItem(String columnName, String desc, FilterType filterType) {
		super();
		this.columnName = columnName;
		this.desc = desc;
		this.filterType = filterType;
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
	public FilterType getFilterType() {
		return filterType;
	}
	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}
	public String getInputValue() {
		return inputValue;
	}
	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}
	public boolean filter(Map<String,Object> rowMap) {
		String rowValue = rowMap.get(this.getColumnName())+"";
		String inputValue = this.getInputValue();
		if(Util.isEmpty(inputValue)){//条件没有填写即返回true
			return true;
		}
		long time = 0;
		long rowTime = 0;
		switch (filterType) {
		case STRING_EQUAL:
//			LogCore.BASE.info("rowValue={},inputValue={}, result={}", rowValue, inputValue, Objects.equal(rowValue, inputValue));
			return Objects.equal(rowValue, inputValue);
		case TIME_BEFORE:
			try {							    
				time = TimeUtil.parseAndGetTime(inputValue, "yyyy-MM-dd");
				rowTime = Long.parseLong(rowValue);
				return rowTime <= time;
			} catch (ParseException e) {
				return false;
			}
		case TIME_AFTER:
			try {
				time = TimeUtil.parseAndGetTime(inputValue, "yyyy-MM-dd");//mm/dd/yyyy 不是java的日期格式
				rowTime = Long.parseLong(rowValue);
				return rowTime >= time;
			} catch (ParseException e) {
				return false;
			}
		default:
			return true;
		}
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	@Override
	public String getKey() {
		return columnName + desc;
	}
	@Override
	public Object cloneThis() throws CloneNotSupportedException {
		return clone();
	}
	//空实现
	@Override
	public FilterItem afterInit() {
		return this;
	}
}
