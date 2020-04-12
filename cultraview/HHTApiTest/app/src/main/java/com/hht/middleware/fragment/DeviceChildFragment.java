package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.device.HHTDeviceManager;
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
import java.util.Map;

/**
 * Author: chenhu
 * Time: 2019/12/19 15:41
 * Description do somethings
 */
public class DeviceChildFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTDeviceManager mHHTDeviceManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTDeviceManager = HHTDeviceManager.getInstance();//(HHTDeviceManager) mActivity.getSystemService(HHTDeviceManager.SERVICE);
    }

    public static DeviceChildFragment newInstance() {
        Bundle args = new Bundle();
        DeviceChildFragment fragment = new DeviceChildFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        setTitleTextVew(getString(R.string.fragment_device_child_title));
        mListView = findViewById(R.id.fragment_device_child_list_view);
        mDetailsAdapter = new DetailsAdapter(mActivity, getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_child_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(DeviceFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog = new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(240);
        introduceDialog.show(getString(R.string.fragment_device_child_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mListData = new ArrayList<>();
        mListData.add(new DetailsBean("writeCmdStrToTVOS(int iCmd,String strData)",
                "往tvOS传输strCmd参数控制命令"));

        mListData.add(new DetailsBean("readCmdStrToTVOS(int iCmd)",
                "根据strCmd参数控制命令读取tvOS传输数据"));

        mListData.add(new DetailsBean("getTempSensorValue()",
                "获取温度值"));

        mListData.add(new DetailsBean("getLightSensorValue()",
                "环境光亮强度"));

        mListData.add(new DetailsBean("setBacklightOff(boolean bOnOff)",
                "开关背光使能:关背光，息屏"));

        mListData.add(new DetailsBean("isBacklightOff()",
                "开光背光状态"));

        mListData.add(new DetailsBean("setBrightnessValue(int iVal)",
                "亮度设置"));

        mListData.add(new DetailsBean("getBrightnessValue()",
                "亮度获取"));

        mListData.add(new DetailsBean("setBrightnessValueForThirdPartyApp(int iVal)",
                "第三方APP背光亮度设定"));

        mListData.add(new DetailsBean("getBrightnessValueForThirdPartyApp()",
                "获取第三方APP背光亮度值"));

        mListData.add(new DetailsBean("setBrightnessMode(String strMode)",
                "设置背光模式"));

        mListData.add(new DetailsBean("getBrightnessMode()",
                "获取背光模式"));

        mListData.add(new DetailsBean("getBrightnessMap()",
                "获取背光所有模式的列表map"));

        return mListData;
    }

    private List<ModeBean> getBrightnessModeDataList() {
        List<ModeBean> mListData = new ArrayList<>();
        mListData.add(new ModeBean("MODE_BL_STANDARD"
                , HHTDeviceManager.MODE_BL_STANDARD));

        mListData.add(new ModeBean("MODE_BL_CUS"
                , HHTDeviceManager.MODE_BL_CUS));

        mListData.add(new ModeBean("MODE_BL_AUTO"
                , HHTDeviceManager.MODE_BL_AUTO));

        mListData.add(new ModeBean("MODE_BL_ECO"
                , HHTDeviceManager.MODE_BL_ECO));

        return mListData;
    }

    private List<ModeBean> getBackLight() {
        List<ModeBean> mListData = new ArrayList<>();
        mListData.add(new ModeBean("true"
                , 1));

        mListData.add(new ModeBean("false"
                , 2));

        return mListData;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                if (mHHTDeviceManager != null) {
                    boolean mDeviceInfo = mHHTDeviceManager.writeCmdStrToTVOS("test");
                    ToastUtils.showShortToast("writeCmdStrToTVOS是否成功==" + mDeviceInfo);
                }
                // HHTDeviceManager.getInstance().writeCmdStrToTVOS(1, "");
                break;
            case 1:
                if (mHHTDeviceManager != null) {
                    String mDeviceInfo = mHHTDeviceManager.readCmdStrToTVOS(1);
                    ToastUtils.showShortToast("readCmdStrToTVOS==" + mDeviceInfo);
                }
                // HHTDeviceManager.getInstance().readCmdStrToTVOS(1);
                break;
            case 2:
                if (mHHTDeviceManager != null) {
                    int mDeviceInfo = mHHTDeviceManager.getTempSensorValue();
                    ToastUtils.showShortToast("getTempSensorValue==" + mDeviceInfo);
                }
                // HHTDeviceManager.getInstance().getTempSensorValue();
                break;
            case 3:
                if (mHHTDeviceManager != null) {
                    int mDeviceInfo = mHHTDeviceManager.getLightSensorValue();
                    ToastUtils.showShortToast("getLightSensorValue==" + mDeviceInfo);
                }
                // HHTDeviceManager.getInstance().getLightSensorValue();
                break;
            case 4:
                showDialog(getBackLight(), "setBacklightOff", 2,position);

                // HHTDeviceManager.getInstance().setBacklightOff(true);
                break;
            case 5:

                if (mHHTDeviceManager != null) {
                    boolean mDeviceInfo = mHHTDeviceManager.isBacklightOff();
                    ToastUtils.showShortToast("isBacklightOff==" + mDeviceInfo);
                }
                // HHTDeviceManager.getInstance().isBacklightOff();
                break;
            case 6:
                if (mHHTDeviceManager != null) {
                    boolean mDeviceInfo = mHHTDeviceManager.setBrightnessValue(1);
                    ToastUtils.showShortToast("setBrightnessValue是否成功==" + mDeviceInfo);
                }
                // HHTDeviceManager.getInstance().setBrightnessValue(1);
                break;
            case 7:
                if (mHHTDeviceManager != null) {
                    int mDeviceInfo = mHHTDeviceManager.getBrightnessValue();
                    ToastUtils.showShortToast("getBrightnessValue==" + mDeviceInfo);
                }
                // HHTDeviceManager.getInstance().getBrightnessValue();
                break;
            case 8:
                if (mHHTDeviceManager != null) {
                    boolean mDeviceInfo = mHHTDeviceManager.setBrightnessValueForThirdPartyApp(100);
                    ToastUtils.showShortToast("setBrightnessValueForThirdPartyApp是否成功==" + mDeviceInfo);
                }
                // HHTDeviceManager.getInstance().setBrightnessValueForThirdPartyApp(1);
                break;
            case 9:
                if (mHHTDeviceManager != null) {
                    int mDeviceInfo = mHHTDeviceManager.getBrightnessValueForThirdPartyApp();
                    ToastUtils.showShortToast("getBrightnessValueForThirdPartyApp==" + mDeviceInfo);
                }
                // HHTDeviceManager.getInstance().getBrightnessValueForThirdPartyApp();
                break;
            case 10:
                showDialog(getBrightnessModeDataList(), "setBrightnessMode", 2,position);
                break;
            case 11:
                if (mHHTDeviceManager != null) {
                    String mDeviceInfo = mHHTDeviceManager.getBrightnessMode();
                    ToastUtils.showShortToast("getBrightnessMode==" + mDeviceInfo);
                }
                // HHTDeviceManager.getInstance().getBrightnessMode();
                break;
            case 12:
                if (mHHTDeviceManager != null) {
                    Map<String, String> mDeviceInfo = mHHTDeviceManager.getBrightnessMap();
                    if (mDeviceInfo != null && mDeviceInfo.size() > 0) {
                        ToastUtils.showShortToast("getBrightnessMap==" + mDeviceInfo.size());
                    }
                }
                // HHTDeviceManager.getInstance().getBrightnessMap();
                break;
        }
    }

    private void showDialog(final List<ModeBean> mListData, String title, int numColumns,final int positionType) {
        showMiddleDialog(mListData, title, numColumns, new OnMiddleDialogListener() {
            @Override
            public void onItemClick(int position) {
//                ToastUtils.showShortToast(mListData.get(position).getModeName()
//                        + "    typeStr==" + mListData.get(position).getTypeStr()
//                        + "     typeInt==" + mListData.get(position).getTypeInt());
                switch (positionType){
                    case 4:
                        if(mHHTDeviceManager!=null) {
                            int typeStr = mListData.get(position).getTypeInt();
                            boolean mDeviceInfo=mHHTDeviceManager.setBacklightOff(typeStr==0);
                            ToastUtils.showShortToast("setBacklightOff=="+mDeviceInfo);
                        }
                        break;
                    case 10:
                        if(mHHTDeviceManager!=null) {
                            String typeStr = mListData.get(position).getTypeStr();
                            boolean mDeviceInfo=mHHTDeviceManager.setBrightnessMode(typeStr);
                            ToastUtils.showShortToast("setBrightnessMode是否成功=="+mDeviceInfo);
                        }
                        break;
                }

                // HHTDeviceManager.getInstance().setBrightnessMode(mListData.get(position).getTypeStr());
            }
        });
    }
}
