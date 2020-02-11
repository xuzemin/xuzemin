package com.hht.middleware.dialog;

import android.content.Context;
import android.text.TextUtils;

import com.hht.middleware.R;
import com.hht.middleware.view.JustifyTextView;

/**
 * Author: chenhu
 * Time: 2019/12/16 17:23
 * Description do somethings
 */
public class IntroduceDialog extends BaseDialog {
    private JustifyTextView mTextView;
    private Context mContext;

    public IntroduceDialog(Context context) {
        super(context);
        mContext = context;
        setDialogHeight(600);
    }

    @Override
    public int getLayoutId() {
        return R.layout.introduce_base_dialog_layout;
    }

    public IntroduceDialog show(String introduce) {


        setTitleTextView(mContext.getString(R.string.title_introduce));
        mTextView = getViewById(R.id.introduce_dialog_tv);
        if (!TextUtils.isEmpty(introduce)) {
            mTextView.setText(introduce);
        }
        return this;
    }

    public void setIntroduceDialogHeight(int height) {
        setDialogHeight(height);
    }

}
