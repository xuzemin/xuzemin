package com.example.systemcheck.uitil;

import android.app.ActivityManager;
import android.app.Notification;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.v4.app.INotificationSideChannel;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.systemcheck.DeleteCacheListener;
import com.example.systemcheck.bean.PackInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class CleanUtils {
    private static boolean isCleanCacheSucceed = false;
    private  Context mContext;
    private static String SDCARD_ROOT = null;
    private static ActivityManager mActivityManager;
    private static PackageManager mPackageManager;
    private Vector<PackInfo> vector = new Vector();
    private static final int MSG_CLEAN_FINISH = 888;
   private DeleteCacheListener listener;
    private long size;
    private Handler handler =new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 999) {
                for (int i = 0; i < CleanUtils.this.vector.size(); i++) {
                    Log.d("hhh", "handleMessage: clean");
                    CleanUtils cleanUtils = CleanUtils.this;
                    cleanUtils.deleteApplicationCacheFiles(((PackInfo) cleanUtils.vector.get(i)).getPakName(), i);
                }
            } else if (msg.what == CleanUtils.MSG_CLEAN_FINISH) {
                Log.d("hhh", "handleMessage: msg == 888");
                long longValue = ((Long) msg.obj).longValue() / (1 << 10);
                if (longValue <= 0) {
                    Log.d("hhh", "handleMessage: longValue <= 0");
                    CleanUtils.this.listener.failed();
                } else {
                    Log.d("hhh", "handleMessage: longValue = "+longValue);
                    CleanUtils.this.listener.success(Long.valueOf(longValue));
                }
            }else if (msg.what == 222){
                CleanUtils.this.listener.failed();
            }
            return true;

        }
    });

    public CleanUtils(Context context) {
        this.mContext = context;
        SDCARD_ROOT = getSdcardRoot();
        mActivityManager = (ActivityManager) this.mContext.getSystemService("activity");
        mPackageManager = this.mContext.getPackageManager();
    }

    private String getSdcardRoot() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return this.mContext.getCacheDir().getPath();
    }
    private boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    private boolean deleteApplicationCacheFiles(String str, final int i) {
        isCleanCacheSucceed = false;
        try {
            Log.d("hhh", "deleteApplicationCacheFiles: start ");
            mPackageManager.getClass().getMethod("deleteApplicationCacheFiles", new Class[]{String.class, IPackageDataObserver.class}).invoke(mPackageManager, new Object[]{str, new IPackageDataObserver.Stub() {
                public void onRemoveCompleted(String str, boolean z) throws RemoteException {
                    CleanUtils.isCleanCacheSucceed = z;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("packageName=");
                    stringBuilder.append(str);
                    String str2 = "-----vector.get(position).getCacheSize()==";
                    stringBuilder.append(str2);
                    stringBuilder.append(((PackInfo) CleanUtils.this.vector.get(i)).getCacheSize());
                    String str3 = "----vector.get(position).getExternalCacheSize()==";
                    stringBuilder.append(str3);
                    stringBuilder.append(((PackInfo) CleanUtils.this.vector.get(i)).getExternalCacheSize());
                    String str4 = "zhanglulu_cache";
                    Log.e(str4, stringBuilder.toString());
                    Log.d("hhh", "onRemoveCompleted: ");
                    if (z) {
                        Log.d("hhh", "onRemoveCompleted: zzzz");
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("succeeded    packageName=");
                        stringBuilder2.append(str);
                        stringBuilder2.append(str2);
                        stringBuilder2.append(((PackInfo) CleanUtils.this.vector.get(i)).getCacheSize());
                        stringBuilder2.append(str3);
                        stringBuilder2.append(((PackInfo) CleanUtils.this.vector.get(i)).getExternalCacheSize());
                        Log.e(str4, stringBuilder2.toString());
                        CleanUtils cleanUtils = CleanUtils.this;
                        cleanUtils.size = cleanUtils.size + (((PackInfo) CleanUtils.this.vector.get(i)).getCacheSize() + ((PackInfo) CleanUtils.this.vector.get(i)).getExternalCacheSize());
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("size==");
                        stringBuilder3.append(CleanUtils.this.size);
                        Log.e(str4, stringBuilder3.toString());
                    }
                    if (i == CleanUtils.this.vector.size() - 1) {
                        Log.d("hhh", "onRemoveCompleted:  send msg == 888");
                        Message message = new Message();
                        message.what = CleanUtils.MSG_CLEAN_FINISH;
                        message.obj = Long.valueOf(CleanUtils.this.size);
                        CleanUtils.this.handler.sendMessage(message);
                    }
                }
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCleanCacheSucceed;
    }

    public void startCleanCache() {
        ArrayList arrayList = (ArrayList) getPackageInfos();
        if (arrayList == null) {
            return;
        }
        if (arrayList.size() > 0) {
            this.vector.clear();
            this.size = 0;

            Log.d("hhh", "startCleanCache: arrayList.size ="+arrayList.size());
            for (int i = 0; i < arrayList.size(); i++) {
                String str = ((PackageInfo) arrayList.get(i)).packageName;
                Log.d("hhh", "startCleanCache:packageName = "+str);
                if (!TextUtils.isEmpty(str)) {
                    if (!str.equals("com.hht.home")) {
                       cleanCache(str, i);
                    }
                }
            }
        }
    }
    public void setDeleteFileListener(DeleteCacheListener deleteCacheListener) {
        this.listener = deleteCacheListener;
    }
    private void cleanCache(String str, int i) {
        if (Build.VERSION.SDK_INT >= 26) {
            Log.d("hhh", "cleanCache---SDK_INT >= 26");
            //  getPackageSizeInfoSupport26(str, i);
          handler.sendEmptyMessage(222);
        } else {
            Log.d("hhh", "cleanCache---SDK_INT < 26");
            getPackageSizeInfo(str, i);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getPackageSizeInfoSupport26(String str, int i) {
        Log.d("hhh", "-----getPackageSizeInfoSupport26----- ");
        StorageStatsManager storageStatsManager;
        UserHandle userHandle;
        Iterator it;
        String str2 = str;
        String str3 = "CleanUtils";
        Log.i(str3, "getPackageSizeInfoSupport26");
        StorageStatsManager storageStatsManager2 = (StorageStatsManager) this.mContext.getSystemService("storagestats");
        List storageVolumes = ((StorageManager) this.mContext.getSystemService("storage")).getStorageVolumes();
        UserHandle myUserHandle = Process.myUserHandle();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("storageVolumes size : ");
        stringBuilder.append(storageVolumes.size());
        Log.i(str3, stringBuilder.toString());
        Log.d("hhh", "getPackageSizeInfoSupport26:  stringBuilder.toString()  ="+stringBuilder.toString());
        Iterator it2 = storageVolumes.iterator();
        long j = 0;
        long j2 = 0;
        long j3 = 0;

        while (it2.hasNext()) {
            UUID uuid;
            String uuid2 = ((StorageVolume) it2.next()).getUuid();
            Log.d("hhh", "getPackageSizeInfoSupport26 ---- uuid2 ="+uuid2);
            if (uuid2 == null) {
                try {
                    uuid = StorageManager.UUID_DEFAULT;
                } catch (Exception e) {
                    e.printStackTrace();
                    uuid = StorageManager.UUID_DEFAULT;
                }
            } else {
                Log.d("hhh", "getPackageSizeInfoSupport26: uuid 2!= null ");
                uuid = UUID.fromString(uuid2);
            }
            try {
                Log.d("hhh", "getPackageSizeInfoSupport26 ----- first   try  1111 ");
                StorageStats queryStatsForPackage = storageStatsManager2.queryStatsForPackage(uuid, str2, myUserHandle);
                Log.d("hhh", "getPackageSizeInfoSupport26 ----- first   try  2222 ");
                StringBuilder stringBuilder2 = new StringBuilder();
                Log.d("hhh", "getPackageSizeInfoSupport26 ----- first   try  3333 ");
                stringBuilder2.append("getAppBytes:");
                Log.d("hhh", "getPackageSizeInfoSupport26 ----- first   try  4444 ");
                storageStatsManager = storageStatsManager2;
                Log.d("hhh", "getPackageSizeInfoSupport26 ----- first   try  5555 ");

                userHandle = myUserHandle;
                try {
                    Log.d("hhh", "getPackageSizeInfoSupport26-----second  try  1111 ");
                    stringBuilder2.append(Formatter.formatShortFileSize(mContext, queryStatsForPackage.getAppBytes()));
                    Log.d("hhh", "getPackageSizeInfoSupport26-----second  try  2222 ");
                    stringBuilder2.append(" getCacheBytes:");
                    Log.d("hhh", "getPackageSizeInfoSupport26-----second  try  3333 ");

                    it = it2;
                } catch (Exception e3) {
                    it = it2;
                    e3.printStackTrace();
                    it2 = it;
                    storageStatsManager2 = storageStatsManager;
                    myUserHandle = userHandle;
                }
                try {
                    Log.d("hhh", "getPackageSizeInfoSupport26----- third   try  1111 ");
                    stringBuilder2.append(Formatter.formatShortFileSize(mContext, queryStatsForPackage.getCacheBytes()));
                    Log.d("hhh", "getPackageSizeInfoSupport26----- third   try  2222 ");
                    stringBuilder2.append(" getDataBytes:");
                    Log.d("hhh", "getPackageSizeInfoSupport26----- third   try  3333 ");

                    stringBuilder2.append(Formatter.formatShortFileSize(mContext, queryStatsForPackage.getDataBytes()));
                    Log.d("hhh", "getPackageSizeInfoSupport26----- third   try  4444 ");

                    Log.d(str3, stringBuilder2.toString());
                    Log.d("hhh", "getPackageSizeInfoSupport26----- third   try  5555 ");

                    j += queryStatsForPackage.getCacheBytes();
                    Log.d("hhh", "getPackageSizeInfoSupport26----- third   try  6666 ");

                    j2 += queryStatsForPackage.getAppBytes();
                    Log.d("hhh", "getPackageSizeInfoSupport26----- third   try  7777 ");
                    j3 += queryStatsForPackage.getDataBytes();
                    Log.d("hhh", "getPackageSizeInfoSupport26----- third   try  8888 ");

                } catch (Exception e4) {

                    e4.printStackTrace();
                    it2 = it;
                    storageStatsManager2 = storageStatsManager;
                    myUserHandle = userHandle;
                }
            } catch (Exception e5) {

                storageStatsManager = storageStatsManager2;
                userHandle = myUserHandle;
                it = it2;
                e5.printStackTrace();
                it2 = it;
                storageStatsManager2 = storageStatsManager;
                myUserHandle = userHandle;
            }
            it2 = it;
            storageStatsManager2 = storageStatsManager;
            myUserHandle = userHandle;
        }
        PackInfo packInfo = new PackInfo();
        packInfo.setCacheSize(j);
        packInfo.setCodeSize(j2);
        packInfo.setDataSize(j3);
        packInfo.setPakName(str2);
        packInfo.setExternalCacheSize(0);
        packInfo.setExternalCodeSize(0);
        packInfo.setExternalDataSize(0);
        vector.add(packInfo);
        if (i == getPackageInfos().size() - 1) {
           handler.sendEmptyMessage(999);
        }
    }
    private void getPackageSizeInfo(final String str, final int i) {
        try {
            PackageManager.class.getMethod("getPackageSizeInfo", new Class[]{String.class, IPackageStatsObserver.class}).invoke(mPackageManager, new Object[]{str, new IPackageStatsObserver.Stub() {

                public void onGetStatsCompleted(PackageStats packageStats, boolean z) throws RemoteException {
                    long j = packageStats.cacheSize;
                    long j2 = packageStats.codeSize;
                    long j3 = packageStats.dataSize;
                    long j4 = packageStats.externalCacheSize;
                    long j5 = packageStats.externalCodeSize;
                    long j6 = packageStats.externalDataSize;
                    PackInfo packInfo = new PackInfo();
                    packInfo.setCacheSize(j);
                    packInfo.setCodeSize(j2);
                    packInfo.setDataSize(j3);
                    packInfo.setPakName(str);
                    packInfo.setExternalCacheSize(j4);
                    packInfo.setExternalCodeSize(j5);
                    packInfo.setExternalDataSize(j6);
                    CleanUtils.this.vector.add(packInfo);
                    if (i == CleanUtils.this.getPackageInfos().size() - 1) {
                        CleanUtils.this.handler.sendEmptyMessage(999);
                    }
                }
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<PackageInfo> getPackageInfos() {
        PackageManager packageManager = mPackageManager;
        return packageManager != null ? packageManager.getInstalledPackages(8192) : null;
    }
    public long getAvailableExternalMemorySize(Context context) {
        if (externalMemoryAvailable()) {
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
            return ((((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())) /(1 << 10)) /(1 << 10);
        }
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return ((((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())) /(1 << 10)) /(1 << 10);
    }
    public Long getEmmcSize() {
        String str = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/sys/block/mmcblk0/size"));
            String readLine = bufferedReader.readLine();
            bufferedReader.close();
            str = readLine;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long valueOf = Long.valueOf(((Long.parseLong(str) * 512) /( 1 << 10)) / (1 << 10));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getEmmcSize-->");
        stringBuilder.append(valueOf);
        Log.d("getEmmcSize", stringBuilder.toString());
        return valueOf;
    }
    public int getEmmcSize_gb() {
        String str = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/sys/block/mmcblk0/size"));
            String readLine = bufferedReader.readLine();
            bufferedReader.close();
            str = readLine;
        } catch (Exception e) {
            e.printStackTrace();
        }
        long valueOf = Long.valueOf(((Long.parseLong(str) * 512) /( 1 << 10)) / (1 << 10));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getEmmcSize-->");
        stringBuilder.append(valueOf);
        Log.d("getEmmcSize", stringBuilder.toString());
        int dd = (int) (valueOf/1024);
        if (dd>128&&dd<=256){
            dd=256;
        }if (dd>64&&dd<=128){
            dd=128;
        }else if (dd>32&&dd<=64){
            dd=64;
        }else if (dd>16&&dd<=32){
            dd=32;
        }else if (dd >8&&dd<=16){
            dd = 16;
        }else if (dd>4.0&&dd<=8.0){
            dd = 8;
        } else {
            dd = 4;
        }
        return dd;
    }
    public String getAvailableExternalMemorySize_gb(Context context) {
        if (externalMemoryAvailable()) {
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double ff=  ((((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())) /(1 << 10)) /(1 << 10);
            double dd =ff/1024;

            String format1 = String.format("%,.2f", dd);
            return format1;
        }
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        double ff=  ((((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())) /(1 << 10)) /(1 << 10);
        double dd =ff/1024;

        String format1 = String.format("%,.2f", dd);

        return   format1;
    }
    public void clearMemoryCache(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        List runningAppProcesses = activityManager.getRunningAppProcesses();
        if (runningAppProcesses != null) {
            for (int i = 0; i < runningAppProcesses.size(); i++) {
                ActivityManager.RunningAppProcessInfo runningAppProcessInfo = (ActivityManager.RunningAppProcessInfo) runningAppProcesses.get(i);
                if (runningAppProcessInfo.importance > TvLanguage.GUARANI) {
                    String[] strArr = runningAppProcessInfo.pkgList;
                    int i2 = 0;
                    while (i2 < strArr.length) {
                        if (!(strArr[i2].equals("com.hht.home") || strArr[i2].equals("com.android.burialpoint"))) {
                            if (!strArr[i2].equals("com.hht.toolbar")) {
                                activityManager.killBackgroundProcesses(strArr[i2]);
                            }
                        }
                        i2++;
                    }
                }
            }
        }
    }


    public long getTotalMemorySize(Context context) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/meminfo"), 2048);
            String readLine = bufferedReader.readLine();
            readLine = readLine.substring(readLine.indexOf("MemTotal:"));
            bufferedReader.close();
            return (long) (((Integer.parseInt(readLine.replaceAll("\\D+", "")) * 1024) / 1024) / 1024);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) context.getSystemService("activity")).getMemoryInfo(memoryInfo);
        return (memoryInfo.availMem /  ( 1 << 10)) / (1 << 10);
    }

    public String getAvailableMemory_GB(Context context) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) context.getSystemService("activity")).getMemoryInfo(memoryInfo);
        float ff   = (memoryInfo.availMem /  ( 1 << 10)) / (1 << 10);
       float dd= ff/1024 ;
        Log.d("memory", "getAvailableMemory_GB:  dd =" +dd   +   "     ff="+((memoryInfo.availMem /  ( 1 << 10)) / (1 << 10)));
      //  String format = df.format(dd);
        String format1 = String.format("%,.2f", dd);
        return format1 ;
    }



   //获得系统内存
    public static String getTotalM(Context ctvContext) {
        ActivityManager am = (ActivityManager) ctvContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
       // Log.i(TAG, "mi.availMem;" + mi.availMem + "mi.totalMem" + mi.totalMem);
        long totalMem = mi.totalMem;
        String totalMeString;
        if (totalMem > 2254857830L && totalMem < 3221225472L) {
            totalMeString = "3";
        }else if (totalMem<2254857830L && totalMem>1254827820L){
            totalMeString = "2";
        }
        else {
          //  totalMeString = Formatter.formatFileSize(ctvContext, totalMem);
            totalMeString="1";
        }
        Log.i("memory", "mi.availMem;" + mi.availMem + "totalMeString" + totalMeString);

        return totalMeString;
    }






    //清除系统存储缓存




}
