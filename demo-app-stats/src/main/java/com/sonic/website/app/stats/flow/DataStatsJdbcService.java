package com.sonic.website.app.stats.flow;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;
/**
 * TODO:动态数据源头
 * @author BAO
 *
 */
@Service
public class DataStatsJdbcService{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	/* */
	public List<Map<String, Object>> getAllData(String tableName,  String incrName, String lowerValue, int limit){
		String sql = Util.format("select * from {} where {} > {} order by {} desc limit {}", tableName, incrName, lowerValue, incrName, limit);
//		long size = jdbcTemplate.q
		List<Map<String, Object>> data= jdbcTemplate.queryForList(sql);
		LogCore.BASE.info("get all {}, result rows.size={}", sql, data.size());
		return data;
	}
	public List<Map<String, Object>> getAnyData(String tableName) {
		String sql = Util.format("select * from {} limit 1", tableName);
		List<Map<String, Object>> data= jdbcTemplate.queryForList(sql);
		Optional<Map<String, Object>> map = data.stream().findAny();
		map.ifPresent(Util::debugMap);
		LogCore.BASE.info("get all {},{}", tableName, Util.prettyJsonStr(data));
		return data;
	}
	public Object tables() {
		List<Map<String, Object>> data= jdbcTemplate.queryForList("SHOW tables");
		data.forEach(map->{
			map.values().forEach(v->System.err.println(v.getClass().getSimpleName()));
		});
		return data;
	}
	public Object databases() {
		List<Map<String, Object>> data= jdbcTemplate.queryForList("SHOW DATABASES");
		data.forEach(map->{
			map.values().forEach(v->System.err.println(v.getClass().getSimpleName()));
		});
		return data;
	}
}
