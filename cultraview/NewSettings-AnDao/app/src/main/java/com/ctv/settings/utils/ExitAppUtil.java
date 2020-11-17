package com.ctv.settings.utils;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wlb on 2017/5/11.
 * android退出程序的工具类，使用单例模式
 * 1.在Activity的onCreate()的方法中调用addActivity()方法添加到mActivityList
 * 2.可以在Activity的onDestroy()的方法中调用delActivity()来删除已经销毁的Activity实例
 * 避免了mActivityList容器中有多余的实例而影响程序退出速度
 * @author xiaanming
 */
public class ExitAppUtil {
    /**
     * 转载Activity的容器
     */
    private List<Activity> mActivityList = new LinkedList<Activity>();
//    private static ExitAppUtil instance = new ExitAppUtil();

    private volatile static ExitAppUtil singleton;

    private ExitAppUtil (){}

    /**
     * 获得实例
     * @return
     */
    public static ExitAppUtil getInstance() {
        if (singleton == null) {
            synchronized (ExitAppUtil.class) {
                if (singleton == null) {
                    singleton = new ExitAppUtil();
                }
            }
        }
        return singleton;
    }

    /**
     * 添加Activity实例到mActivityList中，在onCreate()中调用
     * @param activity
     */
    public void addActivity(Activity activity){
        if (!mActivityList.contains(activity)){
            mActivityList.add(activity);
        }
    }

    /**
     * 从容器中删除多余的Activity实例，在onDestroy()中调用
     * @param activity
     */
    public void delActivity(Activity activity){
        if (mActivityList.contains(activity)){
            mActivityList.remove(activity);
        }
    }

    public void clear(){
        mActivityList.clear();
    }

    /**
     * 退出程序的方法
     */
    public void exit(){
        for(Activity activity : mActivityList){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }

//        new Thread(() -> {
//            SystemClock.sleep(3000);
////            System.exit(0);
//            android.os.Process.killProcess(android.os.Process.myPid());
//        }).start();
//        System.exit(0);
    }

}
