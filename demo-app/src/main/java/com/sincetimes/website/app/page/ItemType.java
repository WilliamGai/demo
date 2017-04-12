package com.sincetimes.website.app.page;

import java.util.HashMap;
import java.util.Map;
public enum ItemType {
	TEXT((byte) 1,	"文本"),
	TEXT_AREA((byte) 2,	"长文本"),
	TITLE_TEXT((byte) 3,  "带标题的长文本"),
	RICH_TEXT((byte) 4,  "富文本"),
	PICTURE((byte) 5, "图片"),//
	PICTURE_LIST((byte) 6, "图片组"),//
	TITLE_TEXT_LIST((byte) 7,  "带标题的长文本组");//
	public final byte id;
	public final String name;
	
	private final static Map<Byte, ItemType> byteEnumMap = new HashMap<>();
	static{
		for(ItemType t:values())
			byteEnumMap.put(t.id, t);
	}
	
	private ItemType(byte id, String name) {
		this.id = id;
		this.name = name;
	}
	public static ItemType getType(byte id){
		return byteEnumMap.get(id);
	}
}
