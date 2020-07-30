package com.protruly.floatwindowlib.activity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.constant.CommConsts;
import com.protruly.floatwindowlib.fragment.MoreFragment;
import com.protruly.floatwindowlib.fragment.MyPagerAdapter;
import com.protruly.floatwindowlib.helper.LightDB;
import com.protruly.floatwindowlib.service.FloatWindowService;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.SPUtil;
import com.yinghe.whiteboardlib.utils.ScreenUtils;
import com.yinghe.whiteboardlib.utils.ViewUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * 设置界面
 *
 * @author wang
 * @date 2018/07/02
 */
public class SettingsNewActivity extends AppCompatActivity {
    private static final String TAG = SettingsNewActivity.class.getSimpleName();

    public static final int KEY_MSG_FINISH = 3;

    private ViewPager mViewPager;

    SeekBar sound;
    SeekBar light;
    AudioManager audioManager;

    LinearLayout eyecareLL;
    ImageView eyecareImage;
    LinearLayout muteLL;
    ImageView muteImage;

    LinearLayout checkSysLL;
    LinearLayout moreSettingsLL;
    TabLayout mTabLayout;

    public static Handler mHandler = null;
    private int curPosition;
    private HandlerThread myHandlerThread;
    public static Handler dataHandler;

    String[] mTabTitles;
    private int[] pics = new int[]{

            R.mipmap.tab_2,
            R.mipmap.tab_3,
            //R.mipmap.tab_signal,
            R.mipmap.tab_1
    };


    private int[] picsSelect = new int[]{

            R.mipmap.tab_2_select,
            R.mipmap.tab_3_select,
            //R.mipmap.tab_signal,
            R.mipmap.tab_1_select
    };

