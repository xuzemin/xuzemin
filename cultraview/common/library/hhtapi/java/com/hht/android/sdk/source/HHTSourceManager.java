package com.hht.android.sdk.source;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * HHTSourceManager 是信号源相关接口管理类。
 * 匹配不同方案的接口（MSTAR，海思平台），
 * 此类都是进行信号源相关的设置，
 * 比如：
 * 信号源修改名字，
 * 信号源切换，
 * 信号源获取，
 * 信号源设置，等
 */
public class HHTSourceManager {
    private final static String TAG = HHTSourceManager.class.getSimpleName();

    public static final String SERVICE = "sdk_SourceManager";


    // "DTV" "ATV" "VGA1" "OPS" "HDMI1" "HDMI2" "HDMI3" "HDMI4"
    // "TYPEC" "DP" "AV1" "YPBPR1" "ANDROID" "STORAGE"

    public static final String 	MODE_FIXED_SOURCE = "MODE_FIXED_SOURCE";
    // 预设固定信号源启动模式
    public static final String 	MODE_LAST_SOURCE = "MODE_LAST_SOURCE";
    // 记忆信号源启动模式

    // 信号源侦测模式
    public static final String 	SOURCE_DET_IN = "SOURCE_DET_IN";
    public static final String 	SOURCE_DET_OFF = "SOURCE_DET_OFF";
    public static final String 	SOURCE_DET_OUT = "SOURCE_DET_OUT";

    // 信号源
    public static final String ANDROID = "ANDROID";
    public static final String ATV = "ATV";
    public static final String AV1 = "AV1";
    public static final String AV2 = "AV2";
    public static final String DP = "DP";
    public static final String DP2 = "DP2";
    public static final String DTV = "DTV";
    public static final String HDMI1 = "HDMI1";
    public static final String HDMI2 = "HDMI2";
    public static final String HDMI3 = "HDMI3";
    public static final String HDMI4 = "HDMI4";
    public static final String HDMI5 = "HDMI5";
    public static final String HDMI6 = "HDMI6";
    public static final String OPS = "OPS";
    public static final String OPS2 = "OPS2";
    public static final String STORAGE = "STORAGE";
    public static final String TYPEC = "TYPEC";
    public static final String TYPEC2 = "TYPEC2";
    public static final String VGA1 = "VGA1";
    public static final String VGA2 = "VGA2";
    public static final String VGA3 = "VGA3";
    public static final String YPBPR1 = "YPBPR1";
    public static final String YPBPR2 = "YPBPR2";

    private static HHTSourceManager INSTANCE;
    private static IHHTSource mService=null;

    public enum EnumSourceDetMode{
        DET_OFF,
        //关闭
        DET_AUTO,
        //自动模式
        DET_MANUAL,
        //手动选择跳转模式
    }

