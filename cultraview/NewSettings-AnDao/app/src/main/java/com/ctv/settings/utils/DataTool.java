
package com.ctv.settings.utils;

import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.utils.CtvCommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @Copyright (C), 2015-12-8, CultraView
 * @author Write Macro.Song(songhong@cultraview.com)
 * @since 1.0.0 628/638 DataTool.
 */
public class DataTool {

    public final static boolean IS_AH_EDU_QD = TextUtils.equals(SystemProperties.get("ro.build.display.id", ""), "CN8386_BH_EDU_QD");
    public final static boolean IS_BH_EDU = TextUtils.equals(SystemProperties.get("ro.build.display.id", ""), "CN8386_BH_EDU");

    /**
     * @Title: isEnable3D
     * @Description: DataTool.isEnable3D(ctvContext)
     * @param ctvContext
     * @return Enable3D is 1 return true else return false
     */
    public static boolean isEnable3D(Context ctvContext) {
        boolean show3D = false;
        int curSource = CtvCommonManager.getInstance().getCurrentTvInputSource();
        if (curSource == CtvCommonManager.INPUT_SOURCE_DTV
                || curSource == CtvCommonManager.INPUT_SOURCE_STORAGE
                || curSource == CtvCommonManager.INPUT_SOURCE_HDMI
                || curSource == CtvCommonManager.INPUT_SOURCE_HDMI2
                || curSource == CtvCommonManager.INPUT_SOURCE_HDMI3) {
            show3D = true;

        } else {
            show3D = false;
        }
        if (show3D
                && CtvCommonUtils.getCultraviewProjectInfo(ctvContext, "tbl_Configuration",
                        "Enable3D").equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @Title: isEnableBluetooth
     * @Description: DataTool.isEnableBluetooth(ctvContext)
     * @param ctvContext
     * @return EnableBluetooth is 1 return true else return false
     */
    public static boolean isEnableBluetooth(Context ctvContext) {
        if (CtvCommonUtils.getCultraviewProjectInfo(ctvContext, "tbl_Configuration",
                "EnableBluetooth").equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @Title: isEnableDSP
     * @Description: DataTool.isEnableDSP(ctvContext)
     * @return dsp.configdsp.config is Y return true else return false
     */
    public static boolean isEnableDSP() {
        if (SystemProperties.get("dsp.config").equals("Y")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @Title: setBootLoader
     * @Description: DataTool.setBootLoader(ctvContext,flag)
     * @param ctvContext ;String BootLoader flag 1:Launcher,2:TV
     */
    public static void setBootLoader(Context ctvContext, String flag) {
        CtvCommonUtils
                .setCultraviewProjectInfo(ctvContext, "tbl_Configuration", "BootLoader", flag);
    }

    /**
     * @Title: setGuideMode
     * @Description: DataTool.setGuideMode(ctvContext,flag)
     * @param ctvContext ;String GuideMode flag
     */
    public static void setGuideMode(Context ctvContext, String flag) {
        CtvCommonUtils.setCultraviewProjectInfo(ctvContext, "tbl_Configuration", "GuideMode", flag);
    }

    /**
     * @Title: getBootLoader
     * @Description: DataTool.getBootLoader(ctvContext)
     * @param ctvContext
     * @return String:BootLoader type 1:is Launcher; 2:is TV
     */
    public static String getBootLoader(Context ctvContext) {
        return CtvCommonUtils.getCultraviewProjectInfo(ctvContext, "tbl_Configuration",
                "BootLoader");
    }

    /**
     * @Title: getDeviceName
     * @Description: DataTool.getDeviceName(ctvContext)
     * @param ctvContext
     * @return String: DeviceName
     */
    public static String getDeviceName(Context ctvContext) {
        return CtvCommonUtils.getCultraviewProjectInfo(ctvContext, "tbl_SoftwareVersion",
                "DeviceName");
    }

    /**
     * @Title: getFreeOrCharge
     * @Description: DataTool.getFreeOrCharge(ctvContext)
     * @param ctvContext
     * @return String: FreeOrCharge
     */
    public static String getFreeOrCharge(Context ctvContext) {
        return CtvCommonUtils.getCultraviewProjectInfo(ctvContext, "tbl_Configuration",
                "FreeOrCharge");
    }

    /**
     * @Title: setDeviceName
     * @Description: DataTool.setDeviceName(ctvContext,deviceName)
     * @param ctvContext ;String DeviceName deviceName
     */
    public static void setDeviceName(Context ctvContext, String deviceName) {
        CtvCommonUtils.setCultraviewProjectInfo(ctvContext, "tbl_SoftwareVersion", "DeviceName",
                deviceName);
    }

    /**
     * @Title: getModelName
     * @Description: DataTool.getModelName(ctvContext)
     * @param ctvContext
     * @return String: ModelName
     */
    public static String getModelName(Context ctvContext) {
        return CtvCommonUtils.getCultraviewProjectInfo(ctvContext, "tbl_SoftwareVersion",
                "ModelName");
    }

    /**
     * @Title: getCustomVersion
     * @Description: DataTool.getCustomVersion(ctvContext)
     * @param ctvContext
     * @return String: CustomVersion
     */
    public static String getCustomVersion(Context ctvContext) {
        return CtvCommonUtils.getCultraviewProjectInfo(ctvContext, "tbl_SoftwareVersion",
                "CustomVersion");
    }

    /**
     * @Title: getCustomer
     * @Description: DataTool.getCustomer(ctvContext)
     * @param ctvContext
     * @return String: Customer
     */
    public static String getCustomer(Context ctvContext) {
        String customer = "";
        StringTokenizer versioninfo = new StringTokenizer(getMainVersion(ctvContext), "-");
        if (versioninfo.hasMoreElements()) {
            customer = versioninfo.nextToken();
        } else {
            customer = "";
        }
        return customer;
    }

    /**
     * @Title: getMainVersion
     * @Description: DataTool.getMainVersion(ctvContext)
     * @param ctvContext
     * @return the string of MainVersion
     */
    public static String getMainVersion(Context ctvContext) {
        return CtvCommonUtils.getCultraviewProjectInfo(ctvContext, "tbl_SoftwareVersion",
                "MainVersion");
    }

    /**
     * // 2014_1017_songhong
     * 
     * @param : context
     * @return String: Country 1."CHINA" on behalf of the domestic
     */
    public static String getCountry(Context context) {
        return CtvCommonUtils.getCultraviewProjectInfo(context, "tbl_SoftwareVersion", "Country");

    }

    /**
     * 2017_01_11_yangqiuling
     * 
     * @return boolean :true,hdcp2KEY exist Mcast enable.false,Mcast disable.
     */
    public static Boolean isEnableMcast() {
        // TODO Auto-generated method stub
        boolean showWifiDisplay = false;
        File mHDCPKeyFile = new File("/factory/CtvHDCPKey.bin");
        if (mHDCPKeyFile != null && mHDCPKeyFile.exists()) {
            if ((getFileSize(mHDCPKeyFile) > 500 && getFileSize(mHDCPKeyFile) < 1500)) {
                showWifiDisplay = true;
            } else {
                showWifiDisplay = false;
            }
        } else {
            showWifiDisplay = false;
        }

        return showWifiDisplay;
    }

    private static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            try {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Log.e("UpdatHdcpKey", "file not exsit!");
        }
        Log.i("UpdatHdcpKey", "file size = " + size);
        return size;
    }

    public static boolean isAHBoard() {
        boolean isAH = false;
        String board = SystemProperties.get("ro.build.display.id", "CN8386_AH_MTG");
        String[] tmp = board.split("_");
        if (tmp.length >= 3) {
            isAH = tmp[1].equals("AH");
        }
        return isAH;
    }
}
