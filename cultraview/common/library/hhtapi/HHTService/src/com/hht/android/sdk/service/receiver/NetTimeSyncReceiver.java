package com.hht.android.sdk.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hht.android.sdk.service.utils.TimeTools;

/**
 * @Description: 同步网络时间的广播
 * @Author: wanghang
 * @CreateDate: 2020/3/6 13:22
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/3/6 13:22
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class NetTimeSyncReceiver extends BroadcastReceiver {
    private final static String TAG = NetTimeSyncReceiver.class.getSimpleName();

    private static Thread getNetTimeThread;

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
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET) {
                        Log.e(TAG, "当前ETHERNET网络连接可用 ");
                    }
                    isNewWorkAvailable = true;

                    // sync net time
                    if(TimeTools.getTimeSettingAuto(context)){
                        syncNetTime(context.getApplicationContext());
                    }
                } else {
                    Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                    isNewWorkAvailable = false;
                }

                Log.e(TAG, "info.getTypeName()" + activeNetwork.getTypeName());
                Log.e(TAG, "getSubtypeName()" + activeNetwork.getSubtypeName());
                Log.e(TAG, "getState()" + activeNetwork.getState());
                Log.e(TAG, "getDetailedState()" + activeNetwork.getDetailedState().name());
                Log.e(TAG, "getDetailedState()" + activeNetwork.getExtraInfo());
                Log.e(TAG, "getType()" + activeNetwork.getType());
            } else { // not connected to the internet
                isNewWorkAvailable = false;
                Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
            }
        }
    }

    /**
     * 同步网络时间
     */
    private void syncNetTime(final Context context) {
        if (getNetTimeThread == null) {
            getNetTimeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    TimeTools.syncNetTime(context);
                    getNetTimeThread = null;
                }
            });
            getNetTimeThread.start();
        }
    }
}
