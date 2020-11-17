package com.hht.android.sdk.system;


import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;


/**
 * HHTSystemManager 是设备接口管理类。
 * 比如：
 * SystemProperties，
 * 系统升级，
 * 系统版控升级，
 * 应用升级，
 * 应用卸载，等
 */
public class HHTSystemManager {
    public static final String SERVICE = "sdk_SystemManager";

    private static IHHTSystem mService=null;
    private HHTSystemManager() {
        IBinder service = ServiceManager.getService(HHTSystemManager.SERVICE);
        mService = IHHTSystem.Stub.asInterface(service);
    }

    private static HHTSystemManager mInstance = null;

    public static HHTSystemManager getInstance() {
        if (mInstance == null) {
            synchronized (HHTSystemManager.class) {
                if (mInstance == null) {
                    mInstance = new HHTSystemManager();
                }
            }
        }
        return mInstance;
    }

    /*public void setSystemProperties(String strName, String strVal) {
        SystemProperties.set(strName, strVal);
    }

    public String getSystemProperties(String strName, String strDef) {
        return SystemProperties.get(strName, strDef);
    }

    public int startSystemService(String strName) {
        SystemProperties.set("ctl.start", strName);
        return 0;
    }

    public int stopSystemService(String strName) {
        int result = 0;
        SystemProperties.set("ctl.stop", strName);
        return result;
    }*/

    /**
     * 更新版本控制配置
     *
     * @param strConfig - 补丁方式，配置更新不同的设置
     * @return
     */
    public boolean updatePatchVersion(String strConfig){
        try {
            return mService.updatePatchVersion(strConfig);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新主固件
     * @param strFilepath - 升级固件的路径
     * @return
     */
    public boolean updateSystem(String strFilepath){
        try {
            return mService.updateSystem(strFilepath);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新主固件
     *
     * @return
     */
    public boolean updateSystem(){
        try {
            return mService.updateSystemMain();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 安装app
     *
     * @param strApkFilePath - app路径
     * @return
     */
    public boolean installApkPackage(String strApkFilePath) {
        try {
            return mService.installApkPackage(strApkFilePath);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 启动截屏 KeyEvent.KEYCODE_SYSRQ
     * dir: storage/emulated/0/Pictures/Screenshots/
     *
     * @return true:Success, or false: failed
    public boolean startScreenShot(){
        try {
            return mService.startScreenShot();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }*/

    /**
     * screenshot截屏
     *
     * @param width
     * @param height
     * @return
     */
    public Bitmap screenshot(int width, int height){
        try {
            return mService.screenshot(width, height);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
