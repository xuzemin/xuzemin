package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.system.HHTSystemManager;
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
 * Time: 2019/12/13 10:19
 * Description do somethings
 */
public class SystemFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTSystemManager mHHTSystemManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTSystemManager = HHTSystemManager.getInstance();// (HHTSystemManager) mActivity.getSystemService(HHTSystemManager.SERVICE);
    }

    public static SystemFragment newInstance() {
        Bundle args = new Bundle();
        SystemFragment fragment = new SystemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.fragment_system_list_view);
        mDetailsAdapter = new DetailsAdapter(getActivity(), getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_system_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog = new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(226);
        introduceDialog.show(getString(R.string.fragment_system_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mList = new ArrayList<>();

        mList.add(new DetailsBean("updatePatchVersion(String strConfig)", "更新版本控制配置"));
        mList.add(new DetailsBean("updateSystem(String strFilepath)", "更新主固件"));

        mList.add(new DetailsBean("updateSystem()", "更新主固件"));
        mList.add(new DetailsBean("installAPKPackage(String strApkFilePath)", "安装app"));

        mList.add(new DetailsBean("screenshot(int width, int height)", "启动截屏"));

        return mList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if (mHHTSystemManager != null) {
                    boolean mSystemInfo = mHHTSystemManager.updatePatchVersion("2.0.0");
                    ToastUtils.showShortToast("updatePatchVersion是否成功==" + mSystemInfo);
                }
                // HHTSystemManager.getInstance().updatePatchVersion("strconfig");
                break;
            case 1:
                if (mHHTSystemManager != null) {
                    boolean mSystemInfo = mHHTSystemManager.updateSystem("filePath");
                    ToastUtils.showShortToast("updateSystem是否成功==" + mSystemInfo);
                }
                // HHTSystemManager.getInstance().updateSystem("path");
                break;
            case 2:
                if (mHHTSystemManager != null) {
                    boolean mSystemInfo = mHHTSystemManager.updateSystem();
                    ToastUtils.showShortToast("updateSystem是否成功==" + mSystemInfo);
                }
                // HHTSystemManager.getInstance().updateSystem();
                break;
            case 3:
                if (mHHTSystemManager != null) {
                    boolean mSystemInfo = mHHTSystemManager.installApkPackage("apkFilePath");
                    ToastUtils.showShortToast("installApkPackage是否成功==" + mSystemInfo);
                }
                // HHTSystemManager.getInstance().installApkPackage("apkFilePath");
                break;
            case 4:
                if (mHHTSystemManager != null) {
                    Bitmap mSystemInfo = mHHTSystemManager.screenshot(1920,1080);
                    ToastUtils.showShortToast("startScreenShot是否成功==" + mSystemInfo);
                }
                break;
        }
    }
}
