
package com.ctv.settings.network.utils;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplayStatus;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.widget.ListView;

import com.ctv.settings.utils.L;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;

/**
 * @Copyright (C), 2015-12-8, CultraView
 * @author xuzemin@cultraview.com
 * @since 1.0.0 NetUtils.
 */
public class NetUtils {

    // Activity
//    public static final int NET_STATE = 0;

    public static final int OPEN_WIFI = 0;

    public static final int START_SCAN_WIFI = 1;

    public static final int REFRESH_SIGNAL_WIFI = 2;

    public static final int CHECKOUT_TIME_WIFI = 3;

    public static final int NET_STATE = 6;

    public static final int WIRE_CONNECT = 1;

    public static final int WIRELESS_CONNECT = 3;

    public static final int PPPOE_CONNECT = 2;

    public static final int SCREEN_HOT = 5;

    public static final int WIFI_HOTSPOT = 6;

    public static final int BLUETOOTH = 4;

//    public static final int PAGE_SIZE = 6;4

    // Dialog
    public static final int SECURE_OPEN = 0;

    public static final int SECURE_PSK = 1;

    public static final int SECURE_WEP = 2;

    public static final int SECURE_EAP = 3;

    public static final String WIFI_HOST_BAND = "Apband2G";

    public static final String SCREEN_HOST_BAND = "Apband5G";

    public static final String HOST_BAND_TYPE = "persist.ctv.ap.band";

    // WifiHotspot
    public static final int SECURE_TYPE_OPEN = 0;

    public static final int SECURE_TYPE_WPA = 2;

    public static final int SECURE_TYPE_WPA2 = 1;

    // WiFi
    public static final int SECURITY_NONE = 0;

    public static final int SECURITY_PSK = 1;

    public static final int SECURITY_WEP = 2;

    public static final int SECURITY_EAP = 3;

    // bluetooth_
    public static final int DISCOVERABLE_TIMEOUT_TWO_MINUTES = 120;

    public static final int DISCOVERABLE_TIMEOUT_FIVE_MINUTES = 300;

    public static final int DISCOVERABLE_TIMEOUT_ONE_HOUR = 3600;

    public static final int DISCOVERABLE_TIMEOUT_NEVER = 0;

    public static final String KEY_DISCOVERABLE_TIMEOUT = "discoverable_timeout";

    // Activity
    public static final String SHARE_NAME = "net_setting";

    public static final int ALL = 1;

    public static final String PPPOE_IS_AUTO_DIALER = "pppoe_is_auto_dialer";

    public static final String WIFI_SETTINGS = "android.settings.WIFI_SETTINGS";

    public static final String PICK_WIFI_NETWORK = "android.net.wifi.PICK_WIFI_NETWORK";

    public static final String WIRELESS_SETTINGS = "android.settings.WIRELESS_SETTINGS";

    public static final String ETHERNET_SETTINGS = "android.settings.ETHERNET_SETTINGS";

    public static final String WIFI_IP_SETTINGS = "android.settings.WIFI_IP_SETTINGS";

    public static final String DEVICE_NAME_CHANGE_ACTION = "android.settings.CTVDEVICE_NAME_CHANGE";

    public static final int Device_AUDIO_VIDEO = 512;

    public static final int Device_INPUTDEVICE = 1024;

    public static final int Device_PAN = 111;

    public static final int DEVICE_INPUT_DEVICE = 4;

    public static final int TYPE_NONE = -1;

    public static final int TYPE_ETHERNET = 9;

    public static final int INVALID_NETWORK_ID = -1;

    public static final int WIFI_AP_STATE_DISABLING = 10;

    public static final int WIFI_AP_STATE_DISABLED = 11;

    public static final int WIFI_AP_STATE_ENABLING = 12;

    public static final int WIFI_AP_STATE_ENABLED = 13;

    public static final int WIFI_AP_STATE_FAILED = 14;

    public static final int WPA2_PSK = 4;

    public static final int FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT = 0x04000000;

    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";

    public static final String ETHERNET_SERVICE = "ethernet";

    public static final String WIFI_DISPLAY_ON = "wifi_display_on";

    public static final int WIFI_STATE_UNKNOWN = 4;

