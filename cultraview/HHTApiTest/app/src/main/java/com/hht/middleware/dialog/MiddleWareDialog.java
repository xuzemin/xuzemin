package com.hht.middleware.dialog;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.hht.middleware.R;
import com.hht.middleware.adapter.MiddleDialogAdapter;
import com.hht.middleware.bean.ModeBean;
import com.hht.middleware.listener.OnMiddleDialogListener;

import java.util.List;

/**
 * Author: chenhu
 * Time: 2019/12/16 16:34
 * Description do somethings
 */
public class MiddleWareDialog extends BaseDialog implements AdapterView.OnItemClickListener {
    private MiddleDialogAdapter mMiddleDialogAdapter;
    private GridView mGridView;
    private Context mContext;
    private OnMiddleDialogListener mOnMiddleDialogListener;

    public void setOnMiddleDialogListener(OnMiddleDialogListener onMiddleDialogListener) {
        mOnMiddleDialogListener = onMiddleDialogListener;
    }

    public MiddleWareDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.middle_base_dialog_layout;
    }

    public MiddleWareDialog show(List<ModeBean> mListData, String title, int numColumns) {
        setTitleTextView(title);
        mGridView = getViewById(R.id.middle_dialog_grid_view);
        mGridView.setNumColumns(numColumns);
        if (mListData != null && mListData.size() > 0) {
            mMiddleDialogAdapter = new MiddleDialogAdapter(mContext, mListData);
            mGridView.setAdapter(mMiddleDialogAdapter);
            mGridView.setOnItemClickListener(this);
        }
        return this;
    }

    public MiddleWareDialog show(List<ModeBean> mListData, String title) {
        setTitleTextView(title);
        mGridView = getViewById(R.id.middle_dialog_grid_view);
        mGridView.setNumColumns(3);
        if (mListData != null && mListData.size() > 0) {
            mMiddleDialogAdapter = new MiddleDialogAdapter(mContext, mListData);
            mGridView.setAdapter(mMiddleDialogAdapter);
            mGridView.setOnItemClickListener(this);
        }
        return this;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnMiddleDialogListener != null) {
            mOnMiddleDialogListener.onItemClick(position);
        }
        dismiss();
        mOnMiddleDialogListener = null;

    }

    public void setMiddleDialogDismiss() {
        dismiss();
    }

}
