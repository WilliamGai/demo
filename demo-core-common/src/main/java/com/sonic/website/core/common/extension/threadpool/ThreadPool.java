
package com.sonic.website.core.common.extension.threadpool;

import java.util.concurrent.Executor;
/**
 * Executor是jse线程池相关的顶级接口
 */
public interface ThreadPool {
    
    /**
     * 线程池
     * @return 线程池
     */
    Executor getExecutor();

}