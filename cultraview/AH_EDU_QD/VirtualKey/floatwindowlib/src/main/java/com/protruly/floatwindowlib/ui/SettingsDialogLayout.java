package com.protruly.floatwindowlib.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.cultraview.tv.CtvTvManager;
import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.ops.HHTOpsManager;
import com.hht.android.sdk.source.HHTSourceManager;
import java.util.Calendar;
import android.text.format.DateFormat;
import com.mstar.android.tv.TvCommonManager;
import com.protruly.floatwindowlib.activity.SettingNewActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.cultraview.tv.CtvPictureManager;
import com.protruly.floatwindowlib.MyApplication;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.adapter.NotificationAdapter;
import com.protruly.floatwindowlib.been.AppInfo;
import com.protruly.floatwindowlib.been.NotificationInfo;
import com.protruly.floatwindowlib.callback.SimpleItemTouchCallBack;
import com.protruly.floatwindowlib.constant.CommConsts;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.entity.SpacesItemDecoration;
import com.protruly.floatwindowlib.helper.LightDB;
import com.protruly.floatwindowlib.service.FloatWindowService;
import com.protruly.floatwindowlib.utils.ApkInfoUtils;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.yinghe.whiteboardlib.bean.TimeInfo;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.SPUtil;
import com.yinghe.whiteboardlib.utils.ScreenUtils;
import com.yinghe.whiteboardlib.utils.TimeUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.hht.android.sdk.device.HHTCommonManager.EnumEyeProtectionMode.EYE_OFF;
import static com.hht.android.sdk.device.HHTCommonManager.EnumEyeProtectionMode.EYE_PLUS;
import static com.hht.android.sdk.device.HHTCommonManager.EnumEyeProtectionMode.EYE_RGB;
import static com.hht.android.sdk.device.HHTCommonManager.EnumEyeProtectionMode.EYE_DIMMING;

import static com.protruly.floatwindowlib.utils.SystemUtils.AP_BAND_2G;
import static com.protruly.floatwindowlib.utils.SystemUtils.AP_BAND_5G;
import static com.protruly.floatwindowlib.utils.SystemUtils.HOST_BAND_TYPE;

/**
 * Desc:设置弹框
 *
 * @author wang
 * @time 2017/4/13.
 */
public class SettingsDialogLayout extends FrameLayout {
    private static final String TAG = SettingsDialogLayout.class.getSimpleName();
    private Context mContext;
    public final static boolean IS_AH_EDU_QD = TextUtils.equals(SystemProperties.get("ro.build.display.id", ""), "CN8386_AH_EDU_QD");
    // 宽和高
    public static int viewWidth;
    public static int viewHeight;

    private String mPassWord;

    private GridView gv_apps;

    private boolean isRightShow = false; // 是否在右边弹出信号源

    TextView tvWeek;
    TextView tvDay;
    TextView tvTime;
    private String date;
    private TextView textviweapm;

    ImageView wireImage; // 有线
    ImageView wifiImage;// 无线
    ImageView hostpotImage; // 热点
    ImageView hostpotImage_5G; // 热点
    ImageView bluetoothImage; // 热点
    ImageView eyecareImage;

    ImageView OPSImage; // OPS
    ImageView HomeImage;// 主页
    ImageView ShutdownImage; // 关机

    SeekBar sound;
    SeekBar light;
    AudioManager audioManager;
    CtvPictureManager mTvPictureManager = null;
    public static Handler mHandler;

    WifiManager mWifiManager;
    BluetoothAdapter bluetoothAdapter;
    EthernetManager mEthernetManager;
    PppoeManager mPppoeManager;

    private SelectAppDialog dialogView;
    private RelativeLayout pupAdd;
    private ConnectivityManager connectivityManager;
    ImageView deleteImage;
    Dialog selectDialog;

    private ImageView addMenu;
    private OnStartTetheringCallback startTetheringCallback;
    private ApkInfoUtils apkInfoUtils;
    private TextView tvAdd;
    private LinearLayout energySaving;
    private List<Map<String, Object>> app_list;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = {R.drawable.apps_wire_normal, R.drawable.apps_wireless_normal, R.drawable.apps_hotspot_normal,
            R.drawable.apps_settings_normal, R.drawable.apps_screenshot_normal, R.drawable.apps_timer_normal,
            R.drawable.apps_record_normal, R.drawable.apps_magnifier_normal, R.mipmap.light_sense_default,
            R.drawable.apps_eye_care_normal, R.mipmap.energy_saving_default, R.drawable.apps_add_normal};
    private int[] iconName = null;

