package com.sincetimes.website.core.common.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sincetimes.website.core.common.extension.UnsafeStringWriter;

public class Util {
	public static boolean isLinux() {
		String os = System.getProperty("os.name").toLowerCase();
		if (!isEmpty(os) && os.contains("windows")) {
			return false;
		}
		return true;
	}

	public static String getM(long availmem) {
		double result = availmem / 1024f / 1024f;
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(result) + " MB";
	}

	public static String getReflectStr(Object o) {
		return ReflectionToStringBuilder.toString(o, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public static <K, V> int size(Map<K, V> map) {
		if (null == map) {
			return 0;
		}
		return map.size();
	}
	public static int size(String s) {
		if (null == s) {
			return 0;
		}
		return s.length();
	}
	public static boolean isContained(String src, String tar) {
		if (Util.isEmpty(src) || Util.isEmpty(tar)) {
			return false;
		}
		return src.contains(tar);
	}

	public static String replace(String src, String s0, String s) {
		if (Util.isEmpty(src)) {
			return null;
		}
		return src.replace(s0, s);
	}
	public static String empty2Null(String s){
		if(null == s || s.trim().length()==0){
			return null;
		}
		return s;
	}
	public static String toEmpyDefalut(String s, String emptyDefalut){
		if(isEmpty(s)){
			return emptyDefalut;
		}
		return s;
	}
	/**
	 * @param t
	 * @param func s如果不为empty,则执行此表达式
	 * @param emptyDefalut 如果t为empty直接返回这个值
	 */
	public static <R> R toEmpyDefalut(String t,Function<String, R> func, R emptyDefalut){
		if(Util.isEmpty(t)){
			return emptyDefalut;
		}
		return func.apply(t);
	}
	public static <T> T toNullDefalut(T s, T nullDefalut){
		if(null == s){
			return nullDefalut;
		}
		return s;
	}
	/**
	 * @param s
	 * @param func s如果不为null,则执行此表达式
	 * @param nullDefalut 如果s为null直接返回这个值
	 */
	public static <T,R> R toNullDefalut(T t,Function<T, R> func, R nullDefalut){
		if(null == t){
			return nullDefalut;
		}
		return func.apply(t);
	}
	public static <T> void ifNotEmpty(T t, Consumer<? super T> consumer) {
        if(!isEmpty(t)){
        	consumer.accept(t);
        }
    }
	public static boolean isEmpty(Object obj) {
		return Objects.isNull(obj);
	}

	public static boolean isEmpty(Object os[]) {
		return null == os || 0 == os.length;
	}

	public static boolean isEmpty(String str) {
		return null == str || 0 == str.length();
	}

	public static boolean nonEmpty(String str) {
		return !isEmpty(str);
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return null == map || 0 == map.size();
	}

	public static boolean isEmpty(Collection<?> collection) {
		return null == collection || 0 == collection.size();
	}

	public <E> int getSize(Collection<E> collection) {
		if (null == collection) {
			return -1;
		}
		return collection.size();
	}

	public <E> E pollFromQueue(Queue<E> queue) {
		if (null == queue) {
			return null;
		}
		return queue.poll();
	}
	
	/** TU **/
	public static <K, V> String joinMap(Map<K, V> map) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Map.Entry<K, V>> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = it.next();
			K k = entry.getKey();
			V v = entry.getValue();
			if (v.getClass().isArray()) {
				Object[] os = (Object[]) v;
				sb.append(k).append("=").append(Arrays.toString(os));
			} else {
				sb.append(k).append("=").append(v);
			}
		}
		return sb.toString();
	}

