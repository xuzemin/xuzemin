package com.mphotool.whiteboard.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapUtils {
    public static Bitmap scaleBitmap(Bitmap bm, float sw, float sh)
    {
        int rawWidth = bm.getWidth();
        int rawHeight = bm.getHeight();
        Matrix matric = new Matrix();
        matric.postScale(sw, sh);
        return Bitmap.createBitmap(bm, 0, 0, rawWidth, rawHeight, matric, true);
    }

    public static Bitmap scaleBitmap(Bitmap bm, int width, int height)
    {
        int rawWidth = bm.getWidth();
        int rawHeight = bm.getHeight();
        float wscale = ((float) width) / ((float) rawWidth);
        float hscale = ((float) height) / ((float) rawHeight);
        Matrix matric = new Matrix();
        matric.postScale(wscale, hscale);
        return Bitmap.createBitmap(bm, 0, 0, rawWidth, rawHeight, matric, true);
    }

    public static Bitmap scaleWithWidth(Bitmap bm, int width)
    {
        int rawWidth = bm.getWidth();
        int rawHeight = bm.getHeight();
        float scale = ((float) width) / ((float) rawWidth);
        Matrix matric = new Matrix();
        matric.postScale(scale, scale);
        return Bitmap.createBitmap(bm, 0, 0, rawWidth, rawHeight, matric, true);
    }

    public static boolean saveBitmapToFile(Bitmap bm, String path) throws IOException
    {
        if (bm == null)
        {
            return false;
        }
        File file = new File(path);
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs())
        {
            return false;
        }
        OutputStream os = new FileOutputStream(file);
        bm.compress(CompressFormat.JPEG, 100, os);
        os.close();
        return true;
    }

    public static Bitmap getBitmapFromFile(String path)
    {
        if (path != null && new File(path).exists())
        {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
            opts.inJustDecodeBounds = true;
            opts.inSampleSize = 2;
            return BitmapFactory.decodeFile(path, opts);

        }
        return null;
    }

    public static Bitmap getBitmapFromFile(String path, int width, int height)
    {
        BaseUtils.dbg(Constants.TAG, " getBitmapFromFile -- tosize = " + width + " / " + height);
        if (TextUtils.isEmpty(path))
        {
            return null;
        }
        File dst = new File(path);
        if (null != dst && dst.exists())
        {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0)
            {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength,
                        width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
                BaseUtils.dbg(Constants.TAG," getBitmapFromFile -- tosize = " + width + " / " + height);
            }
            try
            {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            }
            catch (OutOfMemoryError e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels)
    {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8)
        {
            roundedSize = 1;
            while (roundedSize < initialSize)
            {
                roundedSize <<= 1;
            }
        }
        else
        {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels)
    {
        double w = options.outWidth;
        double h = options.outHeight;

        Log.d(Constants.TAG, " old  size -- " + w + " / " + h);

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound)
        {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1))
        {
            return 1;
        }
        else if (minSideLength == -1)
        {
            return lowerBound;
        }
        else
        {
            return upperBound;
        }
    }


}
