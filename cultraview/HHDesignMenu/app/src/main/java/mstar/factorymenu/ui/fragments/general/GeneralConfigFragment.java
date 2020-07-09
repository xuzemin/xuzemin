package mstar.factorymenu.ui.fragments.general;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvFactoryManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.hht.android.sdk.lock.HHTLockManager;
import com.mstar.android.tvapi.common.TvManager;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;

import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralConfigFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralConfigFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, View.OnFocusChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView imageView_dual_port, imageView_wake_on_lan, imageView_mirror_enable;
    private IFactoryDesk factoryManager;
    private int wakeonlanindex = 0;
    private CtvTvManager mCtvTvManager;
    private CtvFactoryManager mCtvFactoryManager;
    private String CUSTOMER_INI_PATH;
    private String MIRROR_OSD_VALUE;
    String[] mirror_val = {
            "False", "True"
    };
    private int mirrorindex;
    private ImageView imageView_test_pattern_left;
    private ImageView imageView_test_pattern_right;
    private String[] testPatternType = {
            "Off", "White", "Red", "Green", "Blue", "Black"
    };
    private int[] Touch_Type = {0, 1};
    private int touchTypeIndex = 0;
    private int testpartternindex = 0;
    private TextView textView_pattern;
    private RelativeLayout relativeLayout_enable_str;
    private RelativeLayout relativeLayout_wake_on_lan;
    private RelativeLayout relativeLayout_dual_port;
    private RelativeLayout relativeLayout_mirror_enable;
    private RelativeLayout relativeLayout_touch_type_left;
    private RelativeLayout relativeLayout_touch_pattern_left;
    private RelativeLayout relativeLayout_front_function;
    private TextView text_general_config_touch_type;
    private ImageView imageView_front_function;

    public GeneralConfigFragment() {
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
    public static GeneralConfigFragment newInstance(String param1, String param2) {
        GeneralConfigFragment fragment = new GeneralConfigFragment();
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
        View view = inflater.inflate(R.layout.general_configuration, container, false);
        initViews(view);
        return view;
    }


    private void initViews(View view) {
        imageView_dual_port = view.findViewById(R.id.img_dual_port);
        imageView_wake_on_lan = view.findViewById(R.id.img_wake_on_lan);
        imageView_mirror_enable = view.findViewById(R.id.img_mirror_enable);
        imageView_test_pattern_left = view.findViewById(R.id.img_touch_pattern_left);
        imageView_test_pattern_right = view.findViewById(R.id.img_general_config_pattern_right);
        imageView_front_function = view.findViewById(R.id.img_front_function);
        textView_pattern = view.findViewById(R.id.text_general_config_pattern);
        text_general_config_touch_type = view.findViewById(R.id.text_general_config_touch_type);
        imageView_test_pattern_left.setOnClickListener(this);
        imageView_test_pattern_right.setOnClickListener(this);

        relativeLayout_enable_str = view.findViewById(R.id.rl_enable_str);
        relativeLayout_wake_on_lan = view.findViewById(R.id.rl_wake_on_lan);
        relativeLayout_dual_port = view.findViewById(R.id.rl_dual_port);
        relativeLayout_mirror_enable = view.findViewById(R.id.rl_mirror_enable);
        relativeLayout_touch_type_left = view.findViewById(R.id.rl_touch_type_left);
        relativeLayout_touch_pattern_left = view.findViewById(R.id.rl_touch_pattern_left);
        relativeLayout_front_function = view.findViewById(R.id.rl_front_function);

        relativeLayout_enable_str.setOnClickListener(this);
        relativeLayout_wake_on_lan.setOnClickListener(this);
        relativeLayout_dual_port.setOnClickListener(this);
        relativeLayout_mirror_enable.setOnClickListener(this);
        relativeLayout_front_function.setOnClickListener(this);
        setData();
        relativeLayout_touch_pattern_left.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KEYCODE_DPAD_RIGHT) {
                        imageView_test_pattern_left.performClick();
                        return true;
                    } else if (keyCode == KEYCODE_DPAD_LEFT) {
                        imageView_test_pattern_right.performClick();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            mirrorindex = checkIndex(MIRROR_OSD_VALUE, mirror_val);
            LogUtils.d("mirrorindex--->" + mirrorindex);
            if (mirrorindex == 0) {
                //关，
                imageView_mirror_enable.setImageResource(R.mipmap.switch_nor);
            } else {
                //开
                imageView_mirror_enable.setImageResource(R.mipmap.switch_select);
            }

        }
    };

    private void setData() {
        mCtvFactoryManager = CtvFactoryManager.getInstance();
        factoryManager = FactoryDeskImpl.getInstance(getActivity());
        mCtvTvManager = CtvTvManager.getInstance();
        boolean keypadLock = HHTLockManager.getInstance().isKeypadLock();
        LogUtils.d("isKeypadLock--->" + keypadLock);
        if (!keypadLock){
            imageView_front_function.setImageResource(R.mipmap.switch_select);
        }else {
            imageView_front_function.setImageResource(R.mipmap.switch_nor);
        }
        try {
            String singleMode = CtvTvManager.getInstance().getEnvironment("singleMode");
            LogUtils.d("singleMode--->" + singleMode);
            if (singleMode.equals("off")) {
                imageView_dual_port.setImageResource(R.mipmap.switch_nor);
            } else {
                imageView_dual_port.setImageResource(R.mipmap.switch_select);
            }
        } catch (CtvCommonException e) {
            e.printStackTrace();
        }

        wakeonlanindex = factoryManager.getWOLEnableStatus() ? 1 : 0;
        //0 off 1 on
        if (wakeonlanindex == 1) {
            imageView_wake_on_lan.setImageResource(R.mipmap.switch_select);
        } else {
            imageView_wake_on_lan.setImageResource(R.mipmap.switch_nor);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CUSTOMER_INI_PATH = TvManager.getInstance().getStringFromIni(IFactoryDesk.SYS_INI_PATH,
                            IFactoryDesk.CUSTOMER_INI_KEY);
                    MIRROR_OSD_VALUE = TvManager.getInstance().getStringFromIni(CUSTOMER_INI_PATH,
                            IFactoryDesk.MIRROR_OSD_KEY);

//                    CUSTOMER_INI_PATH = mCtvFactoryManager.getStringFromIni(IFactoryDesk.SYS_INI_PATH,
//                            IFactoryDesk.CUSTOMER_INI_KEY);
                    LogUtils.d("CUSTOMER_INI_PATH--->" + CUSTOMER_INI_PATH);
//                    MIRROR_OSD_VALUE = mCtvFactoryManager.getStringFromIni(CUSTOMER_INI_PATH,
//                            IFactoryDesk.MIRROR_OSD_KEY);
                    handler.sendEmptyMessage(1001);
                } catch (Exception e) {
                    LogUtils.e("getStringFromIni e：" + e);
                }
            }
        }).start();

