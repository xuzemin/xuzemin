package mstar.factorymenu.ui.fragments.general;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvFactoryManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.mstar.android.tvapi.common.TvManager;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.factorymenu.ui.utils.MyBurningService;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralDebugFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralDebugFragment extends Fragment implements  View.OnClickListener, View.OnFocusChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView imageView_uart_enable;
    private ImageView imageView_rs232_command;
    private IFactoryDesk factoryManager;
    private short watchDogMode;

    private String[] watchdogenable = {
            "Off", "On"
    };
    private ImageView imageViewWatchDog, img_ttys0;
    private RelativeLayout relativeLayout_uart_enable;
    private RelativeLayout relativeLayout_rs232_command;
    private RelativeLayout relativeLayout_watch_dog;
    private RelativeLayout relativeLayout_ttys0,relativeLayout_aging_mode;

    public GeneralDebugFragment() {
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
    public static GeneralDebugFragment newInstance(String param1, String param2) {
        GeneralDebugFragment fragment = new GeneralDebugFragment();
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
        View view = inflater.inflate(R.layout.general_debug, container, false);
        factoryManager = FactoryDeskImpl.getInstance(getActivity());
        initViews(view);
        return view;
    }


    private void initViews(View view) {
        imageView_uart_enable = view.findViewById(R.id.img_uart_enable);
        imageView_rs232_command = view.findViewById(R.id.img_rs232_command);
        imageViewWatchDog = view.findViewById(R.id.img_watch_dog);
        img_ttys0 = view.findViewById(R.id.img_ttys0);


        relativeLayout_uart_enable = view.findViewById(R.id.rl_uart_enable);
        relativeLayout_rs232_command = view.findViewById(R.id.rl_rs232_command);
        relativeLayout_watch_dog = view.findViewById(R.id.rl_watch_dog);
        relativeLayout_ttys0 = view.findViewById(R.id.rl_ttys0);
        relativeLayout_aging_mode = view.findViewById(R.id.rl_aging_mode);

        try {
            boolean uartOnOff = "on".equals(CtvCommonManager
                    .getInstance().getEnvironment("CENTER_CTL"));
            if (uartOnOff) {
                imageView_rs232_command.setImageResource(R.mipmap.switch_select);
            } else {
                imageView_rs232_command.setImageResource(R.mipmap.switch_nor);
            }
        } catch (Exception e) {
            LogUtils.e("uartOnOff-->Exception：" + e);
        }

        try {
            String center_ctl = CtvCommonManager.getInstance().getEnvironment("CULTRAVIEW_PRINT");
            LogUtils.d("center_ctl:" + center_ctl);
            if (center_ctl.equals("on")) {
                imageView_uart_enable.setImageResource(R.mipmap.switch_select);
            } else {
                imageView_uart_enable.setImageResource(R.mipmap.switch_nor);
            }
//            if (CtvCommonManager.getInstance().getEnvironment("autotest").equals("on")){
//                img_ttys0.setImageResource(R.mipmap.switch_select);
//            } else {
//                img_ttys0.setImageResource(R.mipmap.switch_nor);
//            }
        } catch (CtvCommonException e) {
            e.printStackTrace();
        }




        watchDogMode = factoryManager.getWatchDogMode();

        if (watchDogMode == 1) {
            imageViewWatchDog.setImageResource(R.mipmap.switch_select);
        } else {
            imageViewWatchDog.setImageResource(R.mipmap.switch_nor);
        }

        relativeLayout_uart_enable.setOnClickListener(this);
        relativeLayout_rs232_command.setOnClickListener(this);
        relativeLayout_watch_dog.setOnClickListener(this);
        relativeLayout_ttys0.setOnClickListener(this);
        relativeLayout_aging_mode.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_uart_enable:
                try {
                    String controlStr = "on".equals(CtvCommonManager
                            .getInstance().getEnvironment("CULTRAVIEW_PRINT")) ? "off"
                            : "on";
                    CtvCommonManager.getInstance().setEnvironment("CULTRAVIEW_PRINT",
                            controlStr);
                    LogUtils.d("command-->" + controlStr);
                    if (controlStr.equals("on")) {
                        imageView_uart_enable.setImageResource(R.mipmap.switch_select);
                        imageView_rs232_command.setImageResource(R.mipmap.switch_nor);
                        CtvCommonManager.getInstance().setEnvironment("CENTER_CTL",
                                "off");
                    } else {
                        imageView_uart_enable.setImageResource(R.mipmap.switch_nor);
                        LogUtils.d("factory mode"+CtvTvManager.getInstance().getEnvironment("factory_mode"));
                        if (CtvTvManager.getInstance().getEnvironment("factory_mode").equals("0")){
                            CtvCommonManager.getInstance().setEnvironment("CENTER_CTL",
                                    "on");
                            imageView_rs232_command.setImageResource(R.mipmap.switch_select);
                        }
                    }
                } catch (CtvCommonException e1) {
                    LogUtils.e("CtvCommonException:" + e1);
                }
                break;

            case R.id.rl_rs232_command:
                try {
                    String uartEnable = "on".equals(CtvCommonManager
                            .getInstance().getEnvironment("CENTER_CTL")) ? "off"
                            : "on";
                    CtvCommonManager.getInstance().setEnvironment("CENTER_CTL",
                            uartEnable);
                    LogUtils.d("command-->" + uartEnable);
                    if (uartEnable.equals("on")) {
                        imageView_rs232_command.setImageResource(R.mipmap.switch_select);
                        imageView_uart_enable.setImageResource(R.mipmap.switch_nor);
                        img_ttys0.setImageResource(R.mipmap.switch_nor);
                        CtvCommonManager.getInstance().setEnvironment("CULTRAVIEW_PRINT",
                                "off");
                    } else {
                        imageView_rs232_command.setImageResource(R.mipmap.switch_nor);
                       if (CtvTvManager.getInstance().getEnvironment("factory_mode").equals("0")){
                           CtvCommonManager.getInstance().setEnvironment("CULTRAVIEW_PRINT",
                                   "on");
                           imageView_uart_enable.setImageResource(R.mipmap.switch_select);
                       }


                    }
                } catch (Exception e) {
                    LogUtils.e("Exception:" + e);
                }
                break;

            case R.id.rl_ttys0:
                LogUtils.d("ttys0 onClick");
                try {
                    String controlStr = "on".equals(CtvCommonManager
                            .getInstance().getEnvironment("autotest")) ? "off"
                            : "on";
                    CtvCommonManager.getInstance().setEnvironment("autotest",
                            controlStr);
                    LogUtils.d("command-->" + controlStr);
                    if (controlStr.equals("on")) {
                        imageView_rs232_command.setImageResource(R.mipmap.switch_nor);
                        imageView_uart_enable.setImageResource(R.mipmap.switch_nor);
                        img_ttys0.setImageResource(R.mipmap.switch_select);
                        CtvCommonManager.getInstance().setEnvironment("CENTER_CTL",
                                "off");
                        CtvCommonManager.getInstance().setEnvironment("CULTRAVIEW_PRINT",
                                "off");
                    } else {
                        img_ttys0.setImageResource(R.mipmap.switch_nor);
                    }
                } catch (CtvCommonException e1) {
                    LogUtils.e("CtvCommonException:" + e1);
                }
                break;



            case R.id.rl_watch_dog:
                if (watchDogMode == 0) {
                    watchDogMode = 1;
                    factoryManager.setWatchDogMode(watchDogMode);
                    imageViewWatchDog.setImageResource(R.mipmap.switch_select);
                } else {
                    watchDogMode = 0;
                    factoryManager.setWatchDogMode(watchDogMode);
                    imageViewWatchDog.setImageResource(R.mipmap.switch_nor);
                }
                break;

            case R.id.rl_aging_mode:
                startBurningService();
                break;
        }
    }

    public void startBurningService() {
        Intent intent = new Intent(
                "com.mstar.tv.tvplayer.ui.intent.action.RootActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
        getActivity().finish();
        try {
            CtvTvManager.getInstance().setEnvironment("factory_burningmode", "1");
        } catch (CtvCommonException e) {
            e.printStackTrace();
        }
        getActivity().startService(new Intent(getActivity(), MyBurningService.class));
        // showInfo();

        LogUtils.d("com.mstar.factory.BurningStrat");
    }
    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {

        }
    }
}
