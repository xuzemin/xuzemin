package com.hht.android.sdk.service;

import android.content.Context;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.hht.android.sdk.time.IHHTTime;
import com.hht.android.sdk.time.util.TimeUtil;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

public class HHTTimeService extends IHHTTime.Stub {
    private Context mContext;

    public HHTTimeService(Context context) {
        this.mContext = context;
        Log.i("gyx", "HHTTimeService init");
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
     * 获取定时开机使能状态
     * Returns:
     * true:enable, or false: disable
     */
    @Override
    public boolean isScheduleTimeBootEnable() throws RemoteException {
        boolean onTimerEnable = TvTimerManager.getInstance().isOnTimerEnable();
        return onTimerEnable;
    }

    /**
     * 获取定时关机使能状态
     * Returns:
     * true:enable, or false: disable
     */
    @Override
    public boolean isScheduleTimeShutdownEnable() throws RemoteException {
        boolean offTimerEnable = TvTimerManager.getInstance().isOffTimerEnable();
        return offTimerEnable;
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
//
//    @Override
//    public boolean setRtcTime(long lMillitime, String strTimezone) throws RemoteException {
//        boolean result = false;
//        try {
//            AlarmManager alarm = (AlarmManager) mContext
//                    .getSystemService(Context.ALARM_SERVICE);
//            alarm.setTimeZone(strTimezone);
//            alarm.setTime(lMillitime);
//            result = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            result = false;
//        }
//
//        return result;
//    }

    /**
     * 设置定时开机使能
     * Parameters:
     * enable - true:enable, or false: disable
     * Returns:
     * true:Success, or false: failed
     */
    @Override
    public boolean setScheduleTimeBootEnable(boolean enable) throws RemoteException {
        boolean result = TvTimerManager.getInstance().setOnTimerEnable(enable);
        return result;
    }

    @Override
    public boolean setScheduleTimeForBoot(TimeUtil bootTime) throws RemoteException {
        return false;
    }

    @Override
    public TimeUtil getScheduleTimeForBoot() throws RemoteException {
        return null;
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
        boolean result = TvTimerManager.getInstance().setOffTimerEnable(enable);
        return result;
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
        return false;
    }

    @Override
    public TimeUtil getScheduleTimeForShutdown() throws RemoteException {
        return null;
    }
}
