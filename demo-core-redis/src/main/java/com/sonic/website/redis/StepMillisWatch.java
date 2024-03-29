package com.sonic.website.redis;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.TimeUtil;
import com.sonic.website.core.common.threadpool.ThreadUtil;

public class StepMillisWatch {
    private final AtomicLong timeCount = new AtomicLong(System.currentTimeMillis());
    private final AtomicInteger count = new AtomicInteger();
    private final Map<Object, Long> history = new LinkedHashMap<>();

    public void reset() {
        timeCount.set(System.currentTimeMillis());
    }
    public void clearHistory(){
        history.clear();
    }
    public long interval() {
        long interval = System.currentTimeMillis() - timeCount.getAndSet(System.currentTimeMillis());
        history.put(TimeUtil.getLocalTimeFull() + "(" + count.getAndIncrement() + ")", interval);
        return interval;
    }

    public String prettyPrint() {
        return JSON.toJSONString(history, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);

    }
    @Test
    public void test() {
        StepMillisWatch sw = new StepMillisWatch();
        new Thread(() -> {
            int n = 5;
            while (n-- > 0) {
                ThreadUtil.sleep(100);
                sw.interval();
            }
        }).start();
        LogCore.BASE.info("test = {}", sw.prettyPrint());
    }
}
