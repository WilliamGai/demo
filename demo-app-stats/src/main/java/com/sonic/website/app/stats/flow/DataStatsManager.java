package com.sonic.website.app.stats.flow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.core.common.serialize.File2ObjectUtil;
import com.sonic.website.core.common.serialize.IOUtil;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Sys;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.common.threadpool.LimitedThreadPool;

@Component
public class DataStatsManager extends ManagerBase {
	private static final String CONFIGMAP_FILE_NAME = "configmap.os";
	public Map<String, DataStatsConfig> configMap = new HashMap<>();
	public Map<String, LinkedList<Map<String,Object>>> allDataMap = new ConcurrentHashMap<>();
	public Map<String, Lock> locks = new HashMap<>();
	
	/*20万500M*/
	@Value("${limit:200000}")
	public int limit;
	
	@Autowired
	DataStatsJdbcService service;
	public static DataStatsManager inst() {
		return ManagerBase.inst(DataStatsManager.class);
	}
	
	@Override
	public void init() {
		try {
			initConigMap();
			initData();
		} catch (Exception e) {
			LogCore.BASE.error("init error{}", e);
		}
		LogCore.BASE.info("config map={}", configMap);
	}
	public void initData(){
		if(configMap.isEmpty()){
			return;
		}
		Map<String, Long> timeMap = new HashMap<>();
		CountDownLatch latch = new CountDownLatch(configMap.size());//协作  
		configMap.values().forEach(config->{ 
			LimitedThreadPool.execute(()->{
				long timeBegin = System.currentTimeMillis();
				initData(config);
				long interval = System.currentTimeMillis() - timeBegin;
				timeMap.put(config.getName(), interval);
				latch.countDown();
			});
		});
		/* 不用线程池启动的时候可能会阻塞*/
		LimitedThreadPool.execute(()->{
			try {
				latch.await();
			} catch (InterruptedException e) {
				LogCore.BASE.error("latch.await err:{},", e);
			}
			LogCore.BASE.info("all inited time used{},", timeMap);
		});
	}
	/**
	 * TODO:写文件的方式改为append
	 * @param config
	 */
	private void initData(DataStatsConfig config) {
		Lock lock = locks.computeIfAbsent(config.getId(), (k)->new ReentrantLock());
		lock.lock();
		try {
			StopWatch sw = new StopWatch(config.getName());
 			String listFile = config.getId() + ".os";
			String incrflagFile = config.getId() +config.getIncrName()+ ".os";
			sw.start("parse file");
			Long lastIncrValue = File2ObjectUtil.existFile(incrflagFile)?File2ObjectUtil.readFileFast(incrflagFile): 0L;
			LinkedList<Map<String, Object>> oldList = allDataMap.computeIfAbsent(config.getId(), (key)->new LinkedList<>());
			LogCore.BASE.info("{}, oldList.size={},lastIncrValue={}", config.getId(), oldList.size(), lastIncrValue);
			if(oldList.isEmpty()){//说明是第一次,从缓存文件中读取,文件中的数据新的在后面
				if(File2ObjectUtil.existFile(listFile)){
					List<List<Map<String, Object>>> list = File2ObjectUtil.readFileFast2List(listFile);
					if(!Util.isEmpty(list)){
						list.forEach(oldList::addAll);
						LogCore.BASE.info("list.size={},", list.size());
						list.forEach(ll->LogCore.BASE.info("sublist.size={},", ll.size()));
					}
					oldList.forEach(m->LogCore.BASE.debug(m.get("id")+" init"));
					// clear to let GC do its work
					list.clear();
					list = null;
				}
			}
			sw.stop();
			sw.start("mysql query");
			LogCore.BASE.info("{}, after read file oldList.size={},", config.getId(), oldList.size());
			//从数据库增量
			List<Map<String, Object>> newlist = queryDataFromDB(config, lastIncrValue, limit);
			if(!Util.isEmpty(newlist)){
				oldList.addAll(newlist);
				OptionalLong maxIncrValueNew = newlist.parallelStream().mapToLong(map->Long.valueOf(map.get(config.getIncrName())+"")).max();
				lastIncrValue = Math.max(maxIncrValueNew.orElse(0), lastIncrValue);
				sw.stop();
				sw.start("rewrite file");
				int limit_threshold = (int) (limit * 1.2);
				LogCore.BASE.info("{}, after query oldList.size={}, limit_threshold={}, rewrite files?{} ready to serialize all query datas", config.getId(), oldList.size(), limit_threshold, oldList.size() > limit_threshold);
				if(oldList.size() > limit_threshold){
					LogCore.BASE.info("jvm free={},oldList.size={}", Sys.getJVMStatus(), oldList.size());
				    oldList.subList(0, oldList.size() - limit).clear();//subList()是List快照,不要直接使用。
					File2ObjectUtil.writeFileSafe(listFile, oldList);
					LogCore.BASE.info("jvm free={} after sub list, oldList.size={}", Sys.getJVMStatus(), oldList.size());
				}else{
					File2ObjectUtil.writeFileFastAppendSafe(listFile, newlist);
				}
				File2ObjectUtil.writeFileFast(incrflagFile, lastIncrValue);
			}	
			//debug oldList.forEach(m->LogCore.BASE.debug(m.get("id")+""));

			sw.stop();
			LogCore.BASE.info("{}, after writefile file oldList.size={},{}", config.getId(), oldList.size(), sw.prettyPrint());
		} catch(Exception e){
			LogCore.BASE.error("initData(conf) err:{},", e);
		}finally{
			lock.unlock();
		}
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
	/**
	 * json的方式
	 * <pre>{@code
	 *      String json = JSON.toJSONString(oldList);
	 *      Util.writeFile(json.getBytes(), "jsonfile");
	 *		json = new String(Util.getData("jsonfile"));
	 *		oldList.clear();
	 * 		List<Map> ll = JSON.parseArray(json, Map.class);
	 *		ll.forEach(map->{
	 *			Map<String,Object> _m= new HashMap();
	 *			map.forEach((k,v)->_m.put(k+"", v));
	 *			oldList.add(_m);
	 *		});
	 * }</pre>
	 * 
	 * @param id
	 * @return
	 */
	public void testAll(String id){
			initData();
	}
	/**
	 * 数据转换目前只持持基本数据类型和String
	 * tips:倒着插入 一个list.ArrayList用Collections.reverse(listTar);Linked用list.add(0, element);<br>
	 * <br>Collection.addAll()会创建一个数组
	 * @param config
	 * @return 一个LinkedList数据从最老到最新
	 */
	private List<Map<String, Object>> queryDataFromDB(DataStatsConfig config, Long lastReadTime, int limit) {
		List<Map<String, Object>> list = service.getAllData(config.getTableName(), config.getIncrName() ,lastReadTime+"", limit);
		if(Util.isEmpty(list)){
			return null;
		}
		LinkedList<Map<String, Object>> listTar = new LinkedList<>();
		list.stream().forEach((map)->{
			Map<String,Object> _map = new HashMap<>();
			map.forEach((k, v)->{
				if(null == v){
					return;
				}
				if(v instanceof Integer||v instanceof Long){
					_map.put(k, v);
				}else{
					_map.put(k, v.toString());
				}
			});
			listTar.addFirst(_map);//从最旧的到最新的
		});
		//debug listTar.forEach(m->LogCore.BASE.debug(m.get("id")+"--"));
		// clear to let GC do its work
		list.clear();
		list = null;
		return listTar;
	}

	
	/**
	 *  分页 <br>
	 * @param id
	 * @param skipNum
	 * @param pageSize
	 * @param filters
	 * @return
	 */
	public List<Map<String, Object>> queryPayFlowBySkipLimit(String id, int skipNum, int pageSize, List<FilterItem> filters) {
		return allDataMap.get(id).stream().filter((map)->{
			return filters.stream().allMatch((FilterItem filter)->filter.filter(map));
		})
		.skip(skipNum)
	    .limit(pageSize)
		.collect(Collectors.toList());
	}
	/**
	 * 部分页
	 * <pre><b>same as:</b>
	 * queryPayFlowBySkipLimit(id, 0, Integer.MAX_VALUE, filters)
	 * </pre>
	 * */
	public List<Map<String, Object>> queryPayFlowAll(String id, List<FilterItem> filters) {
		return allDataMap.get(id).stream().filter((map)->{
			return filters.stream().allMatch((FilterItem filter)->filter.filter(map));
		}).collect(Collectors.toList());
	}
	/**
	 * 汇总查询
	 * 支持分组 group
	 * 支持求和 sum
	 * 汇总查询不分页
	 * TODO:使用map.reduce
	 * List<Map>
	 */
	public List<List<String>> queryGroupBySkipLimit(String id, Collection<FilterItem> filters, Collection<GroupColumnItem> groups) {
//		//普通显式的key
//		List<String> multiNormalKeys = groups.stream()
//				.filter(g->g.getGroupType()==GroupColumnItemType.GROUP_DEFAULT)
//				.map(GroupColumnItem::getColumnName)
//				.collect(Collectors.toList());
		//普通字段
		List<GroupColumnItem> nrmls = groups.stream()
				.filter(g->g.getGroupType()==GroupColumnItemType.GROUP_DEFAULT)
				.collect(Collectors.toList());
		//group条件字段
		List<GroupColumnItem> gs = groups.stream()
				.filter(g->g.getGroupType()!=GroupColumnItemType.GROUP_DEFAULT)
				.collect(Collectors.toList());
		Map<String, Long> reduceMap = new HashMap<>(); //可以遍历这个结果
		Map<String, Map<String,Object>> showMap = new LinkedHashMap<>();//key为普通显式的属性组合的key,value为普通的属性值,最后遍历这个结果即可 
		allDataMap.get(id).stream().filter((map)->{
			return filters.stream().allMatch((FilterItem filter)->filter.filter(map));
		}).forEach(map->{
			StringBuilder multiKeyBuilder = new StringBuilder();
			nrmls.forEach(nrml->multiKeyBuilder.append(map.get(nrml.getColumnName())));
			String multiKey = multiKeyBuilder.toString();
			
			showMap.computeIfAbsent(multiKey, (k)->map);
			
			gs.forEach(group->{
				String colName = group.getColumnName();
				String colKey = group.getKey();
				String key = multiKey.concat(colKey);
				if(group.getGroupType()==GroupColumnItemType.COUNT){
					reduceMap.merge(key, 1L, Long::sum);	   //次数
					reduceMap.merge(colKey, 1L, Long::sum);
				}else if(group.getGroupType()==GroupColumnItemType.SUM){
					Long val = Long.valueOf(map.get(colName)+"");
					reduceMap.merge(key, val, Long::sum);	 
					reduceMap.merge(colKey, val, Long::sum);	 
				}else if(group.getGroupType()==GroupColumnItemType.MAX){
					Long val = Long.valueOf(map.get(colName)+"");
					reduceMap.merge(key, val, Long::max);	 
					reduceMap.merge(colKey, val, Long::max);	 
				}else if(group.getGroupType()==GroupColumnItemType.MIN){
					Long val = Long.valueOf(map.get(colName)+"");
					reduceMap.merge(key, val, Long::min);	 
					reduceMap.merge(colKey, val, Long::min);	 
				}
			});
		});
		if(Util.isEmpty(showMap)){
			return new ArrayList<>();
		}

		List<List<String>> viewList = new ArrayList<>();
		showMap.forEach((k,v)->{
			List<String> views = new ArrayList<>();
			groups.forEach((g)->{
				String colKey = g.getKey();
				String key = k.concat(colKey);
				if(g.getGroupType()==GroupColumnItemType.GROUP_DEFAULT){
					String value = v.get(g.getColumnName())+"";
					views.add(g.format(value));
				}else{
					String value = reduceMap.get(key)+"";
					views.add(g.format(value));
				}
			});
			viewList.add(views);
		});
		//一个总的reduce
		List<String> views = new ArrayList<>();
		groups.forEach((g)->{
			String colKey = g.getKey();
			if(g.getGroupType()==GroupColumnItemType.GROUP_DEFAULT){
				views.add("--");
			}else{
				String value = reduceMap.get(colKey)+"";
				views.add(g.format(value));
			}
		});
		viewList.add(views);
		return viewList;
	}
	
	/** 获取总数目*/
	public long queryCount(String id, List<FilterItem> filters){
		return allDataMap.get(id).parallelStream().filter((map)->{
			return filters.stream().allMatch((FilterItem filter)->filter.filter(map));
		}).count();
	}

	@SuppressWarnings("deprecation")
	public void initConigMap(){
		if(File2ObjectUtil.existFile(CONFIGMAP_FILE_NAME)){
			configMap = IOUtil.readObject(CONFIGMAP_FILE_NAME);
		}
	}
	@SuppressWarnings("deprecation")
	public void saveOrUpdateConfig(DataStatsConfig config) {
		configMap.put(config.getId(), config);
		IOUtil.writeObject(configMap, CONFIGMAP_FILE_NAME);
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

	public Object clear(String id) {
		if(allDataMap.containsKey(id)){
			allDataMap.get(id).clear();
		}
		return null;
	}

	public Object tstByTableName(String tableName) {
		return service.getAnyData(tableName);
	}
	public Object tst(String id) {
		Map<String,Object> map = allDataMap.get(id).stream().findAny().get();
		Util.debugMap(map);
		return map;
	}

	public Object tables() {
		return service.tables();
	}
	public Object databases() {
		return service.databases();
	}
}
