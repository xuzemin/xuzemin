package com.protruly.floatwindowlib.service;


import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;


public class MyNotificationListenerService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Log.i("gyx","onNotificationPosted");

    }

    @Override
    public void onListenerConnected() {
        Log.i("gyx","onListenerConnected");
        super.onListenerConnected();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.i("gyx","onNotificationRemoved");
    }



}
