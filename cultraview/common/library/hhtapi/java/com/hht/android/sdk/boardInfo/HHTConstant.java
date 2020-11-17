package com.hht.android.sdk.boardInfo;

/**
 * 各种常量的定义。
 *
 * @time 2019/12/02
 */
public interface HHTConstant {
    boolean DEBUG = true; // 调试模式
	int DEFAULT_MODE_BALANCE = 50;
    // sourceInput name key
    // 信号源
    String ANDROID = "ANDROID";
    String ATV = "ATV";
    String AV = "AV";
    String AV1 = "AV1";
    String AV2 = "AV2";
    String DP = "DP";
    String DP2 = "DP2";
    String DTV = "DTV";
    String HDMI1 = "HDMI1";
    String HDMI2 = "HDMI2";
    String HDMI3 = "HDMI3";
    String FRONT_HDMI = "FRONT_HDMI";
    String HDMI4 = "HDMI4";
    String HDMI5 = "HDMI5";
    String HDMI6 = "HDMI6";
    String OPS = "OPS";
    String OPS2 = "OPS2";
    String STORAGE = "STORAGE";
    String TYPEC = "TYPEC";
    String TYPEC2 = "TYPEC2";
    String VGA = "VGA";
    String VGA1 = "VGA1";
    String VGA2 = "VGA2";
    String VGA3 = "VGA3";
    String YPBPR = "YPBPR";
    String YPBPR1 = "YPBPR1";
    String YPBPR2 = "YPBPR2";
    // sourceInput name key end

    // source
    String SOURCE_LAST = "SOURCE_LAST"; // 最近打开的信号源
    String SOURCE_RECENT = "SOURCE_RECENT"; // 当前的信号源
    String SOURCE_CURRENT = "SOURCE_CURRENT"; // 当前的信号源
    String SOURCE_PRESET = "SOURCE_PRESET"; // 预先设置的信号源
    String BOOT_SOURCE = "persist.sys.boot.source";

    String SOURCE_DETECTION_MODE = "auto_enable";//"SOURCE_DETECTION_MODE"; // 信号检测模式 // auto_enable
    String BOOT_SOURCE_ENABLE = "BOOT_SOURCE_ENABLE"; // 开机信号源模式使能开关
    String BOOT_SOURCE_MODE = "persist.sys.boot.sourcemode"; // 开机信号源模式

    // 信号侦测
    String SOURCE_DET_IN = "IN";
    String SOURCE_DET_OUT = "OUT";
    String SOURCE_DET_OFF = "off";

    // HMDI1/HDMI2/OPS 信号端口侦测:
    int DET_PORT_HDMI1 = 0;
    int DET_PORT_HDMI2 = 2;
    int DET_PORT_OPS = 3;

    // 信号插拔状态
    int PLUG_STATUS_IN = 1; // in
    int PLUG_STATUS_OUT = 0; // out

    String SOURCE_INFO = "source_info";// HDMI1/HDMI2/OPS, 共用source switch info
    String VGA_INFO = "vga_info";// vga switch info

    // Lock status
    String LOCK_STATUS_ON = "on"; // value:"on" , "off"
    String LOCK_STATUS_OFF = "off"; // value:"on" , "off"

    //board
    String PRODUCT_BOARD = "ro.product.board";
    String PRODUCT_BOARD_MODEL = "ro.product.board.model";

    //standby
    String PRODUCT_STANDBY_STATE = "ctv.standby_state";
    // usb key lock
    String LOCK_USB = "persist.sys.usbLock"; // usb lock, value:"on" , "off"
    String LOCK_USB_KEY_ENABLE = "persist.sys.usbLock.status"; // usb lock enable, value:true , false

    // touch lock
    String LOCK_TOUCH = "persist.sys.touch_enable"; // lock touch, value:unlock is "1", lock is "0"
    String LOCK_TOUCH_STATUS_ON = "0"; // lock touch, value:unlock is "1", lock is "0"
    String LOCK_TOUCH_STATUS_OFF = "1"; // lock touch, value:unlock is "1", lock is "0"

    String PANEL_VERSION_INCREMENT = "persist.panel.version.increment";
    String BUILD_VERSION_INCREMENT = "ro.build.version.incremental";
    String BUILD_DISPLAY_ID = "ro.build.display.id";

    String BUILD_DATA_UTC = "ro.build.date.utc";
    // screen lock
    String LOCK_SCREEN = "persist.sys.lockScreen"; // lock Screen, value:"on" , "off"
    String LOCK_SCREEN_ENABLE = LOCK_SCREEN; //"persist.sys.lockScreen.status"; // lock Screen enable, value:"on" , "off"

    String LOCK_SCREEN_PWD = "lock-screen"; // lock screen password

    // keypad lock
    String LOCK_KEYPAD = "persist.sys.keypadlock"; // lock Keypad, value:"on" , "off"
    // IR lock
    String LOCK_IR = "persist.sys.keyremotelock"; // lock IR, value:"on" , "off"