    private HHTSourceManager(){
        IBinder service = ServiceManager.getService(HHTSourceManager.SERVICE);
        mService = IHHTSource.Stub.asInterface(service);
    }
    public static HHTSourceManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HHTSourceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HHTSourceManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 更新信号源名字列表
     * @param sourceKey
     * @param sourceName
     * @return
     */
    public boolean setInputSrcCustomerNameByKey(String sourceKey, String sourceName) {
        try {
            return mService.setInputSrcCustomerNameByKey(sourceKey,sourceName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取信号源排序列表
     * @return
     * 排序列表 "DTV" "ATV" "VGA1" "OPS"
     * "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     * "TYPEC" "DP" "AV1" "YPBPR1"
     * "ANDROID" "STORAGE"
     */
//    public List<String> sortList() {
//        Collection<String> valueCollection = srcMap.values();
////        List<String> arrayList = new ArrayList<>(Arrays.asList(SOURCE_LIST));
//        return new ArrayList<String>(valueCollection);//Arrays.asList(SOURCE_LIST);
//    }

    /**
     * 获取信号源列表map
     *
     * @return  "DTV" "ATV" "VGA1" "OPS"
     * "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     * "TYPEC" "DP" "AV1" "YPBPR1"
     * "ANDROID" "STORAGE"
     */
    public Map<String, String> getInputSrcMap() {
        try {
            return mService.getInputSrcMap();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
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
    public Map<String, String> getInputSrcPlugStateMap() {
        try {
            return mService.getInputSrcPlugStateMap();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }



    /**
     * 保存最近打开的source
     * @param strName
     * @return
     */
    public boolean setLastSource(String strName) {
        try {
            return mService.setLastSource(strName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取最近打开的source
     * @return
     */
    public String getLastSource() {
        try {
            return mService.getLastSource();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
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
    public boolean setRecentTvSource(String strName) {
        try {
            return mService.setRecentTvSource(strName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 保存最近打开的 TV source
     *
     * @return source： "DTV" "ATV" "VGA1" "OPS"
     * "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     * "TYPEC" "DP" "AV1" "YPBPR1"
     * "ANDROID" "STORAGE"
     */
    public String getRecentTvSource() {
        try {
            return mService.getRecentTvSource();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 保存当前source
     * @param strName
     * @return 0->成功， -1 -> 失败
     */
    public boolean setCurSource(String strName) {
        try {
            return mService.setCurSource(strName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
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
    public String getCurSource() {
        try {
            return mService.getCurSource();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
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
    public boolean setPreSource(String strName) {
        try {
            return mService.setPreSource(strName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取预先设置的信号源
     *
     * @return
     */
    public String getPreSource() {
        try {
            return mService.getPreSource();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设置开机信号源模式
     *
     * @param strName -
     *     MODE_FIXED_SOURCE
     *     MODE_LAST_SOURCE
     * @return
     */
    public boolean setBootSourceMode(String strName) {
        try {
            return mService.setBootSourceMode(strName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取开机信号源模式
     *
     * @return
     *     MODE_FIXED_SOURCE
     *     MODE_LAST_SOURCE
     */
    public String getBootSourceMode() {
        try {
            return mService.getBootSourceMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断当前是TV信号源
     *
     * @return true->TV信号源 false->非TV信号源
     */
//    public boolean isTVSource() {
//        return Tools.isTVSource();
//    }

    /**
     * 判断当前是VGA信号源
     *
     * @return true- 是VGA信号源； false- 不是VGA信号源
     */
//    public boolean isVGASource() {
//        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
//        return (inputSource == TvCommonManager.INPUT_SOURCE_VGA);
//    }

    /**
     * 判断当前是HDMI信号源
     * @return true- 是HDMI信号源； false- 不是HDMI信号源
     */
//    public boolean isHDMISource() {
//        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
//        return (inputSource >= TvCommonManager.INPUT_SOURCE_HDMI
//                && inputSource <= TvCommonManager.INPUT_SOURCE_HDMI4);
//    }

    /**
     * 判断当前是OPS信号源
     *
     * @return true- 是OPS信号源； false- 不是OPS信号源
     */
//    public boolean isOPSSource() {
//        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
//        return (inputSource == TvCommonManager.INPUT_SOURCE_HDMI4);
//    }

    /**
     * 判断当前是DP信号源
     *
     * @return true- 是DP信号源； false- 不是DP信号源
     */
    public boolean isDPSource() {
        // todo
        return false;
    }

//    /**
//     * 判断当前是AVorYPBPR信号源
//     *
//     * @return true- 是AVorYPBPR信号源； false- 不是AVorYPBPR信号源
//     */
//    public boolean isAVorYPBPRSource() {
//        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
//        return (inputSource == TvCommonManager.INPUT_SOURCE_YPBPR
//                || inputSource == TvCommonManager.INPUT_SOURCE_CVBS);
//    }

    public boolean setSignalLock(boolean bStatus) {
        try {
            return mService.setSignalLock(bStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否有信号
     *
     * @return true- 有信号； false-无信号
     */
    public boolean isSignalLock() {
        try {
            return mService.isSignalLock();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置开机信号源模式使能开关
     * @param bStatus - true:此功能开启；false：此功能未启动
     * @return true->成功， false -> 失败
     */
    @Deprecated
    public boolean setBootSourceEnable(boolean bStatus) {
        try {
            return mService.setBootSourceEnable(bStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取开机信号源模式使能开关状态
     *
     * @return true- enable； false-disable
     */
    @Deprecated
    public boolean getBootSourceEnable() {
        try {
            return mService.getBootSourceEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置将要执行信号源：(已弃用)
     *
     * @param strSourceName - sourceName： "DTV" "ATV" "VGA1" "OPS"
     *                      "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     *                      "TYPEC" "DP" "AV1" "YPBPR1"
     *                      "ANDROID" "STORAGE"
     * @return 0->成功， -1 -> 失败
     */
    @Deprecated
    public int setSourceConfig(String strSourceName) {
        // todo
        return 1;
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
//    public int setSourceChange(String strInputsource) {
//        // todo
//        int sourceInput = Tools.convertToSourceInput(strInputsource);
//
//        if (sourceInput >= TvCommonManager.INPUT_SOURCE_VGA
//                && sourceInput <= TvCommonManager.INPUT_SOURCE_STORAGE) {
//            TvCommonManager.getInstance().setInputSource(sourceInput);
//            return 0;
//        }
//        return -1;
//    }

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
    public boolean startSourcebyKey(String strInputsource) {
        try {
            return mService.startSourcebyKey(strInputsource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取信号源是否已经插入
     * @param strSource - source： "DTV" "ATV" "VGA1" "OPS"
     *                  "HDMI1" "HDMI2" "HDMI3" "HDMI4"
     *                  "TYPEC" "DP" "AV1" "YPBPR1"
     *                  "ANDROID" "STORAGE"
     * @return status： SOURCE_DET_IN = "IN"; SOURCE_DET_OUT = "OUT"; SOURCE_DET_OFF = "off";
     */
    public String getSourcePlugStateByKey(String strSource){
        try {
            return mService.getSourcePlugStateByKey(strSource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设置信号检测模式
     * @param iMode - 0->off; 1->auto; 2->Manual 是否需要手动切换 枚举类型
     * @return 0->成功， -1 -> 失败
     */
    public boolean setSourceDetectionMode(int iMode){
        try {
            return mService.setSourceDetectionMode(iMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获得信号检测模式
     *
     * @return 0->off; 1->auto; 2->Manual
     */
    public int getSourceDetectionMode(){
        try {
            return mService.getSourceDetectionMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断当前窗口状态
     *
     * @return
     */
    public boolean isTvWindow(){
        try {
            return mService.isTvWindow();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // todo
        return false;
    }

 /*   *//**
     * 冻屏 画面静止
     *
     * @return
     *//*
    public boolean enableFreeze(){
        try {
            return mService.enableFreeze();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    *//**
     * 解冻屏 解除画面静止
     *
     * @return
     *//*
    public boolean disableFreeze(){
        try {
            return mService.disableFreeze();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }*/

//    /**
//     * 当前信号源是否为OPS
//     * @return
//     */
//    public boolean isInOps() {
//        try {
//            return mService.isInOps();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * 当前信号源是否为HDMI1
//     * 判断是否处于ops占用通道的信号源下,不一定是HDMI1,如果占用HDMIX,则将接口改为isInHDMIX
//     * @return
//     */
//    public boolean isInHDMI1() {
//        try {
//            return mService.isInHDMI1();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 当前信号源是否为指定的source
     * @return
     */
    public boolean isCurrentSource(String sourceName){
        try {
            return mService.isCurrentSource(sourceName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
}
