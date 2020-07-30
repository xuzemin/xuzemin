package com.ctv.easytouch.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.ctv.easytouch.Constants;
import com.ctv.easytouch.been.AppInfo;
import com.mstar.android.tv.TvCommonManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 获得apk信息
 */
public class ApkInfoUtils {
    private static final String TAG = ApkInfoUtils.class.getSimpleName();

    public static final String[] ignoreApps = new String[]{
            "com.android.cultraview.launcher.whiteboard",
            "com.android.cultraview.floatbuttonview",
            "com.protruly.floatwindowlib",
            "com.ctv.imageselect",
            "com.cultraview.annotate",
            "com.ctv.whiteboard",
            "com.jrm.localmm",
            "com.android.toofifi",
            "com.android.camera2",
            "com.ctv.easytouch",
            "com.ctv.ctvlauncher",
            "com.android.tv.settings",
            "com.mstar.netplayer",
            "com.protruly.floatwindowlib.virtualkey",
            "com.mstar.tv.tvplayer.ui",
            "com.ctv.sourcemenu",
    };

    public List<AppInfo> scanInstallApp(Context context) {
        Log.d("TAG","scanInstallApp");
        List<String> ignoreList = java.util.Arrays.asList(ignoreApps);

        List<AppInfo> appInfos = new ArrayList<>();
        PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        String userPackageName = (String) SPUtil.getData(context, Constants.Companion.getUSERED_PACKAGE_NAME(), "");

        for (ApplicationInfo app : listAppcations) {
            if (app.packageName == null) {
                Log.d(TAG, "appPackName:" + "null");
            } else {
                Log.d(TAG, "appPackName:" + app.packageName);
            }

            Intent intent = pm.getLaunchIntentForPackage(app.packageName);
            if (intent != null) {

                if (ignoreList.contains(app.packageName)) {
                    continue;
                }
                if (!TextUtils.isEmpty(userPackageName)) {
                    if (userPackageName.contains(app.packageName)) {
                        Log.d("TAG","scanInstallApp  packageName"+app.packageName);
                        continue;
                    }
                }


                appInfos.add(getAppInfo(app, pm));
            }
        }
        return appInfos;
    }

    public AppInfo scanInstallApp(Context context, String packageName) {
        AppInfo appInfo = null;
        PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        for (ApplicationInfo app : listAppcations) {
            if (packageName.equals(app.packageName)) {
                appInfo = getAppInfo(app, pm);
                break;
            }
        }
        return appInfo;
    }

    private AppInfo getAppInfo(ApplicationInfo app, PackageManager pm) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName(pm.getApplicationLabel(app).toString());//应用名称
        appInfo.setAppIcon(app.loadIcon(pm));//应用icon
        appInfo.setPackName(app.packageName);//应用包名，用来卸载
        return appInfo;
    }

    /**
     * 启动apk
     *
     * @param context
     * @param packageName
     */
    public void startApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
        final Intent intent = pm.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);

//		if (packageName.equals("com.android.browser")){ // 浏览器时
//			TvCommonManager.getInstance().setInputSource(34);
//		}
    }
}
