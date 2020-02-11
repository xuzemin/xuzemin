package com.hht.middleware.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hht.middleware.R;
import com.hht.middleware.bean.ModeBean;
import com.hht.middleware.dialog.MiddleWareDialog;
import com.hht.middleware.listener.OnDelayedClickListener;
import com.hht.middleware.listener.OnMiddleDialogListener;
import com.hht.middleware.listener.OnTitleListener;

import java.util.List;

/**
 * Author: chenhu
 * Time: 2019/12/12 15:16
 * Description do somethings
 */
public abstract class BaseFragment extends Fragment implements OnTitleListener {
    protected Activity mActivity;
    private ViewGroup mViewGroup;
    private ImageView mBackImageView, mNextImageView;
    protected TextView mTitleTextVew;
    protected OnTitleListener mOnTitleListener;
    protected MiddleWareDialog mMiddleWareDialog;

    protected void setOnTitleListener(OnTitleListener onTitleListener) {
        mOnTitleListener = onTitleListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutId() <= 0) {
            throw new NullPointerException("layout must no null");
        }
        initActivityView();
        mViewGroup = (ViewGroup) inflater.inflate(getLayoutId(), container, false);
        mViewGroup.setClickable(true);
        mViewGroup.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        initView();
        setOnTitleListener(this);
        return mViewGroup;
    }

    /**
     * 因为切换语言生命周期会变化，所以必须这个时候重新初始华
     */
    private void initActivityView() {
        mBackImageView = mActivity.findViewById(R.id.main_layout_back_iv);
        mTitleTextVew = mActivity.findViewById(R.id.main_layout_title_tv);
        mNextImageView = mActivity.findViewById(R.id.main_layout_next_iv);
        if (mBackImageView != null) {
            mBackImageView.setOnClickListener(
                    new OnDelayedClickListener() {
                        @Override
                        protected void onDelayedClick(View view) {
                            if (mOnTitleListener != null) {
                                mOnTitleListener.OnBackListener();
                            }
                        }
                    });
        }

        if (mNextImageView != null) {
            mNextImageView.setOnClickListener(
                    new OnDelayedClickListener() {
                        @Override
                        protected void onDelayedClick(View view) {
                            if (mOnTitleListener != null) {
                                mOnTitleListener.OnIntroduceListener();
                            }
                        }
                    });
        }
    }


    protected abstract void initView();

    protected abstract int getLayoutId();


    protected void setTitleTextVew(String title) {
        if (mTitleTextVew != null && !TextUtils.isEmpty(title)) {
            mTitleTextVew.setText(title);
        }
    }


    public <T extends View> T findViewById(int id) {
        return mViewGroup.findViewById(id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    protected void replaceFragment(Fragment showFragment) {
        FragmentUtils.getInstance().replaceFragment(showFragment);
    }

    protected void showMiddleDialog(List<ModeBean> mListData, String title, int numColumns, OnMiddleDialogListener listener) {
        mMiddleWareDialog = new MiddleWareDialog(mActivity);
        mMiddleWareDialog.show(mListData, title, numColumns);
        if (listener != null) {
            mMiddleWareDialog.setOnMiddleDialogListener(listener);
        }
    }

    protected void showMiddleDialog(List<ModeBean> mListData, String title, OnMiddleDialogListener listener) {
        mMiddleWareDialog = new MiddleWareDialog(mActivity);
        mMiddleWareDialog.show(mListData, title);
        if (listener != null) {
            mMiddleWareDialog.setOnMiddleDialogListener(listener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMiddleWareDialog != null) {
            mMiddleWareDialog.setMiddleDialogDismiss();
            mMiddleWareDialog = null;
        }
        if (mOnTitleListener != null) {
            mOnTitleListener = null;
        }
    }
}
