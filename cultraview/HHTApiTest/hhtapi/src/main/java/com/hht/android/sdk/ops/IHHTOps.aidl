// IHHTOps.aidl
package com.hht.android.sdk.ops;

// Declare any non-default types here with import statements

interface IHHTOps {
    String	getOpsCpuModel();
    int	getOpsCpuTemperature();
    String	getOpsCpuUseRate();
    String	getOpsDNS();
    int	getOpsHardDiskSize();
    String	getOpsHarddiskUseRate();
    String	getOpsImei();
    String	getOpsIP();
    String	getOpsMAC();
    String	getOpsMainboardModel();
    int	getOpsMemorySize();
    String	getOpsMemoryUseRate();
    boolean	getOpsStartEnable();
    String	getOpsStartMode();
    int	getOpsSystem();
    boolean	isOpsOk();
    boolean	isOpsPlugIn();
    int	setOpsCpuModel(String strStr);
    int	setOpsCpuTemperature(String strStr);
    int	setOpsCpuUseRate(String strUseRate);
    int	setOpsDNS(String strStr);
    int	setOpsHardDiskInfo(String strStr);
    int	setOpsHardDiskSize(int iSize);
    int	setOpsHarddiskUseRate(String strUseRate);
    int	setOpsImei(String strStr);
    int	setOpsMAC(String strStr);
    int	setOpsMainboardModel(String strStr);
    int	setOpsMemorySize(int iSize);
    int	setOpsMemoryUseRate(String strUseRate);
    boolean	setOpsPower();
    boolean	setOpsPowerLongPress();
    boolean	setOpsPowerTurnOff();
    boolean	setOpsPowerTurnOn();
    boolean	setOpsStartEnable(boolean bStatus);
    boolean	setOpsStartMode(String strName);
    int	setOpsSystem(int iType);
    int	setOpsVisiable(boolean bIsVisiable);
    String getOpsOs();
    int getOpsMemoryTotalSize();
    int getOpsMemoryAvailableSize();
    int getOpsHardDiskTotalSize();
    int getOpsHardDiskAvailableSize();

}
