
package mstar.factorymenu.ui.utils;


import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.cultraview.tv.CtvFactoryManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import mstar.tvsetting.factory.desk.IFactoryDesk;

public class USBUpgradeThread {
    private static String mac_ethAddr = "macaddr";
    private static File hdcpKeyFile, ciplusKeyFile;


    static int UPGRATE_END_FAIL = 0;

    public static int UPGRATE_END_SUCCESS = 1;

    public static int UPGRATE_END_FILE_NOT_FOUND = 2;

    public static int UPGRATE_END_FIFL_ALREADY_UP_TO_DATE = 3;

    public static int UPGRATE_END_SUCCESS_MAIN = 4;

    public static int UPGRATE_END_SUCCESS_MAC = 5;

    public static int UPGRATE_START = 6;

    public static int UPGRATE_TYPEC_FAILED = 10;

    public static int UPGRATE_TYPEC_SUCCESS = 11;

    public static int UPGRATE_HDMI_OUT_FAILED = 12;

    public static int UPGRATE_HDMI_OUT_SUCCESS = 13;

    public static int UPGRATE_END_SUCCESS_WIFI_MAC = 7;
    public static String UpgradeFilePath = "/UpgradePanel/";
    public static String UpgradeTypeCFile = "/typec/";

    private static final String TVOS_INTERFACE_CMD_LED_BREATH_ON = "LedBreathOn";

    public enum EnumUpgradeStatus {
        // status fail
        E_UPGRADE_FAIL,
        // status success
        E_UPGRADE_SUCCESS,
        // file not found
        E_UPGRADE_FILE_NOT_FOUND,
        // File is already up to date
        E_UPGRADE_FILE_ALREADY_UP_TO_DATE,
    }

    public static int UPGRATE_PANELOVER = 10;

