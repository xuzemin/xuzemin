/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ctv.settings.network.Listener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.*;
import android.net.IpConfiguration.IpAssignment;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
// MStar Android Patch Begin
import android.net.wifi.WifiSsid;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;

import com.ctv.settings.network.R;
import com.ctv.settings.network.helper.WifiConfigHelper;
import com.ctv.settings.network.utils.*;
import com.ctv.settings.utils.L;

/**
 * Listens for changes to the current connectivity status.
 */
@SuppressLint("NewApi")
public class ConnectivityListener {
    private final boolean mHasProxySettigns = false;

    private NetworkInfo mNetworkInfo = null;

    private WifiConfiguration mConfig = null;

    private List<ScanResult> mScanRst = null;

    public interface Listener {
        void onConnectivityChange(Intent intent);

        void onPPPoeChanged(String status);

        void onEthernetAvailabilityChanged(boolean isAvailable);
    }

    public interface WifiNetworkListener {
        void onWifiListChanged();
    }

    private static final String TAG = "ConnectivityListener";

    private static final boolean DEBUG = true;

    private final Context mContext;

    private final Listener mListener;

    private final IntentFilter mFilter;

    private final BroadcastReceiver mReceiver;

    private boolean mStarted = false;

    private final ConnectivityManager mConnectivityManager;

    private final WifiManager mWifiManager;

    private final EthernetManager mEthernetManager;

    private WifiNetworkListener mWifiListener = null;

    private final PPPoEDialer mPPPoEDialer;

    private String mPPPoeStatusDescription = "";

    private int mNetStatus = -1;

    private final static int TYPE_PPPOE = 15;

    private LinkProperties mErthernetLinkProperties = null;

    private LinkProperties mWifiLinkProperties = null;

    private WifiInfo mWifiInfo = null;

    private boolean isAutoDialer = false;

    private boolean isAutoip = true;