    private int currentVolume;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        //隐藏掉整个ActionBar
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.settings_new);

        mHandler = new UIHandler(this);
        //创建一个线程,线程名字：handler-thread
        myHandlerThread = new HandlerThread("handler-thread");
        //开启一个线程
        myHandlerThread.start();
        dataHandler = new DataHandler(this, myHandlerThread.getLooper());

        layout();

        //初始化视图
        initViews();

        setListen();
    }

    /**
     * 设置窗口位置大小
     */
    private void layout() {
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        Point point = ScreenUtils.getScreenSize(this);
        lp.width = (int) (point.x * 0.50);//ScreenUtils.dip2px(this, 300);
        lp.height = (int) (point.y * 0.60);//ScreenUtils.dip2px(this, 600);
        lp.gravity = Gravity.CENTER;//设置对话框置顶显示
        win.setAttributes(lp);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
//			Log.d(TAG,"onTouchEvent点击了外部");
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void initViews() {
        eyecareLL = (LinearLayout) findViewById(R.id.pup_eyecare);
        eyecareImage = (ImageView) findViewById(R.id.eyecare_iv);
        int eyeCare = Settings.System.getInt(getContentResolver(), CommConsts.IS_EYECARE, 0);
        int resID = (eyeCare == 0) ? R.mipmap.eye_care_default : R.mipmap.eye_care_focus;
        eyecareImage.setImageResource(resID);

        // 静音
        muteLL = (LinearLayout) findViewById(R.id.ll_mute);
        muteImage = (ImageView) findViewById(R.id.mute_iv);

        // 系统检测
        checkSysLL = (LinearLayout) findViewById(R.id.ll_checksys);
        // 更多设置
        moreSettingsLL = (LinearLayout) findViewById(R.id.ll_more);

        //使用适配器将ViewPager与Fragment绑定在一起
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabTitles = getResources().getStringArray(R.array.tab_title_list);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mTabTitles);
        mViewPager.setAdapter(myPagerAdapter);
        curPosition = (Integer) SPUtil.getData(this, CommConsts.CURRENT_PAGE, 0);
        mViewPager.setCurrentItem(curPosition);

        //将TabLayout与ViewPager绑定在一起
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //设置分割线
        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.v_divider)); //设置分割线的样式
        linearLayout.setDividerPadding(ScreenUtils.dip2px(this, 1)); //设置分割线间隔

        mTabLayout.setupWithViewPager(mViewPager);

        //指定Tab的位置
        mTabLayout.setFillViewport(true);
        initTab(mTabLayout);

        sound = (SeekBar) findViewById(R.id.pup_seekbar2);
        light = (SeekBar) findViewById(R.id.pup_seekbar1);

        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        int maxSound = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//获取系统音量最大值
        sound.setMax(maxSound);

        setCustomIcon();
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateTabView(curPosition);
    }

    private void initTab(TabLayout tabLayout) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab == null) return;
            //这里使用到反射，拿到Tab对象后获取Class
            Class c = tab.getClass();
            try {
                Field field = c.getDeclaredField("mView");
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if (view == null) return;
                view.setTag(i);
                view.setOnClickListener(mTabOnClickListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置自定义位置图标
     */
    private void setCustomIcon() {
        mTabLayout.getTabAt(0).setCustomView(makeTabView(0));
        mTabLayout.getTabAt(1).setCustomView(makeTabView2(1));
        mTabLayout.getTabAt(2).setCustomView(makeTabView3(2));

    }

    /**
     * 引入布局设置图标和标题
     *
     * @param position
     * @return
     */
    private View makeTabView(int position) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.tab_text_icon, null);
        textView1 = (TextView) tabView.findViewById(R.id.textview);
        textView1.setText(mTabTitles[position]);
        imageView1 = (ImageView) tabView.findViewById(R.id.imageview);
        imageView1.setImageResource(pics[position]);
        return tabView;
    }

    private View makeTabView2(int position) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.tab_text_icon, null);
        textView2 = (TextView) tabView.findViewById(R.id.textview);
        imageView2 = (ImageView) tabView.findViewById(R.id.imageview);
        textView2.setText(mTabTitles[position]);
        imageView2.setImageResource(pics[position]);

        return tabView;
    }

    private View makeTabView3(int position) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.tab_text_icon, null);
        textView3 = (TextView) tabView.findViewById(R.id.textview);
        imageView3 = (ImageView) tabView.findViewById(R.id.imageview);
        textView3.setText(mTabTitles[position]);
        imageView3.setImageResource(pics[position]);

        return tabView;
    }


    View.OnClickListener mTabOnClickListener = (view) -> {
        int pos = (int) view.getTag();
        TabLayout.Tab tab = mTabLayout.getTabAt(pos);
        updateTabView(pos);

        if (tab != null) {
            tab.select();
        }
    };

    private void updateTabView(int position) {
        int color = R.color.black_light_sub;
        textView1.setTextColor(getResources().getColor(color));
        textView2.setTextColor(getResources().getColor(color));
        textView3.setTextColor(getResources().getColor(color));
        imageView1.setImageResource(pics[0]);
        imageView2.setImageResource(pics[1]);
        imageView3.setImageResource(pics[2]);

        color = R.color.white;
        if (position == 0) {
            textView1.setTextColor(getResources().getColor(color));
            imageView1.setImageResource(picsSelect[0]);

        } else if (position == 1) {
            textView2.setTextColor(getResources().getColor(color));
            imageView2.setImageResource(picsSelect[1]);
        } else {
            textView3.setTextColor(getResources().getColor(color));
            imageView3.setImageResource(picsSelect[2]);

        }

    }

    /**
     * 跳转到更多设置界面
     */
    private void gotoMoreSettings() {
        Intent intent = new Intent("com.cultraview.settings.CTVSETTINGS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "未找到设置界面",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 跳转到系统检测界面
     */
    private void goToCheckSys() {
        String mPackageName = "com.cultraview.hardware";
        String mActivityName = "com.cultraview.hardware.MainActivity";
        AppUtils.gotoOtherApp(this, mPackageName, mActivityName);
    }


    /**
     * 注册监听
     */
    private void setListen() {
        eyecareLL.setOnClickListener(mOnClickListener);
        muteLL.setOnClickListener(mOnClickListener);
        checkSysLL.setOnClickListener(mOnClickListener);
        moreSettingsLL.setOnClickListener(mOnClickListener);

        sound.setOnSeekBarChangeListener(new VoiceListener());
        light.setOnSeekBarChangeListener(new LightListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMuteIU();

        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//获取当前音量
        AppUtils.setProgress(sound, currentVolume, true);

        try {
            light.setProgress(AppUtils.getBacklight());
//			Log.d(TAG, "onResume获得当前亮度->" + mTvPictureManager.getBacklight());

//			mHandler.postDelayed(()->{
//				Log.d(TAG, "postDelayed获得当前亮度->" + mTvPictureManager.getBacklight());
//			}, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        light.setMax(100);
    }

    @Override
    protected void onStop() {
        super.onStop();
        curPosition = mViewPager.getCurrentItem();
        SPUtil.saveData(this, CommConsts.CURRENT_PAGE, curPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (myHandlerThread != null) {
            myHandlerThread.quit();
        }

        if (dataHandler != null) {
            dataHandler.removeCallbacksAndMessages(null);
            dataHandler = null;
        }
    }

    /**
     * 声音监听类
     */
    class VoiceListener implements SeekBar.OnSeekBarChangeListener {
        private int start;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            if (Math.abs(progress - start) <= 5) {
                return;
            }

            if (start == 0 && audioManager.isStreamMute(AudioManager.STREAM_MUSIC)) { // 静音开启时,初次进入时
                return;
            }

            Log.d(TAG, "声音设置值->" + progress);
            start = progress;

            dataHandler.postDelayed(() -> {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
            }, 50);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG, "声音设置值, start");
            if (audioManager.isStreamMute(AudioManager.STREAM_MUSIC)) { // 静音开启时，关闭禁音
//				AppUtils.keyEvent(KeyEvent.KEYCODE_VOLUME_MUTE, 0);
                mHandler.postDelayed(() -> {
                    int resID = R.mipmap.mute_default;
                    muteImage.setImageResource(resID);
                }, 50);
            }

            start = seekBar.getProgress();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.d(TAG, "声音设置值, end->");
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
            if (mHandler != null) {
                mHandler.postDelayed(() -> {
                    int resID = R.mipmap.mute_default;
                    muteImage.setImageResource(resID);
                }, 100);
            }
        }
    }

    /**
     * 调节亮度时，关闭光感
     */
    private void closeLightSense() {
        int lightSense = Settings.System.getInt(getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);

        if (lightSense == 1) { // 当前光感开启时，关闭光感
            Settings.System.putInt(getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);
            mHandler.removeMessages(SettingsNewActivity.MSG_UPDATE_LIGHT); // 更新亮度进度条

            if (MoreFragment.mHandler != null) {
                Message msg = Message.obtain();
                msg.what = MoreFragment.KEY_CHANGE_LIGHT_SENSE;
                msg.obj = false;
                MoreFragment.mHandler.sendMessage(msg);
            }

            if (FloatWindowService.mHandler != null) {
                Message msg = Message.obtain();
                msg.what = FloatWindowService.KEY_CHANGE_LIGHT_SENSE;
                msg.obj = false;
                FloatWindowService.mHandler.sendMessage(msg);
            }
        }
    }

    /**
     * 亮度监听类
     */
    class LightListener implements SeekBar.OnSeekBarChangeListener {
        private int lastProgress;
        private boolean isStarting = false; // 是否正在滑动

        @Override
        public void onProgressChanged(SeekBar seekBar, final int progress, boolean b) {
            light.setProgress(progress);
            try {
                Settings.System.putInt(SettingsNewActivity.this.getContentResolver(), "backlight", progress);
                SystemProperties.set("persist.sys.backlight", "" + progress);
                AppUtils.setBacklight(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }

            LightDB db = new LightDB(SettingsNewActivity.this);
            db.updatePicModeSetting(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    /**
     * 设置护眼模式
     */
    private void setEyecareMode() {
        int eyeCare = Settings.System.getInt(getContentResolver(), CommConsts.IS_EYECARE, 0);
        eyeCare = (eyeCare == 0) ? 1 : 0;

        int resID = (eyeCare == 0) ? R.mipmap.eye_care_default : R.mipmap.eye_care_focus;
        eyecareImage.setImageResource(resID);
        Settings.System.putInt(getContentResolver(), CommConsts.IS_EYECARE, eyeCare);
        Log.d(TAG, "eyeCare->" + eyeCare);
    }

    /**
     * 设置静音模式
     */
    private void setMuteMode() {
        boolean muteFlag = audioManager.isStreamMute(AudioManager.STREAM_MUSIC);
        muteFlag = !muteFlag;
        int direction = muteFlag ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE;
//        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, 0);
        if (muteFlag) {
            //获取当前音量
            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            AppUtils.setProgress(sound, 0, true);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            AppUtils.setProgress(sound, currentVolume, true);
        }

        int resID = muteFlag ? R.mipmap.mute_focus : R.mipmap.mute_default;
        muteImage.setImageResource(resID);


        Log.d(TAG, "setMuteMode muteFlag->" + muteFlag);
    }

    /**
     * 更新背光
     */
    private void updateBlackLightSeekbar(boolean flag) {
        try {
            int blackLight = AppUtils.getBacklight();
            AppUtils.setProgress(light, blackLight, flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新背光
     */
    private void updateBlackLightSeekbar() {
        try {
            int blackLight = AppUtils.getBacklight();
            light.setProgress(blackLight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新UI
     */
    private void updateMuteIU() {
        boolean muteFlag = audioManager.isStreamMute(AudioManager.STREAM_MUSIC);
        int resID = muteFlag ? R.mipmap.mute_focus : R.mipmap.mute_default;
        muteImage.setImageResource(resID);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN: {
                mHandler.postDelayed(() -> {
                    int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//获取当前音量
                    AppUtils.setProgress(sound, currentVolume, true);

                    int resID = R.mipmap.mute_default;
                    muteImage.setImageResource(resID);
                }, 100);
                break;
            }
            case KeyEvent.KEYCODE_VOLUME_MUTE: {
                mHandler.postDelayed(() -> {
                    if (!SettingsNewActivity.this.isFinishing()) {
                        updateMuteIU();
                    }
                }, 100);
                break;
            }
            case KeyEvent.KEYCODE_BACK: {
                finish();
                break;
            }
        }

        return false;
    }

    /**
     * 点击事件
     */
    private View.OnClickListener mOnClickListener = v -> {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }

        int id = v.getId();
        boolean isExit = false;
        switch (id) {
            // 设置护眼模式
            case R.id.pup_eyecare: {
                setEyecareMode();
                break;
            }
            // 静音
            case R.id.ll_mute: {
                setMuteMode();
                break;
            }

            // 系统检测
            case R.id.ll_checksys: {
                goToCheckSys();
                isExit = true;
                break;
            }
            // 更多
            case R.id.ll_more: {
                gotoMoreSettings();
                isExit = true;
                break;
            }
        }

        // 退出设置界面
        if (isExit) {
            SettingsNewActivity.this.finish();
        }
    };

    Runnable hideRunnable = () -> {
        if (!SettingsNewActivity.this.isFinishing()) {
            SettingsNewActivity.this.finish();
        }
    };

    public final static int MSG_UPDATE_LIGHT = 1;
    public final static int MSG_UPDATE_MUTE = 2;
    public final static int MSG_UPDATE_FINISH = 3;
    public final static int MSG_UI_HIDE = 4;

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<SettingsNewActivity> weakReference;

        public UIHandler(SettingsNewActivity activity) {
            super();
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            SettingsNewActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            switch (msg.what) {
                case MSG_UPDATE_LIGHT: { // 自动更新背光
                    if ((msg.obj != null)
                            && (msg.obj instanceof Boolean)) {
                        boolean flag = (boolean) msg.obj;
                        activity.updateBlackLightSeekbar(flag);
                    } else {
                        activity.updateBlackLightSeekbar();
                    }
                    break;
                }
                case MSG_UPDATE_MUTE: { // 自动更新静音图标
                    activity.updateMuteIU();
                    break;
                }
                case MSG_UPDATE_FINISH: { // 停止
                    activity.finish();
                    break;
                }
                case MSG_UI_HIDE: { // 判断有UI操作
//		            Log.d(TAG, "判断有UI操作");
//		            removeCallbacks(activity.hideRunnable);
//		            postDelayed(activity.hideRunnable, 5000);
                    break;
                }
                default:
                    break;
            }
        }
    }

    /**
     * data异步处理
     */
    public static final class DataHandler extends Handler {
        WeakReference<SettingsNewActivity> weakReference;

        public DataHandler(SettingsNewActivity activity, Looper looper) {
            super(looper);
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            SettingsNewActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
        }
    }
}
