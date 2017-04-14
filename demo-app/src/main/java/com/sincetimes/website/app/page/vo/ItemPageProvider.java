package com.sincetimes.website.app.page.vo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sincetimes.website.core.common.support.CloneableSupport;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.manger.SpringManager;
import com.sincetimes.website.redis.jedis.interfaces.JedisWrapper;
import com.sincetimes.website.redis.jedis.spring.JedisWrapperBase;
/***
 * 页面
 * 通过继承此类可以使数据在不同的域,但是需要将不同的模板类型下面的页面实例分布在不同的域,又因为类是spring注入的。因此使用clone来实现
 * sorted set<name,0>
 * dict <id, hash>
 * <br>
 */
@Component
public class ItemPageProvider extends JedisWrapperBase implements CloneableSupport<ItemPageProvider>{
	private static final String PAGES_SET = "pages^set";
	private static final String HASH_FILED_PAGE = "page";
	private static final String HASH_FILED_PAGE_VISITS = "visits";//访问次数
	private static final String KEY_LAST_ITEM_PAGE_ID = "last_item_page_id";

	public String subSpace = "";//域
	public void setSubSpace(String subSpace){
		this.subSpace = subSpace;
	}
	/***覆盖接口的default方法*/
	@Override
	public String makeKey(String key){
		if(!Util.isEmpty(subSpace)){
			key = subSpace.concat(":").concat(key);
		}
		return super.makeKey(key);
	}
	public static ItemPageProvider inst() {
		return SpringManager.inst().getBean(ItemPageProvider.class);
	}
	/**
	 * 模板页面
	 * @param itemPage
	 */
	public void saveOrUpdateItemPage(ItemPage itemPage) {
		zadd(PAGES_SET, 0, itemPage.getId());
		hset(itemPage.getId(), HASH_FILED_PAGE, itemPage.toJSONString());
	}
	/**
	 * @See {@link ItemPageProvider#existItemPageById(String)}
	 */
	public ItemPage getItemPageById(String id) {
		if(!existItemPageById(id)){
			return null;
		}
		List<String> results = hmget(id, HASH_FILED_PAGE, HASH_FILED_PAGE_VISITS);
		LogCore.BASE.info("getItemPageById results:{}", results);
		ItemPage page = ItemPage.parseObject(results.get(0), ItemPage.class);
		long visits = Util.toEmpyDefalut(results.get(1), Long::parseLong, 0L);
		page.setVisits(visits);
		return page;
	}
	/**
	 * @see JedisWrapper#zcard
	 */
	public Long getItemPagesNum(){
		return zcard(PAGES_SET);
	}
	
	public Map<String, ItemPage> getAllItemPages() {
		Set<String> _set = zrange(PAGES_SET, 0, -1);
		return _set.stream()
				.map(this::getItemPageById)
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(ItemPage::getId, Function.identity()));
	}

	public void deleteItemPage(String id) {
		zrem(PAGES_SET, id);
		del(id);
	}
	/**
	 * @param id nullable
	 * @return
	 */
	public Boolean existItemPageById(String id) {
		if(Util.isEmpty(id)){
			return false;
		}
		return exist(id);
	}
	/** 按照时间排好序 */
	public List<Item> getItemsWithSort(String id){
		if(!existItemPageById(id)){
			return new ArrayList<>();
		}
		ItemPage page = getItemPageById(id);
		List<Item> items= new ArrayList<Item>(page.getItems().values());
		items.sort(Comparator.comparing(Item::getCreateTime));
		return items;
	}
	public Map<String, Item> getAllItems(String id) {
		if(!existItemPageById(id)){
			return new HashMap<>();
		}
		return getItemPageById(id).getItems();
	}
	@Override
	public Object cloneThis() throws CloneNotSupportedException {
		return clone();
	}
	public Long applyItemPageId() {
		return incr(KEY_LAST_ITEM_PAGE_ID);
	}

}
