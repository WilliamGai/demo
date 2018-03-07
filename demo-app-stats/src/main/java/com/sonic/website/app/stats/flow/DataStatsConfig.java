package com.sonic.website.app.stats.flow;

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
	private Map<String, FilterItem> filterItems = new HashMap<>();//过滤查询条件
	private Map<String, ColumnItem> columnItems = new HashMap<>();//显式哪些属性
	private Map<String, GroupColumnItem> groupItems = new HashMap<>();//分组显式的属性

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
	public Map<String, GroupColumnItem> getGroupItems() {
		return groupItems;
	}
	public void setGroupItem(Map<String, GroupColumnItem> groupItems) {
		this.groupItems = groupItems;
	}
	public DataStatsConfig putFilterItem(FilterItem item) {
		filterItems.put(item.getKey(), item);
		return this;
	}
	public DataStatsConfig removeFilterItem(String key) {
		filterItems.remove(key);
		return this;
	}
	public DataStatsConfig putColumnItem(String key, ColumnItem item) {
		columnItems.put(key, item);
		return this;
	}
	public DataStatsConfig removeColumnItem(String columnName) {
		columnItems.remove(columnName);
		return this;
	}
	public DataStatsConfig putGroupItem(GroupColumnItem item) {
		groupItems.put(item.getKey(), item);
		return this;
	}
	public DataStatsConfig removeGroupItem(String key) {
		groupItems.remove(key);
		return this;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