	/** to collect */
	public static String join(Enumeration<String> names, String separator) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		while (names.hasMoreElements()) {
			if (first)
				first = false;
			else
				sb.append(separator);
			sb.append(names.nextElement());
		}
		return sb.toString();
	}

	public static String join(Collection<String> names, String separator) {
		return names.stream().collect(Collectors.joining(separator));
	}

	/**
	 * 格式化字符串 ,按照logback的方式,{}中没有数字 
	 * tips,可以映射的函数:BiFunction
	 */
	public static String format(String messagePattern, Object... arguments) {
		if ((messagePattern == null) || (arguments == null) || (arguments.length == 0)) {
			return messagePattern;
		}

		StringBuilder result = new StringBuilder();
		int escapeCounter = 0;
		int currentArgument = 0;
		for (int i = 0; i < messagePattern.length(); i++) {
			char curChar = messagePattern.charAt(i);
			if (curChar == '\\') {
				escapeCounter++;
			} else if ((curChar == '{') && (i < messagePattern.length() - 1) && (messagePattern.charAt(i + 1) == '}')) {
				int escapedEscapes = escapeCounter / 2;
				for (int j = 0; j < escapedEscapes; j++) {
					result.append('\\');
				}

				if (escapeCounter % 2 == 1) {
					result.append('{');
					result.append('}');
				} else {
					if (currentArgument < arguments.length)
						result.append(arguments[currentArgument]);
					else {
						result.append('{').append('}');
					}
					currentArgument++;
				}
				i++;
				escapeCounter = 0;
			} else {
				if (escapeCounter > 0) {
					for (int j = 0; j < escapeCounter; j++) {
						result.append('\\');
					}
					escapeCounter = 0;
				}
				result.append(curChar);
			}
		}
		return result.toString();
	}

	/** 全部不为空时返回真,它的非是有至少有一个为空 */
	public static boolean nonNull(Object... objs) {
		if (null == objs || 0 == objs.length)
			return false;
		for (Object o : objs) {
			if (null == o)
				return false;
		}
		return true;
	}

	/** 全部不为empty就返回真，它的非是有至少一个null或者空 */
	public static boolean nonEmpty(String... ss) {
		if (null == ss || 0 == ss.length)
			return false;
		return Stream.of(ss).map(Util::nonEmpty).reduce((x, y) -> x && y).get();
	}

	/** 只要有一个不为empty就返回真，它的非是全部为null或者空 */
	public static boolean anyNonEmpty(String... ss) {
		if (null == ss || 0 == ss.length)
			return false;
		return Stream.of(ss).map(Util::nonEmpty).reduce((x, y) -> x || y).get();
	}
	public static String tranString(String s, String srcCharset, String tarCharset){
		if(Util.isEmpty(s)){
			return s;
		}
		try {
			return new String (s.getBytes(srcCharset), tarCharset);
		} catch (UnsupportedEncodingException e) {
			LogCore.BASE.error("tranString err:s={},srccharset = {}, tarCharset={}",s,srcCharset,tarCharset, e);
			return null;
		}
	}
	/* 序列化 */
	public static String getUtf(byte[] data) {
		if (Util.isEmpty(data)) {
			return null;
		}
		try {
			return new String(data, "utf-8");
		} catch (Exception e) {
			LogCore.BASE.error("parse bytes err:{}", e);
			return null;
		}
	}

	public static byte[] getUtfBytes(String s) {
		try {
			return s.getBytes("utf-8");
		} catch (Exception e) {
			LogCore.BASE.error("get bytes err:{}", e);
			return null;
		}
	}
	
	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
	   /**
     * 
     * @param e
     * @return string
     */
    public static String toString(Throwable e) {
    	UnsafeStringWriter w = new UnsafeStringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName());
        if (e.getMessage() != null) {
            p.print(": " + e.getMessage());
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }
    
    /**
     * 
     * @param msg
     * @param e
     * @return string
     */
    public static String toString(String msg, Throwable e) {
    	UnsafeStringWriter w = new UnsafeStringWriter();
        w.write(msg + "\n");
        PrintWriter p = new PrintWriter(w);
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

	public static String prettyJsonStr(Object obj) {
		return JSON.toJSONString(obj, SerializerFeature.PrettyFormat,SerializerFeature.WriteClassName, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
	}
	/**
	 * 2017年4月21日17:10:56
	 * @param Seril
	 * @param string
	 * 低效率8Xmysql
	 */
	public static void writeObject(Object obj, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream os= new ObjectOutputStream(fos);
			os.writeObject(obj);//java.io.NotSerializableException:
			os.close();
		} catch (IOException e) {//NotSerializableException
			LogCore.BASE.error("write file err:", e);
		}
	}
	/**
	 * 2017年4月21日17:18:07
	 * 效率很低10Xmysql
	 */
	@SuppressWarnings("unchecked")
	public static <T> T readObject(String fileName) {
		try {
			 FileInputStream fis = new FileInputStream(fileName);
			 ObjectInputStream ois = new ObjectInputStream(fis);
			 Object obj = ois.readObject();
		     ois.close();
		     return (T)obj;
		} catch (FileNotFoundException e) {
			LogCore.BASE.warn("read warn:{}", "no configs saved");
			return null;
		} catch (IOException |ClassNotFoundException e) {
			LogCore.BASE.error("read err:", e);
			return null;
		}
	}
	public static byte[] getData(String fileName) throws Exception{
		return Files.readAllBytes(Paths.get(fileName));
//		FileInputStream fis = new FileInputStream(fileName);
//        ObjectInputStream ois = new ObjectInputStream(fis);
//        byte[] data = (byte[]) ois.readObject();
//        ois.close();
//        return data;
	}
	
	public static void writeFile(byte[] data, String fileName) throws FileNotFoundException,
			IOException {
//		Files.wr
		//如果没有要创建文件
		File dir = new File(fileName);
		LogCore.BASE.info("dir={}", dir);
		if(!dir.exists()) dir.createNewFile();
		Files.write(Paths.get(fileName), data, StandardOpenOption.WRITE);
//		FileOutputStream fos = new FileOutputStream(fileName);
//        ObjectOutputStream os= new ObjectOutputStream(fos);
//        os.writeObject(data);//可以传入Object
//        os.close();
	}
}
