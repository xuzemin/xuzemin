package com.hht.middleware.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.middleware.R;
import com.hht.middleware.adapter.MainFragmentAdapter;
import com.hht.middleware.application.ApplicationManager;
import com.hht.middleware.base.BaseFragment;
import com.hht.middleware.base.MiddleWareConstant;
import com.hht.middleware.dialog.IntroduceDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: chenhu
 * Time: 2019/12/12 16:25
 * Description do somethings
 */
public class MainFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private MainFragmentAdapter mMainFragmentAdapter;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private List<String> getListData() {
        List<String> mListData = new ArrayList<>();
        mListData.add(MiddleWareConstant.HHT_AUDIO);
        mListData.add(MiddleWareConstant.HHT_BOARD_INFO);
        mListData.add(MiddleWareConstant.HHT_CUSTOMER);
        mListData.add(MiddleWareConstant.HHT_DEVICE);
        mListData.add(MiddleWareConstant.HHT_LED);
        mListData.add(MiddleWareConstant.HHT_LOCK);
        mListData.add(MiddleWareConstant.HHT_NET_WORK);
        mListData.add(MiddleWareConstant.HHT_OPS);
        mListData.add(MiddleWareConstant.HHT_PICTURE);
        mListData.add(MiddleWareConstant.HHT_SOURCE);
        mListData.add(MiddleWareConstant.HHT_SYSTEM);
        mListData.add(MiddleWareConstant.HHT_TIME);
        mListData.add(MiddleWareConstant.HHT_TOUCH);
        return mListData;
    }

    @Override
    protected void initView() {
        setTitleTextVew(getString(R.string.app_name));
        mListView = findViewById(R.id.main_fragment_list_view);
        mMainFragmentAdapter = new MainFragmentAdapter(mActivity, getListData());
        mListView.setAdapter(mMainFragmentAdapter);
        mListView.setOnItemClickListener(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_layout;
    }

    @Override
    public void OnBackListener() {
        ApplicationManager.getInstance().mExitApplication();
    }

    @Override
    public void OnIntroduceListener() {
        new IntroduceDialog(mActivity).show(getString(R.string.fragment_main_introduce));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                setClickData(AudioFragment.newInstance(), MiddleWareConstant.HHT_AUDIO);
                break;
            case 1:
                setClickData(BoardInfoFragment.newInstance(), MiddleWareConstant.HHT_BOARD_INFO);
                break;
            case 2:
                setClickData(CustomerFragment.newInstance(), MiddleWareConstant.HHT_CUSTOMER);
                break;
            case 3:
                setClickData(DeviceFragment.newInstance(), MiddleWareConstant.HHT_DEVICE);
                break;
            case 4:
                setClickData(LedFragment.newInstance(), MiddleWareConstant.HHT_LED);
                break;
            case 5:
                setClickData(LockFragment.newInstance(), MiddleWareConstant.HHT_LOCK);
                break;
            case 6:
                setClickData(NetworkFragment.newInstance(), MiddleWareConstant.HHT_NET_WORK);
                break;
            case 7:
                setClickData(OpsFragment.newInstance(), MiddleWareConstant.HHT_OPS);
                break;
            case 8:
                setClickData(PictureFragment.newInstance(), MiddleWareConstant.HHT_PICTURE);
                break;
            case 9:
                setClickData(SourceFragment.newInstance(), MiddleWareConstant.HHT_SOURCE);
                break;
            case 10:
                setClickData(SystemFragment.newInstance(), MiddleWareConstant.HHT_SYSTEM);
                break;
            case 11:
                setClickData(TimeFragment.newInstance(), MiddleWareConstant.HHT_TIME);
                break;
            case 12:
                setClickData(TouchFragment.newInstance(), MiddleWareConstant.HHT_TOUCH);
                break;

        }
    }

    private void setClickData(Fragment fragment, String title) {
        setTitleTextVew(title);
        replaceFragment(fragment);
    }
}
