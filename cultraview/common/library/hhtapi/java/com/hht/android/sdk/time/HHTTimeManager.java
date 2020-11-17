package com.hht.android.sdk.time;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.hht.android.sdk.time.util.TimeUtil;

public class HHTTimeManager {
    public static final String SERVICE = "sdk_TimeManager";

    private static HHTTimeManager mInstance;
    private static IHHTTime mService=null;

    private HHTTimeManager() {
        IBinder service = ServiceManager.getService(HHTTimeManager.SERVICE);
        mService = IHHTTime.Stub.asInterface(service);
    }

    public static HHTTimeManager getInstance() {
        if (mInstance == null) {
            synchronized (HHTTimeManager.class) {
                if (mInstance == null) {
                    mInstance = new HHTTimeManager();
                }
            }
        }
        return mInstance;
    }
//
//    /**
//     * @param lMillitime  系统时间
//     * @param strTimezone 时区
//     * @return
//     */
//    public boolean setRtcTime(long lMillitime, String strTimezone) {
//        try {
//            return mService.setRtcTime(lMillitime,strTimezone);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 保存芯片总运行时间
     * Parameters:
     * lChipTime - long, 单位s
     * Returns:
     * true:Success, or false: failed
     */
    public boolean setChipRuntime(long lChipTime) {
        try {
            return mService.setChipRuntime(lChipTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取芯片总运行时间
     * Returns:
     * long, 单位s
     */
    public long getChipRuntime() {
        try {
            return mService.getChipRuntime();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 保存系统运行时间
     * Parameters:
     * lChipTime - long, 单位s
     * Returns:
     * true:Success, or false: failed
     */
    public boolean setSystemRuntime(long lChipTime) {
        try {
            return mService.setSystemRuntime(lChipTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取系统运行时间
     * Returns:
     * long, 单位s
     */
    public long getSystemRuntime() {
        try {
            return mService.getSystemRuntime();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 设置定时开机使能
     * Parameters:
     * enable - true:enable, or false: disable
     * Returns:
     * true:Success, or false: failed
     */
    public boolean setScheduleTimeBootEnable(boolean enable) {
        try {
            return mService.setScheduleTimeBootEnable(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取定时开机使能状态
     * Returns:
     * true:enable, or false: disable
     */
    public boolean isScheduleTimeBootEnable() {
        try {
            return mService.isScheduleTimeBootEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置定时关机使能
     * Parameters:
     * enable - true:enable, or false: disable
     * Returns:
     * true:Success, or false: failed
     */
    public boolean setScheduleTimeShutdownEnable(boolean enable) {
        try {
            return mService.setScheduleTimeShutdownEnable(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取定时关机使能状态
     * Returns:
     * true:enable, or false: disable
     */
    public boolean isScheduleTimeShutdownEnable() {
        try {
            return mService.isScheduleTimeShutdownEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 定时开关机开机时间
     * 开机时间，就是向RTC驱动写数据
     * Parameters:
     * bootTime - 设定时间
     * Returns:
     * true:enable, or false: disable
     */
    public boolean setScheduleTimeForBoot(TimeUtil bootTime) {
        try {
            return mService.setScheduleTimeForBoot(bootTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 定时开关机开机时间
     * 开机时间，就是向RTC驱动写数据
     * Parameters:
     * bootTime - 设定时间
     * Returns:
     * true:enable, or false: disable
     */
    public TimeUtil getScheduleTimeForBoot() {
        try {
            return mService.getScheduleTimeForBoot();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean setScheduleTimeForShutdown(TimeUtil time){
        try {
            return mService.setScheduleTimeForShutdown(time);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public TimeUtil getScheduleTimeForShutdown() {
        try {
            return mService.getScheduleTimeForShutdown();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
}
