package com.sincetimes.website.core.common.support;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** 抽奖工具类 */
public class DrawTool {
	/** simple */
	public static <T> T getOne(final Map<T, Integer> weightMap) {
		if(Util.isEmpty(weightMap)){
			LogCore.BASE.error("weightMap is empty:{}", weightMap);
			return null;
		}
		SecureRandom rand = new SecureRandom();
		int sum = weightMap.values().stream().filter(Objects::nonNull).reduce((a, b) -> a + b).get();
		int random = rand.nextInt(sum) + 1;// 均匀的产生1到SUM
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
		SecureRandom rand = new SecureRandom();
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
			int random = rand.nextInt(sum) + 1;// 均匀的产生1到SUM
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
	 * 每次取完不放回,抽取必有奖，奖可重复
	 * 思考:要传进来的Map是否线程安全,这个代码是否线性安全,如何判断物品库存不再然后清除的情况
	 */
	public static <T> List<T> getLuckyDrawNobBack(int count, Map<T, Integer> oddsMap) {
		List<T> list = new ArrayList<>();
		SecureRandom rand = new SecureRandom();
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
			int random = rand.nextInt(sum) + 1;// 均匀的产生1到SUM
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
}
