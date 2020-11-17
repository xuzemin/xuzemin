package com.hht.android.sdk.service;

import android.content.Context;
import android.os.RemoteException;
import android.provider.Settings;

import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.device.IHHTDevice;
import com.hht.android.sdk.service.utils.L;
import com.hht.android.sdk.service.utils.Tools;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.PictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.tv.service.DatabaseDesk;

import java.util.HashMap;
import java.util.Map;

public class HHTDeviceService extends IHHTDevice.Stub {
    private final static String TAG = HHTDeviceService.class.getSimpleName();

    private DatabaseDesk databaseDesk;

    private Context mContext;
    public HHTDeviceService(Context context){
        this.mContext=context;

        L.i("gyx","HHTDeviceService init");
    }

    /**
     * 获取亮度所有模式的列表map
     *
     * @return
     */
    @Override
    public Map getBrightnessMap() throws RemoteException {
        Map<String,String> map = new HashMap<>();

        int sourceMax = 34;
        for (int i = 0; i < sourceMax; i++) {
            int brightness = TvPictureManager.getInstance().getVideoItemByInputSource(TvPictureManager.PICTURE_BRIGHTNESS, i);
            map.put(i + "", brightness + "");
        }
        return map;
    }

    /**
     * 获取亮度模式
     *
     * @return
     */
    @Override
    public String getBrightnessMode() throws RemoteException {
//        try {
//            PictureManager.getInstance().get;
//            return true;
//        } catch (TvCommonException e) {
//            e.printStackTrace();
//        }
        return "";
    }

    /**
     * 获得背光
     *
     * @return
     */
    @Override
    public int getBrightnessValue() throws RemoteException {
        // 亮度
        return TvPictureManager.getInstance().getVideoItem(TvPictureManager.PICTURE_BRIGHTNESS);

        // 背光
//        return TvPictureManager.getInstance().getBacklight();
    }

