
package com.ctv.welcome.util;

import android.annotation.SuppressLint;
import android.os.Environment;
import com.ctv.welcome.constant.Config;
import com.ctv.welcome.vo.CategoryContentData;
import com.ctv.welcome.vo.CustomContentData;
import com.ctv.welcome.vo.PicData;
import com.ctv.welcome.vo.PicDataDao.Properties;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.query.WhereCondition;

public final class FileUtils {
    private static CustomContentData data;

    public static List<CustomContentData> getImagePathFromSD() {
        List<CustomContentData> imagePathList = new ArrayList();
        String filePath = Environment.getExternalStorageDirectory() + "/com.ctv.welcome/custom";
        imagePathList.clear();
        File fileAll = new File(filePath);
        if (fileAll.exists()) {
            File[] files = fileAll.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (checkIsImageFile(file.getPath())) {
                        CustomContentData customContentData = new CustomContentData();
                        String name = file.getName();
                        String substring = name.substring(0, name.lastIndexOf("."));
                        if (((PicData) DBUtil.mDaoSession.getPicDataDao().queryBuilder()
                                .where(Properties.FileName.like(substring), new WhereCondition[0])
                                .build().unique()) != null) {
                            customContentData.setFilename(substring);
                            customContentData.setFilepath(file.getPath());
                            imagePathList.add(customContentData);
                        }
                    }
                }
            }
        }
        return imagePathList;
    }

    @SuppressLint({
        "DefaultLocale"
    })
    public static boolean checkIsImageFile(String fName) {
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        return FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg") || FileEnd.equals("bmp");
    }

    public static List<CategoryContentData> getCategoryFiles(String url) {
        File mainFile = new File(url);
        if (!mainFile.exists()) {
            return null;
        }
        File[] files = mainFile.listFiles();
        List<CategoryContentData> paths = new ArrayList();
        if (files == null || files.length <= 0) {
            return null;
        }
        for (File file1 : files) {
            if (file1.isDirectory()) {
                List<CategoryContentData> files1 = getCategoryFiles(file1.getAbsolutePath());
                if (files1 != null && files1.size() > 0) {
                    paths.addAll(files1);
                }
            } else if (file1.getAbsolutePath().endsWith(FileUtil.JPG)
                    || file1.getAbsolutePath().endsWith(FileUtil.PNG)
                    || file1.getAbsolutePath().endsWith(FileUtil.JPEG)
                    || file1.getAbsolutePath().endsWith(FileUtil.BMP)) {
                String path = file1.toString();
                String name = new File(path).getName();
                String substring = name.substring(0, name.lastIndexOf("."));
                CategoryContentData data = new CategoryContentData();
                data.setName(substring);
                data.setIcon(path);
                data.setText(substring);
                data.setImageRes(path);
                paths.add(data);
            }
        }
        return paths;
    }

    public static List<String> getDirNames(String path) {
        List<String> nameList = new ArrayList();
        File mainFile = new File(path);
        if (mainFile.exists()) {
            File[] files = mainFile.listFiles();
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    if (file1.isDirectory()) {
                        nameList.add(file1.getName());
                    }
                }
            }
        }
        return nameList;
    }

    public static List<CustomContentData> getFiles(String url, boolean isAdd) {
        List<CustomContentData> paths = new ArrayList();
        File mainFile = new File(url);
        if (mainFile.exists()) {
            File[] files = mainFile.listFiles();
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    if (file1.isDirectory()) {
                        if (!(file1.getName().equals(Config.SDBACKUPPATH)
                                || file1.getName().equals(Config.THEME_PICTURE)
                                || file1.getName().equals("QRCode") || file1.getName().equals(
                                Config.CUSTOM_CATEGORY_CONTENT_DIR_NAME))) {
                            List<CustomContentData> files1 = getFiles(file1.getAbsolutePath(),
                                    isAdd);
                            if (files1 != null && files1.size() > 0) {
                                paths.addAll(files1);
                            }
                        }
                    } else if (file1.getAbsolutePath().endsWith(FileUtil.JPG)
                            || file1.getAbsolutePath().endsWith(FileUtil.PNG)
                            || file1.getAbsolutePath().endsWith(FileUtil.JPEG)
                            || file1.getAbsolutePath().endsWith(FileUtil.BMP)) {
                        String path = file1.toString();
                        String name = new File(path).getName();
                        String substring = name.substring(0, name.lastIndexOf("."));
                        if (isAdd) {
                            List<PicData> unique = DBUtil.mDaoSession
                                    .getPicDataDao()
                                    .queryBuilder()
                                    .where(Properties.FileName.like(substring),
                                            new WhereCondition[0]).list();
                            if (unique != null && unique.size() > 0) {
                                data = new CustomContentData();
                                data.setFilepath(path);
                                data.setFilename(substring);
                                paths.add(data);
                            }
                        } else {
                            data = new CustomContentData();
                            data.setFilepath(path);
                            data.setFilename(substring);
                            paths.add(data);
                        }
                    }
                }
            }
        }
        return paths;
    }

    public static List<CustomContentData> getUSBFiles() {
        File mainFile = new File("/mnt/usb");
        if (mainFile.exists()) {
            File[] files = mainFile.listFiles();
            List<CustomContentData> arrayList = new ArrayList();
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    String usbFilePath = file1.getAbsolutePath();
                    if (file1.isDirectory()) {
                        List<CustomContentData> files1 = getFiles(file1.getAbsolutePath(), false);
                        if (files1 != null && files1.size() > 0) {
                            arrayList.addAll(files1);
                        }
                    } else if (file1.getAbsolutePath().endsWith(FileUtil.JPG)
                            || file1.getAbsolutePath().endsWith(FileUtil.PNG)) {
                        String path = file1.toString();
                        String name = new File(path).getName();
                        String substring = name.substring(0, name.lastIndexOf("."));
                        data = new CustomContentData();
                        data.setFilepath(path);
                        data.setFilename(substring);
                        LogUtils.d(substring);
                        LogUtils.d(path);
                        arrayList.add(data);
                    }
                }
                return arrayList;
            }
        }
        return null;
    }

    public static List<String> getUsbDevicePath() {
        List<String> path = new ArrayList();
        File mainFile = new File("/mnt/usb");
        if (mainFile.exists()) {
            File[] files = mainFile.listFiles();
            if (files.length > 0) {
                for (File file : files) {
                    if (file.exists()) {
                        File[] files1 = file.listFiles();
                        if (files1 != null && files1.length > 0) {
                            path.add(file.getAbsolutePath());
                        }
                    }
                }
            }
        }
        return path;
    }

    public static boolean isUsbPurgeIn(String path) {
        File mainFile = new File(path);
        if (mainFile.exists()) {
            File[] files = mainFile.listFiles();
            if (files != null && files.length > 0) {
                return true;
            }
        }
        return false;
    }

    public static List<CustomContentData> getUSBFiles(String filePath) {
        File mainFile = new File(filePath);
        if (mainFile.exists()) {
            File[] files = mainFile.listFiles();
            List<CustomContentData> arrayList = new ArrayList();
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    if (file1.isDirectory()) {
                        List<CustomContentData> files1 = getFiles(file1.getAbsolutePath(), false);
                        if (files1 != null && files1.size() > 0) {
                            arrayList.addAll(files1);
                        }
                    } else if (file1.getAbsolutePath().endsWith(FileUtil.JPG)
                            || file1.getAbsolutePath().endsWith(FileUtil.PNG)) {
                        String path = file1.toString();
                        String name = new File(path).getName();
                        String substring = name.substring(0, name.lastIndexOf("."));
                        data = new CustomContentData();
                        data.setFilepath(path);
                        data.setFilename(substring);
                        LogUtils.d(substring);
                        LogUtils.d(path);
                        arrayList.add(data);
                    }
                }
                return arrayList;
            }
        }
        return null;
    }

    private static String FindFileOnUSB(String filename) {
        String filepath = "";
        File usbroot = new File(Config.UDISK_PATH);
        if (usbroot == null || !usbroot.exists()) {
            return filepath;
        }
        File[] usbitems = usbroot.listFiles();
        for (int sdx = 0; sdx < usbitems.length; sdx++) {
            if (usbitems[sdx].isDirectory()) {
                File targetfile = new File(usbitems[sdx].getPath() + "/" + filename);
                if (targetfile != null && targetfile.exists()) {
                    return usbitems[sdx].getPath() + "/" + filename;
                }
            }
        }
        return filepath;
    }

    public static void deleteDir(String path) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    if (file1.isDirectory()) {
                        deleteDir(file1.getAbsolutePath());
                    } else {
                        file1.delete();
                    }
                }
            }
            file.delete();
        }
    }

    public static List<String> getDirFiles(String dir) {
        List<String> picList = new ArrayList();
        File file = new File(dir);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    String name = file1.getName();
                    picList.add(name.substring(0, name.lastIndexOf(".")));
                }
            }
        }
        return picList;
    }
}
