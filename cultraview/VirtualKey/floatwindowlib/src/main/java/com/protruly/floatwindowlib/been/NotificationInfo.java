package com.protruly.floatwindowlib.been;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

public class NotificationInfo {

    public StatusBarNotification getSbn() {
        return sbn;
    }

    public void setSbn(StatusBarNotification sbn) {
        this.sbn = sbn;
    }

    StatusBarNotification sbn;

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    Notification notification;
    Bundle extras;

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    PendingIntent pendingIntent;
    Context mContext;

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    Drawable drawable;

    public NotificationInfo(Context context, StatusBarNotification sbn) {
        this.sbn = sbn;
        this.notification = sbn.getNotification();
        this.extras = notification.extras;
        this.title = extras.getString(Notification.EXTRA_TITLE, "none");
        this.text = extras.getString(Notification.EXTRA_TEXT, "none");
        this.pendingIntent = notification.contentIntent;
        this.mContext = context;
        Icon smallIcon = notification.getSmallIcon();
        drawable = smallIcon.loadDrawable(context);
    }

    public String getTitle() {
        return title;
    }


    String title;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    String text;
}
