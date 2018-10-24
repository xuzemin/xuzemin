package com.android.androidlauncher.activity;

import android.app.Application;
import android.content.Intent;

import com.android.androidlauncher.Service.BackWackService;
import com.android.androidlauncher.callback.SimpleLifecyclecallbacl;

public class LauncherApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new SimpleLifecyclecallbacl());
    }

}
