package com.yinghe.whiteboardlib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 文件操作类
 * Created by Nereo on 2015/4/8.
 */
public class FileUtils {

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    public static File createTmpFile(Context context) throws IOException{
        File dir = null;
        if(TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (!dir.exists()) {
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");
                if (!dir.exists()) {
                    dir = getCacheDirectory(context, true);
                }
            }
        }else{
            dir = getCacheDirectory(context, true);
        }
        return File.createTempFile(JPEG_FILE_PREFIX, JPEG_FILE_SUFFIX, dir);
    }


    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted and app has appropriate permission. Else -
     * Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> (if card is mounted and app has appropriate permission) or
     * on device's file system depending incoming parameters.
     *
     * @param context        Application context
     * @param preferExternal Whether prefer external location for cache
     * @return Cache {@link File directory}
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) { // (sh)it happens too (Issue #989)
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * Returns individual application cache directory (for only image caching from ImageLoader). Cache directory will be
     * created on SD card <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is mounted and app has
     * appropriate permission. Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache {@link File directory}
     */
    public static File getIndividualCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(appCacheDir, cacheDir);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = appCacheDir;
            }
        }
        return individualCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    private static String getLocalPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }

        return null;
    }

    private static String findFileInDir(File FileDir, String filenmame) {
        try {
            String File_direct = FileDir.getAbsolutePath();
            final String FilwName = filenmame;// "cultraview_test.mp3";
            File_direct = File_direct + "/" + FilwName;
            File TestMusicFile = new File(File_direct);
            if (TestMusicFile.exists() && TestMusicFile.canRead()) {
                return File_direct;
            }

        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static long getLastUpgradFileModified(Context mContext) {
        SharedPreferences sharedData = mContext.getSharedPreferences("apk_upgrade", Context.MODE_PRIVATE);
        long lastModified = sharedData.getLong("lastModified", 0);
        return lastModified;
    }

    public static long getLastUpgradFileModified(Context mContext, String fileName) {
        SharedPreferences sharedData = mContext.getSharedPreferences("apk_upgrade", Context.MODE_PRIVATE);
        long lastModified = sharedData.getLong( fileName+ "_lastModified", 0);
        return lastModified;
    }

    /**
     * 在USB中找文件
     * @param cn
     * @param filenmame
     * @return
     */
    public static String FindFileInUsb(Context cn, String filenmame) {
        try {
            String[] usbPath = getExternelUsbPaths(cn);
            if (usbPath == null) {
                return null;
            }
            for (int i = 0; i < usbPath.length; i++) {
                // String path=findFile(usbPath[i],filenmame);
                String path = findFileInDir(new File(usbPath[i]), filenmame);
                Log.d("xue", "xue Usb path " + usbPath[i]);
                if (path != null) {
                    return path;
                }
            }

        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static String[] getExternelUsbPaths(Context cn) {
        String[] paths = null;

        StorageManager storageManager = (StorageManager) cn.getSystemService(Context.STORAGE_SERVICE);
        try {
            paths = (String[]) StorageManager.class.getMethod("getVolumePaths" ).invoke(storageManager);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (paths == null)
            return paths;
        if (paths.length > 1) {
            ArrayList<String> array = new ArrayList<String>();
            for (int i = 0; i < paths.length; i++) {
                File f = new File(paths[i]);
                if (paths[i].equals(getLocalPath()) == false && f.exists()) {

                    if (f.getFreeSpace() > 100)// Double Check the usb Storage
                    // is still exit
                    {
                        array.add(paths[i]);
                    }

                }

            }
            String[] temp = new String[array.size()];
            array.toArray(temp);
            return temp;
        } else {
            // bg_2.setTag("null");
            return null;
        }
    }

}
