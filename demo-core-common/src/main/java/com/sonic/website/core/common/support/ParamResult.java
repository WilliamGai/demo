package com.sonic.website.core.common.support;

import java.util.HashMap;
import java.util.Map;

public class ParamResult{
    
    private static final String KEY_SINGLE = "KEY_SINGLE_PARAM"; // 当一个数据项时，自动用此KEY值，简化操作
    private final Map<String, Object> datas = new HashMap<>();
    public byte result;

    public ParamResult put(String key, Object value) {
        datas.put(key, value);
        return this;
    }
    
    @SuppressWarnings("unchecked")
    public <K> K get(String key) {
        return (K) datas.get(key);
    }
    
    /**当只有单一数据项时 可以用此方法获取*/
    public <K> K get() {
        return get(KEY_SINGLE);
    }
    
    public ParamResult put(Object obj) {
        datas.put(KEY_SINGLE, obj);
        return this;
    }
    
    public ParamResult(){
    }
    
    public ParamResult (byte result){
        this.result = result;
    }
    
    public ParamResult (byte result, Object... params){
        this.result = result;
        if (params == null || params.length == 0) {
            return;
        }
        if (params.length == 1) {
            put(KEY_SINGLE, params[0]);
        } else { // 处理成对参数
            int len = params.length;
            for (int i = 0; i < len; i += 2) {
                String key = (String) params[i];
                Object val = (Object) params[i + 1];
                put(key, val);
            }
        }
    }
    public ParamResult success(){
        this.result = 1;
        return this;
    }
    public boolean isSuccess(){
        return this.result == 1;
    }
}
