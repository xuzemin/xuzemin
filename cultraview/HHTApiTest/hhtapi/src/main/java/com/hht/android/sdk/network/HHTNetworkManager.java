package com.hht.android.sdk.network;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

/**
 * HHTNetworkManager 是网络接口管理类。
 * 比如：
 * 网络开关，
 * WiFi开关，
 * 网络切换，
 * @author hht
 */
public class HHTNetworkManager {
    public static final String TAG = "HHTNetworkManager";
    public static final String SERVICE = "sdk_NetworkManager";

    public static final String ETHERNET_CONNECT_MODE_DHCP = "dhcp";
    public static final String ETHERNET_CONNECT_MODE_MANUAL = "manual";
    public static final String ETHERNET_CONNECT_MODE_NONE = "none";
    public static final String ETHERNET_CONNECT_MODE_PPPOE = "pppoe";
    public static final int ETHERNET_STATE_DISABLED = 0;
    public static final int ETHERNET_STATE_ENABLED = 1;
    public static final int ETHERNET_STATE_UNKNOWN = 2;


    private static HHTNetworkManager INSTANCE;
    private static IHHTNetwork mService=null;
    private HHTNetworkManager(){
        IBinder service = ServiceManager.getService(HHTNetworkManager.SERVICE);
        mService= IHHTNetwork.Stub.asInterface(service);
    }

    public static HHTNetworkManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HHTNetworkManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HHTNetworkManager();
                }
            }
        }
        return INSTANCE;
    }

 /*   *//**
     * 设置WiFi开关，定制功能
     * @param context
     * @return true-enable； false-disable
     *//*
    public boolean getWifiStatus(Context context){
        try {
            return mService.getWifiStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    *//**
     * 设置WiFi开关，定制功能
     * @param context
     * @param bEnable  -- true-enable； false-disable
     * @return 0->true， -1 -> false
     *//*
    public int setWifiStatus(Context context, boolean bEnable){
        try {
            return mService.setWifiStatus(bEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    *//**
     * 获取WiFi热点开关，定制功能
     * @return true-enable； false-disable
     *//*
    public boolean getWifiApStatus(){
        try {
            return mService.getWifiApStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    *//**
     * 设置WiFi开关，定制功能
     * @param bEnable- - true-enable； false-disable
     * @return 0->true， -1 -> false
     *//*
    public int setWifiApStatus( boolean bEnable){
        try {
            return mService.setWifiApStatus(bEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }*/

    /**
     * 网络唤醒开关
     * @param bEnable- - true-enable； false-disable
     * @return 0->true， -1 -> false
     */
    public boolean setWolEn(boolean bEnable){
        try {
            return mService.setWolEn(bEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 网络唤醒开关
     * @return true-enable； false-disable
     */
    public boolean getWolEn(){
        try {
            return mService.getWolEn();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get ethernet connect mode. 获取有线网连接模式
     *
     * @return
     */
    public String getEthernetMode(){
        try {
            return mService.getEthernetMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setEthernetEnabled(boolean enable){
        try {
            mService.setEthernetEnabled(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getEthernetState(){
        try {
            return mService.getEthernetState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Set ethernet connect mode. 设置有线网连接模式
     *
     * @param mode
     */
    public void setEthernetMode(String mode){
        try {
            mService.setEthernetMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Ethernet Hardware Address.
     *
     * @return
     */
    public String getEthernetMacAddress(){
        try {
            return mService.getEthernetMacAddress();
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d(TAG, "getEthernetMacAddress error:" + e.getMessage());
        }

        return "";
    }

    /**
     * Get Ethernet IP Address.
     *
     * @return
     */
    public String getEthernetIpAddress(){
        try {
            return mService.getEthernetIpAddress();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return "";
    }
}
