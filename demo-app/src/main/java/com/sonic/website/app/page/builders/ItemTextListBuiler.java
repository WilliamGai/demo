package com.sonic.website.app.page.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sonic.website.app.page.vo.Item;
import com.sonic.website.core.common.support.Util;
public class ItemTextListBuiler extends ItemBuiler<Item>{
	private ItemTextListBuiler(){
	}
	public final static ItemTextListBuiler INSTACNCE = new ItemTextListBuiler();

	@Override
	public Item changeItem(Map<String, String[]> params, Item item) {
		List<String> list = item.getData();
		if(Util.isEmpty(list)){
			list = new ArrayList<>();
		}
		String[] desc = params.get("desc");
	
		if(Util.isEmpty(desc)){
			return item;
		}
		list.add(desc[0]);
		item.setData(list);
		return item;
	}
}
