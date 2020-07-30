
package com.cultraview.hardware;

import com.mstar.android.tv.TvCommonManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import utils.LogUtil;
import views.TestItemView;

public class MainActivity extends Activity {

    private int widthPixels;

    private int heightPixels;

    private ImageView mImageView;

    private Button button;
    private Button buttonExit;

    private TestItemView moudelTouch;

    private TestItemView moudelPC;

    private TestItemView moudelSense;

    private ProgressBar progressBar;

    private static final String IS_LIGHTSENSE = "IS_LIGHTSENSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getMyDisplay();
        initView();
        
        SystemClock.sleep(1);
        testDevice();
        mHandler.sendEmptyMessage(CHECK_TOUCH_START);
//        mHandler.sendEmptyMessageDelayed(DELAY_HANDLER, 50);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void checkUpdate(){
        String fileName = "HardWare.apk";
        Intent intent = new Intent("com.ctv.utils.SILENT_INSTALL_ACTION");
        intent.putExtra("fileName", fileName);
        sendBroadcast(intent);
    }

    private void getMyDisplay() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels;
    }

    private void initView() {
        mImageView = (ImageView) findViewById(R.id.iv);
        button = (Button) findViewById(R.id.bt_test);
        buttonExit = (Button) findViewById(R.id.bt_exit);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        button.requestFocus();
        button.requestFocusFromTouch();
        RelativeLayout.LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
        params.height = heightPixels * 1 / 2;
        params.width = widthPixels * 3 / 4;
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setLayoutParams(params);

        moudelTouch = (TestItemView) findViewById(R.id.moudel_touch);
        moudelTouch.setName(R.string.device_touch);
        moudelTouch.setResult(R.string.istouch);
        moudelTouch.setIcon(R.drawable.right);

        moudelPC = (TestItemView) findViewById(R.id.moudel_pc);
        moudelPC.setName(R.string.device_pcModule);
        moudelPC.setResult(R.string.isPC);
        moudelPC.setIcon(R.drawable.right);

        moudelSense = (TestItemView) findViewById(R.id.moudel_sense);
        moudelSense.setName(R.string.device_lightsense);
        moudelTouch.setIcon(R.drawable.right);
        moudelSense.setResult(R.string.isLightSense);

        button.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    testDevice();
                    mHandler.sendEmptyMessage(CHECK_TOUCH_START);
                }
                return false;
            }
        });
        buttonExit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    private boolean checkTouchRet = false;
    
    private static final int CHECK_TOUCH_END = 1;

    private static final int CHECK_TOUCH_START = 2;

    private static final int CHECK_SENSE = 3;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CHECK_TOUCH_END: {
                    // checkTouchRet = SystemProperties.get("check_touch",
                    // "0").equals("1");
                    LogUtil.showLog("zhu...checkTouchRet:" + checkTouchRet);
                    progressBar.setVisibility(View.GONE);
                    if (checkTouchRet) {
                        moudelTouch.setIcon(R.drawable.right);
                        moudelTouch.setResult(R.string.istouch);
                    } else {
                        moudelTouch.setIcon(R.drawable.wrong);
                        moudelTouch.setResult(R.string.untouch);
                    }
                    button.setClickable(true);
                    break;
                }
                case CHECK_TOUCH_START: {
                    button.setClickable(false);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                }
                case CHECK_SENSE: {
                    boolean isLightSense = SystemProperties.get("service.light.sense.enable", "0")
                            .equals("1");
                    boolean isSenseOpen = Settings.System.getInt(
                            MainActivity.this.getContentResolver(), IS_LIGHTSENSE, 0) == 1;
                    int resIcon = R.drawable.wrong;
                    int result = R.string.unLightSense;
                    if (!isLightSense) { // 是否支持光感硬件
                        resIcon = R.drawable.wrong;
                        result = R.string.un_support_LightSense;
                    } else if (!isSenseOpen) {// 光感开关未打开
                        resIcon = R.drawable.warning;
                        result = R.string.closeSense;
                    } else { // 判断光感值
                        int num = getLightSense();
                        if (num <= 4) { // 光感异常
                            resIcon = R.drawable.wrong;
                            result = R.string.unLightSense;
                        } else { // 光感正常
                            resIcon = R.drawable.right;
                            result = R.string.isLightSense;
                        }
                    }

                    moudelSense.setIcon(resIcon);
                    moudelSense.setResult(result);
                    break;
                }
                default:
                    break;
            }
        };
    };

    /**
     * 获得光感值
     * @return
     */
    private int getLightSense() {
        int num = 0;
        String numStr = SystemProperties.get("LIGHT_SENSE", "0");
        try {
            num = Integer.parseInt(numStr);
//            if (num != 0) {
//                num = num >= 180 ? 180 : num;
//                num = 100 - (int) (num / 180F * 100);
//                if (num == 50) {
//                    num = 51;
//                }
//            }
        } catch (Exception e) {
            num = 0;
            e.printStackTrace();
            return num;
        }
        return num;
    }

    private void testDevice() {
        LogUtil.showLog("zhu...testDevice....");
        boolean isCheckOPS = checkOPS();
        if (isCheckOPS) {
            moudelPC.setIcon(R.drawable.right);
            moudelPC.setResult(R.string.isPC);
        } else {
            moudelPC.setIcon(R.drawable.wrong);
            moudelPC.setResult(R.string.unPC);
        }
        LogUtil.showLog("checkOPS=" + isCheckOPS);

        checkLightSense();
        // 0:触摸框OK,1:触摸框NG
        checkTouch();
    }

    public void checkTouch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int[] ret = TvCommonManager.getInstance().setTvosCommonCommand("CHECKTOUCH");
                    LogUtil.showLog("zhu...checkTouchRet:" + ret[0]);
                    checkTouchRet = ret[0] == 1 ? true : false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessageDelayed(CHECK_TOUCH_END, 2000);
            }
        }).start();
    }

    public void checkLightSense() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessageDelayed(CHECK_SENSE, 2000);
            }
        }).start();
    }

    /**
     * @return flag true:ops正常 false:ops NG 0:ops接入 1:ops没有接入
     */
    public boolean checkOPS() {
        int[] GetOPSDEVICESTATUS = TvCommonManager.getInstance().setTvosCommonCommand(
                "GetOPSDEVICESTATUS");
        return (GetOPSDEVICESTATUS[0] == 1);
    }

    private boolean isFKeyDown = false;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        LogUtil.showLog("zhu...keycode:..." + keyCode);
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            testDevice();
            mHandler.sendEmptyMessage(CHECK_TOUCH_START);
            LogUtil.showLog("zhu...keycode_enter...");
            /*
             * if (!button.hasFocus()) { button.requestFocus(); }
             * button.clearFocus();
             */
        }
        return super.dispatchKeyEvent(event);
    }
}
