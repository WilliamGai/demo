package com.sincetimes.website.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sincetimes.website.core.common.support.ReflectUtil;
import com.sincetimes.website.core.common.support.Util;

public class TestArrayListCapacity {
	public static void main(String args[]) {
		System.out.println("hi");
		List<String> list = new ArrayList<>();
		Integer capacity = getArraylistCapacity(list);
		System.out.println("init size=" + list.size() + ", capacity=" + capacity);

		int n = 0;
		while (n++ < 50) {
			list.add("a");
			capacity = getArraylistCapacity(list);

			System.out.println("init size=" + list.size() + ", capacity=" + capacity);

		}
	}

	private static Integer getArraylistCapacity(List<String> r) {
		Object[] elementData = ReflectUtil.getField(r, "elementData");
		return Util.size(elementData);
	}

}
