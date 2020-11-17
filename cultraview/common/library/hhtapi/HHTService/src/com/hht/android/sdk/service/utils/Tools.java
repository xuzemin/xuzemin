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
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.SurfaceControl;
import android.provider.Settings;
import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.service.HHTSourceService;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.tv.service.DatabaseDesk;
import com.mstar.tv.service.IDatabaseDesk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private final static boolean IS_MH_BOARD = isMHBoard() || isBHBoard();// 判断是否为MH主板

    private final static boolean IS_LH_BOARD = isLHBoard();//判断是否为LH主板

    private final static boolean IS_AH_BOARD = isAHBoard();//判断是否为AH主板
    /**
     * 判断TIPort处于活动状态的port口
     *
     * return MH board: 0为HDMI, 2为HDMI2, 3为OPS
     */
    public static int getTIPORT(){
        int[] shorts = sendCommand(TvosCommand.TVOS_COMMON_CMD_GET_HDMI_PORT);
        if (shorts != null && shorts.length >= 1){
            return shorts[0];
        }

        return -1;
    }

    /**
     * 切换TIPort
     *
     * @param type 0为HDMI, 2为HDMI2,  3为OPS
     */
    public static void changeTIPort(Context context, int type){
        if (type == 0 ||type == 2 || type == 3){
            String typeCommand = TvosCommand.TVOS_COMMON_CMD_SET_HDMI_PORT + type; // 默认HDMI1
            Settings.System.putInt(context.getContentResolver(), HHTConstant.SOURCE_INFO, type);
            String mString = String.format("changeTIPort typeCommand->%s source_info->%s" , typeCommand, type);
            L.d(TAG, mString);
            sendCommand(typeCommand);
        }
    }

    /**
     * 获得VGA切换的状态
     *
     * return 0为VGA, 1为前置VGA
     */
    public static int getVGASwitchStatus(){
        int[] shorts = sendCommand(TvosCommand.TVOS_COMMON_CMD_GET_VGA_SEL);
        if (shorts != null && shorts.length >= 1){
            return shorts[0];
        }

        return -1;
    }

    /**
     * 切换VGA状态
     *
     * @param type 0为YPBPR, 1为VGA
     */
    public static void switchVGA(Context context, int type){
        if (type == 0 || type == 1){
            // 0为YPBPR, 1为VGA
            String typeCommand = TvosCommand.TVOS_COMMON_CMD_SET_VGA_SEL + type ;

            String mString = String.format("switchVGA typeCommand->%s， vga_info->%s" , typeCommand, type);
            if (HHTConstant.DEBUG) L.d(TAG, mString);

            // vga_info 0为YPBPR, 1为VGA
            Settings.System.putInt(context.getContentResolver(), HHTConstant.VGA_INFO, type);
            sendCommand(typeCommand);
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
                L.i(TAG, "getTotalMemory:" + str2 + "；split string:" + num + "\t");
            }
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();
        } catch (IOException e) {
        }
        // change totalMemeory to constant value
        long totalMemory_MB = initial_memory / 1024 / 1024;// B -> MB
        if (HHTConstant.DEBUG) L.d(TAG, "totalmemory-B:" + initial_memory + "B;totalmemory-MB:" + totalMemory_MB + "MB。");
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
        L.i(TAG, "mi.availMem;" + mi.availMem);
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
        L.i("Tools", "value============" + value);
        return value + suffix;
    }

    /**
     * 判断source plug status有效范围
     * @param strValue
     * @return
     */
    public static boolean sourcePlugStateValid(final String strValue){
//        TvManager.getInstance().getPlayerManager().detectInputSource();

        TvCommonManager.getInstance().GetInputSourceStatus();
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
//        if (IS_MH_BOARD){ // MH
//            return (sourceInput >= TvCommonManager.INPUT_SOURCE_VGA
//                    && sourceInput <= TvCommonManager.INPUT_SOURCE_STORAGE);
//        } else { // AH : 0,23,24,25,26   VGA,HDMI1,HDMI2,HDMI3,OPS
//            return (sourceInput == TvCommonManager.INPUT_SOURCE_VGA)
//                    || (sourceInput == TvCommonManager.INPUT_SOURCE_STORAGE)
//                    || (sourceInput >= 23
//                        && sourceInput <= TvCommonManager.INPUT_SOURCE_HDMI4);
//        }

        return sourceInput >= TvCommonManager.INPUT_SOURCE_VGA
                && sourceInput <= TvCommonManager.INPUT_SOURCE_STORAGE;
    }

    /**
     * 判断source Name有效范围
     * @param sourceNameKey
     * @return
     */
    public static boolean isSourceNameValid(final String sourceNameKey){
        int sourceInput = Tools.convertToInputSource(sourceNameKey);
        return isSourceInputValid(sourceInput);
    }

    /**
     * inputSource转换为sourceKey
     *
     * @param inputSource
     * @return
     */
    public static String convertToSourceKey(final int inputSource){
        L.d(TAG, "convertToSourceKey:   inputSource = "+inputSource);
        String sourceNameKey = "";
        // 判断 sourceInput 是否有效
        if (!isSourceInputValid(inputSource)) {
            return sourceNameKey;
        }

        if (IS_MH_BOARD){ // MH Board
            switch (inputSource){
                case TvCommonManager.INPUT_SOURCE_VGA:{// VGA
                    sourceNameKey = HHTConstant.VGA;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_ATV:{// ATV
                    sourceNameKey = HHTConstant.ATV;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_DTV:{ // DTV
                    sourceNameKey = HHTConstant.DTV;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI3:{// 23
                    sourceNameKey = HHTConstant.HDMI3; // HDMI1
                    int port = getTIPORT();
                    if(port == 2){ // HDMI2
                        sourceNameKey = HHTConstant.HDMI2;
                    } else if(port == 3){ // OPS
                        sourceNameKey = HHTConstant.OPS;
                    }
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI2:{// DP , TYPEC
                    sourceNameKey = HHTConstant.TYPEC;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI:{ // FRONT_HDMI
                    sourceNameKey = HHTConstant.HDMI1;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_YPBPR:{ // YPBPR
                    sourceNameKey = HHTConstant.YPBPR;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_CVBS:{// AV
                    sourceNameKey = HHTConstant.AV;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_STORAGE:{// Android
                    sourceNameKey = HHTConstant.ANDROID;
                    break;
                }
                default:
                    break;
            }
        } else if (isAHBoard()){ // AH Board
            switch (inputSource){
                case TvCommonManager.INPUT_SOURCE_VGA:{// VGA
                    sourceNameKey = HHTConstant.VGA;
                    break;
                }
                case 23:{ // HDMI1
                    sourceNameKey = HHTConstant.HDMI1;
                    try {
                        // HDMI_SEL state
                        int HDMI_SEL = 0x64;
                        int hdmiSel = TvManager.getInstance().getGpioDeviceStatus(HDMI_SEL);
                        L.d(TAG, "currentSource hdmiSel= " + hdmiSel);
                        if (hdmiSel == 1){ // OPS
                            sourceNameKey = HHTConstant.OPS;
                        }
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI2:{// HDMI2
                    sourceNameKey = HHTConstant.HDMI2;
                    break;
                }
                case 25:{ // HDMI3
                    sourceNameKey = HHTConstant.HDMI3;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI4:{ // OPS
                    sourceNameKey = HHTConstant.OPS;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_STORAGE:{// Android
                    sourceNameKey = HHTConstant.ANDROID;
                    break;
                }
                default:
                    break;
            }
        }else if (isLHBoard()){  //高清 ，前置，DP ,typc ,vga,atv,dtv, ybpr,av,ops,
            switch (inputSource){

                case TvCommonManager.INPUT_SOURCE_VGA:{// VGA
                    sourceNameKey = HHTConstant.VGA;
                    int pot =getTIPORT();
                    if (pot == 1){
                        sourceNameKey = HHTConstant.VGA;
                    }

                    break;
                }
                case TvCommonManager.INPUT_SOURCE_ATV:{// ATV
                    sourceNameKey = HHTConstant.ATV;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_DTV:{ // DTV
                    sourceNameKey = HHTConstant.DTV;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI:{// 25
                    sourceNameKey = "空"; // HDMI1
                    int port = getTIPORT();
                    if(port == 2){ // HDMI2
                        sourceNameKey = HHTConstant.HDMI2;
                    } else if(port == 3){ // dp
                        sourceNameKey = HHTConstant.DP;
                    }else if (port == 0){
                        sourceNameKey = HHTConstant.FRONT_HDMI;
                    }
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_HDMI3://23 OPS
                    sourceNameKey = HHTConstant.OPS;
                    break;
                case TvCommonManager.INPUT_SOURCE_HDMI2:{// TYPEC
                    sourceNameKey = HHTConstant.TYPEC;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_YPBPR:{ // YPBPR
                    sourceNameKey = HHTConstant.YPBPR;
                    int pot =getTIPORT();
                    if (pot == 1){
                        sourceNameKey = HHTConstant.YPBPR;
                    }
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_CVBS:{// AV
                    sourceNameKey = HHTConstant.AV;
                    break;
                }
                case TvCommonManager.INPUT_SOURCE_STORAGE:{// Android
                    sourceNameKey = HHTConstant.ANDROID;
                    break;
                }
                default:
                    break;
            }
        }

        return sourceNameKey;
    }

    /**
     * 判断当前source是否为指定的source
     * @param sourceNameKey
     * @return
     */
    public static boolean isCurrentSource(String sourceNameKey){
        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        String keyTmp = convertToSourceKey(inputSource);
        return TextUtils.equals(sourceNameKey, keyTmp);
    }

    /**
     * 获得硬件支持的inputSource列表
     *
     * @return
     */
    public static int[] getSupportInputSourceList(){
        String[] sourceNameKeys = getSourceKeyList();

        if (sourceNameKeys == null){
            return null;
        }

        int[] inputSources = new int[sourceNameKeys.length];
        for (int i = 0; i< sourceNameKeys.length; i++){
            inputSources[i] = convertToInputSource(sourceNameKeys[i]);
        }

        return inputSources;
    }

    /**
     * sourceNameKey转换为InputSource
     *
     * @param sourceKey
     * @return
     */
  public static int convertToInputSource(String sourceKey){
        int sourceInput = -1;

        if (IS_MH_BOARD){ // MH
            L.d(TAG, "convertToInputSource: IS_MH_BOARD");
            if (HHTConstant.DTV.equals(sourceKey)){ // DTV
                sourceInput = TvCommonManager.INPUT_SOURCE_DTV;
            }
            else if (HHTConstant.ATV.equals(sourceKey)){ // ATV
                sourceInput = TvCommonManager.INPUT_SOURCE_ATV;
            }
            else if (HHTConstant.VGA.equals(sourceKey)){ // VGA
                sourceInput = TvCommonManager.INPUT_SOURCE_VGA;
            }
            else if (HHTConstant.AV.equals(sourceKey)){ // AV
                sourceInput = TvCommonManager.INPUT_SOURCE_CVBS;
            }
            else if (HHTConstant.OPS.equals(sourceKey)){// OPS
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI3;
            }
            else if (HHTConstant.HDMI1.equals(sourceKey)){ // HDMI1
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI;
            }
            else if (HHTConstant.HDMI2.equals(sourceKey)){ // HDMI2
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI3;
            }
            else if (HHTConstant.HDMI3.equals(sourceKey)){ // FRONT_HDMI
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI3;
            }
            else if (HHTConstant.FRONT_HDMI.equals(sourceKey)){ // FRONT_HDMI
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI;
            }
            else if (HHTConstant.TYPEC.equals(sourceKey)){ // TYPEC
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI2;
            }
            else if (HHTConstant.DP.equals(sourceKey)){ // DP
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI2;
            }
            else if (HHTConstant.YPBPR.equals(sourceKey)){ // YPBPR
                sourceInput = TvCommonManager.INPUT_SOURCE_YPBPR;
            }
            else if (HHTConstant.ANDROID.equals(sourceKey)){ // Android
                sourceInput = TvCommonManager.INPUT_SOURCE_STORAGE;
            }
            else if (HHTConstant.STORAGE.equals(sourceKey)){  // Android
                sourceInput = TvCommonManager.INPUT_SOURCE_STORAGE;
            }
            else {
                sourceInput = -1;
            }
        } else if (isAHBoard()){
            // AH Board---- VGA,HDMI1,HDMI2,HDMI3,OPS:0,23,24,25,26
            L.d(TAG, "convertToInputSource: isAHBoard");
            if (HHTConstant.VGA.equals(sourceKey)){ // VGA
                sourceInput = TvCommonManager.INPUT_SOURCE_VGA;
            }
            else if (HHTConstant.HDMI1.equals(sourceKey)){ // HDMI1
                sourceInput = 23;
            }
            else if (HHTConstant.HDMI2.equals(sourceKey)){ // HDMI2
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI2;
            }
            else if (HHTConstant.HDMI3.equals(sourceKey)){ // HDMI3
                sourceInput = 25;
            }
            else if (HHTConstant.OPS.equals(sourceKey)){// OPS
                sourceInput = 26;
            }
            else if (HHTConstant.ANDROID.equals(sourceKey)){ // Android
                sourceInput = TvCommonManager.INPUT_SOURCE_STORAGE;
            }
            else if (HHTConstant.STORAGE.equals(sourceKey)){  // Android
                sourceInput = TvCommonManager.INPUT_SOURCE_STORAGE;
            }
            else {
                sourceInput = -1;
            }
        }else if (isLHBoard()){
            L.d(TAG, "convertToInputSource: isLHBoard:" + sourceKey);

            if (HHTConstant.DTV.equals(sourceKey)){ // DTV
                sourceInput = TvCommonManager.INPUT_SOURCE_DTV;
            }
            else if (HHTConstant.ATV.equals(sourceKey)){ // ATV
                sourceInput = TvCommonManager.INPUT_SOURCE_ATV;
            }
            else if (HHTConstant.VGA.equals(sourceKey)){ // VGA
                sourceInput = TvCommonManager.INPUT_SOURCE_VGA;
            }
            else if (HHTConstant.AV.equals(sourceKey)){ // AV
                sourceInput = TvCommonManager.INPUT_SOURCE_CVBS;
            }
            else if (HHTConstant.OPS.equals(sourceKey)){// OPS
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI3;
            }
            else if (HHTConstant.HDMI2.equals(sourceKey)){ // HDMI2
                L.d("hongcc", "tools------convertToInputSource: "+sourceKey);
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI;
            }
            else if (HHTConstant.FRONT_HDMI.equals(sourceKey)){ // FRONT_HDMI
                L.d("hongcc", "tools------convertToInputSource: "+sourceKey);
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI;
            }
            else if (HHTConstant.TYPEC.equals(sourceKey)){ // TYPEC
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI2;
            }
            else if (HHTConstant.DP.equals(sourceKey)){ // DP
                sourceInput = TvCommonManager.INPUT_SOURCE_HDMI;
            }
            else if (HHTConstant.YPBPR.equals(sourceKey)){ // YPBPR
                sourceInput = TvCommonManager.INPUT_SOURCE_YPBPR;
            }
            else if (HHTConstant.ANDROID.equals(sourceKey)){ // Android
                sourceInput = TvCommonManager.INPUT_SOURCE_STORAGE;
            }
            else if (HHTConstant.STORAGE.equals(sourceKey)){  // Android
                sourceInput = TvCommonManager.INPUT_SOURCE_STORAGE;
            }
            else {
                sourceInput = -1;
				L.d(TAG, "convertToInputSource: sourceInput = -1 error");
            }
        }

        return sourceInput;
    }

    /**
     * MH Board ,切换sourceInput时，切换VGA PORT 或者 TI PORT
     *
     * @param sourceNameKey
     * @return
     */
    public static void handleSetVGAOrTIPort(String sourceNameKey){
        String cmdStr = null;
        if(isLHBoard()){
            if (HHTConstant.FRONT_HDMI.equals(sourceNameKey)){
                cmdStr = TvosCommand.TVOS_COMMON_CMD_SET_HDMI_PORT + 0;
            }else if (HHTConstant.HDMI2.equals(sourceNameKey)){
                cmdStr = TvosCommand.TVOS_COMMON_CMD_SET_HDMI_PORT + 2;
            }else if (HHTConstant.DP.equals(sourceNameKey)){
                cmdStr = TvosCommand.TVOS_COMMON_CMD_SET_HDMI_PORT + 3;
            }else if (HHTConstant.VGA.equals(sourceNameKey)){ // VGA
                cmdStr = TvosCommand.TVOS_COMMON_CMD_SET_VGA_SEL + 1;
            } else if (HHTConstant.YPBPR.equals(sourceNameKey)){ // YPBPR
                cmdStr = TvosCommand.TVOS_COMMON_CMD_SET_VGA_SEL + 0;
            }

        }else {
            if (HHTConstant.HDMI3.equals(sourceNameKey)){ // HDMI3
                cmdStr = TvosCommand.TVOS_COMMON_CMD_SET_HDMI_PORT + 0;
            } else if (HHTConstant.HDMI2.equals(sourceNameKey)){ // HDMI2
                cmdStr = TvosCommand.TVOS_COMMON_CMD_SET_HDMI_PORT + 2;
            } else if (HHTConstant.OPS.equals(sourceNameKey)){// OPS
                cmdStr = TvosCommand.TVOS_COMMON_CMD_SET_HDMI_PORT + 3;
            } else if (HHTConstant.VGA.equals(sourceNameKey)){ // VGA
                cmdStr = TvosCommand.TVOS_COMMON_CMD_SET_VGA_SEL + 1;
            } else if (HHTConstant.YPBPR.equals(sourceNameKey)){ // YPBPR
                cmdStr = TvosCommand.TVOS_COMMON_CMD_SET_VGA_SEL + 0;
            }

        }


        if (!TextUtils.isEmpty(cmdStr)){
            L.d(TAG, "handleSetVGAOrTIPort cmdStr->" + cmdStr);
            sendCommand(cmdStr);
        }
    }

    /**
     * 获取温度
     */
    public static float getTmpValue(){
        float tmp = 20F;
        int[] values = sendCommand(TvosCommand.TVOS_COMMON_CMD_GET_TEMP);
        if (values != null && values.length > 0){
            tmp = values[0];
        }
        if (HHTConstant.DEBUG) L.d(TAG, "GetTemperature->" + tmp);
        return tmp;
    }

    /**
     * 获得光感值
     * @return
     */
    public static int getLightSense() {
        int lightSense = 60;
        int[] values = sendCommand(TvosCommand.TVOS_COMMON_CMD_GET_LIGHT_CTL_VAL);
        if (values != null && values.length > 0){
            lightSense = values[0];
        }
        return lightSense;
    }

    /**
     * 触摸框检测
     * @return true:OK false:NG
     */
    public static boolean checkTouch(){
        boolean ret = false;
        int[] values = sendCommand(TvosCommand.TVOS_INTERFACE_CMD_GET_TOUCH_VER);
        if (values != null && values.length > 0){
            ret = true;
        }
        return ret;
    }

    /**
     * 从tvos万能接口获得结果
     * @param tvosCmd
     * @return
     */
    public static int getResultFromTvOS(String tvosCmd){
        int ret = -1;
        int[] values = TvCommonManager.getInstance().setTvosCommonCommand(tvosCmd);
        L.d(TAG,"getResultFromTvOS:setTvosCommonCommand command->" + tvosCmd);
        if (values != null && values.length > 0){
            ret = values[0];
            L.d(TAG,"getResultFromTvOS:setTvosCommonCommand ret->" + ret);
        }
        return ret;
    }

    /**
     * 检测光控是否可用
     * @return true:OK false:NG
     */
    public static boolean isLightSensorNormal() {
        boolean ret = false;
        int[] values = sendCommand(TvosCommand.TVOS_COMMON_CMD_GET_LIGHT_CTL_STATUS);
        if (values != null && values.length > 0){
            ret = values[0] == 1;
        }
        return ret;
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

    /**
     * 执行shell命令
     * @param cmd
     * @return
     */
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
                    if (HHTConstant.DEBUG) L.d("UpgradeMainFun:", "setEnvironment Failed!");
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
            L.d("chen_powerdown", "GetOPSDEVICESTATUS:" + GetOPSDEVICESTATUS[0]);
            L.d("chen_powerdown", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);
            if (GetOPSDEVICESTATUS[0] == 0 || GetOPSPOWERSTATUS[0] == 0) {// 0,表示有OPS设备接入；1，表示没有OPS设备接入。
//                myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        DiaL close_diaL = close_builder.create();
//                        close_diaL.getWindow().setBackgroundDrawableResource(
//                                android.R.color.transparent);
//                        close_diaL.getWindow().setType(
//                                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                        close_diaL.setCanceledOnTouchOutside(false);
//                        close_diaL.show();
//                        WindowManager.LayoutParams lp = close_diaL.getWindow().getAttributes();
//                        lp.width = 640;
//                        close_diaL.getWindow().setAttributes(lp);
//                    }
//                });
                L.d("chen_powerdown", "start");
                L.d("chen_powerdown", "close ops");
                TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWER");
                Thread.sleep(200);
                TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWERON");
                L.d("chen_powerdown", "ops:state");
                Thread.sleep(2000);
                GetOPSDEVICESTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSDEVICESTATUS");
                GetOPSPOWERSTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSPOWERSTATUS");
                L.d("chen_powerdown", "GetOPSDEVICESTATUS:" + GetOPSDEVICESTATUS[0]);
                L.d("chen_powerdown", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);
                int count = 0;
                while (GetOPSPOWERSTATUS[0] == 1) {
                    L.d("chen_powerdown", "checkops state start ");
                    L.d("chen_powerdown", "checkops time count : " + count);
                    Thread.sleep(1000);
                    count++;
                    if (count == 30) {
                        L.d("chen_powerdown", "ops force now :count: " + count);
                        TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWER");
                        Thread.sleep(5 * 1000);
                        TvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWERON");
                        break;
                    }
                    L.d("chen_powerdown", "change ops state start");
                    GetOPSDEVICESTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                            "GetOPSDEVICESTATUS");
                    GetOPSPOWERSTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                            "GetOPSPOWERSTATUS");
                    L.d("chen_powerdown", "change ops state resutl:");
                    L.d("chen_powerdown", "GetOPSDEVICESTATUS:" + GetOPSDEVICESTATUS[0]);
                    L.d("chen_powerdown", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);
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
        if (currentSource >= TvCommonManager.INPUT_SOURCE_HDMI3
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
//            L.d(TAG, "isEasyTouchHide packageNameTop->" + packageNameTop);
            if (HHTConstant.TV_PLAY_PACKAGE.equals(packageNameTop)){ // 若Top 为TV_PLAY_PACKAGE时,则为false
                flag = true;
            }
        }

        if (HHTConstant.DEBUG) L.d(TAG, "isTvWindow is " + flag);

        return flag;
    }

    /**
     * 切换信号通道
     *
     * @param mContext
     * @param inputSource
     */
    public static void changeSignal(final Context mContext, final int inputSource){
        if (HHTConstant.DEBUG) L.d(TAG, "zhuzhu..changeSignal, inputSource:" + inputSource);
        new Thread(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent("com.mstar.android.intent.action.START_TV_PLAYER");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.putExtra("task_tag", "input_source_changed");
                intent.putExtra("inputSrc", inputSource);
                if (HHTConstant.DEBUG) L.d(TAG, "changeSignal:"+inputSource);

                try {
                    mContext.startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 切换信号通道
     *
     * @param mContext
     * @param sourceKey
     */
    public static boolean changeSignalBySourceKey(final Context mContext, final String sourceKey){
        final int inputSource = Tools.convertToInputSource(sourceKey);
        L.d(TAG, "changeSignalBySourceKey sourceKey:%s, changeSignal:%s",sourceKey, inputSource);
        // check source
        if (inputSource < TvCommonManager.INPUT_SOURCE_VGA
                || inputSource > TvCommonManager.INPUT_SOURCE_STORAGE) {
            return false;
        }

        // jump to source UI
        new Thread(new Runnable(){
            @Override
            public void run() {
                // chang VGA info or set IT port
                if (IS_MH_BOARD || isLHBoard()){ // MH Board
//                    TvCommonManager.getInstance().setInputSource(inputSource);
                    Tools.handleSetVGAOrTIPort(sourceKey); // change port
                }

                Intent intent = new Intent("com.mstar.android.intent.action.START_TV_PLAYER");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.putExtra("task_tag", "input_source_changed");
                intent.putExtra("inputSrc", inputSource);
                intent.putExtra("sourceName", sourceKey);
                if (HHTConstant.DEBUG) L.d(TAG, "changeSignal:"+inputSource);

                try {
                    mContext.startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        return true;
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
     * open OPS
     */
    public static boolean openOps() {
        // 1.判断OPS设备是否插入
        if (!isOpsPlugIn()){ // plug status is false
            L.d(TAG, "openOps:please check, the device is not plug in.");
            return false;
        }

        // 2.判断OPS设备在关机状态
        // ops power status
        int powerStatus = getResultFromTvOS(TvosCommand.TVOS_INTERFACE_CMD_GET_OPS_POWER_STATUS);

        // OPS GPIO口取反变量值
        String opsGpioStatus = SystemProperties.get(HHTConstant.PERSIST_GPIO_STATUS,"0");
        boolean isOpsCloed;
        L.d(TAG, "openOps ops power status:%s, ops gpio status:%s" ,
                powerStatus, opsGpioStatus);
        if (TextUtils.equals(opsGpioStatus, "0")) { //0 默认  1 取反
            // powerStatus:0，表示OPS关机状态; 1,表示OPS开机状态
            isOpsCloed = (powerStatus == 0);
        }else {
            //powerStatus:0,表示OPS开机状态；1，表示OPS关机状态。
            isOpsCloed = (powerStatus == 1);
        }

        boolean openFlag = false;

        // 3.若OPS在关闭状态时，执行开启OPS操作
        if (isOpsCloed){
            openOrCloseOpsAction();
            openFlag = true;
        }
        L.d(TAG, "openOps openFlag:%s", openFlag);
        return openFlag;
    }

    /**
     * close OPS
     */
    public static boolean closeOps() {
        // 1.判断OPS设备是否插入
        if (!isOpsPlugIn()){ // plug status is false
            L.d(TAG, "closeOps:please check, the device is not plug in.");
            return false;
        }

        // 2.判断OPS设备在开机状态
        // ops power status
        int powerStatus = getResultFromTvOS(TvosCommand.TVOS_INTERFACE_CMD_GET_OPS_POWER_STATUS);

        // 0,表示有OPS设备接入；1，表示没有OPS设备接入。
        String opsGpioStatus = SystemProperties.get(HHTConstant.PERSIST_GPIO_STATUS,"0");
        boolean isOpsOpened;
        L.d(TAG, "closeOps ops power status:%s, gpio status:%s" ,
                powerStatus, opsGpioStatus);
        if (TextUtils.equals(opsGpioStatus, "0")) { //0 默认  1 取反
            // 0，表示OPS关机状态; 1,表示OPS开机状态；
            isOpsOpened = (powerStatus == 1);
        }else {
            //0,表示OPS开机状态；1，表示OPS关机状态。
            isOpsOpened = (powerStatus == 0);
        }

        boolean closeFlag = false;

        // 3.若OPS处于开机状态，执行关闭OPS操作
        if (isOpsOpened){
            openOrCloseOpsAction();
            closeFlag = true;
        }

        L.d(TAG, "closeOps closeFlag:%s",  closeFlag);
        return closeFlag;
    }

    /**
     * 开关机触发端口操作
     */
    private static void openOrCloseOpsAction(){
        // "SetOPSPOWER"
        sendCommand(TvosCommand.TVOS_INTERFACE_CMD_OPS_POWER);
        SystemClock.sleep(300);
        // "SetOPSPOWERON"
        sendCommand(TvosCommand.TVOS_INTERFACE_CMD_OPS_POWER_HIGH);
    }

    /**
     * 判断OPS是否已经启动
     * @return
     */
    public static boolean isOpsOpen(){
        if (!isOpsPlugIn()){ // plug status is false
            L.d(TAG, "isOpsOpen:please check, the device is not plug in.");
            return false;
        }

        // 判断OPS是否处于开机状态
        boolean openFlag;
        int powerState = getResultFromTvOS(TvosCommand.TVOS_INTERFACE_CMD_GET_OPS_POWER_STATUS);
        String opsGpioStatus = SystemProperties.get(HHTConstant.PERSIST_GPIO_STATUS,"0");
        if (TextUtils.equals(opsGpioStatus, "0")) { //  0 默认  1 取反
            // 1,表示OPS开机；0，表示OPS关机
            openFlag = (powerState == 1);
        } else {
            //0,表示OPS开机；1，表示OPS关机。
            openFlag = (powerState == 0);
        }
        L.d(TAG, "isOpsOpen: openFlag:" + openFlag);
        return openFlag;
    }

    /**
     * 判断OPS是否已经插入卡座
     * @return
     */
    public static boolean isOpsPlugIn(){
        int plugStatus = getResultFromTvOS(TvosCommand.TVOS_INTERFACE_CMD_GET_OPS_DEVICE_STATUS);
        L.d(TAG, "isOpsPlugIn ops plug status:" + plugStatus);

        return plugStatus == 1;
    }

    /**
     * 发送命令到TVOS
     * @param command
     */
    public static int[] sendCommand(String command){
        L.d(TAG,"sendCommand:setTvosCommonCommand command->" + command);
        return TvCommonManager.getInstance().setTvosCommonCommand(command);
    }

    /**
     * 获得HDMITX
     *
     * @return
     */
    public static int getHDMITX(){
        int mode = HHTCommonManager.EnumHdmiOutMode.AUTO.ordinal();

        try {
            String hdmiTXMode = TvManager.getInstance().getEnvironment("HdmiOutResolute");
            if (TextUtils.isEmpty(hdmiTXMode)){
                return mode;
            }

            hdmiTXMode = hdmiTXMode.trim().toLowerCase();
            if (TextUtils.equals(hdmiTXMode, "720p")){
                mode = HHTCommonManager.EnumHdmiOutMode.HDMITX_720p_60.ordinal();
            }
            else if (TextUtils.equals(hdmiTXMode, "2k")){
                mode = HHTCommonManager.EnumHdmiOutMode.HDMITX_2k_60.ordinal();
            }
            else if (TextUtils.equals(hdmiTXMode, "4k")){
                mode = HHTCommonManager.EnumHdmiOutMode.HDMITX_4k_60.ordinal();
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return mode;
    }

    /**
     * 判断是否为MH版型
     * "ro.product.board":[CN8386_MH]
     * @return
     */
    public static boolean isMHBoard(){
        return TextUtils.equals(SystemProperties.get(HHTConstant.PRODUCT_BOARD,""), "CN8386_MH");
    }
    /**
     * 判断是否为BH版型
     * "ro.product.board":[CN8386_BH]
     * @return
     */
    public static boolean isBHBoard(){
        return TextUtils.equals(SystemProperties.get(HHTConstant.PRODUCT_BOARD,""), "CN8386_BH");
    }


    /**
     * 判断是否为AH版型
     * "ro.product.board":[CN8386_AH]
     * @return
     */
    public static boolean isAHBoard(){
		return TextUtils.equals(SystemProperties.get(HHTConstant.PRODUCT_BOARD,""), "CN8386_AH");
    }


    public static boolean isLHBoard(){
        return TextUtils.equals(SystemProperties.get(HHTConstant.PRODUCT_BOARD,""),"CN8386_LH");
    }

    /**
     * 获得版型
     *  "ro.product.board.model":[HH_C|HH_H]
     *
     * @return
     */
    public static String getBoardModel() {
        return SystemProperties.get(HHTConstant.PRODUCT_BOARD_MODEL, "HH_C");
    }

    /**
     * 判断待机状态：判断是否正在进行假待机
     * @return
     */
    public static boolean isPartialPoweroff(){
        boolean isPartialPoweroff = "1".equals(SystemProperties.get(HHTConstant.PRODUCT_STANDBY_STATE,"0"));
        return isPartialPoweroff;
    }

    /**
     * 获得共享信号源的状态
     * @return
     */
    public static boolean[] getPortDetGoodStatus(){
        L.d(TAG, "getPortDetGoodStatus start");
        boolean[] states = new boolean[3];
        if (IS_LH_BOARD){// NOT MH BOARD
            L.d(TAG, "       LH_BOARD       ");
            int[] goodStatuses = sendCommand(TvosCommand.TVOS_COMMON_CMD_GET_PORT_STATUS);
            if (goodStatuses == null || goodStatuses.length < 4){
                return states;
            }

            states[0] = goodStatuses[0] == 1;//前置
            states[1] = goodStatuses[2] == 1;// HDMI2 高清
            states[2] = goodStatuses[3] == 1;// DP
            L.d(TAG, "getPortDetGoodStatus, 前置:%s, 高清:%s, DP:%s",
                    goodStatuses[0],goodStatuses[2],goodStatuses[3]);
            return states;

        }
        int[] goodStatuses = sendCommand(TvosCommand.TVOS_COMMON_CMD_GET_PORT_STATUS);
        if (goodStatuses == null || goodStatuses.length < 4){
            return states;
        }

        states[0] = goodStatuses[0] == 1;// HDMI3
        states[1] = goodStatuses[2] == 1;// HDMI2
        states[2] = goodStatuses[3] == 1;// OPS
        L.d(TAG, "getPortDetGoodStatus, HDMI1:%s, HDMI2:%s, OPS:%s",
                goodStatuses[0],goodStatuses[2],goodStatuses[3]);
        return states;
    }

    /**
     * 获得所有信号通道的状态
     * @return
     */
    public static boolean[] getInputSourceStatus(){
        L.i(TAG, "getInputSourceStatus start");
        String[] sourceKeyList = getSourceKeyList();

        // get det state:HDMI1 HDMI2 OPS
        boolean[] portDetStatuses = getPortDetGoodStatus();

        // 获得信号源状态列表
        boolean[] inputSourceStatuses = TvCommonManager.getInstance().GetInputSourceStatus();

        if (inputSourceStatuses == null){
            return null;
        }

        // 遍历本地信号源值列表
        boolean[] sourceStatus = new boolean[sourceKeyList.length];

        for (int index = 0; index< sourceKeyList.length; index++){
            String sourceKey = sourceKeyList[index];
            int inputSource = convertToInputSource(sourceKey);

            // MH主板 判断共用source 23
            if (IS_MH_BOARD && inputSource == TvCommonManager.INPUT_SOURCE_HDMI3){
                if (TextUtils.equals(sourceKey, HHTConstant.HDMI3)){
                    sourceStatus[index] =  portDetStatuses[0];
                } else if (TextUtils.equals(sourceKey, HHTConstant.HDMI2)){
                    sourceStatus[index] =  portDetStatuses[1];
                } else if (TextUtils.equals(sourceKey, HHTConstant.OPS)){
                    sourceStatus[index] =  portDetStatuses[2];
                }

                L.i(TAG, "getInputSourceStatus sourceKey:%s, inputSource:%s, sourceStatus:%s",
                        sourceKey, inputSource, sourceStatus[index]);
                continue;
            }

            // 过滤原生信号通道的状态列表, 判断其他source
            for (int i = 0; i< inputSourceStatuses.length; i++){
                // MH主板 跳过共用信号的判断
                if (IS_MH_BOARD && (i == TvCommonManager.INPUT_SOURCE_HDMI3)){
                    continue;
                }

                // 记录信号状态
                if (i == inputSource){ // 信号值相等时
                    sourceStatus[index] = inputSourceStatuses[i];
                    L.i(TAG, "getInputSourceStatus sourceKey:%s, inputSource:%s, sourceStatus:%s",
                            sourceKey, inputSource, sourceStatus[index]);
                }
            }
        }

        L.i(TAG, "getInputSourceStatus end");
        return sourceStatus;
    }

    /**
     * 获得source key 列表
     * @return
     */
    public static String[] getSourceKeyList(){
        if (isLHBoard()){
            return HHTSourceService.LH_SOURCE_LIST;
        }else if(isAHBoard()){
            return HHTSourceService.AH_SOURCE_LIST;
        }else if (isMHBoard()){
            return HHTSourceService.MH_SOURCE_LIST;
        }else{
            return HHTSourceService.MH_SOURCE_LIST;
        }
    }

    /**
     * 更新信号状态列表
     *
     * @param srcPlugStateMap
     * @return
     */
    public static void updatePlugStateMap(Map<String,String> srcPlugStateMap){
        L.d(TAG, "updatePlugStateMap: start");
        if (IS_MH_BOARD){ // MH
            updatePlugStateMapMH(srcPlugStateMap);
        } else if(IS_AH_BOARD) { // AH
            updatePlugStateMapAH(srcPlugStateMap);
        }else if (IS_LH_BOARD){
            updatePlugStateMapLH(srcPlugStateMap);
        }
        L.d(TAG, "updatePlugStateMap: end");
    }

    /**
     * 更新信号状态列表MH
     *
     * @param srcPlugStateMap
     * @return
     */
    public static void updatePlugStateMapMH(Map<String,String> srcPlugStateMap){
        L.d(TAG, "updatePlugStateMapMH: start");
        String[] sourceKeyList = getSourceKeyList();

        // get det state:HDMI1 HDMI2 OPS
        boolean[] portDetStatuses = getPortDetGoodStatus();

        // 获得信号源状态列表
        boolean[] plugStatuses = TvCommonManager.getInstance().GetInputSourceStatus();

        if (plugStatuses == null){
            return;
        }

        // 遍历sourceKey
        for (int index = 0; index< sourceKeyList.length; index++){
            String sourceKey = sourceKeyList[index];
            int inputSource = convertToInputSource(sourceKey);// sourceKey -> inputSource

            // MH主板 判断共用source 23
            if (inputSource == TvCommonManager.INPUT_SOURCE_HDMI3){
                boolean status = false;
                if (TextUtils.equals(sourceKey, HHTConstant.HDMI3)){
                    status =  portDetStatuses[0];
                } else if (TextUtils.equals(sourceKey, HHTConstant.HDMI2)){
                    status =  portDetStatuses[1];
                } else if (TextUtils.equals(sourceKey, HHTConstant.OPS)){
                    status =  portDetStatuses[2];
                }

                String statusStr = status ? HHTConstant.SOURCE_DET_IN: HHTConstant.SOURCE_DET_OFF;
                srcPlugStateMap.put(sourceKey, statusStr);
                L.i(TAG, "updatePlugStateMapMH sourceKey:%s, inputSource:%s, statusStr:%s",
                        sourceKey, inputSource, statusStr);
                continue;
            }

            // 过滤原生信号通道的状态列表, 判断其他source
            for (int i = 0; i< plugStatuses.length; i++){
                // MH主板 跳过共用信号的判断
                if (i == TvCommonManager.INPUT_SOURCE_HDMI3){
                    continue;
                }

                // 记录信号状态
                if (i == inputSource){ // 信号值相等时
                    String statusStr = plugStatuses[i] ? HHTConstant.SOURCE_DET_IN: HHTConstant.SOURCE_DET_OFF;
                    srcPlugStateMap.put(sourceKey, statusStr);
                    L.i(TAG, "updatePlugStateMapMH sourceKey:%s, inputSource:%s, statusStr:%s",
                            sourceKey, inputSource, statusStr);
                }
            }
        }

        L.d(TAG, "updatePlugStateMapMH: end");
    }

    /**
     * 更新信号状态列表AH
     *
     * @param srcPlugStateMap
     * @return
     */
    public static void updatePlugStateMapAH(Map<String,String> srcPlugStateMap){
        L.d(TAG, "updatePlugStateMapAH: start");
        String[] sourceKeyList = getSourceKeyList();

        // 获得信号源状态列表
        boolean[] plugStatuses = TvCommonManager.getInstance().GetInputSourceStatus();

        if (plugStatuses == null){
            return;
        }

        // 遍历sourceKey
        for (int index = 0; index< sourceKeyList.length; index++){
            String sourceKey = sourceKeyList[index];
            int inputSource = convertToInputSource(sourceKey);// sourceKey -> inputSource

            // 过滤原生信号通道的状态列表, 判断其他source
            for (int i = 0; i< plugStatuses.length; i++){
                // 记录信号状态
                if (i == inputSource){ // 信号值相等时
                    String statusStr = plugStatuses[i] ? HHTConstant.SOURCE_DET_IN: HHTConstant.SOURCE_DET_OFF;
                    srcPlugStateMap.put(sourceKey, statusStr);
                    L.i(TAG, "updatePlugStateMapAH sourceKey:%s, inputSource:%s, statusStr:%s",
                            sourceKey, inputSource, statusStr);
                }
            }
        }

        L.d(TAG, "updatePlugStateMapAH: end");
	}
    /**
     * 更新信号状态列表LH
     *
     * @return
     */
    public static void updatePlugStateMapLH(Map<String,String> srcPlugStateMap){
        L.d(TAG, "updatePlugStateMapLH: start");
        String[] sourceKeyList = getSourceKeyList();

        // get det state:前置 HDMI2 DP
        boolean[] portDetStatuses = getPortDetGoodStatus();

        // 获得信号源状态列表
        boolean[] plugStatuses = TvCommonManager.getInstance().GetInputSourceStatus();

        if (plugStatuses == null){
			L.d(TAG, "updatePlugStateMapLH: plugStatuses == null");
            return;
        }

        // 遍历sourceKey
        for (int index = 0; index< sourceKeyList.length; index++){
            String sourceKey = sourceKeyList[index];
            int inputSource = convertToInputSource(sourceKey);// sourceKey -> inputSource

            // LH主板 判断共用source 25
            if (inputSource == 25){
                boolean status = false;
                if (TextUtils.equals(sourceKey, HHTConstant.FRONT_HDMI)){
                    status =  portDetStatuses[0];
                } else if (TextUtils.equals(sourceKey, HHTConstant.HDMI2)){
                    status =  portDetStatuses[1];
                } else if (TextUtils.equals(sourceKey, HHTConstant.DP)){
                    status =  portDetStatuses[2];
                }

                String statusStr = status ? HHTConstant.SOURCE_DET_IN: HHTConstant.SOURCE_DET_OFF;
                srcPlugStateMap.put(sourceKey, statusStr);
                L.i(TAG, "updatePlugStateMapLH sourceKey:%s, inputSource:%s, statusStr:%s",
                        sourceKey, inputSource, statusStr);
                continue;
            }

            // 过滤原生信号通道的状态列表, 判断其他source
            for (int i = 0; i< plugStatuses.length; i++){
                // MH主板 跳过共用信号的判断
                if (i == TvCommonManager.INPUT_SOURCE_HDMI3){

                    continue;

                }

                // 记录信号状态
                if (i == inputSource){ // 信号值相等时
                    String statusStr = plugStatuses[i] ? HHTConstant.SOURCE_DET_IN: HHTConstant.SOURCE_DET_OFF;
                    srcPlugStateMap.put(sourceKey, statusStr);
                    L.i(TAG, "updatePlugStateMapMH sourceKey:%s, inputSource:%s, statusStr:%s",
                            sourceKey, inputSource, statusStr);
                }
            }
        }

        L.d(TAG, "updatePlugStateMapMH: end");
    }

    /**
     *检测OPS 螺丝状态
     *
     *@return false unlock, true  lock
     *
     */
    public static boolean getOpsLockStatus(){
        String cmdStr = TvosCommand.TVOS_COMMON_CMD_QUERY_TCA9539 + "#2";
        return getResultFromTvOS(cmdStr) == 1;
    }

    /**
     * 判断是否为4K模式
     * @return true：4k, false：2K
     */
    public static boolean is4K2KMode(){
        String status4K = SystemProperties.get(HHTConstant.SYS_CTV_4K_STATUS, "false");
        return TextUtils.equals(status4K.toLowerCase(), "true");
    }

    /**
     * 根据信号源改变图片模式
     *
     * @param mContext
     * @param ePicMode
     * @param inputSource
     * // @see TvPictureService.setPictureModeIdx(int ePicMode)
     * @return
     */
    public static boolean setPictureModeIdx(Context mContext, int ePicMode, int inputSource) {
        // TODO: don't we need to update PicMode setting after set them to
        // TvOS??
        L.d(TAG, "setPictureModeIdx: start inputSource:%s, ePicMode:%s", inputSource, ePicMode);
        IDatabaseDesk.PictureModeSetting picturMode = DatabaseDesk.getInstance(mContext)
                .queryPictureModeSettings(ePicMode, inputSource);
        DatabaseDesk.getInstance(mContext).updatePictureMode(ePicMode, inputSource);
        try {
            TvManager.getInstance().getPictureManager()
                    .setPictureModeBrightness(picturMode.brightness);
            TvManager.getInstance().getPictureManager().setPictureModeContrast(picturMode.contrast);
            TvManager.getInstance().getPictureManager().setPictureModeColor(picturMode.saturation);
            TvManager.getInstance().getPictureManager().setPictureModeSharpness(picturMode.sharpness);
            TvManager.getInstance().getPictureManager().setPictureModeTint(picturMode.hue);
            TvManager.getInstance().getPictureManager().setBacklight(picturMode.backlight);
        } catch (TvCommonException e) {
            e.printStackTrace();
            L.d(TAG, "setPictureModeIdx: error:" + e.getMessage());
            return false;
        }
        L.d(TAG, "setPictureModeIdx: end");
        return true;
    }

    /**
     * 根据信号源改变图片模式
     *
     * @param mContext
     * @param ePicMode
     * // @see TvPictureService.setPictureModeIdx(int ePicMode)
     * @return
     */
    public static boolean setPictureModeForAllSource(Context mContext, int ePicMode) {
        L.d(TAG, "setPictureModeForAllSource: start");
        String[] sourceKeyList = getSourceKeyList();
        boolean isFirstHDMI3 = true;// 共用信号通道只执行一次的标识
        // 遍历sourceKey
        for (String sourceKey : sourceKeyList) {
            int inputSource = convertToInputSource(sourceKey);// sourceKey -> inputSource

            // HDMI3共用信号通道只执行一次
            if (inputSource == TvCommonManager.INPUT_SOURCE_HDMI3) {
                if (!isFirstHDMI3) {
                    continue;
                } else {
                    isFirstHDMI3 = false;
                }
            }

            if(inputSource == TvCommonManager.getInstance().getCurrentTvInputSource()){
                setPictureModeIdx(mContext, ePicMode, inputSource);
            }else{
                IDatabaseDesk.PictureModeSetting picturMode = DatabaseDesk.getInstance(mContext)
                .queryPictureModeSettings(ePicMode, inputSource);
                DatabaseDesk.getInstance(mContext).updatePictureMode(ePicMode, inputSource);
            }
        }
        L.d(TAG, "setPictureModeForAllSource: end");
        return true;
    }
}
