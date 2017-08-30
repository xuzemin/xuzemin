package com.d3.yiqi.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

public class NotiUtil {
	public static void setNotiType(Context context,int iconId, String note) {
	    NotificationManager mNotificationManager = 
	    		(NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent appIntent = PendingIntent.getActivity(context, 0, intent, 0);
		Notification myNoti = new Notification();
		myNoti.icon = iconId;
		myNoti.tickerText = note;
		myNoti.defaults = Notification.DEFAULT_SOUND;
		myNoti.defaults = Notification.DEFAULT_VIBRATE; 
//		myNoti.setLatestEventInfo(context, "系统公告", note, appIntent);
		mNotificationManager.notify(0, myNoti);
	}
}
