package com.ctv.settings.general;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;

import com.ctv.settings.utils.Tools;
import com.cultraview.tv.CtvDatabaseManager;
import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.device.HHTCommonManager.EnumEyeProtectionMode;

import java.io.File;
import java.io.FileInputStream;

;import static com.hht.android.sdk.device.HHTCommonManager.EnumEyeProtectionMode.*;

public class GreneralUtils {
    public static GreneralUtils instance = null;
    private static Context mContext;
    private static boolean mCheckFileRet = false;
    private static final String USB_LOCK = "persist.sys.usbLock";
    private static final String USB_LOCK_STAUS = "persist.sys.usbLock.status";
    private static final String FILE_NAME = "usblock.bat";
    private static final String FILE_CONTENT = "666ctv888";
    private GreneralUtils(){};
    public static GreneralUtils getInstance(Context context) {
        mContext = context;
        if (instance == null){
            synchronized (GreneralUtils.class){
                instance = new GreneralUtils();
            }
        }
        return instance;
    }
/**
*   信号自动跳转状态获取
* */
    public boolean getSignalSwitchModeStatus() {
        long ret = -1;
        int bAutoSourceSwitch = -1;
        int bSourceDetectEnable = -1;
        Cursor cursor = mContext.getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            bAutoSourceSwitch = cursor.getInt(cursor.getColumnIndex("bAutoSourceSwitch"));
            bSourceDetectEnable = cursor.getInt(cursor.getColumnIndex("bSourceDetectEnable"));
        }
        cursor.close();
        if (bAutoSourceSwitch == 1) {
            return true;
        }
        return false;
    }
    /**
     *   信号自动跳转开关设置
     * */
    public void setSignalSwitchModeEnable(boolean enable) {
        int bAutoSourceSwitch = 0;
        if(enable){
            bAutoSourceSwitch = 1;
        }
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("bAutoSourceSwitch", bAutoSourceSwitch);
        try {
            ret = mContext.getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {

        }
        if (ret == -1) {
           // Log.d("chen", "updateSignalSwitchMode failed");
            return;
        }
        try {
            CtvDatabaseManager.getInstance().setDatabaseDirtyByApplication((short) 0x19);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * USB共享状态获取
     * */
    public String getUsbShareStatus(){
        String isShareUsb = SystemProperties.get("persist.sys.share_usb_mode","0");
        return isShareUsb;
    }

    /**
     * 耳机插入检测
     * */
    public Boolean getCheckEarphoneStatus(){
        String isShareUsb = SystemProperties.get("persist.sys.earphone_check","0");
        return isShareUsb.equals("1");
    }
    /**
     * 节能息屏状态获取
     * */
    public boolean getBackLightStatus(){
        int aloneBackLight =  Settings.System.getInt(mContext.getContentResolver(), "isSeperateHear", 1);
        return aloneBackLight==1;
    }

    /**
     * 五指息屏状态获取
     * */
    public boolean getFiveTouchModeStatus(){
        int five_touch = Settings.System.getInt(mContext.getContentResolver(), "FIVE_TOUCH", 0);
        return five_touch == 1;
    }

    /**
     * 五指息屏开关设置
     * */
    public void setFiveTouchModeEnable(boolean enable){
        Settings.System.putInt(mContext.getContentResolver(), "FIVE_TOUCH",enable?1:0);
    }

    /**
     * 四分屏状态获取
     * */

    public boolean getFourScreenStatus(){
        int four_screen_status = Settings.System.getInt(mContext.getContentResolver(), "four_screen",0);
        return four_screen_status == 1;
    }

    /**
     * 四分屏开关设置
     * */

    public void setFourScreenEnable(boolean enable){
        Settings.System.putInt(mContext.getContentResolver(), "four_screen",enable?1:0);
        mContext.sendBroadcast(new Intent("refresh_ui"));
    }
   /**
    * 第三方软件安装权限状态获取
    * */
   @SuppressWarnings("deprecation")
   public boolean getNonMarketAppsAllowedStatus() {
       return Settings.Secure.getInt(mContext.getContentResolver(),
               Settings.Secure.INSTALL_NON_MARKET_APPS, 0) > 0;
   }

    /**
     * 第三方软件安装权限开关设置
     * */
    @SuppressWarnings("deprecation")
    public static void setNonMarketAppsAllowedEnable(boolean enabled) {
        Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.INSTALL_NON_MARKET_APPS, enabled ? 1 : 0);
    }

    /**
     * 锁屏设置
     * */
    public void openLockScreenFunction(){

    }

    /**
     * 处理dialog
     *
     * @param dialog
     */
    private void handleDialog(Dialog dialog) {
        //isShow(false);
    }
    /**
     * U盘锁界面跳转
     */
    public void openUsbLockFunction(){

    }
    /**
     * U盘锁状态获取
     */
    public boolean getUsbLockStatus(){
       String status = SystemProperties.get(USB_LOCK_STAUS,"off");
       if("on".equals(status)){
           return true;
       }
       return false;
    }

    /**
     * U盘锁开关设置
     */
    public void setUsbLockEnable(boolean enable){
        if(enable){
            SystemProperties.set(USB_LOCK_STAUS, "on");
            checkFile(true, true);
        }else{
            SystemProperties.set(USB_LOCK_STAUS, "off");
            checkFile(false, true);
        }
    }

    /**
     * U盘文件检测
     * */
    public  void checkFile(boolean flag, boolean flag1) {
        if (!flag) {
            SystemProperties.set("persist.sys.usbLock", "off");
        } else {
            File usb = new File("/mnt/usb");
            final File[] files = usb.listFiles();
            getFileName(files);
            if (mCheckFileRet) {
                SystemProperties.set("persist.sys.usbLock", "off");
            } else {
                SystemProperties.set("persist.sys.usbLock", "on");
            }
            mCheckFileRet = false;
        }
       /* if (flag1) {
            Intent intent = new Intent(mContext, USBLockService.class);
            intent.putExtra("usblock", "setting_switch");
            mContext.startService(intent);
        }*/
    }

    public static void getFileName(File[] files) {
        if (files == null || files.length <= 0) {
            mCheckFileRet = false;
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                getFileName(file.listFiles());
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

    public static String readFile(String fileName) {
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
     * 悬浮按钮状态获取
     * */
    public boolean getEasyTouchStatus(){
        int easyTouchOpen = Settings.System.getInt(mContext.getContentResolver(),
                "EASY_TOUCH_OPEN", 1);
        return easyTouchOpen == 1;
    }

    /**
     * 设置护眼+
     * */
    public void setEyePlusStatus(int index){
        GreneralViewHolder.eyePlusStatus = index;
        switch (index){
            case 0:
                index = EYE_OFF.ordinal();
                break;
            case 1:
                index = EYE_PLUS.ordinal();
                break;
            case 2:
                index = EYE_RGB.ordinal();
                break;
//            case 3:
//                index = EYE_WRITE_PROTECT.ordinal();
//                break;
            default:
                break;
        }
        Log.e("eyeMode","index"+index);
        HHTCommonManager.getInstance().setEyeProtectionMode(index);
    }

    /**
     * 获得护眼+状态
     * */
    public boolean getEyePlusStatus(){
        int eyeMode = HHTCommonManager.getInstance().getEyeProtectionMode();
        return eyeMode == EYE_PLUS.ordinal();
    }

    public int getEyePlusIndex(){
        int eyeMode = HHTCommonManager.getInstance().getEyeProtectionMode();
        switch (eyeMode){
//            case 0:
//                eyeMode = 1;
//                break;
//            case 1:
//                eyeMode = 1;
//                break;
            case 2:
                eyeMode = 2;
                break;
            default:
                break;
        }
        Log.e("eyeMode","eyeMode"+eyeMode);
        return eyeMode;
    }
    /**
     * 设置带光感
     * */
    public void setEyePlusStatus_Light(int index){
        GreneralViewHolder.eyePlusStatus = index;
        switch (index){
            case 0:
                index = EYE_OFF.ordinal();
                break;
            case 1:
                index = EYE_DIMMING.ordinal();
                break;
            case 2:
                index = EYE_PLUS.ordinal();
                break;
            case 3:
                index = EYE_RGB.ordinal();
                break;
//            case 3:
//                index = EYE_WRITE_PROTECT.ordinal();
//                break;
            default:
                break;
        }
        Log.e("eyeMode","index"+index);
        HHTCommonManager.getInstance().setEyeProtectionMode(index);
    }
    public int getEyePlusIndex_Light(){
        int eyeMode = HHTCommonManager.getInstance().getEyeProtectionMode();
        switch (eyeMode){
//            case 0:
//                eyeMode = 1;
//                break;
            case 1:
                eyeMode = 1;
                break;
            case 2:
                eyeMode = 3;
                break;
            case 3:
                eyeMode = 2;
                break;
            default:
                break;
        }
        Log.e("eyeMode","eyeMode"+eyeMode);
        return eyeMode;
    }

    /**
     * 悬浮按钮开关设置
     * */
    public void setEasyTouchEnable(boolean enable){
        Settings.System.putInt(mContext.getContentResolver(), "EASY_TOUCH_OPEN",
                enable? 1 : 0);
        if(enable){
            Intent fsIntent = new Intent();
            fsIntent.setComponent(new ComponentName("com.ctv.easytouch",
                    "com.ctv.easytouch.service.FloatWindowService"));
            fsIntent.setAction("com.ctv.easytouch.START_ACTION");
            fsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startService(fsIntent);
        }else{
            Intent fsIntent = new Intent();
            fsIntent.setComponent(new ComponentName("com.ctv.easytouch",
                    "com.ctv.easytouch.service.FloatWindowService"));
            fsIntent.setAction("com.ctv.easytouch.CLOSE_ACTION");
            fsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startService(fsIntent);
        }
    }

    /**
     * 三键合一状态获取
     * */
    public boolean getTripleBondOneStatus(){
        int triple_bond_one = Settings.System.getInt(mContext.getContentResolver(), "triple_bond_one",1);
        return triple_bond_one == 1?true:false;
    }

    /**
     * 三键合一开关设置
     * */
    public void setTripleBondOneEnable(boolean enable){
        Settings.System.putInt(mContext.getContentResolver(), "triple_bond_one",enable ? 1 : 0);
    }

    /**
     * 按键提示音状态获取
     * */
    public boolean getHintSoundStatus(){
        int systemSoundStatus = Tools.getSystemSoundStatus(mContext);
        int hintSoundOpen = Settings.System.getInt(mContext.getContentResolver(),"HINT_SOUND_OPEN", 0);
        return hintSoundOpen == 1?true:false;
    }

    /**
     * 按键提示音状态获取
     * */
    public void setHintSoundEnable(boolean enable){
        Settings.System.putInt(mContext.getContentResolver(), "HINT_SOUND_OPEN",enable ? 1 : 0);
        Tools.setSystemSoundStatus(mContext, enable?1:0);
    }

    /**
     * U盘插拔提示音状态获取
     * */
    public boolean getUsbNotifitionStatus(){
        int usbNotifition=Settings.System.getInt(
                mContext.getContentResolver(), "usb_notifition", 1);
        Settings.System.putInt(mContext.getContentResolver(), "usb_notifition",usbNotifition == 1 ? 0 : 1);
        return usbNotifition == 1?true:false;
    }

    /**
     * U盘插拔提示音开关设置
     * */
    public void  setUsbNotifitionEnable(boolean enable){
        Settings.System.putInt(mContext.getContentResolver(), "usb_notifition",enable ? 1 : 0);
    }
}
