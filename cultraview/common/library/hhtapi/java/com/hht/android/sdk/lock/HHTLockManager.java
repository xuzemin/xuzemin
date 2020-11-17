package com.hht.android.sdk.lock;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;


/**
 * HHTLockManager 是相关功能锁接口管理类。
 * 通道锁
 * 遥控锁
 * 童锁
 * 锁触摸
 * 锁屏应用密码
 */
public class HHTLockManager {
    public static final String SERVICE = "sdk_LockManager";

    private static HHTLockManager INSTANCE;
    private static IHHTLock mService=null;
    private HHTLockManager(){
        IBinder service = ServiceManager.getService(HHTLockManager.SERVICE);
        mService= IHHTLock.Stub.asInterface(service);
    }

    public static HHTLockManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HHTLockManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HHTLockManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 按键板的锁状态
     *
     * @return true->enable; false-> disable
     */
    public boolean isKeypadLock() {
        try {
            return mService.isKeypadLock();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

//    /**
//     * 设置按键板锁
//     *
//     * @param bLock - true->enable; false->disable
//     * @return 0->成功， -1 -> 失败
//     */
//    public boolean setKeypadLock(boolean bLock) {
//        try {
//            return mService.setKeypadLock(bLock);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 遥控的锁状态
     * @return true->已锁上; false->解锁
     */
    public boolean isRemoteIrLock() {
        try {
            return mService.isRemoteIrLock();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置遥控锁
     *
     * @param bLock - true->enable; false->disable
     * @return 0->成功， -1 -> 失败
     */
    public boolean setRemoteIrLock(boolean bLock) {
        try {
            return mService.setRemoteIrLock(bLock);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 触摸锁的状态
     *
     * @return true->已锁上; false->解锁
     */
    public boolean isTouchLock() {
        try {
            return mService.isTouchLock();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置触摸锁
     *
     * @param bLock - true->enable; false->disable
     * @return 0->成功， -1 -> 失败
     */
    public boolean setTouchLock(boolean bLock) {
        try {
            return mService.setTouchLock(bLock);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * U盘秘钥锁动作设置
     *
     * @param bLock - true->enable; false->disable
     * @return
     */
    public boolean setUsbKeyLock(boolean bLock) {
        try {
            return mService.setUsbKeyLock(bLock);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * U盘秘钥锁动作设置
     *
     * @param bLock - true->enable; false->disable
     * @return 0->成功， -1 -> 失败
     */
    public boolean isUsbKeyLock(boolean bLock){
        try {
            return mService.isUsbKeyLock(bLock);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取U盘秘钥锁使能状态
     *
     * @return
     */
    public boolean getUsbKeyLockedEnable(){
        try {
            return mService.getUsbKeyLockedEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置U盘秘钥锁使能状态
     * @param bEnable - true->已锁上; false->解锁
     * @return 0->成功， -1 -> 失败
     */
    public boolean setUsbKeyLockedEnable(boolean bEnable){
        try {
            return mService.setUsbKeyLockedEnable(bEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取锁屏界面使能
     *
     * @param bEnable
     * @return
     */
    public int setLockscreenEnable(boolean bEnable){
        try {
            return mService.setLockscreenEnable(bEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 获取锁屏界面使能
     * @return true-enable； false-disable
     */
    public boolean getLockscreenEnable(){
        try {
            return mService.getLockscreenEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置锁屏状态
     *
     * @param iStatus - 0-已解锁； 1-已锁上
     * @return 0->成功， -1 -> 失败
     */
    public int setLockscreenStatus(int iStatus){
        try {
            return mService.setLockscreenStatus(iStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取锁屏状态
     *
     * @return 0-已解锁； 1-已锁上
     */
    public int getLockscreenStatus(){
        try {
            return mService.getLockscreenStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 设置锁屏密码
     *
     * @param strPassword - 密码字符串
     * @return 0->成功， -1 -> 失败
     */
    public int setLockscreenPassword(String strPassword){
        try {
            return mService.setLockscreenPassword(strPassword);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取锁屏密码
     *
     * @return 密码字符串
     */
    public String getLockscreenPassword(){
        try {
            return mService.getLockscreenPassword();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 关闭前置面板
     * 前置面板按键白名单
     * 白名单中的按键不禁止
     * add: wang 0403
     *
     * @param lock
     * @param lockWhiteList keycode
     */
    public void setKeypadLock(boolean lock, int[] lockWhiteList){
        try {
            mService.setKeypadLock(lock, lockWhiteList);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭ops触控
     *
     * @param lock
     */
    public void setOpsTouchLock(boolean lock) {
        try {
            mService.setOpsTouchLock(lock);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取ops触控开关
     * return true 关闭；false 开启
     */
    public boolean isOpsTouchLock() {
        try {
            return mService.isOpsTouchLock();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

}
