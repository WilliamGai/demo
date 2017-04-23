package com.sincetimes.website.app.stats.flow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.alibaba.fastjson.JSON;
import com.mysql.fabric.xmlrpc.base.Param;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.TimeToolNew;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.vo.VOBase;

@Component
public class DataStatsManager extends ManagerBase {
	private static final String CONFIGMAP_FILE_NAME = "configmap.os";
	private static final int TYPE_INT = 0;
	private static final int TYPE_LONG = 1;
	private static final int TYPE_STRING = 2;
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
	public Object testAll(String id){
		DataStatsConfig config = getConfig(id);
		if(null == config){
			return null;
		}
		StopWatch stopWatch = new StopWatch("upfile");
		stopWatch.start("mysql 读取");
		
		List<Map<String, Object>> queryResult = service.getAllData(config.getTableName(), null ,null);
		if(Util.isEmpty(queryResult)){
			return null;
		}
		List<Map<String, Object>> oldList = allDataMap.computeIfAbsent(id, (key)->new LinkedList<>());
		oldList.addAll(queryResult);
		stopWatch.stop();
		stopWatch.start("serialize");
		byte[] data = sericalize(oldList, "test5");
		stopWatch.stop();
		stopWatch.start("serialize 写入文件");
		try {
			Util.writeFile(data, "test5");
		} catch (Exception e1) {e1.printStackTrace();}
		LogCore.BASE.info("serialize oldList.size:{}", oldList.size());
		stopWatch.stop();
		stopWatch.start("deserialize 从文件读取");
		byte[] dd;
		try {
			dd = Util.getData("test5");
			stopWatch.stop();
			stopWatch.start("deserialize");
			deserialize(dd, "test5");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		LogCore.BASE.info("deserialize oldList.size:{}", oldList.size());
		stopWatch.stop();
		stopWatch.start("jse serialize and save file");
		Util.writeObject(oldList, "test4");
		stopWatch.stop();
		stopWatch.start("jse read file and deserialize");
		Util.readObject("test4");
		stopWatch.stop();
		stopWatch.start("make Object to json");
		String json = JSON.toJSONString(oldList);
		stopWatch.stop();
		stopWatch.start("json save file");
		try {
			Util.writeFile(json.getBytes(), "jsonfile");
			stopWatch.stop();
			stopWatch.start("json read");
			json = new String(Util.getData("jsonfile"));
			stopWatch.stop();
			LogCore.BASE.info("string read:{}", json.substring(0, 100));
			oldList.clear();
			stopWatch.start("json parse to Object");
			List<Map> ll = JSON.parseArray(json, Map.class);
			LogCore.BASE.info("json3.size:{}", ll.size());
			
			ll.forEach(map->{
				Map<String,Object> _m= new HashMap();
				map.forEach((k,v)->_m.put(k+"", v));
				oldList.add(_m);
			});
			LogCore.BASE.info("obj:{}", ll.getClass().getSimpleName());
			LogCore.BASE.info("oldList:{}", oldList.size());

			stopWatch.stop();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LogCore.BASE.info("load All time used:{}", stopWatch.prettyPrint());
		return stopWatch.prettyPrint();
	}

	private void deserialize(byte[]data, String fileName) {
//		Util.readObject("test");
		try {
			
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			int size = in.readInt();
			List<Map<String,Object>> list = new ArrayList<>(size);
			for(int i=0;i<size;i++){
				int mapSize = in.readInt();
				Map<String,Object> map = new HashMap<>();
				for(int j=0; j<mapSize; j++){
					byte raw_type = in.readByte();
					if(raw_type == TYPE_INT){
						map.put(in.readUTF(), in.readInt());
						continue;
					}
					if(raw_type == TYPE_LONG){
						map.put(in.readUTF(), in.readLong());
						continue;
					}
					if(raw_type == TYPE_STRING){
						map.put(in.readUTF(), in.readUTF());
						continue;
					}
				}
				list.add(map);
			}
			LogCore.BASE.info("list size={}", list.size());
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private byte[] sericalize(List<Map<String, Object>> oldList, String fileName) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			out.writeInt(oldList.size());
			oldList.forEach((map)->{
				int sz = (int)map.values().stream().filter(Objects::nonNull).count();
				try {
					out.writeInt(sz);
					map.forEach((k,v)->{
						try {
							if(v== null){
								return;
							}
							if(v instanceof Integer){
								out.writeByte(TYPE_INT);
								out.writeUTF(k);
								out.writeInt((Integer)v);
								return;
							}
							if(v instanceof Long){
								out.writeByte(TYPE_LONG);
								out.writeUTF(k);
								out.writeLong((Long)v);
								return;
							}
							out.writeByte(TYPE_STRING);
							out.writeUTF(k);
							out.writeUTF(v+"");
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			out.flush();
			out.close();
			return bout.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
//		Util.writeObject(oldList, "test");
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

	public Object clear(String id) {
		if(allDataMap.containsKey(id)){
			allDataMap.get(id).clear();
		}
		return null;
	}

	public Object tst(String tableName) {
		return service.getAnyData(tableName);
	}

	public Object tables() {
		return service.tables();
	}
	public Object databases() {
		return service.databases();
	}
}
