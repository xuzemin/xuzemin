package com.android.wifi.socket.recieve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Administrator on 2016/8/23.
 */
public class WifiBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("arui.alarm.action")) {
//            Bundle bundle = intent.getExtras();
//            Intent i = new Intent();
//            i.putExtras(bundle);
//            i.setClass(context, WifiConnectService.class);
//            // 启动service
//            // 多次调用startService并不会启动多个service 而是会多次调用onStart
//            context.startService(i);
        }
    }
}

