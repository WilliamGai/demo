package com.sincetimes.website.app.stats.flow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DataStatsConfig implements Serializable{
	private static final long serialVersionUID = -4318194378244586696L;
	private String id;
	private String name;
	private String incrName;//自增字段
	private String sumName; //求和字段
	private String tableName;
	private Map<String, FilterItem> filterItems = new HashMap<>();
	private Map<String, ColumnItem> columnItems = new HashMap<>();
//	
	public DataStatsConfig(){
		
	};
	public DataStatsConfig(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIncrName() {
		return incrName;
	}
	public void setIncrName(String incrName) {
		this.incrName = incrName;
	}
	public String getSumName() {
		return sumName;
	}
	public void setSumName(String sumName) {
		this.sumName = sumName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Map<String, FilterItem> getFilterItems() {
		return filterItems;
	}
	public void setFilterItems(Map<String, FilterItem> filterItems) {
		this.filterItems = filterItems;
	}
	public Map<String, ColumnItem> getColumnItems() {
		return columnItems;
	}
	public void setColumnItems(Map<String, ColumnItem> columnItems) {
		this.columnItems = columnItems;
	}
	public DataStatsConfig putFilterItems(FilterItem item) {
		filterItems.put(item.getKey(), item);
		return this;
	}
	public DataStatsConfig removeFilterItems(String key) {
		filterItems.remove(key);
		return this;
	}
	public DataStatsConfig putColumnItems(String key, ColumnItem item) {
		columnItems.put(key, item);
		return this;
	}
	public DataStatsConfig removeColumnItems(String columnName) {
		columnItems.remove(columnName);
		return this;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
