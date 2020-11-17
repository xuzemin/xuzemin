package com.hht.android.sdk.service;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;

import com.hht.android.sdk.service.utils.L;
import com.hht.android.sdk.service.utils.TimeTools;
import com.hht.android.sdk.time.IHHTTime;
import com.hht.android.sdk.time.util.TimeUtil;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

public class HHTTimeService extends IHHTTime.Stub {
    private Context mContext;
    private Handler mHandler;

    public HHTTimeService(Context context) {
        this.mContext = context;
        mHandler = new Handler();
        L.i("wang", "HHTTimeService init");
    }

    public HHTTimeService(Context context, Handler handler) {
        this.mContext = context;
        mHandler = handler;
        L.i("wang", "HHTTimeService init");
    }
    /**
     * 获取芯片总运行时间
     * Returns:
     * long, 单位s
     */
    @Override
    public long getChipRuntime() throws RemoteException {
        long mChipRuntime=0;
        try {
            String chipRuntime = TvManager.getInstance().getEnvironment("ChipRuntime");
            if (!TextUtils.isEmpty(chipRuntime)){
                mChipRuntime=Long.parseLong(chipRuntime);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return mChipRuntime;
    }

    /**
     * 获取系统运行时间
     * Returns:
     * long, 单位s
     */
    @Override
    public long getSystemRuntime() throws RemoteException {
        long chipRuntime = Settings.System.getLong(mContext.getContentResolver(), "SystemRuntime", 0);
        return chipRuntime;
    }

    /**
     * 保存芯片总运行时间
     * Parameters:
     * lChipTime - long, 单位s
     * Returns:
     * true:Success, or false: failed
     */
    @Override
    public boolean setChipRuntime(long lChipTime) throws RemoteException {
        boolean result = false;
        try {
            result = TvManager.getInstance().setEnvironment("ChipRuntime", String.valueOf(lChipTime));
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取定时开机使能状态
     * Returns:
     * true:enable, or false: disable
     */
    @Override
    public boolean isScheduleTimeBootEnable() throws RemoteException {
        return TvTimerManager.getInstance().isOnTimerEnable();
    }

    /**
     * 获取定时关机使能状态
     * Returns:
     * true:enable, or false: disable
     */
    @Override
    public boolean isScheduleTimeShutdownEnable() throws RemoteException {
        return TvTimerManager.getInstance().isOffTimerEnable();
    }

    /**
     * 设置定时开机使能
     * Parameters:
     * enable - true:enable, or false: disable
     * Returns:
     * true:Success, or false: failed
     */
    @Override
    public boolean setScheduleTimeBootEnable(final boolean enable) throws RemoteException {
        return TimeTools.setScheduleTimeBootEnable(enable);
    }

    @Override
    public boolean setScheduleTimeForBoot(TimeUtil bootTime) throws RemoteException {
        return TimeTools.setTvOnTimer(bootTime, mHandler);
    }

    @Override
    public TimeUtil getScheduleTimeForBoot() throws RemoteException {
        return TimeTools.getTvOnTimer();
    }

    /**
     * 设置定时关机使能
     * Parameters:
     * enable - true:enable, or false: disable
     * Returns:
     * true:Success, or false: failed
     */
    @Override
    public boolean setScheduleTimeShutdownEnable(boolean enable) throws RemoteException {
        return TimeTools.setScheduleTimeShutdownEnable(enable);
    }

    /**
     * 保存系统运行时间
     * Parameters:
     * lChipTime - long, 单位s
     * Returns:
     * true:Success, or false: failed
     */
    @Override
    public boolean setSystemRuntime(long lChipTime) throws RemoteException {
        Settings.System.putLong(mContext.getContentResolver(), "SystemRuntime", lChipTime);
        return true;
    }

    @Override
    public boolean setScheduleTimeForShutdown(TimeUtil time) throws RemoteException {
        return TimeTools.setTvOffTimer(time, mHandler);
    }

    @Override
    public TimeUtil getScheduleTimeForShutdown() throws RemoteException {
        return TimeTools.getTvOffTimer();
    }
}
