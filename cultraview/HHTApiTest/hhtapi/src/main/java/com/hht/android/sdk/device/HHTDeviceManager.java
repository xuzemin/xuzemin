package com.hht.android.sdk.device;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

import java.util.Map;

/**
 * HHTDeviceManager 是设备接口管理类。
 * 比如：
 * CMD接口向tvOS操作，
 * 文件拷贝，
 * gpio设置， 温度获取， 背光获取， 息屏，等
 */
public class HHTDeviceManager {
    public static final String SERVICE = "sdk_DeviceManager";

    public static final String MODE_BL_STANDARD = "MODE_BL_STANDARD";// 自动模式：根据当前环境亮度
    public static final String MODE_BL_CUS = "MODE_BL_CUS";// 自定义模式：可调节
    public static final String MODE_BL_AUTO = "MODE_BL_AUTO";// 节能模式：固定一个50亮度值，或者根据平台的接口（可自动根据当前画面，进行调整亮度）
    public static final String MODE_BL_ECO = "MODE_BL_ECO";// 标准模式：固定100亮度值或者一个合适的标准值
    private static IHHTDevice mService=null;
    private static HHTDeviceManager INSTANCE;
    public enum EnumRWCmd {
        CMD_MAX,
        CMD_TRST;
    }

    private  HHTDeviceManager(){
        IBinder service = ServiceManager.getService(HHTDeviceManager.SERVICE);
        mService=IHHTDevice.Stub.asInterface(service);
    }

    public static HHTDeviceManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HHTDeviceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HHTDeviceManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Write接口
     *
     *     通过此接口，可以往tvOS传输strCmd参数控制命令
     *     HHTDeviceManager.EnumRWCmd 通过EnumRWCmd 枚举进行控制命令，需要与tvOS对齐。
     *     HHTDeviceManager.EnumRWCmd.CMD_TRST
     * @param iCmd
     * @param strData
     * @return
     */
    public boolean writeCmdStrToTVOS(int iCmd, String strData) {
        try {
            return mService.writeCmdStrToTVOS(iCmd,strData);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Read接口，传输字符串参数命令
     *
     *     通过此接口，可以根据strCmd参数控制命令读取tvOS传输数据
     *     HHTDeviceManager.EnumRWCmd 通过EnumRWCmd 枚举进行控制命令，需要与tvOS对其。
     *     HHTDeviceManager.EnumRWCmd.CMD_TRST
     * @param iCmd
     * @return
     */
    public String readCmdStrToTVOS(int iCmd) {
        try {
            return mService.readCmdStrToTVOS(iCmd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获得温度
     * @return
     */
    public int getTempSensorValue(){
        try {
            return mService.getTempSensorValue();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 光感
     *
     * @return
     */
    public int getLightSensorValue(){
        try {
            return mService.getLightSensorValue();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 开关背光使能
     *
     * @param bOnOff  - true:亮屏， false:息屏
     * @return true:Success, or false: failed
     */
    public boolean setBacklightOff(boolean bOnOff){
        try {
            return mService.setBacklightOff(bOnOff);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 开光背光使能状态
     *
     *     关背光，息屏
     * @return true:亮屏， false:息屏
     */
    public boolean isBacklightOff(){
        try {
            return mService.isBacklightOff();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 亮度获取
     *
     * @param iVal
     * @return
     */
    public boolean setBrightnessValue(int iVal){
        try {
            return mService.setBrightnessValue(iVal);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 亮度设置
     *
     * @return
     */
    public int getBrightnessValue(){
        try {
            return mService.getBrightnessValue();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 设置亮度模式
     *
     * @param strMode
     * @return
     */
    public boolean setBrightnessMode(String strMode){
        // todo
        try {
            return mService.setBrightnessMode(strMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取亮度模式
     *
     * @return
     */
    public String getBrightnessMode(){
        try {
            return mService.getBrightnessMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取亮度所有模式的列表map
     *
     * @return
     */
    public Map<String,String> getBrightnessMap(){
        try {
            return mService.getBrightnessMap();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 第三方APP背光亮度设定
     *
     * @param iVal - 设置的背光值（0-100）
     * @return true:Success, or false: failed
     */
    public boolean setBrightnessValueForThirdPartyApp(int iVal){
        try {
            mService.setBrightnessValueForThirdPartyApp(iVal);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getBrightnessValueForThirdPartyApp(){
        // todo
        try {
            return mService.getBrightnessValueForThirdPartyApp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

//    /**
//     * 设置 env 变量
//     *
//     *     慎重使用, 因为这些变量即使是升级固件，也不会被擦除,需要主动进行擦除。
//     *
//     * @param strName
//     * @param strVal
//     * @return
//     */
//    public boolean setEnvironment(String strName, String strVal){
//        try {
//            return mService.setEnvironment(strName,strVal);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 获取 env 变量
     *
     *     慎重使用, 因为这些变量即使是升级固件，也不会被擦除,需要主动进行擦除。
     *
     * @param strName - the name to lookup
     * @return
     */
    public String getEnvironment(String strName){
        try {
            return mService.getEnvironment(strName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean setUartOnOff(boolean bIsEnable){
        try {
            return mService.setUartOnOff(bIsEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getUartOnOff(){
        try {
            return mService.getUartOnOff();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

}
