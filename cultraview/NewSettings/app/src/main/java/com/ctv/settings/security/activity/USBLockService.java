
package com.ctv.settings.security.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ctv.settings.R;
import com.cultraview.tv.CtvPictureManager;

public class USBLockService extends Service {

    private final String TAG = "USBLockService";

    private WindowManager wm;

    private WindowManager.LayoutParams params = new WindowManager.LayoutParams();

    private Display display;

    private View rocket;

    private boolean isShow = false;

    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Log.d(TAG, "zhu...is show:" + isShow);
            if (msg.what == 1) {
                if ("on".equals(SystemProperties.get("persist.sys.usbLock"))) {
                    Log.d(TAG, "zhu..not show..add");
                    if (!isShow) {
                        wm.addView(rocket, params);
                        SystemProperties.set("persist.sys.touch_enable", "0");
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.greneral_lock_open_tip), Toast.LENGTH_LONG)
                                .show();
                        isShow = true;
                        int black_light = Settings.System.getInt(getContentResolver(),"isSeperateHear",1);
                        Log.i(TAG, "zhu..black_light:"+black_light);
                        if(black_light == 1) {
                            Log.i(TAG, "enableBacklight");
                            CtvPictureManager.getInstance().enableBacklight();
                            Settings.System.putInt(getContentResolver(), "isSeperateHear", 0);
                        }
                        // TODO: 2019-10-29 8386
                        //TvCommonManager.getInstance().setUsbTouch(getApplicationContext(), false);
                    }
                } else {
                    if (rocket != null && isShow) {
                        Log.d(TAG, "zhu...is show..remove");
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.greneral_lock_close_tip), Toast.LENGTH_LONG).show();
                        wm.removeView(rocket);
                        SystemProperties.set("persist.sys.touch_enable", "1");

                        isShow = false;
                        // TODO: 2019-10-29 8386
                        //TvCommonManager.getInstance().setUsbTouch(getApplicationContext(), true);
                    }
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "zhu....usblockservice destory..");
        onCreate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "zhu..service oncreate..");
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        initFloatButtonView();
        if ("on".equals(SystemProperties.get("persist.sys.usbLock.status"))) {
           // SystemViewHolder.checkFile(true, false);
        } else {
           // SystemViewHolder.checkFile(false, false);
        }
        handler.sendEmptyMessageDelayed(1, 200);
    }

    public void initFloatButtonView() {
        Log.d(TAG, "zhu...initFloatButtonView");
        display = wm.getDefaultDisplay();
        params.gravity = Gravity.TOP + Gravity.RIGHT; // 改为左上角对
        params.x = 0;
        params.y = 0;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        rocket = View.inflate(this, R.layout.greneral_usb_lock, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "zhu..onstart command..");
        if (intent == null) {
            Log.d(TAG, "zhu..intent is null.");
            return Service.START_STICKY_COMPATIBILITY;
        }
        String param = intent.getStringExtra("usblock");
        if (param == null) {
            Log.d(TAG, "intent getString is null.");
            return Service.START_STICKY_COMPATIBILITY;
        }
        if ("setting_switch".equals(param)) {
            Log.d(TAG, "zhu..command is setting_switch.");
            handler.sendEmptyMessageDelayed(1, 200);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /*
     * TvManager.getInstance().setTvosCommonCommand("SetUARTTOUCH_ON");
     * TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_OE_Low");
     * TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_SEL_High");
     */
    /*
     * try { TvManager.getInstance().setTvosCommonCommand("SetUARTTOUCH_OFF"); }
     * catch (TvCommonException e){ e.printStackTrace(); }
     */

    /*
     * public void screenTouchClose(){
     * Log.d("checkSourceInfo","currentSource:screenTouchClose start"); try{ int
     * status = Settings.System.getInt(getContentResolver(), "IS_ANNOTATE_ON",
     * 0); Log.d("checkSourceInfo","Annotation:status:"+status); if(status ==
     * 0){ int currentSource =
     * CtvCommonManager.getInstance().getCurrentTvInputSource();
     * Log.d("checkSourceInfo","currentSource:"+currentSource); if(currentSource
     * != 25){
     * TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_OE_Low");
     * TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_SEL_Low");
     * }else{
     * TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_OE_Low");
     * TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_SEL_High"); } }
     * }catch (Exception e){ e.printStackTrace(); } } public void
     * screenTouchOpen(){
     * Log.d("checkSourceInfo","currentSource:screenTouchOpen start"); try {
     * TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_OE_High");
     * TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_SEL_Low"); }
     * catch (TvCommonException e){ e.printStackTrace(); } }
     */
}
