
package com.ctv.settings.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Instrumentation;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.cultraview.tv.utils.CtvCommonUtils;
import com.mstar.android.MIntent;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

/**
 * @Copyright (C), 2016-9-5, CultraView
 * @author Write Macro.Song(songhong@cultraview.com)
 * @since 3.0.0 Tools.
 */
@SuppressLint("NewApi")
public class Tools {

    private static final String TAG = "Tools";

//    public static int theme = R.style.NightTheme;

    private static final String DEFAULT_MAC_ADDRESS = "00:30:1B:BA:02:DB";

//    public static void setThemeByTime(Activity activity) {
//        theme = R.style.DayTheme;
//
//        // String the = SystemProperties.get("persist.sys.theme", "0");
//        // if (the.equals("1")) {
//        // theme = R.style.DayTheme;
//        // } else if (the.equals("2")) {
//        // theme = R.style.NightTheme;
//        // } else {
//        // int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//        // if (hour >= 6 && hour < 18) {
//        // theme = R.style.DayTheme;
//        // } else {
//        // theme = R.style.NightTheme;
//        // }
//        // }
//        activity.setTheme(theme);
//    }

    /**
     * readValue(The function of the method)
     * 
     * @Title: readValue
     * @Description: TODO
     * @param filePath
     * @param key
     * @return
     */
    public static String readValue(String filePath, String key) {
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
            String value = props.getProperty(key);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
    }

//    /**
//     * generatePageIndicator(The function of the method)
//     *
//     * @Title: generatePageIndicator
//     * @Description: TODO
//     * @param Context context
//     * @param LinearLayout indicators
//     * @param int pageSize
//     * @param int pSelect
//     */
//    public static void generatePageIndicator(Context context, LinearLayout indicators,
//            int pageSize, int pSelect) {
//        indicators.removeAllViews();
//        ImageView[] status = new ImageView[pageSize + 1];
//        for (int i = 0; i < pageSize; i++) {
//            status[i] = new ImageView(context);
//            status[i].setTag("indicators" + i);
//            if (i == pSelect) {
//                status[i].setImageResource(R.drawable.indicator_focus);
//            } else {
//                status[i].setImageResource(R.drawable.indicator_unfocus);
//            }
//            LayoutParams lp = new LayoutParams(new LayoutParams(22, 22));
//            lp.setMargins(23, 0, 0, 0);
//            status[i].setLayoutParams(lp);
//            indicators.addView(status[i]);
//        }
//    }

    /**
     * saveAutoTimeValue(The function of the method)
     * 
     * @Title: saveAutoTimeValue
     * @Description: TODO
     * @param ctvContext
     * @param isAutoDateTime
     */
    @SuppressLint("NewApi")
    public static void saveAutoTimeValue(Context ctvContext, boolean isAutoDateTime) {
        Log.d(TAG, "--saveAutoTimeValue, isAutoDateTime, " + isAutoDateTime);
        Settings.Global.putInt(ctvContext.getContentResolver(), Settings.Global.AUTO_TIME,
                isAutoDateTime ? 1 : 0);
        Intent timeChanged = new Intent(MIntent.ACTION_TV_AUTO_TIME_SYNC);
        timeChanged.putExtra(MIntent.EXTRA_KEY_TV_AUTO_TIME, !isAutoDateTime);
        ctvContext.sendBroadcast(timeChanged);
    }

