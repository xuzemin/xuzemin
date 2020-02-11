package com.hht.android.sdk.boardInfo;

/**
 * 各种常量的定义。
 *
 * @time 2019/12/02
 */
public interface HHTConstant {
    boolean DEBUG = true; // 调试模式

    // sourceInput name key
    // 信号源
    String ANDROID = "ANDROID";
    String ATV = "ATV";
    String AV1 = "AV1";
    String AV2 = "AV2";
    String DP = "DP";
    String DP2 = "DP2";
    String DTV = "DTV";
    String HDMI1 = "HDMI1";
    String HDMI2 = "HDMI2";
    String HDMI3 = "HDMI3";
    String HDMI4 = "HDMI4";
    String HDMI5 = "HDMI5";
    String HDMI6 = "HDMI6";
    String OPS = "OPS";
    String OPS2 = "OPS2";
    String STORAGE = "STORAGE";
    String TYPEC = "TYPEC";
    String TYPEC2 = "TYPEC2";
    String VGA1 = "VGA1";
    String VGA2 = "VGA2";
    String VGA3 = "VGA3";
    String YPBPR1 = "YPBPR1";
    String YPBPR2 = "YPBPR2";
    // sourceInput name key end

    // source
    String SOURCE_LAST = "SOURCE_LAST"; // 最近打开的信号源
    String SOURCE_RECENT = "SOURCE_RECENT"; // 当前的信号源
    String SOURCE_CURRENT = "SOURCE_CURRENT"; // 当前的信号源
    String SOURCE_PRESET = "SOURCE_PRESET"; // 预先设置的信号源


    String SOURCE_DETECTION_MODE = "auto_enable";//"SOURCE_DETECTION_MODE"; // 信号检测模式 // auto_enable
    String BOOT_SOURCE_ENABLE = "BOOT_SOURCE_ENABLE"; // 开机信号源模式使能开关
    String BOOT_SOURCE_MODE = "BOOT_SOURCE_MODE"; // 开机信号源模式

    // 信号侦测
    String SOURCE_DET_IN = "IN";
    String SOURCE_DET_OUT = "OUT";
    String SOURCE_DET_OFF = "off";

    // goio TIPORT
    String SetTIPORT_0 = "SetTIPORT-0";// 前置HDMI
    String SetTIPORT_2 = "SetTIPORT-2";// HDMI2
    String SetTIPORT_3 = "SetTIPORT-3";// DP

    // 触摸开关
    String SetUSBTOUCH_ON = "SetUSBTOUCH_ON";// 开启USB触摸
    String SetUSBTOUCH_OFF = "SetUSBTOUCH_OFF";// 关闭USB触摸

    String TVOS_COMMON_CMD_VGA_SWITCH_PIN_LOW = "SetVGA_SWITCH_Low"; // Prev VGA
    String TVOS_COMMON_CMD_VGA_SWITCH_PIN_HIGH = "SetVGA_SWITCH_High";// VGA

    String SOURCE_INFO = "source_info";
    String VGA_INFO = "vga_info";

    // CMD
    // HDMI1/PREHDMI/DP  set TIport
    String TVOS_INTERFACE_CMD_GET_TIPORT = "GetTIPORT";
    // VGA/PREVGA
    String TVOS_INTERFACE_CMD_GET_VGA_SWITCH_PIN_STATUS = "Get_VGA_SWITCH_STATUS";

    // Lock status
    String LOCK_STATUS_ON = "on"; // value:"on" , "off"
    String LOCK_STATUS_OFF = "off"; // value:"on" , "off"

    // usb key lock
    String LOCK_USB = "persist.sys.usbLock"; // usb lock, value:"on" , "off"
    String LOCK_USB_KEY_ENABLE = "persist.sys.usbLock.status"; // usb lock enable, value:true , false

    // touch lock
    String LOCK_TOUCH = "persist.sys.touch_enable"; // lock touch, value:unlock is "1", lock is "0"
    String LOCK_TOUCH_STATUS_ON = "0"; // lock touch, value:unlock is "1", lock is "0"
    String LOCK_TOUCH_STATUS_OFF = "1"; // lock touch, value:unlock is "1", lock is "0"

    // screen lock
    String LOCK_SCREEN = "persist.sys.lockScreen"; // lock Screen, value:"on" , "off"
    String LOCK_SCREEN_ENABLE = LOCK_SCREEN; //"persist.sys.lockScreen.status"; // lock Screen enable, value:"on" , "off"

    String LOCK_SCREEN_PWD = "lock-screen"; // lock screen password

    // keypad lock
    String LOCK_KEYPAD = "persist.sys.keypadlock"; // lock Keypad, value:"on" , "off"
    // IR lock
    String LOCK_IR = "persist.sys.keyremotelock"; // lock IR, value:"on" , "off"

    String IN_TV_WINDOW_STATUS = "IN_TV_WINDOW_STATUS"; // in tv window


    String TV_PLAY_PACKAGE = "com.mstar.tv.tvplayer.ui"; // TV Play包名

    // RTC time
    String TVOS_COMMON_CMD_SET_RTCTIME = "SetRTCTime"; //
    String TVOS_COMMON_CMD_GET_RTCTIME = "GetRTCTime";

    String SHARE_USB_CHANGE = "persist.sys.share_usb_mode"; // share usb mode 0:share, 1:ony Android
}
