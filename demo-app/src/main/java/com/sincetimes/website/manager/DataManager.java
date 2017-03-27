package com.sincetimes.website.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sincetimes.website.core.common.cache.MyCache;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.redis.jedis.spring.JedisManagerBase;
/**
 * 书写示例
 */
@Component
public class DataManager extends JedisManagerBase {
	
	/**
	 * 所有以VALUE打头的静态字符串将被引入后台管理
	 * 所有以LIST_NAME打头的会出现在统计信息
	 */
	public static final String VALUE_YULAN_URL = "default_yulan_url";
	public static final String VALUE_DOWNLOAD_URL = "download_url";
	public static final String VALUE_DOWNLOAD_URL_IOS = "download_url_ios";
	public static final String VALUE_DOWNLOAD_URL_ANDROID = "download_url_android";
	
	public MyCache<String, String[]> userBiInfoCache = new MyCache<String, String[]>(50000, 4);
	
	public static DataManager inst() {
		return ManagerBase.inst(DataManager.class);
	}
	
	@Override
	public void init() {
		
	}
}
