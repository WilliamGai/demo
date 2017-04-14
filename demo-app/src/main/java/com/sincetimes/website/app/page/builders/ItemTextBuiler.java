package com.sincetimes.website.app.page.builders;

import java.util.Map;

import com.sincetimes.website.app.page.vo.Item;
public class ItemTextBuiler extends ItemBuiler<Item>{
	private ItemTextBuiler(){
	}
	public final static ItemTextBuiler INSTACNCE = new ItemTextBuiler();

	@Override
	public Item changeItem(Map<String, String[]> params, Item item) {
		String[] s = params.get("desc");
		item.setData(s[0]);
		return item;
	}
}