    /**
     * getTimeZoneText(The function of the method)
     * 
     * @Title: getTimeZoneText
     * @Description: TODO
     * @return String: System current time zone
     */
    public static String getTimeZoneText() {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        boolean daylight = tz.inDaylightTime(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append(formatOffset(tz.getRawOffset())).append(", ")
                .append(tz.getDisplayName(daylight, TimeZone.LONG));
        return sb.toString();
    }

    public static char[] formatOffset(int off) {
        off = off / 1000 / 60;
        char[] buf = new char[9];
        buf[0] = 'G';
        buf[1] = 'M';
        buf[2] = 'T';
        if (off < 0) {
            buf[3] = '-';
            off = -off;
        } else {
            buf[3] = '+';
        }
        int hours = off / 60;
        int minutes = off % 60;
        buf[4] = (char) ('0' + hours / 10);
        buf[5] = (char) ('0' + hours % 10);
        buf[6] = ':';
        buf[7] = (char) ('0' + minutes / 10);
        buf[8] = (char) ('0' + minutes % 10);
        return buf;
    }

    /**
     * commitDateFormat(The function of the method)
     * 
     * @Title: commitDateFormat
     * @Description:
     * @param context
     * @param mDateFormatIndex
     */
    public static void commitDateFormat(Context context, int mDateFormatIndex) {
        Log.i("gyx","mDateFormatIndex="+mDateFormatIndex);
        SystemProperties.set("persist.sys.dateformat", mDateFormatIndex + "");
    }

    /**
     * getDateFormat(The function of the method)
     * 
     * @Title: getDateFormat
     * @Description: TODO
     * @param context
     * @return
     */
    public static int getDateFormat(Context context) {
        return Integer.parseInt(SystemProperties.get("persist.sys.dateformat", "2"));
    }

    /**
     * isNonMarketAppsAllowed(The function of the method)
     * 
     * @Title: isNonMarketAppsAllowed
     * @Description: TODO
     * @param ctvContext
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean isNonMarketAppsAllowed(Context ctvContext) {
        return Settings.Secure.getInt(ctvContext.getContentResolver(),
                Settings.Secure.INSTALL_NON_MARKET_APPS, 0) > 0;
    }

    /**
     * setNonMarketAppsAllowed(The function of the method)
     * 
     * @Title: setNonMarketAppsAllowed
     * @Description: TODO Tools.setNonMarketAppsAllowed(ctvContext,true)
     * @param ctvContext
     * @param enabled
     */
    @SuppressWarnings("deprecation")
    public static void setNonMarketAppsAllowed(Context ctvContext, boolean enabled) {
        Settings.Secure.putInt(ctvContext.getContentResolver(),
                Settings.Secure.INSTALL_NON_MARKET_APPS, enabled ? 1 : 0);
    }

    /**
     * getBootDesktopType(The function of the method)
     * 
     * @Title: getBootDesktopType
     * @Description: TODO flag 1:Launcher,2:TV
     * @param ctvContext
     * @return
     */
    public static String getBootDesktopType(Context ctvContext, String[] BOOT_DESKTOP_ITEMS) {
        if ("1".equals(DataTool.getBootLoader(ctvContext))) {
            // 1.Launcher
            return BOOT_DESKTOP_ITEMS[0];
        } else if ("2".equals(DataTool.getBootLoader(ctvContext))) {
            // 2.TV
            return BOOT_DESKTOP_ITEMS[1];
        } else {
            // 3.Memory
            return BOOT_DESKTOP_ITEMS[2];
        }
    }

    /**
     * getWireMacAddress(The function of the method)
     * 
     * @Title: getWireMacAddress
     * @return
     */
    public static String getWireMacAddress(Context ctvContext) {
        String macAddress = "";
        String filePath = "/sys/class/net/eth0/address";
        StringBuffer fileData = new StringBuffer(1000);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            reader.close();
            macAddress = fileData.toString().toUpperCase().substring(0, 17);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "macAddress error!");
        }
        if (macAddress.equals(DEFAULT_MAC_ADDRESS)) {
            Log.d(TAG, "DEFAULT_MAC_ADDRESS: 00:30:1B:BA:02:DB");
            return "00.00.00.00.00.00";
        }
        String envMac = "";
        try {
            envMac = CtvTvManager.getInstance().getEnvironment("macaddr");
        } catch (CtvCommonException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "envMac1 = " + envMac);
        if (envMac == null || envMac.equals("")) {
            return "00.00.00.00.00.00";
        }
        envMac = envMac.toUpperCase();
        if (macAddress.equals(envMac)) {
            return macAddress;
        } else {
            return "00.00.00.00.00.00";
        }
    }

    /**
     * getAvailMemory(The function of the method)
     * 
     * @Title: getAvailMemory
     * @Description: TODO Tools.getAvailMemory(ctvContext)
     * @param ctvContext
     * @return
     */
    public static String getAvailMemory(Context ctvContext) {
        ActivityManager am = (ActivityManager) ctvContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        Log.i(TAG, "mi.availMem;" + mi.availMem);
        return Formatter.formatFileSize(ctvContext, mi.availMem);
    }

    /**
     * getTotalMemory(The function of the method)
     * 
     * @Title: getTotalMemory
     * @Description: TODO Tools.getTotalMemory(ctvContext)
     * @param ctvContext
     * @return
     */
    public static String getTotalMemory(Context ctvContext) {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(TAG, "getTotalMemory:" + str2 + "；split string:" + num + "\t");
            }
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();
        } catch (IOException e) {
        }
        // change totalMemeory to constant value
        long totalMemory_MB = initial_memory / 1024 / 1024;
        Log.d(TAG, "totalmemory-B:" + initial_memory + "B;totalmemory-MB:" + totalMemory_MB + "MB。");
        if (totalMemory_MB > 2048) {
            return Formatter.formatFileSize(ctvContext, initial_memory);
        } else if (totalMemory_MB > 1536) {
            return "2GB";
        } else if (totalMemory_MB > 1024) {
            return "1.5GB";
        } else if (totalMemory_MB > 768) {
            return "1GB";
        } else if (totalMemory_MB > 512) {
            return "768MB";
        } else {
            return "512MB";
        }

    }

    /**
     * formatTimeRemaining(The function of the method)
     * 
     * @Title: Tools.formatTimeRemaining(time) Eg: 120s--> (2:00)
     * @Description: TODO
     * @param time :How many seconds
     * @return format time
     */
    public static String formatTimeRemaining(int time) {
        StringBuilder sb = new StringBuilder(6); // "mmm:ss"
        int min = time / 60;
        int sec = time % 60;
        sb.append(min).append(" : ");
        if (sec < 10) {
            sb.append('0');
        }
        sb.append(sec);
        return " ( " + sb.toString() + " )";
    }