    private final BroadcastReceiver mWifiListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mWifiListener != null) {
                mWifiListener.onWifiListChanged();
            }
        }
    };

    private final EthernetManager.Listener mEthernetListener = new EthernetManager.Listener() {
        @Override
        public void onAvailabilityChanged(boolean isAvailable) {
            mListener.onEthernetAvailabilityChanged(isAvailable);
        }
    };

    public static class ConnectivityStatus {
        public static final int NETWORK_NONE = 1;

        public static final int NETWORK_WIFI_OPEN = 3;

        public static final int NETWORK_WIFI_SECURE = 5;

        public static final int NETWORK_ETHERNET = 7;

        public static final int NETWORK_PPPOE = 9;

        public int mPPPoeNetworkType;

        // MStar Android Patch Begin
        public int mEthNetworkType;

        public int mWifiNetworkType;

        public String mWifiSsid;

        public int mWifiSignalStrength;

        public boolean isEthernetConnected() {
            return mEthNetworkType == NETWORK_ETHERNET;
        }

        public boolean isWifiConnected() {
            return mWifiNetworkType == NETWORK_WIFI_OPEN || mWifiNetworkType == NETWORK_WIFI_SECURE;
        }

        public boolean isPPPoeConnected() {
            return mPPPoeNetworkType == NETWORK_PPPOE;
        }

        public boolean isDisconnect() {
            if (mEthNetworkType == NETWORK_NONE && mWifiNetworkType == NETWORK_NONE
                    && mPPPoeNetworkType == NETWORK_NONE) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return new StringBuilder().append("mEthNetworkType ").append(mEthNetworkType)
                    .append("mWifiNetworkType ").append(mWifiNetworkType)
                    .append("mPPPoeNetworkType").append(mPPPoeNetworkType).append("  miWifiSsid ")
                    .append(mWifiSsid).append("  mWifiSignalStrength ").append(mWifiSignalStrength)
                    .toString();
        }
        // MStar Android Patch End
    }

    private final ConnectivityStatus mConnectivityStatus = new ConnectivityStatus();

    // MStar Android Patch Begin
    private final Handler mPPPoEHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == mPPPoEDialer.PPPOE_STATE_CONNECT) {
                mPPPoeStatusDescription = mContext.getString(R.string.pppoe_connected);
                mListener.onPPPoeChanged(mPPPoeStatusDescription);
            } else if (msg.what == mPPPoEDialer.PPPOE_STATE_DISCONNECT) {
                mPPPoeStatusDescription = mContext.getString(R.string.pppoe_disconnected);
                mListener.onPPPoeChanged(mPPPoeStatusDescription);
            } else if (msg.what == mPPPoEDialer.PPPOE_STATE_CONNECTING) {
                mPPPoeStatusDescription = mContext.getString(R.string.pppoe_dialing);
                mListener.onPPPoeChanged(mPPPoeStatusDescription);
            } else if (msg.what == mPPPoEDialer.PPPOE_STATE_AUTHFAILED) {
                mPPPoeStatusDescription = mContext.getString(R.string.pppoe_authfailed);
                mListener.onPPPoeChanged(mPPPoeStatusDescription);
            } else if (msg.what == mPPPoEDialer.PPPOE_STATE_FAILED) {
                mPPPoeStatusDescription = mContext.getString(R.string.pppoe_failed);
                mListener.onPPPoeChanged(mPPPoeStatusDescription);
            }
            super.handleMessage(msg);
        }
    };

    // MStar Android Patch End
    @SuppressLint("WrongConstant")
    public ConnectivityListener(Context context, Listener listener) {
        mContext = context;
        // mHandler = handler;
        mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mPPPoEDialer = new PPPoEDialer(mContext, mPPPoEHandler);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mEthernetManager = (EthernetManager) mContext.getSystemService(NetUtils.ETHERNET_SERVICE);//Context.ETHERNET_SERVICE);
        mListener = listener;
        mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mFilter.addAction("android.net.conn.INET_CONDITION_ACTION");//ConnectivityManager.INET_CONDITION_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        // wifi state change
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // pppoe state change
        // TODO: 2019-10-29 8386
//        mFilter.addAction(PppoeManager.PPPOE_STATE_ACTION);
//        mFilter.addAction(MWifiManager.WIFI_DEVICE_ADDED_ACTION);
//        mFilter.addAction(MWifiManager.WIFI_DEVICE_REMOVED_ACTION);
        // wifi ap
        mFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");//WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
        mFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        mFilter.addAction(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
        mFilter.addAction(ConnectivityManager.ACTION_TETHER_STATE_CHANGED);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // MStar Android Patch Begin
                if (DEBUG) {
                    Log.d(TAG,
                            "==========================Connectivity change! action="
                                    + intent.getAction());
                }
                updateEthernetStatus();
                updateWifiStatus();
                updatePPPoeStatus();
                mListener.onConnectivityChange(intent);
            }
        };
        checkNetworkConnectMode();
        // MStar Android Patch Begin
        // mPPPoEDialer = new PPPoEDialer(mContext, mHandler);
        // TODO: 2019-10-23 8386 star 
