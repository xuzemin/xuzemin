// IHHTCommon.aidl
package com.hht.android.sdk.device;

// Declare any non-default types here with import statements

interface IHHTCommon {
    int	getCurHdmiTxMode();
    int getStandbyMode();
    int getSleepModeTime();
    int	getEyeProtectionMode();
    boolean	getNoEventEnable();
    int	getNoEventTime();
    boolean	getNoSignalEnable();
    int	getNoSignalTime();
//    boolean	getScreenSaverEnable();
//    int	getScreenSaverTime();
    boolean	getShowTempEnable();
    int	getShowTempMode();
    boolean	isNoAndroidStatus();
    boolean	isSupportHDMITx();
    boolean	isSystemSleep();
    boolean	isSystemVitrualStandby();
    boolean	setEyeProtectionMode(int mode);
    boolean	setHdmiTxMode(int iMode);
    boolean setStandbyMode(int standby_mode);
    boolean	setNoAndroidStatus(boolean bType);
    boolean	setNoEventEnable(boolean bStatus);
    boolean	setNoEventTime(int iTime);
    boolean	setNoSignalEnable(boolean bStatus);
    boolean	setNoSignalTime(int iTime);
//    boolean	setScreenSaverEnable(boolean iVal);
//    boolean	setScreenSaverTime(int iVal);
    boolean	setShowTempEnable(boolean bStatus);
    boolean	setShowTempMode(int iMode);
    boolean	getSleepModeEnable();
    boolean setSleepModeEnable(boolean bStatus);
    boolean	setSleepModeTime(int iTime);
    boolean	setSystemVitrualStandby(boolean bMode);
//    boolean	startScreenSaver();
    boolean	startSystemSleep(boolean bMode);
    List getSupportHDMITxList();

    int getUsbChannelMode();
    void setUsbChannelMode(int mode);
    void setAutoWakeupBySourceEnable(boolean enable);
    boolean isAutoWakeupBySourceEnable();
    boolean standby();

//    void setLightControl(boolean enable);
//
//    boolean isLigthControl();
    void setBlackBoardEnable(boolean enable);
    boolean isBlackBoardEnabled();
    void setBlackBoardTime(int value);
    int getBlackBoardTime();

    void setScreenTmpThreshold(int tmp);
    int getScreenTmpThreshold();

    boolean isTouchFrameNormal();
    boolean isLightSensorNormal();

    boolean is4K2KMode();// 0413
}
