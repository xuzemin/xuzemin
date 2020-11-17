// IHHTLock.aidl
package com.hht.android.sdk.lock;

// Declare any non-default types here with import statements

interface IHHTLock {
    boolean	getLockscreenEnable();
    String	getLockscreenPassword();
    int	getLockscreenStatus();
    boolean	getUsbKeyLockedEnable();
    boolean	isKeypadLock();
    boolean	isRemoteIrLock();
    boolean	isTouchLock();
    boolean	isUsbKeyLock(boolean bLock);
//    boolean	setKeypadLock(boolean bLock);// 0403 delete
    int	setLockscreenEnable(boolean bEnable);
    int	setLockscreenPassword(String strPassword);
    int	setLockscreenStatus(int iStatus);
    boolean	setRemoteIrLock(boolean bLock);
    boolean	setTouchLock(boolean bLock);
    boolean	setUsbKeyLock(boolean bLock);
    boolean	setUsbKeyLockedEnable(boolean bEnable);

    void setKeypadLock(boolean lock, inout int[] lockWhiteList);// 0403 add
    void setOpsTouchLock(boolean lock);// 0403 add

    boolean isOpsTouchLock();// 0414
}
