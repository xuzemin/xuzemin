package com.hht.android.sdk.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.widget.Toast;

import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.lock.IHHTLock;
import com.hht.android.sdk.service.utils.L;
import com.hht.android.sdk.service.utils.Tools;
import com.hht.android.sdk.service.utils.TvosCommand;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

public class HHTLockService extends IHHTLock.Stub {

    private Context mContext;
    private static final String FILE_NAME = "usblock.bat"; // usb key file
    private static final String FILE_CONTENT = "666ctv888"; // usb key password
    private static boolean mCheckFileRet = false;

    public HHTLockService(Context context) {
        this.mContext = context;
        L.i("gyx", "HHTLockService init");
    }

    /**
     * 获取锁屏界面使能
     * @return true-enable； false-disable
     */
    @Override
    public boolean getLockscreenEnable() throws RemoteException {
        String status = SystemProperties.get(HHTConstant.LOCK_SCREEN_ENABLE, HHTConstant.LOCK_STATUS_OFF);
        return HHTConstant.LOCK_STATUS_ON.equals(status);
    }


    /**
     * 获取锁屏密码
     *
     * @return 密码字符串
     */
    @Override
    public String getLockscreenPassword() throws RemoteException {
        return Settings.System.getString(mContext.getContentResolver(),HHTConstant.LOCK_SCREEN_PWD);
    }

    /**
     * 获取锁屏状态
     *
     * @return 0-已解锁； 1-已锁上
     */
    @Override
    public int getLockscreenStatus() throws RemoteException {
        String status = SystemProperties.get("persist.sys.lockScreen", HHTConstant.LOCK_STATUS_OFF);
        return HHTConstant.LOCK_STATUS_ON.equals(status) ? 0 : 1;
    }

    /**
     * 获取U盘秘钥锁使能状态
     *
     * @return
     */
    @Override
    public boolean getUsbKeyLockedEnable() throws RemoteException {
        String status = SystemProperties.get(HHTConstant.LOCK_SCREEN_ENABLE, HHTConstant.LOCK_STATUS_OFF);
        return (HHTConstant.LOCK_STATUS_ON.equals(status));
    }

    /**
     * 按键板的锁状态
     *
     * @return true->enable; false-> disable
     */
    @Override
    public boolean isKeypadLock() throws RemoteException {
        String lockStatus = SystemProperties.get(HHTConstant.LOCK_KEYPAD, HHTConstant.LOCK_STATUS_OFF);
        return HHTConstant.LOCK_STATUS_ON.equals(lockStatus);
    }

    /**
     * 遥控的锁状态
     *
     * @return true->已锁上; false->解锁
     */
    @Override
    public boolean isRemoteIrLock() throws RemoteException {
        String lockStatus = SystemProperties.get(HHTConstant.LOCK_IR, HHTConstant.LOCK_STATUS_OFF);
        return HHTConstant.LOCK_STATUS_ON.equals(lockStatus);
    }

    /**
     * 触摸锁的状态
     *
     * @return true->已锁上; false->解锁
     */
    @Override
    public boolean isTouchLock() throws RemoteException {
        String touche_enable = SystemProperties.get(HHTConstant.LOCK_TOUCH, HHTConstant.LOCK_TOUCH_STATUS_OFF);
        return HHTConstant.LOCK_TOUCH_STATUS_ON .equals(touche_enable);
    }

    /**
     * U盘秘钥锁动作设置
     *
     * @param bLock - true->enable; false->disable
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean isUsbKeyLock(boolean bLock) throws RemoteException {
        String lockStatus = bLock ? HHTConstant.LOCK_STATUS_ON : HHTConstant.LOCK_STATUS_OFF;
        SystemProperties.set(HHTConstant.LOCK_USB, lockStatus);
        return true;
    }

//    /**
//     * 设置按键板锁
//     *
//     * @param bLock - true->enable; false->disable
//     * @return 0->成功， -1 -> 失败
//     */
//    @Override
//    public boolean setKeypadLock(boolean bLock) throws RemoteException {
//        String lockStatus = bLock ? HHTConstant.LOCK_STATUS_ON : HHTConstant.LOCK_STATUS_OFF;
//        SystemProperties.set(HHTConstant.LOCK_KEYPAD, lockStatus);
//        return true;
//    }

