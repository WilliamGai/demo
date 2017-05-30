package com.sincetimes.website.app.event;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.sincetimes.website.core.common.cache.LRUCache;
import com.sincetimes.website.core.common.cache.MyCache;

/**
 * LRUCache
 * @see {@link MyCache}
 * ConcurrentLinkedQueue和LinkedList是单向队列
 * 使用Stack
 */
public class EventMsgContext {
	private static final int MAX_CAPACITY = 1000;
	private static final int MAX_DEQUE_LENGTH = 16;
	private final static EventMsgContext INSTACNCE = new EventMsgContext();
	private Map<Object, ConcurrentLinkedDeque<String>> map = new LRUCache<>(MAX_CAPACITY);
	
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
	public ConcurrentLinkedDeque<String> getMsgDeque(Object obj){
		return map.computeIfAbsent(obj, (k)->new ConcurrentLinkedDeque<>());
	}
	/**
	 *  {@link #putMsg(String)}
	 */
	@Deprecated
	public boolean putMsg(Object obj, String msg){
		return getMsgDeque(obj).add(msg);
	}
	/**
	 * 给所有监听者添加
	 */
	public void putMsg(String msg){
		map.values().forEach(deque->{
			deque.add(msg);
			if(deque.size() > MAX_DEQUE_LENGTH){
				int n = deque.size() - MAX_DEQUE_LENGTH;
				while(n-->0){
					deque.poll();
				}
			}
		});
	}
	public void putMsg(Collection<String> msgs){
		map.values().forEach(deque->deque.addAll(msgs));
	}
	public void unregist(Object obj) {
		ConcurrentLinkedDeque<?> deque = map.remove(obj);
		if(null != deque)deque.clear();
	}
}
