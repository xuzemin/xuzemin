package com.hht.android.sdk.service;

import android.content.Context;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.os.SystemProperties;
import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.service.utils.DataUtils;
import com.hht.android.sdk.service.utils.L;
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
import static com.hht.android.sdk.boardInfo.HHTConstant.SOURCE_RECENT;

public class HHTSourceService extends IHHTSource.Stub {
    private final static String TAG = HHTSourceManager.class.getSimpleName();
    private Context mContext;
    // 信号源列表map
    private Map<String, String> srcMap;
    // 信号源插入状态map
    private Map<String, String> srcPlugStateMap;
    public static final String[] LH_SOURCE_LIST = new String[]{
            HHTConstant.FRONT_HDMI, HHTConstant.HDMI2, HHTConstant.DP,
            HHTConstant.DTV, HHTConstant.OPS, HHTConstant.TYPEC,
            HHTConstant.AV, HHTConstant.YPBPR, HHTConstant.VGA,
            HHTConstant.OPS
    };

    // MH主板
    public static final String[] MH_SOURCE_LIST = new String[]{
            HHTConstant.ATV, HHTConstant.DTV, HHTConstant.HDMI1,
            HHTConstant.HDMI2, HHTConstant.OPS, HHTConstant.HDMI3,
            HHTConstant.DP, HHTConstant.AV, HHTConstant.YPBPR,
            HHTConstant.VGA
    };

    // AH主板
    public static final String[] AH_SOURCE_LIST = new String[]{
            HHTConstant.VGA, HHTConstant.HDMI1, HHTConstant.HDMI2,
            HHTConstant.HDMI3, HHTConstant.OPS
    };


