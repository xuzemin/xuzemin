package com.hht.android.sdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ServiceManager;
import android.util.Log;

import com.hht.android.sdk.audio.HHTAudioManager;
import com.hht.android.sdk.boardInfo.HHTBoardInfoManager;
import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.device.HHTDeviceManager;
import com.hht.android.sdk.led.HHTLedManager;
import com.hht.android.sdk.lock.HHTLockManager;
import com.hht.android.sdk.network.HHTNetworkManager;
import com.hht.android.sdk.ops.HHTOpsManager;
import com.hht.android.sdk.picture.HHTPictureManager;
import com.hht.android.sdk.source.HHTSourceManager;
import com.hht.android.sdk.system.HHTSystemManager;
import com.hht.android.sdk.time.HHTTimeManager;

public class HHTService extends Service {
    private final static String TAG = HHTService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (HHTConstant.DEBUG) Log.i(TAG, "onStartCommand start");
        ServiceManager.addService(HHTAudioManager.SERVICE, new HHTAudioService(this));
        ServiceManager.addService(HHTBoardInfoManager.SERVICE, new HHTBoardInfoService(this));
        ServiceManager.addService(HHTCommonManager.SERVICE, new HHTCommomService(this));
        ServiceManager.addService(HHTDeviceManager.SERVICE, new HHTDeviceService(this));
        ServiceManager.addService(HHTLedManager.SERVICE, new HHTLedService(this));
        ServiceManager.addService(HHTLockManager.SERVICE, new HHTLockService(this));
        ServiceManager.addService(HHTNetworkManager.SERVICE, new HHTNetworkService(this));
        ServiceManager.addService(HHTOpsManager.SERVICE, new HHTOpsService(this));
        ServiceManager.addService(HHTPictureManager.SERVICE, new HHTPictureService(this));
        ServiceManager.addService(HHTSourceManager.SERVICE, new HHTSourceService(this));
        ServiceManager.addService(HHTSystemManager.SERVICE, new HHTSystemService(this));
        ServiceManager.addService(HHTTimeManager.SERVICE, new HHTTimeService(this));
        if (HHTConstant.DEBUG) Log.i(TAG, "onStartCommand end");
        return super.onStartCommand(intent, flags, startId);
    }
}
