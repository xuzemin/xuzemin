package com.android.jdrd.robot.activity;

import android.app.Application;

import com.android.jdrd.robot.helper.RobotDBHelper;

/**
 * Created by jdrd on 2017/6/14.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RobotDBHelper.getInstance(getApplicationContext());
    }
}
