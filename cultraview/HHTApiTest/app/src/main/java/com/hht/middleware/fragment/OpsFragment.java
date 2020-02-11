package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.ops.HHTOpsManager;
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
 * Time: 2019/12/13 10:09
 * Description do somethings
 */
public class OpsFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTOpsManager mHHTOpsManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTOpsManager = HHTOpsManager.getInstance();// (HHTOpsManager) mActivity.getSystemService(HHTOpsManager.SERVICE);
    }

    public static OpsFragment newInstance() {
        Bundle args = new Bundle();
        OpsFragment fragment = new OpsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.fragment_ops_list_view);
        mDetailsAdapter = new DetailsAdapter(getActivity(), getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ops_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog = new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(230);
        introduceDialog.show(getString(R.string.fragment_ops_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mList = new ArrayList<>();
        mList.add(new DetailsBean("setOpsStartMode(String mode)", "设置OPS电脑启动模式"));
        mList.add(new DetailsBean("getOpsStartMode()", "获取OPS电脑启动模式"));

        mList.add(new DetailsBean("setOpsStartEnable(boolean bStatus)", "设置OPS启动使能"));
        mList.add(new DetailsBean("getOpsStartEnable()", "获取OPS启动使能状态"));

        mList.add(new DetailsBean("isOpsOk()", "判断OPS是否已经启动"));
        mList.add(new DetailsBean("isOpsPlugIn()", "判断OPS是否已经插入卡座"));

        mList.add(new DetailsBean("setOpsPowerTurnOn()", "OPS开机"));
        mList.add(new DetailsBean("setOpsPowerTurnOff()", "OPS关机"));
        mList.add(new DetailsBean("setOpsPowerLongPress()", "OPS长按强制关机"));

        mList.add(new DetailsBean("getOpsOs()", "获取OPS操作系统版本"));
        mList.add(new DetailsBean("getOpsCpuModel()", "获取OPS的CPU型号"));

        mList.add(new DetailsBean("getOpsIP()", "获取OPS的IP地址"));
        mList.add(new DetailsBean("getOpsMAC()", "获取OPS的MAC地址"));

        mList.add(new DetailsBean("getOpsDNS()", "获取OPS的DNS"));
        mList.add(new DetailsBean("getOpsMemoryAvailableSize()", "获取Ops可用内存"));

        mList.add(new DetailsBean("getOpsMemoryTotalSize()", "获取Ops总内存"));
        mList.add(new DetailsBean("getOpsHardDiskTotalSize()", "获取OPS的硬盘空间"));

        mList.add(new DetailsBean("getOpsHardDiskAvailableSize()", "获取OPS的可用硬盘空间"));
        mList.add(new DetailsBean("getOpsCpuTemperature()", "获取OPS的CPU温度"));
        return mList;
    }

    private List<ModeBean> getOpsStartModeDataList() {
        List<ModeBean> mList = new ArrayList<>();

        mList.add(new ModeBean("MODE_OPS_ONLY",
                HHTOpsManager.MODE_OPS_ONLY));
        mList.add(new ModeBean("MODE_OPS_ANY_CHANNEL",
                HHTOpsManager.MODE_OPS_ANY_CHANNEL));
        mList.add(new ModeBean("MODE_OPS_NONE",
                HHTOpsManager.MODE_OPS_NONE));

        return mList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                showDialog(getOpsStartModeDataList(), "setOpsStartMode", 2);
                break;
            case 1:
                if (mHHTOpsManager != null) {
                    String mOpsInfo = mHHTOpsManager.getOpsStartMode();
                    ToastUtils.showShortToast("getOpsStartMode==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsStartMode();
                break;
            case 2:
                if (mHHTOpsManager != null) {
                    boolean mOpsInfo = mHHTOpsManager.setOpsStartEnable(true);
                    ToastUtils.showShortToast("setOpsStartEnable是否成功==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().setOpsStartEnable(true);
                break;
            case 3:
                if (mHHTOpsManager != null) {
                    boolean mOpsInfo = mHHTOpsManager.getOpsStartEnable();
                    ToastUtils.showShortToast("getOpsStartEnable==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsStartEnable();
                break;
            case 4:
                if (mHHTOpsManager != null) {
                    boolean mOpsInfo = mHHTOpsManager.isOpsOk();
                    ToastUtils.showShortToast("isOpsOk==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().isOpsOk();
                break;
            case 5:
                if (mHHTOpsManager != null) {
                    boolean mOpsInfo = mHHTOpsManager.isOpsPlugIn();
                    ToastUtils.showShortToast("isOpsPlugIn==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().isOpsPlugIn();
                break;
            case 6:
                if (mHHTOpsManager != null) {
                    boolean mOpsInfo = mHHTOpsManager.setOpsPowerTurnOn();
                    ToastUtils.showShortToast("setOpsPowerTurnOn是否成功==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().setOpsPowerTurnOn();
                break;
            case 7:
                if (mHHTOpsManager != null) {
                    boolean mOpsInfo = mHHTOpsManager.setOpsPowerTurnOff();
                    ToastUtils.showShortToast("setOpsPowerTurnOff是否成功==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().setOpsPowerTurnOff();
                break;
            case 8:
                if (mHHTOpsManager != null) {
                    boolean mOpsInfo = mHHTOpsManager.setOpsPowerLongPress();
                    ToastUtils.showShortToast("setOpsPowerLongPress是否成功==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().setOpsPowerLongPress();
                break;
            case 9:
                if (mHHTOpsManager != null) {
                    String mOpsInfo = mHHTOpsManager.getOpsOs();
                    ToastUtils.showShortToast("getOpsOs==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsOs();
                break;
            case 10:
                if (mHHTOpsManager != null) {
                    String mOpsInfo = mHHTOpsManager.getOpsCpuModel();
                    ToastUtils.showShortToast("getOpsCpuModel==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsCpuModel();
                break;
            case 11:
                if (mHHTOpsManager != null) {
                    String mOpsInfo = mHHTOpsManager.getOpsIP();
                    ToastUtils.showShortToast("getOpsIP==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsIP();
                break;
            case 12:
                if (mHHTOpsManager != null) {
                    String mOpsInfo = mHHTOpsManager.getOpsMAC();
                    ToastUtils.showShortToast("getOpsMAC==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsMAC();
                break;
            case 13:
                if (mHHTOpsManager != null) {
                    String mOpsInfo = mHHTOpsManager.getOpsDNS();
                    ToastUtils.showShortToast("getOpsDNS==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsDNS();
                break;
            case 14:
                if (mHHTOpsManager != null) {
                    int mOpsInfo = mHHTOpsManager.getOpsMemoryAvailableSize();
                    ToastUtils.showShortToast("getOpsMemoryAvailableSize==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsMemoryAvailableSize();
                break;
            case 15:
                if (mHHTOpsManager != null) {
                    int mOpsInfo = mHHTOpsManager.getOpsMemoryTotalSize();
                    ToastUtils.showShortToast("getOpsMemoryTotalSize==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsMemoryTotalSize();
                break;
            case 16:
                if (mHHTOpsManager != null) {
                    int mOpsInfo = mHHTOpsManager.getOpsHardDiskTotalSize();
                    ToastUtils.showShortToast("getOpsHardDiskTotalSize==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsHardDiskTotalSize();
                break;
            case 17:
                if (mHHTOpsManager != null) {
                    int mOpsInfo = mHHTOpsManager.getOpsHardDiskAvailableSize();
                    ToastUtils.showShortToast("getOpsHardDiskAvailableSize==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsHardDiskAvailableSize();
                break;
            case 18:
                if (mHHTOpsManager != null) {
                    int mOpsInfo = mHHTOpsManager.getOpsCpuTemperature();
                    ToastUtils.showShortToast("getOpsCpuTemperature==" + mOpsInfo);
                }
                // HHTOpsManager.getInstance().getOpsCpuTemperature();
                break;
        }
    }

    private void showDialog(final List<ModeBean> mListData, String title, int numColumns) {
        showMiddleDialog(mListData, title, numColumns, new OnMiddleDialogListener() {
            @Override
            public void onItemClick(int position) {
//                ToastUtils.showShortToast(mListData.get(position).getModeName()
//                        + "    typeStr==" + mListData.get(position).getTypeStr()
//                        + "     typeInt==" + mListData.get(position).getTypeInt());
                if (mHHTOpsManager != null) {
                    String typeStr = mListData.get(position).getTypeStr();
                    boolean mOpsInfo = mHHTOpsManager.setOpsStartMode(typeStr);
                    ToastUtils.showShortToast("setOpsStartMode是否成功==" + mOpsInfo);
                }

                // HHTOpsManager.getInstance().setOpsStartMode(mListData.get(position).getTypeStr());
            }
        });
    }
}
