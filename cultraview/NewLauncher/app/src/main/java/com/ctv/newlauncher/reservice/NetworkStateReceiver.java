package com.ctv.newlauncher.reservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {
    private final static String TAG = "TimeUtil";
    private Thread getNetTimeThread;
    public static boolean isNewWorkAvailable = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Log.i(TAG, "CONNECTIVITY_ACTION");

            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.isConnected()) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi

                        Log.e(TAG, "当前WiFi连接可用 ");
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connected to the mobile provider's data plan

                        Log.e(TAG, "当前移动网络连接可用 ");
                    }else if(activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET){
                        Log.e(TAG, "当前ETHERNET网络连接可用 ");
                    }
                    isNewWorkAvailable = true;
                  /*  try {
                        Thread.sleep(3000);
                        TimeUtil.initTime(false,0);
                        isNewWorkAvailable = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                       /*new Thread(new Runnable() {
                           @Override
                           public void run() {
                               try {
                                   TimeUtil.getTime();
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                           }
                       }).start();*/

                } else{
                    Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                    isNewWorkAvailable = false;
                }


                Log.e(TAG, "info.getTypeName()" + activeNetwork.getTypeName());
                Log.e(TAG, "getSubtypeName()" + activeNetwork.getSubtypeName());
                Log.e(TAG, "getState()" + activeNetwork.getState());
                Log.e(TAG, "getDetailedState()"
                        + activeNetwork.getDetailedState().name());
                Log.e(TAG, "getDetailedState()" + activeNetwork.getExtraInfo());
                Log.e(TAG, "getType()" + activeNetwork.getType());
            } else {   // not connected to the internet
                DataChangeReciver.isDateUpdateFinish = false;
                isNewWorkAvailable = false;
                Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");


            }


        }

    }
}
