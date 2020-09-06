package com.ctv.newlauncher.reservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ctv.newlauncher.utils.TimeUtil;
import com.mstar.android.tvapi.common.exception.TvCommonException;

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
//        try {
//           TimeUtil.initTime(context,false,0);
//        } catch (TvCommonException e) {
//            e.printStackTrace();
//        }
        if(action.equals("android.intent.action.TIME_SET")){
            Log.d(TAG, "DataChangeReciver:TIME_SET");
            isDateUpdateFinish = true;
        }

    }
}
