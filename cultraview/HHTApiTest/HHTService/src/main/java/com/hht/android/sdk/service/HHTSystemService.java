package com.hht.android.sdk.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;

import com.hht.android.sdk.service.utils.Tools;
import com.hht.android.sdk.system.IHHTSystem;

import java.io.File;

public class HHTSystemService extends IHHTSystem.Stub{
    private Context mContext;
    public HHTSystemService(Context context){
        this.mContext=context;
        Log.i("gyx","HHTSystemService init");
    }


    @Override
    public boolean installApkPackage(String strApkFilePath) throws RemoteException {
        return Tools.installBySlient(strApkFilePath) == 1;
    }

    @Override
    public boolean updatePatchVersion(String strConfig) throws RemoteException {
        return false;
    }

    /**
     * 更新主固件
     * @param strFilepath
     * @return
     */
    @Override
    public boolean updateSystem(String strFilepath) throws RemoteException {
        Tools.updateZip(mContext, new File(strFilepath));
        return true;
    }

    /**
     * 更新主固件
     * @return
     */
    @Override
    public boolean updateSystemMain() throws RemoteException {
        return Tools.upgradeMainFun() == 1;
    }

    @Override
    public Bitmap screenshot(int width, int height) throws RemoteException {
        return Tools.screenshot(width, height);
    }

}
