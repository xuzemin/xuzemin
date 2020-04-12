package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.device.HHTTvEventListener;
import com.hht.middleware.R;
import com.hht.middleware.adapter.DetailsAdapter;
import com.hht.middleware.base.BaseFragment;
import com.hht.middleware.bean.DetailsBean;
import com.hht.middleware.bean.ModeBean;
import com.hht.middleware.dialog.IntroduceDialog;
import com.hht.middleware.listener.OnMiddleDialogListener;
import com.hht.middleware.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: chenhu
 * Time: 2019/12/13 9:59
 * Description do somethings
 */
public class CommonFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTCommonManager mHHTCommonManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTCommonManager = HHTCommonManager.getInstance();// (HHTCommonManager) mActivity.getSystemService(HHTCommonManager.SERVICE);
    }

    public static CommonFragment newInstance() {
        Bundle args = new Bundle();
        CommonFragment fragment = new CommonFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        setTitleTextVew(getString(R.string.fragment_device_common_title));
        mListView = findViewById(R.id.fragment_common_list_view);
        mDetailsAdapter = new DetailsAdapter(mActivity, getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);


    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_common_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(DeviceFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog = new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(280);
        introduceDialog.show(getString(R.string.fragment_common_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mListData = new ArrayList<>();

        mListData.add(new DetailsBean("setEyeProtectionMode(int mode)", "设置护眼模式"));
        mListData.add(new DetailsBean("getEyeProtectionMode", "获取护眼模式"));

        mListData.add(new DetailsBean("setNoSignalEnable(boolean bStatus)", "设置无信号操作使能"));
        mListData.add(new DetailsBean("getNoSignalEnable", "无信号操作使能"));

        mListData.add(new DetailsBean("setNoSignalTime(int iTime)", "设置无信号操作时间"));
        mListData.add(new DetailsBean("getNoSignalTime", "获取无信号操作时间 "));

        mListData.add(new DetailsBean("setNoEventEnable(boolean bStatus)", "设置无操作事件使能开关 "));
        mListData.add(new DetailsBean("getNoEventEnable", "获取无操作事件使能开关  "));

        mListData.add(new DetailsBean("setNoEventTime(int iTime)", "设置无操作事件时间   "));
        mListData.add(new DetailsBean("getNoEventTime", "获取无操作事件时间  "));

        mListData.add(new DetailsBean("setSleepModeEnable(boolean bStatus)", "设置睡眠模式使能开关"));
        mListData.add(new DetailsBean("getSleepModeEnable()", "获取睡眠模式使能开关"));

        mListData.add(new DetailsBean("setSleepModeTime(int iTime)", "设置睡眠模式时间设置"));
        mListData.add(new DetailsBean("getSleepModeTime()", "获取睡眠模式时间"));

        mListData.add(new DetailsBean("startSystemSleep(boolean bMode)", "启动睡眠模式"));
        mListData.add(new DetailsBean("isSystemSleep()", "获取睡眠模式启动状态"));

        mListData.add(new DetailsBean("setShowTempEnable(boolean bStatus)", "设置温度检测使能开关"));
        mListData.add(new DetailsBean("getShowTempEnable()", "获取温度检测使能开关"));

        mListData.add(new DetailsBean("setShowTempMode(int iMode)", "设置温度显示模式"));
        mListData.add(new DetailsBean("getShowTempMode()", "获取温度显示模式"));

        mListData.add(new DetailsBean("setNoAndroidStatus(boolean bType)", "设置无Android系统模式状态"));
        mListData.add(new DetailsBean("isNoAndroidStatus()", "获取无Android模式状态 "));

        mListData.add(new DetailsBean("setSystemVitrualStandby(boolean bMode)", "系统假待机设置"));
        mListData.add(new DetailsBean("isSystemVitrualStandby()", "获取系统虚拟待机状态 "));

        mListData.add(new DetailsBean("setScreenSaverEnable(boolean iVal)", "设置屏保使能"));
        mListData.add(new DetailsBean("getScreenSaverEnable()", "获取屏保使能"));

        mListData.add(new DetailsBean("setScreenSaverTime(int iVal)", "设置启动屏保的时间"));
        mListData.add(new DetailsBean("getScreenSaverTime", "获取启动屏保的时间"));

        mListData.add(new DetailsBean("startScreenSaver()", "启动屏保"));
        mListData.add(new DetailsBean("isSupportHDMITx()", "是否支持HDMI OUT输出"));

        mListData.add(new DetailsBean("getSupportHDMITxList()", "获取Hdmi输出显示支持列表"));

        mListData.add(new DetailsBean("setHdmiTxMode()", "设置当前Hdmi输出分辨率模式"));
        mListData.add(new DetailsBean("getCurHdmiTxMode()", "获取当前Hdmi输出分辨率模式"));

        mListData.add(new DetailsBean("StandbyMode()", "进入假待机"));
        mListData.add(new DetailsBean("getStandbyMode()", "查询待机模式"));
        mListData.add(new DetailsBean("setStandbyMode()", "设置待机模式"));
        mListData.add(new DetailsBean("setOnTvEventLister()", "监听"));
        mListData.add(new DetailsBean("setSourceAutoStart()", "自动唤醒开关"));
        mListData.add(new DetailsBean("isSourceAutoStart()", "唤醒开关状态"));
        mListData.add(new DetailsBean("getBlackBoard()", "黑板待机延时时间"));
        mListData.add(new DetailsBean("setBlackBoard() = 5", "设置黑板待机延时时间"));
        mListData.add(new DetailsBean("isBlackBoardEnable()", "黑板待机状态"));
        mListData.add(new DetailsBean("setBlackBoardEnable()", "黑板待机开关"));
        mListData.add(new DetailsBean("getScreenTemThreshold()", "获取屛温阈值"));
        mListData.add(new DetailsBean("setScreenTmpThreshold()", "设置屛温阈值"));

        return mListData;
    }


    private List<ModeBean> getScreenTemThreshold() {
        List<ModeBean> mList = new ArrayList<>();

        mList.add(new ModeBean("50",
                50));

        mList.add(new ModeBean("60",
                60));

        return mList;
    }


    private List<ModeBean> getSourceAutoStart() {
        List<ModeBean> mList = new ArrayList<>();

        mList.add(new ModeBean("TRUE",
                0));

        mList.add(new ModeBean("FASLE ",
                1));

        return mList;
    }

    private List<ModeBean> getEyeProtectionMode() {
        List<ModeBean> mList = new ArrayList<>();

        mList.add(new ModeBean("EYE_OFF",
                HHTCommonManager.EnumEyeProtectionMode.EYE_OFF.ordinal()));

        mList.add(new ModeBean("EYE_DIMMING ",
                HHTCommonManager.EnumEyeProtectionMode.EYE_DIMMING.ordinal()));

        mList.add(new ModeBean("EYE_RGB",
                HHTCommonManager.EnumEyeProtectionMode.EYE_RGB.ordinal()));
        mList.add(new ModeBean("EYE_WRITE_PROTECT",
                HHTCommonManager.EnumEyeProtectionMode.EYE_WRITE_PROTECT.ordinal()));

        return mList;
    }

    private List<ModeBean> getShowTempMod() {
        List<ModeBean> mList = new ArrayList<>();

        mList.add(new ModeBean("EnumTempMode.TEMP_C",
                HHTCommonManager.EnumTempMode.TEMP_C.ordinal()));

        mList.add(new ModeBean("EnumTempMode.TEMP_F",
                HHTCommonManager.EnumTempMode.TEMP_F.ordinal()));

        return mList;
    }

    private List<ModeBean> getStandbyByMode() {
        List<ModeBean> mList = new ArrayList<>();

        mList.add(new ModeBean("正常待机"
                , 0));

        mList.add(new ModeBean("假待机"
                , 1));

        mList.add(new ModeBean("黑板待机"
                , 2));

        return mList;
    }



    private List<ModeBean> getHdmiTxMode() {
        List<ModeBean> mList = new ArrayList<>();

        mList.add(new ModeBean("EnumHdmiOutMode.AUTO"
                , HHTCommonManager.EnumHdmiOutMode.AUTO.ordinal()));

        mList.add(new ModeBean("EnumHdmiOutMode.HDMITX_2k_60"
                , HHTCommonManager.EnumHdmiOutMode.HDMITX_2k_60.ordinal()));

        mList.add(new ModeBean("EnumHdmiOutMode.HDMITX_4k_60"
                , HHTCommonManager.EnumHdmiOutMode.HDMITX_4k_60.ordinal()));

        mList.add(new ModeBean("EnumHdmiOutMode.HDMITX_720p_60"
                , HHTCommonManager.EnumHdmiOutMode.HDMITX_720p_60.ordinal()));

        return mList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                showDialog(getEyeProtectionMode(), "setEyeProtectionMode", 4, position);
                break;
            case 1:
                if (mHHTCommonManager != null) {
                    int commonInfo = mHHTCommonManager.getEyeProtectionMode();
                    ToastUtils.showShortToast("getEyeProtectionMode==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getEyeProtectionMode();
                break;
            case 2:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.setNoSignalEnable(true);
                    ToastUtils.showShortToast("setNoSignalEnable是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().setNoSignalEnable(true);
                break;
            case 3:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.getNoSignalEnable();
                    ToastUtils.showShortToast("getNoSignalEnable==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getNoSignalEnable();
                break;
            case 4:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.setNoSignalTime(100);
                    ToastUtils.showShortToast("setNoSignalTime是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().setNoSignalTime(1);
                break;
            case 5:
                if (mHHTCommonManager != null) {
                    int commonInfo = mHHTCommonManager.getNoSignalTime();
                    ToastUtils.showShortToast("getNoSignalTime==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getNoSignalTime();
                break;
            case 6:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.setNoEventEnable(true);
                    ToastUtils.showShortToast("setNoEventEnable是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().setNoEventEnable(true);
                break;
            case 7:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.getNoEventEnable();
                    ToastUtils.showShortToast("getNoEventEnable==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getNoEventEnable();
                break;
            case 8:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.setNoEventTime(1);
                    ToastUtils.showShortToast("setNoEventTime是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().setNoEventTime(1);
                break;
            case 9:
                if (mHHTCommonManager != null) {
                    int commonInfo = mHHTCommonManager.getNoEventTime();
                    ToastUtils.showShortToast("getNoEventTime==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getNoEventTime();
                break;
            case 10:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.setSleepModeEnable(true);
                    ToastUtils.showShortToast("setSleepModeEnable是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().setSleepModeEnable(true);
                break;
            case 11:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.getSleepModeEnable();
                    ToastUtils.showShortToast("getSleepModeEnable==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getSleepModeEnable();
                break;
            case 12:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.setSleepModeTime(12);
                    ToastUtils.showShortToast("setSleepModeTime是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().setSleepModeTime(1);
                break;
            case 13:
                if (mHHTCommonManager != null) {
                    int commonInfo = mHHTCommonManager.getSleepModeTime();
                    ToastUtils.showShortToast("getSleepModeTime==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getSleepModeTime();
                break;
            case 14:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.startSystemSleep(true);
                    ToastUtils.showShortToast("startSystemSleep是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().startSystemSleep(true);
                break;
            case 15:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.isSystemSleep();
                    ToastUtils.showShortToast("isSystemSleep==" + commonInfo);
                }
                // HHTCommonManager.getInstance().isSystemSleep();
                break;

            case 16:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.setShowTempEnable(true);
                    ToastUtils.showShortToast("setShowTempEnable是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().setShowTempEnable(true);
                break;
            case 17:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.getShowTempEnable();
                    ToastUtils.showShortToast("getShowTempEnable==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getShowTempEnable();
                break;
            case 18:
                showDialog(getShowTempMod(), "setShowTempMode", 2, position);
                break;
            case 19:
                if (mHHTCommonManager != null) {
                    int commonInfo = mHHTCommonManager.getShowTempMode();
                    ToastUtils.showShortToast("getShowTempMode==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getShowTempMode();
                break;
            case 20:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.setNoAndroidStatus(true);
                    ToastUtils.showShortToast("setNoAndroidStatus是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().setNoAndroidStatus(true);
                break;
            case 21:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.isNoAndroidStatus();
                    ToastUtils.showShortToast("isNoAndroidStatus==" + commonInfo);
                }
                // HHTCommonManager.getInstance().isNoAndroidStatus();
                break;
            case 22:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.setSystemVitrualStandby(true);
                    ToastUtils.showShortToast("setSystemVitrualStandby是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().setSystemVitrualStandby(true);
                break;
            case 23:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.isSystemVitrualStandby();
                    ToastUtils.showShortToast("isSystemVitrualStandby==" + commonInfo);
                }
                // HHTCommonManager.getInstance().isSystemVitrualStandby();
                break;
            case 24:
                if (mHHTCommonManager != null) {
                    //boolean commonInfo = mHHTCommonManager.setScreenSaverEnable(true);
                    //ToastUtils.showShortToast("setScreenSaverEnable是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().setScreenSaverEnable(true);
                break;
            case 25:
                if (mHHTCommonManager != null) {
                    //boolean commonInfo = mHHTCommonManager.getScreenSaverEnable();
                    //ToastUtils.showShortToast("getScreenSaverEnable==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getScreenSaverEnable();
                break;
            case 26:
                if (mHHTCommonManager != null) {
                    //boolean commonInfo = mHHTCommonManager.setScreenSaverTime(1);
                    //ToastUtils.showShortToast("setScreenSaverTime是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().setScreenSaverTime(1);
                break;
            case 27:
                if (mHHTCommonManager != null) {
                    //int commonInfo = mHHTCommonManager.getScreenSaverTime();
                    //ToastUtils.showShortToast("getScreenSaverTime==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getScreenSaverTime();
                break;
            case 28:
                if (mHHTCommonManager != null) {
                    //boolean commonInfo = mHHTCommonManager.startScreenSaver();
                    //ToastUtils.showShortToast("startScreenSaver是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().startScreenSaver();
                break;
            case 29:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.isSupportHDMITx();
                    ToastUtils.showShortToast("isSupportHDMITx是否成功==" + commonInfo);
                }
                // HHTCommonManager.getInstance().isSupportHDMITx();
                break;
            case 30:
                if (mHHTCommonManager != null) {
                    List<Integer> commonInfo = mHHTCommonManager.getSupportHDMITxList();
                    if (commonInfo != null && commonInfo.size() > 0) {
                        ToastUtils.showShortToast("getSupportHDMITxList==" + commonInfo.size());
                    }
                }
                // HHTCommonManager.getInstance().getSupportHDMITxList();
                break;
            case 31:
                showDialog(getHdmiTxMode(), "setHdmiTxMode", 2, position);
                break;
            case 32:
                if (mHHTCommonManager != null) {
                    int commonInfo = mHHTCommonManager.getCurHdmiTxMode();
                    ToastUtils.showShortToast("getCurHdmiTxMode==" + commonInfo);
                }
                // HHTCommonManager.getInstance().getCurHdmiTxMode();
                break;
            case 33:
                if (mHHTCommonManager != null) {
                    showDialog(getStandbyByMode(), "standbyByMode", 3, position);
                }
                // HHTCommonManager.getInstance().getCurHdmiTxMode();
                break;
            case 34:
                if (mHHTCommonManager != null) {
                    int commonInfo = mHHTCommonManager.getStandbyMode();
                    ToastUtils.showShortToast("getStandbyMode==" + commonInfo);
                }
                break;
            case 35:
                if (mHHTCommonManager != null) {
                    showDialog(getStandbyByMode(), "setStandbyMode", 3, position);
                }
                // HHTCommonManager.getInstance().getCurHdmiTxMode();
                break;
            case 36:
                if (mHHTCommonManager != null) {
                    mHHTCommonManager.registerHHTTvEventListener(hhtTvEventListener);
//
                    ToastUtils.showShortToast("setOnTvEventListener==");
//                    showDialog(getStandbyByMode(), "setStandbyMode", 3, position);
                }
                // HHTCommonManager.getInstance().getCurHdmiTxMode();
                break;
            case 37:
                    showDialog(getSourceAutoStart(), "setAutoWakeupBySourceEnable", 2, position);
                break;
            case 38:
                if (mHHTCommonManager != null) {
                    boolean commonInfo = mHHTCommonManager.isAutoWakeupBySourceEnable();
                    ToastUtils.showShortToast("isAutoWakeupBySourceEnable=="+commonInfo);
//                    showDialog(getStandbyByMode(), "setStandbyMode", 3, position);
                }
                // HHTCommonManager.getInstance().getCurHdmiTxMode();
                break;
            case 39:
                if (mHHTCommonManager != null) {
                    int commonTime = mHHTCommonManager.getBlackBoardTime();
                    ToastUtils.showShortToast("getBlackBoardTime=="+commonTime);
//                    showDialog(getStandbyByMode(), "setStandbyMode", 3, position);
                }
                // HHTCommonManager.getInstance().getCurHdmiTxMode();
                break;
            case 40:
//                if (mHHTCommonManager != null) {
//                    mHHTCommonManager.setBlackBoardTime(5);
//                    ToastUtils.showShortToast("setBlackBoardTime=="+5);
////                    showDialog(getStandbyByMode(), "setStandbyMode", 3, position);
//                }
                // HHTCommonManager.getInstance().getCurHdmiTxMode();

                showDialog(getSourceAutoStart(), "setBlackBoardTime", 2, position);
                break;
            case 41:
                if (mHHTCommonManager != null) {
                    boolean commonTime = mHHTCommonManager.isBlackBoardEnabled();
                    ToastUtils.showShortToast("isBlackBoardEnabled=="+commonTime);
//                    showDialog(getStandbyByMode(), "setStandbyMode", 3, position);
                }
                // HHTCommonManager.getInstance().getCurHdmiTxMode();
                break;
            case 42:
                    showDialog(getSourceAutoStart(), "setBlackBoardEnable", 2, position);
                // HHTCommonManager.getInstance().getCurHdmiTxMode();
                break;
            case 43:
                if (mHHTCommonManager != null) {
                    int commonTime = mHHTCommonManager.getScreenTmpThreshold();
                    ToastUtils.showShortToast("getScreenTmpThreshold=="+commonTime);
//                    showDialog(getStandbyByMode(), "setStandbyMode", 3, position);
                }
                // HHTCommonManager.getInstance().getCurHdmiTxMode();
                break;
            case 44:
                showDialog(getScreenTemThreshold(), "setScreenTemp", 2, position);
                // HHTCommonManager.getInstance().getCurHdmiTxMode();
                break;
        }


    }

    private HHTTvEventListener hhtTvEventListener = new HHTTvEventListener() {
        @Override
        public void onTvEvent(int i, int i1, int i2, Object o) {
            Log.e("getevent","i = " + i);
            Log.e("getevent","i1 = " + i1);
            Log.e("getevent","i2 = " + i2);
//            Log.e("getevent","Object = " + o.getClass());
        }
    };

    private void showDialog(final List<ModeBean> mListData, String title, int numColumns, final int positionType) {
        showMiddleDialog(mListData, title, numColumns, new OnMiddleDialogListener() {
            @Override
            public void onItemClick(int position) {
                switch (positionType) {
                    case 0:
                        if (mHHTCommonManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean commonInfo = mHHTCommonManager.setEyeProtectionMode(typeInt);
                            ToastUtils.showShortToast("setEyeProtectionMode是否成功==" + commonInfo);
                        }
                        break;
                    case 18:
                        if (mHHTCommonManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean commonInfo = mHHTCommonManager.setShowTempMode(typeInt);
                            ToastUtils.showShortToast("setShowTempMode是否成功==" + commonInfo);
                        }
                        break;
                    case 31:
                        if (mHHTCommonManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean commonInfo = mHHTCommonManager.setHdmiTxMode(typeInt);
                            ToastUtils.showShortToast("setHdmiTxMode是否成功==" + commonInfo);
                        }
                        break;
                    case 33:
                        if (mHHTCommonManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean commonInfo = mHHTCommonManager.standbyByMode(typeInt);
                            ToastUtils.showShortToast("standbyByMode是否成功==" + commonInfo);
                        }
                        break;
                    case 35:
                        if (mHHTCommonManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean commonInfo = mHHTCommonManager.setStandbyMode(typeInt);
                            ToastUtils.showShortToast("setStandbyMode是否成功==" + commonInfo);
                        }
                        break;
                    case 37:
                        if (mHHTCommonManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            mHHTCommonManager.setAutoWakeupBySourceEnable(typeInt == 0);
                            ToastUtils.showShortToast("setAutoWakeupBySourceEnable 设置为==" +( typeInt == 0));
                        }
                        break;
                    case 42:
                        if (mHHTCommonManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            mHHTCommonManager.setBlackBoardEnable(typeInt == 0);
                            ToastUtils.showShortToast("setBlackBoardEnable 设置为==" +( typeInt == 0));
                        }
                        break;
                    case 40:
                        if (mHHTCommonManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            mHHTCommonManager.setBlackBoardTime(typeInt);
                            ToastUtils.showShortToast("setBlackBoardTime 设置为==" +typeInt);
                        }
                        break;
                    case 44:
                        if (mHHTCommonManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            mHHTCommonManager.setScreenTmpThreshold(typeInt);
                            ToastUtils.showShortToast("setScreenTmpThreshold 设置为==" +typeInt);
                        }
                        break;
                }
            }
        });
    }
}
