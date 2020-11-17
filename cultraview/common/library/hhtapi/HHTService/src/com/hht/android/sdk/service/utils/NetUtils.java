package com.hht.android.sdk.service.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.StaticIpConfiguration;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.network.HHTNetworkManager;
import com.hht.android.sdk.network.LinkConfig;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;
/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2020/1/3 16:09
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/1/3 16:09
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class NetUtils {
    private static final String TAG = "NetUtils";
    public static final String ETHERNET_SERVICE = "ethernet";

    /**
     * 获得Ethernet Mac
     *
     *
     *
     *
     *
     * @param context
     * @return
     */
    public static String getEthernetMacAddress(Context context){
        L.d(TAG, "getEthernetMacAddress start");
        String ETH_ADDRESS_PATH = "/sys/class/net/eth0/address";
//        EthernetManager mEthernetManager = (EthernetManager) context.getSystemService(ETHERNET_SERVICE);
//        String mac = mEthernetManager.getMacAddress();
//        mac = TextUtils.isEmpty(mac) ? Tools.loadFileAsString(ETH_ADDRESS_PATH) : mac;
        String mac = Tools.loadFileAsString(ETH_ADDRESS_PATH);
        if (TextUtils.isEmpty(mac)){
            mac = "0:0:0:0:0:0";
        }
        L.d(TAG, "getEthernetMacAddress end mac:" + mac);
        return mac;
    }

    /**
     * 获得 Ethernet Ip
     * @param context
     * @return
     */
    public static String getEthernetIpAddress(Context context){
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        // 判断是否为以太网
        if (networkInfo == null
                || (networkInfo.getType() != ConnectivityManager.TYPE_ETHERNET)
                || !networkInfo.isConnected()){
            return "";
        }

        LinkProperties linkProperties = null;
        try {
            linkProperties = NetUtils.getLinkProperties(mConnectivityManager, ConnectivityManager.TYPE_ETHERNET);
        } catch (Exception e) {
            L.d(TAG, "getEthernetIpAddress error:" + e.toString());
        }

        List<LinkAddress> listLink = null;
        try {
            listLink = NetUtils.getAllLinkAddresses(linkProperties);
        } catch (Exception e) {
            L.d(TAG, e.toString());
        }

        String ethernetIP = "";
        if (linkProperties != null
                && listLink != null
                && !listLink.isEmpty()) {

            for (LinkAddress linkAddress : listLink) {
                InetAddress address = linkAddress.getAddress();
                if (address instanceof Inet4Address) {
                    String ip = address.getHostAddress();
                    if (TextUtils.isEmpty(ip)) {
                        return "0.0.0.0";
                    }

                    ethernetIP = ip;
                }
            }
        }
        return ethernetIP;
    }

    /**
     * 设置网络模式
     *
     * @param context
     * @param mode
     */
    public static void setEthernetMode(Context context, String mode){
        if (isWifiConnected(context)){
            return;
        }

        EthernetManager mEthernetManager = (EthernetManager) context.getSystemService(ETHERNET_SERVICE);
        IpConfiguration ipConfiguration = mEthernetManager.getConfiguration();

        if (TextUtils.equals(mode, HHTNetworkManager.ETHERNET_CONNECT_MODE_DHCP)){
            ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.DHCP);
        } else if (TextUtils.equals(mode, HHTNetworkManager.ETHERNET_CONNECT_MODE_PPPOE)){
            ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.PPPOE);
        } else if (TextUtils.equals(mode, HHTNetworkManager.ETHERNET_CONNECT_MODE_MANUAL)){
            ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
        } else {
            ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.DHCP);
        }

        mEthernetManager.setConfiguration(ipConfiguration);

