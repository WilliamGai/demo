package com.sincetimes.website.app.stats.flow;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;
/**
 * TODO:动态数据源头
 * @author BAO
 *
 */
@Service
public class DataStatsJdbcService{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Map<String, Object>> getAllData(String tableName,  String incrName, String lowerValue){
		String sql = Util.format("select * from {}", tableName);
		List<Map<String, Object>> data= jdbcTemplate.queryForList(sql);
		LogCore.BASE.info("get all {},{}", data.size());
		return data;
	}
	/**
	 * 如果是自增的ID用new TreeMap<>(Integer::compare)或new TreeMap<>(Comparator.comparing(Integer::intValue));
	 */
	public List<DataStatsParam> getAllArticleTypes() {
		return jdbcTemplate.query("select * from t_article_type", 
			(ResultSet rs, int rowNum) -> {
				DataStatsParam tp = new DataStatsParam();
				ResultSetMetaData metaData = rs.getMetaData();
				int columnsCount = metaData.getColumnCount();
				metaData.getColumnTypeName(2);
				rs.getString(1);
			return tp;
		});
	}
}
