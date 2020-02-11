package com.hht.android.sdk.service.utils;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.RecoverySystem;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.SurfaceControl;

import com.hht.android.sdk.boardInfo.HHTConstant;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2019/12/11 14:51
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/12/11 14:51
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class Tools {
    private final static String TAG = Tools.class.getSimpleName();

    /**
     * 切换TIPort
     *
     * return 2为默认HDMI2, 0为前置HDMI, 3为DP
     */
    public static int getTIPORT(){
        try {
            short[] shorts = TvManager.getInstance().setTvosCommonCommand(HHTConstant.TVOS_INTERFACE_CMD_GET_TIPORT);
            if (shorts != null && shorts.length >= 1){
                return shorts[0];
            }
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }

        return -1;
    }

    /**
     * 切换TIPort
     *
     * @param type 2为默认HDMI2, 0为前置HDMI, 3为DP
     */
    public static void changeTIPort(Context context, int type){
        String typeCommand = HHTConstant.SetTIPORT_2; // 默认HDMI2
        switch (type){
            case 0: // 前置HDMI
                typeCommand = HHTConstant.SetTIPORT_0;
                break;
            case 3: // DP
                typeCommand = HHTConstant.SetTIPORT_3;
                break;
        }

        Settings.System.putInt(context.getContentResolver(), HHTConstant.SOURCE_INFO, type);
        String mString = String.format("changeTIPort typeCommand->%s source_info->%s" , typeCommand, type);
        Log.d(TAG, mString);
        try {
            TvManager.getInstance().setTvosCommonCommand(typeCommand);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获得VGA切换的状态
     *
     * return 0为VGA, 1为前置VGA
     */
    public static int getVGASwitchStatus(){
        try {
            short[] shorts = TvManager.getInstance().setTvosCommonCommand(HHTConstant.TVOS_INTERFACE_CMD_GET_VGA_SWITCH_PIN_STATUS);
            if (shorts != null && shorts.length >= 1){
                return shorts[0];
            }
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }

        return -1;
    }

    /**
     * 切换VGA状态
     *
     * @param type 0为VGA, 1为前置VGA
     */
    public static void switchVGA(Context context, int type){
        // 0为VGA, 1为前置VGA
        String typeCommand = (type == 1) ?
                HHTConstant.TVOS_COMMON_CMD_VGA_SWITCH_PIN_LOW : HHTConstant.TVOS_COMMON_CMD_VGA_SWITCH_PIN_HIGH ;

        String mString = String.format("switchVGA typeCommand->%s， vga_info->%s" , typeCommand, type);
        if (HHTConstant.DEBUG) Log.d(TAG, mString);

        Settings.System.putInt(context.getContentResolver(), HHTConstant.VGA_INFO, type); // vga_info 0为VGA, 1为前置VGA
        try {
            TvManager.getInstance().setTvosCommonCommand(typeCommand);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * getTotalMemory(The function of the method)
     *
     * @Title: getTotalMemory
     * @return
     */
    public static int getTotalMemory() {
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
        long totalMemory_MB = initial_memory / 1024 / 1024;// B -> MB
        if (HHTConstant.DEBUG) Log.d(TAG, "totalmemory-B:" + initial_memory + "B;totalmemory-MB:" + totalMemory_MB + "MB。");
        // MB
        if (totalMemory_MB > 2048) {
            return (int)totalMemory_MB;
        } else if (totalMemory_MB > 1536) {
            return 2 * 1024;
//            return "2GB";
        } else if (totalMemory_MB > 1024) {
            return (int)(1.5F * 1024);
//            return "1.5GB";
        } else if (totalMemory_MB > 768) {
            return (int)(1F * 1024);
//            return "1GB";
        } else if (totalMemory_MB > 512) {
            return 768;
//            return "768MB";
        } else {
            return 512;
//            return "512MB";
        }
    }

    /**
     * getAvailMemory(The function of the method)
     * MB
     * @Title: getAvailMemory
     * @Description: TODO Tools.getAvailMemory(ctvContext)
     * @param context
     * @return
     */
    public static int getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        Log.i(TAG, "mi.availMem;" + mi.availMem);
        int availMemMB = (int)(mi.availMem / 1024 / 1024);// B -> MB
//        return Formatter.formatFileSize(context, mi.availMem);
        return availMemMB;
    }

    /**
     * 获得总的有效容量
     *
     * @return
     */
    public static long getSystemAvailableStorage(){
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

        long mb = 1024 * 1024L;// B -> MB

        return availableBlocks / mb;
    }

    public static long getSystemTotalStorage() {
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

        long mb = 1024 * 1024L;// B -> MB
        if (totalSize >= (8 * mb * 1024L)) {
//            availableBlocksOfString = getRomAvailableSize(ctvContext);
//            return availableBlocksOfString + " / 16GB";
            return 16 * 1024;
        } else if (totalSize >= (4 * mb * 1024L)) {
//            return availableBlocksOfString + " / 8GB";
            return 8 * 1024;
        }

        return 4 * 1024;
//        return availableBlocksOfString + " / 4GB";
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
     * 判断source plug status有效范围
     * @param strValue
     * @return
     */
    public static boolean sourcePlugStateValid(final String strValue){
        return (HHTConstant.SOURCE_DET_IN.equals(strValue)
                || HHTConstant.SOURCE_DET_OFF.equals(strValue)
                || HHTConstant.SOURCE_DET_OUT.equals(strValue));
    }

    /**
     * 判断source有效范围
     * @param sourceInput
     * @return
     */
    public static boolean isSourceInputValid(final int sourceInput){
        return (sourceInput >= TvCommonManager.INPUT_SOURCE_VGA
                && sourceInput <= TvCommonManager.INPUT_SOURCE_STORAGE);
    }

    /**
     * 判断source Name有效范围
     * @param sourceNameKey
     * @return
     */
    public static boolean isSourceNameValid(final String sourceNameKey){
        int sourceInput = Tools.convertToSourceInput(sourceNameKey);
        return isSourceInputValid(sourceInput);
    }

    /**
     * sourceNameKey转换为sourceInput
     *
     * @param sourceInput
     * @return
     */
    public static String convertToSourceNameKey(final int sourceInput){
        String sourceNameKey = "";
        // 判断 sourceInput 是否有效
        if (!isSourceInputValid(sourceInput)) {
            return sourceNameKey;
        }

        switch (sourceInput){
            case TvCommonManager.INPUT_SOURCE_VGA:{
                sourceNameKey = HHTConstant.VGA1;
                break;
            }
            case TvCommonManager.INPUT_SOURCE_ATV:{
                sourceNameKey = HHTConstant.ATV;
                break;
            }
            case TvCommonManager.INPUT_SOURCE_HDMI4:{
                sourceNameKey = HHTConstant.OPS;
                break;
            }
            case TvCommonManager.INPUT_SOURCE_DTV:{
                sourceNameKey = HHTConstant.DTV;
                break;
            }

            case TvCommonManager.INPUT_SOURCE_HDMI:{
                sourceNameKey = HHTConstant.HDMI1;
                break;
            }
            case TvCommonManager.INPUT_SOURCE_HDMI2:{
                sourceNameKey = HHTConstant.HDMI2;
                break;
            }
            case TvCommonManager.INPUT_SOURCE_HDMI3:{
                sourceNameKey = HHTConstant.HDMI3;
                break;
            }
            case TvCommonManager.INPUT_SOURCE_HDMI_MAX:{
                sourceNameKey = HHTConstant.HDMI5;
                break;
            }
            case TvCommonManager.INPUT_SOURCE_YPBPR:{
                sourceNameKey = HHTConstant.YPBPR1;
                break;
            }
            case TvCommonManager.INPUT_SOURCE_YPBPR2:{
                sourceNameKey = HHTConstant.YPBPR2;
                break;
            }
            case TvCommonManager.INPUT_SOURCE_CVBS:{
                sourceNameKey = HHTConstant.AV1;
                break;
            }

            case TvCommonManager.INPUT_SOURCE_STORAGE:{
                sourceNameKey = HHTConstant.ANDROID;
                break;
            }
            default:
                break;
        }

        return sourceNameKey;
    }

    /**
     * sourceNameKey转换为sourceInput
     *
     * @param sourceNameKey
     * @return
     */
    public static int convertToSourceInput(String sourceNameKey){
        int sourceInput;
        if (HHTConstant.DTV.equals(sourceNameKey)){
            sourceInput = 28;
        } else if (HHTConstant.ATV.equals(sourceNameKey)){
            sourceInput = 1;
        } else if (HHTConstant.VGA1.equals(sourceNameKey)){
            sourceInput = 0;
        } else if (HHTConstant.VGA2.equals(sourceNameKey)){
            sourceInput = 0;
        } else if (HHTConstant.VGA3.equals(sourceNameKey)){
            sourceInput = 0;
        } else if (HHTConstant.OPS.equals(sourceNameKey)){
            sourceInput = 26;
        } else if (HHTConstant.OPS2.equals(sourceNameKey)){
            sourceInput = 26;
        }

        else if (HHTConstant.HDMI1.equals(sourceNameKey)){
            sourceInput = 23;
        } else if (HHTConstant.HDMI2.equals(sourceNameKey)){
            sourceInput = 24;
        } else if (HHTConstant.HDMI3.equals(sourceNameKey)){
            sourceInput = 25;
        } else if (HHTConstant.HDMI4.equals(sourceNameKey)){
            sourceInput = 26;
        } else if (HHTConstant.HDMI5.equals(sourceNameKey)){
            sourceInput = 25;
        } else if (HHTConstant.HDMI6.equals(sourceNameKey)){
            sourceInput = 26;
        }

        else if (HHTConstant.TYPEC.equals(sourceNameKey)){ // todo
            sourceInput = -1;
        } else if (HHTConstant.TYPEC2.equals(sourceNameKey)){ // todo
            sourceInput = -1;
        } else if (HHTConstant.DP.equals(sourceNameKey)){
            sourceInput = 24;
        } else if (HHTConstant.DP2.equals(sourceNameKey)){
            sourceInput = 24;
        } else if (HHTConstant.AV1.equals(sourceNameKey)){
            sourceInput = 2;
        } else if (HHTConstant.AV2.equals(sourceNameKey)){
            sourceInput = 3;
        } else if (HHTConstant.YPBPR1.equals(sourceNameKey)){
            sourceInput = 16;
        } else if (HHTConstant.YPBPR2.equals(sourceNameKey)){
            sourceInput = 17;
        }

        else if (HHTConstant.ANDROID.equals(sourceNameKey)){
            sourceInput = 34;
        } else if (HHTConstant.STORAGE.equals(sourceNameKey)){
            sourceInput = 34;
        } else {
            sourceInput = -1;
        }

        return sourceInput;
    }

    /**
     * 获取温度
     */
    public static float getTmpValue(){
        Float tmpValue = 0f;
        try {
            short[] shorts = TvManager.getInstance().setTvosCommonCommand("GetTmpValue");
            if (shorts != null && shorts.length >= 2){
                tmpValue = shorts[0] + ((shorts[1] + 1f) / 100f);
            }

            if (HHTConstant.DEBUG) Log.d(TAG, "tmpValue->" + tmpValue);

            return tmpValue;
        } catch (Exception e){
            e.printStackTrace();
            return 0.0f;
        }
    }

    /**
     * 获得光感值
     * @return
     */
    public static int getLightSense() {
        String lightSenseStr = SystemProperties.get("LIGHT_SENSE", "0");
        int num = 0;

        try {
            int lightSense = Integer.parseInt(lightSenseStr);
            num = lightSense;
        } catch (Exception e) {
            num = 0;
            e.printStackTrace();
            return num;
        }
        return num;
    }

    /**
     * 键盘按键事件调用
     *
     * @param keycode
     * @param delayTime
     */
    public static void keyEventBySystem(final int keycode, final int delayTime){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (delayTime > 0){
                        Thread.sleep(delayTime);
                    }

                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keycode);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 判断当前是TV信号源
     *
     * @return true->TV信号源 false->非TV信号源
     */
    public static boolean isTVSource() {
        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        return (inputSource >= TvCommonManager.INPUT_SOURCE_VGA
                && inputSource < TvCommonManager.INPUT_SOURCE_STORAGE);
    }

//    /**
//     * 系统安装apk
//     *
//     * @param context
//     * @param apkPath
//     */
//    public static void installApk(Context context, String apkPath) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        File file = new File(downloadApk);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            PackageManager packageManager = context.getPackageManager();
//            PackageInfo packageInfo = null;
//            try {
//                packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//            Uri apkUri = FileProvider.getUriForFile(context, packageInfo.packageName + ".fileprovider", file);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//        } else {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Uri uri = Uri.fromFile(file);
//            intent.setDataAndType(uri, "application/vnd.android.package-archive");
//        }
//        context.startActivity(intent);
//    }

    /**
     * 静默安装，0-文件不存在, 1-安装成功，或没有升级文件，2-升级安装出现异常，-1-程序异常
     *
     * @param filePath
     * @return
     */
    public static int installBySlient(String filePath) {
        int result = 0;
        try {
            File file = new File(filePath);

            if (filePath == null || filePath.length() == 0
                    || file.length() <= 0 || !file.exists() || !file.isFile()) {
                return 0;
            }

            String[] args = {"pm", "install", "-r", filePath};
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            Process process = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = new StringBuilder();
            StringBuilder errorMsg = new StringBuilder();

            try {
                process = processBuilder.start();
                successResult = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(
                process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }

                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = 2;
            } catch (Exception e) {
                e.printStackTrace();
                result = 2;
            } finally {
                try {
                    if (successResult != null) {
                        successResult.close();
                    }

                    if (errorResult != null) {
                        errorResult.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (process != null) {
                    process.destroy();
                }
            }
            if (successMsg.toString().contains("Success")
                    || successMsg.toString().contains("success")) {
                result = 1;
            } else {
                result = 2;
            }
        } catch (Exception e) {
            result = -1;
        }
        return result;
    }

    public static boolean excuteShellCommand(String cmd){
        Runtime r = Runtime.getRuntime();
        Process p = null;
        try {
            p = r.exec(cmd);
            p.waitFor();
            if(p.exitValue() == 0){
                return true;
            }else
            {
                return false;
            }
        }catch(Exception e)
        {
            return false;
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }

    /**
     * 升级主文件bin
     * @return
     */
    public static int upgradeMainFun() {
        int ret = 0;
        // TODO UpgradeMain function
        String mainpath;
        mainpath = FindFileOnUSB("MstarUpgrade.bin");
        // should not change this file name

        if ("" == mainpath) {
            ret = 2;
        } else {
            try {
                TvManager.getInstance().setEnvironment("factory_mode", "1");

                if (TvManager.getInstance().setEnvironment("upgrade_mode", "usb")) {
                    TvManager.getInstance().setEnvironment("CtvUpgrade_complete", "0");
                    ret = 1;
                } else {
                    if (HHTConstant.DEBUG) Log.d("UpgradeMainFun:", "setEnvironment Failed!");
                    ret = 0;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return ret;
    }


    private static String FindFileOnUSB(String filename) {
        // TODO Find File On USB function
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

    /**
     * 本地升级：升级update文件zip
     *
     * @param context
     * @param mUpdateFile
     */
    public static void updateZip(Context context, File mUpdateFile) {
        try {
            int[] GetOPSDEVICESTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                    "GetOPSDEVICESTATUS");
            int[] GetOPSPOWERSTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                    "GetOPSPOWERSTATUS");
            Log.d("chen_powerdown", "GetOPSDEVICESTATUS:" + GetOPSDEVICESTATUS[0]);
            Log.d("chen_powerdown", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);
            if (GetOPSDEVICESTATUS[0] == 0 || GetOPSPOWERSTATUS[0] == 0) {// 0,表示有OPS设备接入；1，表示没有OPS设备接入。
//                myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Dialog close_dialog = close_builder.create();
//                        close_dialog.getWindow().setBackgroundDrawableResource(
//                                android.R.color.transparent);
//                        close_dialog.getWindow().setType(
//                                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                        close_dialog.setCanceledOnTouchOutside(false);
//                        close_dialog.show();
//                        WindowManager.LayoutParams lp = close_dialog.getWindow().getAttributes();
//                        lp.width = 640;
//                        close_dialog.getWindow().setAttributes(lp);
//                    }
//                });
                Log.d("chen_powerdown", "start");
                Log.d("chen_powerdown", "close ops");
                TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWER");
                Thread.sleep(200);
                TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWERON");
                Log.d("chen_powerdown", "ops:state");
                Thread.sleep(2000);
                GetOPSDEVICESTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSDEVICESTATUS");
                GetOPSPOWERSTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSPOWERSTATUS");
                Log.d("chen_powerdown", "GetOPSDEVICESTATUS:" + GetOPSDEVICESTATUS[0]);
                Log.d("chen_powerdown", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);
                int count = 0;
                while (GetOPSPOWERSTATUS[0] == 0) {
                    Log.d("chen_powerdown", "checkops state start ");
                    Log.d("chen_powerdown", "checkops time count : " + count);
                    Thread.sleep(1000);
                    count++;
                    if (count == 30) {
                        Log.d("chen_powerdown", "ops force now :count: " + count);
                        TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWER");
                        Thread.sleep(5 * 1000);
                        TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWERON");
                        break;
                    }
                    Log.d("chen_powerdown", "change ops state start");
                    GetOPSDEVICESTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                            "GetOPSDEVICESTATUS");
                    GetOPSPOWERSTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                            "GetOPSPOWERSTATUS");
                    Log.d("chen_powerdown", "change ops state resutl:");
                    Log.d("chen_powerdown", "GetOPSDEVICESTATUS:" + GetOPSDEVICESTATUS[0]);
                    Log.d("chen_powerdown", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);
                }
                RecoverySystem.installPackage(context, mUpdateFile);
            } else {
                RecoverySystem.installPackage(context, mUpdateFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置显示比例
     *
     * @param pos
     * @return
     */
    public static boolean setVideoArcType(int pos) {
        int zoomMode = TvPictureManager.VIDEO_ARC_16x9;
        switch (pos) {
            case 0:
                zoomMode = TvPictureManager.VIDEO_ARC_16x9;
                break;
            case 1:
                zoomMode = TvPictureManager.VIDEO_ARC_4x3;
                break;
            case 2:
                zoomMode = TvPictureManager.VIDEO_ARC_AUTO;
                break;
            case 3:
                if (isHdmiSource()) {
                    zoomMode = TvPictureManager.VIDEO_ARC_JUSTSCAN;
                } else {
                    zoomMode = TvPictureManager.VIDEO_ARC_ZOOM1;
                }
                break;
            case 4:
                if (isHdmiSource()) {
                    zoomMode = TvPictureManager.VIDEO_ARC_ZOOM1;
                } else {
                    zoomMode = TvPictureManager.VIDEO_ARC_ZOOM2;
                }
                break;
            case 5:
                if (isHdmiSource()) {
                    zoomMode = TvPictureManager.VIDEO_ARC_ZOOM2;
                }
                break;
            default:
                break;
        }
        return TvPictureManager.getInstance().setVideoArcType(zoomMode);
    }

    /**
     * if the current source is hdmi
     *
     * @return true: is hdmi source. false : other
     */
    public static boolean isHdmiSource() {
        int currentSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        boolean isHdmi = false;
        if (currentSource >= TvCommonManager.INPUT_SOURCE_HDMI
                && currentSource <= TvCommonManager.INPUT_SOURCE_HDMI_MAX) {
            isHdmi = true;
        }
        return isHdmi;
    }


    /**
     * 判断Tv Window是否显示
     *
     * @param mContext
     * @return
     */
    public static boolean isTvWindow(Context mContext){
        boolean flag = false;

        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = activityManager.getRunningTasks(5);
        if(infos != null && infos.size() > 0){
            String packageNameTop = infos.get(0).topActivity.getPackageName();
//            Log.d(TAG, "isEasyTouchHide packageNameTop->" + packageNameTop);
            if (HHTConstant.TV_PLAY_PACKAGE.equals(packageNameTop)){ // 若Top 为TV_PLAY_PACKAGE时,则为false
                flag = true;
            }
        }

        if (HHTConstant.DEBUG) Log.d(TAG, "isTvWindow is " + flag);

        return flag;
    }

    /**
     * 切换信号通道
     *
     * @param mContext
     * @param inputSource
     */
    public static void changeSignal(final Context mContext, final int inputSource){
        if (HHTConstant.DEBUG) Log.d(TAG, "zhuzhu..changeSignal, inputSource:" + inputSource);
        /*try{
            if(inputSource == 24){
                //short[] source_select = TvManager.getInstance().setTvosCommonCommand("GetTIPORT");
                //sourcePort = source_select[0];
                String setPort = "SetTIPORT-"+sourcePort;
                Settings.System.putInt(mContext.getContentResolver(),"source_info",sourcePort);
                TvManager.getInstance().setTvosCommonCommand(setPort);
                Log.d(TAG, "zhuzhu...changeSignal, setPort:"+setPort);
            }*//*else if(inputSource == 0){
                if(sourcePort == 0){
                    Settings.System.putInt(mContext.getContentResolver(),"vga_info",0);
                    TvManager.getInstance().setTvosCommonCommand("SetVGA_SWITCH_High");
                }else if(sourcePort == 1){
                    Settings.System.putInt(mContext.getContentResolver(),"vga_info",1);
                    TvManager.getInstance().setTvosCommonCommand("SetVGA_SWITCH_Low");
                }
            }*//*
        } catch (TvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        new Thread(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent("com.mstar.android.intent.action.START_TV_PLAYER");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.putExtra("task_tag", "input_source_changed");
                intent.putExtra("inputSrc", inputSource);

                if (HHTConstant.DEBUG) Log.d(TAG, "changeSignal:"+inputSource);

                try {
                    mContext.startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    Intent targetIntent;
                    targetIntent = new Intent("mstar.tvsetting.ui.intent.action.RootActivity");
                    targetIntent.putExtra("task_tag", "input_source_changed");
                    /* DO NOT remove on_change_source extra!, it will cause mantis:1088498. */
                    targetIntent.putExtra("no_change_source", true);
                    targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (targetIntent != null){
                        mContext.startActivity(targetIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String getRTCTime(){
        String time = "";
        try {
            short[] rTCTime = TvManager.getInstance().setTvosCommonCommand(HHTConstant.TVOS_COMMON_CMD_GET_RTCTIME);
            int year_rt = rTCTime[0];
            if (year_rt < 1970 || year_rt > 2200) {
                return time;
            }
            int month_rt = rTCTime[1];
            int day_rt = rTCTime[2];
            int week_rt = rTCTime[3];
            int hour_rt = rTCTime[4];
            int minute_rt = rTCTime[5];
            int second_rt = rTCTime[6];

            StringBuffer sb = new StringBuffer();
            sb.append(changeTimeFormat(year_rt) + "-");
            sb.append(changeTimeFormat(month_rt) + "-");
            sb.append(changeTimeFormat(day_rt));
            sb.append(changeTimeFormat(hour_rt) + ":");
            sb.append(changeTimeFormat(minute_rt) + ":");
            sb.append(changeTimeFormat(second_rt) + ":");
            sb.append("000");

            try
            {
                // 对 calendar 设置时间的方法
                // 设置传入的时间格式
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                time = sb.toString();
                // 指定一个日期
                Date date = dateFormat.parse(time);// "2019-06-01 13:24:16:000"
                // 对 calendar 设置为 date 所定的日期
                Log.d(TAG, "getRTCTime:" + time);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        return time;
    }

    public static void setRTCTime(String timeStr) {
        Calendar calendar = Calendar.getInstance();
        try
        {
            // 对 calendar 设置时间的方法
            // 设置传入的时间格式
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            // 指定一个日期
            Date date = dateFormat.parse(timeStr);// "2019-06-01 13:24:16:000"
            // 对 calendar 设置为 date 所定的日期
            calendar.setTime(date);

            // 按特定格式显示刚设置的时间
            String str = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")).format(calendar.getTime());
            Log.d(TAG, "setRTCTime:" + str);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }



//        calendar.set(2013, 1, 2, 17, 35, 44);// year, month, date, hourOfDay, minute, second

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int week_year = calendar.get(Calendar.WEEK_OF_YEAR);

        StringBuffer sb = new StringBuffer();
        sb.append(HHTConstant.TVOS_COMMON_CMD_SET_RTCTIME);
        sb.append(changeTimeFormat(year));
        sb.append(changeTimeFormat(month));
        sb.append(changeTimeFormat(day));
        sb.append(changeTimeFormat(week_year));
        sb.append(changeTimeFormat(hour));
        sb.append(changeTimeFormat(minute));
        sb.append(changeTimeFormat(second));
        try {
            TvManager.getInstance().setTvosCommonCommand(sb.toString());
            Log.d("TimeUtil", "setting settime:" + sb.toString());
        } catch (TvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String changeTimeFormat(int data) {
        if (data > 9) {
            return "" + data;
        } else {
            return "0" + data;
        }
    }

    /**
     * 取得当前时间戳（精确到秒）
     *
     * @return nowTimeStamp
     */
    public static String getNowTimeStamp() {
        long time = System.currentTimeMillis();
        String nowTimeStamp = String.valueOf(time / 1000);
        return nowTimeStamp;
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param dateStr 字符串日期
     * @param format   如：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String Date2TimeStamp(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(dateStr).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Java将Unix时间戳转换成指定格式日期字符串
     * @param timestampString 时间戳 如："1473048265";
     * @param formats 要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
     *
     * @return 返回结果 如："2016-09-05 16:06:42";
     */
    public static String TimeStamp2Date(String timestampString, String formats) {
        if (TextUtils.isEmpty(formats))
            formats = "yyyy-MM-dd HH:mm:ss";
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return date;
    }

    /**
     * 截图
     *
     * @param width
     * @param height
     * @return
     */
    public static Bitmap screenshot(int width, int height){
        return SurfaceControl.screenshot(width, height);
//        Bitmap mScreenBitmap = null;
//        try {
//            Class testClass = Class.forName("android.view.SurfaceControl");
//            Method saddMethod1 = testClass.getMethod("screenshot", new Class[]{int.class ,int.class});
//            mScreenBitmap = (Bitmap) saddMethod1.invoke(null, new Object[]{width, height});
//            return mScreenBitmap;
//        } catch (Exception e){
//            e.printStackTrace();
//            return mScreenBitmap;
//        }
    }

    /**
     * 加载文件
     * @param filePath
     * @return
     */
    public static String loadFileAsString(String filePath){
        BufferedReader macReader = null;
        try {
            macReader = new BufferedReader(new FileReader(filePath));
            String buffer = null;
            StringBuilder builder = new StringBuilder();
            while((buffer = macReader.readLine()) != null){
                builder.append(buffer);
            }
            return builder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if(macReader != null){
                try {
                    macReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 判断是否支持光感
     * @return
     */
    public static boolean isSupportLightSense(){
        return SystemProperties.get("service.light.sense.enable", "0").equals("1");
    }

    /**
     * open OPS
     */
    public static boolean openOps() {
        boolean openFlag = false;

        int[] GetOPSDEVICESTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                "GetOPSDEVICESTATUS");
        int[] GetOPSPOWERSTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                "GetOPSPOWERSTATUS");
        if (GetOPSDEVICESTATUS != null && GetOPSPOWERSTATUS != null) {
            Log.d(TAG, "zhu..ops device status:" + GetOPSDEVICESTATUS[0]);
            //   if (GetOPSDEVICESTATUS[0] == 0) {// 0,表示有OPS设备接入；1，表示没有OPS设备接入。
            if (GetOPSPOWERSTATUS[0] == 1) {// 0,表示OPS开机；1，表示OPS关机。
                Log.d(TAG, "zhu..ops power status:" + GetOPSPOWERSTATUS[0]);
                // Toast.makeText(RootActivity.this,"请将OPS开机",
                // Toast.LENGTH_SHORT).show();
                // showExitDialog2(DIALOG_TYPE_OPS_POWER);
                TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWER");
                SystemClock.sleep(200);
                TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWERON");
                openFlag = true;
                // Toast.makeText(getApplicationContext(), "ops开机啦",
                // 1000).show();
            }
            //   }
        }

        return openFlag;
    }

    /**
     * close OPS
     */
    public static boolean closeOps() {
        boolean closeFlag = false;

        // TVOS_COMMON_CMD_QUERY_TCA9539                     "QueryTca9539"

        int[] GetOPSDEVICESTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                "GetOPSDEVICESTATUS");
        int[] GetOPSPOWERSTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                "GetOPSPOWERSTATUS");
        if (GetOPSDEVICESTATUS != null && GetOPSPOWERSTATUS != null) {
            Log.d(TAG, "zhu..ops device status:" + GetOPSDEVICESTATUS[0]);
            //   if (GetOPSDEVICESTATUS[0] == 0) {// 0,表示有OPS设备接入；1，表示没有OPS设备接入。
            if (GetOPSPOWERSTATUS[0] == 0) {// 0,表示OPS开机；1，表示OPS关机。
                Log.d(TAG, "zhu..ops power status:" + GetOPSPOWERSTATUS[0]);
                TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWER");
                SystemClock.sleep(200);
                TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWERON");
                closeFlag = true;
            }
            //   }
        }

        return closeFlag;
    }

    /**
     * 判断OPS是否已经启动
     * @return
     */
    public static boolean isOpsOpen(){
        boolean isOpsOpen = false;

        int[] GetOPSDEVICESTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                "GetOPSDEVICESTATUS");
        int[] GetOPSPOWERSTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                "GetOPSPOWERSTATUS");
        if (GetOPSDEVICESTATUS != null && GetOPSPOWERSTATUS != null) {
            Log.d(TAG, "zhu..ops device status:" + GetOPSDEVICESTATUS[0]);
            //   if (GetOPSDEVICESTATUS[0] == 0) {// 0,表示有OPS设备接入；1，表示没有OPS设备接入。
            if (GetOPSPOWERSTATUS[0] == 0) {// 0,表示OPS开机；1，表示OPS关机。
                isOpsOpen = true;
            }
            //   }
        }

        return isOpsOpen;
    }

    /**
     * 判断OPS是否已经插入卡座
     * @return
     */
    public static boolean isOpsPlugIn(){
        boolean isOpsPlugIn = false;

        int[] GetOPSDEVICESTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                "GetOPSDEVICESTATUS");
        int[] GetOPSPOWERSTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                "GetOPSPOWERSTATUS");
        if (GetOPSDEVICESTATUS != null && GetOPSPOWERSTATUS != null) {
            Log.d(TAG, "zhu..ops device status:" + GetOPSDEVICESTATUS[0]);
               if (GetOPSDEVICESTATUS[0] == 0) {// 0,表示有OPS设备接入；1，表示没有OPS设备接入。
    //            if (GetOPSPOWERSTATUS[0] == 0) {// 0,表示OPS开机；1，表示OPS关机。
                    isOpsPlugIn = true;
    //            }
               }
        }

        return isOpsPlugIn;
    }
}
