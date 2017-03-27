package com.sincetimes.website.core.common;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.sincetimes.website.core.common.support.LogCore;

/**
 * Main. (API, Static, ThreadSafe)
 * @author william.liangf
 */
public class Main3 {

	public static final String CONTAINER_KEY = "dubbo.container";

	public static final String SHUTDOWN_HOOK_KEY = "dubbo.shutdown.hook";

	public static void main(String[] args) {
		try {
			LogCore.BASE.info("Use container type(" + Arrays.toString(args) + ") to run dubbo serivce.");

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.err.println(
							new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + "  service shutdown!~");
				}
			});

		} catch (RuntimeException e) {
			e.printStackTrace();
			LogCore.BASE.error(e.getMessage(), e);
			System.exit(1);
		}System.exit(1);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}