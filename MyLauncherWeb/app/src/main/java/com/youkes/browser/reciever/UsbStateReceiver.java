package com.youkes.browser.reciever;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.youkes.browser.activity.MainActivity;
import com.youkes.browser.utils.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UsbStateReceiver extends BroadcastReceiver {   //BroadcastReceiver
    private String TAG = "UsbStateReceiver";
    public static final int USB_STATE_ON = 0x00021;
    public static final int USB_STATE_OFF = 0x00022;
    private static String versionName = "";
    private static int versionCode = 0;

    private static final int GET_RUNNING_TASKS_NUM = 30;
    private static final String PACKAGE_NAME = "com.youkes.browser";

    private static Handler mHandler;
    public static Handler getHandler() {
        return mHandler;
    }

    public static void setHandler(Handler handler) {
        mHandler = handler;
    }

    private static int number = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if ( number++ == 0) {
            return;
        }

        Message msg = new Message();

        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
            //USB设备移除，更新UI
            Log.d( TAG, "USB:ACTION_MEDIA_EJECT");
            msg.what = USB_STATE_OFF;

        } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            //USB设备挂载，更新UI
            Log.d( TAG, "USB:ACTION_MEDIA_MOUNTED");
            msg.what = USB_STATE_ON;

            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo taskinfo = am.getRunningTasks(1).get(0);    // 获取到当前正在栈顶运行的Activity。
            String packname = taskinfo.topActivity.getPackageName();    // 获取到当前任务栈顶程序所对应的包名。
            if( !packname.equals(PACKAGE_NAME) ) {
                /*
                final MyDialog confirmDialog = new MyDialog(context, "使用文件管理器打开U盘?", "确定", "取消");
                confirmDialog.setClicklistener(new MyDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm() {
                        confirmDialog.dismiss();
                        Intent i = new Intent(context, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("Selected", "Removable Disk");
                        context.startActivity(i);
                    }
                    @Override
                    public void doCancel() {
                        confirmDialog.dismiss();
                    }
                });
                confirmDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                confirmDialog.show();
                */
//                Intent i = new Intent(context, MainActivity.class);
//                if( isRunningTask(context)) {
//                    i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//                } else {
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                }
//                i.putExtra("Selected", "Removable Disk");
//                context.startActivity(i);
                getAPKPath(context);
            }
        }

        if( mHandler != null ) {
            mHandler.sendMessage(msg);
        }
    }

    public boolean isRunningTask(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks=  am.getRunningTasks(GET_RUNNING_TASKS_NUM);
        Iterator<ActivityManager.RunningTaskInfo> itInfo = tasks.iterator();
        while( itInfo.hasNext() ){
            ActivityManager.RunningTaskInfo info = itInfo.next();
            String packname = info.baseActivity.getPackageName();
            if( packname.equals(PACKAGE_NAME) ) {
                return true;
            }
        }
        return false;
    }

    public boolean isRunningProcess( Context context ) {
        PackageManager pm = context.getPackageManager();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessList = am.getRunningAppProcesses();
        ApplicationInfo appInfo = null;
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            try {
                appInfo = pm.getApplicationInfo(appProcessInfo.processName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if(appInfo.packageName.equals(PACKAGE_NAME)) {
                return true;
            }
        }
        return false;
    }
    @SuppressLint("NewApi")
    public String getAPKPath(Context context){
        File path = new File(Constant.ApkDir);
        if (Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)) {
            File[] files = path.listFiles();// 读取文件夹下文件
            getApkName(files,context);
        }
        return null;
    }

    protected void getApkName(File[] files,Context context) {
        if(files != null){
            PackageManager pm = context.getPackageManager();
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (file.getName().equals(Constant.ApkName) ){
                        PackageInfo pi = pm.getPackageArchiveInfo(Constant.ApkDir+Constant.ApkName, 0);
                        ApplicationInfo applicationInfo = pi.applicationInfo;
                        try {
                            getVersionName(context);
                            if(applicationInfo.packageName.equals(Constant.Package)
                                    && versionName !=null && versionCode != 0
                                    && !pi.versionName.equals(versionName)
                                    && pi.versionCode > versionCode
                            ){
                                install(Constant.ApkDir+Constant.ApkName,context);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    public static void getVersionName(Context context) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        versionName = packInfo.versionName;
        versionCode = packInfo.versionCode;

    }

    private void install(String filePath,Context context) {
        Log.i(TAG, "开始执行安装: " + filePath);
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.w(TAG, "版本大于 N ，开始使用 fileProvider 进行安装");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    context
                    , Constant.Package
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Log.w(TAG, "正常进行安装");
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
