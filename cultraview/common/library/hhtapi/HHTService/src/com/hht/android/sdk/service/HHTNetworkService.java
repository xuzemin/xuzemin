package com.hht.android.sdk.service;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;

import com.hht.android.sdk.network.IHHTNetwork;
import com.hht.android.sdk.network.LinkConfig;
import com.hht.android.sdk.service.utils.L;
import com.hht.android.sdk.service.utils.NetUtils;
import com.mstar.android.tvapi.common.TvManager;
import com.hht.android.sdk.service.utils.Tools;
import com.hht.android.sdk.service.utils.TvosCommand;
public class HHTNetworkService extends IHHTNetwork.Stub {
    private final static String TAG = HHTNetworkService.class.getSimpleName();

    private Context mContext;
	private Handler mHandler = new Handler();
    public HHTNetworkService(Context context){
        this.mContext=context;
        L.i(TAG,"HHTNetworkService init");
    }

    public boolean isLANPortLinked() throws RemoteException {
        try {
            return Tools.getResultFromTvOS(TvosCommand.TVOS_COMMON_CMD_GETRJ45_STATUS) != 0;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 网络唤醒开关
     * @return true-enable； false-disable
     */
    @Override
    public boolean getWolEn() throws RemoteException {
        try {
            return TvManager.getInstance().getFactoryManager().getWOLEnableStatus();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setWolEn(boolean bEnable) throws RemoteException {
        try {
            TvManager.getInstance().getFactoryManager().setWOLEnableStatus(bEnable);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
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
    public void setEthernetMode(final String mode) throws RemoteException {
        try {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    NetUtils.setEthernetMode(mContext, mode);
                }
            });
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

    @Override
    public void setLinkConfig(final LinkConfig config) throws RemoteException {
		mHandler.post(new Runnable() {
            @Override
            public void run() {
                NetUtils.setLinkConfig(mContext, config);
            }
        });
    }
}
