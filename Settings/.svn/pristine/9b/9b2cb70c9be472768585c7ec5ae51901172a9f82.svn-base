package com.ctv.settings.greneral;

import android.app.Activity;
import android.view.View;

import com.ctv.settings.base.BaseViewHolder;

public class GreneralViewHolder extends BaseViewHolder implements View.OnClickListener{
    private View item_hint_sound;
    private View item_easy_touch;
    private View item_lock_screen;
    private View item_usb_lock;
    private View item_four_screen;
    private View triple_bond_one;
    private View five_touch_mode;
    private View item_hint_sound_iv;
    private View item_easy_touch_iv;
    private View item_four_screen_iv;
    private View triple_bond_one_iv;
    private View five_touch_mode_iv;

    public GreneralViewHolder(Activity activity) {
        super(activity);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.item_hint_sound){
            GreneralUtils.getInstance(mActivity).
                    setHintSoundEnable(GreneralUtils.getInstance(mActivity).getHintSoundStatus()?false:true);
            refreshUI();
        }else if(view.getId() == R.id.item_lock_screen){
            GreneralUtils.getInstance(mActivity).openLockScreenFunction();
        }else if(view.getId() == R.id.item_usb_lock){
            GreneralUtils.getInstance(mActivity).openUsbLockFunction();
        }else if(view.getId() == R.id.item_easy_touch){
            GreneralUtils.getInstance(mActivity).
                    setEasyTouchEnable(GreneralUtils.getInstance(mActivity).getEasyTouchStatus()?false:true);
            refreshUI();
        }else if(view.getId() == R.id.item_four_screen){
            GreneralUtils.getInstance(mActivity).
                    setFourScreenEnable(GreneralUtils.getInstance(mActivity).getFourScreenStatus()?false:true);
            refreshUI();
        }else if(view.getId() == R.id.triple_bond_one){
            GreneralUtils.getInstance(mActivity).
                    setTripleBondOneEnable(GreneralUtils.getInstance(mActivity).getTripleBondOneStatus()?false:true);
            refreshUI();
        }else if(view.getId() == R.id.five_touch_mode){
            GreneralUtils.getInstance(mActivity).
                    setFiveTouchModeEnable(GreneralUtils.getInstance(mActivity).getFiveTouchModeStatus()?false:true);
            refreshUI();
        }
    }

    @Override
    public void initUI(Activity activity) {
        item_hint_sound = activity.findViewById(R.id.item_hint_sound);
        item_lock_screen = activity.findViewById(R.id.item_lock_screen);
        item_usb_lock = activity.findViewById(R.id.item_usb_lock);
        item_easy_touch = activity.findViewById(R.id.item_easy_touch);
        item_four_screen = activity.findViewById(R.id.item_four_screen);
        triple_bond_one = activity.findViewById(R.id.triple_bond_one);
        five_touch_mode = activity.findViewById(R.id.five_touch_mode);

        item_hint_sound_iv = activity.findViewById(R.id.item_hint_sound_iv);
        item_easy_touch_iv = activity.findViewById(R.id.item_easy_touch_iv);
        item_four_screen_iv = activity.findViewById(R.id.item_four_screen_iv);
        triple_bond_one_iv = activity.findViewById(R.id.triple_bond_one_iv);
        five_touch_mode_iv = activity.findViewById(R.id.five_touch_mode_iv);
        refreshUI();
    }

    @Override
    public void initListener() {
        item_hint_sound.setOnClickListener(this);
        item_lock_screen.setOnClickListener(this);
        item_usb_lock.setOnClickListener(this);
        item_easy_touch.setOnClickListener(this);
        item_four_screen.setOnClickListener(this);
        triple_bond_one.setOnClickListener(this);
        five_touch_mode.setOnClickListener(this);
    }

    @Override
    public void refreshUI(View view) {

    }

    public void refreshUI(){
        item_hint_sound_iv.setBackgroundResource(
                GreneralUtils.getInstance(mActivity).getHintSoundStatus()?R.mipmap.on:R.mipmap.off);
        item_easy_touch_iv.setBackgroundResource(
                GreneralUtils.getInstance(mActivity).getEasyTouchStatus()?R.mipmap.on:R.mipmap.off);
        item_four_screen_iv.setBackgroundResource(
                GreneralUtils.getInstance(mActivity).getFourScreenStatus()?R.mipmap.on:R.mipmap.off);
        triple_bond_one_iv.setBackgroundResource(
                GreneralUtils.getInstance(mActivity).getTripleBondOneStatus()?R.mipmap.on:R.mipmap.off);
        five_touch_mode_iv.setBackgroundResource(
                GreneralUtils.getInstance(mActivity).getFiveTouchModeStatus()?R.mipmap.on:R.mipmap.off);
    }
}
