package com.sincetimes.website.vo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.JSON;
import com.sincetimes.website.core.common.support.Util;

public abstract class JsonVOBase {

	public String toJSONString() {
		return JSON.toJSONString(this);
	}

	@SuppressWarnings("unchecked")
	public static <T extends JsonVOBase> T parseObject(String jsonStr, Class<T> clazz) {
		if (Util.isEmpty(jsonStr)) {
			return null;
		}
		Object o = JSON.parseObject(jsonStr, clazz);
		return (T) o;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
