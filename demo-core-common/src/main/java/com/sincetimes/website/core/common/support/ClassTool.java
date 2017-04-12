package com.sincetimes.website.core.common.support;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ClassTool {
	/**
	 * 参数1是否为参数2的子类或接口实现
	 */
	public static boolean isInstanceof(Class<?> cls, Class<?> parentCls) {
		return parentCls.isAssignableFrom(cls);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object r, String fieldName) {
		try {
			Field f = r.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			return (T) f.get(r);
		} catch (Exception e) {
			LogCore.BASE.error("getFieldValue err:{}", e);
			return null;
		}
	}

	public static Map<String, Object> getFields(Object r) {
		Map<String, Object> map = new HashMap<>();
		Stream.of(r.getClass().getDeclaredFields()).forEach(field -> {
			field.setAccessible(true);
			try {
				map.put(field.getName(), field.get(r));
			} catch (Exception e) {
				LogCore.BASE.error("getFields err:{}", e);
			}
		});
		return map;
	}
	
	public static Map<String, Object> getFields(Object r, Predicate<Field> p) {
		Map<String, Object> map = new HashMap<>();
		Stream.of(r.getClass().getDeclaredFields()).forEach(field -> {
			if(!p.test(field))
				return;
			field.setAccessible(true);
			try {
				map.put(field.getName(), field.get(r));
			} catch (Exception e) {
				LogCore.BASE.error("getFields(r,p) err:{}", e);
			}
		});
		return map;
	}
	
}
