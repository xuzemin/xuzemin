// IHHTDevice.aidl
package com.hht.android.sdk.device;

// Declare any non-default types here with import statements

interface IHHTDevice {
  Map getBrightnessMap();
  String getBrightnessMode();
  int getBrightnessValue();
  String getEnvironment(String strName);
  int getLightSensorValue();
  int	getTempSensorValue();
  boolean	getUartOnOff();
  boolean	isBacklightOff();
  String	readCmdStrToTVOS(int iCmd);
  boolean	setBacklightOff(boolean bOnOff);
  boolean	setBrightnessMode(String strMode);
  boolean	setBrightnessValue(int iVal);
//  boolean	setEnvironment(String strName, String strVal);
  boolean	setUartOnOff(boolean bIsEnable);
  boolean	writeCmdStrToTVOS(int iCmd,String strData);
  boolean setBrightnessValueForThirdPartyApp(int iVal);
  int getBrightnessValueForThirdPartyApp();
}
