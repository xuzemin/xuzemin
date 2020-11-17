
package com.ctv.welcome.view.customview;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;

import androidx.core.view.ViewCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

public class QRCode {
    private static int IMAGE_HALFWIDTH = 50;

    public static Bitmap createQRCode(String text) {
        return createQRCode(text, 500);
    }

    public static Bitmap createQRCode(String text, int size) {
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size,
                    size, hints);
            int[] pixels = new int[(size * size)];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[(y * size) + x] = ViewCompat.MEASURED_STATE_MASK;
                    } else {
                        pixels[(y * size) + x] = -1;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap createQRCodeWithLogo(String text, Bitmap mBitmap) {
        return createQRCodeWithLogo(text, 500, mBitmap);
    }

    public static Bitmap createQRCodeWithLogo(String text, int size, Bitmap mBitmap) {
        try {
            IMAGE_HALFWIDTH = size / 10;
            Hashtable<EncodeHintType, Object> hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size,
                    size, hints);
            int width = bitMatrix.getWidth();
            int halfW = width / 2;
            int halfH = bitMatrix.getHeight() / 2;
            Matrix m = new Matrix();
            m.setScale((2.0f * ((float) IMAGE_HALFWIDTH)) / ((float) mBitmap.getWidth()),
                    (2.0f * ((float) IMAGE_HALFWIDTH)) / ((float) mBitmap.getHeight()));
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(),
                    m, false);
            int[] pixels = new int[(size * size)];
            int y = 0;
            while (y < size) {
                int x = 0;
                while (x < size) {
                    if (x > halfW - IMAGE_HALFWIDTH && x < IMAGE_HALFWIDTH + halfW
                            && y > halfH - IMAGE_HALFWIDTH && y < IMAGE_HALFWIDTH + halfH) {
                        pixels[(y * width) + x] = mBitmap.getPixel((x - halfW) + IMAGE_HALFWIDTH,
                                (y - halfH) + IMAGE_HALFWIDTH);
                    } else if (bitMatrix.get(x, y)) {
                        pixels[(y * size) + x] = ViewCompat.MEASURED_STATE_MASK;
                    } else {
                        pixels[(y * size) + x] = -1;
                    }
                    x++;
                }
                y++;
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