//    /**
//     * getDiscoverableTimeoutIndex(The function of the method)
//     *
//     * @Title: Tools.getDiscoverableTimeoutIndex(timeout)
//     * @Description: TODO
//     * @param timeout
//     * @return index
//     */
//    public static int getDiscoverableTimeoutIndex(int timeout) {
//        switch (timeout) {
//            case Constants.DISCOVERABLE_TIMEOUT_TWO_MINUTES:
//            default:
//                return 0;
//            case Constants.DISCOVERABLE_TIMEOUT_FIVE_MINUTES:
//                return 1;
//            case Constants.DISCOVERABLE_TIMEOUT_ONE_HOUR:
//                return 2;
//            case Constants.DISCOVERABLE_TIMEOUT_NEVER:
//                return 3;
//        }
//    }
//
//    /**
//     * getDiscoverableTimeout(The function of the method)
//     *
//     * @Title: Tools.getDiscoverableTimeout(index)
//     * @Description: TODO
//     * @param index
//     * @return timeout
//     */
//    public static int getDiscoverableTimeout(int index) {
//        switch (index) {
//            case 0:
//            default:
//                return Constants.DISCOVERABLE_TIMEOUT_TWO_MINUTES;
//            case 1:
//                return Constants.DISCOVERABLE_TIMEOUT_FIVE_MINUTES;
//            case 2:
//                return Constants.DISCOVERABLE_TIMEOUT_ONE_HOUR;
//            case 3:
//                return Constants.DISCOVERABLE_TIMEOUT_NEVER;
//        }
//    }

    /**
     * runCMD(The function of the method)
     * 
     * @Title: Tools.runCMD(cmd)
     * @Description: TODO
     * @param cmd
     */
    public static void runCMD(String cmd) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * isNetConnected(The function of the method)
     * 
     * @Title: Tools.isNetConnected(ctvContext)
     * @param ctvContext
     * @return isConnected
     */
    public static boolean isNetConnected(Context ctvContext) {
        String result = null;
        boolean isConnected = false;
        Process p = null;
        Log.d(TAG, "===judge net status");
        try {
            String ip = "www.bing.com";//
            p = Runtime.getRuntime().exec("ping -c 1 -i 0.2 -W 1 " + ip);
            int status = p.waitFor();
            if (status == 0) {
                result = "successful~";
                isConnected = true;
            } else {
                result = "failed~ cannot reach the IP address";
                isConnected = false;
            }
        } catch (IOException e) {
            result = "failed~ IOException";
            isConnected = false;
        } catch (InterruptedException e) {
            result = "failed~ InterruptedException";
            isConnected = false;
        } finally {
            Log.i(TAG, "==================result = " + result);
            p.destroy();
        }
        return isConnected;
    }

    /**
     * getSystemVersion(The function of the method)
     * 
     * @Title: Tools.getSystemVersion()
     * @return String Build.VERSION
     */
    public static String getSystemVersion() {
        return Build.VERSION.INCREMENTAL.substring(1);
    }

    /**
     * getUUID(get device uuid)
     * 
     * @Description: Tools.getUUID(ctvContext)
     * @param ctvContext
     * @return UUID
     */
    public static UUID getUUID(Context ctvContext) {
        final TelephonyManager phonyManager = (TelephonyManager) ctvContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + phonyManager.getDeviceId();
        tmSerial = "" + phonyManager.getSimSerialNumber();
        androidId = ""
                + Settings.Secure.getString(ctvContext.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32)
                | tmSerial.hashCode());
        return deviceUuid;
    }

    public static boolean string2File(String res, String filePath) {
        boolean flag = true;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists())
                distFile.getParentFile().mkdirs();
            bufferedReader = new BufferedReader(new StringReader(res));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[1024];
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
            return flag;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * DDR(运行内存 )
     * 
     * @Title: Tools.getDDRInformation(ctvContext)
     * @param ctvContext
     * @return MemoryInformation
     */
    public static String getDDRInformation(Context ctvContext) {
        String memoryInfo = Tools.getAvailMemory(ctvContext).replaceAll(" ", "") + " / "
                + Tools.getTotalMemory(ctvContext).replaceAll(" ", "");
        return memoryInfo;
    }

    /**
     * EMMC( sdCard+data)
     * 
     * @Title: Tools.getEMMCInformation(ctvContext)
     * @param ctvContext
     * @return MemoryInformation
     */
    public static String getEMMCInformation(Context ctvContext) {
        /* modify by jambr: add total size */
        long availableBlocks = 0L;
        long totalSize = 0L;
        // data
        StatFs statData = new StatFs("/data");
        long block_size_data = statData.getBlockSizeLong();
        long available_blocks_data = statData.getAvailableBlocksLong();
        long block_count_data = statData.getBlockCountLong();

        // SD
        long block_size_sd = 0L;
        long available_blocks_sd = 0L;
        long block_count_sd = 0L;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = Environment.getExternalStorageDirectory();
            StatFs statSD = new StatFs(file.getPath());
            block_size_sd = statSD.getBlockSizeLong();
            available_blocks_sd = statSD.getAvailableBlocksLong();
            block_count_sd = statSD.getBlockCountLong();
        }
        // all available
        availableBlocks = block_size_data * available_blocks_data + block_size_sd
                * available_blocks_sd;
        // all
        totalSize = block_size_data * block_count_data + block_size_sd * block_count_sd;
        String availableBlocksOfString = convertFileSize(ctvContext, availableBlocks).replace(",",
                ".");
        if (SystemPropertiesUtils.get(ctvContext,"ro.board.platform", "CTV").equalsIgnoreCase("macan")) {
            // 538 sd == data
            availableBlocks = block_size_data * available_blocks_data;
            totalSize = block_size_data * block_count_data;
            availableBlocksOfString = convertFileSize(ctvContext, availableBlocks)
                    .replace(",", ".");
        }
        Log.d(TAG, "totalSize : " + totalSize);
        if (totalSize >= (8 * 1024 * 1024 * 1024l)) {
            availableBlocksOfString = getRomAvailableSize(ctvContext);
            return availableBlocksOfString + " / 16GB";
        } else if (totalSize >= (4 * 1024 * 1024 * 1024l)) {
            return availableBlocksOfString + " / 8GB";
        }

        return availableBlocksOfString + " / 4GB";
    }

    private static String getRomAvailableSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    private static String convertFileSize(Context context, long number) {
        if (context == null) {
            return "";
        }

        float result = number;
        String suffix = "B";
        if (result > 900) {
            suffix = "KB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "MB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "GB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "TB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "PB";
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format(Locale.ENGLISH, "%.2f", result);
        } else if (result < 10) {
            value = String.format(Locale.ENGLISH, "%.2f", result);
        } else if (result < 100) {
            value = String.format(Locale.ENGLISH, "%.2f", result);
        } else {
            value = String.format(Locale.ENGLISH, "%.0f", result);
        }
        Log.i("Tools", "value============" + value);
        return value + suffix;
    }

    /**
     * get the system sound status 0 is close ;1 is on
     * 
     * @param ctvContext
     * @return
     */
    public static Integer getSystemSoundStatus(Context ctvContext) {
        return Settings.System.getInt(ctvContext.getContentResolver(),
                Settings.System.SOUND_EFFECTS_ENABLED, 0);
    }

    public static void setSystemSoundStatus(Context ctvContext, int status) {
        Settings.System.putInt(ctvContext.getContentResolver(),
                Settings.System.SOUND_EFFECTS_ENABLED, status);
    }

    /**
     * 获得开机选项: Launcher "1"; TV "2"
     * 
     * @param context
     * @return
     */
    public static String getBootLoader(Context context) {
        return CtvCommonUtils.getCultraviewProjectInfo(context, "tbl_Configuration", "BootLoader");
    }

    /**
     * 设置开机选项: Launcher "1"; TV "2",PC "3"
     * 
     * @param context
     * @param value
     * @return
     */
    public static void setBootLoader(Context context, String value) {
        CtvCommonUtils.setCultraviewProjectInfo(context, "tbl_Configuration", "BootLoader", value);
    }

    public static void keyInjection(final int keyCode) {

        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }.start();
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }

    public static void startManagePermissionsActivity(Context context,
                                                String mPackageName) {
        // start new activity to manage app permissions
        Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
        intent.putExtra(Intent.EXTRA_PACKAGE_NAME, mPackageName);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG,
                    "No app can handle android.intent.action.MANAGE_APP_PERMISSIONS");
        }
    }
}
