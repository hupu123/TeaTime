package com.hugh.teatime.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池工具
 * Created by Hugh on 2016/3/30 10:01
 */
public class ThreadPoolUtil {

    // 自身实体
    private static ThreadPoolUtil instance;
    // 线程池管理对象
    private ExecutorService executorService;
    // 线程池大小
    private final int THREAD_POOL_SIZE = 5;

    /**
     * 私有的构造函数
     */
    private ThreadPoolUtil() {

        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    /**
     * 获取自身实例
     *
     * @return 自身实例
     */
    public static ThreadPoolUtil getInstance() {

        if (instance == null) {
            instance = new ThreadPoolUtil();
        }

        return instance;
    }

    /**
     * 添加新线程到线程池
     *
     * @param runnable 新线程
     */
    public void addThread(Runnable runnable) {

        if (executorService == null || executorService.isShutdown() || executorService.isTerminated()) {
            return;
        }
        executorService.submit(runnable);
    }
}