    public static final String NETWORK_STATE_CHANGED_ACTION = "android.net.wifi.STATE_CHANGE";

    public static final String EXTRA_WIFI_DISPLAY_STATUS =
            "android.hardware.display.extra.WIFI_DISPLAY_STATUS";

    public static final String ACTION_WIFI_DISPLAY_STATUS_CHANGED =
            "android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED";

    public static final String WIFI_P2P_PERSISTENT_GROUPS_CHANGED_ACTION =
            "android.net.wifi.p2p.PERSISTENT_GROUPS_CHANGED";

    public static final String INET_CONDITION_ACTION =
            "android.net.conn.INET_CONDITION_ACTION";

    public static final String WIFI_STATE_CHANGED_ACTION =
            "android.net.wifi.WIFI_STATE_CHANGED";

    public static final String EXTRA_WIFI_STATE = "wifi_state";

    public static final String WIFI_P2P_DEVICE_NAME = "wifi_p2p_device_name";

    public static final String WIFI_AP_STATE_CHANGED_ACTION =
            "android.net.wifi.WIFI_AP_STATE_CHANGED";

    public static WifiConfiguration getWifiApConfiguration(WifiManager mWifiManager) {
        try {
            Method method = mWifiManager.getClass().getMethod(
                    "getWifiApConfiguration");
            return (WifiConfiguration) method.invoke(mWifiManager);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<LinkAddress> getAllLinkAddresses(LinkProperties prop) throws Exception{
        if (prop == null) return null;
        List<LinkAddress> addressesList = (List<LinkAddress>) prop.getClass().getMethod("getAllLinkAddresses").invoke(prop);
        return addressesList;
    }

    public static LinkProperties getLinkProperties(ConnectivityManager prop,int number) throws Exception{
        if (prop == null) return null;
        LinkProperties addressesList = (LinkProperties) prop.getClass().getMethod("getLinkProperties",int.class).invoke(prop,number);
        return addressesList;
    }

    public static String removeDoubleQuotes(WifiInfo wifiInfo,String ssid) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        if (ssid == null) return null;
        String doubleQuotes = (String) wifiInfo.getClass().getMethod("removeDoubleQuotes", String.class).invoke(wifiInfo, ssid);
        return doubleQuotes;
    }

    public static Boolean isNetworkSupported(ConnectivityManager mConnectivityManager,int netType) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
//        if (mConnectivityManager == null) return null;
//        return (boolean) mConnectivityManager.getClass().getMethod("isNetworkSupported", Integer.class).invoke(mConnectivityManager, TYPE_ETHERNET);

        final Method wifiCheckMethod = mConnectivityManager.getClass().getMethod("isNetworkSupported", int.class);
        boolean hasMobileNetwork = (Boolean) wifiCheckMethod.invoke(mConnectivityManager, netType);
//        String status = hasMobileNetwork ? "This device has mobile support model" : "This is wifi only model";
        L.i( "The network status is..."+hasMobileNetwork);
        return hasMobileNetwork;
    }

    public static void forgetNull(WifiManager mWifiManager,int number) throws Exception {
        if (mWifiManager == null)
            return;
        Method forget = mWifiManager.getClass().getDeclaredMethod("forget", int.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
        forget.setAccessible(true);
        forget.invoke(mWifiManager, number, null);
    }

//    static Object reflectInstance(String className, Class[] argTypes,
//        try {
//            Class viewClass = Class.forName(className);
//            Constructor con = viewClass.getConstructor(argTypes);
//            return con.newInstance(args);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//        return null;
//    }

    public static Boolean setWifiApConfiguration(WifiManager mWifiManager,WifiConfiguration config) throws Exception {
        Method method = mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
        return (boolean) method.invoke(mWifiManager,config);
    }

    public static Boolean setWifiApEnabled(WifiManager mWifiManager,WifiConfiguration config,boolean isflag) throws Exception {
        Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class,Boolean.TYPE);
        return (boolean) method.invoke(mWifiManager,config,isflag);
    }



    public static int getWifiApState(WifiManager mWifiManager) throws Exception {
        Method method = mWifiManager.getClass().getMethod("getWifiApState");
        return (int) method.invoke(mWifiManager);
    }

