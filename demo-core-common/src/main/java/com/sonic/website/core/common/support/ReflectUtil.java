package com.sonic.website.core.common.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ReflectUtil {
	
	
	@SuppressWarnings("unchecked")
	public static <T> T getField(Class<?> clazz, Object r, String filedName) {
		try {
			Field f = clazz.getDeclaredField(filedName);
			f.setAccessible(true);
			return (T) f.get(r);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T changeField(Class<?> clazz, Object r, String filedName, Object newValue) {
	    try {
	        Field f = clazz.getDeclaredField(filedName);
	        f.setAccessible(true);
	        f.set(r, newValue);
	        return (T) f.get(r);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	
	/**
	 * 获得某个对象的某个字段
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getField(Object r, String filedName) {
		try {
			Field f = r.getClass().getDeclaredField(filedName);
			f.setAccessible(true);
			return (T) f.get(r);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String, Object> getFields(Object r) {
		Map<String, Object> map = new LinkedHashMap<>();
		Stream.of(r.getClass().getDeclaredFields()).forEach(field -> {
			field.setAccessible(true);
			try {
				map.put(field.getName(), field.get(r));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return map;
	}
	/**
	 * 包括父类
	 */
	public static Map<String, Object> getFieldsInherit(Object r, Class<?> clazz) {
		Map<String, Object> map = new LinkedHashMap<>();
		Stream.of(clazz.getDeclaredFields()).forEach(field -> {
			field.setAccessible(true);
			try {
				map.put(field.getName(), field.get(r));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		Class<?> superClazz = clazz.getSuperclass();
		if(Object.class == superClazz){
			return map;
		}
		map.putAll(getFieldsInherit(r, superClazz));
		return map;
	}
	/** 包括父类  */
	public static Map<String, Object> getFieldsInherit(Object r, Class<?> clazz, Predicate<Field> p) {
		Map<String, Object> map = new LinkedHashMap<>();
		Stream.of(clazz.getDeclaredFields()).forEach(field -> {
			if(!p.test(field))
				return;
			field.setAccessible(true);
			//LogCore.BASE.info("field.getName()={},getModifiers={},modifiers={},filter={}", field.getName(), field.getModifiers(), modifiers, filter);
			try {
				map.put(field.getName(), field.get(r));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		Class<?> superClazz = clazz.getSuperclass();
		if(Object.class == superClazz){
			return map;
		}
		map.putAll(getFieldsInherit(r, superClazz, p));
		return map;
	}

	public static List<String> getFieldNames(Class<?> clazz) {
		List<String> fieldNames = new ArrayList<>();
		Stream.of(clazz.getDeclaredFields()).forEach(field -> {
			field.setAccessible(true);
			fieldNames.add(field.getName());
		});
		Class<?> superClazz = clazz.getSuperclass();
		if(Object.class == superClazz){
			return fieldNames;
		}
		fieldNames.addAll(getFieldNames(superClazz));
		return fieldNames;
	}

	public static Map<String, Object> getFields(Object r, Predicate<Field> p) {
		Map<String, Object> map = new HashMap<>();
		Stream.of(r.getClass().getDeclaredFields()).forEach(field -> {
			if (!p.test(field))
				return;
			field.setAccessible(true);
			try {
				map.put(field.getName(), field.get(r));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return map;
	}

	/* 如果无返回值则返回null */
	public static Object invokeMethod(Method mtd, Object obj, Object... args) {
		mtd.setAccessible(true);
		try {
			return mtd.invoke(obj, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		Method mtd = null;
		try {
			mtd = clazz.getDeclaredMethod(methodName, parameterTypes);
		} catch (Exception e) {
			e.printStackTrace();
			return mtd;
		}
		return mtd;
	}

	/**
	 * 通过反射获得某个对象的某个方法，并返回方法的结果
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getReflectObject(Class<?> clazz, Object r, String methodName, Object[] params,
	            Class<?>... parameterTypes) {
		try {
			Method mtd = clazz.getDeclaredMethod(methodName, parameterTypes);
			mtd.setAccessible(true);
			return (T) mtd.invoke(r, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
