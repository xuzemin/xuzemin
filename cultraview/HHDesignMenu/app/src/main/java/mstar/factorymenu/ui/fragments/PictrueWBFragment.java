package mstar.factorymenu.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvFactoryManager;
import com.cultraview.tv.CtvPictureManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.cultraview.tv.common.vo.CtvEnumColorTemperature;
import com.cultraview.tv.common.vo.CtvEnumInputSource;
import com.cultraview.tv.factory.vo.CtvWbGainOffset;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictrueWBFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictrueWBFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener, SeekBar.OnSeekBarChangeListener {

    private final String TAG = "PictrueWBFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //WB
    private int sourceindexWB = 0;
    private int currentSourceindex = 0;
    private int clortempindex = 0;
    private int rgainvalWB = 1024;
    private int ggainvalWB = 1024;
    private int bgainvalWB = 1024;
    //private final int gainDisplayDivideWB = 8;// display:256=2048/8,step=8
    private final int gainStepWB = 8;// display:256=2048/8,step=8
    private int roffsetvalWB = 1024;
    private int goffsetvalWB = 1024;
    private int boffsetvalWB = 1024;
    private final int offsetDisplayDivideWB = 1;// Bar_step=1
    private final int offsetStepWB = 1;// Reg_step=1
    private static final int R_Gain = 1;
    private static final int G_Gain = 2;
    private static final int B_Gain = 3;
    private static final int R_Offset = 4;
    private static final int G_Offset = 5;
    private static final int B_Offset = 6;
    private int swith_id = 1;
    private int[] sourceType;
    private static final int ChangeColorTempMsg = 100;

    // FIXME: move to resource file
    int rgainvalWBSharp = 2048;
    int ggainvalWBSharp = 2048;
    int bgainvalWBSharp = 2048;
    int roffsetvalWBSharp = 2048;
    int goffsetvalWBSharp = 2048;
    int boffsetvalWBSharp = 2048;
    private static final int CHANGE_COLOR_TEMPMSG = 100;
    private static final int TSET_PATTERN_CLOSE = 200;
    private static final int TSET_PATTERN_OPEN = 300;
    private static int mTestPatternType = 0;
    private CtvEnumColorTemperature colorTempEnum;
    private CtvWbGainOffset ctvWbGainOffset;
    private String[] patternArrayWB;
    private String[] colortemparray = {
            "Cool", "Nature", "Warm", "User"
    };
    private Button btn_picture_wb_normal, btn_picture_wb_cool, btn_picture_wb_warm,btn_picture_wb_eye;
    private SeekBar seekBar_picture_wb_rgain, seekBar_picture_wb_ggain, seekBar_picture_wb_bgain, seekBar_picture_wb_roffset, seekBar_picture_wb_goffset, seekBar_picture_wb_boffset;
    private TextView textView_picture_wb_roffset, textView_picture_wb_goffset, textView_picture_wb_rgain,
            textView_picture_wb_bgain, textView_picture_wb_ggain, textView_picture_wb_boffset;


    private IFactoryDesk factoryManager;

    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == ChangeColorTempMsg) {
                if (factoryManager != null) {
                    factoryManager.setColorTmpIdx(IFactoryDesk.EN_MS_COLOR_TEMP.values()[clortempindex]);
                }
                freshOffer();
            }
        }

        ;
    };
    private LinearLayout ll_picture_wb_rgain;
    private LinearLayout ll_picture_wb_ggain;
    private LinearLayout ll_picture_wb_bgain;
    private LinearLayout ll_picture_wb_roffset;
    private LinearLayout ll_picture_wb_goffset;
    private LinearLayout ll_picture_wb_boffset;

    public PictrueWBFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PictrueModeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PictrueWBFragment newInstance(String param1, String param2) {
        PictrueWBFragment fragment = new PictrueWBFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_wb, container, false);

        initViews(view);

        factoryManager = FactoryDeskImpl.getInstance(getActivity());


        initWBData();
        return view;
    }

    private void initViews(View view) {
        ll_picture_wb_rgain = view.findViewById(R.id.ll_picture_wb_rgain);
        ll_picture_wb_ggain = view.findViewById(R.id.ll_picture_wb_ggain);
        ll_picture_wb_bgain = view.findViewById(R.id.ll_picture_wb_bgain);
        ll_picture_wb_roffset = view.findViewById(R.id.ll_picture_wb_roffset);
        ll_picture_wb_goffset = view.findViewById(R.id.ll_picture_wb_goffset);
        ll_picture_wb_boffset = view.findViewById(R.id.ll_picture_wb_boffset);


        //WB
        btn_picture_wb_normal = view.findViewById(R.id.picture_wb_normal);
        btn_picture_wb_warm = view.findViewById(R.id.picture_wb_warm);
        btn_picture_wb_cool = view.findViewById(R.id.picture_wb_cool);
        btn_picture_wb_eye = view.findViewById(R.id.picture_wb_eye);



        seekBar_picture_wb_rgain = view.findViewById(R.id.picture_wb_rgain);
        seekBar_picture_wb_ggain = view.findViewById(R.id.picture_wb_ggain);
        seekBar_picture_wb_bgain = view.findViewById(R.id.picture_wb_bgain);
        seekBar_picture_wb_roffset = view.findViewById(R.id.picture_wb_roffset);
        seekBar_picture_wb_goffset = view.findViewById(R.id.picture_wb_goffset);
        seekBar_picture_wb_boffset = view.findViewById(R.id.picture_wb_boffset);
        textView_picture_wb_rgain = view.findViewById(R.id.picture_wb_rgain_text);
        textView_picture_wb_ggain = view.findViewById(R.id.picture_wb_ggain_text);
        textView_picture_wb_bgain = view.findViewById(R.id.picture_wb_bgain_text);
        textView_picture_wb_roffset = view.findViewById(R.id.picture_wb_roffset_text);
        textView_picture_wb_goffset = view.findViewById(R.id.picture_wb_goffset_text);
        textView_picture_wb_boffset = view.findViewById(R.id.picture_wb_boffset_text);

        btn_picture_wb_normal.setNextFocusLeftId(R.id.list_sub_title);

        btn_picture_wb_normal.setOnClickListener(this);
        btn_picture_wb_warm.setOnClickListener(this);
        btn_picture_wb_cool.setOnClickListener(this);
        btn_picture_wb_eye.setOnClickListener(this);

        btn_picture_wb_normal.setOnFocusChangeListener(this);
        btn_picture_wb_warm.setOnFocusChangeListener(this);
        btn_picture_wb_cool.setOnFocusChangeListener(this);
        btn_picture_wb_eye.setOnFocusChangeListener(this);

        seekBar_picture_wb_rgain.setOnFocusChangeListener(this);
        seekBar_picture_wb_ggain.setOnFocusChangeListener(this);
        seekBar_picture_wb_bgain.setOnFocusChangeListener(this);
        seekBar_picture_wb_roffset.setOnFocusChangeListener(this);
        seekBar_picture_wb_goffset.setOnFocusChangeListener(this);
        seekBar_picture_wb_boffset.setOnFocusChangeListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (this != null && !hidden) {
            LogUtils.d("onHiddenChanged");
            clearWBBtnState();
            if (clortempindex == 0) {
                btn_picture_wb_cool.setBackgroundResource(R.color.colorFocus);
            } else if (clortempindex == 1) {
                btn_picture_wb_normal.setBackgroundResource(R.color.colorFocus);
            } else if (clortempindex == 2) {
                btn_picture_wb_warm.setBackgroundResource(R.color.colorFocus);
            } else {
                btn_picture_wb_eye.setBackgroundResource(R.color.colorFocus);
            }
        }
        initWBData();
    }

    /**
     * 初始化WB数据
     */
    private void initWBData() {
        ctvWbGainOffset = new CtvWbGainOffset();
        sourceType = getSupportSourcelist();
        int curInputSource = CtvCommonManager.getInstance().getCurrentTvInputSource();
        Log.d(TAG, "curInputSource = " + curInputSource);
        factoryManager.setWBIdx(CtvEnumInputSource.values()[curInputSource]);
        sourceindexWB = factoryManager.getWBIdx().ordinal();
        Log.d(TAG, "sourceindexWB = " + sourceindexWB);
        for (int i = 0; i < sourceType.length; i++) {
            if (sourceType[i] == sourceindexWB) {
                currentSourceindex = i;
            }
        }
        setWbData();

        // "Cool", "Nature", "Warm", "User"
        clearWBBtnState();
        if (clortempindex == 0) {
            btn_picture_wb_cool.setBackgroundResource(R.color.colorFocus);
        } else if (clortempindex == 1) {
            btn_picture_wb_normal.setBackgroundResource(R.color.colorFocus);
        } else if (clortempindex == 2) {
            btn_picture_wb_warm.setBackgroundResource(R.color.colorFocus);
        } else {
            btn_picture_wb_eye.setBackgroundResource(R.color.colorFocus);
        }
        seekBar_picture_wb_rgain.setProgress(rgainvalWBSharp);
        textView_picture_wb_rgain.setText(rgainvalWBSharp + "");
        seekBar_picture_wb_ggain.setProgress(ggainvalWBSharp);
        textView_picture_wb_ggain.setText(ggainvalWBSharp + "");
        seekBar_picture_wb_bgain.setProgress(bgainvalWBSharp);
        textView_picture_wb_bgain.setText(bgainvalWBSharp + "");
        seekBar_picture_wb_roffset.setProgress(roffsetvalWBSharp);
        textView_picture_wb_roffset.setText(roffsetvalWBSharp + "");
        seekBar_picture_wb_goffset.setProgress(goffsetvalWBSharp);
        textView_picture_wb_goffset.setText(goffsetvalWBSharp + "");
        seekBar_picture_wb_boffset.setProgress(boffsetvalWBSharp);
        textView_picture_wb_boffset.setText(boffsetvalWBSharp + "");

        seekBar_picture_wb_rgain.setOnSeekBarChangeListener(this);
        seekBar_picture_wb_ggain.setOnSeekBarChangeListener(this);
        seekBar_picture_wb_bgain.setOnSeekBarChangeListener(this);
        seekBar_picture_wb_roffset.setOnSeekBarChangeListener(this);
        seekBar_picture_wb_goffset.setOnSeekBarChangeListener(this);
        seekBar_picture_wb_boffset.setOnSeekBarChangeListener(this);
    }

    public int[] getSupportSourcelist() {
        int j = 0;
        int[] sourceIndexList = null;
        int[] sourceListFlag = CtvCommonManager.getInstance().getSourceList();
        String[] sourcearrayDM = getActivity().getResources().getStringArray(R.array.str_array_source);
        if (sourceListFlag != null) {
            for (int i = 0; i < sourcearrayDM.length; i++) {
                if (sourceListFlag[i] > 0) {
                    j++;
                }
            }
            sourceIndexList = new int[j];
            j = 0;
            for (int i = 0; i < sourcearrayDM.length; i++) {
                if (sourceListFlag[i] > 0) {
                    Log.d(TAG, "sourceIndexList[j]" + i + "j:" + j);
                    sourceIndexList[j] = i;
                    j++;
                }
            }
        }
        return sourceIndexList;
    }

    private void setWbData() {
        if (factoryManager != null) {
            clortempindex = factoryManager.getColorTmpIdx();
            Log.v(TAG, "-freshOffer----clortempindex----" + clortempindex);
        }
        rgainvalWB = factoryManager.getWbRedGain();
        ggainvalWB = factoryManager.getWbGreenGain();
        bgainvalWB = factoryManager.getWbBlueGain();
        roffsetvalWB = factoryManager.getWbRedOffset();
        goffsetvalWB = factoryManager.getWbGreenOffset();
        boffsetvalWB = factoryManager.getWbBlueOffset();
        ctvWbGainOffset.redGain = (short) rgainvalWB;
        ctvWbGainOffset.greenGain = (short) ggainvalWB;
        ctvWbGainOffset.blueGain = (short) bgainvalWB;
        ctvWbGainOffset.redOffset = (short) roffsetvalWB;
        ctvWbGainOffset.greenOffset = (short) goffsetvalWB;
        ctvWbGainOffset.blueOffset = (short) boffsetvalWB;
        LogUtils.d(TAG, "clortempindex==" + clortempindex);
        LogUtils.d(TAG, "rgainvalWB==" + rgainvalWB);
        LogUtils.d(TAG, "ggainvalWB==" + ggainvalWB);
        LogUtils.d(TAG, "bgainvalWB==" + bgainvalWB);
        LogUtils.d(TAG, "roffsetvalWB==" + roffsetvalWB);
        LogUtils.d(TAG, "goffsetvalWB==" + goffsetvalWB);
        LogUtils.d(TAG, "boffsetvalWB==" + boffsetvalWB);

        colorTempEnum = CtvEnumColorTemperature.E_COLOR_TEMP_NATURE;
        if (clortempindex == CtvPictureManager.COLOR_TEMP_NATURE) {
            colorTempEnum = CtvEnumColorTemperature.E_COLOR_TEMP_NATURE;
        } else if (clortempindex == CtvPictureManager.COLOR_TEMP_COOL) {
            colorTempEnum = CtvEnumColorTemperature.E_COLOR_TEMP_COOL;
        } else if (clortempindex == CtvPictureManager.COLOR_TEMP_WARM) {
            colorTempEnum = CtvEnumColorTemperature.E_COLOR_TEMP_WARM;
        } else if (clortempindex == CtvPictureManager.COLOR_TEMP_USER1) {
            colorTempEnum = CtvEnumColorTemperature.E_COLOR_TEMP_USER;
        } else if (clortempindex == CtvPictureManager.COLOR_TEMP_USER2) {
            colorTempEnum = CtvEnumColorTemperature.E_COLOR_TEMP_USER2;
        }
        try {
            CtvFactoryManager.getInstance().setWbGainOffsetToRegister(colorTempEnum, rgainvalWB,
                    ggainvalWB, bgainvalWB, roffsetvalWB, goffsetvalWB, boffsetvalWB,
                    CtvEnumInputSource.values()[sourceType[currentSourceindex]]);
        } catch (CtvCommonException e) {
            e.printStackTrace();
        }
        rgainvalWBSharp = rgainvalWB << 1;
        ggainvalWBSharp = ggainvalWB << 1;
        bgainvalWBSharp = bgainvalWB << 1;
        roffsetvalWBSharp = roffsetvalWB << 1;
        goffsetvalWBSharp = goffsetvalWB << 1;
        boffsetvalWBSharp = boffsetvalWB << 1;
        if (rgainvalWB == 2047) {
            rgainvalWBSharp = 4095;
        }
        if (ggainvalWB == 2047) {
            ggainvalWBSharp = 4095;
        }
        if (bgainvalWB == 2047) {
            bgainvalWBSharp = 4095;
        }
        if (roffsetvalWB == 2047) {
            roffsetvalWBSharp = 4095;
        }
        if (goffsetvalWB == 2047) {
            goffsetvalWBSharp = 4095;
        }
        if (boffsetvalWB == 2047) {
            boffsetvalWBSharp = 4095;
        }
    }


    /**
     * 更新 WB
     */
    public void freshOffer() {
        setWbData();
        seekBar_picture_wb_rgain.setProgress(rgainvalWBSharp);
        textView_picture_wb_rgain.setText(rgainvalWBSharp + "");
        seekBar_picture_wb_ggain.setProgress(ggainvalWBSharp);
        textView_picture_wb_ggain.setText(ggainvalWBSharp + "");
        seekBar_picture_wb_bgain.setProgress(bgainvalWBSharp);
        textView_picture_wb_bgain.setText(bgainvalWBSharp + "");
        seekBar_picture_wb_roffset.setProgress(roffsetvalWBSharp);
        textView_picture_wb_roffset.setText(roffsetvalWBSharp + "");
        seekBar_picture_wb_goffset.setProgress(goffsetvalWBSharp);
        textView_picture_wb_goffset.setText(goffsetvalWBSharp + "");
        seekBar_picture_wb_boffset.setProgress(boffsetvalWBSharp);
        textView_picture_wb_boffset.setText(boffsetvalWBSharp + "");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture_wb_normal:
                clearWBBtnState();
                clortempindex = CtvPictureManager.COLOR_TEMP_NATURE;
                handler.removeMessages(ChangeColorTempMsg);
                handler.sendEmptyMessageDelayed(ChangeColorTempMsg, 200);
                btn_picture_wb_normal.setBackgroundResource(R.color.colorFocus);
                break;
            case R.id.picture_wb_warm:
                clearWBBtnState();
                // "Cool", "Nature", "Warm", "User"
                clortempindex = CtvPictureManager.COLOR_TEMP_WARM;
                handler.removeMessages(ChangeColorTempMsg);
                handler.sendEmptyMessageDelayed(ChangeColorTempMsg, 200);
                btn_picture_wb_warm.setBackgroundResource(R.color.colorFocus);
                break;
            case R.id.picture_wb_cool:
                clearWBBtnState();
                clortempindex = CtvPictureManager.COLOR_TEMP_COOL;
                handler.removeMessages(ChangeColorTempMsg);
                handler.sendEmptyMessageDelayed(ChangeColorTempMsg, 200);
                btn_picture_wb_cool.setBackgroundResource(R.color.colorFocus);
                break;
            case R.id.picture_wb_eye:
                clearWBBtnState();
                clortempindex = CtvPictureManager.COLOR_TEMP_USER2;
                handler.removeMessages(ChangeColorTempMsg);
                handler.sendEmptyMessageDelayed(ChangeColorTempMsg, 200);
                btn_picture_wb_eye.setBackgroundResource(R.color.colorFocus);
                break;
        }
    }

    private void clearWBBtnState() {
        btn_picture_wb_normal.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_wb_warm.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_wb_cool.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_wb_eye.setBackgroundResource(R.color.colorUnFocus);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            switch (view.getId()) {
                case R.id.picture_wb_normal:
                    clearWBBtnState();
                    btn_picture_wb_normal.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_wb_warm:
                    clearWBBtnState();
                    btn_picture_wb_warm.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_wb_cool:
                    clearWBBtnState();
                    btn_picture_wb_cool.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_wb_eye:
                    clearWBBtnState();
                    btn_picture_wb_eye.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_wb_rgain:
                    ll_picture_wb_rgain.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_wb_ggain:
                    ll_picture_wb_ggain.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_wb_bgain:
                    ll_picture_wb_bgain.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_wb_roffset:
                    ll_picture_wb_roffset.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_wb_boffset:
                    ll_picture_wb_boffset.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_wb_goffset:
                    ll_picture_wb_goffset.setBackgroundResource(R.color.colorFocus);
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.picture_wb_rgain:
                case R.id.picture_wb_ggain:
                case R.id.picture_wb_bgain:
                case R.id.picture_wb_roffset:
                case R.id.picture_wb_boffset:
                case R.id.picture_wb_goffset:
                    clearLLState();
                    break;

            }
        }

    }

    private void clearLLState() {
        ll_picture_wb_rgain.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_wb_bgain.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_wb_ggain.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_wb_roffset.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_wb_boffset.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_wb_goffset.setBackgroundResource(R.color.colorUnFocus);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.picture_wb_rgain:
                rgainvalWBSharp = i;
                ctvWbGainOffset.redGain = (short) (rgainvalWBSharp >> 1);
                textView_picture_wb_rgain.setText(Integer.toString(rgainvalWBSharp));
                break;

            case R.id.picture_wb_ggain:
                ggainvalWBSharp = i;
                ctvWbGainOffset.greenGain = (short) (ggainvalWBSharp >> 1);
                textView_picture_wb_ggain.setText(Integer.toString(ggainvalWBSharp));
                break;
            case R.id.picture_wb_bgain:
                bgainvalWBSharp = i;
                ctvWbGainOffset.blueGain = (short) (bgainvalWBSharp >> 1);
                textView_picture_wb_bgain.setText(Integer.toString(bgainvalWBSharp));
                break;
            case R.id.picture_wb_roffset:
                roffsetvalWBSharp = i;
                ctvWbGainOffset.redOffset = (short) (roffsetvalWBSharp >> 1);
                textView_picture_wb_roffset.setText(Integer.toString(roffsetvalWBSharp));
                break;
            case R.id.picture_wb_goffset:
                goffsetvalWBSharp = i;
                ctvWbGainOffset.greenOffset = (short) (goffsetvalWBSharp >> 1);
                textView_picture_wb_goffset.setText(Integer.toString(goffsetvalWBSharp));
                break;
            case R.id.picture_wb_boffset:
                boffsetvalWBSharp = i;
                ctvWbGainOffset.blueOffset = (short) (boffsetvalWBSharp >> 1);
                textView_picture_wb_boffset.setText(Integer.toString(boffsetvalWBSharp));
                break;
        }
    }

    Runnable setRGBValue = new Runnable() {

        @Override
        public void run() {
            factoryManager.setSharpWbGainOrOffsetEx(ctvWbGainOffset,
                    CtvEnumInputSource.values()[sourceType[currentSourceindex]],
                    colorTempEnum);
            handler.removeMessages(ChangeColorTempMsg);
            handler.sendEmptyMessageDelayed(ChangeColorTempMsg, 200);
        }
    };

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.picture_wb_rgain:
                handler.removeCallbacks(setRGBValue);
                handler.postDelayed(setRGBValue, 500);
                break;

            case R.id.picture_wb_ggain:
                handler.removeCallbacks(setRGBValue);
                handler.postDelayed(setRGBValue, 500);
                break;
            case R.id.picture_wb_bgain:
                handler.removeCallbacks(setRGBValue);
                handler.postDelayed(setRGBValue, 500);
                break;
            case R.id.picture_wb_roffset:
                handler.removeCallbacks(setRGBValue);
                handler.postDelayed(setRGBValue, 500);
                break;
            case R.id.picture_wb_goffset:
                handler.removeCallbacks(setRGBValue);
                handler.postDelayed(setRGBValue, 500);
                break;
            case R.id.picture_wb_boffset:
                handler.removeCallbacks(setRGBValue);
                handler.postDelayed(setRGBValue, 500);
                break;
        }
    }
}
