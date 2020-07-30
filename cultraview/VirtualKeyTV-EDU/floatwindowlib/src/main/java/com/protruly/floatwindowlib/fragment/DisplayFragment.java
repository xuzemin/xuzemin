package com.protruly.floatwindowlib.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.cultraview.tv.CtvAudioManager;
import com.cultraview.tv.CtvPictureManager;
import com.protruly.floatwindowlib.MyApplication;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.activity.SettingsNewActivity;
import com.protruly.floatwindowlib.helper.LightDB;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Carson_Ho on 16/7/22.
 */
public class DisplayFragment extends Fragment {

    SeekBar brightness; // 亮度
    SeekBar contrast; // 对比度
    SeekBar hue; // 色调
    SeekBar sharpness; // 锐度


    TextView brightnessTitle; // 亮度
    TextView contrastTitle; // 对比度
    TextView hueTitle; // 色调
    TextView sharpnessTitle; // 锐度
    TextView brightnessNum; // 亮度
    TextView contrastNum; // 对比度
    TextView hueNum; // 色调
    TextView sharpnessNum; // 锐度

    private int curPosition = 0;
    private CommonAdapter<String> commonAdapter;

    View usersetLL;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.display_layout, container, false);
        initView(view);
        initData();
        setListen();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setSeekBarClickable(curPosition == 1);
    }

    private void initView(View view){
        usersetLL = view.findViewById(R.id.ll_user_set);
        brightness = (SeekBar)view.findViewById(R.id.brightness_seekBar);
        contrast = (SeekBar)view.findViewById(R.id.contrast_seekBar);
        hue = (SeekBar)view.findViewById(R.id.hue_seekBar);
        sharpness = (SeekBar)view.findViewById(R.id.sharpness_seekBar);

        brightnessTitle = (TextView) view.findViewById(R.id.brightness_title);
        contrastTitle = (TextView)view.findViewById(R.id.contrast_title);
        hueTitle = (TextView)view.findViewById(R.id.hue_title);
        sharpnessTitle = (TextView)view.findViewById(R.id.sharpness_title);
        brightnessNum = (TextView) view.findViewById(R.id.brightness_num);
        contrastNum = (TextView)view.findViewById(R.id.contrast_num);
        hueNum = (TextView)view.findViewById(R.id.hue_num);
        sharpnessNum = (TextView)view.findViewById(R.id.sharpness_num);

        String[] strs = getActivity().getResources().getStringArray(R.array.display_mode_list);
        List<String> dataList = Arrays.asList(strs);

        // 初始化
        int gridMenuID = R.layout.voice_grid_item;
        commonAdapter = new CommonAdapter<String>(getContext(),
                gridMenuID, dataList) {
            @Override
            protected void convert(ViewHolder holder, String mode, int position) {
                holder.setText(R.id.voice_text, mode + "");

                int color = R.color.black_light_sub;
                if (position == curPosition){
                    color = R.color.white;
                }
                holder.setTextColorRes(R.id.voice_text, color);
            }
        };

        // 初始化
        GridView gridView = (GridView) view.findViewById(R.id.grid_view);
        gridView.setAdapter(commonAdapter);
        gridView.setOnItemClickListener(mOnItemClickListener);
    }

    private void initData(){
        int mode = CtvPictureManager.getInstance().getPictureMode();
        switch (mode){
            case CtvPictureManager.PICTURE_MODE_NORMAL:
                curPosition = 0;
                break;
            case CtvPictureManager.PICTURE_MODE_USER:
                curPosition = 1;
                break;
            case CtvPictureManager.PICTURE_MODE_SOFT:
                curPosition = 2;
                break;
            case CtvPictureManager.PICTURE_MODE_VIVID:
                curPosition = 3;
                break;
            default:
                break;
        }
    }

    private void setListen(){
        brightness.setOnSeekBarChangeListener(new BrightnessChangeListener());
        contrast.setOnSeekBarChangeListener(new ContrastChangeListener());
        hue.setOnSeekBarChangeListener(new HueChangeListener());
        sharpness.setOnSeekBarChangeListener(new SharpnessChangeListener());
    }

    /**
     * 设置进度条
     * @param isClick
     */
    private void setSeekBarClickable(boolean isClick){
        int resColor = R.color.com_bg_gray;
        if (isClick){ // 可点击时
            resColor = R.color.white;
        }
        brightnessTitle.setTextColor(getResources().getColorStateList(resColor));
        contrastTitle.setTextColor(getResources().getColorStateList(resColor));
        hueTitle.setTextColor(getResources().getColorStateList(resColor));
        sharpnessTitle.setTextColor(getResources().getColorStateList(resColor));

        int bNum = CtvPictureManager.getInstance().getBrightness();
        brightnessNum.setText(bNum + "");
        int cNum = CtvPictureManager.getInstance().getContrast();
        contrastNum.setText(cNum + "");
        int hNum = CtvPictureManager.getInstance().getHue();
        hueNum.setText(hNum + "");
        int sNum = CtvPictureManager.getInstance().getSharpness();
        sharpnessNum.setText(sNum + "");

        brightnessNum.setTextColor(getResources().getColorStateList(resColor));
        contrastNum.setTextColor(getResources().getColorStateList(resColor));
        hueNum.setTextColor(getResources().getColorStateList(resColor));
        sharpnessNum.setTextColor(getResources().getColorStateList(resColor));

        setProgress(brightness, bNum, isClick);
        setProgress(contrast, cNum, isClick);
        setProgress(hue, hNum, isClick);
        setProgress(sharpness, sNum, isClick);
    }

    /**
     * 设置进度条
     * @param bar
     * @param progress
     * @param isClick
     */
    private void setProgress(SeekBar bar, int progress, boolean isClick){
        bar.setClickable(isClick);
        bar.setEnabled(isClick);
        bar.setSelected(isClick);
        bar.setFocusable(isClick);
        bar.setProgress(progress);
    }

    /**
     * 亮度监听类
     */
    class BrightnessChangeListener implements SeekBar.OnSeekBarChangeListener {
        private int start;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            brightnessNum.setText("" + progress);

            if (Math.abs(progress - start) > 5){
                start = progress;
                if (SettingsNewActivity.dataHandler != null){
                    SettingsNewActivity.dataHandler.postDelayed(()->{
                        CtvPictureManager.getInstance().setBrightness(seekBar.getProgress()); // 调节亮度
                    }, 50);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            start = seekBar.getProgress();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            CtvPictureManager.getInstance().setBrightness(seekBar.getProgress()); // 调节亮度
        }
    }

    /**
     * 对比度
     */
    class ContrastChangeListener implements SeekBar.OnSeekBarChangeListener {
        private int start;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            contrastNum.setText("" + progress);
            if (Math.abs(progress - start) > 5){
                start = progress;
                if (SettingsNewActivity.dataHandler != null){
                    SettingsNewActivity.dataHandler.postDelayed(()->{
                        CtvPictureManager.getInstance().setContrast(seekBar.getProgress());
                    }, 50);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            start = seekBar.getProgress();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            CtvPictureManager.getInstance().setContrast(seekBar.getProgress());
        }
    }

    /**
     * 色调
     */
    class HueChangeListener implements SeekBar.OnSeekBarChangeListener {

        private int start;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            hueNum.setText("" + progress);

            if (Math.abs(progress - start) > 5){
                start = progress;

                if (SettingsNewActivity.dataHandler != null){
                    SettingsNewActivity.dataHandler.postDelayed(()->{
                        CtvPictureManager.getInstance().setHue(progress);
                    }, 50);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            start = seekBar.getProgress();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            CtvPictureManager.getInstance().setHue(seekBar.getProgress());
        }
    }

    /**
     * 锐度
     */
    class SharpnessChangeListener implements SeekBar.OnSeekBarChangeListener {
        private int start;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            sharpnessNum.setText("" + progress);

            if (Math.abs(progress - start) > 5){
                start = progress;

                if (SettingsNewActivity.dataHandler != null){
                    SettingsNewActivity.dataHandler.postDelayed(()->{
                        CtvPictureManager.getInstance().setSharpness(seekBar.getProgress());
                    }, 50);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            start = seekBar.getProgress();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            CtvPictureManager.getInstance().setSharpness(seekBar.getProgress());
        }
    }

    /**
     * item事件监听
     */
    private AdapterView.OnItemClickListener mOnItemClickListener = (parent, view, position, id) -> {
        if (curPosition == position){
            return;
        }

        int picMode = CtvPictureManager.PICTURE_MODE_NORMAL;
        switch (position) {
            case 0:
                picMode = CtvPictureManager.PICTURE_MODE_NORMAL;
                break;
            case 1:
                picMode = CtvPictureManager.PICTURE_MODE_USER;
                break;
            case 2:
                picMode = CtvPictureManager.PICTURE_MODE_SOFT;  // 柔和
                break;
            case 3:
                picMode = CtvPictureManager.PICTURE_MODE_VIVID; // 艳丽
                break;
            /*case 5:
                picMode = CtvPictureManager.PICTURE_MODE_NATURAL;
                break;
            case 6:
                picMode = CtvPictureManager.PICTURE_MODE_SPORTS;
                break;*/
            default:
                break;
        }
        CtvPictureManager.getInstance().setPictureMode(picMode);

        setSeekBarClickable(position == 1);
        curPosition = position;
        commonAdapter.notifyDataSetChanged();

//        if (SettingsNewActivity.mHandler != null){
//            SettingsNewActivity.mHandler.sendEmptyMessage(4);
//        }
    };

}
