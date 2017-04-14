package com.sincetimes.website.app.page;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sincetimes.website.app.page.builders.ItemBuiler;
import com.sincetimes.website.app.page.vo.Item;
import com.sincetimes.website.app.page.vo.ItemPage;
import com.sincetimes.website.app.page.vo.ItemPageProvider;
import com.sincetimes.website.app.page.vo.PageProviderManager;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.manager.annotation.ManangerInject;
import com.sincetimes.website.core.common.support.LogCore;
@ManangerInject
@Component
public class PageManager extends ManagerBase {
	
	/*模板*/
	private ItemPageProvider templateProvider = null;
	public static PageManager inst() {
		return ManagerBase.inst(PageManager.class);
	}

	@Override
	public void init() {
		templateProvider = PageProviderManager.provider();
	}
	/**模板是否存在*/
	public boolean existPageTemplate(String templateId){
		return templateProvider.existItemPageById(templateId);
	}
	public List<Item> getItemsWithSort(String templateId, String id) {
		if(!existPageTemplate(templateId)){
			return new ArrayList<>();
		}
		return PageProviderManager.provider(templateId).getItemsWithSort(id);
	}
	/** 编辑用 */
	public List<Item> getItemsWithSort4Edit(String templateId, String id) {
		if(!existPageTemplate(templateId)){
			return new ArrayList<>();
		}
		Map<String, Item> templateItems = PageProviderManager.provider().getAllItems(templateId);
		Map<String, Item> contentItems = PageProviderManager.provider(templateId).getAllItems(id);
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
		return PageProviderManager.provider(templateId).getItemPageById(id);
	}
	
	public void saveOrUpdatePageItem(String templateId, String id, String key, byte type_id, Map<String, String[]> params) {
		if(!existPageTemplate(templateId)){
			return;
		}
		ItemPage page = PageProviderManager.provider(templateId).getItemPageById(id);

		if(null == page){//新创建一个页面
			page = templateProvider.getItemPageById(templateId);
		}
		Item item = page.getItem(key);;
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
	 	PageProviderManager.provider(templateId).saveOrUpdateItemPage(page);
	}

	public boolean existItemPageById(String templateId, String id) {
		if(!existPageTemplate(templateId)){
			return false;
		}
		return PageProviderManager.provider(templateId).existItemPageById(id);
	}

	public Map<String, ItemPage> getAllItemPages(String templateId) {
		if(!existPageTemplate(templateId)){
			return new HashMap<>();
		}
		return PageProviderManager.provider(templateId).getAllItemPages();
	}

	public void saveOrUpdateItemPage(String templateId, ItemPage page) {
		if(!existPageTemplate(templateId)){
			return;
		}
		PageProviderManager.provider(templateId).saveOrUpdateItemPage(page);
	}

	public Long applyItemPageId(String templateId) {
		return PageProviderManager.provider(templateId).applyItemPageId();
	}

	public void removePageItem(String templateId, String id, String key) {
		ItemPage itemPage = PageProviderManager.provider(templateId).getItemPageById(id);
		if(null == itemPage){
			return;
		}
		itemPage.removeItem(key);
		PageProviderManager.provider(templateId).saveOrUpdateItemPage(itemPage);
	}
}