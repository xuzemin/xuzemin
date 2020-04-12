package com.cv.apk.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.cv.apk.manager.utils.ApkInfo;
import com.cv.apk.manager.utils.ApkSearchTools;
import com.cv.apk.manager.utils.Constant;
import com.cv.apk.manager.utils.SeachUsbThread;
import com.cv.apk.manager.utils.Tools;
import com.cv.apk.manager.utils.ViewUtils;
import android.content.SharedPreferences;
import android.os.SystemProperties;
/**
 * @Company: com.cultraview
 * @date: 2016
 * @author Write Macro.Song(songhong@cultraview.com)
 * @since 2.0.0
 */
 public class AppManager extends Activity implements OnClickListener {

    private static final String TAG = "ApkManager";

    private static final int SYS_NO_APK = 0x01;

    private int loginSize = 3;

    private FrameLayout[] fls = new FrameLayout[loginSize];

    private ImageView[] imageViews = new ImageView[loginSize];

    private boolean isExit = false;

    /** True: read system apk is false: not finished reading it */
    public static boolean sys_read_over = false;

    /** Set the sd card 1. False: no 2. True: there are;Set the initial is true */
    public static boolean sd = false;

    /** Read sd card state of true: the false reading: read the complete */
    public static boolean sdread_flag = false;

    /** Read the state of the sys true: the false reading: read the complete */
    public static boolean sys_read_flag = true;

    private ApkSearchTools apkSearchTools_sys;

    public static List<ApkInfo> myApksUsb;

    public static List<ApkInfo> myApksSys;

    // public static List<ApkInfo> myApksClean;

    /** Store all installed software information */
    public List<PackageInfo> allPackageInfos;

    /** Installation of the software to store user information */
    public static List<PackageInfo> userPackageInfos;

    /** Information storage system software installed */
    public static List<PackageInfo> sysPackageInfos;

    public static List<PackageInfo> cleanPackageInfos;

    public static Context cvContext;

    private UninstallOrInstallReceiver uninstallOrInstallReceiver;

    FrameLayout rootView; //顶部的view

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SYS_NO_APK:
                    // Toast.makeText(ApkManager.this,
                    // R.string.MainActivity_sys_no_apks,
                    // Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    final BroadcastReceiver broadcastRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.gc();
            loadUsb();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cvContext = AppManager.this;
        myApksUsb = new ArrayList<ApkInfo>();
        // The apk asynchronous thread to read a usb card
        loadUsb();
        // Asynchronous thread reading system memory
        new Thread(new SeachSystemThread()).start();
        // The apk asynchronous thread reading system memory
        new Thread(new SeachSysApksThread()).start();
        setContentView(R.layout.activity_main);
        initView();
        fls[0].requestFocus();
        initFilter();
    }

    private void initView() {
        fls[0] = (FrameLayout) findViewById(R.id.login_fl_1);
        fls[1] = (FrameLayout) findViewById(R.id.login_fl_2);
        fls[2] = (FrameLayout) findViewById(R.id.login_fl_3);

        rootView = (FrameLayout)findViewById(R.id.root_view);

        imageViews[0] = ((ImageView) findViewById(R.id.login_iv_uninstall));
        imageViews[1] = ((ImageView) findViewById(R.id.login_iv_install));
        imageViews[2] = ((ImageView) findViewById(R.id.login_iv_appatv));
        imageViews[0].setOnClickListener(this);
        imageViews[1].setOnClickListener(this);
        imageViews[2].setOnClickListener(this);



    }

    private void initFilter() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addDataScheme("file");
        try {
            // Register to monitor function
            registerReceiver(broadcastRec, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        uninstallOrInstallReceiver = new UninstallOrInstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.UNINSTALL_STRING);
        filter.addAction(Constant.INSTALL_STRING);
        filter.addDataScheme("package");
        // Registered unloading radio
        registerReceiver(uninstallOrInstallReceiver, filter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "-onKeyDown" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (fls[0].isFocused()) {
                Intent intentUninstall = new Intent();
                intentUninstall.setClass(AppManager.this, UninstallActivity.class);
                AppManager.this.startActivity(intentUninstall);
                return true;
            } else if (fls[1].isFocused()) {
                Intent intentInstall = new Intent();
                intentInstall.setClass(AppManager.this, InstallActivity.class);
                AppManager.this.startActivity(intentInstall);
                return true;
            } else if (fls[2].isFocused()) {
                Intent intentAppatv = new Intent();
                intentAppatv.setClass(AppManager.this, CleanOrStopAppActivity.class);
                AppManager.this.startActivity(intentAppatv);
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // exitBy2Click();

            finish();
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }

        Log.d(TAG, "-onClick" + view.getId());
        switch (view.getId()) {
            case R.id.login_iv_uninstall:
                Intent intentUninstall = new Intent();
                intentUninstall.setClass(AppManager.this, UninstallActivity.class);
                AppManager.this.startActivity(intentUninstall);
                break;
            case R.id.login_iv_install:
                Intent intentInstall = new Intent();
                intentInstall.setClass(AppManager.this, InstallActivity.class);
                AppManager.this.startActivity(intentInstall);
                break;
            case R.id.login_iv_appatv:
                Intent intentAppatv = new Intent();
                intentAppatv.setClass(AppManager.this, CleanOrStopAppActivity.class);
                AppManager.this.startActivity(intentAppatv);
                break;
            default:
                break;
        }
    }

    public static void loadUsb() {
        if (Tools.detectSDCardAvailability() && sdread_flag == false) {
            sd = true;
            myApksUsb.clear();
            new Thread(new SeachUsbThread()).start();
        } else {
            sd = false;
        }
    }

    private class SeachSystemThread extends Thread {
        @Override
        public void run() {
            sys_read_over = false;
            // To obtain the system install all software information
            allPackageInfos = getPackageManager().getInstalledPackages(
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            PackageManager pm = cvContext.getPackageManager();
            // Users to install software packet
            userPackageInfos = new ArrayList<PackageInfo>();
            // System installation software packet
            sysPackageInfos = new ArrayList<PackageInfo>();
            cleanPackageInfos = new ArrayList<PackageInfo>();
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
            boolean isok;
            // Cycle took out all the software information
            for (int i = 0; i < allPackageInfos.size(); i++) {
                // Each software information
                PackageInfo temp = allPackageInfos.get(i);

                // �������ҹ�����ʱ����ӵ�ϵͳAPP�б���
                if ((temp.packageName + "").equals("com.protruly.floatwindowlib")) { // ���ҹ�����
                    sysPackageInfos.add(temp);
                    continue;
                }
                cleanPackageInfos.add(temp);

                ApplicationInfo appInfo = temp.applicationInfo;
                Log.d("app_manger", "packName:" + temp.packageName);
                if (temp.packageName.equals("com.tencent.mm")) {
                    if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                            || (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        Log.d("app_manger", "FLAG_UPDATED_SYSTEM_APP");
                    } else {
                        Log.d("app_manger", "other");
                    }
                }
                if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                        || (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    isok = false;
                    for (ResolveInfo resolveInfo : mApps) {
                        if ((resolveInfo.loadLabel(pm) + "").equals(pm
                                .getApplicationLabel(temp.applicationInfo) + "")) {
                            if (!Tools.isShuldFiled(temp.packageName + "")) {
                                // The system software
                                isok = true;
                            }
                        }
                    }
                    if (isok) {
                        // System has been installed
                        sysPackageInfos.add(temp);
                    }
                } else {
                    // The user has installed the software
                    userPackageInfos.add(temp);
                }
            }

            sys_read_over = true;
            Log.i(TAG, "--The number of system installation:" + sysPackageInfos.size());
            Log.i(TAG, "--Number of users to install:" + userPackageInfos.size());
            if (UninstallActivity.sysall_reading_to_over) {
                UninstallActivity.sysall_reading_to_over = false;
                UninstallActivity.updata_sys_all = true;
            }
        }
    };

    public String getSDPath() {
        String sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            // Access to the root directory of the sd
            sdDir = Environment.getExternalStorageDirectory().toString();
            Log.d(TAG, "--getSDPath-------->>System sd path" + sdDir);
        }
        return sdDir;
    }

    private class SeachSysApksThread extends Thread {
        @SuppressLint("SdCardPath")
        @Override
        public void run() {
            sys_read_flag = true;
            myApksSys = new ArrayList<ApkInfo>();
            try {
                apkSearchTools_sys = new ApkSearchTools(cvContext);

                File file = new File(getSDPath());
                apkSearchTools_sys.findAllApkFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            myApksSys = apkSearchTools_sys.getApkFiles();
            if (myApksSys.size() == 0) {
                Message msg = new Message();
                // No system application
                msg.what = SYS_NO_APK;
                mHandler.sendMessage(msg);
            } else {
                Log.d(TAG, "--SeachSysApksThread----sys The number of the Apk:" + myApksSys.size());
            }
            sys_read_flag = false;
            if (InstallActivity.sys_reading_to_over) {
                InstallActivity.sys_reading_to_over = false;
                InstallActivity.updata_sys = true;
            }
        }
    };

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, R.string.MainActivity_exitBy2Click, Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);

        } else {
            // finish();
            // System.exit(0);
        }
    }

    class UninstallOrInstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // dataString==package:packageName
            String dataString = intent.getDataString();
            Log.i(TAG, "========dataString==" + dataString);
            PackageInfo currentPackageInfo = null;
            if (intent.getAction().equals(Constant.UNINSTALL_STRING)) {
                for (PackageInfo packageInfo : userPackageInfos) {
                    Log.i(TAG, "========packageInfo.packageName==" + packageInfo.packageName);
                    if (dataString.endsWith(packageInfo.packageName)) {
                        currentPackageInfo = packageInfo;
                        break;
                    }
                }
                userPackageInfos.remove(currentPackageInfo);
                UninstallActivity.unInstallReturn = true;

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (UninstallActivity.mHandler != null) {
                            UninstallActivity.mHandler
                                    .sendEmptyMessage(UninstallActivity.UPATE_PAGE);
                        }
                    }
                }, 500);
            } else if (intent.getAction().equals(Constant.INSTALL_STRING)) {
                PackageManager pm = cvContext.getPackageManager();
                List<PackageInfo> all_package_infos = pm
                        .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                                | PackageManager.GET_ACTIVITIES);
                for (PackageInfo packageInfo : all_package_infos) {
                    if (dataString.endsWith(packageInfo.packageName)) {
                        currentPackageInfo = packageInfo;
                        break;
                    }
                }
                userPackageInfos.add(currentPackageInfo);

                UninstallActivity.unInstallReturn = true;

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (UninstallActivity.mHandler != null) {
                            UninstallActivity.mHandler
                                    .sendEmptyMessage(UninstallActivity.UPATE_PAGE);
                        }
                    }
                }, 500);
            }
        }
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(broadcastRec);
        this.unregisterReceiver(uninstallOrInstallReceiver);
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        int bgIndex = sharedPreferences.getInt("bgIndex", -1);
        int resTheme[] = {R.drawable.bg_theme_default, R.drawable.bg_theme_qipao, R.drawable.bg_theme_jingshu, R.drawable.bg_theme_yemo,R.drawable.bg_theme_jianjie
        ,R.drawable.bg_theme_xingkong};
        int gwTheme[] = {R.drawable.bg_theme_default, R.drawable.bg_theme_qipao, R.drawable.bg_theme_jingshu, R.drawable.bg_theme_yemo,R.drawable.bg_theme_jianjie
                ,R.drawable.bg_theme_xingkong,R.drawable.bg_theme_gw};
        boolean gw = SystemProperties.get("client.config").equals("GW");
        if (bgIndex ==-1){
            if (gw)
                setRootBackground(rootView,6);
            else
                setRootBackground(rootView,0);
        }else {
            setRootBackground(rootView,bgIndex);
        }
    }


    private void setRootBackground(View rootLayout,int data) {
        switch (data) {
            case 2:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_jingshu);
                break;
            case 0:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_default);
                break;
            case 1:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_qipao);
                break;
            case 3:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_yemo);
                break;
            case 4:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_jianjie);
                break;
            case 5:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_xingkong);
				break;
            case 6:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_gw);
                break;
        }
    }
//<style name="MyBg">
//        <item name="android:windowBackground">@drawable/bg_theme_default</item>
//
//    </style>
//    <style name="MyBg1">
//        <item name="android:windowBackground">@drawable/bg_theme_qipao</item>
//
//    </style>
//
//    <style name="MyBg2">
//        <item name="android:windowBackground">@drawable/bg_theme_jingshu</item>
//
//    </style>
//    <style name="MyBg3">
//        <item name="android:windowBackground">@drawable/bg_theme_yemo</item>
//    </style>
//    <style name="MyBg4">
//        <item name="android:windowBackground">@drawable/bg_theme_jianjie</item>
//    </style>
//    <style name="MyBg5">
//        <item name="android:windowBackground">@drawable/bg_theme_xingkong</item>
//    </style>
//    <style name="MyBg6">
//        <item name="android:windowBackground">@drawable/bg_theme_gw</item>
//    </style>
//

}
