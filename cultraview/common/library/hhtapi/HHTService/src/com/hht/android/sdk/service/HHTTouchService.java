package com.hht.android.sdk.service;

import android.content.Context;
import android.graphics.Rect;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.graphics.Rect;
import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.service.utils.L;
import com.hht.android.sdk.service.utils.Tools;
import com.hht.android.sdk.service.utils.TvosCommand;
import com.hht.android.sdk.touch.IHHTTouch;
import android.app.ActivityManager;
import android.view.WindowManager;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * @Description: ops touch 转发处理
 * @Author: wanghang
 * @CreateDate: 2020/1/4 15:21
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/1/4 15:21
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class HHTTouchService extends IHHTTouch.Stub {
    private final static String TAG = HHTTouchService.class.getSimpleName();
    // 32767*32767
    private final static int PANEL_WIDTH = 0x7fff;
    private final static int PANEL_HEIGHT = 0x7fff;

    private final static int WIDTH_2K = 1920;
    private final static int HEIGHT_2K = 1080;

    private final static int WIDTH_4K = 3840;
    private final static int HEIGHT_4K = 2160;

    private float scaleX = 1.0f * PANEL_WIDTH / WIDTH_2K;// 17.067
    private float scaleY = 1.0f * PANEL_HEIGHT / HEIGHT_2K;// 30.34

    private Context mContext;

    private boolean is4K;

    private ActivityManager mActivityManager; 

    private WindowManager mWindowManager;

    public HHTTouchService(Context context) {
        this.mContext = context;
        L.i(TAG, "HHTTouchService init");
        mActivityManager = (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 控制电脑触控区域穿透状态
     *
     * @param strPackage  透传应用的包名 例如：批注的包名
     * @param strWinTitle 具体透传区域窗口 例如："hht_win_xxx"
     * @param touchRect   区域：Rect(left, top, right, bottom), 即一个view的区域
     * @param bEnable     此区域触控透传到 PC 或者 false->此区域触控不透传PC
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean controlPcTouchRect(String strPackage, String strWinTitle, Rect touchRect, boolean bEnable) throws RemoteException {
        boolean is4k2kmode = SystemProperties.getBoolean("mstar.4k2k.2k1k.coexist",false);
        boolean is4K = false;
        if(is4k2kmode){
            is4K = (mActivityManager.getPackageScreenCompatMode(strPackage) == 3? true:false);
        }else{
            DisplayMetrics outMetrics = new DisplayMetrics();
            Point outSize = new Point();
            mWindowManager.getDefaultDisplay().getRealSize(outSize);
            if (outSize.y == 2160 && outSize.x == 3840){
                is4K = true;
            }
        }
        
        if (!checkRect(touchRect,is4K)) {
            L.i(TAG, "controlPcTouchRect touchRect is error!");
            return false;
        }

        L.i(TAG, String.format("controlPcTouchRect strPackage->%s, strWinTitle->%s, touchRect->%s, bEnable->%s, is4K app -> %s",
                strPackage, strWinTitle, touchRect, bEnable,is4K));

        String cmdStr;
        if (bEnable) { // 此区域触控透传到 PC
            cmdStr = TvosCommand.TVOS_COMMON_CMD_RECOVER_LIMITED_RECT;
        } else { //此区域触控不透传PC
            cmdStr = TvosCommand.TVOS_COMMON_CMD_SET_LIMITED_RECT;
        }

        if(is4K){
            scaleX = 1.0f * PANEL_WIDTH / WIDTH_4K;
            scaleY = 1.0f * PANEL_HEIGHT / HEIGHT_4K;
        }
        cmdStr += String.format("#%s#%s#%s#%s",
                (int) (touchRect.left * scaleX), // x
                (int) (touchRect.top * scaleY), // y
                (int) ((touchRect.right - touchRect.left) * scaleX), // width
                (int) ((touchRect.bottom - touchRect.top) * scaleY));// height
        Tools.sendCommand(cmdStr);
        L.i(TAG, String.format("controlPcTouchRect scaleX->%s, scaleY->%s, cmdStr->%s",
                scaleX, scaleY, cmdStr));
        return true;
    }

    /**
     * check rect
     *
     * @param rect
     * @return
     */
    private boolean checkRect(Rect rect, boolean is4K) {
        if (rect == null) {
            return false;
        }

        int width = is4K ? WIDTH_4K : WIDTH_2K;
        int height = is4K ? HEIGHT_4K : HEIGHT_2K;

        boolean flag = false;
        if (rect.left >= 0
                && rect.right <= width
                && rect.left <= rect.right
                && rect.top >= 0
                && rect.bottom <= height
                && rect.top <= rect.bottom) {
            flag = true;
        }

        return flag;
    }
}
