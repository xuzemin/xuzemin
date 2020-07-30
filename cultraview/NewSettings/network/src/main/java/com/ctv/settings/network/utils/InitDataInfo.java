
package com.ctv.settings.network.utils;

import java.io.Serializable;
import java.util.List;

import android.net.LinkProperties;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;

public class InitDataInfo implements Serializable {
    private WifiConfiguration mConfig;

    private List<ScanResult> mScanRst;

    private LinkProperties mWifiLinkProperties;

    private WifiInfo mWifiInfo;

    private int mNetStatus;

    private boolean isAutoip;

    private LinkProperties mErthernetLinkProperties;

    private boolean isAutoDialer;

    /**
     * @param mConfig
     * @param mScanRst
     * @param mWifiLinkProperties
     * @param mWifiInfo
     * @param mNetStatus
     * @param isAutoip
     * @param mDevInfo
     * @param isAutoDialer
     */
    public InitDataInfo(WifiConfiguration config, List<ScanResult> scanRst,
            LinkProperties wifiLinkProperties, WifiInfo wifiInfo, int netStatus, boolean isAutoip,
            LinkProperties erthernetLinkProperties, boolean isAutoDialer) {
        super();
        this.mConfig = config;
        this.mScanRst = scanRst;
        this.mWifiLinkProperties = wifiLinkProperties;
        this.mWifiInfo = wifiInfo;
        this.mNetStatus = netStatus;
        this.isAutoip = isAutoip;
        this.mErthernetLinkProperties = erthernetLinkProperties;
        this.isAutoDialer = isAutoDialer;
    }

    public WifiConfiguration getConfig() {
        return mConfig;
    }

    public void setConfig(WifiConfiguration mConfig) {
        this.mConfig = mConfig;
    }

    public List<ScanResult> getScanRst() {
        return mScanRst;
    }

    public void setScanRst(List<ScanResult> scanRst) {
        this.mScanRst = scanRst;
    }

    public LinkProperties getWifiLinkProperties() {
        return mWifiLinkProperties;
    }

    public void setWifiLinkProperties(LinkProperties wifiLinkProperties) {
        this.mWifiLinkProperties = wifiLinkProperties;
    }

    public WifiInfo getWifiInfo() {
        return mWifiInfo;
    }

    public void setWifiInfo(WifiInfo wifiInfo) {
        this.mWifiInfo = wifiInfo;
    }

    public int getNetStatus() {
        return mNetStatus;
    }

    public void setNetStatus(int netStatus) {
        this.mNetStatus = netStatus;
    }

    public boolean isAutoip() {
        return isAutoip;
    }

    public void setAutoip(boolean isAutoip) {
        this.isAutoip = isAutoip;
    }

    public LinkProperties getErthernetLinkProperties() {
        return mErthernetLinkProperties;
    }

    public void setmErthernetLinkProperties(LinkProperties erthernetLinkProperties) {
        this.mErthernetLinkProperties = erthernetLinkProperties;
    }

    public boolean isAutoDialer() {
        return isAutoDialer;
    }

    public void setAutoDialer(boolean isAutoDialer) {
        this.isAutoDialer = isAutoDialer;
    }

}
