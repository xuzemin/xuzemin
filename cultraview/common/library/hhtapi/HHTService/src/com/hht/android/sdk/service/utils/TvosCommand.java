package com.hht.android.sdk.service.utils;

/**
 * @Description: 万能接口名字字段
 * @Author: wanghang
 * @CreateDate: 2020/3/11 14:39
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/3/11 14:39
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface TvosCommand {

    // InputSource Status
    String TVOS_COMMON_CMD_GET_SOURCE_STATUS = "GetInputSourceStatus";
    String TVOS_COMMON_CMD_GET_PORT_STATUS = "GetPortInputState";

    String TVOS_COMMON_CMD_CHECK_DIRECT_TUNE_DTV_RF_VALID = "CheckDTVRfValiD";
    String TVOS_COMMON_CMD_CHECK_DIRECT_TUNE_ATV_RF_VALID = "CheckATVRfValiD";

    String TVOS_COMMON_CMD_SETONOFF_USBTOUCH_ON = "SetUSBTOUCH_ON";
    String TVOS_COMMON_CMD_SETONOFF_USBTOUCH_OFF = "SetUSBTOUCH_OFF";

    String TVOS_COMMON_CMD_SENDKEY = "SendKeyToPC-";
    String TVOS_COMMON_CMD_CHECK_TOUCH = "CHECKTOUCH";

    String TVOS_INTERFACE_CMD_OPS_POWER = "SetOPSPOWER";
    String TVOS_INTERFACE_CMD_OPS_POWER_HIGH = "SetOPSPOWERON";
    String TVOS_INTERFACE_CMD_GET_OPS_DEVICE_STATUS = "GetOPSDEVICESTATUS";
    String TVOS_INTERFACE_CMD_GET_OPS_POWER_STATUS = "GetOPSPOWERSTATUS";

    String TVOS_INTERFACE_CMD_LED_BREATH_ON = "LedBreathOn";
    String TVOS_INTERFACE_CMD_LED_BREATH_OFF = "LedBreathOff";

    String TVOS_COMMON_CMD_SET_HDMI_PORT = "SetTIPORT";
    String TVOS_COMMON_CMD_GET_HDMI_PORT = "GetTIPORT";

    String TVOS_COMMON_CMD_GET_TEMP = "GetTemperature";

    String TVOS_COMMON_CMD_SET_NOVA_TEK_HTX_MODE = "SetNovaTekHtxMode";
    String TVOS_COMMON_CMD_SET_NOVA_TEK_SCREEN_MODE = "SetNovaTekScreenMode";
    String TVOS_COMMON_CMD_SET_NOVA_TEK_TCON_MODE = "SetNovaTekTconMode";

    String TVOS_COMMON_CMD_ENABLE_LIGHT_CTL = "EnableLightCtl";// 光控护眼 open
    String TVOS_COMMON_CMD_DISABLE_LIGHT_CTL = "DisableLightCtl";// 光控护眼 close
    String TVOS_COMMON_CMD_GET_LIGHT_CTL_VAL = "GetLightCtlVal";
    String TVOS_COMMON_CMD_GET_LIGHT_CTL_STATUS  =  "LightSensorStatus";

    String TVOS_COMMON_CMD_SET_VGA_SEL = "SetVGA";
    String TVOS_COMMON_CMD_GET_VGA_SEL = "GetVGA";

    // RTC time
    String TVOS_COMMON_CMD_SET_RTCTIME = "SetRTCTime";// SetRTCTime2020030501175901
    String TVOS_COMMON_CMD_GET_RTCTIME = "GetRTCTime";

    // share usb mode
    String TVOS_COMMON_CMD_SHARE_USB_MODE = "ShareUsbMode";
    String TVOS_COMMON_CMD_GET_USB_MODE = "GetUsbMode";

    // auto wakeup by source
    String TVOS_COMMON_CMD_ENABLE_AUTO_WAKEUP = "ENABLE_AUTOWAKEUP";
    String TVOS_COMMON_CMD_DISABLE_AUTO_WAKEUP = "DISABLE_AUTOWAKEUP";

    // ops touch
    String TVOS_INTERFACE_CMD_GET_TOUCH_VER = "oGetTouchDevVer";
    String TVOS_INTERFACE_CMD_SEND_CMD = "oSendCmd";
    String TVOS_INTERFACE_CMD_CHG_OPS_RES = "oChgOpsRes";
    String TVOS_INTERFACE_CMD_SEND_HOT_KEY = "oSendHotKey";
    String TVOS_INTERFACE_CMD_SEND_KEYBOARD = "oSendKeyBoard";

    // tca9539 get port status
    String TVOS_COMMON_CMD_CONFIG_TCA9539  =  "ConfigTca9539";
    String TVOS_COMMON_CMD_QUERY_TCA9539 =  "QueryTca9539";

    // PC touch through
    String TVOS_COMMON_CMD_SET_LIMITED_RECT  = "SetTheLimitedRect";// SetTheLimitedRect#100#100#600#600
    String TVOS_COMMON_CMD_RECOVER_LIMITED_RECT = "RecoverTheLimitedRect";// RecoverTheLimitedRect#100#100#600#600

    String TVOS_COMMON_CMD_ENABLE_OPS_TOUCH = "EnableOpsTouch";
    String TVOS_COMMON_CMD_DISABLE_OPS_TOUCH = "DisableOpsTouch";

    String TVOS_COMMON_CMD_NOTICE_SCREEN_OFF_TIMEOUT = "NOTICE_SCREEN_OFF_TIMEOUT";

    String TVOS_COMMON_CMD_GET_OPS_TOUCH_STATE = "GetOpsTouchState";
    String TVOS_COMMON_CMD_GETRJ45_STATUS = "GETRJ45_STATUS";
}
