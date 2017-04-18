package com.sincetimes.website.app.page;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sincetimes.website.app.page.vo.Item;
import com.sincetimes.website.app.page.vo.ItemPage;
import com.sincetimes.website.app.page.vo.ItemPageProvider;
import com.sincetimes.website.app.page.vo.ItemPageProviderManager;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.manager.annotation.ManangerInject;
@ManangerInject
@Component
public class ItemPageTemplateManager extends ManagerBase {
	
	/**模板*/
	private ItemPageProvider templateProvider = null;
	public static ItemPageTemplateManager inst() {
		return ManagerBase.inst(ItemPageTemplateManager.class);
	}

	@Override
	public void init() {
		templateProvider = ItemPageProviderManager.provider();
	}
	
	public List<Item> getItemsWithSort(String id) {
		return templateProvider.getItemsWithSort(id);
	}
	
	public void saveOrUpdatePageItem(String id, String key, byte type_id, Map<String, String[]> params) {
		ItemPage page = templateProvider.getItemPageById(id);
		//new
		if(null == page){
			page = templateProvider.getItemPageById(id);
		}
		templateProvider.saveOrUpdateItemPage(page);
	}

	public ItemPage getItemPageById(String id) {
		return templateProvider.getItemPageById(id);
	}

	public Map<String, ItemPage> getAllItemPages() {
		return templateProvider.getAllItemPages();
	}

	public void saveOrUpdateItemPage(ItemPage page) {
		templateProvider.saveOrUpdateItemPage(page);
	}

	public void removeItem(String id, String key) {
		ItemPage itemPage = templateProvider.getItemPageById(id);
		if(null == itemPage){
			return;
		}
		itemPage.removeItem(key);
		templateProvider.saveOrUpdateItemPage(itemPage);
	}

	public void removePage(String id) {
		templateProvider.deleteItemPage(id);
	}
}