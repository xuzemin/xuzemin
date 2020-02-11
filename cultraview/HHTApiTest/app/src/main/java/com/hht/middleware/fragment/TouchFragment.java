package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.touch.HHTTouchManager;
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
 * Time: 2019/12/13 10:46
 * Description do somethings
 */
public class TouchFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTTouchManager mHHTTouchManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTTouchManager = HHTTouchManager.getInstance();// (HHTTouchManager) mActivity.getSystemService(HHTTouchManager.SERVICE);
    }

    public static TouchFragment newInstance() {
        Bundle args = new Bundle();
        TouchFragment fragment = new TouchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.fragment_touch_list_view);
        mDetailsAdapter = new DetailsAdapter(getActivity(), getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_touch_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog = new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(180);
        introduceDialog.show(getString(R.string.fragment_touch_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mList = new ArrayList<>();
        mList.add(new DetailsBean("controlPcTouchRect(String strPackage,String strWinTitle," +
                "rect touchRect, boolean bEnable)", "控制电脑触控区域穿透状态"));
        return mList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mHHTTouchManager != null) {
            boolean mTouchInfo = mHHTTouchManager.controlPcTouchRect("", "", new Rect(), false);
            ToastUtils.showShortToast("controlPcTouchRect是否成功==" + mTouchInfo);
        }
    }
}
