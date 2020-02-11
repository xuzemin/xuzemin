package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.audio.HHTAudioManager;
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
 * Time: 2019/12/13 9:31
 * Description do somethings
 */
public class AudioFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTAudioManager mHHTAudioManager;

    public static AudioFragment newInstance() {
        Bundle args = new Bundle();
        AudioFragment fragment = new AudioFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHHTAudioManager = HHTAudioManager.getInstance();// (HHTAudioManager) mActivity.getSystemService(HHTAudioManager.SERVICE);
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.fragment_audio_list_view);
        mDetailsAdapter = new DetailsAdapter(getActivity(), getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_audio_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        new IntroduceDialog(mActivity).show(getString(R.string.fragment_audio_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mList = new ArrayList<>();

        mList.add(new DetailsBean("setSourceVolumeMute(boolean enable)  ", "Set system volume mode"));
        mList.add(new DetailsBean("getSourceVolumeMute()", "Get sound mode"));

        mList.add(new DetailsBean("setAudioSoundMode(int soundMode)", "Set sound mode"));
        mList.add(new DetailsBean("getAudioSoundMode()", "Get sound mode"));

        mList.add(new DetailsBean("setEarPhoneVolume(int volume)", "Set EarPhone Volume"));
        mList.add(new DetailsBean("setEarPhoneVolume(int volume, boolean saveDb) ", "Set EarPhone Volume"));
        mList.add(new DetailsBean("getEarPhoneVolume()", "Get EarPhone volume"));

        mList.add(new DetailsBean("setBass(int bassValue)", "Set Bass volume"));
        mList.add(new DetailsBean("getBass()", "Get Bass Value"));

        mList.add(new DetailsBean("setTreble(int bassValue)  ", "Set treble value"));
        mList.add(new DetailsBean("getTreble()", "Get treble value"));

        mList.add(new DetailsBean("setBalance(int balanceValue) ", "Set balance"));
        mList.add(new DetailsBean("getBalance()", "Get balance"));

        mList.add(new DetailsBean("setAvcMode(boolean isAvcEnable)", "Set Avc mode"));
        mList.add(new DetailsBean("getAvcMode()", "Get Avc mode"));


        mList.add(new DetailsBean("setAudioSpdifOutMode(int spdifMode)", "set SpdifOut mode"));
        mList.add(new DetailsBean("getAudioSpdifOutMode()", "Get SpdifOut mode"));

        mList.add(new DetailsBean("setEqBand120(int eqValue) ", "Set Equilizer 120HZ EQBand value"));
        mList.add(new DetailsBean("getEqBand120()", "Get Equilizer 120HZ EQBand value"));

        mList.add(new DetailsBean("setEqBand500(int eqValue)  ", "Set Equilizer 500HZ EQBand value"));
        mList.add(new DetailsBean("getEqBand500() ", "Get Equilizer 500HZ EQBand value"));

        mList.add(new DetailsBean("setEqBand1500(int eqValue) ", "Set Equilizer 1500HZ EQBand value"));
        mList.add(new DetailsBean("getEqBand1500()", "Get Equilizer 1500HZ EQBand value"));

        mList.add(new DetailsBean("setEqBand5k(int eqValue)  ", "Set Equilizer 5kHZ EQBand value"));
        mList.add(new DetailsBean("getEqBand5k()", "Get Equilizer 5kHZ EQBand value"));

        mList.add(new DetailsBean("setEqBand10k(int eqValue) ", "Set Equilizer 10kHZ EQBand value"));
        mList.add(new DetailsBean("getEqBand10k()", "Get Equilizer 10kHZ EQBand value"));

        return mList;
    }

//    private List<ModeBean> getSystemVolumeModeDataList() {
//        List<ModeBean> mList = new ArrayList<>();
//        mList.add(new ModeBean("VOLIME_NORMAL", HHTAudioManager.EnumVolumeMode.VOLIME_NORMAL.name()));
//        mList.add(new ModeBean("VOLIME_MUTE", HHTAudioManager.EnumVolumeMode.VOLIME_MUTE.name()));
//        mList.add(new ModeBean("VOLIME_SOURCE_MUTE", HHTAudioManager.EnumVolumeMode.VOLIME_SOURCE_MUTE.name()));
//        return mList;
//    }

    private List<ModeBean> getAudioSoundModeDataList() {
        List<ModeBean> mList = new ArrayList<>();
        mList.add(new ModeBean("SOUND_STANDARD",
                HHTAudioManager.EnumSoundMode.SOUND_MODE_STANDARD.ordinal()));

        mList.add(new ModeBean("SOUND_MUSIC",
                HHTAudioManager.EnumSoundMode.SOUND_MODE_MUSIC.ordinal()));

        mList.add(new ModeBean("SOUND_SPORTS",
                HHTAudioManager.EnumSoundMode.SOUND_MODE_SPORTS.ordinal()));

        mList.add(new ModeBean("SOUND_USER",
                HHTAudioManager.EnumSoundMode.SOUND_MODE_USER.ordinal()));

        return mList;
    }

    private List<ModeBean> getAudioSpdifOutModeDataList() {
        List<ModeBean> mList = new ArrayList<>();

        mList.add(new ModeBean("SPDIF_TYPE_PCM",
                HHTAudioManager.EnumSpdifMode.SPDIF_TYPE_PCM.ordinal()));

        mList.add(new ModeBean("SPDIF_TYPE_OFF",
                HHTAudioManager.EnumSpdifMode.SPDIF_TYPE_OFF.ordinal()));

        mList.add(new ModeBean("SPDIF_TYPE_RAW",
                HHTAudioManager.EnumSpdifMode.SPDIF_TYPE_RAW.ordinal()));

        return mList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
              //  showDialog(getSystemVolumeModeDataList(), "setVolimeMode", 2, position);
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setSourceVolumeMute(true);
                    ToastUtils.showShortToast("setSourceVolumeMute==" + mVolumeMode);
                }
                break;
            case 1:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.getSourceVolumeMute();
                    ToastUtils.showShortToast("getSourceVolumeMute==" + mVolumeMode);
                }
                break;
            case 2:
                showDialog(getAudioSoundModeDataList(), "setAudioSoundMode", 2, position);
                break;
            case 3:
                if (mHHTAudioManager != null) {
                    int mVolumeMode = mHHTAudioManager.getAudioSoundMode();
                    ToastUtils.showShortToast("getAudioSoundMode==" + mVolumeMode);
                }
                // HHTAudioManager.getInstance().getAudioSoundMode();
                break;
            case 4:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setEarPhoneVolume(1);
                    ToastUtils.showShortToast("setEarPhoneVolume是否成功==" + mVolumeMode);
                }
                // HHTAudioManager.getInstance().setEarPhoneVolume(0);
                break;
            case 5:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setEarPhoneVolume(1);
                    ToastUtils.showShortToast("setEarPhoneVolume成功否==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().setEarPhoneVolume();
                break;
            case 6:
                if (mHHTAudioManager != null) {
                    int mVolumeMode = mHHTAudioManager.getEarPhoneVolume();
                    ToastUtils.showShortToast("getEarPhoneVolume==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().getEarPhoneVolume();
                break;
            case 7:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setBass(1);
                    ToastUtils.showShortToast("setBass是否成功==" + mVolumeMode);
                }
                // HHTAudioManager.getInstance().setBass(1);
                break;
            case 8:
                if (mHHTAudioManager != null) {
                    int mVolumeMode = mHHTAudioManager.getBass();
                    ToastUtils.showShortToast("getBass==" + mVolumeMode);
                }
                // HHTAudioManager.getInstance().getBass();
                break;
            case 9:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setTreble(1);
                    ToastUtils.showShortToast("setTreble是否成功==" + mVolumeMode);
                }
                // HHTAudioManager.getInstance().setTreble(1);
                break;
            case 10:
                if (mHHTAudioManager != null) {
                    int mVolumeMode = mHHTAudioManager.getTreble();
                    ToastUtils.showShortToast("getTreble==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().getTreble();
                break;
            case 11:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setBalance(1);
                    ToastUtils.showShortToast("setBalance是否成功==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().setBalance(1);
                break;
            case 12:
                if (mHHTAudioManager != null) {
                    int mVolumeMode = mHHTAudioManager.getBalance();
                    ToastUtils.showShortToast("getBalance==" + mVolumeMode);
                }
                // HHTAudioManager.getInstance().getBalance();
                break;
            case 13:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setAvcMode(true);
                    ToastUtils.showShortToast("setAvcMode是否成功==" + mVolumeMode);
                }
                // HHTAudioManager.getInstance().setAvcMode(true);
                break;
            case 14:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.getAvcMode();
                    ToastUtils.showShortToast("getAvcMode==" + mVolumeMode);
                }
                // HHTAudioManager.getInstance().getAvcMode();
                break;
            case 15:
                showDialog(getAudioSpdifOutModeDataList(), "setAudioSoundMode", 2, position);
                break;
            case 16:
                if (mHHTAudioManager != null) {
                    int mVolumeMode = mHHTAudioManager.getAudioSpdifOutMode();
                    ToastUtils.showShortToast("getAudioSpdifOutMode==" + mVolumeMode);
                }
                // HHTAudioManager.getInstance().getAudioSpdifOutMode();
                break;
            case 17:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setEqBand120(1);
                    ToastUtils.showShortToast("setEqBand120是否成功==" + mVolumeMode);
                }
                // HHTAudioManager.getInstance().setEqBand120(1);
                break;
            case 18:
                if (mHHTAudioManager != null) {
                    int mVolumeMode = mHHTAudioManager.getEqBand120();
                    ToastUtils.showShortToast("getEqBand120==" + mVolumeMode);
                }
                // HHTAudioManager.getInstance().getEqBand120();
                break;
            case 19:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setEqBand500(1);
                    ToastUtils.showShortToast("setEqBand500是否成功==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().setEqBand500(1);
                break;
            case 20:
                if (mHHTAudioManager != null) {
                    int mVolumeMode = mHHTAudioManager.getEqBand500();
                    ToastUtils.showShortToast("getEqBand500==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().getEqBand500();
                break;
            case 21:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setEqBand1500(1);
                    ToastUtils.showShortToast("setEqBand1500是否成功==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().setEqBand1500(1);
                break;
            case 22:
                if (mHHTAudioManager != null) {
                    int mVolumeMode = mHHTAudioManager.getEqBand1500();
                    ToastUtils.showShortToast("getEqBand1500==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().getEqBand1500();
                break;
            case 23:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setEqBand5k(1);
                    ToastUtils.showShortToast("setEqBand5k是否成功==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().setEqBand5k(1);
                break;
            case 24:
                if (mHHTAudioManager != null) {
                    int mVolumeMode = mHHTAudioManager.getEqBand5k();
                    ToastUtils.showShortToast("getEqBand5k==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().getEqBand5k();
                break;
            case 25:
                if (mHHTAudioManager != null) {
                    boolean mVolumeMode = mHHTAudioManager.setEqBand10k(1);
                    ToastUtils.showShortToast("setEqBand10k是否成功==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().setEqBand10k(1);
                break;
            case 26:
                if (mHHTAudioManager != null) {
                    int mVolumeMode = mHHTAudioManager.getEqBand10k();
                    ToastUtils.showShortToast("getEqBand10k==" + mVolumeMode);
                }
                //  HHTAudioManager.getInstance().getEqBand10k()
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
//                    case 0:
//                        if (mHHTAudioManager != null) {
//                            String typeStr = mListData.get(position).getTypeStr();
//                            boolean mVolumeMode = mHHTAudioManager.setSystemVolumeMode(typeStr);
//                            ToastUtils.showShortToast("setSystemVolumeMode==" + typeStr + "    是否设置成功==" + mVolumeMode);
//                        }
//
//                        //  HHTAudioManager.getInstance().setSystemVolumeMode(mListData.get(position).getTypeStr());
//                        break;
                    case 2:
                        if (mHHTAudioManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean mVolumeMode = mHHTAudioManager.setAudioSoundMode(typeInt);
                            ToastUtils.showShortToast("setAudioSoundMode==" + typeInt + "    是否设置成功==" + mVolumeMode);
                        }
                        //    HHTAudioManager.getInstance().setAudioSoundMode(mListData.get(position).getTypeInt());
                        break;
                    case 15:
                        if (mHHTAudioManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean mVolumeMode = mHHTAudioManager.setAudioSpdifOutMode(typeInt);
                            ToastUtils.showShortToast("setAudioSpdifOutMode==" + typeInt + "    是否设置成功==" + mVolumeMode);
                        }
                        //   HHTAudioManager.getInstance().setAudioSpdifOutMode(mListData.get(position).getTypeInt());
                        break;
                }
            }
        });
    }
}