    public static boolean UpgradeMAC(final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (handler != null) {
                    int upgrate_status;
                    handler.sendEmptyMessage(UPGRATE_START);
                    upgrate_status = UpgradeMAC();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (upgrate_status == EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal()) {
                        handler.sendEmptyMessage(UPGRATE_END_SUCCESS_MAC);
                    } else if (upgrate_status == EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND
                            .ordinal()) {
                        handler.sendEmptyMessage(UPGRATE_END_FILE_NOT_FOUND);
                    } else if (upgrate_status == EnumUpgradeStatus.E_UPGRADE_FILE_ALREADY_UP_TO_DATE
                            .ordinal()) {
                        handler.sendEmptyMessage(
                                UPGRATE_END_FIFL_ALREADY_UP_TO_DATE);
                    } else {
                        handler.sendEmptyMessage(UPGRATE_END_FAIL);
                    }
                }
            }
        }).start();
        return true;
    }

    static int UpgradeMAC() {
        int ret = 0;
        String directoryName = checkFileIsExist("/mnt/usb", "MAC");
        try {
            File listMacfile = new File(directoryName);
            if (listMacfile.exists()) {
                File[] files = listMacfile.listFiles();
                File macfile = files[0];
                StringBuffer sb = new StringBuffer();
                try {
                    if (macfile.exists()) {
                        FileInputStream in = new FileInputStream(macfile);
                        int len = 1;
                        byte[] temp = new byte[len];
                        while (in.read(temp) != -1) {
                            appendHexPair(temp[0], sb);
                            sb.append(":");
                        }
                        in.close();
                    } else {
                        ret = EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
                    }
                    String tempMac = sb.toString();
                    LogUtils.d("tempMac =" + tempMac);
                    String macaddr = tempMac.substring(0, tempMac.length() - 1);
                    LogUtils.d("macaddr =" + macaddr);
                    String existsMac = CtvTvManager.getInstance().getEnvironment(mac_ethAddr);
                    LogUtils.d("existsMac =" + existsMac);
                    if (existsMac.trim().equals(macaddr) || existsMac.trim() == macaddr) {
                        macfile.delete();
                    } else {
                        boolean macResult = CtvTvManager.getInstance().setEnvironment(mac_ethAddr,
                                macaddr);
                        if (macResult) {
                            String successMac = CtvTvManager.getInstance().getEnvironment(
                                    mac_ethAddr);
                            if (successMac.trim().equals(macaddr) || successMac.trim() == macaddr) {
                                macfile.delete();
                                //macaddress = successMac;
                                // TODO: 2020/3/30 刷新MAC
                                ret = EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal();
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    ret = EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
                    e.printStackTrace();
                } catch (IOException e) {
                    ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                    e.printStackTrace();
                }
            } else {
                ret = EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
            }
        } catch (Exception e) {
            ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
            e.printStackTrace();
            LogUtils.e("E_UPGRADE_FAIL:" + e);
        }
        return ret;
    }

    private static String checkFileIsExist(String path, String fileName) {
        File usbfile = new File(path);
        File[] flist = usbfile.listFiles();
        String filePath = "";

        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory() && flist[i].canRead()) {
                String sdname = flist[i].getName();
                String checkfilename = path + "/" + sdname + "/" + fileName;
                File file = new File(checkfilename);
                if (file.exists() && file.canRead()) {
                    filePath = checkfilename;
                    break;
                }
            }
        }
        return filePath;
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    // HDCP1.4 key for HDMI 1.4 Rx
    public static boolean writeHDCP14Key(Context mContext) {
        String directoryName = checkFileIsExist("/mnt/usb", "Ctv_HDCP14_Key");
        File hdcpKeyFile = null;
        if (directoryName != null && !directoryName.equals("")) {
            File hdcpKeyOnBoad = new File("/factory/hdcp_key.bin");
            if (hdcpKeyOnBoad != null && hdcpKeyOnBoad.exists() && (getFileSize(hdcpKeyOnBoad) == 304)) {
                Toast.makeText(mContext, "HDCP1.4 key already exists!", Toast.LENGTH_LONG).show();
                return false;
            }
            File[] files = new File(directoryName).listFiles();
            Pattern pattern = Pattern.compile("^CtvHDCP14Key.*\\.bin$");
            for (int i = 0; i < files.length; i++) {
                if (pattern.matcher(files[i].getName()).matches()) {
                    hdcpKeyFile = files[i];
                    LogUtils.d("TAG", "hdcpKeyFile=" + hdcpKeyFile.getName());
                    break;
                }
            }
            if (hdcpKeyFile != null && hdcpKeyFile.exists()) {
                if (getFileSize(hdcpKeyFile) < 280 || getFileSize(hdcpKeyFile) > 600) {
                    Toast.makeText(mContext, "Specified HDCP1.4 key File Size Error, please check it: " + hdcpKeyFile.getName(), Toast.LENGTH_LONG).show();
                    return false;
                }

                //boolean ret = copyFile(hdcpKeyFile, hdcpKeyOnBoad);
                boolean ret = false;
                try {
                    ret = TvManager.getInstance().setTvosInterfaceCommand("CopyHDCP14Key" + " " + hdcpKeyFile.getPath());
                } catch (TvCommonException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (ret) {
                    if (checkMd5(hdcpKeyOnBoad, hdcpKeyFile)) {
                        hdcpKeyFile.delete();
                        Toast.makeText(mContext, "HDCP1.4 Key Upgrade OK!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "HDCP1.4 Key Upgrade Fail: checkMd5 NG.", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Toast.makeText(mContext, "HDCP1.4 Key Upgrade Fail: copyFile NG.", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                Toast.makeText(mContext, "HDCP1.4 Key is not found!", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            //Toast.makeText(mContext, "HHDCP1.4 Key Folder is not found!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // HDCP2.2 key for HDMI 2.0 Rx and Miracast RX
    public static boolean writeHDCP22Key(Context mContext) {
        String directoryName = checkFileIsExist("/mnt/usb", "Ctv_HDCP_Key");
        File hdcpKeyFile = null;
        if (directoryName != null && !directoryName.equals("")) {
            LogUtils.d("TAG", "directoryName=" + directoryName);
            File hdcpKeyOnBoad = new File("/factory/CtvHDCPKey.bin");
            if (hdcpKeyOnBoad != null && hdcpKeyOnBoad.exists()) {
                Toast.makeText(mContext, "HDCP2.2 key already exists!", Toast.LENGTH_LONG).show();
                return false;
            }
            File[] files = new File(directoryName).listFiles();
            Pattern pattern = Pattern.compile("^CtvHDCPKey.*\\.bin$");
            LogUtils.d("TAG", "files=" + files.length);
            for (int i = 0; i < files.length; i++) {
                if (pattern.matcher(files[i].getName()).matches()) {
                    hdcpKeyFile = files[i];
                    LogUtils.d("TAG", "hdcpKeyFile=" + hdcpKeyFile.getName());
                    break;
                }
            }
            if (hdcpKeyFile != null && hdcpKeyFile.exists()) {
                if (getFileSize(hdcpKeyFile) < 1000 || getFileSize(hdcpKeyFile) > 1288) {
                    Toast.makeText(mContext, "Specified HDCP2.2 key File Size Error, please check it: " + hdcpKeyFile.getName(), Toast.LENGTH_LONG).show();
                    return false;
                }

                boolean ret = false;
                try {
                    ret = CtvTvManager.getInstance().setTvosInterfaceCommand("CopyHDCPKey" + " " + hdcpKeyFile.getPath());
                } catch (CtvCommonException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (ret) {
                    if (checkMd5(hdcpKeyOnBoad, hdcpKeyFile)) {
                        hdcpKeyFile.delete();
                        Toast.makeText(mContext, "HDCP2.2 Key Upgrade OK!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "HDCP2.2 Key Upgrade Fail: checkMd5 NG.", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Toast.makeText(mContext, "HDCP2.2 Key Upgrade Fail: copyFile NG.", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                Toast.makeText(mContext, "HDCP2.2 Key is not found!", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            //Toast.makeText(mContext, "HDCP2.2 Key Folder is not found!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public static int UpgradeHDCPKey() {
        int ret = 0;
        boolean bRet = false;
        String directoryName = checkFileIsExist("/mnt/usb", "Ctv_HDCP_Key");
        if (directoryName != null && !directoryName.equals("")) {
            File[] files = new File(directoryName).listFiles();
            Pattern pattern = Pattern.compile("^CtvHDCPKey.*\\.bin$");
            for (int i = 0; i < files.length; i++) {
                if (pattern.matcher(files[i].getName()).matches()) {
                    hdcpKeyFile = files[i];
                    LogUtils.d("TAG", "hdcpKeyFile=" + hdcpKeyFile.getName());
                    break;
                }
            }
            if (hdcpKeyFile != null && hdcpKeyFile.exists()) {
                if (getFileSize(hdcpKeyFile) < 500 || getFileSize(hdcpKeyFile) > 1500) {
                    renameFile(directoryName, hdcpKeyFile.getName(),
                            "error" + hdcpKeyFile.getName());
                    LogUtils.d("qkmin---->hdcpKeyFile getFileSize <500  >1500");

                    ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                    return ret;
                }
                try {
                    bRet = CtvTvManager.getInstance().setTvosInterfaceCommand(
                            "CopyHDCPKey " + hdcpKeyFile.getPath());
                } catch (CtvCommonException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                File hdcpKeyOnBoad = new File("/factory/CtvHDCPKey.bin");
                if (bRet) {
                    if (checkMd5(hdcpKeyFile, hdcpKeyOnBoad)) {
                        hdcpKeyFile.delete();
                        ret = EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal();
                    } else {
                        if (hdcpKeyOnBoad.exists()) {
                            LogUtils.d("TAG", "Delet hdcpkey file on board!");
                            try {
                                CtvTvManager.getInstance().setTvosInterfaceCommand(
                                        "DeletHDCPKeyFile");
                            } catch (CtvCommonException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        LogUtils.d("qkmin---->ret:" + ret);
                        ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                    }
                } else {
                    if (hdcpKeyOnBoad.exists()) {
                        LogUtils.d("TAG", "Delet hdcpkey file on board!");
                        try {
                            CtvTvManager.getInstance().setTvosInterfaceCommand("DeletHDCPKeyFile");
                        } catch (CtvCommonException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                }
            } else {
                ret = EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
            }
        } else {
            ret = EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
        }
        return ret;
    }

    private static void renameFile(String path, String oldname, String newname) {
        if (!oldname.equals(newname)) {
            File oldfile = new File(path + "/" + oldname);
            File newfile = new File(path + "/" + newname);
            if (!oldfile.exists()) {
                return;
            }
            if (newfile.exists())
                Log.e("TAG", "rename error!" + newname + " is exsit!");
            else {
                oldfile.renameTo(newfile);
            }
        } else {
            Log.e("TAG", "rename error!old name is equal new name.");
        }
    }


    private static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            try {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
                fis.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Log.e("TAG", "file not exsit!");
        }
        LogUtils.d("TAG", "file size = " + size);
        return size;
    }

    private static boolean checkMd5(File oriFile, File desFile) {
        String oriHdcpKeyMd5, desHdcpKeyMd5;
        boolean ret = false;
        try {
            oriHdcpKeyMd5 = getFileMD5String(oriFile);
            desHdcpKeyMd5 = getFileMD5String(desFile);
            LogUtils.d("TAG", "oriHdcpKeyMd5:" + oriHdcpKeyMd5);
            LogUtils.d("TAG", "desHdcpKeyMd5:" + desHdcpKeyMd5);
            if (oriHdcpKeyMd5.equals(desHdcpKeyMd5)) {
                ret = true;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("TAG", "checkMd5 ret =" + ret);
        return ret;
    }

    public static String getFileMD5String(File file) throws IOException {
        MessageDigest messagedigest;
        try {
            messagedigest = MessageDigest.getInstance("MD5");

            FileInputStream in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            messagedigest.update(byteBuffer);
            in.close();
            return bufferToHex(messagedigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    public static boolean UpgradeHDCP(final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (handler != null) {
                    int upgrate_status;
                    handler.sendEmptyMessage(UPGRATE_START);
                    upgrate_status = UpgradeHDCPKey();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (upgrate_status == EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal()) {
                        handler.sendEmptyMessage(UPGRATE_END_SUCCESS);
                    } else if (upgrate_status == EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND
                            .ordinal()) {
                        handler.sendEmptyMessage(UPGRATE_END_FILE_NOT_FOUND);
                    } else if (upgrate_status == EnumUpgradeStatus.E_UPGRADE_FILE_ALREADY_UP_TO_DATE
                            .ordinal()) {
                        handler.sendEmptyMessage(
                                UPGRATE_END_FIFL_ALREADY_UP_TO_DATE);
                    } else {
                        handler.sendEmptyMessage(UPGRATE_END_FAIL);
                    }
                }
            }
        }).start();
        return true;
    }

    /**
     * 更新屏幕参数
     *
     * @return
     */
    public static boolean UpgradePanelConfigs(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int upgrate_status;
                upgrate_status = UpgradePanelConfigs2(path);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogUtils.d("UpgradePanelConfigs：" + upgrate_status);
            }

        }).start();
        return true;
    }

    public static String FindUSB() {
        String filepath = "";
        File usbroot = new File("/mnt/usb/");
        File targetfile;
        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();
            for (int sdx = 0; sdx < usbitems.length; sdx++) {
                if (usbitems[sdx].isDirectory()) {
                    filepath = usbitems[sdx].getPath();
                }
            }
        }
        LogUtils.d("FindUSB == " + filepath);
        return filepath;
    }

    public static String FindFolderOnUSB() {
        String filepath = "";
        File usbroot = new File("/mnt/usb/");
        File targetfile;
        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();
            for (int sdx = 0; sdx < usbitems.length; sdx++) {
                if (usbitems[sdx].isDirectory()) {
                    targetfile = new File(usbitems[sdx].getPath() + UpgradeFilePath);
                    if (!targetfile.exists() || !targetfile.isDirectory()) {
                        if (targetfile.mkdir()) {
                            filepath = targetfile.getPath();
                            LogUtils.d("filepath1 == " + filepath);
                        } else {
                            LogUtils.e("create export file fall!!");
                        }
                        break;
                    } else {
                        filepath = targetfile.getPath();
                        break;
                    }
                }
            }
        }
        LogUtils.d("filepath2 == " + filepath);
        return filepath;
    }


    public static String FindTypeCFolderOnUSB() {
        String filepath = "";
        File usbroot = new File("/mnt/usb/");
        File targetfile;
        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();
            for (int sdx = 0; sdx < usbitems.length; sdx++) {
                if (usbitems[sdx].isDirectory()) {
                    targetfile = new File(usbitems[sdx].getPath() + UpgradeTypeCFile);
                    if (!targetfile.exists() || !targetfile.isDirectory()) {
                        if (targetfile.mkdir()) {
                            filepath = targetfile.getPath();
                            LogUtils.d("filepath1 == " + filepath);
                        } else {
                            LogUtils.e("create export file fall!!");
                        }
                        break;
                    } else {
                        filepath = targetfile.getPath();
                        break;
                    }
                }
            }
        }
        LogUtils.d("filepath2 == " + filepath);
        return filepath;
    }


    public static int UpgradePanelConfigs2(String CTV_USB_EU) {
        int ret = 0;
        String[] temp = null;
        String CUSTOMER_INI_PATH = CtvFactoryManager.getInstance().getStringFromIni(IFactoryDesk.SYS_INI_PATH,
                IFactoryDesk.CUSTOMER_INI_KEY);
        temp = CUSTOMER_INI_PATH.split("/");
        String CUSTOMER_INI_NAME = temp[temp.length - 1];

        String PANEL_INI_PATH = CtvFactoryManager.getInstance().getStringFromIni(CUSTOMER_INI_PATH,
                IFactoryDesk.PANEL_INI_KEY);
        temp = PANEL_INI_PATH.split("/");
        String PANEL_INI_NAME = temp[temp.length - 1];

        File panel_OnUsb = new File(CTV_USB_EU, PANEL_INI_NAME);
        File customer_OnUsb = new File(CTV_USB_EU, CUSTOMER_INI_NAME);
        // should not change this file name
        // ------------------------------------//
        File panel_Onboard = new File(PANEL_INI_PATH);
        File customer_Onboard = new File(CUSTOMER_INI_PATH);

        if (panel_OnUsb.exists() || customer_OnUsb.exists()) {

            try {
                ret = CtvTvManager.getInstance().copyCmDb(CTV_USB_EU + "/" + PANEL_INI_NAME,
                        PANEL_INI_PATH);
                if (ret == -1) {
                    return USBUpgradeThread.EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                }
                ret = CtvTvManager.getInstance().copyCmDb(CTV_USB_EU + "/" + CUSTOMER_INI_NAME,
                        CUSTOMER_INI_PATH);
                if (ret != -1) {
                    ret = USBUpgradeThread.EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal();
                } else {
                    ret = USBUpgradeThread.EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                }
            } catch (CtvCommonException e) {
                ret = USBUpgradeThread.EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                e.printStackTrace();
            }

        } else {
            ret = USBUpgradeThread.EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
        }
        return ret;
    }


    /**
     * 更新PQ
     *
     * @param
     * @param handler
     * @return
     */
    public static int UpgradePQ(String CTV_USB_EU, Handler handler) {
        int ret = 0;
        File Main_OnUsb = new File(CTV_USB_EU + "/Main.bin");
        File Main_Text_OnUsb = new File(CTV_USB_EU + "/Main_Text.bin");

        File Main_Color_OnUsb = new File(CTV_USB_EU + "/Main_Color.bin");
        File Main_Color_Text_OnUsb = new File(CTV_USB_EU + "/Main_Color_Text.bin");
        // should not change this file name
        // ------------------------------------//
        File Main_Onboard = new File("/tvconfig/config/pq/", "Main.bin");
        File Main_Text_Onboard = new File("/tvconfig/config/pq/", "Main_Text.bin");

        File Main_Color_Onboard = new File("/tvconfig/config/pq/", "Main_Color.bin");
        File Main_Color_Text_Onboard = new File("/tvconfig/config/pq/", "Main_Color_Text.bin");
        if (Main_OnUsb.exists() && Main_Text_OnUsb.exists()) {
            try {
                ret = CtvTvManager.getInstance().copyCmDb(CTV_USB_EU + "/Main*.bin",
                        "/tvconfig/config/pq/");
                File DLCFile = new File(CTV_USB_EU + "/Main.bin");
                if (DLCFile.exists()) {
                    CtvTvManager.getInstance().copyCmDb(CTV_USB_EU + "/DLC.ini",
                            "/tvconfig/config/DLC/");
                }
                if (ret != -1) {
                    ret = EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal();
                } else {
                    ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                }
            } catch (CtvCommonException e) {
                ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                e.printStackTrace();
            }
        } else {
            ret = EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
        }
        LogUtils.d("UpgradePQ--->ret:" + ret);
        handler.sendEmptyMessage(UPGRATE_PANELOVER);
        return ret;
    }

    public static int UpgradeLogo(String CTV_USB_EU) {
        int ret = 0;
        File boot0_OnUsb = new File(CTV_USB_EU + "/boot0.jpg");
        File boot0_Onboard = new File("/tvconfig/boot0.jpg");

        if (!boot0_OnUsb.exists()) {
            ret = EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
        } else {
            try {
                ret = CtvTvManager.getInstance().copyCmDb(CTV_USB_EU + "/boot0.jpg",
                        "/tvconfig/");
            } catch (CtvCommonException e) {
                e.printStackTrace();
            }
            if (ret != -1) {
                ret = EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal();
            } else {
                ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
            }
        }
        LogUtils.d("UpgradeLogo:" + ret);
        return ret;
    }

    public static boolean UpgradeBootanimation(final String outputDirectory) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int upgrate_status = UpgradeBootanimation2(outputDirectory);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }).start();
        return true;
    }

    static int UpgradeBootanimation2(String CTV_USB_EU) {
        int ret = 0;
        File bootanimation_OnUsb = new File(CTV_USB_EU + "/bootanimation.zip");

        // should not change this file name
        // ------------------------------------//
        File factory_bootanimation = new File("/factory/bootanim/bootanimation.zip");

        if (!factory_bootanimation.exists() || !bootanimation_OnUsb.exists()) {
            ret = EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
            LogUtils.e("factory_bootanimation no find ");
        } else {
            try {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ret = CtvTvManager.getInstance().copyCmDb(CTV_USB_EU + "/bootanimation.zip",
                        "/factory/bootanim/");
                LogUtils.d("factory bootanimation ret:" + ret);
            } catch (CtvCommonException e) {
                e.printStackTrace();
            }
        }


        LogUtils.d("UpgradeBootanimation2 ret:" + ret);
        return ret;
    }

    public static int UpgradeDatabase(String CTV_USB_EU) {
        int ret = 0;
        File factory_OnUsb = new File(CTV_USB_EU + "/factory.db");
        File user_setting_OnUsb = new File(CTV_USB_EU + "/user_setting.db");
        if (factory_OnUsb.exists() || user_setting_OnUsb.exists()) {
            try {
                ret = CtvTvManager.getInstance().copyCmDb(CTV_USB_EU + "/*.db",
                        "/tvdatabase/Database/");
                CtvTvManager.getInstance().copyCmDb(CTV_USB_EU + "/*.db",
                        "/tvdatabase/DatabaseBackup/");
                CtvTvManager.getInstance().copyCmDb(CTV_USB_EU + "/*.db",
                        "/tvconfig/TvBackup/Database/");
                if (ret != -1) {
                    ret = EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal();
                } else {
                    ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                }
            } catch (CtvCommonException e) {
                ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                e.printStackTrace();
            }
        } else {
            ret = EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
        }
        LogUtils.d("UpgradeDatabase--->ret:" + ret);
        return ret;
    }

    /**
     * 解压assets的zip压缩文件到指定目录
     *
     * @throws IOException
     */
    public static void unzipFile(Context context, String zipPtath, String outputDirectory, Handler handler) throws IOException {
        deleteDirWihtFile(new File(outputDirectory));
        try {
            LogUtils.d("开始解压的文件： " + zipPtath + "\n" + "解压的目标路径：" + outputDirectory);
            // 创建解压目标目录
            File file = new File(outputDirectory);
            // 如果目标目录不存在，则创建
            if (!file.exists()) {
                file.mkdirs();
            }
            // 打开压缩文件
            InputStream inputStream = new FileInputStream(zipPtath);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);

            // 读取一个进入点
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            // 使用1Mbuffer
            byte[] buffer = new byte[1024 * 1024];
            // 解压时字节计数
            int count = 0;
            // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {  //如果是一个文件
                    // 如果是文件
                    String fileName = zipEntry.getName();
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);  //截取文件的名字 去掉原文件夹名字
                    file = new File(outputDirectory + File.separator + fileName);  //放到新的解压的文件路径

                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
                // 定位到下一个文件入口
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();

        } catch (Exception e) {
            LogUtils.e("unzip error e:" + e);
            Toast.makeText(context, "打开版控文件失败", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            TvManager.getInstance().setTvosInterfaceCommand("RemountRWTvconfig");
        } catch (Exception e) {
            LogUtils.e("RemountRWTvconfig error:" + e);
        }
        if (!hasUpdatePanelFile(outputDirectory)) {
            Toast.makeText(context, "版控包中没有可用文件", Toast.LENGTH_SHORT).show();
            return;
        }
        USBUpgradeThread.UpgradePanelConfigs(outputDirectory);
        USBUpgradeThread.UpgradeLogo(outputDirectory);
        USBUpgradeThread.UpgradeBootanimation(outputDirectory);
        USBUpgradeThread.UpgradeDatabase(outputDirectory);
        USBUpgradeThread.UpgradePQ(outputDirectory, handler);


    }

    private static boolean hasUpdatePanelFile(final String outputDirectory) {
        File file = new File(outputDirectory);

        File[] listFiles = file.listFiles();
        if (listFiles.length > 0) {
            LogUtils.d("hasUpdatePanelFile has file ");
            File file1 = new File(outputDirectory + "/boot0.jpg");
            File file2 = new File(outputDirectory + "/bootanimation.zip");
            File file3 = new File(outputDirectory + "/UD_VB1_8LANE_M28DGJ_L30.ini");
            File file4 = new File(outputDirectory + "/Customer_1.ini");
            File file5 = new File(outputDirectory + "/DLC.ini");
            File file6 = new File(outputDirectory + "/factory.db");
            File file7 = new File(outputDirectory + "/user_setting.db");
            File file8 = new File(outputDirectory + "/Main.bin");
            File file9 = new File(outputDirectory + "/Main_Text.bin");
            File file10 = new File(outputDirectory + "/Main_Color.bin");
            File file11 = new File(outputDirectory + "/Main_Color_Text.bin");
            File file12 = new File(outputDirectory + "/singleMode.txt");
            if (file1.exists() || file2.exists() || file3.exists() || file4.exists() || file5.exists() || file6.exists()
                    || file7.exists() || file8.exists() || file9.exists() || file10.exists() || file11.exists() || file12.exists()) {
                LogUtils.d("hasUpdatePanelFile has file return true");
                return true;
            }
        } else {
            LogUtils.d("hasUpdatePanelFile not file ");
            return false;
        }
        return false;
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDirWihtFile(file);
        }
        dir.delete();
    }

    /**
     * Type c升级
     *
     * @param path 路径
     */
    public static void UpgradeTypeC(final Handler handler, final String path) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d("UpgradeTypeC path :" + path);
                        try {
                            boolean isSuccess = TvManager.getInstance().setTvosInterfaceCommand("Upgrade_typc_fw#" + path);
                            if (handler != null) {
                                if (!isSuccess) {
                                    //失败
                                    handler.sendEmptyMessage(UPGRATE_TYPEC_FAILED);
                                } else {
                                    //成功
                                    handler.sendEmptyMessage(UPGRATE_TYPEC_SUCCESS);
                                }
                            }
                            LogUtils.d("shorts :" + isSuccess);
                        } catch (TvCommonException e) {
                            e.printStackTrace();
                            LogUtils.e("UpgradeTypeC error :" + e);
                        }
                    }
                }
        ).start();

    }


    /**
     * hdmi out升级
     *
     * @param path 路径
     */
    public static void UpgradeHdmiOut(final Handler handler, final String path) {
        TvCommonManager.getInstance().setTvosCommonCommand(TVOS_INTERFACE_CMD_LED_BREATH_ON);
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d("UpgradeHdmiOut path :" + path);
                        try {
                            boolean isSuccess = TvManager.getInstance().setTvosInterfaceCommand("NovaTekUpgrade#" + path);
                            if (handler != null) {
                                if (isSuccess) {
                                    handler.sendEmptyMessage(UPGRATE_HDMI_OUT_SUCCESS);
                                } else {
                                    handler.sendEmptyMessage(UPGRATE_HDMI_OUT_FAILED);
                                }
                            }
                            LogUtils.d("shorts :" + isSuccess);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.e("UpgradeHdmiOut error :" + e);
                        }
                    }
                }
        ).start();

    }
}
