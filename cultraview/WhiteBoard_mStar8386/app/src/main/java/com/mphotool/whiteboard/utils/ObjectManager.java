package com.mphotool.whiteboard.utils;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wjun on 2018/04.
 */
public class ObjectManager {

    public static final String TAG = ObjectManager.class.getSimpleName();

    private static final int MAX_COUNT_LOG_DATA_INFO = 1064;

    private static BlockingQueue<LogDataInfo> logDataInfoQueue = new ArrayBlockingQueue<>(MAX_COUNT_LOG_DATA_INFO);

    private static int log_data_info_allocated_count = 0;


    private static Lock LogDataInfoObj_lock = new ReentrantLock();


    public static LogDataInfo allocateLogDataInfoObj()
    {
        LogDataInfo obj;
        LogDataInfoObj_lock.lock();

        obj = logDataInfoQueue.poll();
        if (obj == null)
        {
            if (log_data_info_allocated_count < MAX_COUNT_LOG_DATA_INFO)
            {
                obj = new LogDataInfo();
                obj.logContent = new StringBuilder();

                obj.data_len = 0;

                log_data_info_allocated_count++;
                //listPicDataInfo.add(obj);
            }
        }

        LogDataInfoObj_lock.unlock();
        return obj;
    }

    public static void freeLogDataInfoQueueObj(LogDataInfo obj)
    {
        LogDataInfoObj_lock.lock();

        if (!logDataInfoQueue.offer(obj))
        {
            Log.e(TAG, "freeLogDataInfoQueueObj error");
        }

        LogDataInfoObj_lock.unlock();
    }


}
