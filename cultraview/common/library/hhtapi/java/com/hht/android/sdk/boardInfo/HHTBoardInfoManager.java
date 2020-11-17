package com.hht.android.sdk.boardInfo;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

/**
 * HHTBoardInfoManager 是板卡终端信息的接口管理类。 17
 * 匹配不同方案的接口（MSTAR，海思平台），
 * 比如：
 * 系统存储，
 * 系统可用存储，
 * MAC，
 * 版本信息，
 * 系统版本，
 * 芯片型号，
 * 触摸框型号，
 * 固件版本号，
 * 生产序列号，
 * 激活码，等
 */
public class HHTBoardInfoManager {
    private final static String TAG = HHTBoardInfoManager.class.getSimpleName();

    public static final String SERVICE = "sdk_BoardInfoManager";

    private static HHTBoardInfoManager INSTANCE;
    private static IHHTBoardInfo mService = null;

    private HHTBoardInfoManager(){
        IBinder service = ServiceManager.getService(HHTBoardInfoManager.SERVICE);
        mService = IHHTBoardInfo.Stub.asInterface(service);
    }

    public static HHTBoardInfoManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HHTBoardInfoManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HHTBoardInfoManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 获取操作系统版本号
     *
     *     eg. 6.0；8.0
     * @return Get a string of version
     */
    public String getAndroidOsVersion() {
        try {
            return mService.getAndroidOsVersion();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取无线投屏激活码
     * @param packageName - 应用包名
     * @return Get a string of activation code
     */
    public String getApplicationActivationCode(String packageName) {
        try {
            return mService.getApplicationActivationCode(packageName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getBoardMacAddr() {
        try {
            return mService.getBoardMacAddr();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getBoardSerial() {
        try {
            return mService.getBoardSerial();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getProductSerial() {
        try {
            return mService.getProductSerial();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取可用内存
     * 可用内存大小，单位是Mb
     *
     * @return int, Free memory space
     */
    public int getMemoryAvailableSize() {
        try {
            return mService.getMemoryAvailableSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取总内存
     * 单位是Mb
     *
     * @return int, Total memory space
     */
    public int getMemoryTotalSize() {
        try {
            return mService.getMemoryTotalSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取EMMC总存储
     * EMMC总存储，单位是Mb
     *
     * @return int, Total storage
     */
    public long getSystemTotalStorage() {
        try {
            return mService.getSystemTotalStorage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取系统可用存储空间
     * EMMC总存储，单位是Mb
     * @return
     */
    public long getSystemAvailableStorage() {
        try {
            return mService.getSystemAvailableStorage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取芯片型号
     * @return
     */
    public String getChipModel() {
        try {
            return mService.getChipModel();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "8386";
    }

    /**
     * 获取主板型号
     * @return
     */
    public String getBoardModel() {
        try {
            return mService.getBoardModel();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "CV8386H_A";
    }

    /**
     * 获取产品型号
     * 根据产品而定
     * @return
     */
    public String getProductModel() {
        try {
            return mService.getProductModel();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "CN8386_AH_MTG";
    }

    /**
     * 获取终端设备屏幕尺寸，比如：65寸，70寸
     *
     *     eg. 65寸:60
     * @return
     */
    public int getScreenSize() {
        // todo
        try {
            return mService.getScreenSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 70;
    }

    /**
     * 获取制造商信息
     * @return
     */
    public String getManufacturer() {
        try {
            return mService.getManufacturer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "MStar";
    }

    public String getTouchPanelVersion() {
        try {
            mService.getTouchPanelVersion();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取版控升级补丁的版本
     *
     * @return Get a string of version
     */
    public String getUpdateSystemPatchVersion() {
        // todo
        try {
            return mService.getUpdateSystemPatchVersion();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取固件版本号
     *
     * @return
     */
    public String getFirmwareVersion() {
        // todo
        try {
            return mService.getFirmwareVersion();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取SDK版本号
     * @return
     */
    public String getSdkVersion(){
        try {
            return mService.getSdkVersion();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "V2.0.7";
    }
}
