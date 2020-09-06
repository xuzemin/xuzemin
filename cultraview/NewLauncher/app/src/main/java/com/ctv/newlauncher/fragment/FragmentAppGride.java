package com.ctv.newlauncher.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ctv.ctvlauncher.R;
import com.ctv.newlauncher.adapter.OthersGridviewAadpter;
import com.ctv.newlauncher.bean.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentAppGride implements AdapterView.OnItemClickListener {
    private static OthersGridviewAadpter mAdapter;
    private GridView gridview;
    private View contentView;
    private static List<AppInfo> listapp;
    private Context mContext;
    private PackageManager pm;
    private Thread listAppThread;
    private static long timebak = -1;
    private AnimationSet manimationSet;
    private MyInstalledReceiver installedReceiver;
    private AnimationSet animationSet;
    private View temp_view;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mAdapter.setAppData(listapp);
                    listAppThread = null;
                    break;
            }
        }
    };

    public FragmentAppGride(View view, Context context) {
        this.contentView = view;
        this.mContext = context;
        initview();
    }

    AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (view == null) {
                return;
            }
            mAdapter.setSelectedId(i);
            setScaleAnimation(view, i, false);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    private void initview() {
        gridview = contentView.findViewById(R.id.others_gridview);
        gridview.setOnItemClickListener(this);
        gridview.setOnItemSelectedListener(listener);
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        initData();
    }

    private void initData() {
        if (mAdapter == null) {
            mAdapter = new OthersGridviewAadpter(mContext, listapp);
        }
        gridview.setAdapter(mAdapter);

        update();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listapp == null ) {
            return;
        }

        String name = listapp.get(position).getPackName();
        Log.d("startActivity", "name:" + name);
        pm = mContext.getPackageManager();
        final Intent intent = pm.getLaunchIntentForPackage(listapp.get(position).getPackName());
        if (intent == null) {
            return;
        }
        if (temp_view == null) {
            Log.d("hhh", "onItemClick:temp_view == null ");
            temp_view = parent.getChildAt(0);
        }
        //setScaleAnimation(view, position, false);
        clearAnimation();
        mContext.startActivity(intent);
    }

    public void clearAnimation() {
        Log.d("hhh", "clearAnimation: ");
        mAdapter.setSelectedId(-1);
    }

    class MyInstalledReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {  // install
                update();
            }
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) { // uninstall
                update();
            }
        }
    }

    private void update() {
        Log.d("hhh ", "FragmentGridView-----update ");

        if (timebak != -1 && System.currentTimeMillis() - timebak < 2 * 1000) {
            return;
        }
        timebak = System.currentTimeMillis();

        if (listAppThread == null) {
            listAppThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    listapp = scanInstallApp();
                    mHandler.sendEmptyMessage(0);
                    Log.d("hhh", "update-----run ");
                }
            });
            listAppThread.start();
        }
    }

    public void setScaleAnimation(View view, int position, boolean isonclick) {
        if (view == null) {
            return;
        }
        if (temp_view != null) {
            //temp_view.setAlpha(0.5f);
            Log.d("hhh", "setScaleAnimation: temp_view.setScaleX(1f) ");
            temp_view.setScaleX(1f);
            temp_view.setScaleY(1f);
            //測試
            //temp_view.clearAnimation();
        }
        animationSet = new AnimationSet(true);
        if (manimationSet != null && manimationSet != animationSet) {
            manimationSet.setFillAfter(false);
            manimationSet.cancel();
        }
        if (isonclick) {
            return;
        }
        float x = view.getScaleX();
        if (x == 1.25f) {
            //   view.setAlpha(1.0f);
            Log.d("hhh", "setScaleAnimation:temp_view = view; ");
            temp_view = view;
            mAdapter.setSelectedId(position);
            return;
        }
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.25f, 1.0f, 1.25f, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setDuration(100);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setFillAfter(true);
        view.startAnimation(animationSet);
        manimationSet = animationSet;
        temp_view = view;
        mAdapter.setSelectedId(position);
    }

    public List<AppInfo> scanInstallApp() {
        List<AppInfo> appInfos = new ArrayList<>();
        // 获得PackageManager对象
        pm = mContext.getPackageManager();
        List<ApplicationInfo> listAppcations = pm

                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        for (ApplicationInfo app : listAppcations) {
            Intent intent = pm.getLaunchIntentForPackage(app.packageName);
            if (intent != null) {
                if (app.packageName.equals("com.android.cultraview.launcher.whiteboard")
                        || app.packageName.equals("com.android.cultraview.floatbuttonview")
                        || app.packageName.equals("com.protruly.floatwindowlib")
                        || app.packageName.equals("com.ctv.easytouch")
                        || app.packageName.equals("com.ctv.imageselect")
                        || app.packageName.equals("com.cultraview.annotate")
                        || app.packageName.equals("com.ctv.whiteboard")
                        || app.packageName.equals("com.jrm.localmm")
                        || app.packageName.equals("com.android.toofifi")
//                        ||app.packageName.equals("com.android.camera2")
                        || app.packageName.equals("com.inpor.fastmeetingcloud")
                        || app.packageName.equals("com.bozee.meetingmark")
                        || app.packageName.equals("com.mysher.mtalk")
                        || app.packageName.equals("com.example.newmagnifier")
//		                ||app.packageName.equals("com.bozee.andisplay.configer")
//		                ||app.packageName.equals("com.bozee.andisplay")
                        || app.packageName.equals("com.bozee.remoteserver")
                        || app.packageName.equals("com.bozee.registerclient")
                        || app.packageName.equals("com.bozee.dlna.server")
                        || app.packageName.equals("com.mstar.tv.tvplayer.ui")
                        || app.packageName.equals("com.mstar.netplayer")
                        || app.packageName.equals("mstar.factorymenu.ui")
                        || app.packageName.equals("com.ctv.annotation")
                        || app.packageName.equals("com.dazzlewisdom.screenrec")//录像
                        || app.packageName.equals("com.dazzle.timer")// 计时器
                        || app.packageName.equals("com.android.camera2")// 原生相机


//                        || app.packageName .equals ("com.mphotool.whiteboard")//白板
//                        || app.packageName .equals ("com.android.calculator2")//计算器
//                        || app.packageName .equals ("com.android.documentsui") //文件
//                        || app.packageName .equals ("com.ctv.settings")        //设置
//                        || app.packageName .equals ("com.cultraview.cleaner")  //一键清理
//                        || app.packageName .equals ("com.cv.apk.manager")      //应用管理
//                        || app.packageName .equals ("cn.wps.moffice_eng")      //视频通话
//                        || app.packageName .equals ("com.ctv.welcome")         //欢迎辞
//                        || app.packageName .equals ("cn.wps.moffice_eng")      //wps


                        || app.packageName.equals("com.android.tv.settings")) {
                    continue;
                }
                Log.d("apphong", "-----scanInstallApp---- ");
                appInfos.add(getAppInfo(app, pm));
            }
        }
        return appInfos;
    }

    private AppInfo getAppInfo(ApplicationInfo app, PackageManager pm) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName(pm.getApplicationLabel(app).toString());//应用名称
        appInfo.setAppIcon(app.loadIcon(pm));
        appInfo.setPackName(app.packageName);//应用包名，用来卸载
        return appInfo;
    }

}
