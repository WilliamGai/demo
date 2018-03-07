package com.sonic.website.core.common.support;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;

/** 抽奖工具类 */
public class DrawUtil {
	public static final SecureRandom RAND = new SecureRandom();

	/** simple */
	public static <T> T getOne(final Map<T, Integer> weightMap) {
		if (Util.isEmpty(weightMap)) {
			LogCore.BASE.error("weightMap is empty:{}", weightMap);
			return null;
		}
		int sum = weightMap.values().stream().filter(Objects::nonNull).reduce((a, b) -> a + b).get();
		int random = RAND.nextInt(sum) + 1;// 均匀的产生1到SUM
		int overlapCount = 0;

		for (Map.Entry<T, Integer> entry : weightMap.entrySet()) {
			T key = entry.getKey();
			int value = entry.getValue();
			if ((overlapCount < random) && (random <= (overlapCount + value))) {
				return key;
			}
			overlapCount += value;
		} // for
		return null;
	}

	/** remain the captain */
	public static <T> List<T> getLuckyDrawPutBack(int count, Map<T, Integer> oddsMap) {
		List<T> list = new ArrayList<>();
		Map<T, Integer> factorMap = new HashMap<>();
		factorMap.putAll(oddsMap);
		if (null == factorMap || 0 == factorMap.size()) {
			return list;
		}
		int sum = 0;
		for (Integer value : factorMap.values()) {
			sum += value;
		}
		if (0 == sum) {
			return list;
		}
		for (int i = 0; i < count; i++) {
			int random = RAND.nextInt(sum) + 1;// 均匀的产生1到SUM
			int overlapCount = 0;
			for (Map.Entry<T, Integer> entry : factorMap.entrySet()) {
				T key = entry.getKey();
				int value = entry.getValue();
				if ((overlapCount < random) && (random <= (overlapCount + value))) {
					list.add(key);
					break;
				}
				overlapCount += value;
			} // for
		}
		return list;
	}

	/**
	 * 每次取完不放回,抽取必有奖，奖可重复 思考:要传进来的Map是否线程安全,这个代码是否线性安全,如何判断物品库存不再然后清除的情况
	 */
	public static <T> List<T> getLuckyDrawNobBack(int count, Map<T, Integer> oddsMap) {
		List<T> list = new ArrayList<>();
		Map<T, Integer> factorMap = new HashMap<>();
		factorMap.putAll(oddsMap);
		if (null == factorMap || 0 == factorMap.size()) {
			return list;
		}
		int sum = 0;
		for (Integer value : factorMap.values()) {
			sum += value;
		}
		if (0 == sum) {
			return list;
		}
		for (int i = 0; i < count; i++) {
			int random = RAND.nextInt(sum) + 1;// 均匀的产生1到SUM
			int overlapCount = 0;
			for (Iterator<Map.Entry<T, Integer>> it = factorMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry<T, Integer> entry = it.next();
				T key = entry.getKey();
				int value = entry.getValue();
				if ((overlapCount < random) && (random <= (overlapCount + value))) {
					list.add(key);
					/*
					 * 要取得不重复结果的方法 it.remove() sum -= value;
					 */

					sum -= 1; // 放回盒内，每次机会相等
					entry.setValue(value - 1);//
					break;
				}
				overlapCount += value;
			} // for
		}
		return list;
	}

	public static <T> T any(T[] arr) {
		if (Util.isEmpty(arr)) {
			return null;
		}
		return arr[RAND.nextInt(arr.length)];
	}

	public static <T> T any(List<T> list) {
		if (Util.isEmpty(list)) {
			return null;
		}
		return list.get(RAND.nextInt(list.size()));
	}

	/* 一下 参考API.Collections.shuffle */
	public static <T> List<T> any(List<T> list, int num) {
		List<T> rst = new ArrayList<>(num);
		while (num-- > 0) {
			rst.add(any(list));
		}
		return rst;
	}

	public static <T> List<T> any(T[] arr, int num) {
		List<T> rst = new ArrayList<>(num);
		while (num-- > 0) {
			rst.add(any(arr));
		}
		return rst;
	}

	/** 会改变传进来的列表 */
	public static <T> List<T> anyUnique(List<T> list, int num) {
		List<T> rst = new ArrayList<>(num);
		int size = list.size();
		int count = 0;
		if (list instanceof RandomAccess) {
			for (int i = size; i > 1 && count < num; i--, count++)// 随机出一个跟最后一个交换，然后随机一个跟倒数第二个交换
			{
				T t = Util.swap(list, RAND.nextInt(i), i - 1);
				rst.add(t);
			}
		} else {
			Object arr[] = list.toArray();
			for (int i = size; i > 1 && count < num; i--, count++) {
				T t = Util.swap(arr, RAND.nextInt(i), i - 1);
				rst.add(t);
			}

		}
		return rst;
	}

	/** 会改变传进来的数组 */
	public static <T> List<T> anyUnique(Object[] arr, int num) {
		List<T> rst = new ArrayList<>(num);
		int size = arr.length;
		int count = 0;
		for (int i = size; i > 1 && count < num; i--, count++) {
			T t = Util.swap(arr, RAND.nextInt(i), i - 1);
			rst.add(t);
		}
		return rst;
	}

}
