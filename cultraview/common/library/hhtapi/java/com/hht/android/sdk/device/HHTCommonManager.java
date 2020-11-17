package com.hht.android.sdk.device;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import com.hht.android.sdk.ops.HHTOpsManager;
import com.hht.android.sdk.lock.HHTLockManager;
import com.hht.android.sdk.led.HHTLedManager;
import com.hht.android.sdk.boardInfo.HHTConstant;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tv.TvPictureManager;
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

    public static final String TAG = "HHTCommonManager";

    private static HHTCommonManager INSTANCE;
    private static IHHTCommon mService = null;
    public HHTTvEventListener mListener;
    private static List<HHTTvEventListener> mListeners = new ArrayList<>();

    public static final int HHT_INPUTSOURCE_CHANGE = 0x1000;
    public static final int HHT_INPUTSOURCE_PLUG_STATUS = 0x1001;
    public static final int HHT_INPUTSOURCE_LOCK = 0x1002;
    public static final int HHT_INPUTSOURCE_UNLOCK = 0x1003;
    public static final int HHT_INPUTSOURCE_UNSTABLE = 0x1004;

    //TODO... 以下事件的定义，需要根据占用通道信号源底层实现方式待定。
    //hht power status 1 power on,0 power off
    public static final int HHT_OPS_POWER_STATUS = 0x1005;
    public static final int HHT_TYPEC_STATUS = 0x1006;

    public static final int HHT_SCREEN_TEMP = 0x2000;// 屏幕温度监控
    public static final int HHT_SCREEN_BACKLIGHT = 0x2001;
    public static final int HHT_SCREEN_LIGHT_SENSOR = 0x2002;

    public static final int HHT_BLACK_STANDBY = 0x3000; // 黑板关闭自动待机
    public static final int HHT_SCREEN_OFF_TIMEOUT = 0x3001;// 无操作事件，并将无操作事件提前30S上抛.

    // option codes of TvManager.TV_UNITY_EVENT
    private static final int SWITCH_INPUT_SOURCE = 0x111;

    private static final int UPDATE_INPUT_SOURCE_STATUS = 0x112;

    private static final int AUTO_SWITCH_INPUT_SOURCE = 0x113;

    private static final int CURRENT_INPUT_SOURCE_PLUG_OUT = 0x114;

    private static final int CURRENT_DET_PORT_IN = 0x118; // HDMI1 端口侦测 in
    private static final int CURRENT_DET_PORT_OUT = 0x119; // HDMI1 端口侦测 out

    private static final int EV_TVOS_UTIITY_EVENT_MAINBOARD_TMP = 0x11C; // temp
    public static final int EV_TVOS_UTIITY_EVENT_LIGHT_SENSOR_VAL = 0x11D;// light sensor
    private static final int EV_TVOS_UTIITY_EVENT_BACK_LIGHT_VAL = 0x11E;
    private static final int EV_TVOS_UTIITY_EVENT_BLACK_BOARD_STANDBY = 0x11F;// black board STANDBY
    private static final int EV_TVOS_UTIITY_EVENT_SCREEN_OFF_TIMEOUT = 0x120;// no event

    private static final int EV_TVOS_UTIITY_EVENT_FAKE_STANDBY_WOL = 0x122;// no event

    private static final int TVPICTURE_EVENT_BACKLIGHT_CHANGE = 2001;// PictureManager.TVPICTURE_EVENT_BACKLIGHT_CHANGE;

    private static final int HHT_OPS_ON_OFF_STATUS = 0x11A;// ops status :1 is  on,0 is off

    private static final String TVOS_COMMON_CMD_GET_HDMI_PORT = "GetTIPORT";

    /**
     * eye protect
     */
    public enum EnumEyeProtectionMode {
        EYE_OFF,
        EYE_DIMMING, // 光控护眼
        EYE_RGB, // 调整画面模式为护眼（PICTURE_MODE_SOFT）模式
        EYE_PLUS, // 护眼+模式
        EYE_WRITE_PROTECT // 书写护眼
    }

    /**
     * HDMI Out Mode
     */
    public enum EnumHdmiOutMode {
        AUTO,
        HDMITX_720p_60,
        HDMITX_2k_60,
        HDMITX_4k_60;
    }

    /**
     * temperature mode
     */
    public enum EnumTempMode {
        TEMP_C,
        TEMP_F;
    }

    private HHTCommonManager() {
        IBinder service = ServiceManager.getService(HHTCommonManager.SERVICE);
        mService = IHHTCommon.Stub.asInterface(service);
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

    @Deprecated
    public boolean setNoEventEnable(boolean bStatus) {
        try {
            return mService.setNoEventEnable(bStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Deprecated
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
     * <p>
     * 在固定时间内，会进入睡眠状态
     *
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
     *              <p>
     *              HHTCommonManager.EnumTempMode.TEMP_C
     *              HHTCommonManager.EnumTempMode.TEMP_F
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
     * <p>
     * The supported mode are:
     * <p>
     * HHTCommonManager.EnumTempMode.TEMP_C
     * HHTCommonManager.EnumTempMode.TEMP_F
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

    public boolean setSystemVitrualStandby(boolean bMode) {
        try {
            return mService.setSystemVitrualStandby(bMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSystemVitrualStandby() {
        try {
            return mService.isSystemVitrualStandby();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public boolean setScreenSaverEnable(boolean iVal) {
//        try {
//            return mService.setScreenSaverEnable(iVal);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    public boolean getScreenSaverEnable() {
//        try {
//            return mService.getScreenSaverEnable();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    public int getScreenSaverTime() {
//        try {
//            return mService.getScreenSaverTime();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return 1;
//    }

//    public boolean setScreenSaverTime(int iVal) {
//        try {
//            return mService.setScreenSaverTime(iVal);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    public boolean startScreenSaver() {
//        try {
//            return mService.startScreenSaver();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public boolean isSupportHDMITx() {
        try {
            return mService.isSupportHDMITx();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Integer> getSupportHDMITxList() {
        try {
            return mService.getSupportHDMITxList();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new ArrayList<Integer>();
    }

    public boolean setHdmiTxMode(int iMode) {
        try {
            return mService.setHdmiTxMode(iMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getCurHdmiTxMode() {
        try {
            return mService.getCurHdmiTxMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获取双通道USB设置
     * 0:share
     * 1:only for android
     * 2:only for ops
     *
     * @return
     */
    public int getUsbChannelMode() {
        try {
            return mService.getUsbChannelMode();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 设置USB双通道模式
     * 0:share
     * 1:only for android
     * 2:only for ops
     *
     * @param mode
     */
    public void setUsbChannelMode(int mode) {
        try {
            mService.setUsbChannelMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 信号源(HDMI/VAG)自动唤醒开关
     *
     * @param enable true:开,false:关
     */
    public void setAutoWakeupBySourceEnable(boolean enable) {
        try {
            mService.setAutoWakeupBySourceEnable(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 信号源自动唤醒开关
     *
     * @return
     */
    public boolean isAutoWakeupBySourceEnable() {
        try {
            return mService.isAutoWakeupBySourceEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置待机模式
     *
     * @param standby_mode
     */
    public boolean setStandbyMode(int standby_mode) {
        try {
            return mService.setStandbyMode(standby_mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取待机模式
     *
     * @return
     */
    public int getStandbyMode() {
        try {
            return mService.getStandbyMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据待机模式执行待机操作
     *
     * @param mode
     */
    public boolean standby() {
        try {
            return mService.standby();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

//
//    /**
//     * 设置光控护眼
//     *
//     * @param enable
//     */
//    public void setLightControl(boolean enable) {
//        try {
//            mService.setLightControl(enable);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 是否为光控护眼
//     *
//     * @return true是/false否
//     */
//    public boolean isLigthControl() {
//        try {
//            return mService.isLigthControl();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 设置黑板自动节能开关
     * 当大屏被遮挡时，根据待机模式设置中的待机模式，定时自动待机；
     * 如当前待机模式为‘正常待机’，则定时执行待机；
     * 当前待机模式为‘部分待机’，则定时执行部分待机功能；
     * 当前模式STR，则定时执行STR
     * @param enable
     */
    public void setBlackBoardEnable(boolean enable) {
        try {
            mService.setBlackBoardEnable(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取黑板自动节能开关
     *
     * @return
     */
    public boolean isBlackBoardEnabled() {
        try {
            return mService.isBlackBoardEnabled();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置黑板自动节能时间
     *
     * @param value ，以分为单位
     */
    public void setBlackBoardTime(int value) {
        try {
            mService.setBlackBoardTime(value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取黑板自动节能时间
     *
     * @return 自动待机时间，以分为单位
     */
    public int getBlackBoardTime() {
        try {
            return mService.getBlackBoardTime();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 设置屏温阈值
     * @param tmp 屏温阈值 .默认阈值90摄氏度
     */
    public void setScreenTmpThreshold(int tmp){
        try {
            mService.setScreenTmpThreshold(tmp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取屏温阈值
     */
    public int getScreenTmpThreshold(){
        try {
            return mService.getScreenTmpThreshold();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 90;
        }
    }

    /**
     * 添加监听
     *
     * @param listener
     */
    public void registerHHTTvEventListener(HHTTvEventListener listener) {
        if (mListeners.size() == 0) { // 注册监听事件
            TvChannelManager.getInstance().registerOnSignalEventListener(mOnSignalEventListener);
            TvCommonManager.getInstance().registerOnInputSourceEventListener(mOnInputSourceEventListener);
            TvCommonManager.getInstance().registerOnUnityEventListener(mOnUnityEventListener);
        }

        if (listener != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * 移除监听
     *
     * @param listener
     */
    public void unregisterHHTTvEventListener(HHTTvEventListener listener) {
        if (listener != null && !mListeners.contains(listener)) {
            mListeners.remove(listener);
        }

        if (mListeners.size() == 0) { // 移除注册监听事件
            TvChannelManager.getInstance().unregisterOnSignalEventListener(mOnSignalEventListener);
            TvCommonManager.getInstance().unregisterOnInputSourceEventListener(mOnInputSourceEventListener);
            TvCommonManager.getInstance().unregisterOnUnityEventListener(mOnUnityEventListener);
        }
    }

    /**
     * 检测ANDROID触控是否可用
     *
     * @return  true 可用,false 不可用
     */
    public boolean isTouchFrameNormal() {
        try {
            return mService.isTouchFrameNormal();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 光感检测
     *
     * @return
     */
    public boolean isLightSensorNormal() {
        try {
            return mService.isLightSensorNormal();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *return   true：4k，flase：2K
     *
     */
    public boolean is4K2KMode(){
        try {
            return mService.is4K2KMode();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断TIPort处于活动状态的port口
     *
     * return MH board: 0为HDMI, 2为HDMI2, 3为OPS
     */
    private int getTIPORT(){
        int[] shorts = TvCommonManager.getInstance().setTvosCommonCommand(TVOS_COMMON_CMD_GET_HDMI_PORT);
        if (shorts != null && shorts.length >= 1){
            return shorts[0];
        }

        return -1;
    }

    /**
     * sourceKey转换为inputSource
     *
     * @param inputSource
     * @return
     */
    private String convertToSourceKey(final int inputSource){
        String sourceNameKey = "";
        String boardInfo = SystemProperties.get(HHTConstant.PRODUCT_BOARD, "");//CV8386H_AH
        boolean isMHBoard = TextUtils.equals(boardInfo, "CN8386_MH");
        if (isMHBoard){ // MH Board
            switch (inputSource){
                case TvCommonManager.INPUT_SOURCE_VGA:{// VGA
                    sourceNameKey = HHTConstant.VGA;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_ATV:{// ATV
                    sourceNameKey = HHTConstant.ATV;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_DTV:{ // DTV
                    sourceNameKey = HHTConstant.DTV;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI3:{// 23
                    sourceNameKey = HHTConstant.HDMI3; // HDMI1
                    int port = getTIPORT();
                    if(port == 2){ // HDMI2
                        sourceNameKey = HHTConstant.HDMI2;
                    } else if(port == 3){ // OPS
                        sourceNameKey = HHTConstant.OPS;
                    }
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI2:{// DP , TYPEC
                    sourceNameKey = HHTConstant.DP;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI:{ // FRONT_HDMI
                    sourceNameKey = HHTConstant.HDMI1;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_YPBPR:{ // YPBPR
                    sourceNameKey = HHTConstant.YPBPR;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_CVBS:{// AV
                    sourceNameKey = HHTConstant.AV;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_STORAGE:{// Android
                    sourceNameKey = HHTConstant.ANDROID;
                    break;
                }
                default:
                    break;
            }
        } else { // AH Board
            switch (inputSource){
                case TvCommonManager.INPUT_SOURCE_VGA:{// VGA
                    sourceNameKey = HHTConstant.VGA;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI:{ // HDMI1
                    sourceNameKey = HHTConstant.HDMI1;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI2:{// HDMI2
                    sourceNameKey = HHTConstant.HDMI2;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI3:{ // HDMI3
                    sourceNameKey = HHTConstant.HDMI3;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI4:{ // OPS
                    sourceNameKey = HHTConstant.OPS;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_STORAGE:{// Android
                    sourceNameKey = HHTConstant.ANDROID;
                    break;
                }
                default:
                    break;
            }
        }

        return sourceNameKey;
    }

    /**
     * signal event
     *
     * @param what
     * @param arg1
     * @param arg2
     * @param obj
     */
    private void handleSignalEvent(int what, int arg1, int arg2, Object obj){
        boolean flag = true;
        int whatTmp = what;
        int arg1Tmp = arg1;
        int arg2Tmp = arg2;

        switch (what){
            case TvChannelManager.TVPLAYER_SIGNAL_UNLOCK:{ // unlock
                whatTmp = HHT_INPUTSOURCE_UNLOCK;
                break;
            }
            case TvChannelManager.TVPLAYER_SIGNAL_LOCK:{  // lock
                whatTmp = HHT_INPUTSOURCE_LOCK;
                break;
            }
            case TvCommonManager.TV_INPUT_SOURCE_CHANGED:{ // change
                /**
                 * TvManager.setInputSource()
                 *             m.what = TV_INPUT_SOURCE_CHANGED;
                 *             m.arg1 = MAIN_WINDOW;
                 *             m.arg2 = eInputSrc.ordinal();
                 */
                whatTmp = HHT_INPUTSOURCE_CHANGE;
                arg1Tmp = arg2;// inputSource
                break;
            }
            case TvChannelManager.TVPLAYER_SIGNAL_UNSTABLE:{  // HHT_INPUTSOURCE_UNSTABLE
                whatTmp = HHT_INPUTSOURCE_UNSTABLE;
                break;
            }
            default:{
                flag = false;
                break;
            }
        }

        if (flag && mListeners.size() > 0){
            if (HHTConstant.DEBUG){
                String msg = String.format("handleSignalEvent what:%s, arg1:%s, arg2:%s", whatTmp, arg1Tmp, arg2Tmp);
                 Log.d(TAG, msg);
            }
            synchronized (HHTCommonManager.class) {
                for (HHTTvEventListener l : mListeners) {
                    l.onTvEvent(whatTmp, arg1Tmp, arg2Tmp, obj);
                }
            }
        }
    }

    /**
     * inpout source change event
     */
    TvCommonManager.OnInputSourceEventListener mOnInputSourceEventListener = new TvCommonManager.OnInputSourceEventListener() {
        @Override
        public boolean onInputSourceEvent(int what, int arg1, int arg2, Object obj) {
            boolean flag = true;
            int whatTmp = what;
            int arg1Tmp = arg1;
            int arg2Tmp = arg2;

            if (what == TvCommonManager.TV_INPUT_SOURCE_CHANGED){
                /**
                 * TvManager.setInputSource()
                 *             m.what = TV_INPUT_SOURCE_CHANGED;
                 *             m.arg1 = MAIN_WINDOW;
                 *             m.arg2 = eInputSrc.ordinal();
                 */
                whatTmp = HHT_INPUTSOURCE_CHANGE;
                arg1Tmp = arg2;// inputSource
            } else {
                flag = false;
            }

            if (flag && mListeners.size() > 0){
                if (HHTConstant.DEBUG) {
                    String msg = String.format("onInputSourceEvent what:%s, arg1:%s, arg2:%s", whatTmp, arg1Tmp, arg2Tmp);
                    Log.d(TAG, msg);
                }
                synchronized (HHTCommonManager.class) {
                    for (HHTTvEventListener l : mListeners) {
                        l.onTvEvent(whatTmp, arg1Tmp, arg2Tmp, obj);
                    }
                }
            }
            return false;
        }
    };

    /**
     * signal event
     */
    TvChannelManager.OnSignalEventListener mOnSignalEventListener = new TvChannelManager.OnSignalEventListener() {
        @Override
        public boolean onAtvSignalEvent(int what, int arg1, int arg2, Object obj) {
            handleSignalEvent(what, arg1, arg2, obj);
            return false;
        }

        @Override
        public boolean onDtvSignalEvent(int what, int arg1, int arg2, Object obj) {
            handleSignalEvent(what, arg1, arg2, obj);
            return false;
        }

        @Override
        public boolean onTvSignalEvent(int what, int arg1, int arg2, Object obj) {
            handleSignalEvent(what, arg1, arg2, obj);
            return false;
        }
    };

    /**
     * Unity Event
     */
    TvCommonManager.OnUnityEventListener mOnUnityEventListener = new TvCommonManager.OnUnityEventListener() {
        @Override
        public boolean onUnityEvent(int what, int arg1, int arg2, Object obj) {
            boolean flag = true;
            int whatTmp = what;
            int arg1Tmp = arg1;
            int arg2Tmp = arg2;
            Object objTmp = obj;
            // TV_UNITY_EVENT
            if (what == TvManager.TV_UNITY_EVENT) {
                arg1Tmp = arg2;// para
                if (arg1 == HHT_OPS_ON_OFF_STATUS) { // OPS status:on or off
                    if (HHTConstant.DEBUG) Log.d(TAG, "HHT_OPS_ON_OFF_STATUS status --->" + arg2);
                    whatTmp = HHT_OPS_POWER_STATUS;// event type
                    arg1Tmp = TvCommonManager.INPUT_SOURCE_HDMI;// OPS: inputSource  23
                    arg2Tmp = arg2; // status: 1 on, 0 off
                    objTmp = HHTConstant.OPS;
                }
                else if (arg1 == CURRENT_DET_PORT_IN
                        || arg1 == CURRENT_DET_PORT_OUT) { // HDMI1/HDMI2/OPS, plug statuss in/out : det port
                    if (HHTConstant.DEBUG) Log.d(TAG, "CURRENT_DET_PORT_IN CURRENT_DET_PORT_OUT");
                    whatTmp = HHT_INPUTSOURCE_PLUG_STATUS;
                    arg1Tmp = TvCommonManager.INPUT_SOURCE_HDMI;// inputSource 23
                    arg2Tmp = (arg1 == CURRENT_DET_PORT_IN) ?
                            HHTConstant.PLUG_STATUS_IN : HHTConstant.PLUG_STATUS_OUT;// 1 is in, 0 is out

                    String sourceNameKey;
                    switch (arg2) {
                        case HHTConstant.DET_PORT_HDMI2:
                            sourceNameKey = HHTConstant.HDMI2;
                            break;
                        case HHTConstant.DET_PORT_OPS:
                            sourceNameKey = HHTConstant.OPS;
                            break;
                        default:
                            sourceNameKey = HHTConstant.HDMI3;
                            break;
                    }
                    objTmp = sourceNameKey;
                }
                else if ((arg1 == AUTO_SWITCH_INPUT_SOURCE)
                    || (arg1 == CURRENT_INPUT_SOURCE_PLUG_OUT)) { // other inputSource plug in/out
                    if (HHTConstant.DEBUG) Log.d(TAG, "AUTO_SWITCH_INPUT_SOURCE CURRENT_INPUT_SOURCE_PLUG_OUT");
                    whatTmp = HHT_INPUTSOURCE_PLUG_STATUS;
                    arg1Tmp = arg2;// inputSource
                    arg2Tmp = (AUTO_SWITCH_INPUT_SOURCE == arg1)
                            ? HHTConstant.PLUG_STATUS_IN : HHTConstant.PLUG_STATUS_OUT; // plugin status,1 is in, 0 is out
                    objTmp = convertToSourceKey(arg2);
                }
//                else if (CURRENT_INPUT_SOURCE_PLUG_OUT == arg1) { // other inputSource plug out HHT_INPUTSOURCE_PLUGOUT
//                    whatTmp = HHT_INPUTSOURCE_PLUG_STATUS;// HHT_INPUTSOURCE_PLUGOUT;
//                    arg1Tmp = arg2;// inputSource
//                    arg2Tmp = 0; // plugout
//                }
                else if (arg1 == EV_TVOS_UTIITY_EVENT_MAINBOARD_TMP) { // HHT_SCREEN_TEMP
                    if (HHTConstant.DEBUG) Log.d(TAG, "HHT_SCREEN_TEMP ");
                    whatTmp = HHT_SCREEN_TEMP;
                    boolean showTempEnable = HHTCommonManager.getInstance().getShowTempEnable();
                    if (!showTempEnable) {// is show temp
                        return false;
                    }
                    arg1Tmp = HHTDeviceManager.getInstance().getTempSensorValue();
                }
                else if (arg1 == EV_TVOS_UTIITY_EVENT_BLACK_BOARD_STANDBY) { // HHT_BLACK_STANDBY
                    if (HHTConstant.DEBUG) Log.d(TAG, "HHT_BLACK_STANDBY status");
                    whatTmp = HHT_BLACK_STANDBY;// event type
                }
                else if (arg1 == EV_TVOS_UTIITY_EVENT_SCREEN_OFF_TIMEOUT) { // HHT_SCREEN_OFF_TIMEOUT
                    if (HHTConstant.DEBUG) Log.d(TAG, "HHT_SCREEN_OFF_TIMEOUT status");
                    whatTmp = HHT_SCREEN_OFF_TIMEOUT;// event type
                }
                else if (arg1 == EV_TVOS_UTIITY_EVENT_FAKE_STANDBY_WOL) { // HHT_SCREEN_OFF_TIMEOUT
                    if (HHTConstant.DEBUG) Log.d(TAG, "EV_TVOS_UTIITY_EVENT_FAKE_STANDBY_WOL status");
                    try{
                        HHTLedManager.getInstance().setLedStatus(false);
                        SystemProperties.set("ctv.standby_state","0");
                        TvPictureManager.getInstance().enableBacklight();
                        HHTOpsManager.getInstance().setOpsPowerTurnOn();
                        HHTLockManager.getInstance().setTouchLock(false);
                        HHTLockManager.getInstance().setKeypadLock(false,new int[]{0});
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    flag = false;
                }
                else {
                    flag = false;
                }
            } else {
                flag = false;
            }

            // 发送事件
            if (flag && mListeners.size() > 0) {
                if (HHTConstant.DEBUG) {
                    String msg = String.format("onUnityEvent what:%s, arg1:%s, arg2:%s",
                            whatTmp, arg1Tmp, arg2Tmp);
                    if (objTmp != null){
                        msg = String.format("onUnityEvent what:%s, arg1:%s, arg2:%s, obj:%s",
                                whatTmp, arg1Tmp, arg2Tmp, objTmp);
                    }
                    Log.d(TAG, msg);
                }
                synchronized (HHTCommonManager.class) {
                    for (HHTTvEventListener l : mListeners) {
                        l.onTvEvent(whatTmp, arg1Tmp, arg2Tmp, objTmp);
                    }
                }
            }
            return false;
        }
    };
}
