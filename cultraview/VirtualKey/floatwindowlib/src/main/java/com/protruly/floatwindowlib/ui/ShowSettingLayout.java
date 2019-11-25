package com.protruly.floatwindowlib.ui;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.apkfuns.logutils.LogUtils;
import com.cultraview.tv.CtvPictureManager;
import com.protruly.floatwindowlib.MyApplication;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.constant.CommConsts;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.helper.LightDB;
import com.yinghe.whiteboardlib.utils.ACache;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CmdUtils;
import com.yinghe.whiteboardlib.utils.CommConst;

/**
 * 更多设置界面
 * Created by wy on 2017/6/21.
 */

public class ShowSettingLayout extends FrameLayout {
    private static final String TAG = ShowSettingLayout.class.getSimpleName();

    private Context mContext;
//    LinearLayout setting;
//    LinearLayout whiteBoard;

    LinearLayout screenshot;
    LinearLayout eyecareLL;
    ImageView eyecareImage;
    LinearLayout temperatureLL;

    SeekBar sound;
    SeekBar light;
    AudioManager audioManager;
    CtvPictureManager mTvPictureManager = null;
    Handler mHandler;

    public ShowSettingLayout(Context context) {
        this(context, null);
    }

    public ShowSettingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.popup_setting, this);

        initView();
        setListen();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mHandler = new Handler();
//        setting = (LinearLayout) findViewById(R.id.pup_seting);
//        whiteBoard = (LinearLayout) findViewById(R.id.pup_whiteboard);

        screenshot = (LinearLayout) findViewById(R.id.pup_screenshot);
        eyecareLL = (LinearLayout) findViewById(R.id.pup_eyecare);
        eyecareImage = (ImageView) findViewById(R.id.eyecare_iv);
        int eyeCare = Settings.System.getInt(mContext.getContentResolver(), CommConsts.IS_EYECARE, 0);
        int resID = (eyeCare == 0) ? R.mipmap.eye_care_default: R.mipmap.eye_care_focus;
        eyecareImage.setImageResource(resID);

        temperatureLL = (LinearLayout) findViewById(R.id.pup_temperature);
        sound = (SeekBar) findViewById(R.id.pup_seekbar2);
        light = (SeekBar) findViewById(R.id.pup_seekbar1);

        audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        int maxSound = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//获取系统音量最大值
        sound.setMax(maxSound);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//获取当前音量
        sound.setProgress(currentVolume);//音量控制Bar的当前值设置为系统音量当前值

        try {
            mTvPictureManager = CtvPictureManager.getInstance();
            light.setProgress(mTvPictureManager.getBacklight());
        } catch (Exception e){
            e.printStackTrace();
        }
        light.setMax(100);
    }

    /**
     * 注册监听
     */
    private void setListen() {
//        setting.setOnClickListener(mOnClickListener);
        screenshot.setOnClickListener(mOnClickListener);
//        whiteBoard.setOnClickListener(mOnClickListener);
        eyecareLL.setOnClickListener(mOnClickListener);
        temperatureLL.setOnClickListener(mOnClickListener);

        sound.setOnSeekBarChangeListener(new SeekBarListen());
        light.setOnSeekBarChangeListener(new LightListen());
    }

    /**
     * 设置护眼模式
     */
    private void setEyecareMode(){
        int eyeCare = Settings.System.getInt(mContext.getContentResolver(), CommConsts.IS_EYECARE, 0);
        eyeCare = (eyeCare == 0) ? 1 : 0;

        int resID = (eyeCare == 0) ? R.mipmap.eye_care_default: R.mipmap.eye_care_focus;
        eyecareImage.setImageResource(resID);
        Settings.System.putInt(mContext.getContentResolver(), CommConsts.IS_EYECARE, eyeCare);
        Log.d(TAG, "eyeCare->" + eyeCare );
    }

    /**
     * 显示温度
     */
    private void showTemperature(){
        float tmpValue = CmdUtils.getTmpValue();

        boolean isFirst = false;
        ThemometerLayout thmometerWindow = FloatWindowManager.getThmometerWindow();
        if (thmometerWindow == null) {
            thmometerWindow = FloatWindowManager.createThemometerWindow(mContext.getApplicationContext());
            isFirst = true;
        }

        int visibility = thmometerWindow.getVisibility();
        if (!isFirst && visibility == View.VISIBLE){ // 若当前是显示，则隐藏
            thmometerWindow.setVisibility(View.INVISIBLE);
        } else { // 若当前是隐藏，则显示
            thmometerWindow.setVisibility(View.VISIBLE);
            // 更新进度
            if (ThemometerLayout.mHandler != null){
                Message message = ThemometerLayout.mHandler.obtainMessage(1, tmpValue);
                ThemometerLayout.mHandler.sendMessage(message);

                // 延迟消失
                ThemometerLayout.mHandler.postDelayed(()->{
                    ThemometerLayout thmometer = FloatWindowManager.getThmometerWindow();
                    if (thmometer != null){
                        thmometer.setVisibility(View.INVISIBLE);
                    }
                }, 3000);
            }

        }
    }

    /**
     * 点击事件
     */
    private OnClickListener mOnClickListener = v -> {
        int id = v.getId();
        switch (id) {
//            //进入系统设置界面
//            case R.id.pup_seting: {
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("com.android.tv.settings","com.android.tv.settings.MainSettings"));
////                Intent intent = new Intent("android.settings.SETTINGS");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                try {
//                    getContext().startActivity(intent);
//                } catch (ActivityNotFoundException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getContext(), "未找到设置界面",
//                            Toast.LENGTH_LONG).show();
//                }
//                break;
//            }

            //截图
            case R.id.pup_screenshot: {
                ACache aCache = ACache.get(mContext.getApplicationContext());
                aCache.put(CommConst.IS_SCREEN_SHOT_ING, "true");
                break;
            }

//            //进入白板
//            case R.id.pup_whiteboard: {
//                Intent i = new Intent();
//                i.setComponent(new ComponentName("com.protruly.whiteboard",
//                        "com.protruly.whiteboard.MainActivity"));
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                try {
//                    getContext().startActivity(i);
//                } catch (ActivityNotFoundException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
            // 设置护眼模式
            case R.id.pup_eyecare: {
                setEyecareMode();
                break;
            }
            // 温度显示
            case R.id.pup_temperature: {
                showTemperature();
                break;
            }

        }

        // 退出设置界面
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    };

    /**
     * 声音监听类
     */
    class SeekBarListen implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int progress = seekBar.getProgress();
            LogUtils.d("声音设置值：" + progress);

            MyApplication.IsTouchSeting = true;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            MyApplication.IsTouchSeting = false;
        }
    }


    /**
     * 亮度监听类
     */
    class LightListen implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int progress = seekBar.getProgress();
            LogUtils.d("亮度设置值：" + progress);

            light.setProgress(progress);
            try {
                mTvPictureManager.setBacklight(progress);
            } catch (Exception e){
                e.printStackTrace();
                LogUtils.e("设置亮度异常：" + e.getMessage());
            }

            LightDB db = new LightDB(getContext());
            db.updatePicModeSetting(progress);
            MyApplication.IsTouchSeting = true;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            MyApplication.IsTouchSeting = false;
        }
    }
}
