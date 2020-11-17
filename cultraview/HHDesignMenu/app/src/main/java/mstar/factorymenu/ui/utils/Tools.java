package mstar.factorymenu.ui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.storage.StorageVolume;
import android.util.TypedValue;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvFactoryManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.cultraview.tv.common.vo.CtvEnumInputSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mstar.tvsetting.factory.desk.IFactoryDesk;

public class Tools {

    public static String FindFileOnUSB(String filename) {
        String filepath = "";
        File usbroot = new File("/mnt/usb/");
        File targetfile;
        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();
            for (int sdx = 0; sdx < usbitems.length; sdx++) {
                if (usbitems[sdx].isDirectory()) {
                    targetfile = new File(usbitems[sdx].getPath() + "/" + filename);
                    if (targetfile != null && targetfile.exists()) {
                        filepath = usbitems[sdx].getPath() + "/" + filename;
                        break;
                    }
                }
            }
        }
        return filepath;
    }

    public static boolean CheckUsbIsExist() {
        boolean ret = false;
        File usbfile = new File("/mnt/usb/");
        if (getUsbCout(usbfile) > 0) {
            ret = true;
        }
        return ret;
    }

    private static int getUsbCout(File file) {
        int cout = 0;
        File[] flist = file.listFiles();

        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory() && flist[i].canRead()) {
                // android 6.0 external directory is not contain "sd"
                cout++;
                /*
                 * String sdname = flist[i].getName(); Pattern mPatern =
                 * Pattern.compile("sd+[a-z]{1}+[1-9]{1}"); Matcher mMatcher =
                 * mPatern.matcher(sdname); boolean issd = mMatcher.matches();
                 * if (issd) { cout++; }
                 */
            }
        }
        return cout;
    }

    /**
     * bin文件升级
     */
    public static void UpgradeMain() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (CtvTvManager.getInstance().setEnvironment("upgrade_mode", "usb")) {
                        CtvTvManager.getInstance().setEnvironment("CtvUpgrade_complete", "0");
                    }
                    Thread.sleep(1000);
                    CtvCommonManager.getInstance().rebootSystem("reboot");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String getPath(StorageVolume volume)
            throws Exception {
        Method createBondMethod = volume.getClass().getMethod("getPath");
        String returnValue = (String) createBondMethod.invoke(volume);
        return returnValue;
    }

    public static int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }


    public static String hex2Str(String hex) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String h = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(h, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }


    public static boolean isADCFragmentShow() {
        int currentTvInputSource = CtvCommonManager.getInstance().getCurrentTvInputSource();
        LogUtils.d("currentTvInputSource-->" + currentTvInputSource);
        if (currentTvInputSource == CtvEnumInputSource.E_INPUT_SOURCE_VGA.ordinal()
                || currentTvInputSource == CtvEnumInputSource.E_INPUT_SOURCE_YPBPR.ordinal()) {
            return true;
        } else {
            return false;
        }
    }

    //读取指定目录下的所有TXT文件的文件内容
    public static String getFileContent(File file) {
        String content = "";
        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
            if (file.getName().endsWith("txt")) {//文件格式为""文件
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader
                                = new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line = "";
                        //分行读取
                        while ((line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }
                        instream.close();//关闭输入流
                    }
                } catch (java.io.FileNotFoundException e) {
                    LogUtils.e("The File doesn't not exist.");
                } catch (IOException e) {
                    LogUtils.e(e.getMessage());
                }
            }
        }
        return content;
    }

    public static boolean getFormatName(String fileName) {
        //先判断是否是zip
        fileName = fileName.trim();
        String s[] = fileName.split("\\.");
        if (s.length >= 2) {
            if (!s[s.length - 1].equalsIgnoreCase("zip"))
                return false;
        } else {
            return false;
        }
        String substring = fileName.substring(0, fileName.lastIndexOf("."));
        String[] listName = substring.split("_");
        LogUtils.d("listName length:" + listName.length);
        if (listName.length <= 2 || listName.length > 3) {
            LogUtils.e("file length no matching");
            return false;
        }

        if (!listName[0].equals("PanelFiles")) {
            return false;
        }

        return true;
    }


}
