package com.android.jdrd.dudu.application;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by xuzemin on 2017/3/23.
 * init for ifly
 */

public class DuduApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID +"=57d8f09b");
    }
}