//        boolean isEnabled = mEthernetManager.isEnabled();
//        ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
    }

    /**
     * 打开以太网
     *
     * @param context
     * @param bEnable
     * @return
     */
    public static boolean setEthernetEnabled(Context context, boolean bEnable){
        EthernetManager mEthernetManager = (EthernetManager) context.getSystemService(ETHERNET_SERVICE);
        mEthernetManager.setEnabled(bEnable);
        return true;
    }
    private static String mode = "";
    /**
     * 设置网络模式
     *
     * @param context
     */
    public static String getEthernetMode(final Context context){
        if (HHTConstant.DEBUG) L.d(TAG, "getEthernetMode start-----");
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @java.lang.Override
            public void run() {
                EthernetManager mEthernetManager = (EthernetManager) context.getSystemService(ETHERNET_SERVICE);
                IpConfiguration ipConfiguration = mEthernetManager.getConfiguration();
                IpConfiguration.IpAssignment ipAssignment = ipConfiguration.getIpAssignment();
                mode = HHTNetworkManager.ETHERNET_CONNECT_MODE_DHCP;
                if (ipAssignment == IpConfiguration.IpAssignment.DHCP){
                    mode = HHTNetworkManager.ETHERNET_CONNECT_MODE_DHCP;
                }else if (ipAssignment == IpConfiguration.IpAssignment.STATIC){
                    mode = HHTNetworkManager.ETHERNET_CONNECT_MODE_MANUAL;
                } else if (ipAssignment == IpConfiguration.IpAssignment.PPPOE){
                    mode = HHTNetworkManager.ETHERNET_CONNECT_MODE_PPPOE;
                } else {
                    mode = HHTNetworkManager.ETHERNET_CONNECT_MODE_NONE;
                }
                if (HHTConstant.DEBUG) L.d(TAG, "getEthernetMode end mode is " + mode);

            }
        });
        try{
            Thread.sleep(1000);
        }catch(Exception e){
            L.e(TAG, "Thread error"+e);
        }
        if (HHTConstant.DEBUG) L.d(TAG, "getEthernetMode end mode is " + mode);
        return mode;
    }

    public static int getEthernetState(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (HHTConstant.DEBUG) {
            L.d(TAG, "getEthernetState Ethernet networkInfo=" + networkInfo);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            return HHTNetworkManager.ETHERNET_STATE_ENABLED;
        } else {
            return HHTNetworkManager.ETHERNET_STATE_DISABLED;
        }
    }

    public static LinkProperties getLinkProperties(ConnectivityManager prop,int number) throws Exception{
        if (prop == null) return null;
        LinkProperties addressesList = (LinkProperties) prop.getClass().getMethod("getLinkProperties",int.class).invoke(prop,number);
        return addressesList;
    }

    /**
     * 获取当前 详细的连接网络类型
     * @param context
     * @return
     */
    public static int getNetType(Context context) {
        int netType = 0;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;// wifi
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager mTelephony = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    && !mTelephony.isNetworkRoaming()) {
                netType = 2;// 2G
            } else {
                netType = 3;// 3G
            }
        } else if (nType == ConnectivityManager.TYPE_ETHERNET){
            netType = ConnectivityManager.TYPE_ETHERNET;// ETHERNET
        }
        return netType;
    }

    public static List<LinkAddress> getAllLinkAddresses(LinkProperties prop) throws Exception{
        if (prop == null) return null;
        List<LinkAddress> addressesList = (List<LinkAddress>) prop.getClass().getMethod("getAllLinkAddresses").invoke(prop);
        return addressesList;
    }

    /**
     * 判断WiFi是否连接
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        if (DEBUG) {
            L.d(TAG, "updateWifiStatus wifi networkInfo=" + networkInfo);
//        }
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    /**
     * 设置IPConfig信息
     * @param config
     */
    public static void setLinkConfig(Context context, LinkConfig config){
        IpConfiguration ipConfiguration = new IpConfiguration();
        if (config.isDhcp) { // DHCP
            ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.DHCP);
        } else {
            ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
            StaticIpConfiguration staticIpConfiguration = new StaticIpConfiguration();

            InetAddress ip = NetworkUtils.numericToInetAddress(config.ipAddress);

            int prefixLength = NetworkUtils.netmaskToPrefixLength((Inet4Address)NetworkUtils.numericToInetAddress(config.netmask));
            staticIpConfiguration.ipAddress = new LinkAddress(ip, prefixLength);

            if(!TextUtils.isEmpty(config.gateway)){
                staticIpConfiguration.gateway  = NetworkUtils.numericToInetAddress(config.gateway);
            }
            if(!TextUtils.isEmpty(config.dns1)){
                staticIpConfiguration.dnsServers.add(NetworkUtils.numericToInetAddress(config.dns1));
            }
            if(!TextUtils.isEmpty(config.dns2)){
                staticIpConfiguration.dnsServers.add(NetworkUtils.numericToInetAddress(config.dns2));
            }

            ipConfiguration.setStaticIpConfiguration(staticIpConfiguration);
        }

        try {
            EthernetManager mEthernetManager = (EthernetManager) context.getSystemService(ETHERNET_SERVICE);
            mEthernetManager.setConfiguration(ipConfiguration);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
