package com.hht.android.sdk.service;

import android.content.Context;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;

import com.hht.android.sdk.touch.IHHTTouch;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2020/1/4 15:21
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/1/4 15:21
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class HHTTouchService extends IHHTTouch.Stub {
    private final static String TAG = HHTTouchService.class.getSimpleName();

    private Context mContext;

    public HHTTouchService(Context context) {
        this.mContext = context;
        Log.i(TAG, "HHTTouchService init");
    }

    @Override
    public boolean controlPcTouchRect(String strPackage, String strWinTitle, Rect touchRect, boolean bEnable) throws RemoteException {
        return false;
    }
}
