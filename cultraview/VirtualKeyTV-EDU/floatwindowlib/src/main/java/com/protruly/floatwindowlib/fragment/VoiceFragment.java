package com.protruly.floatwindowlib.fragment;

import android.app.Service;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
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
import com.mstar.android.tv.TvCecManager;
import com.protruly.floatwindowlib.MyApplication;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.activity.SettingActivity;
import com.protruly.floatwindowlib.activity.SettingsNewActivity;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.entity.SignalInfo;
import com.protruly.floatwindowlib.helper.LightDB;
import com.protruly.floatwindowlib.ui.ControlMenuLayout;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Carson_Ho on 16/7/22.
 */
public class VoiceFragment extends Fragment {

    View usersetLL;
    SeekBar bass;
    SeekBar treble;
    SeekBar balance;

	TextView bassTitle;
	TextView trebleTitle;
	TextView balanceTitle;
	TextView bassNum;
	TextView trebleNum;
	TextView balanceNum;

    private int curPosition = 0;
	private CommonAdapter<String> commonAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.voice_layout, container, false);

        initView(view);
        setListen();
	    initData();
        return view;
    }

	@Override
	public void onResume() {
		super.onResume();
		setSeekBarClickable(curPosition == 1);
	}

    private void initView(View view){
        usersetLL = view.findViewById(R.id.ll_user_set);
        bass = (SeekBar) view.findViewById(R.id.bass_seekBar);
        treble = (SeekBar) view.findViewById(R.id.treble_seekBar);
        balance = (SeekBar) view.findViewById(R.id.balance_seekBar);

	    bassTitle = (TextView) view.findViewById(R.id.bass_title);
	    trebleTitle = (TextView) view.findViewById(R.id.treble_title);
	    balanceTitle = (TextView) view.findViewById(R.id.balance_title);
	    bassNum = (TextView) view.findViewById(R.id.bass_num);
	    trebleNum = (TextView) view.findViewById(R.id.treble_num);
	    balanceNum = (TextView) view.findViewById(R.id.balance_num);

        String[] strs = getActivity().getResources().getStringArray(R.array.voice_mode_list);
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

	/**
	 * 初始化数据
	 */
	private void initData(){
	    int mode = CtvAudioManager.getInstance().getAudioSoundMode();
	    switch (mode){
		    case CtvAudioManager.SOUND_MODE_STANDARD:
			    curPosition = 0;
			    break;
		    case CtvAudioManager.SOUND_MODE_USER:
			    curPosition = 1;
			    break;
		    case CtvAudioManager.SOUND_MODE_MUSIC:
			    curPosition = 2;
			    break;
		    case CtvAudioManager.SOUND_MODE_MOVIE:
			    curPosition = 3;
			    break;
		    default:
			    break;
	    }
    }

    private void setListen(){
        bass.setOnSeekBarChangeListener(new BassListen());
        treble.setOnSeekBarChangeListener(new TrebleListen());
        balance.setOnSeekBarChangeListener(new BalanceListen());
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
		bassTitle.setTextColor(getResources().getColorStateList(resColor));
		trebleTitle.setTextColor(getResources().getColorStateList(resColor));
		balanceTitle.setTextColor(getResources().getColorStateList(resColor));

		int bNum = CtvAudioManager.getInstance().getBass();
		bassNum.setText(bNum + "");
		int tNum = CtvAudioManager.getInstance().getTreble();
		trebleNum.setText(tNum + "");
		int baNum = CtvAudioManager.getInstance().getBalance();
		balanceNum.setText(baNum + "");

		bassNum.setTextColor(getResources().getColorStateList(resColor));
		trebleNum.setTextColor(getResources().getColorStateList(resColor));
		balanceNum.setTextColor(getResources().getColorStateList(resColor));

		setProgress(bass, bNum, isClick);
		setProgress(treble, tNum, isClick);
		setProgress(balance, baNum, isClick);
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
     * 低音监听类
     */
    class BassListen implements SeekBar.OnSeekBarChangeListener {

    	private int start;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
//            LogUtils.d("低音设置值：" + progress);
	        bassNum.setText("" + progress);

	        if (Math.abs(progress - start) > 5){
		        start = progress;

		        if (SettingsNewActivity.dataHandler != null){
			        SettingsNewActivity.dataHandler.postDelayed(()->{
				        CtvAudioManager.getInstance().setBass(seekBar.getProgress()); // 低音
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
	        CtvAudioManager.getInstance().setBass(seekBar.getProgress()); // 低音
        }
    }

    /**
     * 高音监听类
     */
    class TrebleListen implements SeekBar.OnSeekBarChangeListener {
		private int start;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
//            LogUtils.d("高音设置值：" + progress);
	        trebleNum.setText("" + progress);
	        if (Math.abs(progress - start) > 5){
		        start = progress;

		        if (SettingsNewActivity.dataHandler != null){
			        SettingsNewActivity.dataHandler.postDelayed(()->{
				        CtvAudioManager.getInstance().setTreble(seekBar.getProgress()); // 高音
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
	        CtvAudioManager.getInstance().setTreble(seekBar.getProgress()); // 高音
        }
    }

    /**
     * 平衡监听类
     */
    class BalanceListen implements SeekBar.OnSeekBarChangeListener {
		private int start;
        @Override
        public void onProgressChanged(SeekBar seekBar, final int progress, boolean b) {
//            LogUtils.d("平衡设置值：" + progress);
	        balanceNum.setText("" + progress);
	        if (Math.abs(progress - start) > 5){
		        start = progress;
		        if (SettingsNewActivity.dataHandler != null){
			        SettingsNewActivity.dataHandler.postDelayed(()->{
				        CtvAudioManager.getInstance().setBalance(progress); // 平衡
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
	        CtvAudioManager.getInstance().setBalance(seekBar.getProgress()); // 平衡
        }
    }

	/**
	 * item事件监听
	 */
	private AdapterView.OnItemClickListener mOnItemClickListener = (parent, view, position, id) -> {
		if (curPosition == position){
			return;
		}

		int soundMode = CtvAudioManager.SOUND_MODE_STANDARD;
		switch (position) {
			case 0:
				soundMode = CtvAudioManager.SOUND_MODE_STANDARD;
				break;
			case 1:
				soundMode = CtvAudioManager.SOUND_MODE_USER;
				break;
			case 2:
				soundMode = CtvAudioManager.SOUND_MODE_MUSIC;
				break;
			case 3:
				soundMode = CtvAudioManager.SOUND_MODE_MOVIE;
				break;
			default:
				break;
		}

		curPosition = position;
		commonAdapter.notifyDataSetChanged();

		CtvAudioManager.getInstance().setAudioSoundMode(soundMode);
		setSeekBarClickable(position == 1);
	};

}
