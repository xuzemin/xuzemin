package mstar.factorymenu.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvPictureManager;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;
import mstar.tvsetting.factory.ui.designmenu.DesignMenuActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictrueModeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictrueModeFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnFocusChangeListener, View.OnClickListener {
    private final String TAG = PictrueModeFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private int currentTvInputSource = 0;
    private IFactoryDesk factoryManager;
    private int itemIndex = 0;
    private int picmodeindex = 0;
    private int brightnessval = 50;
    private int contrastval = 50;
    private int saturationval = 50;
    private int hueval = 50;
    private int sharpnessval = 50;
    private int backlightval = 50;
    private String[] mPicModeStrArray;
    private String[] sourcearrayPM;
    private int sourceTypeIndex = 0;
    private final int pictureModeNormal = 1;
    private final int pictureModeSoft = 2;
    private final int pictureModeUser = 3;
    private final int pictureModeVivid = 7;
    private Button btn_adc_ypbpr_sd;
    private Button btn_adc_ypbpr_hd;
    private Button btn_adc_vga;
    private CtvPictureManager mTvPictureManager = null;
    private Button btn_picture_mode_normal;
    private Button btn_picture_mode_soft;
    private Button btn_picture_mode_vivid;
    private Button btn_picture_mode_user;


    private SeekBar seekBar_picture_mode_brightness, seekBar_picture_mode_contrast, seekBar_picture_mode_staturation,
            seekBar_picture_mode_hue, seekBar_picture_mode_sharpness;
    private TextView textView_picture_mode_brightness, textView_picture_mode_contrast, textView_picture_mode_saturation,
            textView_picture_mode_hue, textView_picture_mode_sharpness;


    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

        }

        ;
    };
    private LinearLayout ll_picture_mode_brightness;
    private LinearLayout ll_picture_mode_contrast;
    private LinearLayout ll_picture_mode_saturation;
    private LinearLayout ll_picture_mode_hue;
    private LinearLayout ll_picture_mode_sharpness;

    public PictrueModeFragment() {
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
    public static PictrueModeFragment newInstance(String param1, String param2) {
        PictrueModeFragment fragment = new PictrueModeFragment();
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
        LogUtils.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.picture_mode, container, false);
        initViews(view);
        initMode();
        return view;
    }

    private void initViews(View view) {
        btn_picture_mode_normal = view.findViewById(R.id.picture_mode_normal);
        btn_picture_mode_soft = view.findViewById(R.id.picture_mode_soft);
        btn_picture_mode_vivid = view.findViewById(R.id.picture_mode_vivid);
        btn_picture_mode_user = view.findViewById(R.id.picture_mode_user);
        seekBar_picture_mode_brightness = view.findViewById(R.id.picture_mode_brightness);
        seekBar_picture_mode_contrast = view.findViewById(R.id.picture_mode_contrast);
        seekBar_picture_mode_staturation = view.findViewById(R.id.picture_mode_saturation);
        seekBar_picture_mode_hue = view.findViewById(R.id.picture_mode_hue);
        seekBar_picture_mode_sharpness = view.findViewById(R.id.picture_mode_sharpness);
        textView_picture_mode_brightness = view.findViewById(R.id.picture_mode_brightness_text);
        textView_picture_mode_contrast = view.findViewById(R.id.picture_mode_contrast_text);
        textView_picture_mode_saturation = view.findViewById(R.id.picture_mode_saturation_text);
        textView_picture_mode_hue = view.findViewById(R.id.picture_mode_hue_text);
        textView_picture_mode_sharpness = view.findViewById(R.id.picture_mode_sharpness_text);

        ll_picture_mode_brightness = view.findViewById(R.id.ll_picture_mode_brightness);
        ll_picture_mode_contrast = view.findViewById(R.id.ll_picture_mode_contrast);
        ll_picture_mode_saturation = view.findViewById(R.id.ll_picture_mode_saturation);
        ll_picture_mode_hue = view.findViewById(R.id.ll_picture_mode_hue);
        ll_picture_mode_sharpness = view.findViewById(R.id.ll_picture_mode_sharpness);


        btn_picture_mode_normal.setOnFocusChangeListener(this);
        btn_picture_mode_soft.setOnFocusChangeListener(this);
        btn_picture_mode_vivid.setOnFocusChangeListener(this);
        btn_picture_mode_user.setOnFocusChangeListener(this);
        seekBar_picture_mode_brightness.setOnFocusChangeListener(this);
        seekBar_picture_mode_contrast.setOnFocusChangeListener(this);
        seekBar_picture_mode_staturation.setOnFocusChangeListener(this);
        seekBar_picture_mode_hue.setOnFocusChangeListener(this);
        seekBar_picture_mode_sharpness.setOnFocusChangeListener(this);
        //mode listener
        btn_picture_mode_normal.setOnClickListener(this);
        btn_picture_mode_soft.setOnClickListener(this);
        btn_picture_mode_vivid.setOnClickListener(this);
        btn_picture_mode_user.setOnClickListener(this);
        btn_picture_mode_normal.setNextFocusLeftId(R.id.list_sub_title);

    }


    /**
     * 初始化MODE
     */
    private void initMode() {
        currentTvInputSource = CtvCommonManager.getInstance().getCurrentTvInputSource();
        mTvPictureManager = CtvPictureManager.getInstance();
        factoryManager = FactoryDeskImpl.getInstance(getActivity());
        LogUtils.d(TAG, "factoryManager:" + factoryManager);
        picmodeindex = factoryManager.getPictureModeIdx();
        brightnessval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_BRIGHTNESS);
        contrastval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_CONTRAST);
        saturationval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_SATURATION);
        hueval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_HUE);
        sharpnessval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_SHARPNESS);
        backlightval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_BACKLIGHT);
        Log.d(TAG, "picmodeindex:" + picmodeindex + "brightnessval：" + brightnessval + "contrastval：" + contrastval
                + "saturationval" + saturationval + "hueval：" + hueval + "sharpnessval：" + sharpnessval + "backlightval：" + backlightval);

        clearModeBtnState();
        if (picmodeindex == pictureModeSoft) {
            btn_picture_mode_soft.setBackgroundResource(R.color.colorFocus);
        } else if (picmodeindex == pictureModeVivid) {
            btn_picture_mode_vivid.setBackgroundResource(R.color.colorFocus);
        } else if (picmodeindex == pictureModeUser) {
            btn_picture_mode_user.setBackgroundResource(R.color.colorFocus);
        } else {
            btn_picture_mode_normal.setBackgroundResource(R.color.colorFocus);
        }
        seekBar_picture_mode_brightness.setProgress(brightnessval);
        textView_picture_mode_brightness.setText(brightnessval + "");
        seekBar_picture_mode_contrast.setProgress(contrastval);
        textView_picture_mode_contrast.setText(contrastval + "");
        seekBar_picture_mode_staturation.setProgress(saturationval);
        textView_picture_mode_saturation.setText(saturationval + "");
        seekBar_picture_mode_hue.setProgress(hueval);
        textView_picture_mode_hue.setText(hueval + "");
        seekBar_picture_mode_sharpness.setProgress(sharpnessval);
        textView_picture_mode_sharpness.setText(sharpnessval + "");

        seekBar_picture_mode_brightness.setOnSeekBarChangeListener(this);
        seekBar_picture_mode_contrast.setOnSeekBarChangeListener(this);
        seekBar_picture_mode_staturation.setOnSeekBarChangeListener(this);
        seekBar_picture_mode_hue.setOnSeekBarChangeListener(this);
        seekBar_picture_mode_sharpness.setOnSeekBarChangeListener(this);
    }

    /**
     * ADC 通道 btn状态恢复
     */
    private void clearModeBtnState() {
        btn_picture_mode_normal.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_mode_soft.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_mode_vivid.setBackgroundResource(R.color.colorUnFocus);
        btn_picture_mode_user.setBackgroundResource(R.color.colorUnFocus);
    }

    private void clearLLState() {
        ll_picture_mode_brightness.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_mode_contrast.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_mode_saturation.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_mode_hue.setBackgroundResource(R.color.colorUnFocus);
        ll_picture_mode_sharpness.setBackgroundResource(R.color.colorUnFocus);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.picture_mode_brightness:
                this.brightnessval = i;
                textView_picture_mode_brightness.setText(Integer.toString(brightnessval));
                mTvPictureManager.setBrightness(brightnessval);
                break;
            case R.id.picture_mode_contrast:
                this.contrastval = i;
                textView_picture_mode_contrast.setText(Integer.toString(i));
                mTvPictureManager.setContrast(contrastval);
                break;
            case R.id.picture_mode_saturation:
                this.saturationval = i;
                textView_picture_mode_saturation.setText(Integer.toString(saturationval));
                mTvPictureManager.setSaturation(saturationval);
                break;
            case R.id.picture_mode_hue:
                this.hueval = i;
                textView_picture_mode_hue.setText(Integer.toString(hueval));
                mTvPictureManager.setHue(hueval);
                break;
            case R.id.picture_mode_sharpness:
                this.sharpnessval = i;
                textView_picture_mode_sharpness.setText(Integer.toString(sharpnessval));
                mTvPictureManager.setSharpness(sharpnessval);
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
    public void onFocusChange(View view, boolean b) {
        if (b) {
            switch (view.getId()) {
                case R.id.picture_mode_normal:
                    clearModeBtnState();
                    btn_picture_mode_normal.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_mode_soft:
                    clearModeBtnState();
                    btn_picture_mode_soft.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_mode_vivid:
                    clearModeBtnState();
                    btn_picture_mode_vivid.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_mode_user:
                    clearModeBtnState();
                    btn_picture_mode_user.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_mode_brightness:
                    ll_picture_mode_brightness.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_mode_contrast:
                    ll_picture_mode_contrast.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_mode_saturation:
                    ll_picture_mode_saturation.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_mode_hue:
                    ll_picture_mode_hue.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.picture_mode_sharpness:
                    ll_picture_mode_sharpness.setBackgroundResource(R.color.colorFocus);
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.picture_mode_brightness:
                case R.id.picture_mode_contrast:
                case R.id.picture_mode_saturation:
                case R.id.picture_mode_hue:
                case R.id.picture_mode_sharpness:
                    clearLLState();
                    break;
            }
        }

    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (this != null && !hidden) {
            LogUtils.d("onHiddenChanged");
            clearModeBtnState();
            if (picmodeindex == pictureModeSoft) {
                btn_picture_mode_soft.setBackgroundResource(R.color.colorFocus);
            } else if (picmodeindex == pictureModeVivid) {
                btn_picture_mode_vivid.setBackgroundResource(R.color.colorFocus);
            } else if (picmodeindex == pictureModeUser) {
                btn_picture_mode_user.setBackgroundResource(R.color.colorFocus);
            } else {
                btn_picture_mode_normal.setBackgroundResource(R.color.colorFocus);
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture_mode_normal:
                clearModeBtnState();
                setPictureMode(pictureModeNormal);
                btn_picture_mode_normal.setBackgroundResource(R.color.colorFocus);
                break;
            case R.id.picture_mode_soft:
                clearModeBtnState();
                setPictureMode(pictureModeSoft);
                btn_picture_mode_soft.setBackgroundResource(R.color.colorFocus);
                break;
            case R.id.picture_mode_vivid:
                clearModeBtnState();
                setPictureMode(pictureModeVivid);
                btn_picture_mode_vivid.setBackgroundResource(R.color.colorFocus);
                break;
            case R.id.picture_mode_user:
                clearModeBtnState();
                setPictureMode(pictureModeUser);
                btn_picture_mode_user.setBackgroundResource(R.color.colorFocus);
                break;
        }
    }

    /**
     * 设置图片模式
     *
     * @param picmodeindex 1 normal  2 soft 3 user 7 vivid
     */
    private void setPictureMode(int picmodeindex) {
        this.picmodeindex = picmodeindex;
        setPictureMode();
    }

    public void setPictureMode() {
        handler.removeCallbacks(setpicturemode);
        handler.postDelayed(setpicturemode, 300);
    }

    Runnable setpicturemode = new Runnable() {

        @Override
        public void run() {
            Log.d(TAG, "picmodeindex:" + picmodeindex);
            mTvPictureManager.setPictureMode(picmodeindex);
            freshDataToUIWhenPicModChange();
        }
    };

    /**
     * MODE 更新 UI
     */
    private void freshDataToUIWhenPicModChange() {
        picmodeindex = factoryManager.getPictureModeIdx();
        brightnessval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_BRIGHTNESS);
        contrastval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_CONTRAST);
        saturationval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_SATURATION);
        hueval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_HUE);
        sharpnessval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_SHARPNESS);
        backlightval = factoryManager.getVideoItem(IFactoryDesk.EN_MS_VIDEOITEM.MS_VIDEOITEM_BACKLIGHT);
        // update values
        Log.d(TAG, "picmodeindex:" + picmodeindex + "brightnessval：" + brightnessval + "contrastval：" + contrastval
                + "saturationval" + saturationval + "hueval：" + hueval + "sharpnessval：" + sharpnessval + "backlightval：" + backlightval);
        seekBar_picture_mode_brightness.setProgress(brightnessval);
        textView_picture_mode_brightness.setText(brightnessval + "");
        seekBar_picture_mode_contrast.setProgress(contrastval);
        textView_picture_mode_contrast.setText(contrastval + "");
        seekBar_picture_mode_staturation.setProgress(saturationval);
        textView_picture_mode_saturation.setText(saturationval + "");
        seekBar_picture_mode_hue.setProgress(hueval);
        textView_picture_mode_hue.setText(hueval + "");
        seekBar_picture_mode_sharpness.setProgress(sharpnessval);
        textView_picture_mode_hue.setText(sharpnessval + "");
    }


}
