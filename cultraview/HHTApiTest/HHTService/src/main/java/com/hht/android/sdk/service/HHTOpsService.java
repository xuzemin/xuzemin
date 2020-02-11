package com.hht.android.sdk.service;

import android.content.Context;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.hht.android.sdk.ops.HHTOpsManager;
import com.hht.android.sdk.ops.IHHTOps;
import com.hht.android.sdk.service.utils.Tools;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.PictureManager;

public class HHTOpsService extends IHHTOps.Stub {
    private static TvPictureManager mTvPicMgr = null;
    private static PictureManager mApiPicMgr = null;
    private Context mContext;
    public HHTOpsService(Context context){
        this.mContext=context;
        Log.i("gyx","HHTOpsService init");
    }
    @Override
    public String getOpsCpuModel() throws RemoteException {
        // todo
        return null;
    }

    @Override
    public int getOpsCpuTemperature() throws RemoteException {
        // todo
        return 0;
    }

    @Override
    public String getOpsCpuUseRate() throws RemoteException {
        // todo
        return null;
    }

    @Override
    public String getOpsDNS() throws RemoteException {
        // todo
        return null;
    }

    @Override
    public int getOpsHardDiskSize() throws RemoteException {
        // todo
        return 0;
    }

    @Override
    public String getOpsHarddiskUseRate() throws RemoteException {
        // todo
        return null;
    }

    @Override
    public String getOpsImei() throws RemoteException {
        // todo
        return null;
    }

    @Override
    public String getOpsIP() throws RemoteException {
        // todo
        return null;
    }

    @Override
    public String getOpsMAC() throws RemoteException {
        // todo
        return null;
    }

    @Override
    public String getOpsMainboardModel() throws RemoteException {
        // todo
        return null;
    }

    @Override
    public int getOpsMemorySize() throws RemoteException {
        // todo
        return 0;
    }

    @Override
    public String getOpsMemoryUseRate() throws RemoteException {
        // todo
        return null;
    }

    @Override
    public boolean getOpsStartEnable() throws RemoteException {
        int mode = Settings.System.getInt(mContext.getContentResolver(), "OpsStartEnable", 0);
        boolean bStatus = mode == 1;
        return bStatus;
    }

    @Override
    public String getOpsStartMode() throws RemoteException {
        String mode = Settings.System.getString(mContext.getContentResolver(), "OpsStartMode");
        if (TextUtils.isEmpty(mode)){
            mode = HHTOpsManager.MODE_OPS_NONE;
        }
        return mode;
    }

    @Override
    public int getOpsSystem() throws RemoteException {
        // todo
        return 0;
    }

    @Override
    public boolean isOpsOk() throws RemoteException {
        return Tools.isOpsOpen();
    }

    @Override
    public boolean isOpsPlugIn() throws RemoteException {
        return Tools.isOpsPlugIn();
    }

    @Override
    public int setOpsCpuModel(String strStr) throws RemoteException {
        // todo
        return 0;
    }

    @Override
    public int setOpsCpuTemperature(String strStr) throws RemoteException {
        // todo
        return 0;
    }

    @Override
    public int setOpsCpuUseRate(String strUseRate) throws RemoteException {
        // todo
        return 0;
    }

    @Override
    public int setOpsDNS(String strStr) throws RemoteException {
        // todo
        return 0;
    }

    @Override
    public int setOpsHardDiskInfo(String strStr) throws RemoteException {
        // todo
        return 0;
    }

    @Override
    public int setOpsHardDiskSize(int iSize) throws RemoteException {
        // todo
        return 0;
    }

    @Override
    public int setOpsHarddiskUseRate(String strUseRate) throws RemoteException {
        return 0;
    }

    @Override
    public int setOpsImei(String strStr) throws RemoteException {
        return 0;
    }

    @Override
    public int setOpsMAC(String strStr) throws RemoteException {
        return 0;
    }

    @Override
    public int setOpsMainboardModel(String strStr) throws RemoteException {
        return 0;
    }

    @Override
    public int setOpsMemorySize(int iSize) throws RemoteException {
        return 0;
    }

    @Override
    public int setOpsMemoryUseRate(String strUseRate) throws RemoteException {
        return 0;
    }

    @Override
    public boolean setOpsPower() throws RemoteException {
        return false;
    }

    @Override
    public boolean setOpsPowerLongPress() throws RemoteException {
        return Tools.closeOps();
    }

    @Override
    public boolean setOpsPowerTurnOff() throws RemoteException {
        return Tools.closeOps();
    }

    @Override
    public boolean setOpsPowerTurnOn() throws RemoteException {
        return Tools.openOps();
    }

    @Override
    public boolean setOpsStartEnable(boolean bStatus) throws RemoteException {
        int mode = bStatus ? 1:0;
        Settings.System.putInt(mContext.getContentResolver(), "OpsStartEnable", mode);
        return true;
    }

    @Override
    public boolean setOpsStartMode(String strName) throws RemoteException {
//        int mode = bStatus ? 1:0;
        if (TextUtils.equals(strName, HHTOpsManager.MODE_OPS_NONE)
            || TextUtils.equals(strName, HHTOpsManager.MODE_OPS_ANY_CHANNEL)
            || TextUtils.equals(strName, HHTOpsManager.MODE_OPS_ONLY)){
            Settings.System.putString(mContext.getContentResolver(), "OpsStartMode", strName);
            return true;
        }
        return false;
    }

    @Override
    public int setOpsSystem(int iType) throws RemoteException {
        return 0;
    }

    @Override
    public int setOpsVisiable(boolean bIsVisiable) throws RemoteException {
        return 0;
    }

    @Override
    public String getOpsOs() throws RemoteException {
        return null;
    }

    @Override
    public int getOpsMemoryTotalSize() throws RemoteException {
        return 0;
    }

    @Override
    public int getOpsMemoryAvailableSize() throws RemoteException {
        return 0;
    }

    @Override
    public int getOpsHardDiskTotalSize() throws RemoteException {
        return 0;
    }

    @Override
    public int getOpsHardDiskAvailableSize() throws RemoteException {
        return 0;
    }
}
