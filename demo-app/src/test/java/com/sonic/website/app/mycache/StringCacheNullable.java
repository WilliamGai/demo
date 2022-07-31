package com.sonic.website.app.mycache;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.cache.CacheBuilder;
import com.sonic.website.core.common.support.Util;

/**
 * 单机缓存,注意操作asMap过期策略依然有效, 此缓存的put不一定是更新数据库,而是等缓存过期了才更新数据库 此缓存屏蔽了get(key,loadFc)
 * 因为loadFc要求不能返回null 0,此缓存允许传回空值null 1,containsKey和getValue都会触发加载数据库到内存
 * 2,containsKeyInMap不触发加载数据库到缓存
 */
public class StringCacheNullable extends MyCacheBase<String, String> {

    /**
     * @param max_size
     *            缓存最大容纳个数
     * @param expire_time
     *            过期时间
     */
    public StringCacheNullable(int max_size, int expire_time, Function<String, String> function_select,
            BiConsumer<String, String> consumer_insert_or_update) {
        cache = CacheBuilder.newBuilder().recordStats().maximumSize(max_size)
                .expireAfterWrite(expire_time, TimeUnit.SECONDS).build();
        fnc_function_select = function_select;
        fnc_consumer_insert_or_update = consumer_insert_or_update;
    }

    /** 如果缓存和数据库中没有或者存的为空值则返回 false **/
    @Override
    public boolean containsKey(String key) {
        return null != getValueByCall(key, fnc_function_select);
    }

    /** 如果缓存中没有则返回 false **/
    @Override
    public boolean containsKeyInMap(String key) {
        return cache.asMap().containsKey(key);
    }

    /** 如果缓存中没有或者存的为空值则返回null */
    @Override
    public String getValue(String key) {
        return getValueByCall(key, fnc_function_select);
    }

    /**
     * @param key
     * @param value
     *            前值
     * @return
     */
    @Override
    public String putValue(String key, String value) {
        return putValueWithByCall(key, value, fnc_consumer_insert_or_update);
    }

    /***
     * @param key
     * @param fnc
     *            执行的查找函数,此函数可以是从持久化或者网路取得
     * @return 可以返回null
     */
    @Override
    public String getValueByCall(String key, Function<String, String> fnc) {
        /*
         * 当fnc能保证绝对能取到非null值的时候采用此方法,另外本身CuncrrentHashMap的K和V都不能为null return
         * cache.get(key, () -> {return fnc.apply(key);});
         */
        String valueBefore = cache.asMap().get(key);
        if (null != valueBefore) {
            return valueBefore;
        }
        String valueNow = fnc.apply(key);
        if (null != valueNow) {
            cache.asMap().put(key, valueNow);
        }
        return valueNow;
    }

    /** 如果 缓存中之前不存在此key,则执行方法,保留数据库中老的值,等缓存过期后遇到更新的时候会更改数据库 */
    @Override
    public String putValueWithByCall(String key, String value, BiConsumer<String, String> consumer_insert_or_update) {
        if (Util.nonEmpty(key, value)) {
            return null;
        }
        String valueBefore = cache.asMap().put(key, value);
        if (Util.isEmpty(valueBefore)) {
            consumer_insert_or_update.accept(key, value);
        }
        return valueBefore;
    }
}
