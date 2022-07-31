package com.sonic.website.core.common.threadpool;

import com.sonic.website.core.common.support.LogCore;
/**InterruptedException出现的时机*/

public class ThreadUtil {
    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LogCore.BASE.error("thread sleep interrupted", e);
        }
    }
}