package mstar.factorymenu.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.common.vo.CtvEnumInputSource;
import com.mstar.android.tv.TvCommonManager;

import java.util.ArrayList;
import java.util.List;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictureADCFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureADCFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, View.OnFocusChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FactoryDeskImpl factoryManager;
    private int sourceIndexAdc = 0;
    private List<Integer> sourceId = new ArrayList<Integer>();
    private boolean enableVGA =true, enableYPBPR =true, enableSCART;

    private ProgressDialog progressDialog = null;
    private String[] sourcearrayADC = {
            "VGA", "YPbPr(SD)", "YPbPr(HD)", "Scart(RGB)"
    };
    private int rgainvalADC = 8191;
    private int ggainvalADC = 8191;
    private int bgainvalADC = 8191;
    private int roffsetvalADC = 4095;
    private int goffsetvalADC = 4095;
    private int boffsetvalADC = 4095;
    private int phasevalADC = 50;
    private LinearLayout ll_picture_adc_auto_tune1;
    private SeekBar seekBar_picture_adc_rgain;
    private SeekBar seekBar_picture_adc_ggain;
    private SeekBar seekBar_picture_adc_bgain;
    private SeekBar seekBar_picture_adc_r_offSet;
    private SeekBar seekBar_picture_adc_g_offSet;
    private SeekBar seekBar_picture_adc_b_offSet;
    private TextView textView_picture_adc_rgain;
    private TextView textView_picture_adc_ggain;
    private TextView textView_picture_adc_bgain;
    private TextView textView_picture_adc_r_offSet;
    private TextView textView_picture_adc_g_offSet;
    private TextView textView_picture_adc_b_offSet;

    private Button btn_picture_adc_ypbpr_sd;
    private Button btn_picture_adc_ypbpr_hd;
    private Button btn_picture_adc_vga;
    protected Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == IFactoryDesk.AUTOTUNE_START) {
                progressDialog = getProgressDialog();
                progressDialog.show();
            } else if (msg.what == IFactoryDesk.AUTOTUNE_END_SUCESSED) {
                if (null != progressDialog) {
                    progressDialog.dismiss();
                }
                setData();
                Toast.makeText(
                        getActivity(),
                        getActivity().getResources()
                                .getString(R.string.str_factory_adc_autoadjust_ok),
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == IFactoryDesk.AUTOTUNE_END_FAILED) {
                if (null != progressDialog) {
                    progressDialog.dismiss();
                }
                setData();
                Toast.makeText(
                        getActivity(),
                        getActivity().getResources().getString(
                                R.string.str_factory_adc_autoadjust_failed), Toast.LENGTH_SHORT)
                        .show();
            } else if (msg.what == IFactoryDesk.AUTOTUNE_TIMING_MODE_ERR) {
                if (null != progressDialog) {
                    progressDialog.dismiss();
                }
                setData();
                Toast.makeText(
                        getActivity(),
                        getActivity().getResources().getString(
                                R.string.str_factory_adc_autoadjust_timing_mode_err),
                        Toast.LENGTH_SHORT).show();
            }
        }

        ;
    };
    private LinearLayout ll_picture_adc_rgain;
    private LinearLayout ll_picture_adc_ggain;
    private LinearLayout ll_picture_adc_bgain;
    private LinearLayout ll_picture_adc_r_offSet;
    private LinearLayout ll_picture_adc_g_offSet_text;
    private LinearLayout ll_picture_adc_b_offSet_text;


    public PictureADCFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PictureADCFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PictureADCFragment newInstance(String param1, String param2) {
        PictureADCFragment fragment = new PictureADCFragment();
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
        View view = inflater.inflate(R.layout.picture_adc, container, false);
        factoryManager = FactoryDeskImpl.getInstance(getActivity());
        initData();
        findView(view);
        setData();
        return view;
    }

    private void findView(View view) {

        ll_picture_adc_rgain = view.findViewById(R.id.ll_picture_adc_rgain);
        ll_picture_adc_ggain = view.findViewById(R.id.ll_picture_adc_ggain);
        ll_picture_adc_bgain = view.findViewById(R.id.ll_picture_adc_bgain);
        ll_picture_adc_r_offSet = view.findViewById(R.id.ll_picture_adc_r_offSet);
        ll_picture_adc_g_offSet_text = view.findViewById(R.id.ll_picture_adc_g_offSet_text);
        ll_picture_adc_b_offSet_text = view.findViewById(R.id.ll_picture_adc_b_offSet_text);
        ll_picture_adc_auto_tune1 = view.findViewById(R.id.ll_picture_adc_auto_tune);

        btn_picture_adc_ypbpr_sd = view.findViewById(R.id.picture_adc_ypbpr_sd);
        btn_picture_adc_ypbpr_hd = view.findViewById(R.id.picture_adc_ypbpr_hd);
        btn_picture_adc_vga = view.findViewById(R.id.picture_adc_vga);

        seekBar_picture_adc_rgain = view.findViewById(R.id.picture_adc_rgain);
        seekBar_picture_adc_ggain = view.findViewById(R.id.picture_adc_ggain);
        seekBar_picture_adc_bgain = view.findViewById(R.id.picture_adc_bgain);
        seekBar_picture_adc_r_offSet = view.findViewById(R.id.picture_adc_r_offSet);
        seekBar_picture_adc_g_offSet = view.findViewById(R.id.picture_adc_g_offSet);
        seekBar_picture_adc_b_offSet = view.findViewById(R.id.picture_adc_b_offSet);
        textView_picture_adc_rgain = view.findViewById(R.id.picture_adc_rgain_text);
        textView_picture_adc_ggain = view.findViewById(R.id.picture_adc_ggain_text);
        textView_picture_adc_bgain = view.findViewById(R.id.picture_adc_bgain_text);
        textView_picture_adc_r_offSet = view.findViewById(R.id.picture_adc_r_offSet_text);
        textView_picture_adc_g_offSet = view.findViewById(R.id.picture_adc_g_offSet_text);
        textView_picture_adc_b_offSet = view.findViewById(R.id.picture_adc_b_offSet_text);


        ll_picture_adc_auto_tune1.setOnClickListener(this);
        ll_picture_adc_auto_tune1.setOnFocusChangeListener(this);
        btn_picture_adc_ypbpr_sd.setOnClickListener(this);
        btn_picture_adc_ypbpr_hd.setOnClickListener(this);
        btn_picture_adc_vga.setOnClickListener(this);

        btn_picture_adc_ypbpr_sd.setOnFocusChangeListener(this);
        btn_picture_adc_ypbpr_hd.setOnFocusChangeListener(this);
        btn_picture_adc_vga.setOnFocusChangeListener(this);
        seekBar_picture_adc_rgain.setOnFocusChangeListener(this);
        seekBar_picture_adc_ggain.setOnFocusChangeListener(this);
        seekBar_picture_adc_bgain.setOnFocusChangeListener(this);
        seekBar_picture_adc_r_offSet.setOnFocusChangeListener(this);
        seekBar_picture_adc_b_offSet.setOnFocusChangeListener(this);
        seekBar_picture_adc_g_offSet.setOnFocusChangeListener(this);

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (this != null && !hidden) {
            LogUtils.d("onHiddenChanged");
            clearBtnState();
            if (sourceId.get(sourceIndexAdc) == 0) {
                btn_picture_adc_vga.setBackgroundResource(R.color.colorFocus);
            } else if (sourceId.get(sourceIndexAdc) == 1) {
                btn_picture_adc_ypbpr_sd.setBackgroundResource(R.color.colorFocus);
            } else {
                btn_picture_adc_ypbpr_hd.setBackgroundResource(R.color.colorFocus);
            }
        }
    }

    private void initData() {
        if (factoryManager.getCurrentInputSource() == TvCommonManager.INPUT_SOURCE_VGA) {
            sourceIndexAdc = 2;
        } else if (factoryManager.getCurrentInputSource() == TvCommonManager.INPUT_SOURCE_SCART) {
            sourceIndexAdc = 3;
        } else {
            sourceIndexAdc = 0;
        }
        factoryManager.setHandler(handler, 4);

        int[] data = CtvCommonManager.getInstance().getSourceList();
        for (int i = 0; i < data.length; i++) {
            LogUtils.d("getSourceList:"+data[i]);
        }
        int i = 0;
        if (data[0] != 0) {// VGA

            LogUtils.d("getSourceList: data[0] != 0 VGA ");
            sourceId.add(i, 0);
            sourceIndexAdc = 0;
//            factoryManager.execSetInputSource(TvCommonManager.INPUT_SOURCE_VGA);
            i++;
            enableVGA = true;
        }
        if (data[16] != 0) {// YPBPR
            LogUtils.d("getSourceList: data[16] != 0 YPBPR ");

                sourceIndexAdc = 1;
//                factoryManager.execSetInputSource(TvCommonManager.INPUT_SOURCE_YPBPR);
            sourceId.add(i, 1);
            i++;
            sourceId.add(i, 2);
            i++;
            enableYPBPR = true;
        }
        if (data[20] != 0) {// SCART
            LogUtils.d("getSourceList: data[20] != 0 SCART ");
            if (i == 0) {
                sourceIndexAdc = 3;
                factoryManager.execSetInputSource(TvCommonManager.INPUT_SOURCE_SCART);
            }
            sourceId.add(i, 3);
            enableSCART = true;
        }
        if (CtvCommonManager.getInstance().getCurrentTvInputSource()!= CtvEnumInputSource.E_INPUT_SOURCE_VGA.ordinal()){
            factoryManager.execSetInputSource(TvCommonManager.INPUT_SOURCE_YPBPR);
            sourceIndexAdc = 1;
        }else {
            factoryManager.execSetInputSource(TvCommonManager.INPUT_SOURCE_VGA);
            sourceIndexAdc = 0;
        }

    }

    private void setData() {
        int ordinal = factoryManager.getAdcIdx().ordinal();
        factoryManager.loadCurAdcDataFromDB(ordinal);
        rgainvalADC = factoryManager.getADCRedGain();
        ggainvalADC = factoryManager.getADCGreenGain();
        bgainvalADC = factoryManager.getADCBlueGain();
        roffsetvalADC = factoryManager.getADCRedOffset();
        goffsetvalADC = factoryManager.getADCGreenOffset();
        boffsetvalADC = factoryManager.getADCBlueOffset();
        phasevalADC = factoryManager.getADCPhase();
        textView_picture_adc_rgain.setText(Integer.toString(rgainvalADC));
        textView_picture_adc_ggain.setText(Integer.toString(ggainvalADC));
        textView_picture_adc_bgain.setText(Integer.toString(bgainvalADC));
        textView_picture_adc_r_offSet.setText(Integer.toString(roffsetvalADC));
        textView_picture_adc_g_offSet.setText(Integer.toString(goffsetvalADC));
        textView_picture_adc_b_offSet.setText(Integer.toString(boffsetvalADC));

        seekBar_picture_adc_rgain.setProgress(rgainvalADC);
        seekBar_picture_adc_ggain.setProgress(ggainvalADC);
        seekBar_picture_adc_bgain.setProgress(bgainvalADC);
        seekBar_picture_adc_r_offSet.setProgress(roffsetvalADC);
        seekBar_picture_adc_g_offSet.setProgress(goffsetvalADC);
        seekBar_picture_adc_b_offSet.setProgress(boffsetvalADC);

        seekBar_picture_adc_rgain.setOnSeekBarChangeListener(this);
        seekBar_picture_adc_ggain.setOnSeekBarChangeListener(this);
        seekBar_picture_adc_bgain.setOnSeekBarChangeListener(this);
        seekBar_picture_adc_r_offSet.setOnSeekBarChangeListener(this);
        seekBar_picture_adc_g_offSet.setOnSeekBarChangeListener(this);
        seekBar_picture_adc_b_offSet.setOnSeekBarChangeListener(this);


//        text_factory_adc_phase_val.setText(Integer.toString(phasevalADC));
//        text_factory_adc_source_val.setText(sourcearrayADC[sourceId.get(sourceIndexAdc)]);
        clearBtnState();
        LogUtils.d("sourceIndexAdc:"+sourceIndexAdc);
        if (sourceId.get(sourceIndexAdc) == 0) {
            btn_picture_adc_vga.setBackgroundResource(R.color.colorFocus);
        } else if (sourceId.get(sourceIndexAdc) == 1) {
            btn_picture_adc_ypbpr_sd.setBackgroundResource(R.color.colorFocus);
        } else {
            btn_picture_adc_ypbpr_hd.setBackgroundResource(R.color.colorFocus);
        }
        LogUtils.d("rgainvalADC:" + rgainvalADC + "ggainvalADC:" + ggainvalADC
                + "bgainvalADC:" + bgainvalADC + "roffsetvalADC:" + roffsetvalADC + "goffsetvalADC:" + goffsetvalADC + "boffsetvalADC:" + boffsetvalADC
                + "phasevalADC:" + phasevalADC);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        LogUtils.d("onProgressChanged:");
        switch (seekBar.getId()) {
            case R.id.picture_adc_rgain:
                LogUtils.d("onProgressChanged:picture_adc_rgain");

                rgainvalADC = i;
                textView_picture_adc_rgain.setText(Integer.toString(rgainvalADC));
//                factoryManager.setADCRedGain(rgainvalADC);
                break;

            case R.id.picture_adc_ggain:
                LogUtils.d("onProgressChanged:picture_adc_ggain");

                ggainvalADC = i;
                textView_picture_adc_ggain.setText(Integer.toString(ggainvalADC));
//                factoryManager.setADCGreenGain(ggainvalADC);
                break;
            case R.id.picture_adc_bgain:
                LogUtils.d("onProgressChanged:picture_adc_bgain");

                bgainvalADC = i;
                textView_picture_adc_bgain.setText(Integer.toString(bgainvalADC));
//                factoryManager.setADCBlueGain(bgainvalADC);
                break;
            case R.id.picture_adc_r_offSet:
                LogUtils.d("onProgressChanged:picture_adc_r_offSet");

                roffsetvalADC = i;
                textView_picture_adc_r_offSet.setText(Integer.toString(roffsetvalADC));
//                factoryManager.setADCRedOffset(roffsetvalADC);
                break;
            case R.id.picture_adc_g_offSet:
                LogUtils.d("onProgressChanged:picture_adc_g_offSet");

                roffsetvalADC = i;
                textView_picture_adc_g_offSet.setText(Integer.toString(roffsetvalADC));
//                factoryManager.setADCGreenOffset(roffsetvalADC);
                break;
            case R.id.picture_adc_b_offSet:
                LogUtils.d("onProgressChanged:picture_adc_b_offSet");

                boffsetvalADC = i;
                textView_picture_adc_b_offSet.setText(Integer.toString(boffsetvalADC));
//                factoryManager.setADCBlueOffset(boffsetvalADC);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        LogUtils.d("onStopTrackingTouch:");
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (seekBar.getId()) {
                    case R.id.picture_adc_rgain:
                        factoryManager.setADCRedGain(rgainvalADC);
                        break;
                    case R.id.picture_adc_ggain:
                        factoryManager.setADCGreenGain(ggainvalADC);
                        break;
                    case R.id.picture_adc_bgain:
                        factoryManager.setADCBlueGain(bgainvalADC);
                        break;
                    case R.id.picture_adc_r_offSet:
                        factoryManager.setADCRedOffset(roffsetvalADC);
                        break;
                    case R.id.picture_adc_g_offSet:
                        factoryManager.setADCGreenOffset(roffsetvalADC);
                        break;
                    case R.id.picture_adc_b_offSet:
                        factoryManager.setADCBlueOffset(boffsetvalADC);
                        break;
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture_adc_ypbpr_sd:
                sourceIndexAdc = 1;
                exeInputSource();
                setData();
                clearBtnState();
                btn_picture_adc_ypbpr_sd.setBackgroundResource(R.color.colorFocus);

                break;
            case R.id.picture_adc_ypbpr_hd:
                sourceIndexAdc = 2;
                exeInputSource();
                setData();
                clearBtnState();
                btn_picture_adc_ypbpr_hd.setBackgroundResource(R.color.colorFocus);
                break;
            case R.id.picture_adc_vga:
                sourceIndexAdc = 0;
                exeInputSource();
                setData();
                clearBtnState();
                btn_picture_adc_vga.setBackgroundResource(R.color.colorFocus);
                break;
            case R.id.ll_picture_adc_auto_tune:
                factoryManager.ExecAutoADC(sourceIndexAdc);

                break;
        }
    }

    private void clearBtnState() {
        btn_picture_adc_vga.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_adc_ypbpr_hd.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_adc_ypbpr_sd.setBackgroundResource(R.color.colorUnFocus);
    }

    /**
     * 切换通道
     */
    private void exeInputSource() {
        factoryManager.setAdcIdx(IFactoryDesk.E_ADC_SET_INDEX.values()[sourceId
                .get(sourceIndexAdc)]);
        if (factoryManager.getAdcIdx() == IFactoryDesk.E_ADC_SET_INDEX.ADC_SET_VGA && enableVGA) {
            factoryManager.execSetInputSource(TvCommonManager.INPUT_SOURCE_VGA);
        } else if (factoryManager.getAdcIdx() == IFactoryDesk.E_ADC_SET_INDEX.ADC_SET_SCART_RGB
                && enableSCART) {
            factoryManager.execSetInputSource(TvCommonManager.INPUT_SOURCE_SCART);
        } else if ((factoryManager.getAdcIdx() == IFactoryDesk.E_ADC_SET_INDEX.ADC_SET_YPBPR_SD || factoryManager
                .getAdcIdx() == IFactoryDesk.E_ADC_SET_INDEX.ADC_SET_YPBPR_HD) && enableYPBPR) {
            factoryManager.execSetInputSource(TvCommonManager.INPUT_SOURCE_YPBPR);
        }
    }

    private ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getActivity()
                    .getString(R.string.str_factory_adc_autoadjust_val));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        return progressDialog;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            switch (view.getId()) {
                case R.id.picture_adc_ypbpr_sd:
                    clearBtnState();
                    btn_picture_adc_ypbpr_sd.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_adc_ypbpr_hd:
                    clearBtnState();
                    btn_picture_adc_ypbpr_hd.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_adc_vga:
                    clearBtnState();
                    btn_picture_adc_vga.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_adc_rgain:
                    clearLLState();
                    ll_picture_adc_rgain.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_adc_ggain:
                    clearLLState();
                    ll_picture_adc_ggain.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_adc_bgain:
                    clearLLState();
                    ll_picture_adc_bgain.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_adc_r_offSet:
                    clearLLState();
                    ll_picture_adc_r_offSet.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_adc_g_offSet:
                    clearLLState();
                    ll_picture_adc_g_offSet_text.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_adc_b_offSet:
                    clearLLState();
                    ll_picture_adc_b_offSet_text.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.ll_picture_adc_auto_tune:
                    clearLLState();
                    ll_picture_adc_auto_tune1.setBackgroundResource(R.color.colorFocus);
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.picture_adc_rgain:
                case R.id.picture_adc_ggain:
                case R.id.picture_adc_bgain:
                case R.id.picture_adc_r_offSet:
                case R.id.picture_adc_g_offSet:
                case R.id.picture_adc_b_offSet:
                case R.id.ll_picture_adc_auto_tune:
                    clearLLState();
                    break;
            }
        }


    }

    private void clearLLState() {
        ll_picture_adc_rgain.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_adc_ggain.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_adc_bgain.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_adc_r_offSet.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_adc_g_offSet_text.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_adc_b_offSet_text.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_adc_auto_tune1.setBackgroundResource(R.color.colorUnFocus);
    }
}
