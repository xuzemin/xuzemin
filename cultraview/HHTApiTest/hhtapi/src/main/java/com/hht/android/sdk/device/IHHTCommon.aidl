// IHHTCommon.aidl
package com.hht.android.sdk.device;

// Declare any non-default types here with import statements

interface IHHTCommon {
    int	getCurHdmiTxMode();
    int getSleepModeTime();
    int	getEyeProtectionMode();
    boolean	getNoEventEnable();
    int	getNoEventTime();
    boolean	getNoSignalEnable();
    int	getNoSignalTime();
    boolean	getScreenSaverEnable();
    int	getScreenSaverTime();
    boolean	getShowTempEnable();
    int	getShowTempMode();
    boolean	isNoAndroidStatus();
    boolean	isSupportHDMITx();
    boolean	isSystemSleep();
    boolean	isSystemVitrualStandby();
    boolean	setEyeProtectionMode(int mode);
    boolean	setHdmiTxMode(int iMode);
    boolean	setNoAndroidStatus(boolean bType);
    boolean	setNoEventEnable(boolean bStatus);
    boolean	setNoEventTime(int iTime);
    boolean	setNoSignalEnable(boolean bStatus);
    boolean	setNoSignalTime(int iTime);
    boolean	setScreenSaverEnable(boolean iVal);
    boolean	setScreenSaverTime(int iVal);
    boolean	setShowTempEnable(boolean bStatus);
    boolean	setShowTempMode(int iMode);
    boolean	getSleepModeEnable();
    boolean setSleepModeEnable(boolean bStatus);
    boolean	setSleepModeTime(int iTime);
    boolean	setSystemVitrualStandby(boolean bMode);
    boolean	startScreenSaver();
    boolean	startSystemSleep(boolean bMode);
    List getSupportHDMITxList();


}
