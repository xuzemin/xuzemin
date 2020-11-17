package com.ctv.annotation;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.ctv.annotation.utils.BaseUtils;
import com.ctv.annotation.utils.SharedPreferencesUtils;
import com.ctv.annotation.utils.ToofifiLog;

import java.util.Vector;

public class WhiteBoardApplication extends Application {

    private static final String TAG = WhiteBoardApplication.class.getSimpleName();

        /**
         * 是否可用
         */
        public static boolean isWhiteBoardEnable = false;

        /**
         * 用于检测是否根据触摸size进入橡皮擦模式
         */
        public static boolean isAutoEnterEraseMode = false;

        /**
         * 用于控制画板是否自动恢复为画笔状态
         */
        public static boolean isTurnBackStatus = false;

        public static Vector<Activity> mActivityList;
        public static Context context = null;
        private static WhiteBoardApplication mInstance;

        public static ToofifiLog AppLogObj;

        private int mActivityCount;

        public static int SCREEN_WIDTH = 1920;
        public static int SCREEN_HEIGHT = 1080;

        public static WhiteBoardApplication getInstance()
        {
            return mInstance;
        }

        @Override
        public void onCreate()
        {
            super.onCreate();
            mInstance = this;
            BaseUtils.dbg(TAG,"--onCreate--");
            boolean autoErase = (boolean) SharedPreferencesUtils.getParam("isAutoErase", true);
            isAutoEnterEraseMode = autoErase;
            context =this ;
            AppLogObj = new ToofifiLog(getApplicationContext(), ToofifiLog.LOG_LEVEL_INFO);
            AppLogObj.startThread();

            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState)
                {

                }

                @Override public void onActivityStarted(Activity activity)
                {

                }

                @Override public void onActivityResumed(Activity activity)
                {
                    mActivityCount++;
                    BaseUtils.dbg("ww","--onActivityResumed--");
                    sendBroadcast(new Intent("tff.floatbar.hide").setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES));
                }

                @Override public void onActivityPaused(Activity activity)
                {
                    mActivityCount--;

                    /**此时启动floatBar需要延时，确认是否有下一个activity进入*/
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override public void run()
                        {
                            if (mActivityCount <= 0)
                            {
                                BaseUtils.startFloatBarActivity(getApplicationContext());
//                            System.exit(0);
                            }
                        }
                    }, 150);
                }

                @Override public void onActivityStopped(Activity activity)
                {
                }

                @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState)
                {

                }

                @Override public void onActivityDestroyed(Activity activity)
                {

                }
            });

        isWhiteBoardEnable = true;
    }

    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        Log.e(TAG, " onTerminate () ");
        BaseUtils.startFloatBarActivity(getApplicationContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        Log.e(TAG, " onConfigurationChanged () ");
    }

    public static void addActivity(Activity act)
    {
        if (mActivityList == null)
        {
            mActivityList = new Vector<>();
        }
        if (mActivityList != null && mActivityList.size() > 0)
        {
            for (Activity temp : mActivityList)
            {
                if (temp.getClass().getName().equals(act.getClass().getName()))
                {
                    temp.finish();
                    mActivityList.remove(temp);
                }
            }
        }
        mActivityList.add(act);
    }

    public static void removeActivity(Activity act)
    {
        if (mActivityList != null && mActivityList.size() > 0 && mActivityList.contains(act))
        {
            mActivityList.remove(act);
        }
    }

    public static void exitAllActivities()
    {
        if (mActivityList != null && mActivityList.size() > 0)
        {
            for (Activity temp : mActivityList)
            {
                temp.finish();
            }
            mActivityList = null;
        }
    }

}
