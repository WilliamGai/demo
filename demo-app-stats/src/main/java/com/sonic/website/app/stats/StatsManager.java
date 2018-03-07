package com.sonic.website.app.stats;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.core.common.support.DataVO;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.manager.DataManager;
/**
 * TODO:放队列里处理
 */
@Component
public class StatsManager extends ManagerBase {

	private static final int NAOS_1000000 = 1000000;
	private static final DecimalFormat df = new DecimalFormat("#.####");//保留小数点后4位
	
	@Value("${stats_load_histoy}")
	private boolean stats_load_histoy;

	public static final String STATS_TG_count = "人次";
	public static final String STATS_TG_num = "人数";
	public static final String STATS_TG_new = "新增";

	public static final String STATS_TG_code = "激活码";
	//

	public static final String SCOPE_STATS_URI_TG_time = "stats:uri:time";
	public static final String SCOPE_STATS_URI_TG_count = "stats:uri:count";

	public static final String SCOPE_STATS_VALUE = "stats:value:";// 所有键值都放到此域下
	public static final String LIST_NAME = "stats:usrtime";// 记录uri访问时间的列表
	public static final String MAP_NAME = "stats:timemap";// 记录uri访问时间的列表
	public static final String URI_SET_KEY = "stats:uriset";// 记录uri集合

	public Map<String, Long> sn_time_map = new ConcurrentHashMap<>();
	public Map<String, Long> sn_time_map_all = new ConcurrentHashMap<>();
	// URI,访问统计,本地
	public final Map<String, Long> STATS_URI_TIME_MAP = new ConcurrentHashMap<>();
	public final Map<String, Integer> STATS_URI_COUNT_MAP = new ConcurrentHashMap<>();

	public static StatsManager inst() {
		return ManagerBase.inst(StatsManager.class);
	}
	public void clearStatsUri(){
		STATS_URI_TIME_MAP.clear();
		STATS_URI_COUNT_MAP.clear();
	}
	public void statsUri(String uri, long interval) {
		STATS_URI_TIME_MAP.merge(uri, interval, Long::sum);
		STATS_URI_COUNT_MAP.merge(uri, 1, Integer::sum);
	}
	/**优化项**/
	@Override
	public void init() {
		if(!stats_load_histoy){
			LogCore.BASE.info("StatsManager init return, stats_load_histoy={}", stats_load_histoy);
			return;
		}
	}


/*	public List<DataVO> getConstantStats() {
		Map<String, Object> map = ClassTool.getFields(DataManager.inst(), x -> x.getName().startsWith("VALUE"));
		List<DataVO> list = new ArrayList<>();
		map.values().forEach(x -> list.add(new DataVO(x + "", DataManager.inst().get(x + ""))));
		LogCore.BASE.debug("all constants :{}", list);
		return list;
	}*/

	/**
	 * 获取基本信息
	 */
	public List<DataVO> getBaseInfos() {
		List<DataVO> base_info_list = SysInfos();
		// jedis
		base_info_list.add(new DataVO("sn_time_map_all.size", Util.size(sn_time_map_all)));
		base_info_list.add(new DataVO("sn_time_map.size", Util.size(sn_time_map)));
		base_info_list.add(new DataVO("LRU cache size", DataManager.inst().userBiInfoCache.size()));
		base_info_list.add(new DataVO("LRU cache hit rate", DataManager.inst().userBiInfoCache.getStats().hitRate()));
		base_info_list.add(new DataVO("LRU cache all stats", DataManager.inst().userBiInfoCache.getStats()));

		return base_info_list;
	}
	private List<DataVO> SysInfos() {
		List<DataVO> base_info_list = new ArrayList<>();
		long maxM = Runtime.getRuntime().maxMemory();
		long totalM = Runtime.getRuntime().totalMemory();
		long freeM = Runtime.getRuntime().freeMemory();
		long usedM = totalM - freeM;
		base_info_list.add(new DataVO("path", System.getProperty("user.dir")));
		base_info_list.add(new DataVO("maxM", Util.getM(maxM)));
		base_info_list.add(new DataVO("totalM", Util.getM(totalM)));
		base_info_list.add(new DataVO("freeM", Util.getM(freeM)));
		base_info_list.add(new DataVO("usedM", Util.getM(usedM)));
		return base_info_list;
	}


	public List<DataVO> getUriStatsThisTime() {
		List<DataVO> uri_list = new ArrayList<>();
		AtomicLong a_count = new AtomicLong();
		AtomicLong a_time = new AtomicLong();
		STATS_URI_TIME_MAP.keySet().forEach(sn -> {
			long _time = STATS_URI_TIME_MAP.get(sn);
			int _count = STATS_URI_COUNT_MAP.get(sn);
			double avTime = (double) _time / _count;
			uri_list.add(new DataVO(sn, _time/NAOS_1000000, df.format(avTime / NAOS_1000000), _count));
			//总计
			a_time.addAndGet(_time);
			a_count.addAndGet(_count);
		});
		double _avtime_all = (double)a_time.get() / a_count.get();
		uri_list.add(new DataVO("总计",a_time.get() / NAOS_1000000, df.format(_avtime_all / NAOS_1000000), a_count.get()));
		return uri_list;
	}
}
