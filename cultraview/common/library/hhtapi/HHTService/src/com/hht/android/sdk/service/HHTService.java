package com.hht.android.sdk.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ServiceManager;

import com.hht.android.sdk.audio.HHTAudioManager;
import com.hht.android.sdk.boardInfo.HHTBoardInfoManager;
import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.device.HHTDeviceManager;
import com.hht.android.sdk.led.HHTLedManager;
import com.hht.android.sdk.lock.HHTLockManager;
import com.hht.android.sdk.network.HHTNetworkManager;
import com.hht.android.sdk.ops.HHTOpsManager;
import com.hht.android.sdk.picture.HHTPictureManager;
import com.hht.android.sdk.service.utils.L;
import com.hht.android.sdk.source.HHTSourceManager;
import com.hht.android.sdk.system.HHTSystemManager;
import com.hht.android.sdk.time.HHTTimeManager;
import com.hht.android.sdk.touch.HHTTouchManager;

import java.lang.ref.WeakReference;

/**
 * 运行服务类
 */
public class HHTService extends Service {
    private final static String TAG = HHTService.class.getSimpleName();
    private UIHandler mHandler;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new UIHandler(this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.i(TAG, "onStartCommand start");
        // add interface Services
        ServiceManager.addService(HHTAudioManager.SERVICE, new HHTAudioService(this));
        ServiceManager.addService(HHTBoardInfoManager.SERVICE, new HHTBoardInfoService(this));
        ServiceManager.addService(HHTCommonManager.SERVICE, new HHTCommonService(this, mHandler));
        ServiceManager.addService(HHTDeviceManager.SERVICE, new HHTDeviceService(this));
        ServiceManager.addService(HHTLedManager.SERVICE, new HHTLedService(this));
        ServiceManager.addService(HHTLockManager.SERVICE, new HHTLockService(this));
        ServiceManager.addService(HHTNetworkManager.SERVICE, new HHTNetworkService(this));
        ServiceManager.addService(HHTOpsManager.SERVICE, new HHTOpsService(this));
        ServiceManager.addService(HHTPictureManager.SERVICE, new HHTPictureService(this));
        ServiceManager.addService(HHTSourceManager.SERVICE, new HHTSourceService(this));
        ServiceManager.addService(HHTSystemManager.SERVICE, new HHTSystemService(this));
        ServiceManager.addService(HHTTimeManager.SERVICE, new HHTTimeService(this));
        ServiceManager.addService(HHTTouchManager.SERVICE, new HHTTouchService(this));
        L.i(TAG, "onStartCommand end");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<Context> weakReference;

        public UIHandler(Context context) {
            super();
            this.weakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
