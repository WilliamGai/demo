package com.sincetimes.website.core.common.core;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class Utils {
	
	/**
	 * MD5加密
	 * @param s	被加密的字符串
	 * @return	加密后的字符串
	 */
	public static String md5(String s) {
		if(s == null) s = "";
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		try {
			byte[] strTemp = s.getBytes("UTF-8");
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 判断两个对象是否相等
	 * @param objA
	 * @param objB
	 * @return
	 */
	public static boolean isEquals(Object objA, Object objB) {
		return new EqualsBuilder().append(objA, objB).isEquals();
	}
	
	/**
	 * 参数1是否为参数2的子类或接口实现
	 * @param parentCls
	 * @return
	 */
	public static boolean isInstanceof(Class<?> cls, Class<?> parentCls) {
		return parentCls.isAssignableFrom(cls);
	}
	
	/**
	 * 格式化时间戳
	 * @param value
	 * @param pattern
	 * @return
	 */
	public static String formatTime(long timestamp, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);

		return format.format(new Date(timestamp));
	}
	
	/**
	 * 根据时分秒配置 获取今天配置时间点
	 * @param suffix
	 * @return
	 */
	public static long formatDateStr(String dateStr, String pattern) {
		try {
			SimpleDateFormat bartDateFormat = new SimpleDateFormat(pattern);
			
			return bartDateFormat.parse(dateStr).getTime();
		} catch (Exception ex) {
			throw new SysException(ex);
		}
	}
	
	/**
	 * 两个时间戳相差的天数
	 * @param a
	 * @param b
	 * @return
	 */
	public static int getDaysBetween(long ta, long tb) {
		Calendar a = Calendar.getInstance();
		a.setTimeInMillis(ta);
		Calendar b = Calendar.getInstance();
		b.setTimeInMillis(tb);
		
		if (a.after(b)) {
			Calendar swap = a;
			a = b;
			b = swap;
		}
		
		int days = b.get(Calendar.DAY_OF_YEAR) - a.get(Calendar.DAY_OF_YEAR);
		int y2 = b.get(Calendar.YEAR);
		if (a.get(Calendar.YEAR) != y2) {
			a = (Calendar) a.clone();
			do {
				days += a.getActualMaximum(Calendar.DAY_OF_YEAR);
				a.add(Calendar.YEAR, 1);
			} while (a.get(Calendar.YEAR) != y2);
		}
		return days;
	}
	
	/**
	 * 是否是同一天
	 * @param ta
	 * @param tb
	 * @return
	 */
	public static boolean isSameDay(long ta, long tb) {
		return Utils.formatTime(ta, "yyyyMMdd").equals(Utils.formatTime(tb, "yyyyMMdd"));
	}
	
	/**
	 * 获取给定时间当前凌晨的时间对象
	 * @param time 取当天凌晨的话传入 System.currentTimeMillis() 即可
	 * @return
	 */
	public static long getTimeBeginOfToday(long time) {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(time);
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.set(Calendar.MINUTE, 0);
		ca.set(Calendar.SECOND, 0);
		ca.set(Calendar.MILLISECOND, 0);
		
		return ca.getTimeInMillis();
	}
	
	
	/**
	 * 根据当前时间，获取小时数 24小时格式
	 * @param time 当天的时间 
	 * @return
	 */
	public static int getHourOfTime(long time) {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(time);		
		return ca.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 构造List对象
	 * 
	 * 如果传入的是参数仅仅为一个对象数组(Object[])或原生数组(int[], long[]等)
	 * 那么表现结果表现是不同的，Object[]为[obj[0], obj[1], obj[2]]
	 * 而原生数组则为[[int[0], int[1]，int[2]]]
	 * 多了一层嵌套，需要对原生数组进行特殊处理。
	 * @param <T>
	 * @param ts
	 * @return
	 */
	@SafeVarargs
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> ofList(T... ts) {
		List result = new ArrayList();
		
		//对Null进行特殊处理
		if(ts == null) {
			result.add(null);
			return result;
		}
		
		//对单独的原始数组类型进行特殊处理
		if(ts.length == 1 && ts[0] != null && OFLIST_ARRAY_CLASS.contains(ts[0].getClass())) {
			if(ts[0] instanceof int[]) {
				int[] val = (int[]) ts[0];
				for(int v : val) {
					result.add(v);
				}
			} else if(ts[0] instanceof long[]) {
				long[] val = (long[]) ts[0];
				for(long v : val) {
					result.add(v);
				}
			} else if(ts[0] instanceof boolean[]) {
				boolean[] val = (boolean[]) ts[0];
				for(boolean v : val) {
					result.add(v);
				}
			} else if(ts[0] instanceof byte[]) {
				byte[] val = (byte[]) ts[0];
				for(byte v : val) {
					result.add(v);
				}
			} else if(ts[0] instanceof double[]) {
				double[] val = (double[]) ts[0];
				for(double v : val) {
					result.add(v);
				}
			}
		} else {	//对象数组
			for(T t : ts) {
				result.add(t);
			}
		}
		
		return result;
	}
	//专供ofList类使用 对于数组类型进行特殊处理
	private static final List<?> OFLIST_ARRAY_CLASS = Utils.ofList(int[].class, long[].class, boolean[].class, byte[].class, double[].class);
	
	/**
	 * 构造Map对象
	 * @param <T>
	 * @param ts
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> ofMap(Object...params) {
		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
		
		//无参 返回空即可
		if(params == null || params.length == 0) {
			return result;
		}
		
		//处理成对参数
		int len = params.length;
		for (int i = 0; i < len; i += 2) {
			K key = (K) params[i];
			V val = (V) params[i + 1];
			
			result.put(key, val);
		}
		
		return result;
	}
	

	public static void intToBytes(byte[] b,int offset,int v) {
		for(int i = 0; i < 4; ++i) {
			b[offset + i] = (byte)(v >>> (24 - i * 8));
		}
	}
	
	public static int bytesToInt(byte[] b,int offset) {
		int num = 0;
		for(int i = offset; i < offset+4; ++i) {
			num <<= 8;
			num |= (b[i] & 0xff);
		}
		return num;
	}
	
	public static int bytesToInt(Object[] b,int offset) {
		int num = 0;
		for(int i = offset; i < offset+4; ++i) {
			num <<= 8;
			num |= (((byte)b[i]) & 0xff);
		}
		return num;
	}
	
	public static int bytesToLittleEndian32(byte[] b,int offset) {
		return (((int)b[offset] & 0xff)      ) |
		           (((int)b[offset+1] & 0xff) <<  8) |
		           (((int)b[offset+2] & 0xff) << 16) |
		           (((int)b[offset+3] & 0xff) << 24);
	}
	
	public static void LittleEndian32ToBytes(byte[] b,int offset, int value) {
		b[offset+0] = (byte)((value      ) & 0xFF);
		b[offset+1] = (byte)((value >>  8) & 0xFF);
		b[offset+2] = (byte)((value >> 16) & 0xFF);
		b[offset+3] = (byte)((value >> 24) & 0xFF);
	}
	
	public static String toHexString(byte[] byteBuffer, int length) {
		StringBuffer outputBuf = new StringBuffer(length * 4);

		for (int i = 0; i < length; i++) {
			String hexVal = Integer.toHexString(byteBuffer[i] & 0xff);

			if (hexVal.length() == 1) {
				hexVal = "0" + hexVal; //$NON-NLS-1$
			}

			outputBuf.append(hexVal); //$NON-NLS-1$
		}
		return outputBuf.toString();
	}
	
	public static String getClassPath() {
		return Class.class.getResource("/").getPath();
	}
	
	/**
	 * String转为int型
	 * 如果出错 则为0
	 * @param value
	 * @return
	 */
	public static int intValue(String value) {
		if(StringUtils.isNotEmpty(value) && NumberUtils.isNumber(value))
			return Double.valueOf(value).intValue();
		else
			return 0;
	}
	
	public static int intValue(Integer value) {
		if(null == value)
			return 0;
		else
			return value;
	}
	/**
	 * String转为long型
	 * 如果出错 则为0
	 * @param value
	 * @return
	 */
	public static long longValue(String value) {
		if(StringUtils.isNotEmpty(value) && NumberUtils.isNumber(value))
			return Long.valueOf(value);
		else
			return 0;
	}
	public static long longValue(Long value) {
		if(null == value)
			return 0;
		else
			return value;
	}
	/**
	 * String转为double型
	 * 如果出错 则为0.0
	 * @param value
	 * @return
	 */
	public static double doubleValue(String value) {
		if(StringUtils.isNotEmpty(value) && NumberUtils.isNumber(value))
			return Double.valueOf(value);
		else
			return 0.0D;
	}
	public static double doubleValue(Double value){
		if(null == value)
			return 0.0D;
		else
			return value;
	}
	
	/**
	 * String转为float型
	 * 如果出错 则为0.0
	 * @param value
	 * @return
	 */
	public static float floatValue(String value) {
		if(StringUtils.isNotEmpty(value) && NumberUtils.isNumber(value))
			return Float.valueOf(value);
		else
			return 0.0f;
	}
	public static float floatValue(Float value){
		if(null == value)
			return 0.0f;
		else
			return value;
	}
	
	/**
	 * String转为boolean型
	 * 如果出错 则为false
	 * @param value
	 * @return
	 */
	public static boolean booleanValue(String value) {
		if("true".equalsIgnoreCase(value) && value != null)
			return true;
		else
			return false;
	}
	
	/**
	 * 从字符串转为JSONArray，主要目的是包装一下空值处理
	 * @param str 
	 * @return 正常返回对象，否则返回长度为0的JSONArray
	 */
	public static JSONArray toJSONArray(String str) {
		if(StringUtils.isEmpty(str)) {
			str = "[]";
		}
		
		return JSON.parseArray(str);
	}
	
	/**
	 * 从字符串转为JSONObject，主要目的是包装一下空值处理
	 * @param str 
	 * @return 正常返回对象，否则返回空的JSONObject
	 */
	public static JSONObject toJSONObject(String str) {
		if(StringUtils.isEmpty(str)) {
			str = "{}";
		}
		
		return JSON.parseObject(str);
	}
	
	
	/**
	 * 从字符串转为JSONObject，主要目的是包装一下异常处理
	 * @param str 
	 * @return 正常返回对象，否则返回长度为0的JSONArray
	 */
	public static JSONObject str2JSONObject(String str) {
		if(StringUtils.isEmpty(str)) {
			str = "{}";
		}
		return JSON.parseObject(str);
	}
	
	/**
	 * 将对象转化为JSON字符串
	 * @param obj
	 * @return
	 */
	public static String toJSONString(Object obj) {
		return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	/**
	 * 把两个数组组成一个匹配的Json 前面是属性，后面是数值
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String toJOSNString(String []str1, String []str2) {
		Map<String, String> tempMap = new HashMap<String, String>();
		if(str1 != null && str2 != null && str1.length == str2.length) {
			for(int i = 0 ; i < str1.length ; i++) {
				tempMap.put(str1[i], str2[i]);
			}
		}
		return toJSONString(tempMap);
	}
	
	/**
	 * 把两个数组组成一个匹配的Json 前面是属性，后面是数值
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String toJOSNString(String []str1, int []str2) {
		Map<String, Integer> tempMap = new HashMap<String, Integer>();
		if(str1 != null && str2 != null && str1.length == str2.length) {
			for(int i = 0 ; i < str1.length ; i++) {
				tempMap.put(str1[i], str2[i]);
			}
		}
		return toJSONString(tempMap);
	}
	
	public static Map<String, Integer> arrToMap(String []str1, int []str2) {
		Map<String, Integer> tempMap = new HashMap<String, Integer>();
		if(str1 != null && str2 != null && str1.length == str2.length) {
			for(int i = 0 ; i < str1.length ; i++) {
				tempMap.put(str1[i], str2[i]);
			}
		}
		return tempMap;
	}
	
	public static Map<String, Float> toMapWeight(String []str1, float []str2, float weight) {
		Map<String, Float> tempMap = new HashMap<String, Float>();
		if(str1 != null && str2 != null && str1.length == str2.length) {
			for(int i = 0 ; i < str1.length ; i++) {
				tempMap.put(str1[i], (str2[i] + weight));
			}
		}
		return tempMap;
	}
	
	public static String toJOSNStringWeight(String []str1, int []str2, double weight) {
		Map<String, Integer> tempMap = new HashMap<String, Integer>();
		if(str1 != null && str2 != null && str1.length == str2.length) {
			for(int i = 0 ; i < str1.length ; i++) {
				tempMap.put(str1[i], (int)(str2[i] + weight));
			}
		}
		return toJSONString(tempMap);
	}
	
	public static String toJOSNStringNag(String []str1, int []str2) {
		Map<String, Integer> tempMap = new HashMap<String, Integer>();
		if(str1 != null && str2 != null && str1.length == str2.length) {
			for(int i = 0 ; i < str1.length ; i++) {
				tempMap.put(str1[i], -str2[i]);
			}
		}
		return toJSONString(tempMap);
	}
	
	/**
	 * 把两个数组组成一个匹配的Json 前面是属性，后面是数值
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String toJOSNString(String []str1, float []str2) {
		Map<String, Float> tempMap = new HashMap<String, Float>();
		if(str1 != null && str2 != null && str1.length == str2.length) {
			for(int i = 0 ; i < str1.length ; i++) {
				tempMap.put(str1[i], str2[i]);
			}
		}
		return toJSONString(tempMap);
	}
	
	/**
	 * 读取配置文件
	 * @return
	 */
	public static Properties readProperties(String name) {
		String filePath = Utils.class.getClassLoader().getResource(name).getPath();
		try(FileInputStream in = new FileInputStream(filePath)) {
			Properties p = new Properties();
			p.load(in);
			
			return p;
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
	
	/**
	 * 获取对象的属性
	 * 会先尝试利用getter方法获取 然后再直接访问字段属性
	 * 如果给定的属性不存在 会返回null
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fieldRead(Object obj, String fieldName) {
		try {
			//返回值
			Object result = null;
			
			Class<? extends Object> clazz = obj.getClass();
			
			//先通过自省来获取字段的值(getter方法)
			boolean hasGetter = false;
			BeanInfo bi = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] pds = bi.getPropertyDescriptors();
			for(PropertyDescriptor p : pds) {
				if(!p.getName().equals(fieldName)) continue;
				
				result =  p.getReadMethod().invoke(obj);
				hasGetter = true;
			}
			
			//如果通过getter方法没找到 那么就尝试直接读取字段
			if(!hasGetter) {
				for(Field f : clazz.getFields()) {
					if(!f.getName().equals(fieldName)) continue;
					
					result = f.get(obj);
				}
			}
			
			return (T) result;
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
	
	/**
	 * 获取对象的静态属性
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fieldRead(Class<?> clazz, String fieldName) {
		try {
			Field field = FieldUtils.getDeclaredField(clazz, fieldName);
			return (T) field.get(clazz);
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
	
	/**
	 * 设置对象的属性
	 * 会先尝试利用setter方法修改 然后再直接修改字段属性
	 * 如果给定的属性不存在 会抛出异常
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static void fieldWrite(Object obj, String fieldName, Object valueNew) {
		try {
			Class<? extends Object> clazz = obj.getClass();
			
			//先通过自省来设置字段的值(setter方法)
			boolean hasSetter = false;
			BeanInfo bi = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] pds = bi.getPropertyDescriptors();
			for(PropertyDescriptor p : pds) {
				if(!p.getName().equals(fieldName)) continue;
				
				//到这里的话 证明属性能找到（至少有对应的getter）但是没有找到setter
				//可能是setter方法不符合规范 比如非void有返回值等
				//这种情况使用反射再次尝试
				Method wm = p.getWriteMethod();
				if(wm == null) {
					String wmStr = "set" + StringUtils.capitalize(fieldName);
					for(Method m : clazz.getMethods()) {
						if(!m.getName().equals(wmStr)) continue;
						m.invoke(obj, valueNew);
					}
				} else {
					wm.invoke(obj, valueNew);
				}
				
				hasSetter = true;
			}
			
			//如果通过setter方法没找到 那么就尝试直接操作字段
			if(!hasSetter) {
				Field f = clazz.getField(fieldName);
				f.set(obj, valueNew);
			}
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
	
	/**
	 * 通过反射执行函数
	 * @param obj
	 * @param method
	 * @param param
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Object obj, String method, Object...params) {
		try {
			return (T)MethodUtils.invokeMethod(obj, method, params);
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
	
	/**
	 * 通过反射执行函数
	 * @param obj
	 * @param method
	 * @param param
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeStaticMethod(Class<?> cls, String method, Object...params) {
		try {
			return (T)MethodUtils.invokeStaticMethod(cls, method, params);
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
	
	/**
	 * 通过反射执行构造函数
	 * @param cls
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeConstructor(Class<?> cls, Object...params) {
		try {
			return (T) ConstructorUtils.invokeConstructor(cls, params);
		} catch (Exception e) {
			throw new SysException(e);
		}
	}
	

	

	
	/**
	 * 将String[]，转化为String，以“，”分割
	 * @param str
	 * @return
	 */
	public static String arrayStrToStr(String[] arr) {
		if(arr.length == 0){
			return "";
		}
		
		String result = "";
		for (int i = 0; i < arr.length; i++) {
			result += (arr[i] + ","); 
		}
		
		return result.substring(0, result.length() - 1);
	}
	
	/**
	 * 将int[]，转化为String，以“，”分割
	 * @param str
	 * @return
	 */
	public static String arrayIntToStr(int[] arr) {
		if(arr.length == 0){
			return "";
		}
		
		String result = "";
		for (int i = 0; i < arr.length; i++) {
			result += (arr[i] + ","); 
		}
		
		return result.substring(0, result.length() - 1);
	}
	
	/**
	 * 创建函数特征码
	 * 类全路径:函数名(参数类型)
	 * @return
	 */
	public static String createMethodKey(Method method) {
		return createMethodKey(method.getDeclaringClass(), method);
	}
	
	/**
	 * 创建函数特征码
	 * 类全路径:函数名(参数类型)
	 * @return
	 */
	public static String createMethodKey(Class<?> cls, Method method) {
		//类全路径
		String clazzName = cls.getName();
		//函数名
		String methodName = method.getName();
		//参数类型字符串
		StringBuilder methodParam = new StringBuilder();
		methodParam.append("(");
		for(Class<?> clazz : method.getParameterTypes()) {
			if(methodParam.length() > 1) methodParam.append(", "); 
			methodParam.append(clazz.getSimpleName());
		}
		methodParam.append(")");
		
		return clazzName + ":" + methodName + methodParam.toString();
	}
}
