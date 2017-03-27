package com.sincetimes.website.core.common.core;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.sincetimes.website.core.common.support.LogCore;


/** 缓冲池*/
public class BufferPool {
	private static final int BUFFER_SIZE = 2 * 1024 * 1024;		//缓冲包2M
	
	private static final int BUFFER_MAX_NUM = 20;					//缓冲池保留最大数量
	private static final ConcurrentLinkedQueue<byte[]> buffers = new ConcurrentLinkedQueue<byte[]>();
	
	private static AtomicInteger countCreated = new AtomicInteger(0);	//已创建的缓冲包个数

	/** 分配  */
	public static byte[] allocate() {
		byte[] result = buffers.poll();
		//创建新缓冲包
		if(result == null) {
			result = new byte[BUFFER_SIZE];
			
			//记录创建个数
			int count = countCreated.incrementAndGet();
			
			//日志
			if(count <= BUFFER_MAX_NUM) {
				LogCore.CORE.info("创建新的BufferPool缓冲池，已创建总数量：count={}", count);
			} else {
				LogCore.CORE.warn("创建新的BufferPool缓冲池，已创建总数量：count={}", count, new Throwable());
			}
		}
		
		//日志
//		if(LogCore.core.isDebugEnabled()) {
//			LogCore.core.debug("BufferPool缓冲池分配，当前数量={}， 已创建总数量={}", buffers.size(), countCreated);
//		}
		
		return result;
	}
	
	/**
	 * 回收
	 * @param buff 
	 */
	public static void deallocate(byte[] buff) {
		//缓冲池已达上限
		if(buffers.size() >= BUFFER_MAX_NUM) return;
		//回收的缓冲大小必须正确
		if(buff.length != BUFFER_SIZE) return;
		
		//加回到池中
		buffers.add(buff);
		
		//日志
//		if(LogCore.core.isDebugEnabled()) {
//			LogCore.core.debug("BufferPool缓冲池回收，当前数量={}， 已创建总数量={}", buffers.size(), countCreated);
//		}
	}
}
