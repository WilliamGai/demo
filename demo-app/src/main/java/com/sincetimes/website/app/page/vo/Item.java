package com.sincetimes.website.app.page.vo;

import com.sincetimes.website.app.page.ItemType;
import com.sincetimes.website.core.common.support.CloneableSupport;
import com.sincetimes.website.core.common.vo.VOBase;

public class Item extends VOBase implements CloneableSupport<Item>{

	private String key;
	private String name;//名称
	private ItemType type;
	private Object data;//可能为String, List<String>, List<ItemData>等,复制后clone更改原来的也会更改
	private long createTime;
	private transient String createTimeStr;
	
	public Item() {
		super();
	}
	
	public Item(String key, String name, ItemType type) {
		this.key = key;
		this.name = name;
		this.type = type;
	}
	
	public String getKey() {
		return key;
	} 
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ItemType getType() {
		return type;
	}
	public void setType(ItemType type) {
		this.type = type;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getData() {
		return (T)data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	@Override
	public Object cloneThis() throws CloneNotSupportedException {
		return this.clone();
	}

	@Override
	public Item afterInit() {
		return this;
	}

}
