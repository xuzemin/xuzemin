package com.hht.android.sdk.service;

import android.content.Context;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.android.settingslib.dream.DreamBackend;
import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.device.IHHTCommon;
import com.mstar.android.tv.TvAudioManager;
import com.mstar.android.tv.TvFactoryManager;

import java.util.ArrayList;
import java.util.List;

public class HHTCommomService extends IHHTCommon.Stub {
    private Context mContext;
    public HHTCommomService(Context context){
        this.mContext=context;
        Log.i("gyx","HHTCommomService init");
    }
    @Override
    public int getCurHdmiTxMode() throws RemoteException {
        // TODO
        return HHTCommonManager.EnumHdmiOutMode.AUTO.ordinal();
    }

    @Override
    public int getSleepModeTime() throws RemoteException {
        int time = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE);
        return time;
    }

    @Override
    public int getEyeProtectionMode() throws RemoteException {
        // TODO

        return 0;
    }

    @Override
    public boolean getNoEventEnable() throws RemoteException {
        // TODO
        return false;
    }

    @Override
    public int getNoEventTime() throws RemoteException {
        // TODO
        return 0;
    }

    @Override
    public boolean getNoSignalEnable() throws RemoteException {
        boolean noSignalAutoShutdownEnable = TvFactoryManager.getInstance().isNoSignalAutoShutdownEnable();
        return noSignalAutoShutdownEnable;
    }

    @Override
    public int getNoSignalTime() throws RemoteException {
        int time = Settings.System.getInt(mContext.getContentResolver(), "NO_SIGNAL_SHUTDOWN_TIME", 15);
        return time;
    }

    @Override
    public boolean getScreenSaverEnable() throws RemoteException {
        // 屏保开关
        int value = Settings.Secure.getInt(mContext.getContentResolver(),Settings.Secure.SCREENSAVER_ENABLED, 1);
        return (value == 1);
    }

    @Override
    public int getScreenSaverTime() throws RemoteException {
        // 获得屏保时间
        return Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 15 * 1000);
    }

    @Override
    public boolean getShowTempEnable() throws RemoteException {
        // TODO
        int mode = Settings.System.getInt(mContext.getContentResolver(),"ShowTempEnable", 1);

        return mode == 1;
    }

    @Override
    public int getShowTempMode() throws RemoteException {
        int mode = Settings.System.getInt(mContext.getContentResolver(),"ShowTempMode", HHTCommonManager.EnumTempMode.TEMP_C.ordinal());
        return mode;
    }

    @Override
    public boolean isNoAndroidStatus() throws RemoteException {
        // TODO
        return false;
    }

    @Override
    public boolean isSupportHDMITx() throws RemoteException {
        boolean result = TvAudioManager.getInstance().isSupportHDMITx_HDByPassMode();
        return result;
    }

    @Override
    public boolean isSystemSleep() throws RemoteException {
        // TODO
        return false;
    }

    @Override
    public boolean isSystemVitrualStandby() throws RemoteException {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO
        return false;
    }

    @Override
    public boolean setEyeProtectionMode(int mode) throws RemoteException {
        // TODO
        return false;
    }

    @Override
    public boolean setHdmiTxMode(int iMode) throws RemoteException {
        // TODO
        return false;
    }

    @Override
    public boolean setNoAndroidStatus(boolean bType) throws RemoteException {
        // TODO
        return false;
    }

    @Override
    public boolean setNoEventEnable(boolean bStatus) throws RemoteException {
        // TODO
        return false;
    }

    @Override
    public boolean setNoEventTime(int iTime) throws RemoteException {
        // TODO
        return false;
    }

    @Override
    public boolean setNoSignalEnable(boolean bStatus) throws RemoteException {
        boolean result = TvFactoryManager.getInstance().setNoSignalAutoShutdown(bStatus);
        return result;
    }

    @Override
    public boolean setNoSignalTime(int iTime) throws RemoteException {
        boolean result = Settings.Secure.putInt(mContext.getContentResolver(), "NO_SIGNAL_SHUTDOWN_TIME", iTime);
        return result;
    }

    @Override
    public boolean setScreenSaverEnable(boolean iVal) throws RemoteException {
        // TODO
//        DreamService
        int value = iVal ? 1:0;
//        String SCREENSAVER_ENABLED = "screensaver_enabled";
        Settings.Secure.putInt(mContext.getContentResolver(),Settings.Secure.SCREENSAVER_ENABLED, value);
        return true;
    }

    @Override
    public boolean setScreenSaverTime(int iVal) throws RemoteException {
        // 设置屏保时间
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, iVal);
        return false;
    }

    @Override
    public boolean setShowTempEnable(boolean bStatus) throws RemoteException {
        // TODO
        int mode = bStatus ? 1 : 0;
        Settings.System.putInt(mContext.getContentResolver(),"ShowTempEnable", mode);
        return true;
    }

    @Override
    public boolean setShowTempMode(int iMode) throws RemoteException {
        int mode = (iMode != HHTCommonManager.EnumTempMode.TEMP_F.ordinal()) ? HHTCommonManager.EnumTempMode.TEMP_C.ordinal():HHTCommonManager.EnumTempMode.TEMP_F.ordinal();
        Settings.System.putInt(mContext.getContentResolver(),"ShowTempMode", mode);
        return true;
    }

    @Override
    public boolean getSleepModeEnable() throws RemoteException {
        int mode = Settings.System.getInt(mContext.getContentResolver(),"SleepModeEnable", 0);
        return mode == 1;
    }

    @Override
    public boolean setSleepModeEnable(boolean bStatus) throws RemoteException {
        int mode = bStatus ? 1:0;
        Settings.System.putInt(mContext.getContentResolver(),"SleepModeEnable", mode);
        return false;
    }

    @Override
    public boolean setSleepModeTime(int iTime) throws RemoteException {
        Settings.System.putInt(mContext.getContentResolver(), Settings.Secure.SLEEP_TIMEOUT, iTime);
        return true;
    }

    @Override
    public boolean setSystemVitrualStandby(boolean bMode) throws RemoteException {
        return false;
    }

    @Override
    public boolean startScreenSaver() throws RemoteException {
        // 启动屏保
        DreamBackend mBackend = new DreamBackend(mContext);
        mBackend.startDreaming();

//        // 反射调用方法
//        String className = "com.android.settingslib.dream.DreamBackend";
//        Object[] initObj = new Object[]{mContext};
//        Object mBackend = ReflectUtil.newInstanceNoException(className, initObj);
//
//        String method = "startDreaming";
//        ReflectUtil.invokeFromNewInstance(className, initObj, method);

        return true;
    }

    @Override
    public boolean startSystemSleep(boolean bMode) throws RemoteException {
        return false;
    }

    @Override
    public List getSupportHDMITxList() throws RemoteException {
        List list = new ArrayList<Integer>();
        list.add(HHTCommonManager.EnumHdmiOutMode.AUTO);
        list.add(HHTCommonManager.EnumHdmiOutMode.HDMITX_720p_60);
        list.add(HHTCommonManager.EnumHdmiOutMode.HDMITX_2k_60);
        list.add(HHTCommonManager.EnumHdmiOutMode.HDMITX_4k_60);
        return list;
    }
}
