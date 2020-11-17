package com.hht.android.sdk.device;

/**
 * @Description: HHT万能接口命令字段
 * @Author: wanghang
 * @CreateDate: 2020/3/26 9:25
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/3/26 9:25
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public interface HHTTvosCommand {
    /**
     * change ops resolution
     */
    String CMD_CHANGE_OPS_RESOLUTION = "oChgOpsRes";

    /**
     * 获取触摸框版本
     */
    String CMD_GET_TOUCH_VERSION = "oGetTouchDevVer";

    /**
     * 双侧快捷键
     */
    String CMD_TWO_TAILED_HOTKEY = "TWO_TAILED_HOTKEY"; //只需要在中间件中添加定义即可,不需要在tvos中实现.

    /**
     * ops 键盘按键
     */
    String CMD_OPS_HOTKEY = "OPS_HOTKEY"; //只需要在中间件中添加定义即可,不需要在tvos中实现.
}
