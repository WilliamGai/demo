package com.sincetimes.website.core.common.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassUtil {
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

	/**推荐,获取某个类的声明信息*/
	public static String getDefineGenericSimpleString(Class<?> clazz) {
		return clazz.toGenericString().replaceAll("([a-zA-Z]+\\.)", "");
	}
	/**推荐,获取某个类或者接口的所有方法列表,default的标识符保留public*/
	public static String getGenericMethodSimpleString(Class<?> clazz) {
		List<String> list = getGenericMethodSimpleList(clazz);
		return list.stream().collect(Collectors.joining("\n")).replaceAll("([a-zA-Z]+\\.)", "");
	}

	public static List<String> getGenericMethodSimpleList(Class<?> clazz) {
		List<String> list = new ArrayList<>(clazz.getMethods().length);
		Arrays.stream(clazz.getDeclaredMethods()).forEach((m)->{
			list.add(m.toGenericString());
		});
		list.sort(Comparator.comparing(Function.identity()));
		return list;
	}
	
	/** 输出所有的public方法, 抹掉泛型, {@link Method#isDefault()}可以看到default的真正含义*/
	public static String getMethodSimpleString(Class<?> clazz) {
		StringBuilder sb = new StringBuilder();
		Arrays.stream(clazz.getMethods()).forEach((m)->{
			String pStr = Arrays.stream(m.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(","));
			sb.append(m.isDefault()?"default":Modifier.toString(m.getModifiers())).append(" ")
			.append(m.getReturnType().getSimpleName()).append(" ")
			.append(m.getName()+" ")
			.append("(")
			.append(pStr)
			.append(");")
			.append(m.getDeclaringClass().getSimpleName())
			.append("\n");
		});
		return sb.toString();
	}
}
