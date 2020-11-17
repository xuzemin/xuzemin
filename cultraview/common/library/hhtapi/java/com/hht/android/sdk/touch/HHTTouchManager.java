package com.hht.android.sdk.touch;

import android.graphics.Rect;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

public class HHTTouchManager {
    public static final String SERVICE = "sdk_TouchManager";

    private static HHTTouchManager mInstance = null;
    private static IHHTTouch mService = null;
    private HHTTouchManager(){
        IBinder service = ServiceManager.getService(HHTTouchManager.SERVICE);
        mService = IHHTTouch.Stub.asInterface(service);
    }
    public static HHTTouchManager getInstance() {
        if (mInstance == null) {
            mInstance = new HHTTouchManager();
        }
        return mInstance;
    }

    /**
     * 控制电脑触控区域穿透状态
     *
     * @param strPackage 透传应用的包名 例如：批注的包名
     * @param strWinTitle 具体透传区域窗口 例如："hht_win_xxx"
     * @param touchRect 区域：Rect(left, top, right, bottom), 即一个view的区域
     * @param bEnable 此区域触控透传到 PC 或者 false->此区域触控不透传PC
     * @return
     */
    public boolean controlPcTouchRect(String strPackage,
                                      String strWinTitle,
                                      Rect touchRect,
                                      boolean bEnable){
        try {
            return mService.controlPcTouchRect(strPackage, strWinTitle, touchRect, bEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
}
