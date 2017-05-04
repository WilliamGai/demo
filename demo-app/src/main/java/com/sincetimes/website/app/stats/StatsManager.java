package com.sincetimes.website.app.stats;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sincetimes.website.app.websocket.WebSocket;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.port.PortInstance;
import com.sincetimes.website.core.common.support.ClassTool;
import com.sincetimes.website.core.common.support.DataVO;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.manager.DataManager;
/**
 * TODO:放队列里处理
 */
@Component
public class StatsManager extends ManagerBase {

	private static final int NAOS_1000000 = 1000000;
	private static final DecimalFormat df = new DecimalFormat("#.####");//保留小数点后4位
	
	@Autowired
	@Qualifier("StatsServiceJedisImpl")
	private StatsService dataService;
	
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
		String date_tag = TimeTool.formatTime(System.currentTimeMillis(), "yyyyMMdd");
		// 加载今天的登陆openid
		long time_today_begin;
		try {
			time_today_begin = TimeTool.parseAndGetTime(date_tag, "yyyyMMdd");
			PortInstance.inst().addQueue(p->{
				Map<String, Long> sn_time_map_all_temp = dataService.getRecordList2Map(LIST_NAME, x -> true);
				Map<String, Long> sn_time_map_temp = dataService.getRecordList2Map(LIST_NAME, x -> x.getTime() > time_today_begin);
				sn_time_map_all.putAll(sn_time_map_all_temp);
				sn_time_map.putAll(sn_time_map_temp);
				LogCore.BASE.info("StatsManager init, STATS_TIME_TG={},sn_time_map.size={},sn_time_map_all.size={}", date_tag,
						Util.size(sn_time_map_temp), Util.size(sn_time_map_all_temp));
			});
		} catch (Exception e) {
			LogCore.BASE.error("init{},", e);
		}
	}

	/*** 查询 **/
	public String get(String name) {
		return dataService.get(SCOPE_STATS_VALUE.concat(name));
	}

	/*** 写入或增加 */
	public void incr(String name) {
		dataService.incr(SCOPE_STATS_VALUE.concat(name));
	}

	/*** 写入或增加 */
	public void incrBy(String name, long value) {
		dataService.incrBy(SCOPE_STATS_VALUE.concat(name), value);
	}

	/**
	 * 
	 * @param key,主键set名称
	 * @param name
	 * @param value
	 * TODO,zset记录次数，hset记录时间
	 */
	public void statsUriByDb(String uri, long interval) {
		dataService.incr(SCOPE_STATS_URI_TG_count.concat(uri));
		dataService.incrBy(SCOPE_STATS_URI_TG_time.concat(uri), interval);
		dataService.zadd(URI_SET_KEY, 0, uri);
	}
	public int getUriCount(String uri){
		String count = dataService.get(SCOPE_STATS_URI_TG_count.concat(uri));
		return null == count? 0:Integer.parseInt(count);
	}
	
	public List<DataVO> getTimeStats() {
		return dataService.hGetAll2List(MAP_NAME, (k, v) -> new DataVO(Util.getUtf(k), Util.getUtf(v)));
	}

	/**
	 * 获取常量信心
	 */
	public List<DataVO> getConstantStats() {
		Map<String, Object> map = ClassTool.getFields(DataManager.inst(), x -> x.getName().startsWith("VALUE"));
		List<DataVO> list = new ArrayList<>();
		map.values().forEach(x -> list.add(new DataVO(x + "", DataManager.inst().get(x + ""))));
		LogCore.BASE.debug("all constants :{}", list);
		return list;
	}

	/**
	 * 获取基本信息
	 */
	public List<DataVO> getBaseInfos() {
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
		// jedis
		base_info_list.add(new DataVO("sn_time_map_all.size", Util.size(sn_time_map_all)));
		base_info_list.add(new DataVO("sn_time_map.size", Util.size(sn_time_map)));
		base_info_list.add(new DataVO("websockets.mapsize", WebSocket.OPENID_MAP.size()));
		base_info_list.add(new DataVO("websockets.count", WebSocket.ONLINE_COUNT.get()));
		base_info_list.add(new DataVO("LRU cache size", DataManager.inst().userBiInfoCache.size()));
		base_info_list.add(new DataVO("LRU cache hit rate", DataManager.inst().userBiInfoCache.getStats().hitRate()));
		base_info_list.add(new DataVO("LRU cache all stats", DataManager.inst().userBiInfoCache.getStats()));

		List<DataVO> size_list = getListSizes();
		base_info_list.addAll(size_list);
		return base_info_list;
	}

	// 获取列表大小
	public List<DataVO> getListSizes() {
		Map<String, Object> map = ClassTool.getFields(DataManager.inst(), x -> x.getName().startsWith("LIST_NAME"));
		List<DataVO> size_list = new ArrayList<>();
		map.values().forEach(x -> size_list.add(new DataVO(x + "", DataManager.inst().llen(x + ""))));
		return size_list;
	}

	public List<DataVO> getUriStatsAllTime() {
		List<DataVO> list = new ArrayList<>();
		Set<String> keySet = dataService.zRange(URI_SET_KEY);
		AtomicLong a_count = new AtomicLong();
		AtomicLong a_time = new AtomicLong();
		keySet.forEach(name -> {
			String count = dataService.get(SCOPE_STATS_URI_TG_count.concat(name));
			String time = dataService.get(SCOPE_STATS_URI_TG_time.concat(name));
			int _count = Integer.parseInt(count);
			long _time = Long.parseLong(time);
			double _avtime = (double)_time / _count;
			list.add(new DataVO(name,_time / NAOS_1000000, df.format(_avtime / NAOS_1000000), _count));
			//总计
			a_time.addAndGet(_time);
			a_count.addAndGet(_count);
		});
		double _avtime_all = (double)a_time.get() / a_count.get();
		list.add(new DataVO("总计",a_time.get() / NAOS_1000000, df.format(_avtime_all / NAOS_1000000), a_count.get()));
		return list;
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
	/**
	 * 
	 * 统计某个变化的值
	 * 
	 * @param tag
	 *            必须是此类的常量
	 */
	public void countByDay(String tag) {
		String date_tag = TimeTool.formatTime(System.currentTimeMillis(), "yyyyMMdd");
		dataService.hIncrBy(MAP_NAME, date_tag.concat(tag), 1);
	}

	/*** 每天新增,每天的访问的人数，每天访问人次 **/
	public void countOpenid(String openid) {
		String date_tag = TimeTool.formatTime(System.currentTimeMillis(), "yyyyMMdd");
		// 访问记录
		dataService.addRecordList(LIST_NAME, openid);
		// 每日访问人次
		dataService.hIncrBy(MAP_NAME, date_tag + STATS_TG_count, 1);

		// 每日新
		if (!sn_time_map_all.containsKey(openid)) {
			dataService.hIncrBy(MAP_NAME, date_tag + STATS_TG_new, 1);
			sn_time_map_all.put(openid, System.currentTimeMillis());
		}
		// 每日人数
		if (!sn_time_map.containsKey(openid)) {
			dataService.hIncrBy(MAP_NAME, date_tag + STATS_TG_num, 1);
			sn_time_map.put(openid, System.currentTimeMillis());
		}
		LogCore.RECORD.debug(openid);
	}
}
