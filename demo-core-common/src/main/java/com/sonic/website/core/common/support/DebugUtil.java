package com.sonic.website.core.common.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
/**
 * FastJson不能获取没有get set的私有属性,此工具适用于不改动其他代码的情况下查看项目实际配置属性
 * @author bao
 * @date 2018年3月21日 下午5:44:53
 */
public class DebugUtil {

    public static Map<String, Object> valuesMap(Object r) {
        Map<String, Object> map = new HashMap<>();
        return valuesMap(map, r);
    }

    public static Map<String, Object> valuesMap(Map<String, Object> map, Object r) {
        Stream.of(r.getClass().getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);
            try {
                if (isRawType(field.getType())) {
                    map.put(field.getName(), field.get(r));
                    return;
                }
                if (field.getType().isArray()) {
                    map.put(field.getName(), getRawArrayString(field.get(r)));
                    return;
                }
                /*Field中的Type是声明类型, 而Value值是运行时类型*/
                if (Map.class.isAssignableFrom(field.getType()) || Collection.class.isAssignableFrom(field.getType())) {
                    map.put(field.getName(), "type:" + field.getType().getName() + ", size" + Util.size(field.get(r)));
                    return;
                }
                // if debugged
                if(LogCore.BASE.isDebugEnabled()){
                    map.put(field.getName(), "not raw " + field.getType().getName());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return map;
    }

    public static boolean isRawType(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        if (boolean.class == clazz || Boolean.class == clazz) {
            return true;
        }
        if (byte.class == clazz || Byte.class == clazz) {
            return true;
        }
        if (int.class == clazz || Integer.class == clazz) {
            return true;
        }
        if (char.class == clazz || Character.class == clazz) {
            return true;
        }
        if (short.class == clazz || Short.class == clazz) {
            return true;
        }
        if (double.class == clazz || Double.class == clazz) {
            return true;
        }
        if (long.class == clazz || Long.class == clazz) {
            return true;
        }
        if (String.class == clazz) {
            return true;
        }
        return false;
    }

    public static boolean isRawArrayType(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        if (boolean[].class == clazz || Boolean[].class == clazz) {
            return true;
        }
        if (byte[].class == clazz || Byte[].class == clazz) {
            return true;
        }
        if (int[].class == clazz || Integer[].class == clazz) {
            return true;
        }
        if (char[].class == clazz || Character[].class == clazz) {
            return true;
        }
        if (short[].class == clazz || Short[].class == clazz) {
            return true;
        }
        if (double[].class == clazz || Double[].class == clazz) {
            return true;
        }
        if (long[].class == clazz || Long[].class == clazz) {
            return true;
        }
        if (String[].class == clazz) {
            return true;
        }
        return false;
    }

    public static String getRawArrayString(Object r) {
        if (null == r) {
            return null;
        }
        if (r instanceof boolean[]) {
            return Arrays.toString((boolean[]) r);
        }
        if (r instanceof Boolean[]) {
            return Arrays.toString((Boolean[]) r);
        }
        if (r instanceof byte[]) {
            return Arrays.toString((byte[]) r);
        }
        if (r instanceof Byte[]) {
            return Arrays.toString((Byte[]) r);
        }
        if (r instanceof int[]) {
            return Arrays.toString((int[]) r);
        }
        if (r instanceof Integer[]) {
            return Arrays.toString((Integer[]) r);
        }

        if (r instanceof char[]) {
            return Arrays.toString((char[]) r);
        }

        if (r instanceof Character[]) {
            return Arrays.toString((Character[]) r);
        }

        if (r instanceof short[]) {
            return Arrays.toString((short[]) r);
        }

        if (r instanceof Short[]) {
            return Arrays.toString((Short[]) r);
        }
        if (r instanceof double[]) {
            return Arrays.toString((double[]) r);
        }
        if (r instanceof Double[]) {
            return Arrays.toString((Double[]) r);
        }
        if (r instanceof long[]) {
            return Arrays.toString((long[]) r);
        }
        if (r instanceof Long[]) {
            return Arrays.toString((Long[]) r);
        }

        if (r instanceof String[]) {
            return Arrays.toString((String[]) r);
        }

        if (r instanceof Object[]) {
            return Arrays.toString((Object[]) r);
        }
        return toString(r);
    }

    //强制
    public static String toString(Object obj) {
        return obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode());
    }
}
