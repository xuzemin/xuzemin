package com.hht.middleware.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hht.android.sdk.picture.HHTPictureManager;
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
 * Time: 2019/12/13 10:12
 * Description do somethings
 */
public class PictureFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private DetailsAdapter mDetailsAdapter;
    private HHTPictureManager mHHTPictureManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHHTPictureManager = HHTPictureManager.getInstance();// (HHTPictureManager) mActivity.getSystemService(HHTPictureManager.SERVICE);
    }

    public static PictureFragment newInstance() {
        Bundle args = new Bundle();
        PictureFragment fragment = new PictureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.fragment_picture_list_view);
        mDetailsAdapter = new DetailsAdapter(getActivity(), getDataList());
        mListView.setAdapter(mDetailsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_picture_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog dialog = new IntroduceDialog(mActivity);
        dialog.setIntroduceDialogHeight(200);
        dialog.show(getString(R.string.fragment_picture_introduce));
    }

    private List<DetailsBean> getDataList() {
        List<DetailsBean> mList = new ArrayList<>();
        mList.add(new DetailsBean("setPictureMode(int pictureMode)", "设置信号源的图像模式 "));
        mList.add(new DetailsBean("getPictureMode()", "获取信号源的图像模式"));

        mList.add(new DetailsBean("setVideoArcType(int arcType)", "设置VideoArcType"));
        mList.add(new DetailsBean("getVideoArcType()", "获取VideoArcType"));

        mList.add(new DetailsBean("setVideoItem(int item,int value)", "Set specified picture mode"));
        mList.add(new DetailsBean("getVideoItem(int item)", "Get value for specified picture mode"));

        mList.add(new DetailsBean("setColorTempratureIdx(int colorIdx)", "设置当前信源色温"));
        mList.add(new DetailsBean("getColorTempratureIdx()", "获得当前信源色温"));

        mList.add(new DetailsBean("setPCHPos(int hpos)", "Adjust horizontal start position at PC mode"));
        mList.add(new DetailsBean("getPCHPos()", "Get the horizontal value of start position"));

        mList.add(new DetailsBean("setPCVPos(int vpos)", "Adjust vertical start position at PC mode"));
        mList.add(new DetailsBean("getPCVPos()", "Get the vertical value of start position"));

        mList.add(new DetailsBean("setPCClock(int clock)", "Get the PC clock value"));
        mList.add(new DetailsBean("getPCClock()()", "Get the PC clock value"));

        mList.add(new DetailsBean("setPCPhase(int phase)", "Adjust the ADC phase at PC mode"));
        mList.add(new DetailsBean("getPCPhase()", "Get the PC phase value"));

        mList.add(new DetailsBean("execAutoPc()", "Auto tune the screen position at PC mode"));
        mList.add(new DetailsBean("freezeImage()", "To freeze image"));

        mList.add(new DetailsBean("unFreezeImage()", "To un-freeze image"));
        mList.add(new DetailsBean("isImageFreezed()", "To get freeze-image setting status"));
        return mList;
    }

    private List<ModeBean> getPictureModeDataList() {
        List<ModeBean> mList = new ArrayList<>();
        mList.add(new ModeBean("Picture_Normal",
                HHTPictureManager.EnumPictureMode.PICTURE_NORMAL.ordinal()));
        mList.add(new ModeBean("Picture_Soft",
                HHTPictureManager.EnumPictureMode.PICTURE_SOFT.ordinal()));
        mList.add(new ModeBean("Picture_Vivid",
                HHTPictureManager.EnumPictureMode.PICTURE_VIVID.ordinal()));
        mList.add(new ModeBean("Picture_User",
                HHTPictureManager.EnumPictureMode.PICTURE_USER.ordinal()));

        return mList;
    }

    private List<ModeBean> getVideoArcTypeDataList() {
        List<ModeBean> mList = new ArrayList<>();

        mList.add(new ModeBean("Arc_16x9",
                HHTPictureManager.VIDEO_ARC_16x9));
        mList.add(new ModeBean("Arc_4x3",
                HHTPictureManager.VIDEO_ARC_4x3));
        mList.add(new ModeBean("Arc_Dotbydot",
                HHTPictureManager.VIDEO_ARC_DOTBYDOT));
        return mList;
    }

    private List<ModeBean> getVideoItemDataList() {
        List<ModeBean> mList = new ArrayList<>();
        mList.add(new ModeBean("Picture_Brightness",
                HHTPictureManager.PICTURE_BRIGHTNESS));
        mList.add(new ModeBean("Picture_Contrast",
                HHTPictureManager.PICTURE_CONTRAST));
        mList.add(new ModeBean("Picture_Saturation",
                HHTPictureManager.PICTURE_SATURATION));

        mList.add(new ModeBean("Picture_Sharpness",
                HHTPictureManager.PICTURE_SHARPNESS));
        mList.add(new ModeBean("Picture_Hue",
                HHTPictureManager.PICTURE_HUE));
        mList.add(new ModeBean("Picture_Backlight",
                HHTPictureManager.PICTURE_BACKLIGHT));
        return mList;
    }

    private List<ModeBean> getColorTempratureIdxDataList() {

        List<ModeBean> mList = new ArrayList<>();
        mList.add(new ModeBean("Color_Temp_Cool",
                HHTPictureManager.COLOR_TEMP_COOL));
        mList.add(new ModeBean("Color_Temp_Nature",
                HHTPictureManager.COLOR_TEMP_NATURE));
        mList.add(new ModeBean("Color_Temp_Warm",
                HHTPictureManager.COLOR_TEMP_WARM));
        mList.add(new ModeBean("Color_Temp_User1",
                HHTPictureManager.COLOR_TEMP_USER1));
        mList.add(new ModeBean("Color_Temp_User2",
                HHTPictureManager.COLOR_TEMP_USER2));
        return mList;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                showDialog(getPictureModeDataList(), "setPictureMode", position);
                break;
            case 1:
                if (mHHTPictureManager != null) {
                    int mPictureVolume = mHHTPictureManager.getPictureMode();
                    ToastUtils.showShortToast("getPictureMode==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().getPictureMode();
                break;
            case 2:
                showDialog(getVideoArcTypeDataList(), "setVideoArcType", position);
                break;
            case 3:
                if (mHHTPictureManager != null) {
                    int mPictureVolume = mHHTPictureManager.getVideoArcType();
                    ToastUtils.showShortToast("getVideoArcType==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().getVideoArcType();
                break;
            case 4:
                showDialog(getVideoItemDataList(), "setVideoItem", position);
                break;
            case 5:
                if (mHHTPictureManager != null) {
                    int mPictureVolume = mHHTPictureManager.getVideoItem(1);
                    ToastUtils.showShortToast("getVideoItem==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().getVideoItem(1);
                break;
            case 6:
                showDialog(getColorTempratureIdxDataList(), "setColorTempratureIdx", position);
                break;
            case 7:
                if (mHHTPictureManager != null) {
                    int mPictureVolume = mHHTPictureManager.getColorTempratureIdx();
                    ToastUtils.showShortToast("getColorTempratureIdx==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().getColorTempratureIdx();
                break;
            case 8:
                if (mHHTPictureManager != null) {
                    boolean mPictureVolume = mHHTPictureManager.setPCHPos(1);
                    ToastUtils.showShortToast("setPCHPos是否成功==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().setPCHPos(1);
                break;
            case 9:
                if (mHHTPictureManager != null) {
                    int mPictureVolume = mHHTPictureManager.getPCHPos();
                    ToastUtils.showShortToast("getPCHPos==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().getPCHPos();
                break;
            case 10:
                if (mHHTPictureManager != null) {
                    boolean mPictureVolume = mHHTPictureManager.setPCVPos(1);
                    ToastUtils.showShortToast("setPCVPos是否成功==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().setPCVPos(1);
                break;
            case 11:
                if (mHHTPictureManager != null) {
                    int mPictureVolume = mHHTPictureManager.getPCVPos();
                    ToastUtils.showShortToast("getPCVPos==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().getPCVPos();
                break;
            case 12:
                if (mHHTPictureManager != null) {
                    boolean mPictureVolume = mHHTPictureManager.setPCClock(1);
                    ToastUtils.showShortToast("setPCClock是否成功==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().setPCClock(1);
                break;
            case 13:
                if (mHHTPictureManager != null) {
                    int mPictureVolume = mHHTPictureManager.getPCClock();
                    ToastUtils.showShortToast("getPCClock==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().getPCClock();
                break;
            case 14:
                if (mHHTPictureManager != null) {
                    boolean mPictureVolume = mHHTPictureManager.setPCPhase(1);
                    ToastUtils.showShortToast("setPCPhase是否成功==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().setPCPhase(1);
                break;
            case 15:
                if (mHHTPictureManager != null) {
                    int mPictureVolume = mHHTPictureManager.getPCPhase();
                    ToastUtils.showShortToast("getPCPhase==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().getPCPhase();
                break;
            case 16:
                if (mHHTPictureManager != null) {
                    boolean mPictureVolume = mHHTPictureManager.execAutoPc();
                    ToastUtils.showShortToast("execAutoPc是否成功==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().execAutoPc();
                break;
            case 17:
                if (mHHTPictureManager != null) {
                    boolean mPictureVolume = mHHTPictureManager.freezeImage();
                    ToastUtils.showShortToast("freezeImage是否成功==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().freezeImage();
                break;
            case 18:
                if (mHHTPictureManager != null) {
                    boolean mPictureVolume = mHHTPictureManager.unFreezeImage();
                    ToastUtils.showShortToast("unFreezeImage是否成功==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().unFreezeImage();
                break;
            case 19:
                if (mHHTPictureManager != null) {
                    boolean mPictureVolume = mHHTPictureManager.isImageFreezed();
                    ToastUtils.showShortToast("isImageFreezed是否成功==" + mPictureVolume);
                }
                //  HHTPictureManager.getInstance().isImageFreezed();
                break;
        }
    }

    private void showDialog(final List<ModeBean> mListData, String title, final int positionType) {

        showMiddleDialog(mListData, title, new OnMiddleDialogListener() {
            @Override
            public void onItemClick(int position) {
//                ToastUtils.showShortToast(mListData.get(position).getModeName()
//                        + "    typeStr==" + mListData.get(position).getTypeStr()
//                        + "     typeInt==" + mListData.get(position).getTypeInt());
                switch (positionType) {
                    case 0:
                        if (mHHTPictureManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean mPictureVolume = mHHTPictureManager.setPictureMode(typeInt);
                            ToastUtils.showShortToast("setPictureMode==" + typeInt + "    设置是否成功==" + mPictureVolume);
                        }
                        // HHTPictureManager.getInstance().setPictureMode();
                        break;
                    case 2:
                        if (mHHTPictureManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean mPictureVolume = mHHTPictureManager.setVideoArcType(typeInt);
                            ToastUtils.showShortToast("setVideoArcType==" + typeInt + "    设置是否成功==" + mPictureVolume);
                        }
                        // HHTPictureManager.getInstance().setVideoArcType(mListData.get(position).getTypeInt());
                        break;
                    case 4:
                        if (mHHTPictureManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean mPictureVolume = mHHTPictureManager.setVideoItem(typeInt, 0);
                            ToastUtils.showShortToast("setVideoItem==" + typeInt + "    设置是否成功=="
                                    + mPictureVolume);
                        }
                        //  HHTPictureManager.getInstance().setVideoItem(mListData.get(position).getTypeInt(),0);
                        break;
                    case 6:
                        if (mHHTPictureManager != null) {
                            int typeInt = mListData.get(position).getTypeInt();
                            boolean mPictureVolume = mHHTPictureManager.setColorTempratureIdx(typeInt);
                            ToastUtils.showShortToast("setColorTempratureIdx==" + typeInt + "    设置是否成功=="
                                    + mPictureVolume);
                        }
                        // HHTPictureManager.getInstance().setColorTempratureIdx(mListData.get(position).getTypeInt());
                        break;

                }

            }
        });
    }
}