    public static boolean isWifiApEnabled(WifiManager mWifiManager) throws Exception {
        Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
        return (boolean) method.invoke(mWifiManager);
    }

    public static void setDeviceName(WifiP2pManager mWifiP2pManager, WifiP2pManager.Channel c, String devName, WifiP2pManager.ActionListener listener) throws Exception {
        Method method = mWifiP2pManager.getClass().getMethod("setDeviceName",WifiP2pManager.Channel.class,String.class,WifiP2pManager.ActionListener.class);
        method.invoke(mWifiP2pManager,c,devName,listener);
    }

    public static void forgetWifiDisplay(DisplayManager mDisplayManager,String address) throws Exception {
        Method method = mDisplayManager.getClass().getMethod("forgetWifiDisplay",String.class);
        method.invoke(mDisplayManager,address);
    }

    public static void renameWifiDisplay(DisplayManager mDisplayManager,String address,String name) throws Exception {
        Method method = mDisplayManager.getClass().getMethod("renameWifiDisplay",String.class,String.class);
        method.invoke(mDisplayManager,address,name);
    }

    public static void disconnectWifiDisplay(DisplayManager mDisplayManager) throws Exception {
        Method method = mDisplayManager.getClass().getMethod("disconnectWifiDisplay");
        method.invoke(mDisplayManager);
    }

    public static WifiDisplayStatus getWifiDisplayStatus(DisplayManager mDisplayManager) throws Exception {
        Method method = mDisplayManager.getClass().getMethod("getWifiDisplayStatus");
        return (WifiDisplayStatus)method.invoke(mDisplayManager);
    }


    public static ListView connect(WifiManager wifiManager,WifiConfiguration config) throws Exception {
        Class viewClass = Class.forName("android.net.wifi.WifiManager$ActionListener");
        Method method = wifiManager.getClass().getMethod("connect",WifiConfiguration.class,viewClass);
        return (ListView) method.invoke(wifiManager,config,null);
    }

    public static void setProxySettings(WifiConfiguration config,Object obj) throws Exception {
        Class viewClass = obj.getClass();
        Method method = config.getClass().getMethod("setProxySettings",viewClass);
        method.invoke(config,obj);
    }

    public static void setIpAssignment (WifiConfiguration config,Object obj) throws Exception {
        Class viewClass = obj.getClass();
        Method method = config.getClass().getMethod("setIpAssignment",viewClass);
        method.invoke(config,obj);
    }

    public static void setIpConfiguration (WifiConfiguration config,Object obj) throws Exception {
        Class viewClass = obj.getClass();
        Method method = config.getClass().getMethod("setIpAssignment",viewClass);
        method.invoke(config,obj);
    }

    public static LinkAddress getLinkAddress(InetAddress address, int prefixLength) {
        LinkAddress obj = null;
        try {
            Constructor constructor = LinkAddress.class.getDeclaredConstructor(new Class[]{InetAddress.class,int.class});
            if (constructor != null) {
                obj = (LinkAddress) constructor.newInstance(address,prefixLength);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static UserHandle getUserHandle(int id) {
        UserHandle obj = null;
        try {
            Constructor constructor = UserHandle.class.getDeclaredConstructor(new Class[]{int.class});
            if (constructor != null) {
                obj = (UserHandle) constructor.newInstance(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static UserHandle getActionListener(int id) {
        UserHandle obj = null;
        try {
            Constructor constructor = UserHandle.class.getDeclaredConstructor(new Class[]{int.class});
            if (constructor != null) {
                obj = (UserHandle) constructor.newInstance(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /*
    像素转换
    */
    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    /**
     * 判断是否为5G热点
     *
     * @return
     */
    public static boolean isHotspot5G(){
        String wifiapband = SystemProperties.get(HOST_BAND_TYPE);
        return TextUtils.equals(wifiapband, SCREEN_HOST_BAND);
    }

    /**
     * 设置热点类型
     *
     * @return
     */
    public static void setHotspotType(boolean isAp5G){
        // 切换热点类型
        String type = isAp5G ? SCREEN_HOST_BAND : WIFI_HOST_BAND;
        SystemProperties.set(HOST_BAND_TYPE, type);
    }
}


