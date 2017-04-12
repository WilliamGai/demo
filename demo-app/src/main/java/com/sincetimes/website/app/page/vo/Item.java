package com.sincetimes.website.app.page.vo;

import com.sincetimes.website.app.page.ItemType;
import com.sincetimes.website.vo.VOBase;

public class Item extends VOBase{
	public static final int ARRAY			= 0b0001;		//数组
	
	public static final int NULL			= 0;			//空对象
	
	//基本类型
	public static final int BYTE			= 2;
	public static final int BOOLEAN			= 4;
	public static final int INT				= 6;
	public static final int LONG			= 8;
	public static final int FLOAT			= 10;
	public static final int DOUBLE			= 12;
	public static final int STRING			= 14;
	public static final int ENUM			= 16;
	public static final int OBJECT			= 20;
	
	//容器类型
	public static final int LIST			= 22;			//内置支持List对象
	public static final int MAP				= 26;			//内置支持Map对象

	private String key;
	private String name;//名称
	private ItemType type;
	private Object data;
	
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
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}