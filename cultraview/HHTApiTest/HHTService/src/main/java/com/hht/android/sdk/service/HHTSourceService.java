package com.hht.android.sdk.service;

import android.content.Context;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.service.utils.DataUtils;
import com.hht.android.sdk.service.utils.Tools;
import com.hht.android.sdk.source.HHTSourceManager;
import com.hht.android.sdk.source.IHHTSource;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.hht.android.sdk.boardInfo.HHTConstant.BOOT_SOURCE_ENABLE;
import static com.hht.android.sdk.boardInfo.HHTConstant.SOURCE_CURRENT;
import static com.hht.android.sdk.boardInfo.HHTConstant.SOURCE_DETECTION_MODE;
import static com.hht.android.sdk.boardInfo.HHTConstant.SOURCE_RECENT;
import static com.hht.android.sdk.source.HHTSourceManager.SOURCE_LIST;

public class HHTSourceService extends IHHTSource.Stub {
    private final static String TAG = HHTSourceManager.class.getSimpleName();
    private Context mContext;
    // 信号源列表map
    private Map<String, String> srcMap;
    // 信号源插入状态map
    private Map<String, String> srcPlugStateMap;
    public HHTSourceService(Context context){
        this.mContext=context;
        if(srcMap == null){
            srcMap = new ConcurrentHashMap<String, String>();
            initSrcMap();
        }
        if(srcPlugStateMap == null){
            srcPlugStateMap = new ConcurrentHashMap<String, String>();
            initSrcPlugStateMap();
        }
        Log.i("gyx","HHTSourceService init");
    }
    /**
     * 初始化sourceInput Map
     */
    private void initSrcMap(){
        if (HHTConstant.DEBUG) Log.d(TAG, "initSrcMap start");

        for (String item:SOURCE_LIST){
            srcMap.put(item, item);
        }

        DataUtils.getInstance().querySourceInfos(mContext, srcMap);
        if (HHTConstant.DEBUG) Log.d(TAG, "initSrcMap end");
    }
    /**
     * 初始化sourceInput Map
     */
    private void initSrcPlugStateMap(){
        for (String item:SOURCE_LIST){
            srcPlugStateMap.put(item, HHTConstant.SOURCE_DET_OFF);
        }
    }

    /**
     * 获取开机信号源模式使能开关状态
     *
     * @return true- enable； false-disable
     */
    @Override
    public boolean getBootSourceEnable() throws RemoteException {
        return 1 == Settings.System.getInt(mContext.getContentResolver(), BOOT_SOURCE_ENABLE, 0);
    }
    /**
     * 获取开机信号源模式
     *
     * @return
     *     MODE_FIXED_SOURCE
     *     MODE_LAST_SOURCE
     */
    @Override
    public String getBootSourceMode() throws RemoteException {
        return Settings.System.getString(mContext.getContentResolver(), HHTConstant.BOOT_SOURCE_MODE);
    }

    /**
     * 获取当前source
     * @return
     * strName - source：
     * "DTV" "ATV" "VGA1" "OPS"
     * "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     * "TYPEC" "DP" "AV1" "YPBPR1"
     * "ANDROID" "STORAGE"
     */
    @Override
    public String getCurSource() throws RemoteException {
        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        return Tools.convertToSourceNameKey(inputSource);
    }

    /**
     * 获取信号源列表map
     *
     * @return  "DTV" "ATV" "VGA1" "OPS"
     * "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     * "TYPEC" "DP" "AV1" "YPBPR1"
     * "ANDROID" "STORAGE"
     */
    @Override
    public Map getInputSrcMap() throws RemoteException {
        if (HHTConstant.DEBUG) Log.d(TAG, "getInputSrcMap start");

        DataUtils.getInstance().querySourceInfos(mContext, srcMap);

        if (HHTConstant.DEBUG) Log.d(TAG, "getInputSrcMap end");
        return new HashMap<>(srcMap);
    }

