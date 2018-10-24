package com.android.androidlauncher.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.androidlauncher.utils.MyConstant;

public class BackWackService extends Service {
    private static Thread backthread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void resetBack(){
        if(MyConstant.isVideoPlay && backthread != null){
            MyConstant.CurrentNumber = 5;
        }else{
            startThread();
        }
    }

    public void startThread(){
    }

    public void stopThread(){
    }
}
