package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.boardInfo.HHTBoardInfoManager;
import com.hht.middleware.R;
import com.hht.middleware.adapter.DetailsAdapter;
import com.hht.middleware.base.BaseFragment;
import com.hht.middleware.bean.DetailsBean;
import com.hht.middleware.dialog.IntroduceDialog;
import com.hht.middleware.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: chenhu
 * Time: 2019/12/13 9:45
 * Description do somethings
 */
public class BoardInfoFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTBoardInfoManager mHHTBoardInfoManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTBoardInfoManager = HHTBoardInfoManager.getInstance(); //(HHTBoardInfoManager) mActivity.getSystemService(HHTBoardInfoManager.SERVICE);
    }

    public static BoardInfoFragment newInstance() {
        Bundle args = new Bundle();
        BoardInfoFragment fragment = new BoardInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.fragment_board_info_list_view);
        mDetailsAdapter = new DetailsAdapter(mActivity, getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_boardinfo_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog = new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(280);
        introduceDialog.show(getString(R.string.fragment_boardinfo_introduce));

    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mDataList = new ArrayList<>();
        mDataList.add(new DetailsBean("getBoardMacAddr", "获取板卡MAC"));
        mDataList.add(new DetailsBean("getBoardSerial", "获取主板序列号"));
        mDataList.add(new DetailsBean("getProductSerial", "获取产品序列号"));
        mDataList.add(new DetailsBean("getApplicationActivationCode", "获取无线投屏激活码"));
        mDataList.add(new DetailsBean("getMemoryAvailableSize", "获取可用内存"));

        mDataList.add(new DetailsBean("getMemoryTotalSize", "获取总内存"));
        mDataList.add(new DetailsBean("getSystemTotalStorage", "获取EMMC总存储"));
        mDataList.add(new DetailsBean("getSystemAvailableStorage", "获取系统可用存储空间"));
        mDataList.add(new DetailsBean("getChipModel", "获取芯片型号"));
        mDataList.add(new DetailsBean("getBoardModel", "获取主板型号"));

        mDataList.add(new DetailsBean("getProductModel", "获取产品型号"));
        mDataList.add(new DetailsBean("getScreenSize", "获取终端设备屏幕尺寸"));
        mDataList.add(new DetailsBean("getManufacturer", "获取制造商信息"));
        mDataList.add(new DetailsBean("getTouchPanelVersion", "获取触摸屏版本号"));
        mDataList.add(new DetailsBean("getUpdateSystemPatchVersion", "获取版控升级补丁的版本"));

        mDataList.add(new DetailsBean("getFirmwareVersion", "获取固件版本号 "));
        mDataList.add(new DetailsBean("getAndroidOsVersion", "获取操作系统版本号"));

//        mDataList.add(new DetailsBean("HHTBuild.BOARD ", "板卡的型号，客户号"));
//        mDataList.add(new DetailsBean("HHTBuild.VERSION.BUILD_VERSION  ", "build version"));
//        mDataList.add(new DetailsBean("HHTBuild.VERSION.PRODUCT_VERSION ", "product version"));

        return mDataList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getBoardMacAddr();
                    ToastUtils.showShortToast("getBoardMacAddr==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getBoardMacAddr();
                break;
            case 1:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getBoardSerial();
                    ToastUtils.showShortToast("getBoardSerial==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getBoardSerial();
                break;
            case 2:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getProductSerial();
                    ToastUtils.showShortToast("getProductSerial==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getProductSerial();
                break;
            case 3:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getApplicationActivationCode(mActivity.getPackageName());
                    ToastUtils.showShortToast("getApplicationActivationCode==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getApplicationActivationCode("pagName");
                break;
            case 4:
                if (mHHTBoardInfoManager != null) {
                    int boardInfo = mHHTBoardInfoManager.getMemoryAvailableSize();
                    ToastUtils.showShortToast("getMemoryAvailableSize==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getMemoryAvailableSize();
                break;
            case 5:
                if (mHHTBoardInfoManager != null) {
                    int boardInfo = mHHTBoardInfoManager.getMemoryTotalSize();
                    ToastUtils.showShortToast("getMemoryTotalSize==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getMemoryTotalSize();
                break;
            case 6:
                if (mHHTBoardInfoManager != null) {
                    long boardInfo = mHHTBoardInfoManager.getSystemTotalStorage();
                    ToastUtils.showShortToast("getSystemTotalStorage==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getSystemTotalStorage();
                break;
            case 7:
                if (mHHTBoardInfoManager != null) {
                    long boardInfo = mHHTBoardInfoManager.getSystemAvailableStorage();
                    ToastUtils.showShortToast("getSystemAvailableStorage==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getSystemAvailableStorage();
                break;
            case 8:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getChipModel();
                    ToastUtils.showShortToast("getChipModel==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getChipModel();
                break;
            case 9:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getBoardModel();
                    ToastUtils.showShortToast("getBoardModel==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getBoardModel();
                break;
            case 10:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getProductModel();
                    ToastUtils.showShortToast("getProductModel==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getProductModel();
                break;
            case 11:
                if (mHHTBoardInfoManager != null) {
                    int boardInfo = mHHTBoardInfoManager.getScreenSize();
                    ToastUtils.showShortToast("getScreenSize==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getScreenSize();
                break;
            case 12:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getManufacturer();
                    ToastUtils.showShortToast("getManufacturer==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getManufacturer();
                break;
            case 13:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getTouchPanelVersion();
                    ToastUtils.showShortToast("getTouchPanelVersion==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getTouchPanelVersion();
                break;
            case 14:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getUpdateSystemPatchVersion();
                    ToastUtils.showShortToast("getUpdateSystemPatchVersion==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getUpdateSystemPatchVersion();
                break;
            case 15:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getFirmwareVersion();
                    ToastUtils.showShortToast("getFirmwareVersion==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getFirmwareVersion();
                break;
            case 16:
                if (mHHTBoardInfoManager != null) {
                    String boardInfo = mHHTBoardInfoManager.getAndroidOsVersion();
                    ToastUtils.showShortToast("getAndroidOsVersion==" + boardInfo);
                }
                // HHTBoardInfoManager.getInstance().getAndroidOsVersion();
                break;
            case 17:

                //  HHTBuild.BOARD ;
                break;
            case 18:
                // HHTBuild.VERSION.BUILD_VERSION;
                break;
            case 19:
                //   HHTBuild.VERSION.PRODUCT_VERSION;
                break;
        }
    }
}
