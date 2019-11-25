
package com.ctv.welcome.task;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
    private static ThreadPoolProxy threadPoolProxy;

    public static class ThreadPoolProxy {
        private int corePoolSize;

        private long keepAliveTime;

        private int maximumPoolSize;

        private ThreadPoolExecutor threadPoolExecutor;

        ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTieme) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTieme;
        }

        public void excute(Runnable runnable) {
            this.threadPoolExecutor = new ThreadPoolExecutor(this.corePoolSize,
                    this.maximumPoolSize, this.keepAliveTime, TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque(), Executors.defaultThreadFactory());
            this.threadPoolExecutor.execute(runnable);
        }
    }

    public static ThreadPoolProxy getThreadPoolProxy() {
        if (threadPoolProxy == null) {
            threadPoolProxy = new ThreadPoolProxy(5, 10, 5);
        }
        return threadPoolProxy;
    }
}
