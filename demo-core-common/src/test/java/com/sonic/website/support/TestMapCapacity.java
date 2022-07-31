package com.sonic.website.support;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.sonic.website.core.common.support.ReflectUtil;

public class TestMapCapacity {
    public static void main(String args[]) {
        System.out.println("hi");
        Map<String, String> map = new HashMap<>();
        // map.put("a", "b");
        int n = 0;
        while (n++ < 50) {
            map.put("a" + n, "b" + n);
            System.out.println("size=" + map.size() + ", capacity=" + getMapCapacity(map));
        }

        Integer capacity = getMapCapacity(map);
        System.out.println(capacity);
    }

    private static Integer getMapCapacity(Map<String, String> map) {
        Method m = ReflectUtil.getMethod(HashMap.class, "capacity", new Class<?>[] {});
        Object v = ReflectUtil.invokeMethod(m, map, new Object[] {});
        return (Integer) v;
    }
}
