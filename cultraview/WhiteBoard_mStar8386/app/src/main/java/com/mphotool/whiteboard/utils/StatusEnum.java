package com.mphotool.whiteboard.utils;

/**
 * Created by Dong.Daoping on 2018/4/10 0010
 * 说明：画板状态的枚举
 * 星空白板中在PanelManager中，此处设置为公开，方便调整和阅读
 */
public class StatusEnum {

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
    /**书写 */
    public static final int STATUS_WRITE = 8;

    /**选中之后被缩放或移动*/
    public static final int STATUS_MOVE_ZOOM_SELECTION = 9;

    /**形状 */
    public static final int STATUS_SHAPE = 10;
    /**形状 直线*/
    public static final int STATUS_SHAPE_LINE = 11;
    /**形状 椭圆*/
    public static final int STATUS_SHAPE_OVAL = 12;
    /**形状 圆*/
    public static final int STATUS_SHAPE_CIRCLE = 13;
    /**形状 矩形*/
    public static final int STATUS_SHAPE_RECTTANGLE = 14;
    /**形状 正三角*/
    public static final int STATUS_SHAPE_RIGHT_TRIANGLE = 15;
    /**形状 三角形*/
    public static final int STATUS_SHAPE_TRIANGLE = 16;
    /**形状 箭头*/
    public static final int STATUS_SHAPE_ARROW = 17;
    /**形状 五角星*/
    public static final int STATUS_SHAPE_FIVE_POINT = 18;
    /**形状 圆锥**/
    public static final int STATUS_SHAPE_CONE = 19;
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
