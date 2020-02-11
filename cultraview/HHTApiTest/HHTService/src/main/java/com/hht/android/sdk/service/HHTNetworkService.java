package com.hht.android.sdk.service;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.network.IHHTNetwork;
import com.hht.android.sdk.service.utils.NetUtils;

public class HHTNetworkService extends IHHTNetwork.Stub {
    private final static String TAG = HHTNetworkService.class.getSimpleName();

    private Context mContext;
    public HHTNetworkService(Context context){
        this.mContext=context;

        if (HHTConstant.DEBUG) Log.i(TAG,"HHTNetworkService init");
    }

    /**
     * 网络唤醒开关
     * @return true-enable； false-disable
     */
    @Override
    public boolean getWolEn() throws RemoteException {
        // todo
        return false;
    }

    @Override
    public boolean setWolEn(boolean bEnable) throws RemoteException {
        // todo
        return false;
    }

    @Override
    public boolean setEthernetEnabled(boolean bEnable) throws RemoteException {
        return NetUtils.setEthernetEnabled(mContext, bEnable);
    }

    @Override
    public String getEthernetMode() throws RemoteException {
        return NetUtils.getEthernetMode(mContext);
    }

    @Override
    public int getEthernetState() throws RemoteException {
        return NetUtils.getEthernetState(mContext);
    }

    @Override
    public void setEthernetMode(String mode) throws RemoteException {
        try {
            NetUtils.setEthernetMode(mContext, mode);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String getEthernetMacAddress() throws RemoteException {
        return NetUtils.getEthernetMacAddress(mContext);
    }

    @Override
    public String getEthernetIpAddress() throws RemoteException {
        return NetUtils.getEthernetIpAddress(mContext);
    }
}
