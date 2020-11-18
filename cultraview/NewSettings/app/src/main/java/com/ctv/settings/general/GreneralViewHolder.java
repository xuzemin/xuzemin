package com.ctv.settings.general;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.utils.DataTool;
import com.cultraview.tv.CtvPictureManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;

import java.util.concurrent.ExecutorService;

import static com.ctv.settings.utils.CommonConsts.REQUEST_EYE_PLUS_OPTIONS;
import static com.ctv.settings.utils.CommonConsts.REQUEST_HDMI_OUT_OPTIONS;
import static com.ctv.settings.utils.CommonConsts.REQUEST_SHARE_USB_OPTIONS;

public class GreneralViewHolder extends BaseViewHolder implements View.OnClickListener {
    private Handler mhandler;
    private View item_eye_plus;
    private View item_hint_sound;
    private View item_easy_touch;
    private View item_lock_screen;
    private View item_usb_lock;
    private View item_four_screen;
    private View triple_bond_one;
    private View five_touch_mode;
    private View public_usb;
    private View alone_backlight;
    private View item_hint_sound_iv;
    private View item_easy_touch_iv;
    private View item_four_screen_iv;
    private View triple_bond_one_iv;
    //    private View item_eye_plus_iv;
    private TextView tv_eye_plus_iv;
    private View five_touch_mode_iv;
    private TextView five_touch_mode_tv;
    private TextView tv_share_usb;
    private View alone_backlight_iv;
    private View headphone_insertion_test;
    private View headphone_insertion_test_iv;
    public ExecutorService singleThreadExecutor;
    private TextView greneral_item_fourscreen_tv;
    private String[] eye_plus;

    public static int eyePlusStatus = 0;
    private RelativeLayout item_hdmiout;
    private TextView tv_hdmiout;
    private String hdmiOutResolute = "AUTO";
    private final static boolean hdmiout_enable = TextUtils.equals(SystemProperties.get("persist.sys.hdmiout_enable", "on"), "off");
    public GreneralViewHolder(Activity activity) {
        super(activity);
    }

