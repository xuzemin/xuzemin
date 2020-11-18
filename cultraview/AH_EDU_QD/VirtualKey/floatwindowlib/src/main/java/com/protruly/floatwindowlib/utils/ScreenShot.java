package com.protruly.floatwindowlib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.yinghe.whiteboardlib.utils.BitmapUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * 屏幕截图工具类
 * 说明:需要添加READ_FRAME_BUFFER权限，并且用platform签名编译
 * Created by wang on 2017/7/28.
 */
public class ScreenShot {
	private Context context;

	public ScreenShot(Context context) {
		this.context = context;
	}

	public byte[] screenshot() {
		Matrix mDisplayMatrix = new Matrix();
		WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display mDisplay = mWindowManager.getDefaultDisplay();
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		mDisplay.getMetrics(mDisplayMetrics);
		float[] dims = { mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels };
		float degrees = getDegreesForRotation(mDisplay);
		boolean requiresRotation = (degrees > 0);
		if (requiresRotation) {
			mDisplayMatrix.reset();
			mDisplayMatrix.preRotate(-degrees);
			mDisplayMatrix.mapPoints(dims);
			dims[0] = Math.abs(dims[0]);
			dims[1] = Math.abs(dims[1]);
		}

		Bitmap mScreenBitmap = null;//SurfaceControl.screenshot((int) dims[0], (int) dims[1]);
		try {
			mScreenBitmap = getBitmapByReflect((int) dims[0], (int) dims[1]);
		} catch (Exception e){
			e.printStackTrace();
		}

		if (null == mScreenBitmap) {
			return null;
		}
		if (requiresRotation) {
			Bitmap ss = Bitmap.createBitmap(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels,
					Bitmap.Config.ARGB_4444);
			Canvas c = new Canvas(ss);
			c.translate(ss.getWidth() / 2, ss.getHeight() / 2);
			c.rotate(degrees);
			c.translate(-dims[0] / 2, -dims[1] / 2);
			c.drawBitmap(mScreenBitmap, 0, 0, null);
			c.setBitmap(null);
			mScreenBitmap = ss;
		}

		mScreenBitmap.setHasAlpha(true);
		mScreenBitmap.prepareToDraw();
		return ByteBitmap(mScreenBitmap);
	}

	public Bitmap screenshotToBitmap() {
		Matrix mDisplayMatrix = new Matrix();
		WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display mDisplay = mWindowManager.getDefaultDisplay();
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		mDisplay.getMetrics(mDisplayMetrics);
		float[] dims = { mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels };
		float degrees = getDegreesForRotation(mDisplay);
		boolean requiresRotation = (degrees > 0);
		if (requiresRotation) {
			mDisplayMatrix.reset();
			mDisplayMatrix.preRotate(-degrees);
			mDisplayMatrix.mapPoints(dims);
			dims[0] = Math.abs(dims[0]);
			dims[1] = Math.abs(dims[1]);
		}

		Bitmap mScreenBitmap = null;//SurfaceControl.screenshot((int) dims[0], (int) dims[1]);
		try {
			mScreenBitmap = getBitmapByReflect((int) dims[0], (int) dims[1]);
		} catch (Exception e){
			e.printStackTrace();
		}

		if (null == mScreenBitmap) {
			return null;
		}

		if (requiresRotation) {
			Bitmap ss = Bitmap.createBitmap(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels,
					Bitmap.Config.ARGB_4444);
			Canvas c = new Canvas(ss);
			c.translate(ss.getWidth() / 2, ss.getHeight() / 2);
			c.rotate(degrees);
			c.translate(-dims[0] / 2, -dims[1] / 2);
			c.drawBitmap(mScreenBitmap, 0, 0, null);
			c.setBitmap(null);

			// 释放临时图片
			BitmapUtils.releaseBitmap(mScreenBitmap);

			return ss;
		} else {
			mScreenBitmap.setHasAlpha(true);
			mScreenBitmap.prepareToDraw();
			return mScreenBitmap;
		}

	}

	/**
	 * 反射获得截图
	 * @param width
	 * @param height
	 * @return
	 * @throws Exception
     */
	private Bitmap getBitmapByReflect(int width, int height) throws Exception{
		String surfaceClassName;
		if (Build.VERSION.SDK_INT <= 17) {
			surfaceClassName = "android.view.Surface";
		} else {
			surfaceClassName = "android.view.SurfaceControl";
		}

		Class localClass = Class.forName(surfaceClassName);
		Class[] arrayOfClass = new Class[2];
		arrayOfClass[0] = Integer.TYPE;
		arrayOfClass[1] = Integer.TYPE;
		Method localMethod = localClass.getDeclaredMethod("screenshot", arrayOfClass);
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = Integer.valueOf(width);
		arrayOfObject[1] = Integer.valueOf(height);
		Bitmap b = (Bitmap)localMethod.invoke(null, arrayOfObject);
		return b;
	}
	
	private float getDegreesForRotation(Display mDisplay) {
		int rotation = mDisplay.getRotation();
		switch (rotation) {
		case Surface.ROTATION_0:
			return 0f;
		case Surface.ROTATION_90:
			return 360f - 90f;
		case Surface.ROTATION_180:
			return 360f - 180f;
		case Surface.ROTATION_270:
			return 360f - 270f;
		}
		return 0f;
	}

	private byte[] ByteBitmap(Bitmap b) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.JPEG, 10, baos);
		return baos.toByteArray();
	}

	public InputStream readAsRoot(File paramFile) throws Exception {
		Process localProcess = Runtime.getRuntime().exec("su");
		String str = "cat " + paramFile.getAbsolutePath() + "\n";
		localProcess.getOutputStream().write(str.getBytes());
		return localProcess.getInputStream();
	}

}
