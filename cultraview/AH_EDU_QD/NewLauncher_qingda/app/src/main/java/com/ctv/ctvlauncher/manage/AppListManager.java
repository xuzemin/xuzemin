package com.ctv.ctvlauncher.manage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.SystemProperties;
import android.util.Log;

import com.ctv.ctvlauncher.R;
import com.ctv.ctvlauncher.bean.AppInfo;
import com.ctv.ctvlauncher.compat.LauncherActivityInfoCompat;
import com.ctv.ctvlauncher.compat.LauncherActivityInfoCompatV16;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppListManager {

    private static final String TAG = "AppListManager";
    private Context mContext;
    public final List<String> BLACKLIST_PKG = new ArrayList<String>();

    public AppListManager(Context context){

        this.mContext = context;
    }
    public AppListManager(Context context,String pk1,String pk2,String pk3,String pk4) {
        this.mContext = context;

        Log.d(TAG, "AppListManager:  pk1 = "+ pk1  +"pk2  ="+ pk2);
        Log.d(TAG, "AppListManager:  pk3 = "+ pk3  +"pk4  ="+ pk4);
        BLACKLIST_PKG.clear();
        BLACKLIST_PKG.add(pk1);
        BLACKLIST_PKG.add(pk2);
        BLACKLIST_PKG.add(pk3);
        //BLACKLIST_PKG.add(pk4);
        initlist();

}
        public void initlist(){

        //    if (SystemProperties.get("ro.build.display.id").equals("CN8386_AH_EDU")){

          //  }else {
         //       BLACKLIST_PKG. add("com.android.toofifi");
                BLACKLIST_PKG. add("com.dazzle.timer");
                BLACKLIST_PKG. add("com.dazzlewisdom.screenrec");
                BLACKLIST_PKG. add("com.example.newmagnifier");
                BLACKLIST_PKG.add ("com.example.cutcapture");
       //     }
            BLACKLIST_PKG.add (   "mstar.factorymenu.ui.hh");

       //     BLACKLIST_PKG. add("com.android.camera2");
            BLACKLIST_PKG. add("com.tv.filemanager");
            BLACKLIST_PKG. add("com.android.cultraview.launcher.whiteboard");
            BLACKLIST_PKG. add("com.android.cultraview.floatbuttonview");
            BLACKLIST_PKG. add("com.protruly.floatwindowlib");
            BLACKLIST_PKG. add("com.ctv.easytouch");
            BLACKLIST_PKG. add("com.ctv.imageselect");
            BLACKLIST_PKG. add("com.cultraview.annotate");
            BLACKLIST_PKG. add("com.example.launch_2");
            BLACKLIST_PKG. add("com.ctv.newlauncher");
            BLACKLIST_PKG. add("com.ctv.ctvlauncher");
            BLACKLIST_PKG. add("com.inpor.fastmeetingcloud");
            BLACKLIST_PKG. add("com.bozee.meetingmark");
            BLACKLIST_PKG. add("com.bozee.remoteserver");
            BLACKLIST_PKG. add("com.bozee.registerclient");
            BLACKLIST_PKG. add("com.bozee.dlna.server");
            BLACKLIST_PKG. add("com.mstar.tv.tvplayer.ui");
            BLACKLIST_PKG. add("com.mstar.netplayer");
            BLACKLIST_PKG. add("mstar.factorymenu.ui");
            BLACKLIST_PKG. add("com.ctv.annotation");
          //  BLACKLIST_PKG. add("com.android.camera2");
            BLACKLIST_PKG. add("com.android.tv.settings");
            BLACKLIST_PKG. add("com.ctv.sourcemenu");
            BLACKLIST_PKG. add("com.protruly.floatwindowlib.virtualkey");
            BLACKLIST_PKG. add("com.android.tv.settings");
            BLACKLIST_PKG.add("com.android.browser");

        }
    public List<LauncherActivityInfoCompat> getLaunchAppList(String str) {
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN", null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(str);
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        List<LauncherActivityInfoCompat> arrayList = new ArrayList(queryIntentActivities.size());
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            if (!resolveInfo.activityInfo.packageName.equals(this.mContext.getPackageName())) {

                if (!BLACKLIST_PKG.contains(resolveInfo.activityInfo.packageName)) {
                    arrayList.add(new LauncherActivityInfoCompatV16(this.mContext, resolveInfo));
                }
            }
        }
        return arrayList;
    }



    public List<AppInfo> dialog_scanInstallApp(PackageManager pm) {
        Log.d("hhh", "dialog_scanInstallApp: get listapp");
        List<AppInfo> appInfos = new ArrayList<>();
        // 获得PackageManager对象

        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        for (ApplicationInfo app : listAppcations) {
            Intent intent = pm.getLaunchIntentForPackage(app.packageName);
            if (intent!=null){
                Log.d("hhh", "dialog_scanInstallApp-------    list each app.packagename");
                if (
//                        app.packageName.equals("com.ctv.settings")
//                        ||app.packageName.equals("com.cv.apk.manager")
//                        ||app.packageName.equals("com.ctv.welcome")
//                        ||app.packageName.equals("com.mysher.mtalk")
//                            ||    app.packageName.equals("com.android.toofifi")
//                        ||app.packageName.equals("com.jrm.localmm")
//                        ||app.packageName.equals("com.mphotool.whiteboard")
//


                    //    app.packageName.equals("com.android.camera2")
                               app.packageName.equals("mstar.factorymenu.ui.hh")
                                ||app.packageName.equals("com.tv.filemanager")
                                || app.packageName.equals("com.android.cultraview.launcher.whiteboard")
                                || app.packageName.equals("com.android.cultraview.floatbuttonview")
                                || app.packageName.equals("com.protruly.floatwindowlib")
                                || app.packageName.equals("com.ctv.easytouch")
                 //                      ||    app.packageName.equals("com.android.toofifi")
                                ||    app.packageName.equals("com.ctv.imageselect")
                                ||   app.packageName.equals("com.cultraview.annotate")
                                ||   app.packageName.equals("com.example.launch_2")
                                ||   app.packageName.equals("com.ctv.newlauncher")
                                ||  app.packageName.equals("com.ctv.ctvlauncher")
                                ||   app.packageName.equals("com.inpor.fastmeetingcloud")
                                ||     app.packageName.equals("com.bozee.meetingmark")
                                // ||       app.packageName.equals("com.example.newmagnifier")
                                ||     app.packageName.equals("com.bozee.remoteserver")
                                ||      app.packageName.equals("com.bozee.registerclient")
                                ||     app.packageName.equals("com.bozee.dlna.server")
                                ||       app.packageName.equals("com.mstar.tv.tvplayer.ui")
                                ||        app.packageName.equals("com.mstar.netplayer")
                                ||       app.packageName.equals("mstar.factorymenu.ui")
                                ||    app.packageName.equals("com.ctv.annotation")
                                       || app.packageName.equals("com.android.browser")
                                //         ||     app.packageName.equals("com.dazzlewisdom.screenrec")
                                //         ||    app.packageName.equals("com.dazzle.timer")
                              //  ||    app.packageName.equals("com.android.camera2")
                                ||     app.packageName.equals("com.android.tv.settings")
                                ||     app.packageName.equals("com.ctv.sourcemenu")
                                ||   app.packageName.equals("com.protruly.floatwindowlib.virtualkey")
                                ||   app.packageName.equals("com.android.tv.settings")

//                                ||       app.packageName.equals(packagename_1)
//                                ||       app.packageName.equals(packagename_2)
//                                ||       app.packageName.equals(packagename_3)
//                                ||       app.packageName.equals(packagename_4)
                ){
                    Log.d("hhh", "dialog_scanInstallApp---------   list ---packagename ="+app.packageName);
                    continue;


                } else {
                    appInfos.add(getAppInfo(app,pm));
                }
            }
        }
        return appInfos;
    }
    private AppInfo getAppInfo(ApplicationInfo app, PackageManager pm) {
        Log.d("hhh", "-----getAppInfo---- set photo");
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName(pm.getApplicationLabel(app).toString());//应用名称
        if (app.packageName .equals("com.mphotool.whiteboard")){
            Log.d("hhh", "photo: com.mphotool.whiteboard ");
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.mipmap.bt_whiteboard_focus));
        }else if (app.packageName .equals( "com.jrm.localmm")){
            Log.d("hhh", " photo : com.jrm.localmm");
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.mipmap.bt_media_icon_normal));
        }else if (app.packageName .equals("com.mysher.mtalk")){
            Log.d("hhh", "com.mysher.mtalk ");
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.mipmap.bt_video_normal));
        }else if (app.packageName.equals("com.ctv.settings")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.mipmap.bt_settings_icon_normal));
        }else if (app.packageName.equals("com.jxw.launcher")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.drawable.wiedu));
            appInfo.setAppName(mContext.getString(R.string.wiedu));
        }else if (app.packageName.equals("com.example.newmagnifier")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.drawable.fangda));
        }else if (app.packageName.equals("com.android.calculator2")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.drawable.bt_calculater_normal));
        }else if (app.packageName.equals("com.xingen.camera")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.drawable.takephoto));
            appInfo.setAppName(mContext.getString(R.string.camera));
        }else if (app.packageName.equals("cn.wps.moffice_eng")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.drawable.bt_wps_normal));
        }else if (app.packageName.equals("com.dazzlewisdom.screenrec")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.drawable.screenboard));
        }else if (app.packageName.equals("com.dazzle.timer")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.drawable.tmier));

        }else if(app.packageName.equals("com.tencent.androidqqmail")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.mipmap.qqyouxiang));
            appInfo.setPackName(mContext.getResources().getString(R.string.qqemail));
        }
        else {
            Log.d("hhh", "getAppInfo: photo of system ");
            Log.d("hhh", "getAppInfo: Packagename ="+app.packageName);
            appInfo.setAppIcon(app.loadIcon(pm));
        }
        appInfo.setPackName(app.packageName);//应用包名，用来卸载
        return appInfo;
    }
}
