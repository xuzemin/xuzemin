package com.hht.android.sdk;

/*
*  版本控制管理类
*
* */

/**
 * HHTVersionControlManager 是版本控制管理类。
 * 1、Version: HHTMwApi V1.0.0
 * 2、初步定义鸿合中间件架构
 * 3、分10类 Audio、BoardInfo、Customer、Device、Network、Picture、Source、System、Time、Video
 * 4、API有重复，下一版继续整理
 * 5、Author：鸿合中间件标准化小组
 * 1、V1.0.2
 * 2、新细分Led、Touch、Lock类
 * 3、检查API字母大小写按规定命名
 * 4、鸿合缩写统一为HHT
 * 5、检查部分API返回值说明
 * 6、Author：鸿合中间件标准化小组
 * 1、V2.0.0
 * 2、重新调整API分类
 * 3、完善API说明
 * 4、Author：鸿合中间件标准化小组
 * 1、V2.0.2
 * 2、新增区域触控API
 * 3、Author：鸿合中间件标准化小组
 * 1、V2.0.3
 * 2、新增controlPcTouchRect(String strPackage, String strWinTitle, rect touchRect, boolean bEnable)
 * 3、新增setBrightnessValueForThirdPartyApp (int iVal)
 * 4、新增startScreenShot()
 * 5、调整声音模式 图像模式 枚举定义
 * 6、Author：鸿合中间件标准化小组
 * 1、V2.0.4
 * 2、规范化：HHTCommonManager: setSleepModeEnable() 修改改为 getSleepModeEnable()；setSleepModeTime()修改改为setSleepModeTime()。
 * 3、规范化：HHTSystemManager：boolean startScreenShot() 修改改为 Bitmap screenshot(int width, int height)。
 * 4、规范化：HHTNetworkManager： public void setEthernetMode(String mode, DhcpInfo dhcpInfo) 修改为 setEthernetMode(String mode)。
 * 5、规范化：HHTSystemManager： installAPKPackage() 修改改为 installApkPackage()。
 * 6、删掉 HHTSourceManager: enableFreeze() 和 disableFreeze()。
 * 7、删掉 HHTAudioManager: boolean setEarPhoneVolume(in int volume, in boolean saveDb)。
 * 1、V2.0.5
 * 2、删掉 HHTSourceManager: getSourcePlugStateByKey()。
 * 3、删掉 HHTDeviceManager: boolean setEnvironment(String strName, String strVal)。
 * 4、规范化： HHTSystemManager： boolean startScreenShot() 修改改为 Bitmap screenshot(int width, int height)。
 * 5、删掉 HHTAudioManager: boolean setSystemVolumeMode(String strMode)；int getSystemVolumeMode()。
 * 6、增加接口： HHTAudioManager: boolean setSourceVolumeMute(boolean enable)；boolean getSourceVolumeMute()。
 * 7、删掉 HHTTimeManager: boolean setRtcTime(long lMillitime, String strTimezone)。
 * 8、规范化： HHTTimeManager:boolean setScheduleTimeForBoot(TimeUtil time)；TimeUtil getScheduleTimeForBoot()；
 * boolean setScheduleTimeForShutdown(TimeUtil time)；TimeUtil getScheduleTimeForShutdown()
 * 1、V2.0.6
 * 2、删掉 HHTCommonManager: boolean setScreenSaverEnable(in boolean iVal);
 * boolean getScreenSaverEnable();
 * int getScreenSaverTime();
 * boolean setScreenSaverTime(in int iVal);
 * boolean startScreenSaver();
 * 3、增加接口: HHTBoardInfoManager: int getSdkVersion();
 * 4、规范化： TimeUtil： public String week; 修改改为 public int week;
 * 1、V2.0.7
 * 1、增加接口: HHTCommonManager: int getUsbChannelMode(); void setUsbChannelMode(int mode);
 * void setAutoWakeupBySourceEnable(boolean enable);boolean isAutoWakeupBySourceEnable();
 * void setStandbyMode(int mode); int getStandbyMode();
 * void setBlackBoardEnable(boolean enable); boolean isBlackBoardEnabled();
 * void setBlackBoardTime(int value); int getBlackBoardTime();
 *
 */

public class HHTVersionControlManager {
    public static HHTVersionControlManager	getInstance(){
        return new HHTVersionControlManager();
    }
}
