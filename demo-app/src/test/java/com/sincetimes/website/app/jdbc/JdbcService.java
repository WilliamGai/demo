package com.sincetimes.website.app.jdbc;

import java.util.Map;

/**
 * 数据库
 */
public interface JdbcService {
	<T> int  getStockValue(T id);
	<T> int	decreaseStockValue(T id);
	<T> int initStockValue(T name, int value);
	Map<String, String> getAllDataValueMapByBytes();
}
