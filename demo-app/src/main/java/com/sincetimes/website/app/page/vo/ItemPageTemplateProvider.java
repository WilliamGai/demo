package com.sincetimes.website.app.page.vo;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.sincetimes.website.core.spring.manger.SpringManager;

/***
 * 模板页面
 * sorted set<name,0>
 * dict <id, hash>
 * <br>
 */
@Component
public class ItemPageTemplateProvider extends ItemPageProviderBase{
	public static ItemPageProviderBase inst() {
		return SpringManager.inst().getBean(ItemPageTemplateProvider.class);
	}
}
