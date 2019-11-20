
package com.ctv.welcome.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;

public class BitmapUtil {
    private BitmapUtil() {
    }

    public static Bitmap readBitMap(Context context, int resId) {
        Options opt = new Options();
        opt.inPreferredConfig = Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        try {
            return Picasso.with(context).load(resId).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getDiskBitmap(Context context, String path) {
        try {
            if (new File(path).exists()) {
                Options opt = new Options();
                opt.inPreferredConfig = Config.ARGB_8888;
                opt.inPurgeable = true;
                opt.inInputShareable = true;
                Bitmap bitmap = BitmapFactory.decodeFile(path, opt);
                System.gc();
                return bitmap;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static void saveQrcodeToDisk(String imgName, WeakReference<Bitmap> bitmap,
            String baseDir, String path) {
        try {
            File folder = createFolders(baseDir, path);
            if (!folder.exists()) {
                folder = createFolders(baseDir, path);
            }
            File file = new File(folder, imgName);
            if (isFileExists(file.getPath())) {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            ((Bitmap) bitmap.get()).compress(CompressFormat.JPEG, 70, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
    }

    public static void saveImgToDisk(String basePath, String imgName, WeakReference<Bitmap> bitmap) {
        try {
            File baseDir = createFolders(basePath, com.ctv.welcome.constant.Config.SDBACKUPPATH);
            if (!baseDir.exists()) {
                baseDir = createFolders(basePath, com.ctv.welcome.constant.Config.SDBACKUPPATH);
            }
            File file = new File(baseDir, imgName);
            if (!isFileExists(file.getPath())) {
                FileOutputStream out = new FileOutputStream(file);
                ((Bitmap) bitmap.get()).compress(CompressFormat.JPEG, 70, out);
                out.flush();
                out.close();
                System.gc();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
    }

    private static File createFolders(String baseDir, String filename) {
        File aviaryFolder = new File(baseDir, filename);
        if (aviaryFolder.exists()) {
            return aviaryFolder;
        }
        if (aviaryFolder.isFile()) {
            aviaryFolder.delete();
        }
        return !aviaryFolder.mkdirs() ? Environment.getExternalStorageDirectory() : aviaryFolder;
    }

    private static boolean isFileExists(String filePath) {
        try {
            return new File(filePath).exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getSdCardPath() {
        if (isSdCardExist()) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }
        if (radius < 1) {
            return null;
        }
        int i;
        int y;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[(w * h)];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = (radius + radius) + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int[] vmin = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[(divsum * 256)];
        for (i = 0; i < divsum * 256; i++) {
            dv[i] = i / divsum;
        }
        int yi = 0;
        int yw = 0;
        int[][] stack = (int[][]) Array.newInstance(Integer.TYPE, new int[] {
                div, 3
        });
        int r1 = radius + 1;
        for (y = 0; y < h; y++) {
            int x;
            int bsum = 0;
            int gsum = 0;
            int rsum = 0;
            int boutsum = 0;
            int goutsum = 0;
            int routsum = 0;
            int binsum = 0;
            int ginsum = 0;
            int rinsum = 0;
            for (i = -radius; i <= radius; i++) {
                int p = pix[Math.min(wm, Math.max(i, 0)) + yi];
                int[] sir = stack[i + radius];
                sir[0] = (16711680 & p) >> 16;
                sir[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & p) >> 8;
                sir[2] = p & 255;
                int rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            int stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                int []sir = stack[((stackpointer - radius) + div) % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min((x + radius) + 1, wm);
                }
                int p = pix[vmin[x] + yw];
                sir[0] = (16711680 & p) >> 16;
                sir[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & p) >> 8;
                sir[2] = p & 255;
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (int x = 0; x < w; x++) {
            int  bsum = 0;
            int gsum = 0;
            int rsum = 0;
            int boutsum = 0;
            int goutsum = 0;
            int routsum = 0;
            int binsum = 0;
            int ginsum = 0;
            int rinsum = 0;
            int yp = (-radius) * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                int []sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                int rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            int stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (((ViewCompat.MEASURED_STATE_MASK & pix[yi]) | (dv[rsum] << 16)) | (dv[gsum] << 8))
                        | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
               int []sir = stack[((stackpointer - radius) + div) % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                int p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }
}
