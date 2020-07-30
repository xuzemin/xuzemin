package com.yinghe.whiteboardlib.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.WindowManager;

import com.apkfuns.logutils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by TangentLu on 2015/8/19.
 */
public class BitmapUtils {
    private final static String TAG = BitmapUtils.class.getSimpleName();

    public static boolean isLandScreen(Context context) {
        int ori =context.getResources().getConfiguration().orientation;//获取屏幕方向
        return ori == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static Bitmap decodeSampleBitMapFromFile(Context context, String filePath, float sampleScale) {
        //先得到bitmap的高宽
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //再用屏幕一半高宽、缩小后的高宽对比，取小值进行缩放
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int reqWidth = Objects.requireNonNull(wm).getDefaultDisplay().getWidth();
        int reqHeight = wm.getDefaultDisplay().getWidth();
        int scaleWidth = (int) (options.outWidth * sampleScale);
        int scaleHeight = (int) (options.outHeight * sampleScale);
        reqWidth = Math.min(reqWidth, scaleWidth);
        reqHeight = Math.min(reqHeight, scaleHeight);
        options = sampleBitmapOptions(context, options, reqWidth, reqHeight);
        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        Log.e("xxx", bm.getByteCount() + "");
        return bm;
    }
    public static Bitmap decodeSampleBitMapFromResource(Context context, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        options = sampleBitmapOptions(context, options, reqWidth, reqHeight);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resId, options);
        Log.e("xxx", bm.getByteCount() + "");
        return bm;
    }


    public static Bitmap createBitmapThumbnail(Bitmap bitMap, boolean needRecycle, int newHeight, int newWidth) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 计算缩放比例
        float scale = Math.min((float) newWidth / width, (float) (newHeight) / height);
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
        if (needRecycle)
            bitMap.recycle();
        return newBitMap;
    }

    public static BitmapFactory.Options sampleBitmapOptions(
            Context context, BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int targetDensity = context.getResources().getDisplayMetrics().densityDpi;
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        double xSScale = ((double) options.outWidth) / ((double) reqWidth);
        double ySScale = ((double) options.outHeight) / ((double) reqHeight);

        double startScale = xSScale > ySScale ? xSScale : ySScale;

        options.inScaled = true;
        options.inDensity = (int) (targetDensity * startScale);
        options.inTargetDensity = targetDensity;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return options;
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap getBitmapFromAssets(Context context,String path){
        InputStream open = null;
        Bitmap bitmap = null;
        try {
            String temp =  path;
            open = context.getAssets().open(temp);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options = sampleBitmapOptions(context, options, 10, 10);
            bitmap = BitmapFactory.decodeStream(open, null, options);
            return bitmap;
        } catch (Exception e) {e.printStackTrace();
            return null;
        }
    }

    /**
     * 绘制圆形图片方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toRectShape(Bitmap bitmap, Rect rect) {
        // 1.初始化画布
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // 2.绘制边框
        final int color = 0xff424242;
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();
        canvas.drawRect(rect, paint);

        // 3.绘制源图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 释放图片
     * @param bitmap
     */
    public static void releaseBitmap(Bitmap bitmap){
        if (bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
        }
    }

    public static Bitmap srceenshot(Context context){
        Bitmap mScreenBitmap = null;
        try {
            Point point = ScreenUtils.getScreenSize(context);
            Class testClass = Class.forName("android.view.SurfaceControl");
            Method saddMethod1 = testClass.getMethod("screenshot", new Class[]{int.class ,int.class});
            mScreenBitmap = (Bitmap) saddMethod1.invoke(null, new Object[]{point.x, point.y});
            return mScreenBitmap;
        } catch (Exception e){
            e.printStackTrace();
            return mScreenBitmap;
        }
    }

    /**
     * show 保存图片到本地文件，耗时操作
     * @param filePath 文件保存路径
     * @param imgName  文件名
     * @param compress 压缩百分比1-100
     * @return 返回保存的图片文件
     */
    public static File saveInOI(String filePath, Bitmap bitmap, String imgName, int compress) {
        if (!imgName.toLowerCase().contains(DrawConsts.IMAGE_SAVE_SUFFIX)) {
            imgName += DrawConsts.IMAGE_SAVE_SUFFIX;
        }
        Log.e(TAG, "saveInOI: " + System.currentTimeMillis());

        if (bitmap == null ){
            Log.e(TAG,"bitmap为null，请检查！");
            return null;
        }

        Log.e(TAG, "start saveInOI: " + System.currentTimeMillis());
        FileOutputStream out = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File f = new File(filePath, imgName);
            if (!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
            }
            out = new FileOutputStream(f);

            if (compress >= 1 && compress <= 100)
                bitmap.compress(Bitmap.CompressFormat.JPEG, compress, out);
            else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }
            Log.e(TAG, "end saveInOI: " + System.currentTimeMillis());
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            bitmap.recycle();
            bitmap = null;
            try {
                out.close();
            } catch (Exception e1){
                e1.printStackTrace();
            }
        }
    }

    /**
     * show 保存图片到本地文件，耗时操作
     * @param filePath 文件保存路径
     * @param compress 压缩百分比1-100
     * @return 返回保存的图片文件
     */
    public static boolean saveInOI(String filePath, Bitmap bitmap, int compress) {
        Log.e(TAG, "saveInOI: " + System.currentTimeMillis());

        if (bitmap == null ){
            Log.e(TAG,"bitmap为null，请检查！");
            return false;
        }

        Log.e(TAG, "saveInOI: " + System.currentTimeMillis());
        FileOutputStream out = null;
        try {
            File f = new File(filePath);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            if (!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
            }
            out = new FileOutputStream(f);
            Log.e(TAG, "saveInOI: " + System.currentTimeMillis());

            if (compress >= 1 && compress <= 100)
                bitmap.compress(Bitmap.CompressFormat.JPEG, compress, out);
            else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }
            Log.e(TAG, "saveInOI: " + System.currentTimeMillis());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            bitmap.recycle();
            bitmap = null;
            try {
                out.close();
            } catch (Exception e1){
                e1.printStackTrace();
            }
        }
    }
}
