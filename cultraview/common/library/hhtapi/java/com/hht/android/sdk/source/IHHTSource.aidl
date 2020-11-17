// IHHTSource.aidl
package com.hht.android.sdk.source;

// Declare any non-default types here with import statements

interface IHHTSource {
//   boolean	disableFreeze();
//   boolean	enableFreeze();
   boolean	getBootSourceEnable();
   String	getBootSourceMode();
   String	getCurSource();
   Map	getInputSrcMap();
   Map	getInputSrcPlugStateMap();
   String	getLastSource();
   String	getPreSource();
   String	getRecentTvSource();
   int	getSourceDetectionMode();
   String	getSourcePlugStateByKey(String sourceKey);
   boolean	isSignalLock();
   boolean	isTvWindow();
   boolean	setBootSourceEnable(boolean bStatus);
   boolean	setBootSourceMode(String mode);
   boolean	setCurSource(String key);
   boolean	setInputSrcCustomerNameByKey(String sourceKey,String sourceName);
   boolean	setLastSource(String key);
   boolean	setPreSource(String key);
   boolean	setRecentTvSource(String key);
   boolean	setSignalLock(boolean bStatus);
   boolean	setSourceDetectionMode(int iMode);
   boolean	startSourcebyKey(String sourceKey);
//   boolean isInOps();
//   boolean isInHDMI1();
   boolean isCurrentSource(String sourceName);
}
