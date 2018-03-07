package com.sonic.website.app.jdbc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;

@Service("JdbcServiceImpl")
public class JdbcServiceImpl implements JdbcService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 乐观锁,update t_stock set value = value-1 where value >1;并发不会出现-1
	 * 减库存成功则返回1
	 * 库存用完则返回0
	 */
	public <T> int decreaseStockValue(T id){
		Object[] obs = new Object[] { id};
		return jdbcTemplate.update("update t_value set value = value-1 where value >0 and id =?", obs);
	}

	@Override
	public <T> int initStockValue(T name, int value) {
		return jdbcTemplate.update("REPLACE INTO t_value VALUES(?, ?)", new Object[] { name, value });
	}

	@Override
	public <T> int getStockValue(T name) throws DataAccessException {
		return jdbcTemplate.queryForObject("SELECT value FROM t_value WHERE name = ?", new Object[] { name },
				int.class);
	}
	
	@Override
	public Map<String, String> getAllDataValueMapByBytes() {
		Map<String, String> map = new HashMap<>();
		Predicate<Map<String, Object>> pr = x -> Util.nonNull(x.get("no"), x.get("value"));
		jdbcTemplate.queryForList("SELECT * FROM t_data_bytes").stream().filter(pr).forEach(x -> {
			try {
				String key = String.valueOf(x.get("no"));
				String value = new String((byte[]) x.get("value"), "utf-8");
				map.put(key, value);
			} catch (Exception e) {
				LogCore.BASE.error("init DateValueMapByBytes err:", e);
			}
		});
		return map;
	}
}
