package com.sonic.website.core.common;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

    public static final String CONTAINER_KEY = "container";

    public static final String SHUTDOWN_HOOK_KEY = "shutdown.hook";
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    
    private static volatile boolean running = true;

    public static void main(String[] args) {
        try {
;            logger.info("Use container type(" + Arrays.toString(args) + ") to run dubbo serivce.");
            
            if ("true".equals(System.getProperty(SHUTDOWN_HOOK_KEY))) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        synchronized (Main.class) {
                            running = false;
                            Main.class.notify();
                        }
                    }
                });
            }
            
            System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + "service server started!");
        } catch (RuntimeException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
        System.setProperty(SHUTDOWN_HOOK_KEY, "true");
        String s = System.getProperty(SHUTDOWN_HOOK_KEY);
        System.out.println(s);

        synchronized (Main.class) {
            while (running) {
                System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + "  service server started!");

                try {
                    Main.class.wait();
                } catch (Throwable e) {
                }
            }
        }
    }
    
}