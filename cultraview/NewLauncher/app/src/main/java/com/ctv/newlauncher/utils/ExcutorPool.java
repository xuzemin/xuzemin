package com.ctv.newlauncher.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExcutorPool {
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static Future<?> submit(Runnable runnable) {
        return executor.submit(runnable);
    }

    public static void start() {
        executor = Executors.newCachedThreadPool();
    }

    public static void destroy() {
        executor.shutdown();
        if (!executor.isTerminated()) {
            executor.shutdownNow();
        }
    }
}
