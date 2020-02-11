package com.hht.android.sdk.touch;

import android.graphics.Rect;
import android.os.IBinder;
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
     *
     *
     * @param strPackage
     * @param strWinTitle
     * @param touchRect
     * @param bEnable
     * @return
     */
    public boolean controlPcTouchRect(String strPackage,
                                      String strWinTitle,
                                      Rect touchRect,
                                      boolean bEnable){
        return false;
    }
}
