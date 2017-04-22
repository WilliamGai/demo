package com.sincetimes.website.app.stats.flow;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.TimeToolNew;
import com.sincetimes.website.core.common.support.Util;

@Component
public class DataStatsManager extends ManagerBase {
	private static final String CONFIGMAP_FILE_NAME = "configmap.os";
	public Map<String, DataStatsConfig> configMap = new HashMap<>();
	public Map<String, List<Map<String,Object>>> allDataMap = new ConcurrentHashMap<>();
	public Map<String, Long> timeFlagMap = new ConcurrentHashMap<>();
	private Lock loadLock = new ReentrantLock();
	@Autowired
	DataStatsJdbcService service;
	public static DataStatsManager inst() {
		return ManagerBase.inst(DataStatsManager.class);
	}
	
	@Override
	public void init() {
		initConigMap();
		LogCore.BASE.info("config map={}", configMap);
	}
	public Set<String> getAllConfigKeys(){
		return configMap.keySet();
	}
	public Collection<DataStatsConfig> getAllConfigs(){
		return configMap.values();
	}
	public DataStatsConfig getConfig(String id){
		return configMap.getOrDefault(id, new DataStatsConfig(id, "undifined"));
	}
	public Object loadAll(String id) {
		DataStatsConfig config = getConfig(id);
		if(null == config){
			return null;
		}
		StopWatch stopWatch = new StopWatch("upfile");
		stopWatch.start("mysql query");
		
		List<Map<String, Object>> queryResult = service.getAllData(config.getTableName(), null ,null);
		if(Util.isEmpty(queryResult)){
			return null;
		}
		List<Map<String, Object>> oldList = allDataMap.computeIfAbsent(id, (key)->new LinkedList<>());
		oldList.addAll(queryResult);
		stopWatch.stop();
//		data.parallelStream().forEach(row->{
//			Map<String, String> map = new HashMap<>();
//			row.forEach((k, v)->map.put(k, v+""));
//			datalist.add(map);
//			String key = map.get("id");
//			if(!exist(key)){
//				hmset(key, map);
//			}
//		});
		stopWatch.start("reduce ");
		stopWatch.stop();
		LogCore.BASE.info("load All time used:{}", stopWatch.prettyPrint());
		return stopWatch.prettyPrint();
	}
	/**
	 *  分页 <br>
	 * @param id
	 * @param skipNum
	 * @param pageSize
	 * @param filters
	 * @return
	 * TODO:
	 * 支持分组 group
	 * 支持求和 sum
	 */
	public List<Map<String, Object>> queryPayFlowBySkipLimit(String id, int skipNum, int pageSize, List<FilterItem> filters) {
		checkLoad(id);
		return allDataMap.get(id).stream().filter((map)->{
			return filters.stream().allMatch((FilterItem filter)->filter.filter(map));
		})
		.skip(skipNum)
	    .limit(pageSize)
		.collect(Collectors.toList());
	}
	/**
	 * 每次查询钱都要调用
	 */
	private void checkLoad(String id) {
		loadLock.lock();
		try {
			Long now = System.currentTimeMillis();
			Long lastRecordTime = timeFlagMap.put(id, now);
			if(null == lastRecordTime){
				loadAll(id);
				return;
			}
			long interval = now - lastRecordTime;
			if(interval > TimeToolNew.HOUR_SECONDS/2 * 1000)//半小时刷新
			if(Util.isEmpty(allDataMap)){
				loadAll(id);
				return;
			}
		} finally{
			loadLock.unlock();
		}
	}
	/** 获取总数目*/
	public long queryCount(String id, List<FilterItem> filters){
		checkLoad(id);
		return allDataMap.get(id).parallelStream().filter((map)->{
			return filters.stream().allMatch((FilterItem filter)->filter.filter(map));
		}).count();
	}
	public List<Map<String, Object>> queryPayFlow(String id, List<FilterItem> list) {
		checkLoad(id);
		return allDataMap.get(id).stream().filter((map)->{
			return list.stream().allMatch((FilterItem filter)->filter.filter(map));
		}).collect(Collectors.toList());
	}
	@SuppressWarnings("unchecked")
	public void initConigMap(){
		configMap = Util.readObject(CONFIGMAP_FILE_NAME);
	}
	public void saveOrUpdateConfig(DataStatsConfig config) {
		configMap.put(config.getId(), config);
		Util.writeObject(configMap, CONFIGMAP_FILE_NAME);
	}

	public long querySum(String id, List<FilterItem> filters) {
		if(Util.isEmpty(id)){
			return 0;
		}
		DataStatsConfig config = DataStatsManager.inst().getConfig(id);
		if(null == config){
			return 0;
		}
		return allDataMap.get(id).parallelStream().filter((map)->{
				return filters.stream().allMatch((FilterItem filter)->filter.filter(map));
			}).mapToInt(map->(Integer)map.get(config.getSumName())).sum();
		
	}
}
