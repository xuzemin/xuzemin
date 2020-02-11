package com.hht.android.sdk.device;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * HHTCommonManager 是自定义功能公共的接口管理类。
 * 匹配不同方案的接口，
 * 此类主要设置公共常用的接口，
 * 比如： 睡眠模式，
 * 声音模式，
 * 信号检测，
 * 护眼，
 * 无信号时操作，
 * 无输入事件时操作，
 * 睡眠模式，
 * 温度显示模式，
 * 自定义屏保，
 * HDMI OUT 输出模式，等自定义公共接口。
 */
public class HHTCommonManager {
    public static final String SERVICE = "sdk_CommonManager";

    private static HHTCommonManager INSTANCE;
    private static IHHTCommon mService=null;
    public enum EnumEyeProtectionMode {
        EYE_DIMMING,
        EYE_OFF,
        EYE_RGB;
    }

    public enum EnumHdmiOutMode {
        AUTO,
        HDMITX_720p_60,
        HDMITX_2k_60,
        HDMITX_4k_60,
    }

    public enum EnumTempMode {
        TEMP_C,
        TEMP_F,
    }
    private  HHTCommonManager(){
        IBinder service = ServiceManager.getService(HHTCommonManager.SERVICE);
        mService=IHHTCommon.Stub.asInterface(service);
    }

    public static HHTCommonManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HHTCommonManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HHTCommonManager();
                }
            }
        }
        return INSTANCE;
    }

    public boolean setEyeProtectionMode(int mode) {
        try {
            return mService.setEyeProtectionMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getEyeProtectionMode() {
        try {
            return mService.getEyeProtectionMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean setNoSignalEnable(boolean bStatus) {
        try {
            return mService.setNoSignalEnable(bStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getNoSignalEnable() {
        try {
            return mService.getNoSignalEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setNoSignalTime(int iTime) {
        try {
            return mService.setNoSignalTime(iTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getNoSignalTime() {
        try {
            return mService.getNoSignalTime();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean setNoEventEnable(boolean bStatus) {
        try {
            return mService.setNoEventEnable(bStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getNoEventEnable() {
        try {
            return mService.getNoEventEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setNoEventTime(int iTime) {
        try {
            return mService.setNoEventTime(iTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getNoEventTime() {
        try {
            return mService.getNoEventTime();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean setSleepModeEnable(boolean bStatus) {
        try {
            return mService.setSleepModeEnable(bStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getSleepModeEnable() {
        try {
            return mService.getSleepModeEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setSleepModeTime(int iTime) {
        try {
            return mService.setSleepModeTime(iTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取睡眠模式时间
     *
     *     在固定时间内，会进入睡眠状态
     * @return
     */
    public int getSleepModeTime() {
        try {
            return mService.getSleepModeTime();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean startSystemSleep(boolean bMode) {
        try {
            return mService.startSystemSleep(bMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSystemSleep() {
        try {
            return mService.isSystemSleep();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setShowTempEnable(boolean bStatus) {
        try {
            return mService.setShowTempEnable(bStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getShowTempEnable() {
        try {
            return mService.getShowTempEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Set temperature display mode
     *
     * @param iMode The supported mode are:
     *
     *     HHTCommonManager.EnumTempMode.TEMP_C
     *     HHTCommonManager.EnumTempMode.TEMP_F
     * @return
     */
    public boolean setShowTempMode(int iMode) {
        try {
            return mService.setShowTempMode(iMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取温度显示模式
     *
     * The supported mode are:
     *
     *     HHTCommonManager.EnumTempMode.TEMP_C
     *     HHTCommonManager.EnumTempMode.TEMP_F
     *
     * @return
     */
    public int getShowTempMode() {
        try {
            return mService.getShowTempMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean setNoAndroidStatus(boolean bType) {
        try {
            return mService.setNoAndroidStatus(bType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isNoAndroidStatus() {
        try {
            return mService.isNoAndroidStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setSystemVitrualStandby(boolean bMode){
        try {
            return mService.setSystemVitrualStandby(bMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean isSystemVitrualStandby(){
        try {
            return mService.isSystemVitrualStandby();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setScreenSaverEnable(boolean iVal){
        try {
            return mService.setScreenSaverEnable(iVal);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getScreenSaverEnable(){
        try {
            return mService.getScreenSaverEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getScreenSaverTime(){
        try {
            return mService.getScreenSaverTime();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public boolean setScreenSaverTime(int iVal){
        try {
            return mService.setScreenSaverTime(iVal);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean startScreenSaver(){
        try {
            return mService.startScreenSaver();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSupportHDMITx(){
        try {
            return mService.isSupportHDMITx();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Integer> getSupportHDMITxList(){
        try {
            return mService.getSupportHDMITxList();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new ArrayList<Integer>();
    }
    public boolean setHdmiTxMode(int iMode){
        try {
            return mService.setHdmiTxMode(iMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getCurHdmiTxMode(){
        try {
            return mService.getCurHdmiTxMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
