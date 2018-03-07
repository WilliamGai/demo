package com.sonic.website.test;

import java.util.concurrent.atomic.AtomicLong;

import com.sonic.website.core.common.support.HttpUtil;

public class Test {
	public static void main(String args[]){
		AtomicLong n = new AtomicLong();
		while(n.incrementAndGet()<101){
			new Thread(()->{HttpUtil.sendGet("http://localhost:8001/tst?a="+n.get());}).start();
			
		}
	}
//	@org.junit.Test 
//	public void test(){
//		int n = 100;
//		while(n-->0){
//			new Thread(()->{HttpUtil.sendGet("http://localhost:8001/tst");}).start();
//			
//		}
//	}

}
