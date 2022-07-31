package com.sonic.website.core.common.serialize;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sonic.website.core.common.serialize.exception.SerializeException;
import com.sonic.website.core.common.serialize.interfaces.Serializer;
import com.sonic.website.core.common.support.DataSimpleVO;

/**
 * ①JSON.parseObject(jsonStr, clazz)改为JSON.parse(jsonStr)即可不传Class <br>
 * ②JSON.parse(s)和JSON.parseObject(s)反序列化Class类型时只能是显示声明属性为Class才可以，否则如果属性类型是Object会反序列化为字符串<br>
 * ③Class<?> clazz[];fastjson反序列化会失败
 * 
 * @author bao
 * @param <T>
 * @date 2017年7月29日 下午3:17:35
 */
public class JSONSerializer<T> implements Serializer<T> {
    static final Charset DEFAULT_CHATSET = Charset.forName("UTF8");// 限制为UTF8

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(byte[] bytes) throws SerializeException {
        try {
            return (T) JSONUtil.deserialize(new String(bytes, DEFAULT_CHATSET));
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public byte[] serialize(Object serializable) throws SerializeException {
        try {
            return JSONUtil.serialize(serializable).getBytes(DEFAULT_CHATSET);
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }

    /***
     * 如果不指定SerializerFeature.WriteClassName的话 反序列化的时候,作为类的属性声明为Object的会转为JSONObject 声明为抽象类的会转为空 null fastjson的一个bug
     * 
     * @param args
     */
    public static void main2(String args[]) {
        DataSimpleVO a = new DataSimpleVO("a", 1);
        DataSimpleVO b = new DataSimpleVO("b", 2);
        b.value = a;
        Map<String, Object> map = new HashMap<>();
        map.put(a.name, a);
        b.value1 = map;
        String jsonStr = JSON.toJSONString(b);
        System.out.println(jsonStr);
        DataSimpleVO obj = JSON.parseObject(jsonStr, DataSimpleVO.class);
        System.out.println(obj.toString());

    }

    /**
     * 如果序列化的时候不写SerializerFeature.WriteClassName 用JSON.parse(jsonStr) 反序列化结果是com.alibaba.fastjson.JSONObject
     * 
     * @param args
     */
    public static void main3(String args[]) {
        DataSimpleVO a = new DataSimpleVO("a", 1);
        DataSimpleVO b = new DataSimpleVO("b", 2);
        b.value = a;
        Map<String, Object> map = new HashMap<>();
        map.put(a.name, a);
        b.value1 = map;
        String jsonStr = JSON.toJSONString(b, SerializerFeature.WriteClassName);
        System.out.println(jsonStr);
        DataSimpleVO obj = (DataSimpleVO) JSON.parse(jsonStr);
        System.out.println(obj.getClass().getCanonicalName());

    }

    public static class Request {
        public Class<?> clazz[];
    }

    public static void main4(String args[]) {
        Request a = new Request();
        a.clazz = new Class<?>[] { String.class, String.class };

        String jsonStr = JSON.toJSONString(a, SerializerFeature.WriteClassName);
        System.out.println(jsonStr);
        Request obj = (Request) JSON.parse(jsonStr);
        System.out.println(obj.getClass().getCanonicalName());

    }
    //测试fastjson BUG
    public static void main(String args[]) {
        DataSimpleVO a = new DataSimpleVO("a", 1);
        DataSimpleVO b = new DataSimpleVO("b", 2);
        b.value1 = a;
        Map<String, Object> map = new HashMap<>();
        map.put(a.name, a);
        b.value = map;
        String jsonStr = JSON.toJSONString(b);
        DataSimpleVO obj =  JSON.parseObject(jsonStr, DataSimpleVO.class);
        System.out.println("JSON值:"+jsonStr);
        System.out.println("原始值:"+b);
        System.out.println("反序列化:"+obj);

    }
}