    /**
     * 获取 env 变量
     *
     *     慎重使用, 因为这些变量即使是升级固件，也不会被擦除,需要主动进行擦除。
     *
     * @param strName - the name to lookup
     * @return
     */
    @Override
    public String getEnvironment(String strName) throws RemoteException {
        try {
            return TvManager.getInstance().getEnvironment(strName);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 光感
     *
     * @return
     */
    @Override
    public int getLightSensorValue() throws RemoteException {
        return Tools.getLightSense();
    }

    @Override
    public int getTempSensorValue() throws RemoteException {
        int mode = Settings.System.getInt(mContext.getContentResolver(),"ShowTempMode", 0);

        float tmp = Tools.getTmpValue();
        if (mode == HHTCommonManager.EnumTempMode.TEMP_F.ordinal()){// F mode
            tmp = 32 + tmp * 1.8F;
        }

        return (int)tmp;
    }

    @Override
    public boolean getUartOnOff() throws RemoteException {
        return TvFactoryManager.getInstance().getUartOnOff();
    }

    /**
     * 开光背光使能状态
     *
     *     关背光，息屏
     * @return true:亮屏， false:息屏
     */
    @Override
    public boolean isBacklightOff() throws RemoteException {
        int status = Settings.System.getInt(mContext.getContentResolver(),"isSeperateHear",0);
        return status == 0;
    }

    @Override
    public int[] readCmdStrToTVOS(String iCmd) throws RemoteException {
        try {
            return TvCommonManager.getInstance().setTvosCommonCommand(iCmd);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 开关背光使能
     *
     * @param bOnOff  - true:亮屏， false:息屏
     * @return true:Success, or false: failed
     */
    @Override
    public boolean setBacklightOff(boolean bOnOff) throws RemoteException {
        if (bOnOff){
            TvPictureManager.getInstance().enableBacklight();
        } else {
            TvPictureManager.getInstance().disableBacklight();
        }

        return true;
    }

    /**
     * 设置亮度模式
     *
     * @param strMode
     * @return
     */
    @Override
    public boolean setBrightnessMode(String strMode) throws RemoteException {
        // todo

        int value = TvPictureManager.PICTURE_MODE_USER;

        if ("".equals(strMode)){
            value = TvPictureManager.PICTURE_MODE_USER;
        }

        try {
            PictureManager.getInstance().setPictureModeBrightness((short) value);
            return true;
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 背光获取
     *
     * @param iVal
     * @return
     */
    @Override
    public boolean setBrightnessValue(int iVal) throws RemoteException {
        // 背光
//        if (iVal >= 0 && iVal <= 100){
//            SystemProperties.set("persist.sys.backlight", ""+iVal);
//            TvPictureManager.getInstance().setBacklight(iVal);
//            return true;
//        }

        // 亮度
        if (iVal >= 0 && iVal <= 100){
            return TvPictureManager.getInstance().setVideoItem(TvPictureManager.PICTURE_BRIGHTNESS, iVal);
        }

        return false;
    }

    @Override
    public boolean setUartOnOff(boolean bIsEnable) throws RemoteException {
        return TvFactoryManager.getInstance().setUartOnOff(bIsEnable);
    }

//    @Override
//    public boolean writeCmdStrToTVOS(int iCmd, String strData) throws RemoteException {
//        HHTDeviceManager.EnumRWCmd[] values = HHTDeviceManager.EnumRWCmd.values();
//        if (iCmd < 0 || iCmd >= values.length){
//            return false;
//        }
//        // "HHT_writeCmdStrToTVOS"+ "#"+ key + "#" + value;
//        String cmdStr = "HHT_writeCmdStrToTVOS"+ "#"+ values[iCmd].toString() + "#" + strData;
//        try {
//            TvManager.getInstance().setTvosCommonCommand(cmdStr);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    /**
     *
     * @param data  data由 cmd 和 value 组成 ,格式: cmd#value
     *              cmd {@link HHTTvosCommand}
     * @add wanghang 0326
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean writeCmdStrToTVOS(String data) throws RemoteException {
//        HHTDeviceManager.EnumRWCmd[] values = HHTDeviceManager.EnumRWCmd.values();
//        if (iCmd < 0 || iCmd >= values.length){
//            return false;
//        }
//        // "HHT_writeCmdStrToTVOS"+ "#"+ key + "#" + value;
//        String cmdStr = "HHT_writeCmdStrToTVOS"+ "#"+ values[iCmd].toString() + "#" + strData;
        try {
            TvManager.getInstance().setTvosCommonCommand(data);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean setBrightnessValueForThirdPartyApp(int iVal) throws RemoteException {
        L.d(TAG, "setBrightnessValueForThirdPartyApp start");
//        if (databaseDesk == null){
//            databaseDesk = DatabaseDesk.getInstance(mContext.getApplicationContext());
//        }
//        IDatabaseDesk.ExternSetting externSetting = databaseDesk.queryFactoryExtern();
//        externSetting.uiBrightness = iVal;
//        databaseDesk.updateFactoryExtern(externSetting);

        L.d(TAG, "setBrightnessValueForThirdPartyApp end");
        return true;
    }

    @Override
    public int getBrightnessValueForThirdPartyApp() throws RemoteException {
        L.d(TAG, "getBrightnessValueForThirdPartyApp start");
//        if (databaseDesk == null){
//            databaseDesk = DatabaseDesk.getInstance(mContext.getApplicationContext());
//        }
//        IFactoryDesk factoryDesk = FactoryDeskImpl.getInstance(mContext.getApplicationContext());
//        factoryDesk.queryFactoryExtern();


//        IDatabaseDesk.ExternSetting externSetting = databaseDesk.queryFactoryExtern();

//        L.d(TAG, "getBrightnessValueForThirdPartyApp:" + externSetting.uiBrightness);
//        return externSetting.uiBrightness;
        return -1;
    }
}
