package com.yinghe.whiteboardlib.utils;

import android.graphics.Color;
import android.os.Environment;

/**
 * 绘图常量
 *
 * @author wang
 * @time on 2017/3/21.
 */
public interface DrawConsts {
    /**
     * SketchView中常量
     */
    int STROKE_TYPE_ERASER = 1;// 橡皮擦
    int STROKE_TYPE_DRAW = 2;// 光滑曲线
    int STROKE_TYPE_LINE = 3;// 直线
    int STROKE_TYPE_CIRCLE = 4;// 圆形
    int STROKE_TYPE_RECTANGLE = 5;// 方形

    int STROKE_TYPE_TEXT = 6;// 文字
    int STROKE_TYPE_ERASER_CIRCLE = 7;// 橡皮擦圈选
    int STROKE_TYPE_PHOTO = 8;// 图片
    int STROKE_TYPE_MOVE_RECORD = 9;// 画笔迁移
    int STROKE_TYPE_ERASER_RECT = 10;// 橡皮擦接触面积擦除

    int EDIT_STROKE = 1;// 画笔模式
    int EDIT_PHOTO = 2;// 图片模式
    int EDIT_VIDEO = 3;// 视频模式
    int EDIT_MOVE_RECT = 4;// 画笔迁移模式

    /**
     * 颜色
     */
    int COLOR_BLACK = Color.parseColor("#ff000000");
    int COLOR_RED = Color.parseColor("#ffff4444");
    int COLOR_GREEN = Color.parseColor("#ff99cc00");
    int COLOR_ORANGE = Color.parseColor("#ffffbb33");
    int COLOR_BLUE = Color.parseColor("#ff33b5e5");
    int REQUEST_IMAGE = 2;
    int REQUEST_BACKGROUND = 3;

    // 按钮透明度
    float BTN_ALPHA = 0.4f;
    float ALL_ALPHA = 1.0f;

    // sdcard目录
    String SDCARD_DIR = Environment.getExternalStorageDirectory().toString();

    // 只保存到本地
    String ANNOTATE_LOCAL_DIR = SDCARD_DIR + "/annotate/";
    // 分享上传文件
    String ANNOTATE_SHARE_FILES_DIR = SDCARD_DIR + "/annotate/share_files/";

    // 截图保存路径
    String SCREEN_SOT_DIR = SDCARD_DIR + "/screenshot/";

    int pupWindowsDPWidth = 300;//弹窗宽度，单位DP
    int strokePupWindowsDPHeight = 300;//画笔弹窗高度，单位DP
    int eraserPupWindowsDPHeight = 70;//橡皮擦弹窗高度，单位DP
    int menuPupWindowsDPHeight = 200;//画笔弹窗高度，单位DP

    int signalPupWindowsDPWidth = 280;//信号切换弹窗高度，单位DP
    int signalPupWindowsDPHeight = 200;//信号切换弹窗高度，单位DP

    int VALID_MIN_LEN = 10;// 点击的最小距离

    // 弹框的Y轴偏移距离
    int POPUP_WIN_YOFF = 8;

    String LAST_POINT_KEY = "LAST_POINT_KEY";
    String LAST_POINT_FORMAT = "%s-%s";

    String LAST_POINT_Y_KEY = "LAST_POINT_Y_KEY"; // 上一次Y轴位置

    String IMAGE_SAVE_SUFFIX = ".jpg";// 图片保存的后缀
}
