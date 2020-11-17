package com.ctv.ctvlauncher.manage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.SystemProperties;
import android.util.Log;

import com.ctv.ctvlauncher.MainActivity;
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
        BLACKLIST_PKG.add(pk4);
        initlist();

}
        public void initlist(){

        //    if (SystemProperties.get("ro.build.display.id").equals("CN8386_AH_EDU")){

          //  }else {
              //  BLACKLIST_PKG. add("com.android.toofifi");
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



}
