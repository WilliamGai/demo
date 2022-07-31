package com.sonic.website.app.event;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.sonic.website.core.common.cache.LRUCache;
import com.sonic.website.core.common.cache.MyCache;

/**
 * TODO:
 * distributed
 * Listener
 * 将ConcurrentHashMap 改为 LRUCache,使用最简单的缓存<br>
 * 复杂的缓存参考{@link MyCache}
 */
public class EventMsgContext {
    private static final int MAX_CAPACITY = 10000;
    private final static EventMsgContext INSTACNCE = new EventMsgContext();
    private Map<Object, ConcurrentLinkedQueue<String>> map = new LRUCache<>(MAX_CAPACITY);
    
    private EventMsgContext(){
    }
    public static EventMsgContext inst(){
        return INSTACNCE;
    }
    /**
     * 获取
     * @param obj
     * @return ConcurrentLinkedQueue<String>
     */
    public ConcurrentLinkedQueue<String> getMsgQueue(Object obj){
        return map.computeIfAbsent(obj, (k)->new ConcurrentLinkedQueue<>());
    }
    /** 存入
     *  请使用 {@link #putMsg(String)}
     */
    @Deprecated
    public boolean putMsg(Object obj, String msg){
        return getMsgQueue(obj).add(msg);
    }
    /**
     * 给所有监听者添加
     */
    public void putMsg(String msg){
        map.values().forEach(queue->queue.add(msg));
    }
    public void putMsg(Collection<String> msgs){
        map.values().forEach(queue->queue.addAll(msgs));
    }
    public void unregist(Object obj) {
        map.remove(obj);
    }
}