    public GreneralViewHolder(Activity activity, Handler handler, ExecutorService singleThreadExecutor) {
        super(activity);
        this.mhandler = handler;
        this.singleThreadExecutor = singleThreadExecutor;
        initData(activity);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.item_hint_sound) {
            GreneralUtils.getInstance(mActivity).
                    setHintSoundEnable(GreneralUtils.getInstance(mActivity).getHintSoundStatus() ? false : true);
            refreshUI();
        } else if (view.getId() == R.id.item_eye_plus) { // Eye Plus
            mActivity.startActivityForResult(new Intent(mActivity, EyePlusStatusActivity.class), REQUEST_EYE_PLUS_OPTIONS);
        } else if (view.getId() == R.id.item_lock_screen) {
            GreneralUtils.getInstance(mActivity).openLockScreenFunction();
        } else if (view.getId() == R.id.item_usb_lock) {
            GreneralUtils.getInstance(mActivity).openUsbLockFunction();
        } else if (view.getId() == R.id.item_easy_touch) {
            // TODO: 2019-11-07 点击问题
            int enable = Settings.System.getInt(mActivity.getContentResolver(), "EASY_TOUCH_OPEN_ENABLE",0);
            boolean status = GreneralUtils.getInstance(mActivity).getEasyTouchStatus();
            if(status){
//                if(enable == 1) {
                GreneralUtils.getInstance(mActivity).setEasyTouchEnable(false);
//                }else{
//                    Toast.makeText(mActivity,mActivity.getString(R.string.proxy_easytouch_opening),Toast.LENGTH_SHORT).show();
//                }
            }else{
                if(enable == 0) {
                    GreneralUtils.getInstance(mActivity).setEasyTouchEnable(true);
                }else{
                    Toast.makeText(mActivity,mActivity.getString(R.string.proxy_easytouch_closing),Toast.LENGTH_SHORT).show();
                }
            }
            refreshUI();
        } else if (view.getId() == R.id.item_four_screen) {
            GreneralUtils.getInstance(mActivity).
                    setFourScreenEnable(GreneralUtils.getInstance(mActivity).getFourScreenStatus() ? false : true);
            refreshUI();
        } else if (view.getId() == R.id.triple_bond_one) {
            GreneralUtils.getInstance(mActivity).
                    setTripleBondOneEnable(GreneralUtils.getInstance(mActivity).getTripleBondOneStatus() ? false : true);
            refreshUI();
        } else if (view.getId() == R.id.five_touch_mode) {
            GreneralUtils.getInstance(mActivity).
                    setFiveTouchModeEnable(GreneralUtils.getInstance(mActivity).getFiveTouchModeStatus() ? false : true);
            refreshUI();
        } else if (view.getId() == R.id.public_usb) {
            mActivity.startActivityForResult(new Intent(mActivity, ShareUsbActivity.class), REQUEST_SHARE_USB_OPTIONS);
        } else if (view.getId() == R.id.rl_hdmiout) {
            mActivity.startActivityForResult(new Intent(mActivity, HdmiOutActivity.class), REQUEST_HDMI_OUT_OPTIONS);
        } else if (view.getId() == R.id.alone_backlight) {
            CtvPictureManager.getInstance().disableBacklight();
            Settings.System.putInt(mActivity.getContentResolver(), "isSeperateHear", 1);
            refreshUI();
        } else if (view.getId() == R.id.headphone_insertion_test) {
            Boolean isCheckEarPhone = GreneralUtils.getInstance(mActivity).getCheckEarphoneStatus();
            if (isCheckEarPhone) {
                SystemProperties.set("persist.sys.earphone_check", "0");
            } else {
                SystemProperties.set("persist.sys.earphone_check", "1");
            }
            refreshUI();
        }
    }

    private final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime;

    public boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    @Override
    public void initUI(Activity activity) {
        item_eye_plus = activity.findViewById(R.id.item_eye_plus);
        tv_eye_plus_iv = activity.findViewById(R.id.tv_eye_plus_iv);
        item_hint_sound = activity.findViewById(R.id.item_hint_sound);
        item_lock_screen = activity.findViewById(R.id.item_lock_screen);
        item_usb_lock = activity.findViewById(R.id.item_usb_lock);
        item_easy_touch = activity.findViewById(R.id.item_easy_touch);
        item_four_screen = activity.findViewById(R.id.item_four_screen);
        triple_bond_one = activity.findViewById(R.id.triple_bond_one);
        five_touch_mode = activity.findViewById(R.id.five_touch_mode);
        five_touch_mode_tv = (TextView) activity.findViewById(R.id.five_touch_mode_tv);
        public_usb = activity.findViewById(R.id.public_usb);
        alone_backlight = activity.findViewById(R.id.alone_backlight);
        headphone_insertion_test = activity.findViewById(R.id.headphone_insertion_test);

        headphone_insertion_test_iv = activity.findViewById(R.id.headphone_insertion_test_iv);
        tv_share_usb = (TextView) activity.findViewById(R.id.tv_share_usb);


//        item_eye_plus_iv = activity.findViewById(R.id.item_eye_plus_iv);
        alone_backlight_iv = activity.findViewById(R.id.alone_backlight_iv);
        item_hint_sound_iv = activity.findViewById(R.id.item_hint_sound_iv);
        item_easy_touch_iv = activity.findViewById(R.id.item_easy_touch_iv);
        item_four_screen_iv = activity.findViewById(R.id.item_four_screen_iv);
        triple_bond_one_iv = activity.findViewById(R.id.triple_bond_one_iv);
        five_touch_mode_iv = activity.findViewById(R.id.five_touch_mode_iv);

        greneral_item_fourscreen_tv = (TextView) activity.findViewById(R.id.greneral_item_fourscreen_tv);

        item_hdmiout = activity.findViewById(R.id.rl_hdmiout);
        if(hdmiout_enable){
            item_hdmiout.setVisibility(RelativeLayout.GONE);
        }else{
            item_hdmiout.setVisibility(RelativeLayout.VISIBLE);
        }
        tv_hdmiout = activity.findViewById(R.id.tv_hdmiout);

//        refreshUI();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REQUEST_SHARE_USB_OPTIONS || resultCode == REQUEST_EYE_PLUS_OPTIONS) {
            refreshFourScreenItemEnable();
//            eyePlusStatus = GreneralUtils.getInstance(mActivity).getEyePlusIndex();
            refreshUI();
        } else if (resultCode == REQUEST_HDMI_OUT_OPTIONS) {
            try {
                hdmiOutResolute = CtvTvManager.getInstance().getEnvironment("HdmiOutResolute");
            } catch (CtvCommonException e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(hdmiOutResolute)) {
                tv_hdmiout.setText(hdmiOutResolute);
            } else {
                tv_hdmiout.setText("AUTO");
            }
        }
    }

    @Override
    public void initData(Activity activity) {
        super.initData(activity);
        refreshFourScreenItemEnable();
        if(DataTool.IS_AH_EDU_QD){
            eyePlusStatus = GreneralUtils.getInstance(mActivity).getEyePlusIndex_Light();
        }else{
            eyePlusStatus = GreneralUtils.getInstance(mActivity).getEyePlusIndex();
        }
        try {
            hdmiOutResolute = CtvTvManager.getInstance().getEnvironment("HdmiOutResolute");
        } catch (CtvCommonException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(hdmiOutResolute)) {
            tv_hdmiout.setText(hdmiOutResolute);
        } else {
            tv_hdmiout.setText("AUTO");
        }
        refreshUI();
    }

    public void refreshFourScreenItemEnable() {
        if (greneral_item_fourscreen_tv == null) {
            return;
        }
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
        int imgIndex = sharedPreferences.getInt("bgIndex", 0);
        int styleIndex = sharedPreferences.getInt("styleIndex", 0);
        if (styleIndex == 1) {
            item_four_screen.setClickable(true);
            greneral_item_fourscreen_tv.setTextColor(mActivity.getResources().getColor(R.color.white));
        } else {
            item_four_screen.setClickable(false);
            greneral_item_fourscreen_tv.setTextColor(mActivity.getResources().getColor(R.color.half_white));
        }
    }

    @Override
    public void initListener() {
        item_eye_plus.setOnClickListener(this);
        item_hint_sound.setOnClickListener(this);
        item_lock_screen.setOnClickListener(this);
        item_usb_lock.setOnClickListener(this);
        item_easy_touch.setOnClickListener(this);
        item_four_screen.setOnClickListener(this);
        triple_bond_one.setOnClickListener(this);
        five_touch_mode.setOnClickListener(this);
        public_usb.setOnClickListener(this);
        alone_backlight.setOnClickListener(this);
        headphone_insertion_test.setOnClickListener(this);
        item_hdmiout.setOnClickListener(this);
    }

    @Override
    public void refreshUI(View view) {

    }

    public void refreshUI() {
        if (mhandler == null) {
            return;
        }
        singleThreadExecutor.execute(() -> {
            boolean hintSoundStatus = GreneralUtils.getInstance(mActivity).getHintSoundStatus();
            boolean easyTouchStatus = GreneralUtils.getInstance(mActivity).getEasyTouchStatus();
            boolean fourScreenStatus = GreneralUtils.getInstance(mActivity).getFourScreenStatus();
            boolean tripleBondOneStatus = GreneralUtils.getInstance(mActivity).getTripleBondOneStatus();
            boolean fiveTouchModeStatus = GreneralUtils.getInstance(mActivity).getFiveTouchModeStatus();
            String isShareUsb = GreneralUtils.getInstance(mActivity).getUsbShareStatus();
//            boolean aloneBackLight = GreneralUtils.getInstance(mActivity).getBackLightStatus();
            String[] share = mActivity.getResources().getStringArray(R.array.share_usb_mode);
            if(DataTool.IS_AH_EDU_QD){
                eye_plus = mActivity.getResources().getStringArray(R.array.eye_plus_mode_Light);
            }else{
                eye_plus = mActivity.getResources().getStringArray(R.array.eye_plus_mode);
            }
            Boolean isCheckEarPhone = GreneralUtils.getInstance(mActivity).getCheckEarphoneStatus();
//            eyePlusStatus = GreneralUtils.getInstance(mActivity).getEyePlusIndex();
            mhandler.post(() -> {
                tv_eye_plus_iv.setText(eye_plus[eyePlusStatus]);
                item_hint_sound_iv.setBackgroundResource(hintSoundStatus ? R.mipmap.on : R.mipmap.off);
                item_easy_touch_iv.setBackgroundResource(easyTouchStatus ? R.mipmap.on : R.mipmap.off);
                item_four_screen_iv.setBackgroundResource(fourScreenStatus ? R.mipmap.on : R.mipmap.off);
                triple_bond_one_iv.setBackgroundResource(tripleBondOneStatus ? R.mipmap.on : R.mipmap.off);
                five_touch_mode_iv.setBackgroundResource(fiveTouchModeStatus ? R.mipmap.on : R.mipmap.off);
                if(DataTool.IS_AH_EDU_QD){
                    five_touch_mode_tv.setText(mActivity.getResources().getString(R.string.item_five_touch_qd));
                }
//                        public_usb_iv.setBackgroundResource(isShareUsb ? R.mipmap.on : R.mipmap.off);
                tv_share_usb.setText(share[Integer.parseInt(isShareUsb)]);
                headphone_insertion_test_iv.setBackgroundResource(isCheckEarPhone ? R.mipmap.on : R.mipmap.off);
                alone_backlight_iv.setBackgroundResource(R.mipmap.off);
            });
        });
    }

}
