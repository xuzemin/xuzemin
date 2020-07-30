package com.ctv.settings.device.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.device.DeviceManager;
import com.ctv.settings.device.DeviceNameActivity;
import com.ctv.settings.device.ThemeBgActvity;
import com.ctv.settings.device.ThemeStyleActvity;
import com.ctv.settings.device.data.Constant;
import com.ctv.settings.device.listener.DeviceChangeListener;
import com.ctv.settings.utils.L;
import com.cultraview.tv.utils.CtvCommonUtils;


/**
 * 设置ViewHolder
 *
 * @author wanghang
 * @date 2019/09/17
 */
public class DeviceViewHolder extends BaseViewHolder implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
        , DeviceChangeListener {
    private final static String TAG = DeviceViewHolder.class.getCanonicalName();

    private View mainSecurity; // 安全模块根界面
    private View itemAppPermissions; // 安全模块根界面
    private View itemUnknownStore; // 安全模块根界面
    private View itemCameraPermissions; // 安全模块根界面
    private View itemUdiskPermissions; // 安全模块根界面

    private ImageView imAppPermissions; // app权限
    private ImageView imUnknownStore; // 位置来源开关
    private ImageView imUdiskPermissions; // U盘权限
    private ImageView imCameraPermissions; // 摄像头权限
    private SeekBar pup_seekbar, pup_volume_seekbar;
    private RelativeLayout itemTheme, itemThemeStyle, itemDeviceName;
    private TextView deviceNameTv, themeBgTV, themeSTyleTV;
    private LinearLayout llSettingLight;
    private TextView lightTv,volumeNumTV;


    public DeviceViewHolder(Activity activity) {
        super(activity);
    }

    /**
     * 初始化UI
     *
     * @param activity
     */
    @Override
    public void initUI(Activity activity) {
        pup_seekbar = (SeekBar) activity.findViewById(R.id.pup_seekbar1);//亮度
        pup_volume_seekbar = (SeekBar)activity.findViewById(R.id.pup_seekbar_volume);//声音
        itemTheme = (RelativeLayout) activity.findViewById(R.id.rl_setting_theme); //主题背景
        itemThemeStyle = (RelativeLayout) activity.findViewById(R.id.rl_setting_theme_sytle); //主题风格
        itemDeviceName = (RelativeLayout)activity.findViewById(R.id.rl_setting_device_name);
        deviceNameTv = (TextView)activity.findViewById(R.id.tv_device_name);
        themeBgTV = (TextView) activity.findViewById(R.id.tv_theme_bg);
        themeSTyleTV = (TextView) activity.findViewById(R.id.tv_theme_style);
        llSettingLight = (LinearLayout)activity.findViewById(R.id.rl_setting_light);
        lightTv = (TextView) activity.findViewById(R.id.light_num);
        volumeNumTV = (TextView) activity.findViewById(R.id.volume_num);


    }



    /**
     * 初始化数据
     *
     * @param activity
     */
    @Override
    public void initData(Activity activity) {
        Log.d(TAG, "initData");
        pup_seekbar.setMax(100);
        pup_seekbar.setProgress(DeviceManager.getScreenBrightness(activity));// 亮度
        lightTv.setText(DeviceManager.getScreenBrightness(activity)+""); //设置默认亮度

        pup_volume_seekbar.setMax(DeviceManager.getMaxVolume(activity));
        pup_volume_seekbar.setProgress(DeviceManager.getVolume(activity));
        volumeNumTV.setText(DeviceManager.getVolume(activity)+""); //设置默认声音

        //设备名称
        String deviceName = CtvCommonUtils.getCultraviewProjectInfo(mActivity, "tbl_SoftwareVersion", "DeviceName");
        deviceNameTv.setText(deviceName);

        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
        int imgIndex = sharedPreferences.getInt("bgIndex", 0);
        int styleIndex = sharedPreferences.getInt("styleIndex", 0);
        themeBgTV.setText(mActivity.getResources().getStringArray(R.array.themeBg)[imgIndex]);
        themeSTyleTV.setText(mActivity.getResources().getStringArray(R.array.themeStyle)[styleIndex]);

//<item>渐变</item>
//        <item>气泡 </item>
//        <item>金属</item>
//        <item>夜幕</item>
//        <item>简洁</item>
//        <item>星空</item>


    }


    /**
     * 初始化监听
     */
    @Override
    public void initListener() {
        Log.d(TAG, "initListener");
        pup_seekbar.setOnSeekBarChangeListener(this);
        pup_volume_seekbar.setOnSeekBarChangeListener(this);
        itemTheme.setOnClickListener(this);
        itemThemeStyle.setOnClickListener(this);
        itemDeviceName.setOnClickListener(this);
    }

    /**
     * 刷新指定view
     *
     * @param view
     */
    @Override
    public void refreshUI(View view) {
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        L.d(TAG, "mOnClickListener view.id->%s", id);
        if (id == R.id.rl_setting_theme) {
            L.d("qkmin-----点击主题设置");
            startActivityForResultUtils(ThemeBgActvity.class, Constant.theme_bg_request_code);
        } else if (id == R.id.rl_setting_theme_sytle) {
            startActivityForResultUtils(ThemeStyleActvity.class, Constant.theme_style_request_code);
        } else if (id == R.id.rl_setting_device_name) {
            startActivityForResultUtils(DeviceNameActivity.class, Constant.device_name_request_code);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (seekBar == pup_seekbar) { //亮度
            L.d("qkmin-设置亮度:" + progress);
            pup_seekbar.setProgress(progress);
            DeviceManager.setBrightness(mActivity, progress);
            lightTv.setText(progress+"");
        } else if (seekBar == pup_volume_seekbar) { //声音
            DeviceManager.setVolume(mActivity, progress);
            volumeNumTV.setText(progress+"");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void startActivityUtils(Class aClass) {
        Intent intent = new Intent(mActivity, aClass);
        mActivity.startActivity(intent);
    }

    public void startActivityForResultUtils(Class aClass, int code) {
        Intent intent = new Intent(mActivity, aClass);
        mActivity.startActivityForResult(intent, code);
    }

    /**
     * 主题风格
     * 设备名称修改
     *
     * @param deviceName
     */
    @Override
    public void deviceNameChange(String deviceName) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceNameTv.setText(deviceName);
            }
        });
    }

    //主题背景

    public void themeBgChange(String name) {
        themeBgTV.setText(name);
    }

    //主题风格
    public void themeStyleChange(String name) {
        themeSTyleTV.setText(name);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "device onActivityResult" + requestCode + "resultCode" + resultCode);
        if (requestCode == Constant.theme_style_request_code) {
            if (resultCode == Constant.theme_style_request_code) {
                if (data!=null){
                    String name = data.getStringExtra(Constant.themeSytleData);
                    themeStyleChange(name);
                }

            }
        } else if (requestCode == Constant.theme_bg_request_code) {
            if (resultCode == Constant.theme_bg_request_code) {
                if (data!=null){
                    String name = data.getStringExtra(Constant.themeBgData);
                    themeBgChange(name);
                }
            }
        } else if (requestCode == Constant.device_name_request_code) {
            if (data!=null){
                String name = data.getStringExtra(Constant.deviceName);
                deviceNameChange(name);
            }

        }
    }

    public void onResume(Activity activity) {
        initData(activity);
    }
}
