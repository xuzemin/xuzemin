package com.protruly.floatwindowlib.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.ui.DownloadingLayout;
import com.protruly.floatwindowlib.utils.MD5Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

/**
 * 升级APK
 * Created by admin on 2017-03-23 0023.
 */
public class UpdateAPKService extends Service {
    private final static String mDownloadDir = Environment.getExternalStorageDirectory().getPath() + "/";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String appUrl = intent.getStringExtra(FloatWindowService.KEY_UPDATE_APP_URL);
        if (!TextUtils.isEmpty(appUrl)){
            downloadFile(appUrl);//下载APK
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 下载apk文件
     *
     * @param appUrl
     */
    private void downloadFile(String appUrl) {
        final String apkFileName = getResources().getString(R.string.apk_file_name);
        OkHttpUtils.get().url(appUrl).build().execute(new FileCallBack(mDownloadDir, apkFileName) {
            @Override
            public void onError(Call call, Exception e, int id) {
                FloatWindowManager.updateAPPMd5 = "";
                FloatWindowManager.updateAPPUrl = "";

                // 关闭进度条
                if (DownloadingLayout.mHandler != null){
                    Message message = DownloadingLayout.mHandler.obtainMessage(1, -1);
                    DownloadingLayout.mHandler.sendMessage(message);
                }
                stopSelf();
            }

            @Override
            public void onResponse(File file, int id) {
                try {
                    String md5Str = MD5Util.getFileMD5(file);
                    // 比较文件的MD5值
                    if (FloatWindowManager.updateAPPMd5.equals(md5Str)){
                        FloatWindowManager.updateAPPMd5 = "";
                        FloatWindowManager.updateAPPUrl = "";

                        //当文件下载完成后回调
                        installAPK(file.getAbsolutePath());
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                // 更新下载进度
                int tmpProgress = (int) (progress * 100);
                if (tmpProgress % 5 == 0) {
                    // 更新进度
                    if (DownloadingLayout.mHandler != null){
                        Message message = DownloadingLayout.mHandler.obtainMessage(1, tmpProgress);
                        DownloadingLayout.mHandler.sendMessage(message);
                    }
                }
            }
        });
    }

    /**
     * 安装APP
     *
     * @param appFilePath
     */
    private void installAPK(String appFilePath){
        // 下载完成,弹出窗口安装方式
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + appFilePath),
                "application/vnd.android.package-archive");
        this.startActivity(i);
        stopSelf();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
