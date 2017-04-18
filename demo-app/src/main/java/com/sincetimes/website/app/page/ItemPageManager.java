package com.sincetimes.website.app.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.sincetimes.website.app.page.builders.ItemBuiler;
import com.sincetimes.website.app.page.vo.Item;
import com.sincetimes.website.app.page.vo.ItemPage;
import com.sincetimes.website.app.page.vo.ItemPageProvider;
import com.sincetimes.website.app.page.vo.ItemPageProviderManager;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.manager.annotation.ManangerInject;
import com.sincetimes.website.core.common.support.LogCore;
@ManangerInject
@Component
public class ItemPageManager extends ManagerBase {
	
	/*模板*/
	private ItemPageProvider templateProvider = null;
	public static ItemPageManager inst() {
		return ManagerBase.inst(ItemPageManager.class);
	}

	@Override
	public void init() {
		templateProvider = ItemPageProviderManager.provider();
	}
	/**模板是否存在*/
	public boolean existPageTemplate(String templateId){
		return templateProvider.existItemPageById(templateId);
	}
	public List<Item> getItemsWithSort(String templateId, String id) {
		if(!existPageTemplate(templateId)){
			return new ArrayList<>();
		}
		return ItemPageProviderManager.provider(templateId).getItemsWithSort(id);
	}
	/** 编辑用 */
	public List<Item> getItemsWithSort4Edit(String templateId, String id) {
		if(!existPageTemplate(templateId)){
			return new ArrayList<>();
		}
		Map<String, Item> templateItems = ItemPageProviderManager.provider().getAllItems(templateId);
		Map<String, Item> contentItems = ItemPageProviderManager.provider(templateId).getAllItems(id);
		templateItems.putAll(contentItems);
		LogCore.BASE.debug("templateItems={}", templateItems);
		LogCore.BASE.debug("contentItems={}", contentItems);
		List<Item> items= new ArrayList<Item>(templateItems.values());
		items.sort(Comparator.comparing(Item::getCreateTime));
		return items;
	}
	/**不存在回返回null*/
	public ItemPage getItemPageById(String templateId, String id) {
		if(!existPageTemplate(templateId)){
			return null;
		}
		return ItemPageProviderManager.provider(templateId).getItemPageById(id);
	}
	
	public void saveOrUpdatePageItem(String templateId, String id, String key, byte type_id, Map<String, String[]> params) {
		if(!existPageTemplate(templateId)){
			return;
		}
		ItemPage page = getItemPageById(templateId, id);

		if(null == page){//新创建一个页面
			page = templateProvider.getItemPageById(templateId);
		}
		Item item = page.getItem(key);
		if(null == item){
			item = templateProvider.getItemPageById(templateId).getItem(key);
		}
		ItemType itemType = ItemType.getType(type_id);
		if(null != itemType){
			ItemBuiler<Item> builder = itemType.getBuilder();
			item = builder.buildItem(item, params);
		}
		page.setId(id);
		page.putItems(key, item);
	 	ItemPageProviderManager.provider(templateId).saveOrUpdateItemPage(page);
	}

	public boolean existItemPageById(String templateId, String id) {
		if(!existPageTemplate(templateId)){
			return false;
		}
		return ItemPageProviderManager.provider(templateId).existItemPageById(id);
	}

	public Map<String, ItemPage> getAllItemPages(String templateId) {
		if(!existPageTemplate(templateId)){
			return new HashMap<>();
		}
		return ItemPageProviderManager.provider(templateId).getAllItemPages();
	}
	

	public void saveOrUpdateItemPage(String templateId, ItemPage page) {
		if(!existPageTemplate(templateId)){
			return;
		}
		ItemPageProviderManager.provider(templateId).saveOrUpdateItemPage(page);
	}

	public String applyItemPageId(String templateId) {
		return ItemPageProviderManager.provider(templateId).applyItemPageId()+"";
	}

	public void removePageItem(String templateId, String id, String key) {
		ItemPage itemPage = ItemPageProviderManager.provider(templateId).getItemPageById(id);
		if(null == itemPage){
			return;
		}
		itemPage.removeItem(key);
		ItemPageProviderManager.provider(templateId).saveOrUpdateItemPage(itemPage);
	}

	public void visit(String templateId, String id) {
		if(!existPageTemplate(templateId)){
			return;
		}
		ItemPageProviderManager.provider(templateId).visit(id);
	}

	public void removePage(String templateId, String id) {
		ItemPageProviderManager.provider(templateId).deleteItemPage(id);
	}
}