package com.protruly.floatwindowlib.been;

import android.graphics.drawable.Drawable;

/**
 * Created by gaoyixiang on 2019/5/20.
 */

public class AppInfo {
    public String packageName;
    public String appName ;
    public Drawable   appIcon;
    public void setAppName(String appName){
        this.appName=appName;
    }
    public void setPackName(String packageName){
        this.packageName=packageName;
    }
   public  void setAppIcon(Drawable appIcon){
        this.appIcon=appIcon;
    }
    public String getAppName(){
        return appName;
    }
    public String getPackName(){
        return packageName;
    }
    public  Drawable getAppIcon(){
        return appIcon;
    }

}
