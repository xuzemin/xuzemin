package com.hht.android.sdk.ops;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

/**
 * HHTOpsManager 是OPS相关接口管理类。
 * 比如：
 * OPS开启状态，
 * OPS插入状态，
 * OPS电源控制，
 * 是否存在OPS，
 * OPS电脑的各种状态，等
 */
public class HHTOpsManager {
    public static final String MODE_OPS_ANY_CHANNEL = "ops_any_channel";
    public static final String MODE_OPS_NONE = "ops_none";
    public static final String MODE_OPS_ONLY = "ops_only";
    public static final String SERVICE = "sdk_OpsManager";

    private static HHTOpsManager INSTANCE;
    private static IHHTOps mService=null;
    private HHTOpsManager(){
        IBinder service = ServiceManager.getService(HHTOpsManager.SERVICE);
        mService= IHHTOps.Stub.asInterface(service);
    }

    public static HHTOpsManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HHTOpsManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HHTOpsManager();
                }
            }
        }
        return INSTANCE;
    }

    public boolean setOpsStartMode(String strName) {
        try {
            return mService.setOpsStartMode(strName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getOpsStartMode() {
        try {
            return mService.getOpsStartMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean setOpsStartEnable(boolean bStatus) {
        try {
            return mService.setOpsStartEnable(bStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getOpsStartEnable() {
        try {
            return mService.getOpsStartEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isOpsOk() {
        try {
            return mService.isOpsOk();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isOpsPlugIn() {
        try {
            return mService.isOpsPlugIn();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setOpsPower() {
        try {
            return mService.setOpsPower();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setOpsPowerTurnOn() {
        try {
            return mService.setOpsPowerTurnOn();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setOpsPowerTurnOff() {
        try {
            return mService.setOpsPowerTurnOff();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setOpsPowerLongPress() {
        try {
            return mService.setOpsPowerLongPress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int setOpsVisiable(boolean bIsVisiable) {
        try {
            return mService.setOpsVisiable(bIsVisiable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public String getOpsImei() {
        try {
            return mService.getOpsImei();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getOpsSystem() {
        try {
            return mService.getOpsSystem();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public String getOpsCpuModel() {
        try {
            return mService.getOpsCpuModel();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getOpsMainboardModel() {
        try {
            return mService.getOpsMainboardModel();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getOpsIP() {
        try {
            return mService.getOpsIP();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getOpsMAC() {
        try {
            return mService.getOpsMAC();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getOpsDNS() {
        try {
            return mService.getOpsDNS();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getOpsMemorySize() {
        try {
            return mService.getOpsMemorySize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int getOpsHardDiskSize() {
        try {
            return mService.getOpsHardDiskSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int getOpsCpuTemperature() {
        try {
            return mService.getOpsCpuTemperature();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public String getOpsCpuUseRate(){
        try {
            return mService.getOpsCpuUseRate();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getOpsMemoryUseRate(){
        try {
            return mService.getOpsMemoryUseRate();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getOpsHarddiskUseRate(){
        try {
            return mService.getOpsHarddiskUseRate();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }
    public int setOpsImei(String strStr){
        try {
            return mService.setOpsImei(strStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int setOpsSystem(int iType){
        try {
            return mService.setOpsSystem(iType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int setOpsCpuModel(String strStr){
        try {
            return mService.setOpsCpuModel(strStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int setOpsMainboardModel(String strStr){
        try {
            return mService.setOpsMainboardModel(strStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int setOpsMAC(String strStr){
        try {
            return mService.setOpsMAC(strStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int setOpsDNS(String strStr){
        try {
            return mService.setOpsDNS(strStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int setOpsMemorySize(int iSize){
        try {
            return mService.setOpsMemorySize(iSize);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int setOpsHardDiskSize(int iSize){
        try {
            return mService.setOpsHardDiskSize(iSize);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int setOpsHardDiskInfo(String strStr){
        try {
            return mService.setOpsHardDiskInfo(strStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int setOpsCpuTemperature(String strStr){
        try {
            return mService.setOpsCpuTemperature(strStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int setOpsCpuUseRate(String strUseRate){
        try {
            return mService.setOpsCpuUseRate(strUseRate);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public int setOpsMemoryUseRate(String strUseRate){
        try {
            return mService.setOpsMemoryUseRate(strUseRate);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int setOpsHarddiskUseRate(String strUseRate){
        try {
            return mService.setOpsHarddiskUseRate(strUseRate);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public String getOpsOs(){
        try {
            return mService.getOpsOs();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getOpsMemoryTotalSize(){
        try {
            return mService.getOpsMemoryTotalSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getOpsMemoryAvailableSize(){
        try {
            return mService.getOpsMemoryAvailableSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getOpsHardDiskTotalSize(){
        try {
            return mService.getOpsHardDiskTotalSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getOpsHardDiskAvailableSize(){
        try {
            return mService.getOpsHardDiskAvailableSize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
