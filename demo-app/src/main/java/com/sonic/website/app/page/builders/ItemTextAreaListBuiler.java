package com.sonic.website.app.page.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sonic.website.app.page.vo.Item;
import com.sonic.website.app.page.vo.ItemData;
import com.sonic.website.core.common.support.Util;
public class ItemTextAreaListBuiler extends ItemBuiler<Item>{
    private ItemTextAreaListBuiler(){
    }
    public final static ItemTextAreaListBuiler INSTACNCE = new ItemTextAreaListBuiler();

    @Override
    public Item changeItem(Map<String, String[]> params, Item item) {
        List<ItemData> list = item.getData();
        if(Util.isEmpty(list)){
            list = new ArrayList<>();
        }
        String[] name = params.get("name");
        String[] desc = params.get("desc");
    
        if(Util.isEmpty(name) || Util.isEmpty(desc)){
            return item;
        }
        list.add(new ItemData(name[0], desc[0]));
        item.setData(list);
        return item;
    }
}
