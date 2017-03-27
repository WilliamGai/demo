package com.sincetimes.website.core.common;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main. (API, Static, ThreadSafe)
 * @author william.liangf
 */
public class Main2 {

	public static final String CONTAINER_KEY = "dubbo.container";

	public static final String SHUTDOWN_HOOK_KEY = "dubbo.shutdown.hook";

	private static final Logger logger = LoggerFactory.getLogger(Main2.class);

	private static volatile boolean running = true;

	public static void main(String[] args) {
		try {
			logger.info("Use container type(" + Arrays.toString(args) + ") to run dubbo serivce.");

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.err.println(
							new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + "  service shutdown!~");
					synchronized (Main2.class) {
						running = false;
						Main2.class.notify();
					}
				}
			});

			System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date())
					+ " Dubbo service server started!");
		} catch (RuntimeException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			System.exit(1);
		}
		System.setProperty(SHUTDOWN_HOOK_KEY, "true");
		String s = System.getProperty(SHUTDOWN_HOOK_KEY);
		System.out.println(s);

		synchronized (Main2.class) {
			while (running) {
				System.out.println(
						new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + "  service server started!");

				try {
					Main2.class.wait();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

}