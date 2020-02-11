// IHHTNetwork.aidl
package com.hht.android.sdk.network;

// Declare any non-default types here with import statements

interface IHHTNetwork {
//    boolean	getWifiApStatus();
//    boolean	getWifiStatus();
    boolean	getWolEn();
//    int	setWifiApStatus(boolean bEnable);
//    int	setWifiStatus(boolean bEnable);
    boolean	setWolEn(boolean bEnable);
    boolean setEthernetEnabled(boolean bEnable);
    String getEthernetMode();
    int getEthernetState();
    void setEthernetMode(String mode);
    String getEthernetMacAddress();
    String getEthernetIpAddress();

}
