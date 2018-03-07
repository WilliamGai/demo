package com.sonic.website.app.page.builders;

import java.util.Map;

import com.sonic.website.app.page.vo.Item;
import com.sonic.website.app.page.vo.ItemData;
public class ItemTextAreaBuiler extends ItemBuiler<Item>{
	private ItemTextAreaBuiler(){
	}
	public final static ItemTextAreaBuiler INSTACNCE = new ItemTextAreaBuiler();

	@Override
	public Item changeItem(Map<String, String[]> params, Item item) {
		String[] name = params.get("name");
		String[] desc = params.get("desc");
		item.setData(new ItemData(name[0], desc[0]));
		return item;
	}
}
