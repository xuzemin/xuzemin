
package com.example.cutcapture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import views.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class ShotActiity extends Activity {
    private MediaProjectionManager mMediaProjectionManager;

    private WindowManager wm;

    private int displayWidth;

    private int displayHeight;

    private DisplayMetrics metrics;

    private int dpi;

    private ImageReader mImageReader;

    private int REQUEST_CODE = 1;

    private MediaProjection project;

    private VirtualDisplay virtualDisplay;

    String pathImage = ""; // 存储路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MediaProjectionManager对象,屏幕宽高及单位像素点,ImageReader对象
        mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
        wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        displayWidth = wm.getDefaultDisplay().getWidth();
        displayHeight = wm.getDefaultDisplay().getHeight();
        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        dpi = metrics.densityDpi;// 英寸点数
        mImageReader = ImageReader.newInstance(displayWidth, displayHeight, 0x1, 2);

        // 申请用户授权
        Intent intent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) { // 取消授权,结束进程
                finish();
            } else { // 表示确认授权,并取得屏幕截图的bitmap

                if (project == null) {
                    project = mMediaProjectionManager.getMediaProjection(resultCode, data);
                }

                // 获取存储屏幕截图的虚拟显示器
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        virtualDisplay = project.createVirtualDisplay("screen-mirror",
                                displayWidth, displayHeight, dpi,
                                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                                mImageReader.getSurface(), null, null);
                    }
                }, 50);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startCapture();
                    }
                }, 200);
            }
        }
    }

    private void startCapture() {
        Image image = mImageReader.acquireLatestImage();
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                Bitmap.Config.ARGB_4444);
        bitmap.copyPixelsFromBuffer(buffer);
        // 对屏幕截图bitmap进行截图
        // bitmap = Bitmap.createBitmap(bitmap, pointX, pointY, widthArea,
        // heightArea);
        image.close();
        Log.i("eyesee", "image data captured");

        if (bitmap != null) {
            try {
                File fileImage = new File(pathImage);
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
                    out.flush();
                    out.close();

                    LogUtil.showLog("startCapture");
                    if (fileImage.isFile() && fileImage.length() > 0) {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.tip_location) + pathImage, 0)
                                .show();
                        virtualDisplay.release(); // 释放存放屏幕截图的虚拟显示器
                        // 退出截屏
                        finish();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
