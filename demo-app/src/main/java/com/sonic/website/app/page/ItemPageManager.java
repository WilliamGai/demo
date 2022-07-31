package com.sonic.website.app.page;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.sonic.website.app.page.builders.ItemBuiler;
import com.sonic.website.app.page.vo.Item;
import com.sonic.website.app.page.vo.ItemPage;
import com.sonic.website.app.page.vo.ItemPageProvider;
import com.sonic.website.app.page.vo.ItemPageProviderManager;
import com.sonic.website.core.common.manager.ManagerBase;
public class ItemPageManager extends ManagerBase{
    
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
        /* fatal err like this:templateItems.putAll(contentItems)*/
        Map<String, Item> tempMap = new HashMap<>(templateItems);
        tempMap.putAll(contentItems);
        List<Item> items= new ArrayList<Item>(tempMap.values());
        items.sort(Comparator.comparing(Item::getCreateTime));
        return items;
    }
    /**
     * @return nullable
     */
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
        Item item = null;
        if(null == page){//新创建一个页面
            page = new ItemPage(id, null);
        }
        item = page.getItem(key);
        if(null == item){
            Item templateItem = templateProvider.getItemPageById(templateId).getItem(key);
            item = templateItem.createClone();
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
    /**
     * 推荐使用 {@link ItemPageManager#getAllItemPagesWithSort(String)}
     * @param templateId
     */
    @Deprecated
    public Map<String, ItemPage> getAllItemPages(String templateId) {
        if(!existPageTemplate(templateId)){
            return new HashMap<>();
        }
        return ItemPageProviderManager.provider(templateId).getAllItemPages();
    }
    public List<ItemPage> getAllItemPagesWithSort(String templateId) {
        if(!existPageTemplate(templateId)){
            return new ArrayList<>();
        }
        //ItemPage::getWeight
        Function<ItemPage, Integer> comp = (p)->Integer.parseInt(p.getId());
        return ItemPageProviderManager.provider(templateId).getAllItemPagesWithSort(comp);
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