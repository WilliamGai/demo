package com.sincetimes.website.app.page.vo;

import java.util.HashMap;
import java.util.Map;

public class ItemPageProviderManager {
	private static final Map<String, ItemPageProvider> instances = new HashMap<>();
	public static ItemPageProvider provider(String name){
		return instances.computeIfAbsent(name, (k)->createProviderCopyWithSubspace(k));
	}
	/**创建副本*/
	private static ItemPageProvider createProviderCopyWithSubspace(String key){
		return provider().createClone(t->t.setSubSpace(key));
	}
	/**默认来源*/
	public static ItemPageProvider provider(){
		return ItemPageProvider.inst();
	}
}