    public HHTSourceService(Context context){
        this.mContext = context;

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(300);
                init();
            }
        }).start();
        L.i("gyx","HHTSourceService init");
    }

    private void init(){
        if(srcMap == null){
            srcMap = new ConcurrentHashMap<String, String>();
            initSrcMap();
        }
        if(srcPlugStateMap == null){
            srcPlugStateMap = new ConcurrentHashMap<String, String>();
            initSrcPlugStateMap();
        }
    }

    /**
     * 初始化sourceInput Map
     */
    private void initSrcMap(){
        L.d(TAG, "initSrcMap start");
        String[] sourceList = Tools.getSourceKeyList();
        for (String item: sourceList){
            srcMap.put(item, item);
        }

        DataUtils.getInstance().querySourceInfos(mContext, srcMap);
        L.d(TAG, "initSrcMap end");
    }
    /**
     * 初始化sourceInput Map
     */
    private void initSrcPlugStateMap(){
        String[] sourceList = Tools.getSourceKeyList();

        for (String item: sourceList){
            srcPlugStateMap.put(item, HHTConstant.SOURCE_DET_OFF);
        }
    }

    /**
     * 获取开机信号源模式使能开关状态
     *
     * @return true- enable； false-disable
     */
    @Deprecated
    @Override
    public boolean getBootSourceEnable() throws RemoteException {
        return true;
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
        return SystemProperties.get(HHTConstant.BOOT_SOURCE_MODE,HHTSourceManager.MODE_FIXED_SOURCE);
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
        return Tools.convertToSourceKey(inputSource);
    }

    /**
     * 获取信号源列表map
     *
     * @return  "DTV" "ATV" "VGA" "OPS"
     * "HDMI1" "HDMI2" "FRONT_HDMI"
     * "DP" "AV" "YPBPR"
     * "ANDROID" "STORAGE"
     */
    @Override
    public Map getInputSrcMap() throws RemoteException {
        L.d(TAG, "getInputSrcMap start");

        DataUtils.getInstance().querySourceInfos(mContext, srcMap);

        L.d(TAG, "getInputSrcMap end");
        return new HashMap<>(srcMap);
    }

    /**
     * 获取信号源插入状态
     *
     * @return
     * Map source：
     * " DTV" "ATV" "VGA" "OPS"
     * "HDMI1" "HDMI2" "FRONT_HDMI"
     * "DP" "AV" "YPBPR"
     * status： SOURCE_DET_IN = "IN"; SOURCE_DET_OUT = "OUT"; SOURCE_DET_OFF = "off";
     */
    @Override
    public Map getInputSrcPlugStateMap() throws RemoteException {
        Tools.updatePlugStateMap(srcPlugStateMap);
        return new HashMap<>(srcPlugStateMap);
    }

    /**
     * 获取最近打开的source
     * @return
     */
    @Override
    public String getLastSource() throws RemoteException {
        return SystemProperties.get(HHTConstant.BOOT_SOURCE,HHTConstant.OPS);
    }

    /**
     * 获取预先设置的信号源
     *
     * @return
     */
    @Override
    public String getPreSource() throws RemoteException {
        return SystemProperties.get(HHTConstant.BOOT_SOURCE,HHTConstant.OPS);
    }

    /**
     * 保存最近打开的 TV source
     *
     * @return source：
     * "DTV" "ATV" "VGA" "OPS"
     * "HDMI1" "HDMI2" "FRONT_HDMI"
     * "DP" "AV" "YPBPR"
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
        int defaultMode = HHTSourceManager.EnumSourceDetMode.DET_AUTO.ordinal();
        int mode = Settings.System.getInt(mContext.getContentResolver(),
                HHTConstant.SOURCE_DETECTION_MODE, -1);
        // set default mode
        if (mode == -1){
            Settings.System.putInt(mContext.getContentResolver(),
                    HHTConstant.SOURCE_DETECTION_MODE, defaultMode);
            int switchState = DataUtils.getInstance().getSignalSwitchMode(mContext.getApplicationContext());
            if (switchState == 0){
                DataUtils.getInstance().updateSignalSwitchMode(mContext.getApplicationContext(), 1);
            }
            mode = defaultMode;
        }

        return mode;
    }

    @Override
    public String getSourcePlugStateByKey(String strSource) throws RemoteException {
        L.d(TAG, "getSourcePlugState:" + strSource);
        int sourceInput = Tools.convertToInputSource(strSource);
        if (!Tools.isSourceInputValid(sourceInput)){
            L.d(TAG, "getSourcePlugState: isSourceInputValid false");
            return HHTConstant.SOURCE_DET_OFF;
        }

        Tools.updatePlugStateMap(srcPlugStateMap);

        String state = srcPlugStateMap.get(strSource);
        if (TextUtils.isEmpty(state)){
            state = HHTConstant.SOURCE_DET_OFF;
        }
        return state;
    }
    /**
     * 判断是否有信号
     *
     * @return true- 有信号； false-无信号
     */
    @Override
    public boolean isSignalLock() throws RemoteException {
        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        try {
            return TvManager.getInstance().getInputSourceLock(inputSource);
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
    @Deprecated
    @Override
    public boolean setBootSourceEnable(boolean bStatus) throws RemoteException {
        return true;
    }

    @Override
    public boolean setBootSourceMode(String strName) throws RemoteException {
        if (HHTSourceManager.MODE_FIXED_SOURCE.equals(strName)
                || HHTSourceManager.MODE_LAST_SOURCE.equals(strName) ){
            SystemProperties.set(HHTConstant.BOOT_SOURCE_MODE,strName);
            return true;
        }
        return false;
    }

    /**
     * 保存当前source
     * @param strName
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean setCurSource(String strName) throws RemoteException {
        boolean flag = false;

        int inputSource = Tools.convertToInputSource(strName);
        if (inputSource > -1){
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
        if (HHTSourceManager.MODE_LAST_SOURCE.equals(getBootSourceMode()) && Tools.isSourceNameValid(strName)) {
            SystemProperties.set(HHTConstant.BOOT_SOURCE,strName);
            return true;
        }

        return false;
    }
    /**
     * 保存预先设置的信号源
     *
     * @param strName - source：
     * "DTV" "ATV" "VGA" "OPS"
     * "HDMI1" "HDMI2" "FRONT_HDMI"
     * "DP" "AV" "YPBPR"
     *                "ANDROID" "STORAGE"
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean setPreSource(String strName) throws RemoteException {
        if (HHTSourceManager.MODE_FIXED_SOURCE.equals(getBootSourceMode()) && Tools.isSourceNameValid(strName)) {
            SystemProperties.set(HHTConstant.BOOT_SOURCE,strName);
            return true;
        }
        return false;
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
        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        return TvCommonManager.getInstance().setInputSourceLock(bStatus, inputSource);
    }

    @Override
    public boolean setSourceDetectionMode(int iMode) throws RemoteException {
        if (iMode >= HHTSourceManager.EnumSourceDetMode.DET_OFF.ordinal()
                && iMode <= HHTSourceManager.EnumSourceDetMode.DET_MANUAL.ordinal()){
            Settings.System.putInt(mContext.getContentResolver(), HHTConstant.SOURCE_DETECTION_MODE, iMode);
            int bAutoSourceSwitch = (iMode == HHTSourceManager.EnumSourceDetMode.DET_OFF.ordinal()) ? 0 : 1;
//            TvCommonManager.getInstance().setSourceSwitchState(bAutoSourceSwitch);
            DataUtils.getInstance().updateSignalSwitchMode(mContext.getApplicationContext(), bAutoSourceSwitch);
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
     *                       "TYPEC" "DP" "AV" "YPBPR1"
     *                       "ANDROID" "STORAGE"
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean startSourcebyKey(String strInputsource) throws RemoteException {
        return Tools.changeSignalBySourceKey(mContext, strInputsource);
    }

    @Override
    public boolean isCurrentSource(String sourceName) throws RemoteException {
        return Tools.isCurrentSource(sourceName);
    }

}
