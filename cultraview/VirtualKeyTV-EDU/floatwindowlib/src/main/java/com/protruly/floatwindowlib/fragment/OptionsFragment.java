package com.protruly.floatwindowlib.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvDatabaseManager;
import com.cultraview.tv.CtvPictureManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.cultraview.tv.common.vo.CtvEnumMfcMode;
import com.mstar.android.tv.TvCommonManager;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.activity.AutoAdjustView;
import com.protruly.floatwindowlib.adapters.PageItemAdapter;
import com.protruly.floatwindowlib.utils.SystemUtils;

import org.w3c.dom.Text;

public class OptionsFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private final String TAG = OptionsFragment.class.getSimpleName();
    private TextView text_scale_mode, text_noise_reduction_mode, text_vga_mode;
    private TextView text_sign_step;
    private ListView listView;
    private String[] img_noise_string;
    private int select = 0;
    private PageItemAdapter adapter;
    private int flag = 0;
    private LinearLayout ll_vga;
    private Button btn_pic_pc_image;
    private SeekBar seekbar_clock;
    private SeekBar seekbar_phase;
    private SeekBar seekbar_horizontal;
    private SeekBar seekbar_vertical;
    private int iClock, iPhase, iHorivontal, iVertical;
    private TextView txt_clock_val, txt_phase_val, txt_horizontal_val, txt_vertical_val;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_option, container, false);
        initView(view);
        initDatas();
        return view;
    }

    @SuppressLint("ResourceAsColor")
    private void initDatas() {
        hideView();
        listView.setVisibility(View.VISIBLE);
        text_sign_step.setTextColor(getResources().getColor(R.color.white));
        int currentInputSource = CtvCommonManager.getInstance().getCurrentTvInputSource();
        if ((currentInputSource == CtvCommonManager.INPUT_SOURCE_VGA)
                && CtvCommonManager.getInstance().isSignalStable(currentInputSource)) {
            text_vga_mode.setVisibility(View.VISIBLE);
        } else {
            text_vga_mode.setVisibility(View.INVISIBLE);
        }


        // initData
        iClock = CtvPictureManager.getInstance().getPCClock();
        iPhase = CtvPictureManager.getInstance().getPCPhase();
        iHorivontal = CtvPictureManager.getInstance().getPCHPos();
        iVertical = CtvPictureManager.getInstance().getPCVPos();
        seekbar_clock.setProgress(iClock);
        txt_clock_val.setText(iClock + "");
        seekbar_phase.setProgress(iPhase);
        txt_phase_val.setText(iPhase + "");
        seekbar_horizontal.setProgress(iHorivontal);
        txt_horizontal_val.setText(iHorivontal + "");
        seekbar_vertical.setProgress(iVertical);
        txt_vertical_val.setText(iVertical + "");

        seekbar_clock.setOnSeekBarChangeListener(this);
        seekbar_phase.setOnSeekBarChangeListener(this);
        seekbar_horizontal.setOnSeekBarChangeListener(this);
        seekbar_vertical.setOnSeekBarChangeListener(this);


    }


    private void initView(View view) {

        text_sign_step = (TextView) view.findViewById(R.id.text_sign_step);
        text_scale_mode = (TextView) view.findViewById(R.id.text_scale_mode);
        text_noise_reduction_mode = (TextView) view.findViewById(R.id.text_noise_reduction_mode);
        text_vga_mode = (TextView) view.findViewById(R.id.text_vga_mode);

        ll_vga = (LinearLayout) view.findViewById(R.id.ll_vga);
        listView = (ListView) view.findViewById(R.id.lv_all_list);

        seekbar_clock = (SeekBar) view.findViewById(R.id.seek_clock);
        seekbar_phase = (SeekBar) view.findViewById(R.id.seek_phase);
        seekbar_horizontal = (SeekBar) view.findViewById(R.id.seek_horizontal);
        seekbar_vertical = (SeekBar) view.findViewById(R.id.seek_vertical);

        txt_clock_val = (TextView) view.findViewById(R.id.txt_clock);
        txt_phase_val = (TextView) view.findViewById(R.id.txt_phase_val);
        txt_horizontal_val = (TextView) view.findViewById(R.id.txt_horizontal_val);
        txt_vertical_val = (TextView) view.findViewById(R.id.txt_vertical_val);

        //vga
        btn_pic_pc_image = (Button) view.findViewById(R.id.btn_pic_pc_image);
        text_scale_mode.setOnClickListener(this);
        text_noise_reduction_mode.setOnClickListener(this);
        text_vga_mode.setOnClickListener(this);
        text_sign_step.setOnClickListener(this);
        btn_pic_pc_image.setOnClickListener(this);

        select = SystemUtils.getSignalSwitchMode(getActivity());
        img_noise_string = getResources().getStringArray(
                R.array.str_arr_sign_step);
        adapter = new PageItemAdapter(getActivity(), select, img_noise_string);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (flag == 0) {
                    SystemUtils.updateSignalSwitchMode(getActivity(), position);
                } else if (flag == 1) {
                    SystemUtils.setZoomMode(position);
                } else if (flag == 2) {
                    SystemUtils.setImgNoiseMode(position);
                }
                adapter.setSelectImage(position);
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_sign_step:
                hideView();
                listView.setVisibility(View.VISIBLE);
                text_sign_step.setTextColor(getResources().getColor(R.color.white));
                img_noise_string = getResources().getStringArray(
                        R.array.str_arr_sign_step);
                select = SystemUtils.getSignalSwitchMode(getActivity());
                flag = 0;
                updateList(img_noise_string, select);
                break;
            case R.id.text_scale_mode:
                hideView();
                listView.setVisibility(View.VISIBLE);
                img_noise_string = getResources().getStringArray(
                        R.array.str_arr_scale);
                text_scale_mode.setTextColor(getResources().getColor(R.color.white));
                select = SystemUtils.getZoomMode();
                flag = 1;
                updateList(img_noise_string, select);

                break;
            case R.id.text_noise_reduction_mode:

                hideView();
                flag = 2;
                img_noise_string = getResources().getStringArray(
                        R.array.str_arr_img_mpeg_noisereduction_vals);
                listView.setVisibility(View.VISIBLE);
                text_noise_reduction_mode.setTextColor(getResources().getColor(R.color.white));
                select = SystemUtils.getImgNoiseMode();
                updateList(img_noise_string, select);
                break;
            case R.id.text_vga_mode:
                hideView();
                ll_vga.setVisibility(View.VISIBLE);
                text_vga_mode.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.btn_pic_pc_image:
                if (CtvCommonManager.getInstance().isSignalStable(
                        CtvCommonManager.getInstance().getCurrentTvInputSource())) {
                    Intent intent = new Intent();
                    intent.setAction("com.cultraview.virtualkey.intent.action.AutoAdjustView");
                    getActivity().startActivity(intent);
                }
                FragmentActivity activity = getActivity();
                activity.finish();
                break;
        }
    }

    private void updateList(String[] img_noise_string, int select) {
        adapter.setSelectImage(select);
        adapter.setData(img_noise_string);
    }

    /**
     *
     */
    private void hideView() {
        ll_vga.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);


        text_sign_step.setTextColor(getResources().getColor(R.color.com_bg_gray));
        text_scale_mode.setTextColor(getResources().getColor(R.color.com_bg_gray));
        text_noise_reduction_mode.setTextColor(getResources().getColor(R.color.com_bg_gray));
        text_vga_mode.setTextColor(getResources().getColor(R.color.com_bg_gray));
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int pro, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seek_clock:

                txt_clock_val.setText("" + pro);
                CtvPictureManager.getInstance().setPCClock(pro);
                seekbar_clock.setProgress(pro);
                break;


            case R.id.seek_phase:

                txt_phase_val.setText("" + pro);
                CtvPictureManager.getInstance().setPCPhase(pro);
                seekbar_phase.setProgress(pro);
                break;

            case R.id.seek_horizontal:
                txt_horizontal_val.setText("" + pro);
                CtvPictureManager.getInstance().setPCHPos(pro);
                seekbar_horizontal.setProgress(pro);
                break;

            case R.id.seek_vertical:
                txt_vertical_val.setText("" + pro);
                CtvPictureManager.getInstance().setPCVPos(pro);
                seekbar_vertical.setProgress(pro);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