    /**
     * 获取锁屏界面使能
     *
     * @param bEnable
     * @return
     */
    @Override
    public int setLockscreenEnable(boolean bEnable) throws RemoteException {
        String lockStatus = bEnable ? HHTConstant.LOCK_STATUS_ON : HHTConstant.LOCK_STATUS_OFF;
        SystemProperties.set(HHTConstant.LOCK_SCREEN, lockStatus);
        SystemProperties.set(HHTConstant.LOCK_SCREEN_ENABLE, lockStatus);

        try {
            // 启动锁屏 UI
            Intent intent = new Intent("com.ctv.lockscreen.action.LockScreen");
            intent.setComponent(new ComponentName("com.ctv.lockscreen", "com.ctv.lockscreen.LockScreen"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mContext, "There is not exist Lock screen View", Toast.LENGTH_LONG).show();
        }

        return 0;
    }

    /**
     * 设置锁屏密码
     *
     * @param strPassword - 密码字符串
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public int setLockscreenPassword(String strPassword) throws RemoteException {
        if (strPassword == null || "".equals(strPassword.trim())){
            return -1;
        }
        Settings.System.putString(mContext.getContentResolver(), HHTConstant.LOCK_SCREEN_PWD, strPassword);
        return 0;
    }

    /**
     * 设置锁屏状态
     *
     * @param iStatus - 0-已解锁； 1-已锁上
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public int setLockscreenStatus(int iStatus) throws RemoteException {
        String lockStatus = (iStatus == 1) ? HHTConstant.LOCK_STATUS_ON : HHTConstant.LOCK_STATUS_OFF;
        SystemProperties.set(HHTConstant.LOCK_SCREEN, lockStatus);
        return 0;
    }

    /**
     * 设置遥控锁
     *
     * @param bLock - true->enable; false->disable
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean setRemoteIrLock(boolean bLock) throws RemoteException {
        String lockStatus = bLock ? HHTConstant.LOCK_STATUS_ON : HHTConstant.LOCK_STATUS_OFF;
        SystemProperties.set(HHTConstant.LOCK_IR, lockStatus);
        return true;
    }

    /**
     * 设置触摸锁
     *
     * @param bLock - true->enable; false->disable
     * @return 0->成功， -1 -> 失败
     */
    @Override
    public boolean setTouchLock(boolean bLock) throws RemoteException {
        String lockStatus = bLock ? HHTConstant.LOCK_TOUCH_STATUS_ON : HHTConstant.LOCK_TOUCH_STATUS_OFF;
        SystemProperties.set(HHTConstant.LOCK_TOUCH, lockStatus);
        return true;
    }

    /**
     * U盘秘钥锁动作设置
     *
     * @param bLock - true->enable; false->disable
     * @return
     */
    @Override
    public boolean setUsbKeyLock(boolean bLock) throws RemoteException {
        String lockStatus = bLock ? HHTConstant.LOCK_STATUS_ON : HHTConstant.LOCK_STATUS_OFF;
        SystemProperties.set(HHTConstant.LOCK_USB, lockStatus);
        return true;
    }

    @Override
    public boolean setUsbKeyLockedEnable(boolean bEnable) throws RemoteException {
        boolean isShowLockUI = true;

        if(bEnable){
            SystemProperties.set(HHTConstant.LOCK_USB_KEY_ENABLE, HHTConstant.LOCK_STATUS_ON);
            SystemProperties.set(HHTConstant.LOCK_TOUCH, HHTConstant.LOCK_TOUCH_STATUS_ON);
            checkUSBKeyFile(true);
        } else {
            SystemProperties.set(HHTConstant.LOCK_USB_KEY_ENABLE, HHTConstant.LOCK_STATUS_OFF);
            checkUSBKeyFile(false);
        }

        // 启动锁USB UI
        if (isShowLockUI) {
            try {
                Intent intent = new Intent("com.cultraview.settings.action.usblock");
                intent.setComponent(new ComponentName("com.ctv.settings", "com.ctv.settings.security.activity.USBLockService"));
//            Intent intent = new Intent(context, USBLockService.class);
                intent.putExtra("usblock", "setting_switch");
                mContext.startService(intent);
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(mContext, "There is not exist Lock USB View", Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }
    /**
     * U盘文件检测
     * */
    private void checkUSBKeyFile(boolean isUsbLocked) {
        if (!isUsbLocked) {
            SystemProperties.set(HHTConstant.LOCK_USB, HHTConstant.LOCK_STATUS_OFF);
        } else {
            File usb = new File("/mnt/usb");
            final File[] files = usb.listFiles();
            checkCheckFileRet(files);
            if (mCheckFileRet) { // 关闭U盘锁，关闭触摸锁
                SystemProperties.set(HHTConstant.LOCK_USB, HHTConstant.LOCK_STATUS_OFF);
                SystemProperties.set(HHTConstant.LOCK_TOUCH, HHTConstant.LOCK_TOUCH_STATUS_OFF);
            } else { // 打开U盘锁，打开触摸锁
                SystemProperties.set(HHTConstant.LOCK_USB, HHTConstant.LOCK_STATUS_ON);
                SystemProperties.set(HHTConstant.LOCK_TOUCH, HHTConstant.LOCK_TOUCH_STATUS_ON);
            }
            mCheckFileRet = false;
        }
    }
    /**
     * 检测U盘中的文件USB key
     * @param files
     */
    private void checkCheckFileRet(File[] files) {
        if (files == null || files.length <= 0) {
            mCheckFileRet = false;
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                checkCheckFileRet(file.listFiles());
            } else {
                String fileName = file.getName();
                String filePath = file.getPath().toString();
                if (fileName.equals(FILE_NAME)) {
                    if (FILE_CONTENT.equals(readFile(filePath))) {
                        mCheckFileRet = true;
                        return;
                    }
                }
            }
        }
    }

    /**
     * 读文件内容
     * @param fileName
     * @return
     */
    private String readFile(String fileName) {
        File file = new File(fileName);
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            //String res = EncodingUtils.getString(buffer, "UTF-8");
            String res  = new String(buffer, "UTF-8");
            fis.close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭前置面板
     * 前置面板按键白名单
     * 白名单中的按键不禁止
     *
     * @param lock
     * @param lockWhiteList keycode
     */
    @Override
    public void setKeypadLock(boolean lock, int[] lockWhiteList) throws RemoteException {
        // set keypad lock
        String lockStatus = lock ? HHTConstant.LOCK_STATUS_ON : HHTConstant.LOCK_STATUS_OFF;
        SystemProperties.set(HHTConstant.LOCK_KEYPAD, lockStatus);

        // set keypad whitelist
        String value = "";

        if (lockWhiteList != null) {
            value = Arrays.toString(lockWhiteList);
        }

        SystemProperties.set(HHTConstant.LOCK_KEYPAD_WHITELIST, value);
    }

    /**
     * 关闭ops触控
     *
     * @param closeOpsTouch
     */
    @Override
    public void setOpsTouchLock(boolean closeOpsTouch) {
        String cmdStr = closeOpsTouch ? TvosCommand.TVOS_COMMON_CMD_DISABLE_OPS_TOUCH : TvosCommand.TVOS_COMMON_CMD_ENABLE_OPS_TOUCH;
        Tools.sendCommand(cmdStr);
    }

    /**
     * 获取ops触控开关
     * return true 关闭；false 开启
     */
    @Override
    public boolean isOpsTouchLock() throws RemoteException {
        return Tools.getResultFromTvOS(TvosCommand.TVOS_COMMON_CMD_GET_OPS_TOUCH_STATE) == 1;
    }
}
