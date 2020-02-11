package com.hht.middleware.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.hht.middleware.base.FragmentUtils;

import java.util.Stack;


/**
 * Author: chenhu
 * Time: 2019/12/12 14:15
 * Description  Activity的生命周期进行管理
 */

public class ApplicationManager {

    private static Stack<Activity> activityStack;

    private ApplicationManager() {
    }

    private static class Holder {
        static ApplicationManager holder = new ApplicationManager();
    }

    public static ApplicationManager getInstance() {
        return Holder.holder;
    }

    /**
     * 添加Activity到堆栈中
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后压入的）
     */
    public Activity getCurrentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后压入的）
     */
    public void finishCurrentActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && activityStack.contains(activity)) {
            activityStack.remove(activity);
            activity.finish();
        }
    }


    /**
     * 结束所有Activity
     */
    private void finishAllActivity() {
        for (int i = 0; i < activityStack.size(); i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
    }

    /**
     * 彻底退出应用程序，安全退出
     */
    public void mExitApplication() {
        try {
            finishAllActivity();
            ActivityManager manager = (ActivityManager) MiddleWareApplication.getInstance().
                    getSystemService(Context.ACTIVITY_SERVICE);
            if (manager != null) {
                manager.restartPackage(MiddleWareApplication.getInstance().getPackageName());
            }
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
