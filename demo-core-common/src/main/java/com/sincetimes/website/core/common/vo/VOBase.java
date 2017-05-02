package com.sincetimes.website.core.common.vo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sincetimes.website.core.common.support.ClassTool;
import com.sincetimes.website.core.common.support.Util;
/**
 * VOBase不可以出现 getXXX()方法
 * JSONObject.parse返回的是JSONArray或JSONObject
 */
public abstract class VOBase {
	/* fastJson */
	public String toJSONString() {
		return JSON.toJSONString(this);
	}

	public static <T extends VOBase> T parseObject(String jsonStr, Class<T> clazz) {
		if (Util.isEmpty(jsonStr)) {
			return null;
		}
		return JSON.parseObject(jsonStr, clazz);
	}

	/**
	 * 数组类型要特殊处理
	 * @return 一个Arraylist
	 */
	public static <T> List<T> parseArray(String json, Class<T> clazz){
		List<T> result = new ArrayList<T>();
		JSONArray ja = JSON.parseArray(json);
		if (ja == null) {
			return result;
		}
		for (int i = 0; i < ja.size(); i++) {
			T t = ja.getObject(i, clazz);
			result.add(t);
		}
		return result;
	}
	/*reflect */
	/**
	 *  获取对象一般的字段名和字段值
		{@code	Predicate<Field> special = field->((Modifier.STATIC|Modifier.TRANSIENT|Modifier.FINAL)&field.getModifiers()) != 0;
		}等价于
		<pre>{@code
		if(Modifier.isTransient(f.getModifiers())){
			return false;
		}...
	 	}<pre>
	 */
	public Map<String, Object> generalFieldMap(){
		Predicate<Field> special = field->((Modifier.STATIC|Modifier.TRANSIENT|Modifier.FINAL)&field.getModifiers()) != 0;
 		return ClassTool.getFields(this, special.negate());
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
