package com.sincetimes.website.app.page.vo;

import java.io.Serializable;
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

import com.sincetimes.website.core.common.cache.MyCache;
import com.sincetimes.website.core.common.support.CloneableSupport;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.manger.SpringManager;
import com.sincetimes.website.redis.jedis.interfaces.JedisWrapper;
import com.sincetimes.website.redis.jedis.spring.JedisWrapperBase;
/***
 * 页面操作内容提供者
 * ItemPageProvider是spring注入的。用clone来创建控制不同域的ItemPageProvider实例
 * sorted set<name,0>
 * dict <id, hash>
 * <br>
 * @see CloneableSupport
 * @see ItemPage
 * @see MyCache
 * @see<a href="https://github.com/WilliamGai/demo/tree/master/demo-app/doc"/>redis存储对象</a>
 */
@Component
public class ItemPageProvider extends JedisWrapperBase implements CloneableSupport<ItemPageProvider>{
	private static final String PAGES_SET = "pages^set";
	private static final String HASH_FILED_PAGE = "page";
	private static final String HASH_FILED_PAGE_VISITS = "visits";//访问次数
	private static final String KEY_LAST_ITEM_PAGE_ID = "last_item_page_id";//页面自增ID

	public String subSpace = "";//域
	public MyCache<String, ItemPage> pageCache = new MyCache<>(1000, 4);

	public ItemPageProvider setSubSpace(String subSpace){
		this.subSpace = subSpace;
		return this;
	}
	/** TODO:缓存*/
	public ItemPageProvider init(){
		return this;
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
	/*重要方法*/
	public ItemPage deleteItemPage(String id) {
		return pageCache.removeValue(id, this::_deleteItemPage);
	}
	private void _deleteItemPage(String id) {
		zrem(PAGES_SET, id);
		del(id);
	}
	/** 
	 * @see ItemPageProvider#_saveOrUpdateItemPage(String, ItemPage) 
	 */
	public void saveOrUpdateItemPage(ItemPage itemPage) {
		pageCache.putValue(itemPage.getId(), itemPage, this::_saveOrUpdateItemPage);
	}
	/***
	 * 页面的持久化<br>
	 * 分开存储的方式{@code hmset(itemPage.getId(), itemPage.createItemsStringMap());}
	 * @param itemPage 要存储的页面
	 */
	private void _saveOrUpdateItemPage(String id, ItemPage itemPage){
		zadd(PAGES_SET, 0, itemPage.getId());
		hset(itemPage.getId(), HASH_FILED_PAGE, itemPage.toJSONString());
	}
	/**
	 * @See {@link ItemPageProvider#_getItemPageById(String)}
	 */
	public ItemPage getItemPageById(String id) {
		return pageCache.getValue(id, this::_getItemPageById);
	}
	
	/**
	 * @See {@link ItemPageProvider#existItemPageById(String)}
	 */
	private final ItemPage _getItemPageById(String id) {
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
	/*重要方法end*/
	/**
	 * @see JedisWrapper#zcard
	 */
	public Long getItemPagesNum(){
		return zcard(PAGES_SET);
	}
	/**
	 * @return not null
	 */
	public Map<String, ItemPage> getAllItemPages() {
		LogCore.BASE.debug("subSpace={},cache.size={}, cache.keys={}", subSpace, pageCache.asMap().size(), pageCache.asMap().keySet());
		Set<String> _set = zrange(PAGES_SET, 0, -1);
		if(Util.isEmpty(_set)){
			return new HashMap<>();
		}
		return _set.stream()
				.map(this::getItemPageById)
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(ItemPage::getId, Function.identity()));
	}
	public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor)
    {
        Objects.requireNonNull(keyExtractor);
        return (Comparator<T> & Serializable)
            (c1, c2) -> keyExtractor.apply(c1).compareTo(keyExtractor.apply(c2));
    }
	public <U extends Comparable<? super U>> List<ItemPage> getAllItemPagesWithSort(Function<ItemPage, ? extends Comparable<?>> keyExtractor) {
		Set<String> _set = zrange(PAGES_SET, 0, -1);
		if(Util.isEmpty(_set)){
			return new ArrayList<>();
		}
		return _set.stream()
				.map(this::getItemPageById)
				.filter(Objects::nonNull)
//				.sorted(Comparator.comparing(keyExtractor))
				.collect(Collectors.toList());
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
	public Long visit(String id) {
		if(!existItemPageById(id)){
			return -1L;
		}
		return hincrBy(id, HASH_FILED_PAGE_VISITS, 1L);
	}
	@Override
	public ItemPageProvider afterInit() {
		pageCache = new MyCache<>(1000, 4);
		return this;
	}
}
