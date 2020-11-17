package com.ctv.ctvlauncher.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class LauncherActivityInfoCompatV16 extends LauncherActivityInfoCompat {

    private final ActivityInfo mActivityInfo;
    private final PackageManager mPm;
    private final ResolveInfo mResolveInfo;


    public LauncherActivityInfoCompatV16(Context context, ResolveInfo resolveInfo) {
        this.mResolveInfo = resolveInfo;
        this.mActivityInfo = resolveInfo.activityInfo;
        this.mPm = context.getPackageManager();
    }

    public ComponentName getComponentName() {
        final ComponentName mComponentName = new ComponentName(this.mActivityInfo.packageName, this.mActivityInfo.name);

        return mComponentName;
    }
    public Drawable getIcon(int r4) {
        return this.mResolveInfo.loadIcon(this.mPm);
//        throw new UnsupportedOperationException(
//                "Method not decompiled: com.example.demo_002.compat.LauncherActivityInfoCompatV16.getIcon(int):android.graphics.drawable.Drawable");
    }
    public long getFirstInstallTime() {

        throw new UnsupportedOperationException(
                "Method not decompiled: com.example.demo_002.compat.LauncherActivityInfoCompatV16.getFirstInstallTime():long");
    }
    @Override
    public ApplicationInfo getApplicationInfo() {
        return this.mActivityInfo.applicationInfo;
    }



    public String getName() {
        return this.mActivityInfo.name;
    }

    public Drawable getBadgedIcon(int i) {
        return null;
//        return getIcon(i);
    }

    @Override
    public CharSequence getLabel() {
        return this.mResolveInfo.loadLabel(this.mPm);
    }
}
