package com.sonic.website.app.page.builders;

import java.util.Map;

import com.sonic.website.app.page.ItemType;
import com.sonic.website.app.page.vo.Item;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;
/**
 * GOF.3/4 FactoryMethod TemplateMethod
 * @author BAO
 * @param <T>
 */
public abstract class ItemBuiler<T extends Item> {
    public static final String FILE_PARAM_TAG = "filepath";

    /**
     * 子类实现 
     * @param params 参数表
     * @param newItem 要修改的Item
     * @return 返回修改后的newItem
     */
    public abstract Item changeItem(Map<String, String[]> params, final Item item);
    
    public Item buildItem(String key, String name, ItemType itemType, Map<String, String[]> params) {
        Item item = new Item(key, name, itemType);
        return changeItem(params, item);
    }
    /**创建模板或者文章的Item的副本*/
    public Item buildItem(final Item item, Map<String, String[]> params) {
        LogCore.BASE.debug("params={}", Util.prettyJsonStr(params));
        return changeItem(params, item.createClone());
    }
}