//        boolean wolEnableStatus = factoryManager.getWOLEnableStatus();
//        if (!wolEnableStatus) {
//            //关，
//            imageView_mirror_enable.setImageResource(R.mipmap.switch_nor);
//        } else {
//            //开
//            imageView_mirror_enable.setImageResource(R.mipmap.switch_select);
//        }

        //>10 电容
        //<10 红外
        try {
            short[] getTouchModes = CtvTvManager.getInstance().setTvosCommonCommand("oGetTouchMode");
            if (getTouchModes != null) {
                LogUtils.d("oGetTouchMode-->" + getTouchModes.length);
                for (int i = 0; i < getTouchModes.length; i++) {
                    short getTouchMode = getTouchModes[i];
                    LogUtils.d("oGetTouchMode-i->" + getTouchMode);
                    if (getTouchMode > 10) {
                        touchTypeIndex = 0;
                    } else {
                        touchTypeIndex = 1;
                    }
                }
            }
        } catch (CtvCommonException e) {
            e.printStackTrace();
        }

        testpartternindex = factoryManager.getTestPattern();
        textView_pattern.setText(testPatternType[testpartternindex]);
        text_general_config_touch_type.setText(Touch_Type[touchTypeIndex] + "");
    }

    private int checkIndex(String s, String[] sss) {
        int index = 0;
        for (int i = 0; i < sss.length; i++) {
            if (s.equals(sss[i])) {
                index = i;
                break;
            }
        }
        LogUtils.d(s + " : " + index);
        return index;
    }


    /**
     * 设置单双屏  off 单屏 on 双屏
     *
     * @param dualFlag
     */
    public void setDual(String dualFlag) {
        try {
            LogUtils.d("singleMode SetNovaTekTconMode#:" + dualFlag);
            CtvTvManager.getInstance().setTvosCommonCommand("SetNovaTekTconMode#" + dualFlag);
            LogUtils.d("singleMode #:" + CtvTvManager.getInstance().getEnvironment("singleMode"));
        } catch (Exception e) {
            LogUtils.e("CtvTvManager Exception:" + e);
        }


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {


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
            case R.id.rl_enable_str:
                Toast.makeText(getActivity(), "功能还未开发", Toast.LENGTH_SHORT).show();
                break;

            case R.id.rl_dual_port:
                try {
                    String singleMode = "on".equals(CtvCommonManager
                            .getInstance().getEnvironment("singleMode")) ? "off"
                            : "on";
                    LogUtils.d("img_dual_port onclick  singleMode:" + singleMode);
                    CtvTvManager.getInstance().setEnvironment("singleMode", singleMode);

                    if (singleMode.equals("off")) {
                        setDual("1");
                        imageView_dual_port.setImageResource(R.mipmap.switch_nor);
                    } else {
                        setDual("0");
                        imageView_dual_port.setImageResource(R.mipmap.switch_select);
                    }

                } catch (Exception e) {
                    LogUtils.e("Exception-->" + e);
                }

                break;
            case R.id.rl_wake_on_lan:
                if (wakeonlanindex == 1) {
                    wakeonlanindex = 0;
                    imageView_wake_on_lan.setImageResource(R.mipmap.switch_nor);
                    factoryManager.setWOLEnableStatus(false);
                } else {
                    wakeonlanindex = 1;
                    imageView_wake_on_lan.setImageResource(R.mipmap.switch_select);
                    factoryManager.setWOLEnableStatus(true);
                }
                break;
            case R.id.rl_mirror_enable:
                try {
                    TvManager.getInstance().setTvosInterfaceCommand("RemountRWTvconfig");
                } catch (Exception e) {
                    LogUtils.e("rl_mirror_enable error:" + e);
                }
                if (mirrorindex == 0) {
                    mirrorindex = 1;
                    imageView_mirror_enable.setImageResource(R.mipmap.switch_select);
                } else {
                    mirrorindex = 0;
                    imageView_mirror_enable.setImageResource(R.mipmap.switch_nor);
                }
                LogUtils.d("updateCustomerIniFile");
                try {
                    boolean ret = mCtvTvManager.updateCustomerIniFile(IFactoryDesk.MIRROR_OSD_KEY, mirror_val[mirrorindex]);
                    LogUtils.d("TvManager updateCustomerIniFile ret:" + ret + "IFactoryDesk.MIRROR_OSD_KEY:" + IFactoryDesk.MIRROR_OSD_KEY);
                    boolean ret2 = mCtvTvManager.updateCustomerIniFile(IFactoryDesk.MIRROR_VIDEO_KEY, mirror_val[mirrorindex]);
                    LogUtils.d("TvManager updateCustomerIniFile ret2:" + ret2 + "IFactoryDesk.MIRROR_VIDEO_KEY:" + IFactoryDesk.MIRROR_VIDEO_KEY);

                    if (ret2) {
                        MIRROR_OSD_VALUE = mirror_val[mirrorindex];
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    TvManager.getInstance().setTvosInterfaceCommand("RemountROTvconfig");
                } catch (Exception e) {
                    LogUtils.e("rl_mirror_enable error:" + e);
                }
                break;
            case R.id.img_general_config_pattern_right:
                if (testpartternindex != IFactoryDesk.TEST_PATTERN_MODE_OFF) {
                    testpartternindex++;
                    if (testpartternindex == 0) {
                        testpartternindex = 1;
                    }
                } else {
                    testpartternindex = 1;
                }

                // testPatternType[testpartternindex]
                textView_pattern.setText(testPatternType[testpartternindex]);

                factoryManager.setTestPattern(testpartternindex);
                if (testpartternindex == 0) {
                    SystemProperties.set("user.testpattern.enable", "0");
                } else {
                    SystemProperties.set("user.testpattern.enable", "1");
                }
                Intent intent_testpattern = new Intent();
                intent_testpattern
                        .setAction("android.intent.action.testpattern.enable");
                getActivity().sendBroadcast(intent_testpattern);
                LogUtils.d(" send android.intent.action.testpattern.enable:");
                break;
            case R.id.img_touch_pattern_left:
                if (testpartternindex != 0) {
                    testpartternindex--;
                    if (testpartternindex == 0) {
                        testpartternindex = 5;
                    }
                } else {
                    testpartternindex = IFactoryDesk.TEST_PATTERN_MODE_BLACK;
                }
                // testPatternType[testpartternindex]
                textView_pattern.setText(testPatternType[testpartternindex]);

                factoryManager.setTestPattern(testpartternindex);
                if (testpartternindex == 0) {
                    SystemProperties.set("user.testpattern.enable", "0");
                } else {
                    SystemProperties.set("user.testpattern.enable", "1");
                }
                Intent intent_testpattern2 = new Intent();
                intent_testpattern2
                        .setAction("android.intent.action.testpattern.enable");
                getActivity().sendBroadcast(intent_testpattern2);
                LogUtils.d(" send android.intent.action.testpattern.enable:");
                break;
            case R.id.rl_front_function:
                LogUtils.d("isKeypadLock"+HHTLockManager.getInstance().isKeypadLock());
                if (HHTLockManager.getInstance().isKeypadLock()){
                    HHTLockManager.getInstance().setKeypadLock(false,new int[]{0});
                    imageView_front_function.setImageResource(R.mipmap.switch_select);
                }else {
                    HHTLockManager.getInstance().setKeypadLock(true,new int[]{KeyEvent.KEYCODE_POWER});
                    imageView_front_function.setImageResource(R.mipmap.switch_nor);
                }
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
        }
    }
}