//        mPPPoeStatusDescription = mPPPoEDialer.isConnected() ? mContext
//                .getString(R.string.pppoe_connected) : mContext
//                .getString(R.string.pppoe_disconnected);
        // TODO: 2019-10-23 8386 end 
        // MStar Android Patch End

    }

    /**
     * Starts {@link ConnectivityListener}. This should be called only from main
     * thread.
     */
    public void start() {
        if (!mStarted) {
            mStarted = true;
            // MStar Android Patch Begin
            updateWifiStatus();
            updateEthernetStatus();
            // MStar Android Patch End
            mContext.registerReceiver(mReceiver, mFilter);
            mContext.registerReceiver(mWifiListReceiver, new IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            mEthernetManager.addListener(mEthernetListener);
            mPPPoEDialer.registerPPPoEReceiver();
        }
    }

    /**
     * Stops {@link ConnectivityListener}. This should be called only from main
     * thread.
     */
    public void stop() {
        if (mStarted) {
            mStarted = false;
            mContext.unregisterReceiver(mReceiver);
            mContext.unregisterReceiver(mWifiListReceiver);
            mEthernetManager.removeListener(mEthernetListener);
            mPPPoEDialer.exit();
            onExit();
        }
    }

    private void onExit() {
        mConfig = null;
        if (mScanRst != null) {
            mScanRst.clear();
            mScanRst = null;
        }
        mStarted = false;
        mWifiListener = null;
        mPPPoeStatusDescription = "";
        mNetStatus = -1;
        mErthernetLinkProperties = null;
        mWifiLinkProperties = null;
        mWifiInfo = null;
        isAutoDialer = false;
        isAutoip = true;
        mPPPoEHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Listener is notified when results are available via onWifiListChanged.
     */
    public void scanWifiAccessPoints(WifiNetworkListener callbackListener) {
        if (DEBUG)
            Log.d(TAG, "scanning for wifi access points");
        mWifiListener = callbackListener;
    }

    public ConnectivityStatus getConnectivityStatus() {
        return mConnectivityStatus;
    }

    public String getWifiIpAddress() {
        if (mConnectivityStatus.isWifiConnected()) {
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff),
                    (ip >> 24 & 0xff));
        } else {
            return "";
        }
    }

    /**
     * Return the MAC address of the currently connected Wifi AP.
     */
    public String getWifiMacAddress() {
        if (mConnectivityStatus.isWifiConnected()) {
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            return wifiInfo.getMacAddress();
        } else {
            return "";
        }
    }

    /**
     * Return whether Ethernet port is available.
     */
        public boolean isEthernetAvailable() {

        try {
            if (NetUtils.isNetworkSupported(mConnectivityManager,ConnectivityManager.TYPE_ETHERNET)) {
                L.e("mEthernetManager.isAvailable()"+mEthernetManager.isAvailable());
                return mEthernetManager.isAvailable();
            }
        } catch (Exception e) {
            L.d(e.toString());
        }

        return false;
    }

    public boolean isEthernetEnable() {
        return mEthernetManager.isEnabled();
    }

    public String getEthernetMacAddress() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || networkInfo.getType() != ConnectivityManager.TYPE_ETHERNET) {
            return "";
        } else {
            return networkInfo.getExtraInfo();
        }
    }

