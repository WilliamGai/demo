package com.sincetimes.website.app.page.vo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.manger.SpringManager;
import com.sincetimes.website.redis.jedis.interfaces.JedisWrapper;
import com.sincetimes.website.redis.jedis.spring.JedisWrapperBase;

/***
 * 页面
 * sorted set<name,0>
 * dict <id, hash>
 * <br>
 */
public class ItemPageProviderBase extends JedisWrapperBase{
	private static final String PAGES_SET = "pages^set";
	private static final String HASH_FILED_PAGE = "page";
	private static final String HASH_FILED_PAGE_VISITS = "visits";//访问次数
	
	public static ItemPageProviderBase inst() {
		return SpringManager.inst().getBean(ItemPageProviderBase.class);
	}
	
	/**
	 * 模板页面
	 * @param itemPage
	 */
	public void saveOrUpdateItemPage(ItemPage itemPage) {
		zadd(PAGES_SET, 0, itemPage.getId());
		hset(itemPage.getId(), HASH_FILED_PAGE, itemPage.toJSONString());
	}

	public ItemPage getItemPageById(String id) {
		if(!exist(id)){
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

	public Boolean existItemPageById(String id) {
		return exist(id);
	}
}
