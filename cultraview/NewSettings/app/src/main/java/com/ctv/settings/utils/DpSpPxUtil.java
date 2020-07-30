package com.ctv.settings.utils;
import android.content.Context;
import android.util.DisplayMetrics;

public class DpSpPxUtil {
	/**
	 * 屏幕信息
	 *
	 * @param context
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static String screenInfo(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        String drawableType;
        if (density <= 0.75F){ // ldpi
            drawableType = "ldpi";
        } else if (density <= 1F){ // hdpi
            drawableType = "mdpi";
        } else if (density <= 1.5F){ // hdpi
            drawableType = "hdpi";
        } else if (density <= 2F){ // xdpi
            drawableType = "xhdpi";
        } else if ( density <= 3F){ // xxdpi
            drawableType = "xxhdpi";
        } else if (density <= 4F){ // xxxdp
            drawableType = "xxxhdpi";
        } else { // xxxdpi
            drawableType = "xxxxhdpi";
        }
		return String.format("screenInfo:width*height->%s*%spx, densityDpi->%s, density->%s, drawableType->%s",
                displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi, displayMetrics.density, drawableType);
	}

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param context
	 * @param pxValue
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param context
	 * @param dipValue
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param context
	 * @param pxValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param context
	 * @param spValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static float sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (spValue * fontScale + 0.5f);
	}

}
