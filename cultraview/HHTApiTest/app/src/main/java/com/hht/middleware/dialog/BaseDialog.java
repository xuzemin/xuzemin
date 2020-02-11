package com.hht.middleware.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hht.middleware.R;


/**
 * Author: chenhu
 * Time: 2019/12/16 14:28
 * Description do somethings
 */

public abstract class BaseDialog {
    private CusDialog mCusDialog;
    protected TextView mTitleTextView;


    public BaseDialog(Context context) {
        mCusDialog = new CusDialog.Builder()
                .setContext(context)
                .setLayoutId(R.layout.base_dialog_layout)
                .setWidth(660)
                .setHeight(240)
                .builder();
        mTitleTextView = mCusDialog.getViewbyId(R.id.base_dialog_title);
        FrameLayout frameLayout = mCusDialog.getViewbyId(R.id.base_dialog_content);
        View netView = LayoutInflater.from(context).inflate(getLayoutId(), null);
        frameLayout.removeAllViews();
        frameLayout.addView(netView);

        Window window = mCusDialog.getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.setTitle("mill_base_dialog");
            window.setAttributes(params);
        }
    }

    protected void setDialogHeight(int height) {
        Window window = mCusDialog.getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.height = height;
            window.setAttributes(lp);
        }
    }

    protected void dismiss() {
        if (mCusDialog != null) {
            mCusDialog.dismiss();
            mCusDialog = null;
        }
    }

    public abstract int getLayoutId();

    public void setTitleTextView(String title) {
        if (!TextUtils.isEmpty(title) && mTitleTextView != null) {
            mTitleTextView.setText(title);
        }
    }

    public <T extends View> T getViewById(int viewId) {
        return mCusDialog.getViewbyId(viewId);
    }


}