    /**
     * 获取信号源插入状态
     *
     * @return
     * Map source：
     * "DTV" "ATV" "VGA1" "OPS"
     * "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     * "TYPEC" "DP" "AV1" "YPBPR1"
     * "ANDROID" "STORAGE"
     * status： SOURCE_DET_IN = "IN"; SOURCE_DET_OUT = "OUT"; SOURCE_DET_OFF = "off";
     */
    @Override
    public Map getInputSrcPlugStateMap() throws RemoteException {
        return new HashMap<>(srcPlugStateMap);
    }

    /**
     * 获取最近打开的source
     * @return
     */
    @Override
    public String getLastSource() throws RemoteException {
        return Settings.System.getString(mContext.getContentResolver(), HHTConstant.SOURCE_LAST);
    }

    /**
     * 获取预先设置的信号源
     *
     * @return
     */
    @Override
    public String getPreSource() throws RemoteException {
        return Settings.System.getString(mContext.getContentResolver(), HHTConstant.SOURCE_PRESET);
    }

    /**
     * 保存最近打开的 TV source
     *
     * @return source： "DTV" "ATV" "VGA1" "OPS"
     * "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     * "TYPEC" "DP" "AV1" "YPBPR1"
     * "ANDROID" "STORAGE"
     */
    @Override
    public String getRecentTvSource() throws RemoteException {
        return Settings.System.getString(mContext.getContentResolver(), HHTConstant.SOURCE_RECENT);
    }
    /**
     * 获得信号检测模式
     *
     * @return 0->off; 1->auto; 2->Manual
     */
    @Override
    public int getSourceDetectionMode() throws RemoteException {
        return Settings.System.getInt(mContext.getContentResolver(),
                SOURCE_DETECTION_MODE, HHTSourceManager.EnumSourceDetMode.DET_OFF.ordinal());
    }
//
//    @Override
//    public String getSourcePlugStateByKey(String strSource) throws RemoteException {
//        if (HHTConstant.DEBUG) Log.d(TAG, "getSourcePlugState:" + strSource);
//        int sourceInput = Tools.convertToSourceInput(strSource);
//        if (!Tools.isSourceInputValid(sourceInput)){
//            if (HHTConstant.DEBUG) Log.d(TAG, "getSourcePlugState: isSourceInputValid false");
//            return SOURCE_DET_OFF;
//        }
//        TvOsType.EnumInputSource enumInputSource = TvOsType.EnumInputSource.values()[sourceInput];
//        try {
//            boolean flag = TvManager.getInstance().getPlayerManager().detectInputSource(enumInputSource);
//            if (HHTConstant.DEBUG) Log.d(TAG, "getSourcePlugState flag:" + flag);
//            String sourceDet = flag ? SOURCE_DET_IN : SOURCE_DET_OFF;
//            srcPlugStateMap.put(strSource, sourceDet);
//            return sourceDet;
//        } catch (TvCommonException e) {
//            e.printStackTrace();
//            return SOURCE_DET_OFF;
//        }
//    }
    /**
     * 判断是否有信号
     *
     * @return true- 有信号； false-无信号
     */
    @Override
    public boolean isSignalLock() throws RemoteException {
        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        try {
            return TvManager.getInstance().isSignalStable(inputSource);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 判断当前窗口状态
     *
     * @return
     */
    @Override
    public boolean isTvWindow() throws RemoteException {
        return Tools.isTvWindow(mContext);
    }
    /**
     * 设置开机信号源模式使能开关
     * @param bStatus - true:此功能开启；false：此功能未启动
     * @return true->成功， false -> 失败
     */
    @Override
    public boolean setBootSourceEnable(boolean bStatus) throws RemoteException {
        int flag = bStatus ? 1 : 0;
        Settings.System.putInt(mContext.getContentResolver(), BOOT_SOURCE_ENABLE, flag);
        return true;
    }

    @Override
    public boolean setBootSourceMode(String strName) throws RemoteException {
        boolean flag = false;

        if (HHTSourceManager.MODE_FIXED_SOURCE.equals(strName)
                || HHTSourceManager.MODE_LAST_SOURCE.equals(strName) ){
            Settings.System.putString(mContext.getContentResolver(), HHTConstant.BOOT_SOURCE_MODE, strName);
            flag = true;
        }
        return flag;
    }

    /**
     * 保存当前source
     * @param strName
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean setCurSource(String strName) throws RemoteException {
        boolean flag = false;

        if (Tools.isSourceNameValid(strName)) {
            int inputSource = Tools.convertToSourceInput(strName);
            TvCommonManager.getInstance().setInputSource(inputSource);
            Settings.System.putString(mContext.getContentResolver(), SOURCE_CURRENT, strName);
            flag = true;
        }

        return flag;
    }


    /**
     * 更新信号源名字列表
     * @param sourceKey
     * @param sourceName
     * @return
     */
    @Override
    public boolean setInputSrcCustomerNameByKey(String sourceKey, String sourceName) throws RemoteException {
        if (!Tools.isSourceNameValid(sourceKey)){
            return false;
        }

        DataUtils.getInstance().updateSourceName(mContext, sourceKey, sourceName);
        srcMap.put(sourceKey, sourceName);

        return true;
    }

    /**
     * 保存最近打开的source
     * @param strName
     * @return
     */
    @Override
    public boolean setLastSource(String strName) throws RemoteException {
        boolean flag = false;

        if (Tools.isSourceNameValid(strName)) {
            Settings.System.putString(mContext.getContentResolver(), HHTConstant.SOURCE_LAST, strName);
            flag = true;
        }

        return flag;
    }
    /**
     * 保存预先设置的信号源
     *
     * @param strName - source： "DTV" "ATV" "VGA1" "OPS"
     *                "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     *                "TYPEC" "DP" "AV1" "YPBPR1"
     *                "ANDROID" "STORAGE"
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean setPreSource(String strName) throws RemoteException {
        boolean flag = false;

        if (Tools.isSourceNameValid(strName)) {
            Settings.System.putString(mContext.getContentResolver(), HHTConstant.SOURCE_PRESET, strName);
            flag = true;
        }

        return flag;
    }

    /**
     * 保存最近打开的 TV source
     *
     * @param strName "DTV" "ATV" "VGA1" "OPS"
     *                "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     *                "TYPEC" "DP" "AV1" "YPBPR1"
     *                "ANDROID" "STORAGE"
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean setRecentTvSource(String strName) throws RemoteException {
        boolean flag = false;

        if (Tools.isSourceNameValid(strName)) {
            Settings.System.putString(mContext.getContentResolver(), SOURCE_RECENT, strName);
            flag = true;
        }

        return flag;
    }

    @Override
    public boolean setSignalLock(boolean bStatus) throws RemoteException {
        return TvCommonManager.getInstance().isHdmiSignalMode();
    }

    @Override
    public boolean setSourceDetectionMode(int iMode) throws RemoteException {
        if (iMode >= HHTSourceManager.EnumSourceDetMode.DET_OFF.ordinal() && iMode <= HHTSourceManager.EnumSourceDetMode.DET_MANUAL.ordinal()){
            Settings.System.putInt(mContext.getContentResolver(), SOURCE_DETECTION_MODE, iMode);
            int bAutoSourceSwitch = (iMode == HHTSourceManager.EnumSourceDetMode.DET_OFF.ordinal()) ? 0 : 1;
            DataUtils.getInstance().updateSignalSwitchMode(mContext, bAutoSourceSwitch);
            return true;
        }
        return false;
    }

    /**
     * 信号源切换
     *
     * @param strInputsource - inputsource：
     *                       "DTV" "ATV" "VGA1" "OPS"
     *                       "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     *                       "TYPEC" "DP" "AV1" "YPBPR1"
     *                       "ANDROID" "STORAGE"
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean startSourcebyKey(String strInputsource) throws RemoteException {
        int sourceInput = Tools.convertToSourceInput(strInputsource);

        if (sourceInput >= TvCommonManager.INPUT_SOURCE_VGA
                && sourceInput <= TvCommonManager.INPUT_SOURCE_STORAGE) {
            TvCommonManager.getInstance().setInputSource(sourceInput);
            Tools.changeSignal(mContext, sourceInput);
            return true;
        }
        return false;
    }
}
