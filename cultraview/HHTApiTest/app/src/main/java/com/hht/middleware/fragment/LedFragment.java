package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.led.HHTLedManager;
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
 * Time: 2019/12/13 10:01
 * Description do somethings
 */
public class LedFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTLedManager mHHTLedManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTLedManager = HHTLedManager.getInstance();//(HHTLedManager) mActivity.getSystemService(HHTLedManager.SERVICE);
    }

    public static LedFragment newInstance() {
        Bundle args = new Bundle();
        LedFragment fragment = new LedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.fragment_led_list_view);
        mDetailsAdapter = new DetailsAdapter(getActivity(), getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_led_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog = new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(180);
        introduceDialog.show(getString(R.string.fragment_led_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mList = new ArrayList<>();
        mList.add(new DetailsBean("setLedStatus(boolean bStatus)", "设置Led遥控灯状态 "));
        mList.add(new DetailsBean("setLedMode(int iMode) ", "设置LED遥控灯模式"));
        mList.add(new DetailsBean("getLedMode()", "获取LED的模式状态 "));
        return mList;
    }

    private List<ModeBean> getLedModeDataList() {
        List<ModeBean> mList = new ArrayList<>();
        mList.add(new ModeBean("HHTLedManager_LED_RED ", HHTLedManager.LED_RED));
        mList.add(new ModeBean("HHTLedManager_LED_GREEN ", HHTLedManager.LED_GREEN));
        return mList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if (mHHTLedManager != null) {
                    boolean mLedInfo = mHHTLedManager.setLedStatus(true);
                    ToastUtils.showShortToast("setLedStatus是否成功==" + mLedInfo);
                }
                //  HHTLedManager.getInstance().setLedStatus(true);
                break;
            case 1:
                showDialog(getLedModeDataList(), "setLedMode", 2, position);
                break;
            case 2:
                if (mHHTLedManager != null) {
                    int mLedInfo = mHHTLedManager.getLedMode();
                    ToastUtils.showShortToast("getLedMode==" + mLedInfo);
                }
                // HHTLedManager.getInstance().getLedMode();
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
                    case 1:
                        if (mHHTLedManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean mLedInfo = mHHTLedManager.setLedMode(typeInt);
                            ToastUtils.showShortToast("setLedMode是否成功==" + mLedInfo);
                        }
                        //   HHTLedManager.getInstance().setLedMode(mListData.get(position).getTypeInt());
                        break;
                }
            }
        });
    }
}
