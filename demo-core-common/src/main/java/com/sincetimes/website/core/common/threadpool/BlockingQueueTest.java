package com.sincetimes.website.core.common.threadpool;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class BlockingQueueTest {
	
	public static void main(String args[]){
		BlockingQueue<String> queue;
		LinkedBlockingDeque<String> de;
		LinkedBlockingQueue<String> qe;
		ArrayBlockingQueue<String> ae = new ArrayBlockingQueue<>(4);
		Executors.newCachedThreadPool();
		int nThreads = 40;
		Executors.newFixedThreadPool(nThreads);
		int corePoolSize = 10;
		ScheduledExecutorService ss = Executors.newScheduledThreadPool(corePoolSize);
		Executors.newSingleThreadExecutor();
		CopyOnWriteArrayList c;
		ArrayList aa;
	}
}
