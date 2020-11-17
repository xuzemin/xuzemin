package com.hht.android.sdk.service;

import android.content.Context;
import android.os.RemoteException;
import android.os.SystemProperties;

import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.led.IHHTLed;
import com.hht.android.sdk.service.utils.L;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import static com.hht.android.sdk.led.HHTLedManager.LED_RED;

public class HHTLedService extends IHHTLed.Stub {
    private Context mContext;

    public HHTLedService(Context context) {
        this.mContext = context;
        L.i("gyx", "HHTLedService init");
    }

    /**
     * 获取LED的模式状态
     * 0->开机默认红色; 1-> 开机默认绿色
     * 有可能是反向，需要根据当前硬件决定
     *
     * @return
     */
    @Override
    public int getLedMode() throws RemoteException {
        return SystemProperties.getInt(HHTConstant.BOOTUP_LED_MODE, 1);
    }

    /**
     * 设置Led遥控灯状态,有可能是反向，需要根据当前硬件决定
     * 0->开机默认红色; 1-> 开机默认绿色
     *
     * @param iMode
     * @return
     */
    @Override
    public boolean setLedMode(int iMode) throws RemoteException {
        if (iMode >= 0 && iMode <=1){
            SystemProperties.set(HHTConstant.BOOTUP_LED_MODE, iMode + "");
            return true;
        }

        return false;
    }

    /**
     * 设置Led遥控灯状态,有可能是反向，需要根据当前硬件决定
     * @param bStatus false--red, true--green
     * @return true:Success, or false: failed
     */
    @Override
    public boolean setLedStatus(boolean bStatus) throws RemoteException {
        boolean isRed = !bStatus;

        // led mode  0->开机默认红色; 1-> 开机默认绿色
        int ledMode = SystemProperties.getInt(HHTConstant.BOOTUP_LED_MODE, 1);
        if (ledMode == 1){ // 开机默认绿色
            isRed = bStatus;
        }
        try {
            TvManager.getInstance().setGpioDeviceStatus(LED_RED, isRed);
        } catch (TvCommonException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
