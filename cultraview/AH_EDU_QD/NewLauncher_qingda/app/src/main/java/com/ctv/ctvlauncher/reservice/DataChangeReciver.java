package com.ctv.ctvlauncher.reservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class DataChangeReciver extends BroadcastReceiver {
    public static boolean isDateUpdateFinish = false;
    public final static String TAG ="TimeUtil";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "DataChangeReciver:action:"+action);
        if(action == null){
            return;
        }

        if(action.equals("android.intent.action.TIME_SET")){
            Log.d(TAG, "DataChangeReciver:TIME_SET");
            isDateUpdateFinish = true;
        }

    }
}
