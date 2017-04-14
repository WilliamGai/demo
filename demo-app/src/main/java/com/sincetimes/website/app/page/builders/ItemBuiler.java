package com.sincetimes.website.app.page.builders;

import java.util.Map;

import com.sincetimes.website.app.page.ItemType;
import com.sincetimes.website.app.page.vo.Item;
import com.sincetimes.website.core.common.support.LogCore;
/**
 * GOF.3/4 FactoryMethod TemplateMethod
 * @author BAO
 *
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
	public abstract Item changeItem(Map<String, String[]> params, Item item);
	
	public Item buildItem(String key, String name, ItemType itemType, Map<String, String[]> params) {
		Item item = new Item(key, name, itemType);
		return changeItem(params, item);
	}
	/**创建模板或者文章的Item的副本*/
	public Item buildItem(Item item, Map<String, String[]> params) {
		LogCore.BASE.debug("params=", params);
		Item newItem = item.createClone();
		return changeItem(params, newItem);
	}
}
