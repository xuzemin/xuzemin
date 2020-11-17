package com.ctv.ctvlauncher.bean;

import android.graphics.drawable.Drawable;

public class mAppInfo {
    private String packName;
    private Drawable AppIcon;
    private String appName;

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public Drawable getAppIcon() {
        return AppIcon;
    }

    public void setAppIcon(Drawable AppIcon) {
        this.AppIcon = AppIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
