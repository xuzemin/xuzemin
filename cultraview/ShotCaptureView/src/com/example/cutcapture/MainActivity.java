
package com.example.cutcapture;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import views.DrawView;
import views.DrawView.ClickListenrInterface;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity implements ClickListenrInterface {

    private static MainActivity activity;

    private DrawView dw;

    private int widthArea;

    private int heightArea;

    private int pointX;

    private int pointY;

    private int displayWidth;

    private int displayHeight;

    private String pathImage;

    private String tipSavePath;

    private WindowManager wm;

    private String strDate;

    private MainActivity mContext;

    // 显示和隐藏,关闭应用的广播
    public static final String SHOW_ACTION = "com.ctv.launcher.SHOW";

    public static final String HIDE_ACTION = "com.ctv.launcher.HIDE";

    public static final String FINISH_ACTION = "com.ctv.launcher.FINISH";

    public static MainActivity getInstance() {
        if (activity == null) {
            activity = new MainActivity();
        }
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mContext = MainActivity.this;
        createVirtualEnvironment();
        dw = (DrawView) findViewById(R.id.dw);
        dw.setClickListener(this);

        // 默认截图区域
        int[] point = new int[] {
                displayWidth / 6, displayHeight / 4, displayWidth * 5 / 6, displayHeight * 3 / 4
        };
        dw.initMarkArea(point);
        dw.postInvalidate();
    }

    private void createVirtualEnvironment() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        strDate = dateFormat.format(new Date());
        pathImage = Environment.getExternalStorageDirectory().getPath() + "/Pictures/" + strDate
                + ".png";
        tipSavePath = this.getResources().getString(R.string.save_dir) + "/Pictures/" + strDate
                + ".png";
        wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        displayWidth = wm.getDefaultDisplay().getWidth();
        displayHeight = wm.getDefaultDisplay().getHeight();
        String path = Environment.getExternalStorageDirectory().getPath();
    }

    @Override
    public void doConfirm() {
        // 发送广播,隐藏窗口选项按钮
        Intent hideIntent = new Intent(HIDE_ACTION);
        sendBroadcast(hideIntent);

        int[] area = dw.getDrawArea();
        widthArea = area[0];
        heightArea = area[1];
        int[] markPoint = dw.getDrawMarkPoint(); // 取重绘后的数组
        pointX = markPoint[0];
        pointY = markPoint[1];
        dw.circlePaint.setAlpha(0);
        dw.mBitPaint.setAlpha(0);
        dw.setMarkPoint(markPoint);
        dw.postInvalidate();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                screenshot();
            }
        }, 100);
    }

    @Override
    public void doCancel() {
        finish();
    }

    @Override
    public void doZoomOut() {
        int[] area = dw.getDrawArea();
        int widthArea = area[0];
        int heightArea = area[1];
        if (displayWidth > widthArea && displayHeight > heightArea) {
            dw.setBitmapPaint(R.drawable.zoomin);
            int[] point = new int[] {
                    0, 0, displayWidth, displayHeight
            };
            dw.setMarkPoint(point);
            dw.postInvalidate();
        } else {
            // 取触摸松手时的数组
            dw.setBitmapPaint(R.drawable.zoomout);
            int[] markPoint = dw.getMarkPoint();
            dw.setMarkPoint(markPoint);
            dw.postInvalidate();
        }
    }

    public void screenshot() {
        Bitmap mScreenBitmap = null;
        try {
            Class<?> testClass = Class.forName("android.view.SurfaceControl");
            Method saddMethod1 = testClass.getMethod("screenshot", new Class[] {
                    int.class, int.class
            });
            mScreenBitmap = (Bitmap) saddMethod1.invoke(null, new Object[] {
                    displayWidth, displayHeight
            });
            mScreenBitmap = Bitmap.createBitmap(mScreenBitmap, pointX, pointY, widthArea,
                    heightArea);
            if (mScreenBitmap != null) {
                File fileImage = new File(pathImage);
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                if (out != null) {
                    mScreenBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
                    out.flush();
                    out.close();
                    // 截图完成后恢复图标显示
                  //  dw.circlePaint.setAlpha(100);
                  //  dw.mBitPaint.setAlpha(100);
                  //  dw.invalidate();
                    if (fileImage.isFile() && fileImage.length() > 0) {
                        Toast.makeText(mContext,
                                getResources().getString(R.string.tip_location) + tipSavePath, Toast.LENGTH_LONG)
                                .show();
                        // 退出截屏
                        finish();

                        // 发送广播,显示窗口选项按钮
                      //  Intent showIntent = new Intent(SHOW_ACTION);
                      //  sendBroadcast(showIntent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent finishIntent = new Intent(FINISH_ACTION);
        sendBroadcast(finishIntent);
    }

}