//    public String getEthernetIpAddress() {
//        @SuppressLint("MissingPermission") LinkProperties linkProperties = mConnectivityManager
//                .getLinkProperties(Network.fromNetworkHandle(ConnectivityManager.TYPE_ETHERNET));
//        // MStar Android Patch Begin
//
////        UserHandle.getUserHandle(UserHandle.ALL);
//        if (linkProperties != null) {
//            try {
//                List<LinkAddress> linkAddressList = NetUtils.getAllLinkAddresses(linkProperties);
//                for (LinkAddress linkAddress : linkAddressList) {
//                    InetAddress address = linkAddress.getAddress();
//                    if (address instanceof Inet4Address) {
//                        return address.getHostAddress();
//                    }
//                }
//            } catch (Exception e) {
//                L.d(e.toString());
//            }
//        }
//        // MStar Android Patch End
//
//        // IPv6 address will not be shown like WifiInfo internally does.
//        return "";
//    }

    public LinkProperties getEthernetLinkProperties() {
        try {
            LinkProperties linkProperties = NetUtils.getLinkProperties(mConnectivityManager,ConnectivityManager.TYPE_ETHERNET);
            if (linkProperties != null) {
                return linkProperties;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        LinkProperties linkProperties = mConnectivityManager
//                .getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
//        if (linkProperties != null) {
//            return linkProperties;
//        }
        return null;
    }

    public LinkProperties getWifiLinkProperties() {
        try {
            LinkProperties linkProperties = NetUtils.getLinkProperties(mConnectivityManager,ConnectivityManager.TYPE_WIFI);
            if (linkProperties != null) {
                return linkProperties;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        LinkProperties linkProperties = mConnectivityManager
//                .getLinkProperties(Network.fromNetworkHandle(ConnectivityManager.TYPE_WIFI));
        return null;
    }

    public WifiInfo getWifiInfo() {
        if (mConnectivityStatus.isWifiConnected()) {
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            return wifiInfo;
        } else {
            return null;
        }
    }

    public int getWifiSignalStrength(int maxLevel) {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), maxLevel);
    }

    public void forgetWifiNetwork() throws Exception {
        int networkId = getWifiNetworkId();
        if (networkId != -1) {
            NetUtils.forgetNull(mWifiManager,networkId);
//            mWifiManager.forget(networkId, null);
        }
    }

    public int getWifiNetworkId() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getNetworkId();
        } else {
            return -1;
        }
    }

    public WifiConfiguration getWifiConfiguration() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            int networkId = wifiInfo.getNetworkId();
            List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
            if (configuredNetworks != null) {
                for (WifiConfiguration configuredNetwork : configuredNetworks) {
                    if (configuredNetwork.networkId == networkId) {
                        return configuredNetwork;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return a list of wifi networks. Ensure that if a wifi network is
     * connected that it appears as the first item on the list.
     */
    public List<ScanResult> getAvailableNetworks() {
        if (DEBUG)
            Log.d(TAG, "getAvailableNetworks");

        ArrayList<ScanResult> networkList = null;
        WifiInfo connectedWifiInfo = mWifiManager.getConnectionInfo();
        String currentConnectedSSID = connectedWifiInfo == null ? "" : connectedWifiInfo.getSSID();
        try {
            currentConnectedSSID = NetUtils.removeDoubleQuotes(connectedWifiInfo,currentConnectedSSID);//WifiInfo.removeDoubleQuotes(currentConnectedSSID);
            WifiSecurity currentConnectedSecurity = WifiConfigHelper.getCurrentConnectionSecurity(
                    mWifiManager, connectedWifiInfo);

            // TODO : Refactor with similar code in SelectFromListWizard
            final List<ScanResult> results = mWifiManager.getScanResults();

            // if (results.size() == 0) {
            // Log.w(TAG, "No results found! Initiate scan...");
            mWifiManager.startScan();
            // }//refress wifi list on time.

            final HashMap<Pair<String, WifiSecurity>, ScanResult> consolidatedScanResults = new HashMap<Pair<String, WifiSecurity>, ScanResult>();
            HashMap<Pair<String, WifiSecurity>, Boolean> specialNetworks = new HashMap<Pair<String, WifiSecurity>, Boolean>();
            for (ScanResult result : results) {
                if (TextUtils.isEmpty(result.SSID)) {
                    continue;
                }

                Pair<String, WifiSecurity> key = Pair.create(result.SSID,
                        WifiSecurity.getSecurity(result));
                ScanResult existing = consolidatedScanResults.get(key);

                if (WifiConfigHelper.areSameNetwork(mWifiManager, result, connectedWifiInfo)) {
                    // The currently connected network should always be included.
                    consolidatedScanResults.put(key, result);
                    specialNetworks.put(key, true);
                } else {
                    if (existing == null
                            || (!specialNetworks.containsKey(key) && existing.level < result.level)) {
                        consolidatedScanResults.put(key, result);
                    }
                }
            }

            networkList = new ArrayList<>(
                    consolidatedScanResults.size());
            networkList.addAll(consolidatedScanResults.values());
            ScanResultComparator comparator = connectedWifiInfo == null ? new ScanResultComparator()
                    : new ScanResultComparator(currentConnectedSSID, currentConnectedSecurity);
            Collections.sort(networkList, comparator);
            return networkList;
        } catch (Exception e) {
            L.d(e.toString());
            return null;
        }
    }

    public IpConfiguration getIpConfiguration() {
        return mEthernetManager.getConfiguration();
    }

    private boolean isSecureWifi(WifiInfo wifiInfo) {
        if (wifiInfo == null)
            return false;
        int networkId = wifiInfo.getNetworkId();
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration configuredNetwork : configuredNetworks) {
                if (configuredNetwork.networkId == networkId) {
                    return configuredNetwork.allowedKeyManagement.get(KeyMgmt.WPA_PSK)
                            || configuredNetwork.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
                            || configuredNetwork.allowedKeyManagement.get(KeyMgmt.IEEE8021X);
                }
            }
        }
        return false;
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public void setWifiEnabled(boolean enable) {
        mWifiManager.setWifiEnabled(enable);
    }

    // MStar Android Patch Begin
    private boolean setEthernetNetworkType(int networkType) {
        boolean hasChanged = mConnectivityStatus.mEthNetworkType != networkType;
        mConnectivityStatus.mEthNetworkType = networkType;
        return hasChanged;
    }

    private boolean setWifiNetworkType(int networkType) {
        boolean hasChanged = mConnectivityStatus.mWifiNetworkType != networkType;
        mConnectivityStatus.mWifiNetworkType = networkType;
        return hasChanged;
    }

    private boolean setPPPoeNetworkType(int networkType) {
        boolean hasChanged = mConnectivityStatus.mPPPoeNetworkType != networkType;
        mConnectivityStatus.mPPPoeNetworkType = networkType;
        return hasChanged;
    }

    private boolean updateWifiStatus() {
        NetworkInfo networkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (DEBUG) {
            Log.d(TAG, "updateWifiStatus wifi networkInfo=" + networkInfo);
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            boolean hasChanged = false;
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

            // Determine if this is an open or secure wifi connection.
            if (isSecureWifi(wifiInfo)) {
                hasChanged = setWifiNetworkType(ConnectivityStatus.NETWORK_WIFI_SECURE);
            } else {
                hasChanged = setWifiNetworkType(ConnectivityStatus.NETWORK_WIFI_OPEN);
            }

            // Find the SSID of network.
            String ssid = null;
            if (wifiInfo != null) {
                ssid = wifiInfo.getSSID();
                if (ssid == null || ssid.equals(WifiSsid.NONE)) {
                    setWifiNetworkType(ConnectivityStatus.NETWORK_NONE);
                    return false;
                } else {
                    try {
                        ssid = NetUtils.removeDoubleQuotes(wifiInfo,ssid);
                        hasChanged = true;
                    } catch (Exception e) {
                        L.d(e.toString());
                    }
//                    ssid = WifiInfo.removeDoubleQuotes(ssid);
                }
            }

            if (!TextUtils.equals(mConnectivityStatus.mWifiSsid, ssid)) {
                hasChanged = true;
                mConnectivityStatus.mWifiSsid = ssid;
            }

            // Calculate the signal strength.
            int signalStrength;
            if (wifiInfo != null) {
                // Calculate the signal strength between 0 and 3.
                signalStrength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4);
            } else {
                signalStrength = 0;
            }
            if (mConnectivityStatus.mWifiSignalStrength != signalStrength) {
                hasChanged = true;
                mConnectivityStatus.mWifiSignalStrength = signalStrength;
            }
            return hasChanged;
        } else {
            return setWifiNetworkType(ConnectivityStatus.NETWORK_NONE);
        }
    }

    private boolean updateEthernetStatus() {
        NetworkInfo networkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (DEBUG) {
            Log.d(TAG, "updateEthernetStatus Ethernet networkInfo=" + networkInfo);
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            return setEthernetNetworkType(ConnectivityStatus.NETWORK_ETHERNET);
        } else {
            return setEthernetNetworkType(ConnectivityStatus.NETWORK_NONE);
        }
    }

    // MStar Android Patch End

    private boolean updatePPPoeStatus() {
        if (mPPPoEDialer.isConnected()) {
            return setPPPoeNetworkType(ConnectivityStatus.NETWORK_PPPOE);
        } else {
            return setPPPoeNetworkType(ConnectivityStatus.NETWORK_NONE);
        }

    }

    public PPPoEDialer getPPPoEDialer() {
        return mPPPoEDialer;
    }

    public boolean hasProxy() {
        return mHasProxySettigns;
    }

    public EthernetManager getEthernetManager() {
        return mEthernetManager;
    }

    public WifiManager getWifiManager() {
        return mWifiManager;
    }

    public String getPPPoeStatusDescription() {
        return mPPPoeStatusDescription;
    }

    private void checkNetworkConnectMode() {
        String mode = SystemProperties.get("persist.sys.networkstyle");
        Log.i(TAG, "============ checkNetworkConnectMode mode=" + mode);
        if (mode.equals("0")) {
            // ANDORID 6.0 default mode :WIFI off,Ethernet on;
            mWifiManager.setWifiEnabled(false);
            mEthernetManager.setEnabled(true);
            SystemProperties.set("persist.sys.networkstyle", "3");
        } else if (mode.equals("1")) {
            // WIFI on,Ethernet on;
            mWifiManager.setWifiEnabled(true);
            mEthernetManager.setEnabled(true);
            SystemProperties.set("persist.sys.networkstyle", "3");
        } else if (mode.equals("2")) {
            // WIFI on,Ethernet off;
            mWifiManager.setWifiEnabled(true);
            mEthernetManager.setEnabled(false);
            SystemProperties.set("persist.sys.networkstyle", "3");
        }
    }

    public void setNetstateViewData() {
        if (getConnectivityStatus().isWifiConnected()) {
            mNetStatus = ConnectivityManager.TYPE_WIFI;
        } else if (getConnectivityStatus().isPPPoeConnected()) {
            mNetStatus = TYPE_PPPOE;
        } else if (getConnectivityStatus().isEthernetConnected()) {
            mNetStatus = ConnectivityManager.TYPE_ETHERNET;
        } else {
            mNetStatus = NetUtils.TYPE_NONE;
        }
        switch (mNetStatus) {
            case TYPE_PPPOE:
                break;
            case ConnectivityManager.TYPE_ETHERNET:
                mErthernetLinkProperties = getEthernetLinkProperties();
                break;
            case ConnectivityManager.TYPE_WIFI:
                mWifiInfo = getWifiInfo();
                if (null != mWifiInfo && null != mWifiInfo.getSSID()
                        && mWifiInfo.getNetworkId() != NetUtils.INVALID_NETWORK_ID) {
                    mWifiLinkProperties = getWifiLinkProperties();
                }
                break;
            case NetUtils.TYPE_NONE:
                break;
            default:
                break;
        }
    }

    private void setPppoeViewData(Context context) {
        isAutoDialer = Tools.getBooleanPref(context, NetUtils.PPPOE_IS_AUTO_DIALER, false);
    }

    private void setWifiHotspotViewData() {
//        mConfig = mWifiManager.getWifiApConfiguration();
        mConfig = NetUtils.getWifiApConfiguration(mWifiManager);
    }

    private void setWirelessViewData() {
        mWifiManager.startScan();
        mScanRst = mWifiManager.getScanResults();
    }

    private void setWireViewData() {
        isAutoip = mEthernetManager.getConfiguration().getIpAssignment().equals(IpAssignment.DHCP) ? true
                : false;
    }

    public InitDataInfo initViewNetworkData(Context context) {
        setNetstateViewData();
        setPppoeViewData(context);
        setWifiHotspotViewData();
        setWirelessViewData();
        setWireViewData();
        InitDataInfo initDataInfo = new InitDataInfo(mConfig, mScanRst, mWifiLinkProperties,
                mWifiInfo, mNetStatus, isAutoip, mErthernetLinkProperties, isAutoDialer);
        return initDataInfo;
    }
}
