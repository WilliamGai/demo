package com.sonic.website.app.page;

import java.util.HashMap;
import java.util.Map;

import com.sonic.website.app.page.builders.ItemBuiler;
import com.sonic.website.app.page.builders.ItemPictureBuiler;
import com.sonic.website.app.page.builders.ItemPictureListBuiler;
import com.sonic.website.app.page.builders.ItemTextAreaBuiler;
import com.sonic.website.app.page.builders.ItemTextAreaListBuiler;
import com.sonic.website.app.page.builders.ItemTextBuiler;
import com.sonic.website.app.page.builders.ItemTextListBuiler;
import com.sonic.website.app.page.vo.Item;
public enum ItemType {
	TEXT((byte) 1,	"文本"),
	TEXT_LIST((byte) 7, "文本组"),
	TEXT_AREA((byte) 2,	"长文本"),
	TEXT_AREA_LIST((byte) 3,  "长文本组"),
	RICH_TEXT((byte) 4,  "富文本"),
	PICTURE((byte) 5, "图片"),
	PICTURE_LIST((byte) 6, "图片组");

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
	public ItemBuiler<Item> getBuilder() {
		switch (this) {
		case TEXT:
			return ItemTextBuiler.INSTACNCE;
		case TEXT_LIST:
			return ItemTextListBuiler.INSTACNCE;
		case TEXT_AREA:
			return ItemTextAreaBuiler.INSTACNCE;
		case TEXT_AREA_LIST:
			return ItemTextAreaListBuiler.INSTACNCE;
		case PICTURE:
			return ItemPictureBuiler.INSTACNCE;
		case PICTURE_LIST:
			return ItemPictureListBuiler.INSTACNCE;
		default:
			return null;
		}
	}
}
