package com.sincetimes.website.app;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sincetimes.website.core.common.support.LogCore;

public class Test {

	public static void main(String[] args) {
		Map<Integer, Integer>map = new HashMap<>();
		map.put(1, 2);
		map.put(3, 5);
		Collection<Integer> list = map.values();
		LogCore.BASE.info("list ={}", list);

	}

}
