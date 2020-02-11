// IHHTBoardInfo.aidl
package com.hht.android.sdk.boardInfo;

// Declare any non-default types here with import statements

interface IHHTBoardInfo {
    String	getAndroidOsVersion();
    String	getApplicationActivationCode(String packageName);
    String	getBoardMacAddr();
    String	getBoardModel();
    String	getBoardSerial();
    String	getChipModel();
    String	getFirmwareVersion();
    String	getManufacturer();
    int	getMemoryAvailableSize();
    int	getMemoryTotalSize();
    String	getProductModel();
    String	getProductSerial();
    int	getScreenSize();
    long getSystemAvailableStorage();
    long getSystemTotalStorage();
    String	getTouchPanelVersion();
    String	getUpdateSystemPatchVersion();

}