    protected boolean isOpenWifi = false, isDialog = false;
    boolean isOpenHotspot = false;
    boolean isOpenHotspot5G = false;
    private int eyeFlag = 0;
    private Listener mListener = new Listener() {
        @Override
        public void onConnectivityChange(Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
                upApStateChange(intent);
            }
        }
    };
    private TextView tv_sound;
    private TextView tv_light;
    private LinearLayout lightSenseLL;
    private ImageView lightSenseImage;
    private LinearLayout magnifierLL;
    private RecyclerView mRecyclerView;
    private NotificationAdapter notificationAdapter;
    private ArrayList<NotificationInfo> notificationList;
    private TextView mAllIgnore;
    private TextView tv_eye;

    /**
     * 更新AP状态
     *
     * @param intent
     */
    private void upApStateChange(Intent intent) {
        int state = intent.getIntExtra("wifi_state",
                14);

        SPUtil.saveData(getContext(), CommConsts.WIFI_STATE, state);
        switch (state) {
            default:
            case 12:
                Log.d(TAG, "WIFI_AP_STATE_ENABLING");
                break;
            case 13:
                Log.d(TAG, "WIFI_AP_STATE_ENABLED");
                // 开启热点
                if (!isOpenHotspot) {
                    isOpenHotspot = true;
                    setOpenHotspot(false, false);
                    Log.d(TAG, "CHANGE WIFI_AP_STATE_ENABLED:" + isOpenHotspot);
                }

                SPUtil.saveData(getContext(), CommConsts.IS_HOTSPOT_ON, isOpenHotspot);
                break;
            case 10:
                Log.d(TAG, "WIFI_AP_STATE_DISABLING");
                break;
            case 11:
                Log.d(TAG, "WIFI_AP_STATE_DISABLED");
                if (isOpenHotspot) {
                    isOpenHotspot = false;
                    Log.i(TAG, "CHANGE WIFI_AP_STATE_DISABLED:" + isOpenHotspot);
                    SPUtil.saveData(getContext(), CommConsts.IS_HOTSPOT_ON, isOpenHotspot);
                }
                break;
        }
    }

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
     */
    private Timer timer;

    public SettingsDialogLayout(Context context) {
        this(context, null);
    }

    public SettingsDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.dialog_setting, this);
        initView();

        isOpenHotspot = isWifiApEnabled();
        reflashUI();
        initReceiver();


        Log.i(TAG, "----last-isOpenHotspot:" + isOpenHotspot);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_OUTSIDE) {
            this.setVisibility(View.GONE);
            if (isRightShow) {
                FloatWindowManager.getMenuWindow().changeIndexBg(true);
            } else {
                FloatWindowManager.getMenuWindowLeft().changeIndexBg(true);
            }

        }
        return super.onTouchEvent(event);
    }

    /**
     * 初始化UI
     */
    @SuppressLint("SetTextI18n")
    private void initView() {
        mHandler = new UIHandler(this);
        apkInfoUtils = new ApkInfoUtils();
        tvWeek = findViewById(R.id.tv_week);
        tvDay = findViewById(R.id.tv_day);
        tvTime = findViewById(R.id.tv_time);
        textviweapm = findViewById(R.id.apm);

        // 有线网络
        mEthernetManager = (EthernetManager) getContext().getSystemService("ethernet");
        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        // P2P网络
//        mPppoeManager = PppoeManager.getInstance(getContext());

        //OPS HOME shuatdow
        OPSImage = findViewById(R.id.btn_ops);
        HomeImage = findViewById(R.id.btn_android);
        ShutdownImage = findViewById(R.id.btn_shutdown);

        //通知
        mRecyclerView = findViewById(R.id.recycleview_notification);
        mAllIgnore = findViewById(R.id.all_ignore);

        notificationList = FloatWindowManager.getNotificationList();
        if (notificationList != null) {
            Log.i("gyx", "init recycleview");
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
            notificationAdapter = new NotificationAdapter(mContext, notificationList);
            SimpleItemTouchCallBack simpleItemTouchCallBack = new SimpleItemTouchCallBack(notificationAdapter);
            ItemTouchHelper helper = new ItemTouchHelper(simpleItemTouchCallBack);
            helper.attachToRecyclerView(mRecyclerView);
            mRecyclerView.setAdapter(notificationAdapter);
        }

        // WiFi网络
        wifiImage = findViewById(R.id.wifi_image);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        // WiFi网络
        hostpotImage = findViewById(R.id.hotspot_image);
        hostpotImage_5G = findViewById(R.id.hotspot_image_5);

        bluetoothImage = findViewById(R.id.iv_bluetooth);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        boolean isBluetoothOpen = bluetoothAdapter.isEnabled();
        if (isBluetoothOpen) {
            bluetoothImage.setImageResource(R.mipmap.apps_bluetooth_focus);
        } else {
            bluetoothImage.setImageResource(R.mipmap.apps_bluetooth_default);
        }

        lightSenseLL = findViewById(R.id.pup_light_sense);
        lightSenseImage = findViewById(R.id.iv_light_sense);


        //放大镜
        magnifierLL = findViewById(R.id.pup_magnifier);
        View wireLL = findViewById(R.id.pup_net);
        View wifiLL = findViewById(R.id.pup_wifi);
        View hotspotLL = findViewById(R.id.pup_hotspot);
        View settingsLL = findViewById(R.id.pup_settings);
        View hotspot5G = findViewById(R.id.pup_hotspot_5);
        View bluetooth = findViewById(R.id.pup_bluetooth);
        View screenshotLL = findViewById(R.id.pup_screenshot);
        View timerLL = findViewById(R.id.pup_timer);
        View recordLL = findViewById(R.id.pup_record);

        //节能
        energySaving = findViewById(R.id.pup_energy_saving);
        // 护眼
        View eyecareLL = findViewById(R.id.pup_eyecare);
        tv_eye = findViewById(R.id.tv_eye);
        eyecareImage = findViewById(R.id.eyecare_iv);
        if(IS_AH_EDU_QD){
            eyeFlag = getEyePlusIndex_Light();
            int resID = (eyeFlag == 0) ? R.mipmap.apps_eye_care_default : R.mipmap.apps_eye_care_focus;
            if (eyeFlag ==0){
            tv_eye.setText(mContext.getString(R.string.apps_eyecare_close));
            }else if (eyeFlag == 1) {
                tv_eye.setText(mContext.getString(R.string.apps_eyecare_Electric));
            }else if (eyeFlag == 2) {
            tv_eye.setText(mContext.getString(R.string.apps_eyecare_open));
        }else {
            tv_eye.setText(mContext.getString(R.string.apps_eyecare_light));
        }
            eyecareImage.setImageResource(resID);
        }else{
            eyeFlag = getEyePlusIndex();
            int resID = (eyeFlag == 0) ? R.mipmap.apps_eye_care_default : R.mipmap.apps_eye_care_focus;
            if (eyeFlag ==0){
                tv_eye.setText(mContext.getString(R.string.apps_eyecare_close));
            }else if (eyeFlag == 1) {
                tv_eye.setText(mContext.getString(R.string.apps_eyecare_open));
            }else {
                tv_eye.setText(mContext.getString(R.string.apps_eyecare_light));
            }
        eyecareImage.setImageResource(resID);
        }

        pupAdd = findViewById(R.id.pup_add);
        pupAdd.setOnLongClickListener(mOnLongClickListener);

        tvAdd = findViewById(R.id.tv_add);
        addMenu = findViewById(R.id.add_menu);
        deleteImage = findViewById(R.id.btn_delete);

        // 声音和亮度
        sound = findViewById(R.id.pup_seekbar2);
        light = findViewById(R.id.pup_seekbar1);

        tv_sound = findViewById(R.id.tv_sound);
        tv_light = findViewById(R.id.tv_light);

        audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        int maxSound = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//获取系统音量最大值
        sound.setMax(maxSound);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//获取当前音量
        sound.setProgress(currentVolume);//音量控制Bar的当前值设置为系统音量当前值
        tv_sound.setText("" + currentVolume);
        try {
            mTvPictureManager = CtvPictureManager.getInstance();
            int progress = SystemProperties.getInt("persist.sys.backlight", 50);
            light.setProgress(progress);
            tv_light.setText("" + progress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        light.setMax(100);

        // 设置监听
        wireLL.setOnClickListener(mOnClickListener);
        wifiLL.setOnClickListener(mOnClickListener);
        hotspotLL.setOnClickListener(mOnClickListener);
        hotspot5G.setOnClickListener(mOnClickListener);
        bluetooth.setOnClickListener(mOnClickListener);
        settingsLL.setOnClickListener(mOnClickListener);

        OPSImage.setOnClickListener(mOnClickListener);
        HomeImage.setOnClickListener(mOnClickListener);
        ShutdownImage.setOnClickListener(mOnClickListener);

        screenshotLL.setOnClickListener(mOnClickListener);
        timerLL.setOnClickListener(mOnClickListener);
        recordLL.setOnClickListener(mOnClickListener);
        eyecareLL.setOnClickListener(mOnClickListener);

        sound.setOnSeekBarChangeListener(new SeekBarListen());
        light.setOnSeekBarChangeListener(new LightListen());

        pupAdd.setOnClickListener(mOnClickListener);
        mAllIgnore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatWindowManager.clearALL(mContext);
            }
        });

        deleteImage.setOnClickListener(mOnClickListener);
        magnifierLL.setOnClickListener(mOnClickListener);
        lightSenseLL.setOnClickListener(mOnClickListener);
        energySaving.setOnClickListener(mOnClickListener);
        updateTime();
        // 开启定时器，每隔2秒刷新一次
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 2000);
        }
        initLightSenseUI();
        initSelectDialog();
        updateUseredIcon();
    }

    /**
     * 初始化光感UI
     */
    private void initLightSenseUI() {
        boolean isClick = false;
        float alpha = 0.4F;
        boolean isLightSenseEnable = MyUtils.isSupportLightSense();
        int resID = R.mipmap.light_sense_default;
        if (isLightSenseEnable) {
            isClick = true;
            alpha = 1F;

            int lightSense = Settings.System.getInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);
            resID = (lightSense == 0) ? R.mipmap.light_sense_default : R.mipmap.light_sense_focus;
        }
        lightSenseImage.setImageResource(resID);
        lightSenseLL.setClickable(isClick);
        lightSenseLL.setAlpha(alpha);
    }


    /**
     * 长按事件
     */
    private View.OnLongClickListener mOnLongClickListener = (view) -> {
        String packageName = (String) SPUtil.getData(getContext(), CommConst.USERED_PACKAGE_NAME, "");
        if (TextUtils.isEmpty(packageName)) {
            return true;
        }
        autoDelayHide();
        if (deleteImage != null) {
            deleteImage.setVisibility(View.VISIBLE);
        }

        return true;
    };

    public List<Map<String, Object>> getData() {
        //cion和iconName的长度是相同的，这里任选其一都可以
        iconName = new int[]{R.string.apps_wire, R.string.apps_wireless, R.string.apps_hotspot,
                R.string.apps_settings, R.string.apps_screenshot, R.string.apps_timer,
                R.string.apps_record, R.string.magnifier, R.string.light_sense,
                R.string.apps_eyecare, R.string.energy_saving, R.string.apps_usred};
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", icon[i]);
            map.put("text", mContext.getString(iconName[i]));
            app_list.add(map);
        }

        return app_list;
    }

    /**
     * 更新图标
     */
    private void updateUseredIcon() {
        String packageName = (String) SPUtil.getData(getContext(), CommConst.USERED_PACKAGE_NAME, "");
        if (!TextUtils.isEmpty(packageName)) {
            Log.d(TAG, "updateUseredIcon start");
            AppInfo appInfo = apkInfoUtils.scanInstallApp(getContext(), packageName);
            if (appInfo != null) {
                Log.d(TAG, "updateUseredIcon change icon");
                addMenu.setImageDrawable(appInfo.getAppIcon());
                tvAdd.setText(appInfo.getAppName());
            } else {
                addMenu.setImageResource(R.drawable.apps_add_normal);
                tvAdd.setText(getContext().getResources().getString(R.string.apps_usred));
            }
        } else {
            addMenu.setImageResource(R.drawable.apps_add_normal);
            tvAdd.setText(getContext().getResources().getString(R.string.apps_usred));
        }
    }

    private void initSelectDialog() {
        dialogView = new SelectAppDialog(this.getContext().getApplicationContext());
        selectDialog = new Dialog(this.getContext().getApplicationContext(), R.style.dialog);
        selectDialog.setContentView(dialogView);
        selectDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        selectDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        selectDialog.setCanceledOnTouchOutside(true);

        // 设置对话框的大小
        Window dialogWindow = selectDialog.getWindow();

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = ScreenUtils.dip2px(this.getContext(), 180); // 宽度
        lp.height = ScreenUtils.dip2px(this.getContext(), 300); // 高度

        dialogWindow.setAttributes(lp);
        dialogView.setCallBack(mCallback);
    }

    SelectAppDialog.Callback mCallback = (appInfo) -> {
        if (selectDialog.isShowing()) {
            updateUseredIcon();
            selectDialog.dismiss();
        }

        if (appInfo != null) {
            SPUtil.saveData(getContext(), CommConst.USERED_PACKAGE_NAME, appInfo.getPackName());
            updateUseredIcon();
        }
    };


    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        getContext().registerReceiver(mWifiReceiver, filter);

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mFilter.addAction("com.ctv.UPDATE_NOTIFICATION");
        mFilter.addAction("android.net.conn.INET_CONDITION_ACTION");
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        // wifi state change
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // pppoe state change
//        mFilter.addAction(PppoeManager.PPPOE_STATE_ACTION);
//        mFilter.addAction(MWifiManager.WIFI_DEVICE_ADDED_ACTION);
//        mFilter.addAction(MWifiManager.WIFI_DEVICE_REMOVED_ACTION);
        // wifi ap
        mFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");

        mContext.registerReceiver(mReceiver, mFilter);

        mContext.registerReceiver(mWifiListReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void removeReceiver() {
        mContext.unregisterReceiver(mReceiver);
        mContext.unregisterReceiver(mWifiListReceiver);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.ctv.UPDATE_NOTIFICATION")) {
                notificationList = FloatWindowManager.getNotificationList();
                notificationAdapter.notifyDataSetChanged();
            } else {
                mListener.onConnectivityChange(intent);
            }

        }
    };

    private final BroadcastReceiver mWifiListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (mWifiListener != null) {
//                mWifiListener.onWifiListChanged();
//            }
        }
    };

    private final BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!isOpenWifi || isDialog) {
                return;
            }
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                Log.d(TAG, "----------------Got supplicant state: " + state.name());
            }
        }
    };

    public void reflashUI() {
        // 有线
        boolean isWireOpen = mEthernetManager.isEnabled();
        wireImage = findViewById(R.id.wire_image);
        if (isWireOpen) {
            wireImage.setImageResource(R.mipmap.apps_wire_focus);
        } else {
            wireImage.setImageResource(R.mipmap.apps_wire_default);
        }
        SPUtil.saveData(getContext(), CommConsts.IS_WIRE_ON, isWireOpen);

        // WiFi
        boolean isWifiOpen = mWifiManager.isWifiEnabled();
        if (isWifiOpen) {
            wifiImage.setImageResource(R.mipmap.apps_wireless_focus);
        } else {
            wifiImage.setImageResource(R.mipmap.apps_wireless_default);
        }
        SPUtil.saveData(getContext(), CommConsts.IS_WIFI_ON, isWifiOpen);
        Log.i(TAG, "isWifiEnabled:" + isWifiOpen);

        // Wifi热点
        setOpenHotspot(false, false);

        MyUtils.checkUSB(getContext().getApplicationContext(),false);
    }

    /**
     * 设置Wifi热点
     *
     * @param isClick
     */
    void setOpenHotspot(boolean isClick, boolean is5G) {
        int apState = (Integer) SPUtil.getData(getContext(), CommConsts.WIFI_STATE, 11);
        Log.i(TAG, "setOpenHotspot-isOpenHotspot:" + isOpenHotspot);
        String wifiapband = SystemProperties.get(HOST_BAND_TYPE);
        if (isOpenHotspot) {
            if (!hasReady(isClick)) {
                Log.d(TAG, "wifiap have not ready.");
                isOpenHotspot = false;
                return;
            }
            if (apState == 12
                    || apState == 13) {
                isOpenHotspot = true;
                if (isOpenHotspot){
                    if(wifiapband.equals(AP_BAND_5G)){
                        hostpotImage.setImageResource(R.mipmap.apps_hotspot_default_2_4);
                        hostpotImage_5G.setImageResource(R.mipmap.apps_hotspot_focus_5);
                    }else if(wifiapband.equals(AP_BAND_2G)){
                        hostpotImage_5G.setImageResource(R.mipmap.apps_hotspot_default_5);
                        hostpotImage.setImageResource(R.mipmap.apps_hotspot_focus__2_4);
                    }else{
                        hostpotImage.setImageResource(R.mipmap.apps_hotspot_default_2_4);
                        hostpotImage_5G.setImageResource(R.mipmap.apps_hotspot_default_5);
                    }
                } else {
                    hostpotImage.setImageResource(R.mipmap.apps_hotspot_default_2_4);
                    hostpotImage_5G.setImageResource(R.mipmap.apps_hotspot_default_5);
//                    hostpotImage.setImageResource(R.mipmap.apps_hotspot_default);
                }

                SPUtil.saveData(getContext(),CommConsts.IS_HOTSPOT_ON, isOpenHotspot);
                return;
            }
        } else {
            if (apState == 10
                    || apState == 11) {
                isOpenHotspot = false;
            }
        }
        if (isClick) {
            setSoftapEnabled(mContext,isOpenHotspot,is5G);
//            if (setWifiApEnabled(isOpenHotspot)) {
            Log.i(TAG, "setOpenHotspot-setWifiApEnabled:" + isOpenHotspot);
//            }
        }

        if (isOpenHotspot){
            if (isOpenHotspot && !"".equals(wifiapband)){
                if(wifiapband.equals(AP_BAND_5G)){
                    hostpotImage.setImageResource(R.mipmap.apps_hotspot_default_2_4);
                    hostpotImage_5G.setImageResource(R.mipmap.apps_hotspot_focus_5);
                }else if(wifiapband.equals(AP_BAND_2G)){
                    hostpotImage_5G.setImageResource(R.mipmap.apps_hotspot_default_5);
                    hostpotImage.setImageResource(R.mipmap.apps_hotspot_focus__2_4);
                }else{
                    hostpotImage.setImageResource(R.mipmap.apps_hotspot_default_2_4);
                    hostpotImage_5G.setImageResource(R.mipmap.apps_hotspot_default_5);
                }
            }
        } else {
            hostpotImage.setImageResource(R.mipmap.apps_hotspot_default_2_4);
            hostpotImage_5G.setImageResource(R.mipmap.apps_hotspot_default_5);
        }

        SPUtil.saveData(getContext(),CommConsts.IS_HOTSPOT_ON, isOpenHotspot);
    }

    /**
     * ap 打开和关闭
     *
     * @param mContext
     * @param enable
     */
    public void setSoftapEnabled(Context mContext, boolean enable,boolean is5G) {
        final ContentResolver cr = mContext.getContentResolver();
        /**
         * Disable Wifi if enabling tethering
         */
        int wifiState = mWifiManager.getWifiState();
        if (enable && ((wifiState == WifiManager.WIFI_STATE_ENABLING) ||
                (wifiState == WifiManager.WIFI_STATE_ENABLED))) {
            mWifiManager.setWifiEnabled(false);
            Settings.Global.putInt(cr, Settings.Global.WIFI_SAVED_STATE, 1);
        }

        startTetheringCallback = new OnStartTetheringCallback();

        if (enable) {
            connectivityManager.stopTethering(ConnectivityManager.TETHERING_WIFI);
            if(is5G){
                SystemProperties.set(HOST_BAND_TYPE, AP_BAND_5G);
            }else{
                SystemProperties.set(HOST_BAND_TYPE,AP_BAND_2G);
            }
            connectivityManager.startTethering(ConnectivityManager.TETHERING_WIFI, true, startTetheringCallback,mHandler);
        } else {
            connectivityManager.stopTethering(ConnectivityManager.TETHERING_WIFI);
        }

        /**
         *  If needed, restore Wifi on tether disable
         */
        if (!enable) {
            int wifiSavedState = 0;
            try {
                wifiSavedState = Settings.Global.getInt(cr, Settings.Global.WIFI_SAVED_STATE);
            } catch (Settings.SettingNotFoundException e) {
            }
            if (wifiSavedState == 1) {
                mWifiManager.setWifiEnabled(true);
                Settings.Global.putInt(cr, Settings.Global.WIFI_SAVED_STATE, 0);
            }
        }
    }

    private boolean hasReady(boolean isClick){
//        if (!MWifiManager.getInstance().isWifiDeviceExist()) {
//            if (isClick){
//                Toast.makeText(getContext(), R.string.please_insert_dongle, Toast.LENGTH_LONG).show();
//            }
//            Log.d(TAG, "setAndCheckWifiData --  wifi_dongle");
//            return false;
//        }
//        if (!MWifiManager.getInstance().isWifiDeviceSupportSoftap()) {
//            if (isClick){
//                Toast.makeText(getContext(), R.string.device_do_not_support, Toast.LENGTH_LONG).show();
//            }
//            Log.d(TAG, "hasReady -- support wifi hotspot?");
//            return false;
//        }

        return true;
    }

    /**
     * 更新时间
     */
    private void updateTime(){
        TimeInfo timeInfo = TimeUtils.getTimeInfo(getContext());
        tvWeek.setText("" + timeInfo.getWeek());
        tvDay.setText("" + timeInfo.getDay());
        String flag = Settings.System.getString(getContext().getContentResolver(),Settings.System.TIME_12_24);
        Log.d(TAG, "updateShowTime:   flag  = "+flag);
        int dateFormatIndex = TimeUtils.getDateFormat();
        Calendar now = Calendar.getInstance();
        date = android.text.format.DateFormat.format(TimeUtils.DATE_FORMAT_STRINGS[dateFormatIndex], now).toString();

        String time = DateFormat.getTimeFormat(getContext()).format(Calendar.getInstance().getTime());
        StringBuilder str_time = new StringBuilder();
        if(flag == null){
            flag = "24";
        }
        Log.d(TAG, "updateShowTime:   flag  = "+flag);
        if(flag.equals("12")) {
            String appm  = "";
            String s_hour="";
            String s_min = "";
            int hours = now.get(Calendar.HOUR);
            if (hours <10 || hours >0){
                s_hour = String.format("%02d", hours);
            }
            int min = now.get(Calendar.MINUTE);
            if (min <10 || min>=0){
                s_min = String.format("%02d", min);
            }
            int apm = now.get(Calendar.AM_PM);
            if (apm == 0) {
                appm = getContext().getResources().getString(R.string.am);
            } else if (apm == 1) {
                appm = getContext().getResources().getString(R.string.pm);
            } else {
                appm = "";
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(appm);
            textviweapm.setVisibility(View.VISIBLE);
            textviweapm.setText(stringBuilder2);
            str_time.append(s_hour);
            str_time.append(":");
            str_time.append(s_min);
            Log.d(TAG, "updateShowTime:appm =  "+appm);
        }else{
            textviweapm.setVisibility(View.GONE);
            str_time.append(time);
        }
        tvTime.setText(str_time.toString());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setRightShow(boolean rightShow) {
        isRightShow = rightShow;
    }

    /**
     * 设置护眼模式
     */
    private void setEyecareMode(){
        // 修改UI
        int eyeCare = Settings.System.getInt(mContext.getContentResolver(), CommConsts.IS_EYECARE, 0);
        final int eyeCareTmp = (eyeCare == 0) ? 1 : 0;

        eyeFlag = (eyeCareTmp == 0) ? R.mipmap.apps_eye_care_default: R.mipmap.apps_eye_care_focus;
        eyecareImage.setImageResource(eyeFlag);

        // 改变状态
        mHandler.postDelayed(()->{
            Settings.System.putInt(getContext().getContentResolver(), CommConsts.IS_EYECARE, eyeCareTmp);
        }, 500);
        Settings.System.putInt(mContext.getContentResolver(), CommConsts.IS_EYECARE, eyeCareTmp);
        Log.d(TAG, "setEyecareMode eyeCare->" + eyeCareTmp);

        if (eyeCareTmp == 1){ // 打开护眼模式
            mHandler.removeMessages(KEY_RESET_BACK_LIGHT);

            // 保存背光值
            int curBacklight = SystemProperties.getInt("persist.sys.backlight",80);//AppUtils.getBacklight();
            if (curBacklight > 50){
                Settings.System.putInt(getContext().getContentResolver(), "lastBlackLight", curBacklight);
                LogUtils.d("护眼模式 降低light setBacklight 50, curBacklight:" + curBacklight);
            }

            if(curBacklight > 50){
                SystemProperties.set("persist.sys.backlight",""+50);
                AppUtils.setBacklight(50);
                light.setProgress(50);
            }
//
//            int lightSense = Settings.System.getInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);
//            if (lightSense == 1){
//                // 切换光感
//                Settings.System.putInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);
//                if (!MyUtils.isSupportLightSense()){
//                    return;
//                }
//
//                lightSense = 0;
//                Settings.System.putInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, lightSense);
//
//                int ID =  R.mipmap.light_sense_default;
//                lightSenseImage.setImageResource(ID);
//                Log.d(TAG, "isLightSense->" + lightSense);
//            }
        } else { // 关闭护眼时，恢复背光值
            mHandler.sendEmptyMessage(KEY_RESET_BACK_LIGHT);
        }
    }

    /**
     * 设置带光控
     */
    public void setEyePlusStatus_Light(int index) {
        switch (index) {
            case 0:
                index = EYE_OFF.ordinal();
                tv_eye.setText(mContext.getString(R.string.apps_eyecare_close));
                eyecareImage.setImageResource(R.mipmap.apps_eye_care_default);
                break;
            case 1:
                index = EYE_DIMMING.ordinal();
                tv_eye.setText(mContext.getString(R.string.apps_eyecare_Electric));
                eyecareImage.setImageResource(R.mipmap.apps_eye_care_focus);
                break;
            case 2:
                index = EYE_PLUS.ordinal();
                tv_eye.setText(mContext.getString(R.string.apps_eyecare_open));
                eyecareImage.setImageResource(R.mipmap.apps_eye_care_focus);
                break;
            case 3:
                index = EYE_RGB.ordinal();
                tv_eye.setText(mContext.getString(R.string.apps_eyecare_light));
                eyecareImage.setImageResource(R.mipmap.apps_eye_care_focus);
                break;
            default:
                break;
        }
        Log.e("eyeMode", "index" + index);
        HHTCommonManager.getInstance().setEyeProtectionMode(index);
    }

    public int getEyePlusIndex_Light() {
        int eyeMode = HHTCommonManager.getInstance().getEyeProtectionMode();
        switch (eyeMode) {
            case 0:
                eyeMode = 0;
                break;
            case 1:
                eyeMode = 1;
                break;
            case 2:
                eyeMode = 3;
                break;
            case 3:
                eyeMode = 2;
                break;
//            case 4:
//                eyeMode = 3;
//                break;
            default:
                break;
        }
        Log.e("eyeMode", "eyeMode" + eyeMode);
        return eyeMode;
    }


    /**
     * 设置护眼+
     */
    public void setEyePlusStatus(int index) {
        switch (index) {
            case 0:
                index = EYE_OFF.ordinal();
                tv_eye.setText(mContext.getString(R.string.apps_eyecare_close));
                eyecareImage.setImageResource(R.mipmap.apps_eye_care_default);
                break;
            case 1:
                index = EYE_PLUS.ordinal();
                tv_eye.setText(mContext.getString(R.string.apps_eyecare_open));
                eyecareImage.setImageResource(R.mipmap.apps_eye_care_focus);
                break;
            case 2:
                index = EYE_RGB.ordinal();
                tv_eye.setText(mContext.getString(R.string.apps_eyecare_light));
                eyecareImage.setImageResource(R.mipmap.apps_eye_care_focus);
                break;
            default:
                break;
        }
        Log.e("eyeMode", "index" + index);
        HHTCommonManager.getInstance().setEyeProtectionMode(index);
    }

    public int getEyePlusIndex() {
        int eyeMode = HHTCommonManager.getInstance().getEyeProtectionMode();
        switch (eyeMode) {
//            case 0:
//                eyeMode = 1;
//                break;
//            case 1:
//                eyeMode = 1;
//                break;
            case 2:
                eyeMode = 2;
                break;
            case 3:
                eyeMode = 1;
                break;
//            case 4:
//                eyeMode = 3;
//                break;
            default:
                break;
        }
        Log.e("eyeMode", "eyeMode" + eyeMode);
        return eyeMode;
    }


    // wifi热点开关

    /**
     * 关闭WiFi AP
     * @param enabled
     * @return
     */
    public boolean setWifiApEnabled(boolean enabled) {
        try {
            //通过反射调用设置热点
            Method method = mWifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);

            //返回热点打开状态
            return (Boolean) method.invoke(mWifiManager, null, enabled);
        } catch (Exception e) {
            return false;
        }
    }

    // wifi热点开关
    public boolean isWifiApEnabled() {
        try {
            //通过反射调用设置热点
            Method method = mWifiManager.getClass().getMethod(
                    "isWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);

            //返回热点打开状态
            return (Boolean) method.invoke(mWifiManager);
        } catch (Exception e) {
            return false;
        }
    }

    // wifi热点开关
    public int getWifiApState() {
        try {
            //通过反射调用设置热点
            Method method = mWifiManager.getClass().getMethod(
                    "getWifiApState", WifiConfiguration.class, Integer.TYPE);

            //返回热点打开状态
            return (Integer) method.invoke(mWifiManager);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 自动延迟收缩
     */
    public void autoDelayHide(){
        if(mHandler !=null){
            mHandler.removeCallbacks(shrinkRunnable);
        }else{
            mHandler = new UIHandler(this);
        }
        mHandler.postDelayed(shrinkRunnable, 10000);
    }

    /**
     * 清除
     */
    public void destroy(){
        MyUtils.checkUSB(getContext().getApplicationContext(),true);
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        mContext.unregisterReceiver(mReceiver);
        mContext.unregisterReceiver(mWifiListReceiver);

    }

    /**
     * 更新声音UI
     */
    public void updateVoiceUI(){
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//获取当前音量
        sound.setProgress(currentVolume);//音量控制Bar的当前值设置为系统音量当前值
        tv_sound.setText(""+currentVolume);
    }

    public void updateVoiceUI(boolean isADD){
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//获取当前音量
        if(isADD && currentVolume != audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)){
            currentVolume++;
        }else if(!isADD && currentVolume!=0){
            currentVolume--;
        }
        sound.setProgress(currentVolume);
        tv_sound.setText(""+currentVolume);
    }

    /**
     * 点击有线
     */
    private void clickWire(){
        boolean isWireOn = (Boolean) SPUtil.getData(getContext(),CommConsts.IS_WIRE_ON, false);
        isWireOn = !isWireOn;
        mEthernetManager.setEnabled(isWireOn);
        isWireOn = mEthernetManager.isEnabled();
        SPUtil.saveData(getContext(),CommConsts.IS_WIRE_ON, isWireOn);
        int resID = R.mipmap.apps_wire_default;
        if (isWireOn){ // 开启有线网络
            resID = R.mipmap.apps_wire_focus;
        }

        wireImage.setImageResource(resID);

//        // 判断WIFI是否打开
//        if (isWireOn){
//            if (MWifiManager.getInstance().isWifiDeviceExist()) {
//                boolean isWifiOn = mWifiManager.isWifiEnabled();
//                if (isWifiOn) {
//                    mWifiManager.setWifiEnabled(false);
//                }
//            }
//        }
    }

    /**
     * 点击WIFI
     */
    private void clickWifi(){
        boolean isWifiOn = (Boolean) SPUtil.getData(getContext(),CommConsts.IS_WIFI_ON, false);
//        if (!MWifiManager.getInstance().isWifiDeviceExist()) {
//            Toast.makeText(getContext(), R.string.please_insert_dongle, Toast.LENGTH_LONG).show();
//            Log.d(TAG, "setAndCheckWifiData --  wifi_dongle");
//            SPUtil.saveData(getContext(),CommConsts.IS_WIFI_ON, false);
//            return;
//        }

        isWifiOn = !isWifiOn;
//        if (isWifiOn){ // 开启WiFi时，关闭WiFi和PPPoE,开启有线


//            if (PPPOE_STA.CONNECTING == mPppoeManager.PppoeGetStatus()) { // 2.Close PPPoE
//                mPppoeManager.PppoeHangUp();
//            }
//
//            if (mEthernetManager.isEnabled()) { // 3.Close Ethernet
//                mEthernetManager.setEnabled(false);
//            }
//        }

        mWifiManager.setWifiEnabled(isWifiOn);
        if (isWifiOn){
            mWifiManager.getScanResults();
        }
        Log.i(TAG, "setWifi-setWifiEnabled:" + isWifiOn);
//                isWifiOn = mWifiManager.isWifiEnabled();
        SPUtil.saveData(getContext(),CommConsts.IS_WIFI_ON, isWifiOn);

        int resID = R.mipmap.apps_wireless_default;
        if (isWifiOn){ // 开启Wifi网络
            resID = R.mipmap.apps_wireless_focus;
        }

        wifiImage.setImageResource(resID);
    }

    /**
     * item事件监听
     */
    private View.OnClickListener mOnClickListener = (view) -> {
//        SettingsDialogLayout.this.setVisibility(View.GONE);


        // 在PC界面时，收起菜单
        // 收缩菜单
        ControlMenuLayout controlMenu = isRightShow ? FloatWindowManager.getMenuWindow() : FloatWindowManager.getMenuWindowLeft();
        if ((controlMenu != null)) {
            controlMenu.shrinkMenu();
        }

        int id = view.getId();
        switch (id){
            case R.id.pup_net: { // 网络
//                    gotoNetUI(CommConsts.WIRE_CONNECT);
                clickWire();
//                HHTSourceManager.getInstance().isCurrentSource("OPS");
                break;
            }
            case R.id.pup_wifi: { // WiFi
                clickWifi();
//                HHTSourceManager.getInstance().isCurrentSource("HDMI1");

                break;
            }
            case R.id.pup_hotspot: { // 热点
                String wifiapband = SystemProperties.get(HOST_BAND_TYPE);
                if(wifiapband.equals(AP_BAND_2G)){
                    isOpenHotspot = false;
                    setOpenHotspot(true,false);
                }else{
                    SystemProperties.set(HOST_BAND_TYPE, AP_BAND_2G);
                    isOpenHotspot = true;
                    setOpenHotspot(true,false);
                }
                Log.e("HHTSourceManager",""+HHTSourceManager.getInstance().isCurrentSource("VGA"));

//                HHTSourceManager.getInstance().isCurrentSource("VGA");
                break;
            }

            case R.id.pup_hotspot_5: { // 热点
//                isOpenHotspot = !isOpenHotspot;
//                setOpenHotspot(true);
                String wifiapband = SystemProperties.get(HOST_BAND_TYPE);
                if(wifiapband.equals(AP_BAND_5G)){
                    isOpenHotspot = false;
                    setOpenHotspot(true,true);
                }else{
                    isOpenHotspot = true;
                    SystemProperties.set(HOST_BAND_TYPE, AP_BAND_5G);
                    setOpenHotspot(true,true);
                }
//                Log.e("HHTSourceManager",""+HHTSourceManager.getInstance().isCurrentSource("VGA"));

                break;
            }

            case R.id.pup_bluetooth: { // 蓝牙
//                isOpenHotspot = !isOpenHotspot;
//                setOpenHotspot(true);
                if(bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.disable();
                }else{
                    bluetoothAdapter.enable();
                }

                break;
            }

            case R.id.pup_settings: { // 设置
                String action = "com.cultraview.settings.CTVSETTINGS";
                AppUtils.gotoOtherApp(getContext(), action);
                //退出设置界面
                if (mContext instanceof Activity) {
                    ((Activity) mContext).finish();
                }
                break;
            }

            case R.id.pup_screenshot: { // 截屏
                /*SettingsDialogLayout.this.setVisibility(View.GONE);
                Log.i(TAG, "screenshot start");
                AppUtils.showScreenshot(getContext().getApplicationContext());
                //退出设置界面
                if (mContext instanceof Activity) {
                    ((Activity) mContext).finish();
                }*/
                mPassWord = getTargetContextPSW();
                Log.d("zhu","mPassWord = " + mPassWord);
                if (mPassWord.equals("")) {
                    Log.d("zhu1","mPassWord = " + mPassWord);
                    Settings.System.putString(mContext.getContentResolver(),"lock-screen","123456");
                }
                if ("on".equals(SystemProperties.get("persist.sys.lockScreen"))) {
                    SystemProperties.set("persist.sys.lockScreen", "off");
                } else {
                    SystemProperties.set("persist.sys.lockScreen", "on");
                    Intent intent = new Intent("com.ctv.lockscreen.action.LockScreen");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    //退出设置界面
                    if (mContext instanceof Activity) {
                        ((Activity) mContext).finish();
                    }
                }
                break;
            }
            case R.id.pup_timer: { // 计时器
                SettingsDialogLayout.this.setVisibility(View.GONE);
                String mPackageName = "com.dazzle.timer";
                String mActivityName = "com.dazzle.timer.TimerActivity";
                AppUtils.gotoOtherApp(getContext(), mPackageName, mActivityName);
                //退出设置界面
                if (mContext instanceof Activity) {
                    ((Activity) mContext).finish();
                }
                break;
            }
            case R.id.pup_record: { // 录像
                SettingsDialogLayout.this.setVisibility(View.GONE);
                String mPackageName = "com.dazzlewisdom.screenrec";
                String mActivityName = "com.dazzlewisdom.screenrec.ScreenRecActivity";
                AppUtils.gotoOtherApp(getContext(), mPackageName, mActivityName);
                //退出设置界面
                if (mContext instanceof Activity) {
                    ((Activity) mContext).finish();
                }
                break;
            }
            case R.id.pup_eyecare: { // 护眼
                //setEyecareMode();
                if(IS_AH_EDU_QD){
                ++eyeFlag;
                    if (eyeFlag == 4) eyeFlag = 0;
                    setEyePlusStatus_Light(eyeFlag);
                    break;
                }else {
                    ++eyeFlag;
                if (eyeFlag == 3) eyeFlag = 0;
                setEyePlusStatus(eyeFlag);
                break;
                }
            }
            case R.id.pup_add: {//自定义
                setUserAPPShow();
                break;
            }
            case R.id.btn_delete: { // 删除添加的快捷应用
                autoDelayHide();

                SPUtil.saveData(getContext(), CommConst.USERED_PACKAGE_NAME, "");
                deleteImage.setVisibility(View.GONE);

                updateUseredIcon();
                break; // 不隐藏UI，还可以再操作
            }
            case R.id.pup_light_sense: {
                // 允许背光进度条滑动
                mHandler.post(() ->{
                    if (SettingNewActivity.mHandler != null){
                        Message msg = SettingNewActivity.mHandler.obtainMessage(SettingNewActivity.MSG_UPDATE_LIGHT,
                                true);
                        SettingNewActivity.mHandler.sendMessage(msg); // 更新亮度进度条
                    }
                });
                // 切换光感
                changeLightSense();

                break;
            }
            case R.id.pup_energy_saving:{//节能
                CtvPictureManager.getInstance().disableBacklight();
                Settings.System.putInt(mContext.getContentResolver(), "isSeperateHear", 1);
                break;
            }
            case R.id.pup_magnifier:{ //放大镜
                SettingsDialogLayout.this.setVisibility(View.GONE);
                String mPackageName = "com.example.newmagnifier";
                String mActivityName = "com.example.newmagnifier.MainActivity";
                AppUtils.gotoOtherApp(getContext(), mPackageName, mActivityName);
                //退出设置界面
                if (mContext instanceof Activity) {
                    ((Activity) mContext).finish();
                }
                break;
            }
            case R.id.btn_ops: {//OPS
//                AppUtils.changeSignal(mContext, 26);
                HHTOpsManager hhtOpsManager = HHTOpsManager.getInstance();
                if(!hhtOpsManager.isOpsOk()){
                    hhtOpsManager.setOpsPowerTurnOn();
                }
                HHTSourceManager.getInstance().startSourcebyKey("OPS");
                //退出设置界面
                if (mContext instanceof Activity) {
                    ((Activity) mContext).finish();
                }
                break;
            }
            case R.id.btn_android: { //主页
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_HOME);
                //退出设置界面
                if (mContext instanceof Activity) {
                    ((Activity) mContext).finish();
                }
                break;
            }
            case R.id.btn_shutdown: { //关机
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_POWER);
                //退出设置界面
                if (mContext instanceof Activity) {
                    ((Activity) mContext).finish();
                }
                break;
            }

        }

    };
    private Context getTargetContext() {
        try {
            return mContext.createPackageContext("com.cultraview.settings", Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
    private String getTargetContextPSW() {
        Context context = getTargetContext();
        String psw = null;
        if(context == null){
            psw = Settings.System.getString(mContext.getContentResolver(),"lock-screen");

            Log.d("zhu","psw = " +psw);
            return  psw == null ? "":psw;
        }else{
            return getTargetContext().getSharedPreferences("lock-screen", Context.MODE_PRIVATE).getString("lock-screen","");
        }
    }
    /**
     * 改变光感
     */
    private void changeLightSense() {
        // 切换光感
        int lightSense = Settings.System.getInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);
        boolean isOpen = (lightSense == 0);
        setLightSenseMode(isOpen);

        // 自动光感开启时,关闭护眼模式
        lightSense = Settings.System.getInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);
        if (lightSense == 1) { // 自动光感时,关闭护眼模式
            int eyeCare = Settings.System.getInt(getContext().getContentResolver(), CommConsts.IS_EYECARE, 0);
            if (eyeCare == 1) { // 当在护眼时， 关闭护眼
                Settings.System.putInt(getContext().getContentResolver(), CommConsts.IS_EYECARE, 0);
                //setEyecareMode();
                setEyePlusStatus(0);
                // 恢复背光值
                resetBlackLight();
            }
        }
    }

    /**
     * 设置自动感光
     */
    private void setLightSenseMode(boolean isOpen){
        if (!MyUtils.isSupportLightSense()){
            return;
        }

        int lightSense = isOpen ? 1 : 0;
        Settings.System.putInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, lightSense);

        int resID = isOpen ? R.mipmap.light_sense_focus : R.mipmap.light_sense_default;
        lightSenseImage.setImageResource(resID);
        Log.d(TAG, "isLightSense->" + lightSense);

        // 更新光感定时任务
        updateLightSense();
    }

    /**
     * 更新光感定时任务
     */
    private void updateLightSense(){
        LogUtils.d("更新光感定时任务.....");
        Intent tempIntent = new Intent(getContext(), FloatWindowService.class);
        tempIntent.setAction("com.ctv.FloatWindowService.LIGHT_SENSE_ACTION");
        getContext().startService(tempIntent);
    }


    /**
     * 设置自定义应用
     */
    private void setUserAPPShow(){
        String packageName = (String)SPUtil.getData(getContext(), CommConst.USERED_PACKAGE_NAME, "");
        // 若是没有选择APP，则弹出选择对话框;反之，则启动APP
        if (TextUtils.isEmpty(packageName)){
            selectDialog.show();
        } else {
            // 启动APP
            apkInfoUtils.startApp(getContext(), packageName);
        }
    }
    Runnable shrinkRunnable = ()-> {
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
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            autoDelayHide();

//            int progress = seekBar.getProgress();
//            LogUtils.d("声音设置值：" + progress);

            MyApplication.IsTouchSeting = true;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            tv_sound.setText(""+progress);
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
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            autoDelayHide();

//            int progress = seekBar.getProgress();
//            LogUtils.d("亮度设置值：" + progress);

//            int progress = SystemProperties.getInt("persist.sys.backlight",80);

            light.setProgress(progress);
            try {
                Settings.System.putInt(mContext.getContentResolver(),"backlight",progress);
                SystemProperties.set("persist.sys.backlight",""+progress);
                mTvPictureManager.setBacklight(progress);
                tv_light.setText(""+progress);
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

    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            // 当前没有浮框，直接创建浮框
            SettingsDialogLayout settingsDialog = FloatWindowManager.getSettingsDialog();
            if (settingsDialog!= null &&  settingsDialog.getVisibility() == View.VISIBLE){
                mHandler.post(() -> {
                    updateTime();
                    SystemClock.sleep(500);
                });
            }
            if (bluetoothAdapter.isEnabled()){
                bluetoothImage.setImageResource(R.mipmap.apps_bluetooth_focus);
            } else {
                bluetoothImage.setImageResource(R.mipmap.apps_bluetooth_default);
            }
        }
    }

    /**
     * 恢复背光值
     */
    private void resetBlackLight(){
        mHandler.removeMessages(KEY_RESET_BACK_LIGHT);

        // 恢复背光值
        int lastBlackLight = Settings.System.getInt(mContext.getContentResolver(),
                "lastBlackLight", 50);
        if (lastBlackLight > 50){
            SystemProperties.set("persist.sys.backlight",""+lastBlackLight);
            AppUtils.setBacklight(lastBlackLight);
            // 更新背光
            updateBlackLightSeekbar();
            LogUtils.d("护眼式 恢复light setBacklight 模lastBlackLight:" + lastBlackLight);
        }
    }

    private void updateBlackLightSeekbar(){
        light.setProgress(SystemProperties.getInt("persist.sys.backlight",80));
    }

    public static final int KEY_CHANGE_LIGHT_SENSE = 1; // 切换光感
    public static final int KEY_CHANGE_EYE_CARE = 2; // 切换护眼
    public static final int KEY_RESET_BACK_LIGHT = 4; // 恢复背光
    public static final int MSG_UPDATE_LIGHT = 5; // 更新背光进度条值
    public static final int MSG_UPDATE_BLUETOOTH = 6; // 更新背光进度条值

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<SettingsDialogLayout> weakReference;

        public UIHandler(SettingsDialogLayout dialogLayout) {
            super();
            this.weakReference = new WeakReference<>(dialogLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SettingsDialogLayout dialogLayout = weakReference.get();
            if (dialogLayout == null) {
                return;
            }
            switch (msg.what){
                case KEY_RESET_BACK_LIGHT:{ // 恢复背光值
                    dialogLayout.resetBlackLight();
                    break;
                }
                case MSG_UPDATE_LIGHT:{ // 更新背光进度条值
                    dialogLayout.updateBlackLightSeekbar();
                    break;
                }
//                case MSG_UPDATE_BLUETOOTH:{ // 更新背光进度条值
//                    if (dialogLayout.bluetoothAdapter.isEnabled()){
//                        dialogLayout.bluetoothImage.setImageResource(R.mipmap.apps_bluetooth_focus);
//                    } else {
//                        dialogLayout.bluetoothImage.setImageResource(R.mipmap.apps_bluetooth_default);
//                    }
//                    mHandler.sendEmptyMessageDelayed(SettingsDialogLayout.MSG_UPDATE_BLUETOOTH,1000);
//                    break;
//                }
                default:
                    break;
            }
        }
    }

    public interface Listener {
        void onConnectivityChange(Intent intent);

//        void onPPPoeChanged(String status);
//
//        void onEthernetAvailabilityChanged(boolean isAvailable);
    }

    private static final class OnStartTetheringCallback extends
            ConnectivityManager.OnStartTetheringCallback {

        OnStartTetheringCallback() {
        }

        @Override
        public void onTetheringStarted() {
        }

        @Override
        public void onTetheringFailed() {
        }

    }

}
