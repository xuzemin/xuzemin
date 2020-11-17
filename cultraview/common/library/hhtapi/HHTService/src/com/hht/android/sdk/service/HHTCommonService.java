package com.hht.android.sdk.service;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.device.IHHTCommon;
import com.hht.android.sdk.led.HHTLedManager;
import com.hht.android.sdk.lock.HHTLockManager;
import com.hht.android.sdk.ops.HHTOpsManager;
import com.hht.android.sdk.service.utils.EyeProtectionUtils;
import com.hht.android.sdk.service.utils.L;
import com.hht.android.sdk.service.utils.TimeTools;
import com.hht.android.sdk.service.utils.Tools;
import com.hht.android.sdk.service.utils.TvosCommand;
import com.mstar.android.tv.TvAudioManager;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import android.app.ActivityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HHTCommonService extends IHHTCommon.Stub {
    private static final String TAG = HHTCommonService.class.getSimpleName();
    private Context mContext;

    private List<HHTCommonManager.EnumHdmiOutMode> mHDMITxList;// HDMITx list

    private ExecutorService mThreadPool = Executors.newFixedThreadPool(10);

    private Handler mHandler;
    public HHTCommonService(Context context){
        this.mContext = context;
        L.i("gyx","HHTCommonService init");

        mHandler = new Handler();

        init();
    }

    public HHTCommonService(Context context, Handler handler){
        this.mContext = context;
        L.i("gyx","HHTCommonService init");

        mHandler = handler;
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        mHDMITxList = new ArrayList<>();
        mHDMITxList.add(HHTCommonManager.EnumHdmiOutMode.AUTO);
        mHDMITxList.add(HHTCommonManager.EnumHdmiOutMode.HDMITX_720p_60);
        mHDMITxList.add(HHTCommonManager.EnumHdmiOutMode.HDMITX_2k_60);
        mHDMITxList.add(HHTCommonManager.EnumHdmiOutMode.HDMITX_4k_60);
    }

    @Override
    public boolean standby() throws RemoteException {
        int mode = getStandbyMode();
        switch(mode){
            case 0:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Instrumentation inst = new Instrumentation();
                            inst.sendKeyDownUpSync(638);//KeyCode_power
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case 1:
                boolean isPartialPoweroff = "1".equals(SystemProperties.get("ctv.standby_state","0"));
                L.d("PhoneWindow","standbyByMode"+SystemProperties.get("ctv.standby_state","0"));
                if(!isPartialPoweroff){
                    try{
                         //触摸framework判断
                        SystemProperties.set("ctv.standby_state","1");
                        L.d("PhoneWindow","standbyByMode"+SystemProperties.get("ctv.standby_state","0"));
                        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);  
                        try{
                            am.forceStopPackage("com.hht.uc.FileBrowser");
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                            //背光
                        TvPictureManager.getInstance().disableBacklight();
                            //OPS关机
                        HHTOpsManager.getInstance().setOpsPowerTurnOff();

                        HHTLockManager.getInstance().setTouchLock(true);

                        HHTLockManager.getInstance().setKeypadLock(true,new int[]{KeyEvent.KEYCODE_POWER});
                        
                        HHTLedManager.getInstance().setLedStatus(true);
                    } catch (Exception e){
                        e.printStackTrace();
                        L.d("PhoneWindow","standbyByMode"+e.toString());
                        return false;
                    }
                }
                
                break;
        }

        return true;
    }

    @Override
    public int getStandbyMode() throws RemoteException {
        String mode = SystemProperties.get(HHTConstant.STANDBY_MODE, "0");
        try{
            Integer integer = Integer.valueOf(mode.trim());
            if (integer !=null && integer >= 0 && integer < 3){
                return integer;
            }
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    @Override
    public boolean setStandbyMode(int standby_mode) throws RemoteException {
        if (standby_mode < 0 || standby_mode > 2){
            return false;
        }
        SystemProperties.set(HHTConstant.STANDBY_MODE, standby_mode + "");
        return true;

    }

    @Override
    public int getCurHdmiTxMode() throws RemoteException {
        int mode = SystemProperties.getInt(HHTConstant.HDMI_TX_MODE, 0);
        int len = HHTCommonManager.EnumHdmiOutMode.values().length;
        if (mode >= 0 && mode < len){
            return mode;
        }

        return HHTCommonManager.EnumHdmiOutMode.AUTO.ordinal();
//        return Tools.getHDMITX();
    }

    @Override
    public int getSleepModeTime() throws RemoteException {
        return TvTimerManager.getInstance().getSleepTimeMode();
    }

    @Override
    public int getEyeProtectionMode() throws RemoteException {
        int mode = SystemProperties.getInt(HHTConstant.EYE_PROTECTION_MODE,
                HHTCommonManager.EnumEyeProtectionMode.EYE_OFF.ordinal());

        int len = HHTCommonManager.EnumEyeProtectionMode.values().length;
        if (mode < 0 || mode >= len){
            return HHTCommonManager.EnumEyeProtectionMode.EYE_OFF.ordinal();
        }
        return mode;
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

//    @Override
//    public boolean getScreenSaverEnable() throws RemoteException {
//        // 屏保开关
//        int value = Settings.Secure.getInt(mContext.getContentResolver(),Settings.Secure.SCREENSAVER_ENABLED, 1);
//        return (value == 1);
//    }

//    @Override
//    public int getScreenSaverTime() throws RemoteException {
//        // 获得屏保时间
//        return Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 15 * 1000);
//    }

    @Override
    public boolean getShowTempEnable() throws RemoteException {
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
        return TvAudioManager.getInstance().isSupportHDMITx_HDByPassMode();
    }

    @Override
    public boolean isSystemSleep() throws RemoteException {
        // TODO
        return false;
    }

    @Override
    public boolean isSystemVitrualStandby() throws RemoteException {
        return Tools.isPartialPoweroff();
    }

    @Override
    public boolean setEyeProtectionMode(int mode) throws RemoteException {
        return EyeProtectionUtils.getInstance().
                setEyeProtectionMode(mContext, mode,mThreadPool);
    }

    @Override
    public boolean setHdmiTxMode(int iMode) throws RemoteException {
        if (iMode < 0 || iMode > 4){
            return false;
        }

        SystemProperties.set(HHTConstant.HDMI_TX_MODE, "" + iMode);

        String cmd = "SetNovaTekHtxMode#" + iMode;
        try {
            TvManager.getInstance().setTvosCommonCommand(cmd);
        } catch (TvCommonException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean setNoAndroidStatus(boolean bType) throws RemoteException {
        // TODO
        return false;
    }



    @Override
    public boolean getNoEventEnable() throws RemoteException {
        return false;
    }

    /**
     * 获得无操作休眠时间
     * @return
     * @throws RemoteException
     */
    @Override
    public int getNoEventTime() throws RemoteException {
        int time = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,Integer.MAX_VALUE);
        if (time == Integer.MAX_VALUE){// sleep is closed
            return Integer.MAX_VALUE;
        }

        return time / 1000;
    }

    @Override
    public boolean setNoEventEnable(boolean bStatus) throws RemoteException {
        return false;
    }

    /**
     * 设置无操作待机时间
     * @param iTime 单位second
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean setNoEventTime(int iTime) throws RemoteException {
        if(iTime <= 0){
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,Integer.MAX_VALUE);
        }else{
            int time = 0;
            if(iTime * 1000 > Integer.MAX_VALUE){
                time = Integer.MAX_VALUE;
            }else{
                time = iTime * 1000;
            }
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,time);
        } 

        return true;
    }

    @Override
    public boolean setNoSignalEnable(boolean bStatus) throws RemoteException {
        return TvFactoryManager.getInstance().setNoSignalAutoShutdown(bStatus);
    }

    @Override
    public boolean setNoSignalTime(int iTime) throws RemoteException {
        // iTime is second
        boolean result = Settings.Secure.putInt(mContext.getContentResolver(), "NO_SIGNAL_SHUTDOWN_TIME", iTime);
        return result;
    }

//    @Override
//    public boolean setScreenSaverEnable(boolean iVal) throws RemoteException {
//        // TODO
////        DreamService
//        int value = iVal ? 1:0;
////        String SCREENSAVER_ENABLED = "screensaver_enabled";
//        Settings.Secure.putInt(mContext.getContentResolver(),Settings.Secure.SCREENSAVER_ENABLED, value);
//        return true;
//    }

//    @Override
//    public boolean setScreenSaverTime(int iVal) throws RemoteException {
//        // 设置屏保时间
//        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, iVal);
//        return false;
//    }

    @Override
    public boolean setShowTempEnable(boolean bStatus) throws RemoteException {
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
        return TvTimerManager.getInstance().getSleepTimeMode() != TvTimerManager.SLEEP_TIME_OFF;
    }

    @Override
    public boolean setSleepModeEnable(boolean bStatus) throws RemoteException {
        boolean isOff = TvTimerManager.getInstance().getSleepTimeMode() == TvTimerManager.SLEEP_TIME_OFF;
        if (bStatus){ // open sleep mode
            if (isOff){
                // set default time:10min
                TvTimerManager.getInstance().setSleepTimeMode(TvTimerManager.SLEEP_TIME_10MIN);
            }
        } else { // close sleep mode
            if (!isOff){
                TvTimerManager.getInstance().setSleepTimeMode(TvTimerManager.SLEEP_TIME_OFF);
            }
        }
        return false;
    }

    @Override
    public boolean setSleepModeTime(int iTime) throws RemoteException {
        // sleep time mode:0-8 SLEEP_TIME_OFF-.SLEEP_TIME_240MIN
        TimeTools.setSleepTimeMode(mContext.getApplicationContext(), iTime);
        return true;

//        Settings.System.putInt(mContext.getContentResolver(), Settings.Secure.SLEEP_TIMEOUT, iTime);
//        return true;
    }

    @Override
    public boolean setSystemVitrualStandby(boolean bMode) throws RemoteException {
        return false;
    }
//
//    @Override
//    public boolean startScreenSaver() throws RemoteException {
//        // 启动屏保
//        DreamBackend mBackend = new DreamBackend(mContext);
//        mBackend.startDreaming();
//
////        // 反射调用方法
////        String className = "com.android.settingslib.dream.DreamBackend";
////        Object[] initObj = new Object[]{mContext};
////        Object mBackend = ReflectUtil.newInstanceNoException(className, initObj);
////
////        String method = "startDreaming";
////        ReflectUtil.invokeFromNewInstance(className, initObj, method);
//
//        return true;
//    }

    @Override
    public boolean startSystemSleep(boolean bMode) throws RemoteException {
        return false;
    }

    @Override
    public List getSupportHDMITxList() throws RemoteException {
        return mHDMITxList;
    }

    /**
     * 获取双通道USB设置
     * 0:share
     * 1:only for android
     * 2:only for ops
     * @return
     */
    @Override
    public int getUsbChannelMode() throws RemoteException {
        return SystemProperties.getInt(HHTConstant.SHARE_USB_CHANGE, 0);
    }

    /**
     * 设置USB双通道模式
     * 0:share
     * 1:only for android
     * 2:only for ops
     * @param mode
     */
    @Override
    public void setUsbChannelMode(int mode) throws RemoteException {
        if (mode >= 0 && mode <= 2){
            Tools.sendCommand(TvosCommand.TVOS_COMMON_CMD_SHARE_USB_MODE + "#" + mode);
        }
    }

    @Override
    public void setAutoWakeupBySourceEnable(boolean enable) throws RemoteException {
        String cmdStr  = enable ? TvosCommand.TVOS_COMMON_CMD_ENABLE_AUTO_WAKEUP :
                TvosCommand.TVOS_COMMON_CMD_DISABLE_AUTO_WAKEUP;
        Tools.sendCommand(cmdStr);
    }

    @Override
    public boolean isAutoWakeupBySourceEnable() throws RemoteException {
        String str = SystemProperties.get(HHTConstant.SOURCE_AUTO_START, "on");
        return TextUtils.equals(str, "on");
    }

    /**
     * 设置黑板自动节能开关
     * 当大屏被遮挡时，根据待机模式设置中的待机模式，定时自动待机；
     * 如当前待机模式为‘正常待机’，则定时执行待机；
     * 当前待机模式为‘部分待机’，则定时执行部分待机功能；
     * 当前模式STR，则定时执行STR
     * @param enable
     * @throws RemoteException
     */
    @Override
    public void setBlackBoardEnable(boolean enable) throws RemoteException {
        // set blackboard switch
        int switchValue = enable ? 1 :0;
        SystemProperties.set(HHTConstant.BLACK_BOARD_SHUTDOWN_ENABLE, "" + switchValue);
    }

    /**
     * 获取黑板自动节能开关
     *
     * @return
     */
    @Override
    public boolean isBlackBoardEnabled() throws RemoteException {
        int mode = SystemProperties.getInt(HHTConstant.BLACK_BOARD_SHUTDOWN_ENABLE, 0);
        return mode == 1;
    }

    /**
     * 设置黑板自动节能时间
     *
     * @param value ，以分钟为单位
     */
    @Override
    public void setBlackBoardTime(int value) throws RemoteException {
        SystemProperties.set(HHTConstant.BLACK_BOARD_SHUTDOWN_TIME, "" + value);
    }

    /**
     * 获取黑板自动节能时间
     *
     * @return 自动节能生效时间，以分钟为单位
     */
    @Override
    public int getBlackBoardTime() throws RemoteException {
        return SystemProperties.getInt(HHTConstant.BLACK_BOARD_SHUTDOWN_TIME, 1);
    }

    /**
     * 设置屏温阈值
     * @param tmp 屏温阈值 .默认阈值90摄氏度
     */
    @Override
    public void setScreenTmpThreshold(int tmp) throws RemoteException {
        SystemProperties.set(HHTConstant.SCREEN_TEMPERATURE_THRESHOLD, "" + tmp);
    }

    /**
     * 获取屏温阈值
     */
    @Override
    public int getScreenTmpThreshold() throws RemoteException {
        return SystemProperties.getInt(HHTConstant.SCREEN_TEMPERATURE_THRESHOLD, HHTConstant.TEMPERATURE_THRESHOLD);
    }

    /**
     * TODO ...
     * 检测ANDROID触控是否可用
     *
     * @return  true 可用,false 不可用
     */
    @Override
    public boolean isTouchFrameNormal() throws RemoteException {
        return Tools.checkTouch();
    }

    /**
     * 光感检测
     *
     * @return
     */
    @Override
    public boolean isLightSensorNormal() throws RemoteException {
        return Tools.isLightSensorNormal();
    }

    /**
     *return   true：4k, false：2K
     *
     */
    @Override
    public boolean is4K2KMode() throws RemoteException {
        return Tools.is4K2KMode();
    }
}
