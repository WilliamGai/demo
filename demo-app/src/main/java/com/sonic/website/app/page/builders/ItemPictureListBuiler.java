package com.sonic.website.app.page.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sonic.website.app.page.vo.Item;
import com.sonic.website.core.common.support.Util;

public class ItemPictureListBuiler extends ItemBuiler<Item>{
	private ItemPictureListBuiler(){
	}
	public final static ItemPictureListBuiler INSTACNCE = new ItemPictureListBuiler();
	
	public Item changeItem(Map<String, String[]> params, Item item) {
		String[] ss = params.get(FILE_PARAM_TAG);
		if(Util.isEmpty(ss)){
			return item;
		}
		
		List<String> list = item.getData();
		if(Util.isEmpty(list)){
			list = new ArrayList<>();
		}
		
		list.add(ss[0]);
		item.setData(list);
		return item;
	}
}
