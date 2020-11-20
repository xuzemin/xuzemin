package com.mphotool.whiteboard.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mphotool.whiteboard.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Dong.Daoping on 2018/4/9 0009
 * 说明：
 */
public class BaseUtils {
    private static Class<?> mClassType = null;
    private static Method mGetIntMethod = null;
    private static Method mGetMethod = null;
    private static Method mSetMethod = null;
    public static final String KEY_WLAN_PATH = "/sys/class/net/wlan0/address";
    public static final String KEY_ETH_PATH  = "/sys/class/net/eth0/address";

    public static void dbg(String msg)
    {
        if (com.mphotool.whiteboard.BuildConfig.DEBUG)
        {
            ToofifiLog.d(Constants.TAG, msg);
        }
    }

    public static void dbg(String tag, String msg)
    {
        ToofifiLog.d(tag,msg);
    }

    public static String getSystemProperty(String key, String def)
    {
        init();
        try
        {
            return (String) mGetMethod.invoke(mClassType, new Object[]{key});
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return def;
        }
    }

    public static String setSystemProperty(String key, String value)
    {
        init();
        try
        {
            mSetMethod.invoke(mClassType, new Object[]{key, value});
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    private static void init()
    {
        try
        {
            if (mClassType == null)
            {
                mClassType = Class.forName("android.os.SystemProperties");
                mGetMethod = mClassType.getDeclaredMethod("get", new Class[]{String.class});
                mSetMethod = mClassType.getDeclaredMethod("set", new Class[]{String.class, String.class});
                mGetIntMethod = mClassType.getDeclaredMethod("getInt", new Class[]{String.class, Integer.TYPE});
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**可控制是否显示触摸点浮标*/
    public static void showTouches(boolean showTouches){
        dbg(" showTouches  --  " + showTouches);
//        Settings.System.putInt(WhiteBoardApplication.getInstance().getContentResolver(), "show_touches", showTouches ? 0 : 1);
    }

    public static void hideNavigationBar(Activity context)
    {
        View decorView = context.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        int visible = decorView.getSystemUiVisibility();
        decorView.setSystemUiVisibility(uiOptions);
    }

    public static void hideNavigationBar(Dialog context)
    {
        View decorView = context.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        int visible = decorView.getSystemUiVisibility();
        decorView.setSystemUiVisibility(uiOptions);
    }

    public static int dpiTopixel(Context context, int dpi)
    {
//        if (mDensity < 0.0f) {
        float mDensity = context.getResources().getDisplayMetrics().density;
//        }
        return (int) (((float) dpi) * mDensity);
    }

    public static int getComparisonColor(int color)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] - 0.07f;
        if (hsv[2] < 0.0f)
        {
            hsv[2] = 0.0f;
        }
        hsv[1] = hsv[1] + 0.05f;
        if (hsv[1] > 1.0f)
        {
            hsv[1] = 1.0f;
        }
        return Color.HSVToColor(hsv);
    }

    public static void runInUIThread(Action1 action)
    {
        Observable.just(Integer.valueOf(1)).observeOn(AndroidSchedulers.mainThread()).subscribe(action);
    }

    public static boolean isNetworkAvailed(ConnectivityManager cm)
    {
        if (cm == null)
        {
            return false;
        }
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null)
        {
            return info.isConnected();
        }
        return false;
    }

    public static String getStackTrace(Throwable throwable)
    {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

    public static boolean writeOutputStreamString(OutputStream os, String str) throws IOException
    {
        byte[] bytes = str.getBytes();
        os.write(intToByteArray(bytes.length));
        os.write(bytes);
        return true;
    }

    public static String readInputStreamString(InputStream is) throws IOException
    {
        int length = readInputStreamInt(is, new byte[4]);
        byte[] str = new byte[length];
        if (is.read(str) == length)
        {
            return new String(str);
        }
        ToofifiLog.e(Constants.TAG, "read String failed");
        throw new IOException();
    }

    public static long readInputStreamLong(InputStream is) throws IOException
    {
        byte[] bytes = new byte[8];
        if (is.read(bytes) == bytes.length)
        {
            return byteArrayToLong(bytes).longValue();
        }
        throw new IOException();
    }

    public static float readInputStreamFloat(InputStream is, byte[] bytes) throws IOException
    {
        if (is.read(bytes) == bytes.length)
        {
            return byteToFloat(bytes);
        }
        throw new IOException();
    }

    public static int readInputStreamInt(InputStream is, byte[] bytes) throws IOException
    {
        if (is.read(bytes) == bytes.length)
        {
            return byteArrayToInt(bytes);
        }
        throw new IOException();
    }

    public static float byteToFloat(byte[] b)
    {
        return ByteBuffer.wrap(b).getFloat();
    }

    public static byte[] floatToByte(float f)
    {
        return ByteBuffer.allocate(4).putFloat(f).array();
    }

    public static int byteArrayToInt(byte[] b)
    {
        return ByteBuffer.wrap(b).getInt();
    }

    public static byte[] intToByteArray(int a)
    {
        return ByteBuffer.allocate(4).putInt(a).array();
    }

    public static byte[] longToByteArray(long a)
    {
        return ByteBuffer.allocate(8).putLong(a).array();
    }

    public static Long byteArrayToLong(byte[] b)
    {
        return Long.valueOf(ByteBuffer.wrap(b).getLong());
    }

    public static String getEventInfo(MotionEvent event)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(getActionName(event.getActionMasked()));
        sb.append(" / count:" + event.getPointerCount());
        sb.append(" / actionId: " + event.getActionIndex());
        sb.append(" / size : " + event.getSize());
        return sb.toString();
    }

    public static String getActionName(int action)
    {
        String actionName = "";
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                actionName = "ACTION_DOWN";
                break;
            case MotionEvent.ACTION_UP:
                actionName = "ACTION_UP";
                break;
            case MotionEvent.ACTION_MOVE:
                actionName = "ACTION_MOVE";
                break;
            case MotionEvent.ACTION_CANCEL:
                actionName = "ACTION_CANCEL";
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                actionName = "ACTION_POINTER_DOWN";
                break;
            case MotionEvent.ACTION_POINTER_UP:
                actionName = "ACTION_POINTER_UP";
                break;
            case MotionEvent.ACTION_SCROLL:
                actionName = "ACTION_SCROLL";
                break;
            case MotionEvent.ACTION_HOVER_MOVE:
                actionName = "ACTION_HOVER_MOVE";
                break;
            case MotionEvent.ACTION_HOVER_ENTER:
                actionName = "ACTION_HOVER_ENTER";
                break;
            case MotionEvent.ACTION_OUTSIDE:
                actionName = "ACTION_OUTSIDE";
                break;
            default:
                break;
        }
        return actionName;
    }


    /**
     * 获取调用栈
     *
     * @param level 为调用getStackTrace()的层数级别，0表示当前层，每+1，则向上追溯1层级
     * @return level对应的类名和方法名
     */
    public static String whoCalledMe(int level)
    {
        StackTraceElement ste = new Throwable().getStackTrace()[level];
        // if (level < 2 || level > 3)return "";
        // 获得包名、类名和方法名
        String clazzName = ste.getClassName();
        String mothodName = ste.getMethodName();
        int index = clazzName.lastIndexOf(".");
        clazzName = clazzName.substring(index < 0 ? 0 : index + 1);
        String str = "[" + clazzName + ":" + mothodName + "]";
        return str;
    }

    /**
     * 用于调起批注的侧边栏
     * @param cxt
     */
    public static void startFloatBarActivity(Context cxt)
    {
        try
        {
            cxt.sendBroadcast(new Intent("tff.floatbar.show").setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getSDPATH()
    {
        String SDPATH;
        if (isSdcardMounted())
        {
            SDPATH = Environment.getExternalStorageDirectory().getPath();
        }
        else
        {
            SDPATH = Environment.getDataDirectory().getPath();
        }
        return SDPATH;
    }

    public static String getDateFloder()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    /**
     * 获取保存文件时的默认名称
     *
     * @return
     */
    public static String getDefaultFileName(Context cxt)
    {
        if (com.mphotool.whiteboard.BuildConfig.create_file_by_date)
        {
            return cxt.getResources().getString(R.string.file_name_prefix) + getDateFloder();
        }
        else
        {
            int fileCount = (int) SharedPreferencesUtils.getParam("file_count", 1);
            String countStr = fileCount + "";
            if (fileCount < 10)
            {
                countStr = "0" + fileCount;
            }
            return cxt.getResources().getString(R.string.file_name_prefix) + countStr;
        }
    }

    public static void deleteFloder(String floder)
    {
        try
        {
            File file = new File(floder);
            if (file.exists() && file.isDirectory())
            {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++)
                {
                    File f = files[i];
                    f.delete();
                }
                file.delete();//如要保留文件夹，只删除文件，请注释这行
            }
            else if (file.exists())
            {
                file.delete();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 是否可在SD卡可读写文件
     */
    public static boolean isSdcardMounted()
    {
        String status = Environment.getExternalStorageState();
        boolean isMounted = false;
        if (status.equals(Environment.MEDIA_MOUNTED))
        {
            isMounted = true;
        }
        else
        {
            isMounted = false;
        }
        Log.d("BaseUtils", "isSdcardMounted = " + isMounted + " , status = " + status);
        return isMounted;
    }

    public static int getFileCount(File floder){
        int size = 0;
        if(!floder.exists()){
            return 0;
        }
        if(floder.isDirectory()){
            File flist[] = floder.listFiles();
            if(flist == null){
                return 1;
            }
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileCount(flist[i]);
                } else {
                    size = size + 1;
                }
            }
            return size;
        }else{
            return 1;
        }

    }

    public static File createSDFile(String filePath)
    {
        if (TextUtils.isEmpty(filePath)) return null;
        File file = new File(filePath);
        try
        {
            file.getParentFile().mkdirs();// 创建目录，如果目录已经存在,则会跳过
            file.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public static File createSDFile(String dirPath, String fileName)
    {
        if (TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(fileName)) return null;

        File file = new File(dirPath, fileName);
        try
        {
            file.getParentFile().mkdirs();// 创建目录，如果目录已经存在,则跳过
            file.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.d("BaseUtils", "createSDFile  failed");
            return null;
        }
        Log.d("BaseUtils", "createSDFile  success");
        return file;
    }

    /**
     * 获取MAC地址
     *
     * @param which 0- WLAN  1- ETH0
     */
    public static String getMacAddress(int which)
    {
        String macAddr = "";
        FileReader fr = null;
        BufferedReader br = null;
        try {
            File f = new File(which == 0 ? KEY_WLAN_PATH : KEY_ETH_PATH);
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            macAddr = br.readLine();
            if (null != macAddr) {
                macAddr = macAddr.trim();
            }
            Log.e("BaseUtils", "getMacAddress() " + (which == 0 ? "wifi" : "ethernet") + " mac address=" + macAddr);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            closeIO(br, fr, null, null);
        }
        return macAddr;
    }

    /* 关闭IO流 */
    private static void closeIO(BufferedReader br, FileReader fr, BufferedWriter bw, FileWriter fw) {
        try {
            if (br != null) {
                br.close();
            }
            if (fr != null)
            {
                fr.close();
            }
            if (bw != null)
            {
                bw.close();
            }
            if (fw != null)
            {
                fw.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