    String TV_PLAY_PACKAGE = "com.mstar.tv.tvplayer.ui"; // TV Play包名

    String SHARE_USB_CHANGE = "persist.sys.share_usb_mode"; // share usb mode 0:share, 1:ony Android, 2:only ops

    String SOURCE_AUTO_START = "persist.sys.source_auto_start"; // Source Auto Start ,value:"on" is enable,"off"is not enable

    String STANDBY_MODE = "persist.sys.standby_mode"; // standby mode 0:general, 1:partial , 2:STR
    String BLACK_BOARD_SHUTDOWN_ENABLE = "persist.sys.blackboard_shutdown_enable"; // // BlackBoard auto shut down enable, value: enable is "1",  unable is "0"

    String BLACK_BOARD_SHUTDOWN_TIME = "persist.sys.blackboard_shutdown_time"; // // BlackBoard time

    String LIGHT_CONTROL_ENABLE = "persist.sys.light_control_enable";// light control enable ,value: enable is "1",  unable is "0"

    String EYE_PROTECTION_MODE = "persist.sys.eye_protection_mode";// eye protection mode ,EYE_DIMMING 调光 EYE_OFF 关闭 EYE_RGB 调整RGB

    String BOOTUP_LED_MODE = "persist.sys.led_mode";// boot up led mode ,value: when Boot up ,"0" is default open red led , "1" is open green led

    String SCREEN_TEMPERATURE_THRESHOLD = "persist.sys.screen_temp_threshold"; // Temperature Threshold
    
    String PERSIST_GPIO_STATUS = "persist.sys.ops.gpio.status";

    String SERVICE_LIGHT_ENABLE = "service.light.sense.enable";

    int TEMPERATURE_THRESHOLD = 90;

    String EYE_LAST_BACKLIGHT = "EYE_LAST_BACKLIGHT"; // EYE LAST BACKLIGHT

    String SYS_BACKLIGHT = "persist.sys.backlight"; // system save backlight

    String HDMI_TX_MODE = "persist.sys.hdmiTxMode"; // hdmitx mode

    String SYS_CTV_4K_STATUS = "persist.sys.ctv.4k2k";

    // public enum EnumTimerPeriod {
    //    // / timer period is none
    //    EN_Timer_Off,
    //    // / timer period is once
    //    EN_Timer_Once,
    //    // / timer period is everyday
    //    EN_Timer_Everyday,
    //    // / timer period is from Monday to Friday
    //    EN_Timer_Mon2Fri,
    //    // / timer period is from Monday to Saturdat
    //    EN_Timer_Mon2Sat,
    //    // / timer period is from Saturdat to Sunday
    //    EN_Timer_Sat2Sun,
    //    // / timer period is only Sunday
    //    EN_Timer_Sun,
    //    // / timer period is user defined
    //    EN_Timer_User_Defined,
    //    // / counts of this enum
    //    EN_Timer_Num
    //}
    // if EnumTimerPeriod is EN_Timer_User_defined, save the mode
    // use 1 byte save the the day of week:
    //byte： 0    1   1    1      1   1   1   1
    //week:      SAT FRI  THU    WED TUE MON SUN
    // like 0100 1001 is contained SAT, WED, SUN
    String SYS_ON_TIME_PERIOD_MODE = "persist.sys.on.TimerPeriodMode"; // set time on: Timer Period mode
    String SYS_ON_TIME_PERIOD_WEEK = "persist.sys.on.TimerPeriodWeek"; // set time on: Timer Period Week
    String SYS_OFF_TIME_PERIOD_MODE = "persist.sys.off.TimerPeriodMode"; // set time off: Timer Period mode
    String SYS_OFF_TIME_PERIOD_WEEK = "persist.sys.off.TimerPeriodWeek"; // set time off: Timer Period week
    String OPS_START_MODE = "persist.sys.ops.start.mode";
    // week repeat
    int DAYS_PER_WEEK = 7;
    // timer repeat mode Sunday
    int REPEAT_SUN = 0x01;
    // timer repeat mode Monday
    int REPEAT_MON = 0x02;
    // timer repeat mode Tuesday
    int REPEAT_TUE = 0x04;
    // timer repeat mode Wednesday
    int REPEAT_WED = 0x08;
    // timer repeat mode Thursday
    int REPEAT_THU = 0x10;
    // timer repeat mode Friday
    int REPEAT_FRI = 0x20;
    // timer repeat mode Saturday
    int REPEAT_SAT = 0x40;
    // timer repeat mode From Saturday to Sunday
    int REPEAT_SAT_SUN = 0x03;
    // timer repeat mode From Monday to Friday
    int REPEAT_MON_FRI = 0x3E;
    // timer repeat mode From Monday to Saturday
    int REPEAT_MON_SAT = 0x7E;
    // timer repeat mode daily
    int REPEAT_DAILY = 0x7F;

    // locak keypad white list
    String LOCK_KEYPAD_WHITELIST = "persist.lock.keypad.whitelist";
}
