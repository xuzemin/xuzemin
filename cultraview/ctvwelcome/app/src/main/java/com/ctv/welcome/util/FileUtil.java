
package com.ctv.welcome.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import com.ctv.welcome.R;
import com.ctv.welcome.activity.IndexActivity;
import com.ctv.welcome.constant.Config;
import com.ctv.welcome.task.VIPApplication;
import com.ctv.welcome.vo.LocalFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class FileUtil {
    public static final String BMP = ".bmp";

    public static final String JPEG = ".jpeg";

    public static final String JPG = ".jpg";

    public static final String PNG = ".png";

    public static final String GIF = ".gif";

    public static final String[] IMAGES = new String[] {
            JPG, JPEG, GIF, PNG, BMP
    };



    public static boolean checkFileExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return new File(path).exists();
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static WeakReference<Bitmap> takeViewScreenShot(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return new WeakReference(cache);
    }

    public static WeakReference<Bitmap> takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        LogUtils.i("FileUtil", "screen width:" + width + ",height:" + height);
        WeakReference<Bitmap> bitmapWeakReference = new WeakReference(Bitmap.createBitmap(b1, 0,
                statusBarHeight, width, height - statusBarHeight));
        view.destroyDrawingCache();
        return bitmapWeakReference;
    }

    public static WeakReference<Bitmap> takeIndexActivity(Activity activity, int left, int top) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        LogUtils.d("FileUtil", "takeIndexActivity,width:" + width + ",height:" + height);
        if (IndexActivity.mIsWindowMode) {
            width = (int) (((float) width) * 0.8f);
            height = (int) (((float) height) * 0.8f);
        }
        WeakReference<Bitmap> bitmapWeakReference = new WeakReference(Bitmap.createBitmap(b1, left,
                top, width, height));
        view.destroyDrawingCache();
        return bitmapWeakReference;
    }

    public static boolean copyPic(String srcFilePath, String dstFileDir, String fileName) {
        IOException e;
        Throwable th;
        boolean z = false;
        File srcFile = new File(srcFilePath);
        if (srcFile.exists()) {
            File dstDir = new File(dstFileDir);
            if (!dstDir.exists()) {
                dstDir.mkdirs();
            }
            File dstFile = new File(dstDir, fileName);
            FileInputStream in = null;
            FileOutputStream out = null;
            if (dstFile.exists()) {
                dstFile.delete();
            }
            try {
                dstFile.createNewFile();
                byte[] buffer = new byte[1024];
                FileInputStream in2 = new FileInputStream(srcFile);
                try {
                    FileOutputStream out2 = new FileOutputStream(dstFile);
                    while (true) {
                        try {
                            int len = in2.read(buffer, 0, buffer.length);
                            if (len == -1) {
                                break;
                            }
                            out2.write(buffer, 0, len);
                        } catch (IOException e2) {
                             e2.printStackTrace();
                            LogUtils.i(TAG, "copyPic,IOException e2:" + e2.toString());
                        }
                    }
                    z = true;
                    if (in2 != null) {
                        try {
                            in2.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                            LogUtils.i(TAG, "copyPic,IOException e3:" + e3.toString());
                        }
                    }
                    if (out2 != null) {
                        out2.close();
                    }
                } catch (IOException e4) {
                    LogUtils.i(TAG, "copyPic,IOException e4:" + e4.toString());
                }
            } catch (IOException e5) {
                LogUtils.i(TAG, "copyPic,IOException e5:" + e5.toString());
            }
        }
        LogUtils.i(TAG, "copyPic,z:" + z);
        return z;
    }

    public static String saveSignature(String baseDir, WeakReference<Bitmap> b) {
        String filename = new SimpleDateFormat("yyyyMMddkkmmss").format(new Date(System
                .currentTimeMillis())) + JPEG;
        try {
            FileOutputStream fos = new FileOutputStream(new File(getAlbumStorageDir(baseDir,
                    Config.SIGNATURE_PICTURE), filename));
            Bitmap bitmap = (Bitmap) b.get();
            if (bitmap != null) {
                bitmap.compress(CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();
                bitmap.recycle();
                System.gc();
            }
        }catch(Exception e){
            return null;
        }
        return filename;
    }

    public static boolean savePic(String dir, WeakReference<Bitmap> b, String filename) {
        IOException e;
        Throwable th;
        boolean z = false;
        FileOutputStream fos = null;
        File baseDir = new File(dir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        try {
            File photo = new File(baseDir, filename + JPEG);
            if (!photo.exists()) {
                if (b != null) {
                    Bitmap bitmap = (Bitmap) b.get();
                    if (bitmap != null) {
                        FileOutputStream fos2 = new FileOutputStream(photo);
                        try {
                            bitmap.compress(CompressFormat.JPEG, 90, fos2);
                            bitmap.recycle();
                            fos2.flush();
                            fos2.close();
                            System.gc();
                            z = true;
                            if (fos2 != null) {
                                try {
                                    fos2.close();
                                    System.gc();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                            fos = fos2;
                        } catch (IOException e3) {

                        }
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                        System.gc();
                    } catch (IOException e2222) {
                        e2222.printStackTrace();
                    }
                }
            } else if (fos != null) {
                try {
                    fos.close();
                    System.gc();
                } catch (IOException e22222) {
                    e22222.printStackTrace();
                }
            }
        } catch (IOException e4) {
            return z;
        }
        return z;
    }

    public static String getAlbumStoragePath(String baseDir, String albumName) {
        return baseDir + albumName;
    }

    public static String getBaseStorageDir() {
        String baseDir = "";
        String storeType = DBUtil.getPicStoreType();
        LogUtils.i("FileUtil", "getBaseStorageDir,storeType:" + storeType);
        return storeType + "/";
    }

    public static String getBaseStorageDir(String storeType) {
        String baseDir = "";
        return storeType + "/";
    }

    public static File getAlbumStorageDir(String baseDir, String albumName) {
        File file = new File(baseDir, albumName);
        if (!file.mkdirs()) {
            LogUtils.e("FileUtil,Directory not created");
        }
        return file;
    }

    public static String getSecondaryStorageDirectory() {
        try {
            Class<?> clazz = Class.forName("android.os.Environment");
            Object environment = clazz.newInstance();
            Method method = clazz.getDeclaredMethod("getSecondaryStorageDirectory", new Class[0]);
            method.setAccessible(true);
            return (String) method.invoke(environment, new Object[0]);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        } catch (NoSuchMethodException e4) {
            e4.printStackTrace();
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
        }
        return "";
    }

    public static String getSecondaryStorageState() {
        try {
            Class<?> clazz = Class.forName("android.os.Environment");
            Object environment = clazz.newInstance();
            Method method = clazz.getDeclaredMethod("getSecondaryStorageState", new Class[0]);
            method.setAccessible(true);
            return (String) method.invoke(environment, new Object[0]);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        } catch (NoSuchMethodException e4) {
            e4.printStackTrace();
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
        }
        return "";
    }

    public static ArrayList<LocalFile> listPictures(File file) {
        ArrayList<LocalFile> result = new ArrayList();
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            String innerStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (listFiles != null && listFiles.length > 0) {
                for (File listFile : listFiles) {
                    LocalFile localFile;
                    String absoluteFile;
                    String displayName;
                    if (listFile.isDirectory()) {
                        LogUtils.i("FileUtil",
                                "listPictures,dir path:" + listFile.getAbsolutePath());
                        if (listFile.getAbsolutePath().startsWith(Config.UDISK_PATH)) {
                            File[] dirs = listFile.listFiles();
                            if (dirs != null) {
                                if (dirs.length == 0) {
                                }
                            }
                        }
                        if (!(listFile.getName().equals(Config.SDBACKUPPATH)
                                || listFile.getName().equals(
                                        Config.CUSTOM_CATEGORY_CONTENT_DIR_NAME)
                                || listFile.getName().equals(Config.SIGNATURE_PICTURE) || listFile
                                .getName().equals("QRCode"))) {
                            localFile = new LocalFile();
                            absoluteFile = listFile.getAbsoluteFile().toString();
                            displayName = absoluteFile.substring(absoluteFile.lastIndexOf(47) + 1,
                                    absoluteFile.length());
                            localFile.setFileType(1);
                            localFile.setFileName(displayName);
                            localFile.setFilePath(absoluteFile);
                            if (absoluteFile.contains(innerStorage)) {
                                localFile.setFilePathText(absoluteFile.replace(
                                        innerStorage,
                                        VIPApplication.getContext().getString(
                                                R.string.inner_storage)));
                            } else {
                                localFile.setFilePathText(absoluteFile);
                            }
                            result.add(localFile);
                        }
                    }
                    if (listFile.isFile()
                            && (listFile.getName().toLowerCase().endsWith(JPG)
                                    || listFile.getName().toLowerCase().endsWith(JPEG)
                                    || listFile.getName().toLowerCase().endsWith(PNG) || listFile
                                    .getName().toLowerCase().endsWith(BMP))) {
                        localFile = new LocalFile();
                        absoluteFile = listFile.getAbsoluteFile().toString();
                        String suffix = absoluteFile.substring(absoluteFile.lastIndexOf("."),
                                absoluteFile.length());
                        displayName = absoluteFile.substring(absoluteFile.lastIndexOf(47) + 1,
                                absoluteFile.length());
                        localFile.setFileType(0);
                        localFile.setSuffix(suffix);
                        localFile.setFileName(displayName);
                        localFile.setFilePath(absoluteFile);
                        if (absoluteFile.contains(innerStorage)) {
                            localFile.setFilePathText(absoluteFile.replace(innerStorage,
                                    VIPApplication.getContext().getString(R.string.inner_storage)));
                        } else {
                            localFile.setFilePathText(absoluteFile);
                        }
                        result.add(localFile);
                    }
                }
            }
        }
        return result;
    }

    public static ArrayList<LocalFile> listFileDetail(File file) {
        ArrayList<LocalFile> fileList = new ArrayList();
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            String innerStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (listFiles != null && listFiles.length > 0) {
                for (File listFile : listFiles) {
                    LocalFile localFile;
                    String absoluteFile;
                    String displayName;
                    if (listFile.isDirectory()) {
                        LogUtils.i("FileUtil",
                                "listPictures,dir path:" + listFile.getAbsolutePath());
                        if (listFile.getAbsolutePath().equals(Config.UDISK_PATH)) {
                            File[] dirs = listFile.listFiles();
                            if (dirs != null) {
                                if (dirs.length == 0) {
                                }
                            }
                        }
                        if (!(listFile.getName().equals(Config.SDBACKUPPATH)
                                || listFile.getName().equals(
                                        Config.CUSTOM_CATEGORY_CONTENT_DIR_NAME)
                                || listFile.getName().equals(Config.SIGNATURE_PICTURE) || listFile
                                .getName().equals("QRCode"))) {
                            localFile = new LocalFile();
                            absoluteFile = listFile.getAbsoluteFile().toString();
                            displayName = absoluteFile.substring(absoluteFile.lastIndexOf(47) + 1,
                                    absoluteFile.length());
                            localFile.setFileType(1);
                            localFile.setFileName(displayName);
                            localFile.setFilePath(absoluteFile);
                            localFile.setSize(getFileSize(listFile));
                            localFile.setModifyTime(getFileModifyTime(listFile));
                            if (absoluteFile.contains(innerStorage)) {
                                localFile.setFilePathText(absoluteFile.replace(
                                        innerStorage,
                                        VIPApplication.getContext().getString(
                                                R.string.inner_storage)));
                            } else {
                                localFile.setFilePathText(absoluteFile);
                            }
                            fileList.add(localFile);
                        }
                    } else if (listFile.isFile()
                            && (listFile.getName().toLowerCase().endsWith(JPG)
                                    || listFile.getName().toLowerCase().endsWith(JPEG)
                                    || listFile.getName().toLowerCase().endsWith(PNG) || listFile
                                    .getName().toLowerCase().endsWith(BMP))) {
                        localFile = new LocalFile();
                        absoluteFile = listFile.getAbsoluteFile().toString();
                        String suffix = absoluteFile.substring(absoluteFile.lastIndexOf("."),
                                absoluteFile.length());
                        displayName = absoluteFile.substring(absoluteFile.lastIndexOf(47) + 1,
                                absoluteFile.length());
                        localFile.setFileType(0);
                        localFile.setSuffix(suffix);
                        localFile.setFileName(displayName);
                        localFile.setFilePath(absoluteFile);
                        localFile.setSize(getFileSize(listFile));
                        localFile.setModifyTime(getFileModifyTime(listFile));
                        if (absoluteFile.contains(innerStorage)) {
                            localFile.setFilePathText(absoluteFile.replace(innerStorage,
                                    VIPApplication.getContext().getString(R.string.inner_storage)));
                        } else {
                            localFile.setFilePathText(absoluteFile);
                        }
                        fileList.add(localFile);
                    }
                }
            }
        }
        return fileList;
    }

    private static String getFileSize(File dir) {
        String size = "0B";
        long length = -1;
        if (!dir.isDirectory()) {
            return Formatter.formatFileSize(VIPApplication.getContext(), dir.length());
        }
        File[] files = dir.listFiles();
        if (files == null || files.length <= 0) {
            return size;
        }
        for (File file : files) {
            length += file.length();
        }
        return Formatter.formatFileSize(VIPApplication.getContext(), length);
    }

    private static String getFileModifyTime(File file) {
        return new SimpleDateFormat("yyyy/MM/d/").format(new Date(file.lastModified()));
    }
}
