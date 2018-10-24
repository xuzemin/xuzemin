package com.android.androidlauncher.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackWackService extends Service {
    private static Thread backthread;
    private static boolean isRun = false;
    private static int CurrentNumber;

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
        if(isRun && backthread != null){
            CurrentNumber = 60;
        }else{
            startThread();
        }
    }

    public void startThread(){
        isRun = true;
        CurrentNumber = 60;
        backthread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    CurrentNumber --;
                    if(CurrentNumber == 0){
                        isRun = false;
                    }
                }
            }
        });
    }

    public void stopThread(){
        isRun = false;
        if(backthread != null){
            backthread.interrupt();
            backthread = null;
        }
    }
}
