package com.hht.android.sdk.led;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

/**
 * HHTLedManager 是遥控灯接口管理类。
 * 遥控灯闪烁
 * 设置遥控灯模式
 * @author hht
 */
public class HHTLedManager {
    public static final String SERVICE = "sdk_LedManager";

    public enum EnumLED{
        LED_GREEN,
        LED_RED,
    }

    public static final int LED_RED = 0; // only use LED_RED 8
    public static final int LED_GREEN = 1;// only use LED_GREEN 9
    private static HHTLedManager INSTANCE;
    private static IHHTLed mService=null;
    private HHTLedManager(){
        IBinder service = ServiceManager.getService(HHTLedManager.SERVICE);
        mService= IHHTLed.Stub.asInterface(service);
    }

    public static HHTLedManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HHTLedManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HHTLedManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 设置Led遥控灯状态,有可能是反向，需要根据当前硬件决定
     * @param bStatus false--red, true--green
     * @return true:Success, or false: failed
     */
    public boolean setLedStatus(boolean bStatus){
        try {
            return mService.setLedStatus(bStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 设置Led遥控灯状态,有可能是反向，需要根据当前硬件决定
     * 0->开机默认红色; 1-> 开机默认绿色
     *
     * @param iMode
     * @return
     */
    public boolean setLedMode(int iMode){
        try {
            mService.setLedMode(iMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取LED的模式状态
     *  0->开机默认红色; 1-> 开机默认绿色
     * 有可能是反向，需要根据当前硬件决定
     *
     * @return
     */
    public int getLedMode(){
        try {
            return mService.getLedMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
