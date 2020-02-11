package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.lock.HHTLockManager;
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
 * Time: 2019/12/13 10:03
 * Description do somethings
 */
public class LockFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTLockManager mHHTLockManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTLockManager = HHTLockManager.getInstance();// (HHTLockManager) mActivity.getSystemService(HHTLockManager.SERVICE);
    }

    public static LockFragment newInstance() {
        Bundle args = new Bundle();
        LockFragment fragment = new LockFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.fragment_lock_list_view);
        mDetailsAdapter = new DetailsAdapter(getActivity(), getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_lock_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog = new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(180);
        introduceDialog.show(getString(R.string.fragment_lock_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mList = new ArrayList<>();
        mList.add(new DetailsBean("setKeypadLock(boolean bLock)", "设置按键板锁"));
        mList.add(new DetailsBean("isKeypadLock()", "获取按键板的已锁状态"));

        mList.add(new DetailsBean("setRemoteIrLock(boolean bLock)", "设置遥控锁"));
        mList.add(new DetailsBean("isRemoteIrLock()", "获取遥控的已锁状态"));

        mList.add(new DetailsBean("setTouchLock(boolean bLock)", "设置触摸锁"));
        mList.add(new DetailsBean("isTouchLock()", "获取触摸的已锁状态"));

        mList.add(new DetailsBean("setUsbKeyLock(boolean bLock)", "设置U盘秘钥锁动作"));
        mList.add(new DetailsBean("isUsbKeyLock()", "U盘秘钥锁的已锁上状态"));

        mList.add(new DetailsBean("setUsbKeyLockedEnable(boolean bEnable)", "设置U盘秘钥锁使能状态"));
        mList.add(new DetailsBean("getUsbKeyLockedEnable()", "U盘秘钥锁使能状态"));

//        mList.add(new DetailsBean("setLockscreenEnable(boolean bEnable)", "设置锁屏界面使能"));
//        mList.add(new DetailsBean("getLockscreenEnable()", "获取锁屏界面使能"));
//
//        mList.add(new DetailsBean("setLockscreenStatus(int bStatus)", "设置锁屏状态"));
//        mList.add(new DetailsBean("getLockscreenStatus()", "获取锁屏状态"));
//
//        mList.add(new DetailsBean("setLockscreenPassword(String strPassword)", "设置锁屏密码"));
//        mList.add(new DetailsBean("getLockscreenPassword()", "获取锁屏密码"));


        return mList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {

            case 0:
                if (mHHTLockManager != null) {
                    boolean mLockInfo = mHHTLockManager.setKeypadLock(true);
                    ToastUtils.showShortToast("setKeypadLock是否成功==" + mLockInfo);
                }
                // HHTLockManager.getInstance().setKeypadLock(true);
                break;
            case 1:
                if (mHHTLockManager != null) {
                    boolean mLockInfo = mHHTLockManager.isKeypadLock();
                    ToastUtils.showShortToast("isKeypadLock==" + mLockInfo);
                }
                // HHTLockManager.getInstance().isKeypadLock();
                break;
            case 2:
                if (mHHTLockManager != null) {
                    boolean mLockInfo = mHHTLockManager.setRemoteIrLock(true);
                    ToastUtils.showShortToast("setRemoteIrLock是否成功==" + mLockInfo);
                }
                // HHTLockManager.getInstance().setRemoteIrLock(true);
                break;
            case 3:
                if (mHHTLockManager != null) {
                    boolean mLockInfo = mHHTLockManager.isRemoteIrLock();
                    ToastUtils.showShortToast("isRemoteIrLock==" + mLockInfo);
                }
                // HHTLockManager.getInstance().isRemoteIrLock();

                break;
            case 4:
                if (mHHTLockManager != null) {
                    boolean mLockInfo = mHHTLockManager.setTouchLock(true);
                    ToastUtils.showShortToast("setTouchLock是否成功==" + mLockInfo);
                }
                // HHTLockManager.getInstance().setTouchLock(true);
                break;
            case 5:
                if (mHHTLockManager != null) {
                    boolean mLockInfo = mHHTLockManager.isTouchLock();
                    ToastUtils.showShortToast("isTouchLock==" + mLockInfo);
                }
                // HHTLockManager.getInstance().isTouchLock();
                break;
            case 6:
                if (mHHTLockManager != null) {
                    boolean mLockInfo = mHHTLockManager.setUsbKeyLock(true);
                    ToastUtils.showShortToast("setUsbKeyLock是否成功==" + mLockInfo);
                }
                // HHTLockManager.getInstance().setUsbKeyLock(true);
                break;
            case 7:
                if (mHHTLockManager != null) {
                    boolean mLockInfo = mHHTLockManager.isUsbKeyLock(true);
                    ToastUtils.showShortToast("isUsbKeyLock==" + mLockInfo);
                }
                // HHTLockManager.getInstance().isUsbKeyLock(true);
                break;
            case 8:
                if (mHHTLockManager != null) {
                    boolean mLockInfo = mHHTLockManager.setUsbKeyLockedEnable(true);
                    ToastUtils.showShortToast("setUsbKeyLockedEnable是否成功==" + mLockInfo);
                }
                // HHTLockManager.getInstance().setUsbKeyLockedEnable(true);
            case 9:
                if (mHHTLockManager != null) {
                    boolean mLockInfo = mHHTLockManager.getUsbKeyLockedEnable();
                    ToastUtils.showShortToast("getUsbKeyLockedEnable是否成功==" + mLockInfo);
                }
                // HHTLockManager.getInstance().getUsbKeyLockedEnable();
                break;
        }
    }
}
