package com.ctv.annotation.utils;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.View;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.mstar.android.tv.TvCommonManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Hashtable;

public class BaseUtils {


    public static void dbg(String tag, String msg)
    {
      // Log.d(tag, msg);
    }
    public static int getComparisonColor(int color)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] - 0.02f;
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
    public static float readInputStreamFloat(InputStream is, byte[] bytes) throws IOException
    {
        if (is.read(bytes) == bytes.length)
        {
            return byteToFloat(bytes);
        }
        throw new IOException();
    }
    public static Bitmap createQRCode(String url, int widthAndHeight)
    {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = null;
        try
        {
            matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        }
        catch (com.google.zxing.WriterException e)
        {
            e.printStackTrace();
        }

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        //画黑点
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                if (matrix.get(x, y))
                {
                    pixels[y * width + x] = Color.BLACK; //0xff000000
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
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
    /**
     * 改变USB触控
     *
     * @param isOpen
     */
    public static void changeUSBTouch(Context context, boolean isOpen) {
        // TODO: 2019-10-28 8386
        try {
            Class<?> bookClass = Class.forName("android.os.SystemProperties");//完整类名
            Object book = bookClass.newInstance();//获得实例
            Method getAuthor = bookClass.getDeclaredMethod("set", String.class, String.class);//获得私有方法
            getAuthor.setAccessible(true);//调用方法前，设置访问标志


            Log.d("qkmin", "setUSB isOpen" );
            if (isOpen) {
                getAuthor.invoke(book, "ctv.sendKeyCode", "on");//使用方法
                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_ON");
                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_ON");
                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_ON");
            } else {
                getAuthor.invoke(book, "ctv.sendKeyCode", "off");//使用方法
                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_OFF");
                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_OFF");
                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_OFF");
            }
//            TvCommonManager.getInstance().setUsbTouch(context, isOpen);
        } catch (Exception e) {
            Log.e("qkmin", "setUSB error:" + e);
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
    public static void setEasyTouchEnable(boolean enable , Context context){
        Log.d("hong", "dialog---exit--- setEasyTouchEnable---true ");
//        Settings.System.putInt(context.getContentResolver(), "EASY_TOUCH_OPEN",
//                enable? 1 : 0);
        if(enable){
            Intent fsIntent = new Intent();
            fsIntent.setComponent(new ComponentName("com.ctv.easytouch",
                    "com.ctv.easytouch.service.FloatWindowService"));
            fsIntent.setAction("com.ctv.easytouch.START_ACTION");
            fsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(fsIntent);
        }else{
            Intent fsIntent = new Intent();
            fsIntent.setComponent(new ComponentName("com.ctv.easytouch",
                    "com.ctv.easytouch.service.FloatWindowService"));
            fsIntent.setAction("com.ctv.easytouch.CLOSE_ACTION");
            fsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(fsIntent);
        }
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
    public static long readInputStreamLong(InputStream is) throws IOException
    {
        byte[] bytes = new byte[8];
        if (is.read(bytes) == bytes.length)
        {
            return byteArrayToLong(bytes).longValue();
        }
        throw new IOException();
    }
}

