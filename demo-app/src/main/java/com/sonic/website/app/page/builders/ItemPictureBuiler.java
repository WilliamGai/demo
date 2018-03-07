package com.sonic.website.app.page.builders;

import java.util.Map;

import com.sonic.website.app.page.vo.Item;
import com.sonic.website.core.common.support.Util;

public class ItemPictureBuiler extends ItemBuiler<Item>{
	private ItemPictureBuiler(){
	}
	public final static ItemPictureBuiler INSTACNCE = new ItemPictureBuiler();
	
	public Item changeItem(Map<String, String[]> params, Item item) {
		String[] ss = params.get(FILE_PARAM_TAG);
		if(Util.isEmpty(ss)){
			return item;
		}
		item.setData(ss[0]);
		return item;
	}
}
