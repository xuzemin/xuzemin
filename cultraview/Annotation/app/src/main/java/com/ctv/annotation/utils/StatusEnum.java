package com.ctv.annotation.utils;

/**
 * Created by Dong.Daoping on 2018/4/10 0010
 * 说明：画板状态的枚举
 * 星空白板中在PanelManager中，此处设置为公开，方便调整和阅读
 */
public class StatusEnum {
    /**书写 */
    public static final int STATUS_WRITE = 8;

    /**选中消除*/
    public static final int STATUS_ERASE_BY_SELECTION = 7;

    /**橡皮擦移动消除*/
    public static final int STATUS_ERASE_BY_SIZE = 2;

    /**初始/重置状态*/
    public static final int STATUS_NC = 5;

    /**画线*/
    public static final int STATUS_PAINT = 0;

    /**选择(此处的选中之后对应缩放和移动)*/
    public static final int STATUS_SELECTION = 6;

    /**选中之后被缩放或移动*/
    public static final int STATUS_MOVE_ZOOM_SELECTION = 9;

//    /**缩放*/
//    public static final int STATUS_ZOOM = 3;
//
//    /**移动状态*/
//    public static final int STATUS_MOVE = 4;



    /**菜单栏对应的MODE，mode决定了PanelManager的status*/
    public static final int MODE_ERASE_MATERIAL = 2;
    public static final int MODE_ERASE_SIZE = 1;
    public static final int MODE_MAX = 4;
    public static final int MODE_PAINT = 0;
    public static final int MODE_SELECTION = 3;

}
