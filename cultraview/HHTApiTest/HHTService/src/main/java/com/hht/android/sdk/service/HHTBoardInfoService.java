package com.hht.android.sdk.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import com.hht.android.sdk.boardInfo.IHHTBoardInfo;
import com.hht.android.sdk.service.utils.DataUtils;
import com.hht.android.sdk.service.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HHTBoardInfoService extends IHHTBoardInfo.Stub {
    private Context mContext;

    public HHTBoardInfoService(Context context) {
        this.mContext = context;
        Log.i("gyx", "HHTBoardInfoService init");
    }

    /**
     * 获取操作系统版本号
     * <p>
     * eg. 6.0；8.0
     *
     * @return Get a string of version
     */
    @Override
    public String getAndroidOsVersion() throws RemoteException {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取无线投屏激活码
     * @param packageName - 应用包名
     * @return Get a string of activation code
     */
    @Override
    public String getApplicationActivationCode(String packageName) throws RemoteException {
        // 获取packagemanager的实例
        PackageManager packageManager = mContext.getPackageManager();

        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(packageName, 0);
            return packInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getBoardMacAddr() throws RemoteException {
        return DataUtils.getInstance().getEnvironment("ethaddr");
    }

    /**
     * 获取主板型号
     * @return
     */
    @Override
    public String getBoardModel() throws RemoteException {
        return SystemProperties.get("ro.product.board", "CV8386H_A");
    }

    @Override
    public String getBoardSerial() throws RemoteException {
        String sn = DataUtils.getInstance().getEnvironment("sn");
        if (TextUtils.isEmpty(sn)){
            sn = "SN123456";
        }
        return sn;
    }
    /**
     * 获取芯片型号
     * @return
     */
    @Override
    public String getChipModel() throws RemoteException {
        return "8386";
    }

    @Override
    public String getFirmwareVersion() throws RemoteException {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long utc1 = Long.parseLong(SystemProperties.get("ro.build.date.utc", "1546304400"));
        String str_build_date =sdr.format(new Date(utc1 * 1000L));
        try {
            return getProductModel() + " " + str_build_date;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取制造商信息
     * @return
     */
    @Override
    public String getManufacturer() throws RemoteException {
        return "Cultraview";//SystemProperties.get("ro.product.manufacturer", "MStar");
    }

    /**
     * 获取可用内存
     * 可用内存大小，单位是Mb
     *
     * @return int, Free memory space
     */
    @Override
    public int getMemoryAvailableSize() throws RemoteException {
        return Tools.getAvailMemory(mContext);
    }

    @Override
    public int getMemoryTotalSize() throws RemoteException {
        return Tools.getTotalMemory();
    }

    /**
     * 获取产品型号
     * 根据产品而定
     * @return
     */
    @Override
    public String getProductModel() throws RemoteException {
        return SystemProperties.get("ro.build.display.id", "CN8386_AH_MTG");
    }

    @Override
    public String getProductSerial() throws RemoteException {
        String ps = DataUtils.getInstance().getEnvironment("psn");
        if (TextUtils.isEmpty(ps)){
            ps = "PS123456";
        }
        return ps;
    }

    /**
     * 获取终端设备屏幕尺寸，比如：65寸，70寸
     *
     *     eg. 65寸:60
     * @return
     */
    @Override
    public int getScreenSize() throws RemoteException {
        // todo
        return 70;
    }

    /**
     * 获取系统可用存储空间
     * EMMC总存储，单位是Mb
     * @return
     */
    @Override
    public long getSystemAvailableStorage() throws RemoteException {
        return Tools.getSystemAvailableStorage();
    }

    /**
     * 获取EMMC总存储
     * EMMC总存储，单位是Mb
     *
     * @return int, Total storage
     */
    @Override
    public long getSystemTotalStorage() throws RemoteException {
        return Tools.getSystemTotalStorage();
    }

    @Override
    public String getTouchPanelVersion() throws RemoteException {
        // todo
        return "";
    }

    @Override
    public String getUpdateSystemPatchVersion() throws RemoteException {
        // todo
        return getFirmwareVersion();
    }

}
