package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.source.HHTSourceManager;
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
 * Time: 2019/12/13 10:16
 * Description do somethings
 */
public class SourceFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTSourceManager mHHTSourceManager;

    public static SourceFragment newInstance() {
        Bundle args = new Bundle();
        SourceFragment fragment = new SourceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTSourceManager = HHTSourceManager.getInstance();// (HHTSourceManager) mActivity.getSystemService(HHTSourceManager.SERVICE);
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.fragment_source_list_view);
        mDetailsAdapter = new DetailsAdapter(getActivity(), getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_source_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog dialog = new IntroduceDialog(mActivity);
        dialog.setIntroduceDialogHeight(260);
        dialog.show(getString(R.string.fragment_source_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mList = new ArrayList<>();

        mList.add(new DetailsBean("setInputSrcCustomerNameByKey(String key,String name)  ", "更新信号源名字"));
        mList.add(new DetailsBean("getInputSrcMap()", "获取信号源列表map"));
        mList.add(new DetailsBean("getInputSrcPlugStateMap()", "获取信号源插入状态"));

        mList.add(new DetailsBean("setLastSource(String key)", "保存最近一次打开的source "));
        mList.add(new DetailsBean("getLastSource()", "获取最近一次打开的source"));

        mList.add(new DetailsBean("setRecentTvSource(String key)", "保存最近打开的 TV source "));
        mList.add(new DetailsBean("getRecentTvSource()", "获取最近打开的 TV source "));

        mList.add(new DetailsBean("setCurSource(String key)", "保存当前source"));
        mList.add(new DetailsBean("getCurSource()", "获取当前source"));

        mList.add(new DetailsBean("setPreSource(String key)", "保存预先设置的信号源 "));
        mList.add(new DetailsBean("getPreSource()", "获取预先设置的信号源"));

        mList.add(new DetailsBean("setBootSourceEnable(boolean bStatus)", "设置开机信号源模式使能开关"));
        mList.add(new DetailsBean("getBootSourceEnable()", "获取开机信号源模式使能开关状态"));

        mList.add(new DetailsBean("setBootSourceMode(String mode)", "设置开机信号源模式"));
        mList.add(new DetailsBean("getBootSourceMode()", "获取开机信号源模式"));


        mList.add(new DetailsBean("setSignalLock(boolean bStatus)", "设置是否有信号"));
        mList.add(new DetailsBean("isSignalLock()", "判断是否有信号"));

        mList.add(new DetailsBean("startSourcebyKey(String sourceKey)", "信号源启动"));

//        mList.add(new DetailsBean("getSourcePlugStateByKey(String sourceKey)", "获取信号源是否已经插入"));

        mList.add(new DetailsBean("setSourceDetectionMode(int iMode)", "设置信号插入检测模式"));
        mList.add(new DetailsBean("getSourceDetectionMode() ", "获取信号插入检测模式 "));

        mList.add(new DetailsBean("isTvWindow()", "判断当前窗口处于TV状态 "));
        // mList.add(new DetailsBean("enableFreeze()", "冻屏"));

        //  mList.add(new DetailsBean("disableFreeze()", "解冻屏"));

        mList.add(new DetailsBean("getSourcePlugStateByKey()", "单通道获取状态 "));
        mList.add(new DetailsBean("getInputSrcPlugStateMap()", "全通道状态获取 "));
        mList.add(new DetailsBean("isCurrentSource()", "是否为当前信号源 "));

        return mList;
    }

    private List<ModeBean> getInputSrcCustomerNameByKeyDataList() {
        List<ModeBean> mList = new ArrayList<>();
        mList.add(new ModeBean("DTV", HHTSourceManager.DTV));
        mList.add(new ModeBean("ATV", HHTSourceManager.ATV));
        mList.add(new ModeBean("VGA", HHTConstant.VGA));
        mList.add(new ModeBean("VGA1", HHTSourceManager.VGA1));
        mList.add(new ModeBean("VGA2", HHTSourceManager.VGA2));
        mList.add(new ModeBean("VGA3", HHTSourceManager.VGA3));

        mList.add(new ModeBean("OPS", HHTSourceManager.OPS));
        mList.add(new ModeBean("OPS2", HHTSourceManager.OPS2));
        mList.add(new ModeBean("HDMI1", HHTSourceManager.HDMI1));
        mList.add(new ModeBean("HDMI2", HHTSourceManager.HDMI2));
        mList.add(new ModeBean("HDMI3", HHTSourceManager.HDMI3));
        mList.add(new ModeBean("HDMI4", HHTSourceManager.HDMI4));
        mList.add(new ModeBean("HDMI5", HHTSourceManager.HDMI5));
        mList.add(new ModeBean("HDMI6", HHTSourceManager.HDMI6));

        mList.add(new ModeBean("TYPEC", HHTSourceManager.TYPEC));
        mList.add(new ModeBean("TYPEC2", HHTSourceManager.TYPEC2));
        mList.add(new ModeBean("DP", HHTSourceManager.DP));
        mList.add(new ModeBean("DP2", HHTSourceManager.DP2));
        mList.add(new ModeBean("AV1", HHTSourceManager.AV1));
        mList.add(new ModeBean("AV2", HHTSourceManager.AV2));

        mList.add(new ModeBean("YPBPR1", HHTSourceManager.YPBPR1));
        mList.add(new ModeBean("YPBPR2", HHTSourceManager.YPBPR2));
        mList.add(new ModeBean("ANDROID", HHTSourceManager.ANDROID));
        mList.add(new ModeBean("STORAGE", HHTSourceManager.STORAGE));


        return mList;
    }

    private List<ModeBean> getBootSourceModeDataList() {
        List<ModeBean> mList = new ArrayList<>();
        mList.add(new ModeBean("Mode_Fixed", HHTSourceManager.MODE_FIXED_SOURCE));
        mList.add(new ModeBean("Mode_Last", HHTSourceManager.MODE_LAST_SOURCE));
        return mList;
    }

    private List<ModeBean> getSourceDetectionModeDataList() {
        List<ModeBean> mList = new ArrayList<>();
        mList.add(new ModeBean("DetMode.DET_OFF", HHTSourceManager.EnumSourceDetMode.DET_OFF.ordinal()));
        mList.add(new ModeBean("DetMode.DET_AUTO", HHTSourceManager.EnumSourceDetMode.DET_AUTO.ordinal()));
        mList.add(new ModeBean("DetMode.DET_MANUAL", HHTSourceManager.EnumSourceDetMode.DET_MANUAL.ordinal()));
        return mList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                showDialog(getInputSrcCustomerNameByKeyDataList(), "InputSrcCustomerNameByKey", 3, position);
                break;
            case 1:
                if (mHHTSourceManager != null) {
                    Map<String, String> mSourceVolume = mHHTSourceManager.getInputSrcMap();
                    Log.e("HHTApiTest","mSourceVolume"+mSourceVolume.toString());
                    if (mSourceVolume != null && mSourceVolume.size() > 0) {
                        ToastUtils.showShortToast("getInputSrcMap的size==" + mSourceVolume.size());
                    }
                }
                // HHTSourceManager.getInstance().getInputSrcMap();
                break;
            case 2:
                if (mHHTSourceManager != null) {
                    Map<String, String> mSourceVolume = mHHTSourceManager.getInputSrcPlugStateMap();
                    if (mSourceVolume != null && mSourceVolume.size() > 0) {
                        ToastUtils.showShortToast("getInputSrcPlugStateMap的size==" + mSourceVolume.size());
                    }
                }
                //  HHTSourceManager.getInstance().getInputSrcPlugStateMap();
                break;
            case 3:
                showDialog(getInputSrcCustomerNameByKeyDataList(), "setLastSource", 3, position);
                break;
            case 4:
                if (mHHTSourceManager != null) {
                    String mSource = mHHTSourceManager.getLastSource();
                    ToastUtils.showShortToast("getLastSource==" + mSource);
                }
                // HHTSourceManager.getInstance().getLastSource();
                break;
            case 5:
                showDialog(getInputSrcCustomerNameByKeyDataList(), "setRecentTvSource", 3, position);
                break;
            case 6:
                if (mHHTSourceManager != null) {
                    String mSource = mHHTSourceManager.getRecentTvSource();
                    ToastUtils.showShortToast("getRecentTvSource==" + mSource);
                }
                // HHTSourceManager.getInstance().getRecentTvSource();
                break;
            case 7:
                showDialog(getInputSrcCustomerNameByKeyDataList(), "setCurSource", 3, position);
                break;
            case 8:
                if (mHHTSourceManager != null) {
                    String mSource = mHHTSourceManager.getCurSource();
                    ToastUtils.showShortToast("getCurSource==" + mSource);
                }
                // HHTSourceManager.getInstance().getCurSource();
                break;
            case 9:
                showDialog(getInputSrcCustomerNameByKeyDataList(), "setPreSource", 3, position);
                break;
            case 10:
                if (mHHTSourceManager != null) {
                    String mSource = mHHTSourceManager.getPreSource();
                    ToastUtils.showShortToast("getPreSource==" + mSource);
                }
                // HHTSourceManager.getInstance().getPreSource();
                break;
            case 11:
                if (mHHTSourceManager != null) {
                    boolean mSource = mHHTSourceManager.setBootSourceEnable(true);
                    ToastUtils.showShortToast("setBootSourceEnable是否成功==" + mSource);
                }
                // HHTSourceManager.getInstance().setBootSourceEnable(true);
                break;
            case 12:
                if (mHHTSourceManager != null) {
                    boolean mSource = mHHTSourceManager.getBootSourceEnable();
                    ToastUtils.showShortToast("getBootSourceEnable==" + mSource);
                }
                // HHTSourceManager.getInstance().getBootSourceEnable();
                break;
            case 13:
                showDialog(getBootSourceModeDataList(), "setBootSourceMode", 2, position);
                break;
            case 14:
                if (mHHTSourceManager != null) {
                    String mSource = mHHTSourceManager.getBootSourceMode();
                    ToastUtils.showShortToast("getBootSourceMode==" + mSource);
                }
                // HHTSourceManager.getInstance().getBootSourceMode();
                break;
            case 15:
                if (mHHTSourceManager != null) {
                    boolean mSource = mHHTSourceManager.setSignalLock(true);
                    ToastUtils.showShortToast("setSignalLock是否成功==" + mSource);
                }
                // HHTSourceManager.getInstance().setSignalLock(true);
                break;
            case 16:
                if (mHHTSourceManager != null) {
                    boolean mSource = mHHTSourceManager.isSignalLock();
                    ToastUtils.showShortToast("isSignalLock==" + mSource);
                }
                //  HHTSourceManager.getInstance().isSignalLock();
                break;
            case 17:
                showDialog(getInputSrcCustomerNameByKeyDataList(), "startSourcebyKey", 3, position);
                break;
           // case 18:
             //   showDialog(getInputSrcCustomerNameByKeyDataList(), "getSourcePlugStateByKey", 3, position);
           //     break;
            case 18:
                showDialog(getSourceDetectionModeDataList(), "setSourceDetectionMode", 2, position);
                break;
            case 19:
                if (mHHTSourceManager != null) {
                    int mSource = mHHTSourceManager.getSourceDetectionMode();
                    ToastUtils.showShortToast("getSourceDetectionMode==" + mSource);
                }
                //   HHTSourceManager.getInstance().getSourceDetectionMode();
                break;
            case 20:
                if (mHHTSourceManager != null) {
                    boolean mSource = mHHTSourceManager.isTvWindow();
                    ToastUtils.showShortToast("isTvWindow==" + mSource);
                }
                //   HHTSourceManager.getInstance().isTvWindow();
                break;
            case 21:
//                if (mHHTSourceManager != null) {
//                    String mSourceInfo = mHHTSourceManager.getSourcePlugStateByKey("");
//                    ToastUtils.showShortToast("isTvWindow==" + mSourceInfo);
//                }

                showDialog(getInputSrcCustomerNameByKeyDataList(), "getSourcePlugStateByKey", 3, position);

                //   HHTSourceManager.getInstance().isTvWindow();
                break;
            case 22:
                if (mHHTSourceManager != null) {
                    Map<String, String> mSourceInfo = mHHTSourceManager.getInputSrcPlugStateMap();
                    ToastUtils.showShortToast("getSourceDetectionMode==" + mSourceInfo.toString());
                }
//                if (mHHTSourceManager != null) {
//                    boolean mSource = mHHTSourceManager.();
//                    ToastUtils.showShortToast("isTvWindow==" + mSource);
//                }
                //  HHTSourceManager.getInstance().disableFreeze();
                break;
            case 23:
//                if (mHHTSourceManager != null) {
//                    String mSourceInfo = mHHTSourceManager.getSourcePlugStateByKey("");
//                    ToastUtils.showShortToast("isTvWindow==" + mSourceInfo);
//                }

                showDialog(getInputSrcCustomerNameByKeyDataList(), "isCurrentSource", 3, position);

                //   HHTSourceManager.getInstance().isTvWindow();
                break;
        }

    }

    private void showDialog(final List<ModeBean> mListData, String title, int numColumns, final int positionType) {
        showMiddleDialog(mListData, title, numColumns, new OnMiddleDialogListener() {
            @Override
            public void onItemClick(int position) {
//                ToastUtils.showShortToast(mListData.get(position).getModeName()
//                        + "    typeStr==" + mListData.get(position).getTypeStr()
//                        + "     typeInt==" + mListData.get(position).getTypeInt());
                switch (positionType) {
                    case 0:
                        if (mHHTSourceManager != null) {
                            String typeStr = mListData.get(position).getTypeStr();
                            boolean mSourceVolume = mHHTSourceManager.setInputSrcCustomerNameByKey(typeStr, "atv");
                            ToastUtils.showShortToast("setInputSrcCustomerNameByKey==" + typeStr + "    设置是否成功==" + mSourceVolume);
                        }

//                          HHTSourceManager.getInstance().setInputSrcCustomerNameByKey(
//                                  mListData.get(position).getTypeStr(), "dtv");
                        break;
                    case 3:
                        if (mHHTSourceManager != null) {
                            String typeStr = mListData.get(position).getTypeStr();
                            boolean mSourceVolume = mHHTSourceManager.setLastSource(typeStr);
                            ToastUtils.showShortToast("setLastSource==" + typeStr + "    设置是否成功==" + mSourceVolume);
                        }
//                          HHTSourceManager.getInstance().setLastSource(
//                                  mListData.get(position).getTypeStr());
                        break;
                    case 5:
                        if (mHHTSourceManager != null) {
                            String typeStr = mListData.get(position).getTypeStr();
                            boolean mSourceVolume = mHHTSourceManager.setRecentTvSource(typeStr);
                            ToastUtils.showShortToast("setRecentTvSource==" + typeStr + "    设置是否成功==" + mSourceVolume);
                        }
                        //   HHTSourceManager.getInstance().setRecentTvSource(mListData.get(position).getTypeStr());
                        break;
                    case 7:
                        if (mHHTSourceManager != null) {
                            String typeStr = mListData.get(position).getTypeStr();
                            boolean mSourceVolume = mHHTSourceManager.setCurSource(typeStr);
                            ToastUtils.showShortToast("setCurSource==" + typeStr + "    设置是否成功==" + mSourceVolume);
                        }
                        //  HHTSourceManager.getInstance().setCurSource(mListData.get(position).getTypeStr());
                        break;
                    case 9:
                        if (mHHTSourceManager != null) {
                            String typeStr = mListData.get(position).getTypeStr();
                            boolean mSourceVolume = mHHTSourceManager.setPreSource(typeStr);
                            ToastUtils.showShortToast("setPreSource==" + typeStr + "    设置是否成功==" + mSourceVolume);
                        }
                        //  HHTSourceManager.getInstance().setPreSource(mListData.get(position).getTypeStr());
                        break;
                    case 13:
                        if (mHHTSourceManager != null) {
                            String typeStr = mListData.get(position).getTypeStr();
                            boolean mSourceVolume = mHHTSourceManager.setBootSourceMode(typeStr);
                            ToastUtils.showShortToast("setBootSourceMode==" + typeStr + "    设置是否成功==" + mSourceVolume);
                        }
                        //    HHTSourceManager.getInstance().setBootSourceMode(mListData.get(position).getTypeStr());
                        break;
                    case 17:
                        if (mHHTSourceManager != null) {
                            String typeStr = mListData.get(position).getTypeStr();
                            boolean mSourceVolume = mHHTSourceManager.startSourcebyKey(typeStr);
                            ToastUtils.showShortToast("startSourcebyKey==" + typeStr + "    设置是否成功==" + mSourceVolume);
                        }
                        //  HHTSourceManager.getInstance().startSourcebyKey(mListData.get(position).getTypeStr());
                        break;
                  //  case 18:
//                        if (mHHTSourceManager != null) {
//                            String typeStr = mListData.get(position).getTypeStr();
//                            String mSourceVolume = mHHTSourceManager.getSourcePlugStateByKey(typeStr);
//                            ToastUtils.showShortToast("getSourcePlugStateByKey==" + typeStr + "    设置是否成功==" + mSourceVolume);
//                        }
                        //  HHTSourceManager.getInstance().getSourcePlugStateByKey(mListData.get(position).getTypeStr());
                   //     break;
                    case 18:
                        if (mHHTSourceManager != null) {
                            int typeStr = mListData.get(position).getTypeInt();
                            boolean mSourceVolume = mHHTSourceManager.setSourceDetectionMode(typeStr);
                            ToastUtils.showShortToast("setSourceDetectionMode==" + typeStr + "    设置是否成功==" + mSourceVolume);
                        }
                        //  HHTSourceManager.getInstance().setSourceDetectionMode(mListData.get(position).getTypeInt());
                        break;
                    case 21:
                        if (mHHTSourceManager != null) {
                            String typeStr = mListData.get(position).getModeName();
                            String mSourceInfo = mHHTSourceManager.getSourcePlugStateByKey(typeStr);
                            ToastUtils.showShortToast("getSourcePlugStateByKey==" + mSourceInfo);
                        }
                        break;
                    case 23:
                        if (mHHTSourceManager != null) {
                            String typeStr = mListData.get(position).getModeName();
                            boolean mSourceInfo = mHHTSourceManager.isCurrentSource(typeStr);
                            ToastUtils.showShortToast("isCurrentSource==" + mSourceInfo);
                        }
                        break;
                }
            }
        });
    }
}
