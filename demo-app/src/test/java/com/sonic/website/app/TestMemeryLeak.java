package com.sonic.website.app;

import java.util.concurrent.atomic.AtomicInteger;

public class TestMemeryLeak {

	public static void main(String[] args) throws Exception {
		AtomicInteger n = new AtomicInteger(2000);
		new Thread(() -> {
			while (n.decrementAndGet() > 0) {
				try {
					Thread.sleep(100);
					System.out.println(n);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(() -> {
			while (n.decrementAndGet() > 0) {
				try {
					Thread.sleep(100);
					System.out.println(n);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
