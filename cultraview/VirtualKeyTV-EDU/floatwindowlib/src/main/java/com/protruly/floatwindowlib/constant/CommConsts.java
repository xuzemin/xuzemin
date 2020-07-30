package com.protruly.floatwindowlib.constant;

/**
 * Desc:常量
 *
 * @author wng
 * @time 2017/5/10.
 */
public interface CommConsts {
    /**
     * 切换PC通道
     */
    String PC_CHANNEL_PKG = "com.mstar.tvsetting";
    String PC_CHANNEL_CLASS = "com.mstar.tvsetting.switchinputsource.PictrueChangeActivity";

    // 服务器的URL地址
//    String BASE_URL = "http://yun11.luckshow.cn:8099/pot/";
    String BASE_URL = "http://yun8.luckshow.cn/pot/";
    String UPDLOAD_URL = BASE_URL + "upload";
    String DOWN_URL = BASE_URL + "down";
    String SIZE_URL = BASE_URL + "size";
    String SHOW_URL = BASE_URL + "show";
    String SHARE_URL = BASE_URL + "share";
    String PWD_URL = BASE_URL + "pwd";

    // 护眼的值
    String IS_EYECARE = "isEyecare";
    String IS_LIGHTSENSE = "IS_LIGHTSENSE"; // 自动光感

    String CURRENT_PAGE = "CURRENT_PAGE"; // 当前选择的页码

    String LIGHT_SENSE = "LIGHT_SENSE"; // 光感值
}
