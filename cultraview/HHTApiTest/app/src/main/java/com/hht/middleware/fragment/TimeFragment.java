package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.time.HHTTimeManager;
import com.hht.android.sdk.time.util.TimeUtil;
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
 * Time: 2019/12/13 10:24
 * Description do somethings
 */
public class TimeFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTTimeManager mHHTTimeManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTTimeManager = HHTTimeManager.getInstance();// (HHTTimeManager) mActivity.getSystemService(HHTTimeManager.SERVICE);
    }

    public static TimeFragment newInstance() {
        Bundle args = new Bundle();
        TimeFragment fragment = new TimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.fragment_time_list_view);
        mDetailsAdapter = new DetailsAdapter(getActivity(), getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_time_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog = new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(210);
        introduceDialog.show(getString(R.string.fragment_time_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mList = new ArrayList<>();
        mList.add(new DetailsBean("getScheduleTimeForBoot()", "获得定时开机时间"));
        mList.add(new DetailsBean("setChipRuntime(long lChipTime)", "保存芯片总运行时间"));
        mList.add(new DetailsBean("getChipRuntime()", "获取芯片总运行时间"));

        mList.add(new DetailsBean("setSystemRuntime(long lChipTime)", "保存系统运行时间"));
        mList.add(new DetailsBean("getSystemRuntime()", "获取系统运行时间"));

        mList.add(new DetailsBean("setScheduleTimeBootEnable(boolean enable)", "设置定时开机使能"));
        mList.add(new DetailsBean("isScheduleTimeBootEnable()", "获取定时开机使能状态"));

        mList.add(new DetailsBean("setScheduleTimeShutdownEnable(boolean enable)", "设置定时关机使能"));
        mList.add(new DetailsBean("isScheduleTimeShutdownEnable()", "获取定时关机使能状态"));

        mList.add(new DetailsBean("setScheduleTimeForBoot(TimeUtil time)", "定时开关机开机时间 "));


        mList.add(new DetailsBean("setScheduleTimeForShutdown(TimeUtil time)", "设置定时关机时间 "));
        mList.add(new DetailsBean("getScheduleTimeForShutdown(TimeUtil time)", "获取定时关机时间 "));



        return mList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if (mHHTTimeManager != null) {
                    TimeUtil mTimeInfo = mHHTTimeManager.getScheduleTimeForBoot();
                    ToastUtils.showShortToast("getScheduleTimeForBoot==" + mTimeInfo.toString());
                }
                // HHTTimeManager.getInstance().setRtcTime(1, "");
                break;
            case 1:
                if (mHHTTimeManager != null) {
                    boolean mTimeInfo = mHHTTimeManager.setChipRuntime(1);
                    ToastUtils.showShortToast("setChipRuntime是否成功==" + mTimeInfo);
                }
                // HHTTimeManager.getInstance().setChipRuntime(1);
                break;
            case 2:
                if (mHHTTimeManager != null) {
                    long mTimeInfo = mHHTTimeManager.getChipRuntime();
                    ToastUtils.showShortToast("getChipRuntime==" + mTimeInfo);
                }
                // HHTTimeManager.getInstance().getChipRuntime();
                break;
            case 3:
                if (mHHTTimeManager != null) {
                    boolean mTimeInfo = mHHTTimeManager.setSystemRuntime(1);
                    ToastUtils.showShortToast("setSystemRuntime是否成功==" + mTimeInfo);
                }
                // HHTTimeManager.getInstance().setSystemRuntime(1);
                break;
            case 4:
                if (mHHTTimeManager != null) {
                    long mTimeInfo = mHHTTimeManager.getSystemRuntime();
                    ToastUtils.showShortToast("getSystemRuntime==" + mTimeInfo);
                }
                // HHTTimeManager.getInstance().getSystemRuntime();
                break;
            case 5:
                if (mHHTTimeManager != null) {
                    boolean mTimeInfo = mHHTTimeManager.setScheduleTimeBootEnable(true);
                    ToastUtils.showShortToast("setScheduleTimeBootEnable是否成功==" + mTimeInfo);
                }
                // HHTTimeManager.getInstance().setScheduleTimeBootEnable(true);
                break;
            case 6:
                if (mHHTTimeManager != null) {
                    boolean mTimeInfo = mHHTTimeManager.isScheduleTimeBootEnable();
                    ToastUtils.showShortToast("isScheduleTimeBootEnable==" + mTimeInfo);
                }
                // HHTTimeManager.getInstance().isScheduleTimeBootEnable();
                break;
            case 7:
                if (mHHTTimeManager != null) {
                    boolean mTimeInfo = mHHTTimeManager.setScheduleTimeShutdownEnable(true);
                    ToastUtils.showShortToast("setScheduleTimeShutdownEnable是否成功==" + mTimeInfo);
                }
                // HHTTimeManager.getInstance().setScheduleTimeShutdownEnable(true);
                break;
            case 8:
                if (mHHTTimeManager != null) {
                    boolean mTimeInfo = mHHTTimeManager.isScheduleTimeShutdownEnable();
                    ToastUtils.showShortToast("isScheduleTimeShutdownEnable==" + mTimeInfo);
                }
                // HHTTimeManager.getInstance().isScheduleTimeShutdownEnable();
                break;
            case 9:
                if (mHHTTimeManager != null) {
                    List<String> mList =new ArrayList<>();
                    mList.add("MON");
                    TimeUtil timeUtil =new TimeUtil(mList,12,0);
                    boolean mTimeInfo = mHHTTimeManager.setScheduleTimeForBoot(timeUtil);
                    ToastUtils.showShortToast("setScheduleTimeForBoot是否成功==" + mTimeInfo);
                }
                break;
            case 10:
                if (mHHTTimeManager != null) {
                    List<String> mList =new ArrayList<>();
                    mList.add("MON");
                    TimeUtil timeUtil =new TimeUtil(mList,9,0);
                    boolean mTimeInfo = mHHTTimeManager.setScheduleTimeForShutdown(timeUtil);
                    ToastUtils.showShortToast("setScheduleTimeForShutdown是否成功==" + mTimeInfo);
                }
                break;
            case 11:
                if (mHHTTimeManager != null) {
                    TimeUtil mTimeInfo = mHHTTimeManager.getScheduleTimeForShutdown();
                    ToastUtils.showShortToast("getScheduleTimeForShutdown==" + mTimeInfo);
                }
                break;
        }
    }
}
