package com.sonic.website.core.common.serialize;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.sonic.website.core.common.serialize.annoation.NotSerialize;
import com.sonic.website.core.common.serialize.annoation.SerializeAsSting;
import com.sonic.website.core.common.serialize.exception.SerializeException;

/***
 * 自定义的FastJSON序列化
 * 
 * @author bao
 * @date 2017年10月20日 上午10:52:01
 */
public class JSONUtilSpecial {
    public final static ObjectSerializer longSerializer = new ObjectSerializer() {
        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
                    throws IOException {
            // LogCore.BASE.info("obect={}， fieldType={}", object , fieldType);
            SerializeWriter out = serializer.getWriter();
            if (object == null) {
                if (out.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
                    out.writeString("0");
                } else {
                    out.writeNull();
                }
                return;
            }
            out.writeString(Objects.toString(object, null));
        }
    };

    public static final PropertyFilter resultJsonPropertyFilter = new PropertyFilter() {
        @Override
        public boolean apply(Object object, String key, Object value) {
            try {
                Field field = object.getClass().getDeclaredField(key);
                if (null != field && null != field.getAnnotation(NotSerialize.class)) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                return true;
            }
        }
    };

    public static final ValueFilter resultValueAsStringFilter = new ValueFilter() {
        @Override
        public Object process(Object object, String name, Object value) {
            // LogCore.BASE.info("object={}, name={},value={}", object, name,
            // value);
            if (value == null) {
                return value;
            }
            try {
                Field field = object.getClass().getDeclaredField(name);
                if (null != field && null != field.getAnnotation(SerializeAsSting.class)) {
                    return value.toString();
                }
                return value;
            } catch (Exception e) {
                return value;
            }
        }
    };
    public final static SerializeFilter[] resultJsonFilters = new SerializeFilter[] { resultJsonPropertyFilter,
                resultValueAsStringFilter };// 先
    public final static SerializeConfig resultJsonConfig = new SerializeConfig();// 后

    static {
        // resultJsonConfig.put(Long.class,objectSerializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(String data) throws SerializeException {
        return (T) JSON.parse(data);// 切记不可以使用ParseObject(obj),parseObject(s,clz)的使用必须传入Class
    }

    public static String serialize(Object serializable) throws SerializeException {
        return JSON.toJSONString(serializable, SerializerFeature.WriteClassName);
    }
}