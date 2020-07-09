package mstar.factorymenu.ui.fragments;

import android.os.Bundle;
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
import com.cultraview.tv.CtvPictureManager;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictrueCurveTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictrueCurveTypeFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, View.OnFocusChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //curve type
    private String[] curvetypearray = {
            "Volume", "Brightness", "Contrast", "Saturation", "Sharpness", "Hue", "BackLight"
    };
    private int curvetypeindex = 0;
    private int osd0val = 50;
    private int osd25val = 50;
    private int osd50val = 50;
    private int osd75val = 50;
    private int osd100val = 50;
    private int VolumeMaxVol = 100;
    private int BrightnessMaxVol = 255;
    private int ContrastMaxVol = 255;
    private int SaturationMaxVol = 255;
    private int SharpnessMaxVol = 63;
    private int HueMaxVol = 100;
    private int BackLightMaxVol = 255;
    private int BackLightStep = 256;
    private int BackLightVal = 0;
    private String[] sourcearrayNL;
    private int[] curvemaxarray = {
            VolumeMaxVol, BrightnessMaxVol, ContrastMaxVol, SaturationMaxVol, SharpnessMaxVol,
            HueMaxVol, BackLightMaxVol
    };

    private IFactoryDesk factoryManager;

    private Button btn_picture_curve_type_brightness, btn_picture_curve_type_contrast, btn_picture_curve_type_saturation, btn_picture_curve_type_hue, btn_picture_curve_type_sharpness;
    private SeekBar seekBar_picture_curve_type_osd0, seekBar_picture_curve_type_osd25, seekBar_picture_curve_type_osd50, seekBar_picture_curve_type_osd75, seekBar_picture_curve_type_osd100;
    private TextView textView_picture_curve_type_osd0_text, textView_picture_curve_type_osd25_text, textView_picture_curve_type_osd75_text, textView_picture_curve_type_osd50_text, textView_picture_curve_type_osd100_text;
    private LinearLayout ll_picture_curve_type_osd0;
    private LinearLayout ll_picture_curve_type_osd25;
    private LinearLayout ll_picture_curve_type_osd50;
    private LinearLayout ll_picture_curve_type_osd75;
    private LinearLayout ll_picture_curve_type_osd100;

    public PictrueCurveTypeFragment() {
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
    public static PictrueCurveTypeFragment newInstance(String param1, String param2) {
        PictrueCurveTypeFragment fragment = new PictrueCurveTypeFragment();
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
        View view = inflater.inflate(R.layout.picture_curve_type, container, false);
        initViews(view);
        initCurveType();
        return view;
    }

    /**
     * CurveType
     */
    private void initCurveType() {
        factoryManager = FactoryDeskImpl.getInstance(getActivity());
        curvetypeindex = factoryManager.getCurveType().ordinal();
        if (curvetypeindex == IFactoryDesk.MS_NLA_SET_INDEX.EN_NLA_BACKLIGHT.ordinal()) {
            curvemaxarray[curvetypeindex] = getmaxbacklight();
            if (curvemaxarray[curvetypeindex] < 1000) {
                BackLightStep = 1;
            } else {
                BackLightStep = 256;
            }
        }
        if (curvetypeindex == IFactoryDesk.MS_NLA_SET_INDEX.EN_NLA_VOLUME.ordinal()) {
            curvetypeindex = IFactoryDesk.MS_NLA_SET_INDEX.EN_NLA_BRIGHTNESS.ordinal();
        }
        if (curvetypeindex == IFactoryDesk.MS_NLA_SET_INDEX.EN_NLA_BACKLIGHT.ordinal()) {
            osd0val = factoryManager.getOsdV0Nonlinear() / BackLightStep;
            osd25val = factoryManager.getOsdV25Nonlinear() / BackLightStep;
            osd50val = factoryManager.getOsdV50Nonlinear() / BackLightStep;
            osd75val = factoryManager.getOsdV75Nonlinear() / BackLightStep;
            osd100val = factoryManager.getOsdV100Nonlinear() / BackLightStep;
        } else {
            osd0val = factoryManager.getOsdV0Nonlinear();
            osd25val = factoryManager.getOsdV25Nonlinear();
            osd50val = factoryManager.getOsdV50Nonlinear();
            osd75val = factoryManager.getOsdV75Nonlinear();
            osd100val = factoryManager.getOsdV100Nonlinear();
        }

        textView_picture_curve_type_osd0_text.setText(Integer.toString(osd0val));
        textView_picture_curve_type_osd25_text.setText(Integer.toString(osd25val));
        textView_picture_curve_type_osd50_text.setText(Integer.toString(osd50val));
        textView_picture_curve_type_osd75_text.setText(Integer.toString(osd75val));
        textView_picture_curve_type_osd100_text.setText(Integer.toString(osd100val));

        seekBar_picture_curve_type_osd0.setProgress(osd0val);
        seekBar_picture_curve_type_osd25.setProgress(osd25val);
        seekBar_picture_curve_type_osd50.setProgress(osd50val);
        seekBar_picture_curve_type_osd75.setProgress(osd75val);
        seekBar_picture_curve_type_osd100.setProgress(osd100val);

        clearCurveType();
        LogUtils.d("curvetypeindex:" + curvetypeindex);
        if (curvetypeindex == 1) {
            btn_picture_curve_type_brightness.setBackgroundResource(R.color.colorFocus);
        } else if (curvetypeindex == 2) {
            btn_picture_curve_type_contrast.setBackgroundResource(R.color.colorFocus);
        } else if (curvetypeindex == 3) {
            btn_picture_curve_type_saturation.setBackgroundResource(R.color.colorFocus);
        } else if (curvetypeindex == 5) {
            btn_picture_curve_type_hue.setBackgroundResource(R.color.colorFocus);
        } else {
            btn_picture_curve_type_sharpness.setBackgroundResource(R.color.colorFocus);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (this != null && !hidden) {
            LogUtils.d("onHiddenChanged");
            clearCurveType();
            if (curvetypeindex == 1) {
                btn_picture_curve_type_brightness.setBackgroundResource(R.color.colorFocus);
            } else if (curvetypeindex == 2) {
                btn_picture_curve_type_contrast.setBackgroundResource(R.color.colorFocus);
            } else if (curvetypeindex == 3) {
                btn_picture_curve_type_saturation.setBackgroundResource(R.color.colorFocus);
            } else if (curvetypeindex == 5) {
                btn_picture_curve_type_hue.setBackgroundResource(R.color.colorFocus);
            } else {
                btn_picture_curve_type_sharpness.setBackgroundResource(R.color.colorFocus);
            }
        }
    }
    /**
     * btn状态恢复
     */
    private void clearCurveType() {
        btn_picture_curve_type_brightness.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_curve_type_contrast.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_curve_type_saturation.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_curve_type_hue.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_curve_type_sharpness.setBackgroundResource(R.color.colorUnFocus);
        if (curvetypeindex == 4) {
            seekBar_picture_curve_type_osd0.setMax(63);
            seekBar_picture_curve_type_osd25.setMax(63);
            seekBar_picture_curve_type_osd50.setMax(63);
            seekBar_picture_curve_type_osd75.setMax(63);
            seekBar_picture_curve_type_osd100.setMax(63);
        } else if (curvetypeindex == 5) {
            seekBar_picture_curve_type_osd0.setMax(100);
            seekBar_picture_curve_type_osd25.setMax(100);
            seekBar_picture_curve_type_osd50.setMax(100);
            seekBar_picture_curve_type_osd75.setMax(100);
            seekBar_picture_curve_type_osd100.setMax(100);
        } else {
            seekBar_picture_curve_type_osd0.setMax(255);
            seekBar_picture_curve_type_osd25.setMax(255);
            seekBar_picture_curve_type_osd50.setMax(255);
            seekBar_picture_curve_type_osd75.setMax(255);
            seekBar_picture_curve_type_osd100.setMax(255);
        }
    }

    private void initViews(View view) {
        ll_picture_curve_type_osd0 = view.findViewById(R.id.ll_picture_curve_type_osd0);
        ll_picture_curve_type_osd25 = view.findViewById(R.id.ll_picture_curve_type_osd25);
        ll_picture_curve_type_osd50 = view.findViewById(R.id.ll_picture_curve_type_osd50);
        ll_picture_curve_type_osd75 = view.findViewById(R.id.ll_picture_curve_type_osd75);
        ll_picture_curve_type_osd100 = view.findViewById(R.id.ll_picture_curve_type_osd100);

        btn_picture_curve_type_brightness = view.findViewById(R.id.picture_curve_type_brightness);
        btn_picture_curve_type_contrast = view.findViewById(R.id.picture_curve_type_contrast);
        btn_picture_curve_type_saturation = view.findViewById(R.id.picture_curve_type_saturation);
        btn_picture_curve_type_hue = view.findViewById(R.id.picture_curve_type_hue);
        btn_picture_curve_type_sharpness = view.findViewById(R.id.picture_curve_type_sharpness);

        btn_picture_curve_type_brightness.setNextFocusLeftId(R.id.list_sub_title);

        seekBar_picture_curve_type_osd0 = view.findViewById(R.id.picture_curve_type_osd0);
        textView_picture_curve_type_osd0_text = view.findViewById(R.id.picture_curve_type_osd0_text);
        seekBar_picture_curve_type_osd25 = view.findViewById(R.id.picture_curve_type_osd25);
        textView_picture_curve_type_osd25_text = view.findViewById(R.id.picture_curve_type_osd25_text);
        seekBar_picture_curve_type_osd50 = view.findViewById(R.id.picture_curve_type_osd50);
        textView_picture_curve_type_osd50_text = view.findViewById(R.id.picture_curve_type_osd50_text);
        seekBar_picture_curve_type_osd75 = view.findViewById(R.id.picture_curve_type_osd75);
        textView_picture_curve_type_osd75_text = view.findViewById(R.id.picture_curve_type_osd75_text);
        seekBar_picture_curve_type_osd100 = view.findViewById(R.id.picture_curve_type_osd100);
        textView_picture_curve_type_osd100_text = view.findViewById(R.id.picture_curve_type_osd100_text);

        seekBar_picture_curve_type_osd0.setOnSeekBarChangeListener(this);
        seekBar_picture_curve_type_osd25.setOnSeekBarChangeListener(this);
        seekBar_picture_curve_type_osd50.setOnSeekBarChangeListener(this);
        seekBar_picture_curve_type_osd75.setOnSeekBarChangeListener(this);
        seekBar_picture_curve_type_osd100.setOnSeekBarChangeListener(this);


        seekBar_picture_curve_type_osd0.setOnFocusChangeListener(this);
        seekBar_picture_curve_type_osd25.setOnFocusChangeListener(this);
        seekBar_picture_curve_type_osd50.setOnFocusChangeListener(this);
        seekBar_picture_curve_type_osd75.setOnFocusChangeListener(this);
        seekBar_picture_curve_type_osd100.setOnFocusChangeListener(this);

        btn_picture_curve_type_brightness.setOnFocusChangeListener(this);
        btn_picture_curve_type_contrast.setOnFocusChangeListener(this);
        btn_picture_curve_type_saturation.setOnFocusChangeListener(this);
        btn_picture_curve_type_hue.setOnFocusChangeListener(this);
        btn_picture_curve_type_sharpness.setOnFocusChangeListener(this);
        btn_picture_curve_type_brightness.setOnClickListener(this);
        btn_picture_curve_type_contrast.setOnClickListener(this);
        btn_picture_curve_type_saturation.setOnClickListener(this);
        btn_picture_curve_type_hue.setOnClickListener(this);
        btn_picture_curve_type_sharpness.setOnClickListener(this);
    }

    public int getmaxbacklight() {
        int ret = 0;
        // TODO: 2020-01-04 8386
//        Uri uri = Uri.parse(("content://mstar.tv.factory/factoryextern"));
//        ContentResolver contentResolver = this.getContentResolver();
//        Cursor cursor = contentResolver.query(uri, null, null, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                ret = cursor.getInt(cursor.getColumnIndex("maxBacklightpwm"));
//            }
//            cursor.close();
//        }
        return ret;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.picture_curve_type_osd0:
                osd0val = i;
                textView_picture_curve_type_osd0_text.setText(Integer.toString(osd0val));
                factoryManager.setOsdV0Nonlinear(osd0val);
                break;
            case R.id.picture_curve_type_osd25:
                osd25val = i;
                textView_picture_curve_type_osd25_text.setText(Integer.toString(osd25val));
                factoryManager.setOsdV25Nonlinear(osd25val);
                break;
            case R.id.picture_curve_type_osd50:
                osd50val = i;
                textView_picture_curve_type_osd50_text.setText(Integer.toString(osd50val));
                factoryManager.setOsdV50Nonlinear(osd50val);
                break;
            case R.id.picture_curve_type_osd75:
                osd75val = i;
                textView_picture_curve_type_osd75_text.setText(Integer.toString(osd75val));
                factoryManager.setOsdV75Nonlinear(osd75val);
                break;
            case R.id.picture_curve_type_osd100:
                osd100val = i;
                textView_picture_curve_type_osd100_text.setText(Integer.toString(osd100val));
                factoryManager.setOsdV100Nonlinear(osd100val);
                break;

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

//curve type
//            private String[] curvetypearray = {
//                    "Volume", "Brightness", "Contrast", "Saturation", "Sharpness", "Hue", "BackLight"
//            };
            case R.id.picture_curve_type_brightness:
                curvetypeindex = 1;
                factoryManager.setCurveType(IFactoryDesk.MS_NLA_SET_INDEX.values()[curvetypeindex]);
                initCurveType();
                break;
            case R.id.picture_curve_type_contrast:
                curvetypeindex = 2;
                factoryManager.setCurveType(IFactoryDesk.MS_NLA_SET_INDEX.values()[curvetypeindex]);
                initCurveType();
                break;
            case R.id.picture_curve_type_saturation:
                curvetypeindex = 3;
                factoryManager.setCurveType(IFactoryDesk.MS_NLA_SET_INDEX.values()[curvetypeindex]);
                initCurveType();
                break;
            case R.id.picture_curve_type_hue:
                curvetypeindex = 5;
                factoryManager.setCurveType(IFactoryDesk.MS_NLA_SET_INDEX.values()[curvetypeindex]);
                initCurveType();
                break;
            case R.id.picture_curve_type_sharpness:
                curvetypeindex = 4;
                factoryManager.setCurveType(IFactoryDesk.MS_NLA_SET_INDEX.values()[curvetypeindex]);
                initCurveType();
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            switch (view.getId()) {
                case R.id.picture_curve_type_brightness:
                    clearCurveType();
                    btn_picture_curve_type_brightness.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_curve_type_contrast:
                    clearCurveType();
                    btn_picture_curve_type_contrast.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_curve_type_saturation:
                    clearCurveType();
                    btn_picture_curve_type_saturation.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_curve_type_hue:
                    clearCurveType();
                    btn_picture_curve_type_hue.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_curve_type_sharpness:
                    clearCurveType();
                    btn_picture_curve_type_sharpness.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_curve_type_osd0:
                    ll_picture_curve_type_osd0.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_curve_type_osd25:
                    ll_picture_curve_type_osd25.setBackgroundResource(R.color.colorFocus);
                    break;

                case R.id.picture_curve_type_osd50:
                    ll_picture_curve_type_osd50.setBackgroundResource(R.color.colorFocus);
                    break;

                case R.id.picture_curve_type_osd75:
                    ll_picture_curve_type_osd75.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_curve_type_osd100:
                    ll_picture_curve_type_osd100.setBackgroundResource(R.color.colorFocus);
                    break;

            }
        } else {
            switch (view.getId()) {
                case R.id.picture_curve_type_osd0:
                case R.id.picture_curve_type_osd25:
                case R.id.picture_curve_type_osd50:
                case R.id.picture_curve_type_osd75:
                case R.id.picture_curve_type_osd100:
                    clearLLState();
                    break;
            }
        }

    }

    private void clearLLState() {
        ll_picture_curve_type_osd0.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_curve_type_osd25.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_curve_type_osd50.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_curve_type_osd75.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_curve_type_osd100.setBackgroundResource(R.color.colorUnFocus);
    }
}
