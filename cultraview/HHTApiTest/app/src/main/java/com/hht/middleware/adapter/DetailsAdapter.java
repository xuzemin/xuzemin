package com.hht.middleware.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hht.middleware.R;
import com.hht.middleware.bean.DetailsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: chenhu
 * Time: 2019/12/12 17:52
 * Description do somethings
 */
public class DetailsAdapter extends BaseAdapter {
    private Context mContext;
    private List<DetailsBean> mListData;

    public DetailsAdapter(Context context, List<DetailsBean> listData) {
        mContext = context;
        if (listData == null) {
            mListData = new ArrayList<>();
        } else {
            mListData = listData;
        }
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_details_item_layout, parent, false);
            mViewHolder.mTitleTextView = convertView.findViewById(R.id.fragment_details_item_title_tv);
            mViewHolder.mTypeTextView = convertView.findViewById(R.id.fragment_details_item_type_tv);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (mListData != null && mListData.size() > 0) {
            String title = mListData.get(position).getTitle();
            String type = mListData.get(position).getType();
            if (!TextUtils.isEmpty(title)) {
                if (title.startsWith("set") || title.startsWith("start")) {
                    mViewHolder.mTitleTextView.setTextColor(mContext.getResources().getColor(R.color.blue));
                } else {
                    mViewHolder.mTitleTextView.setTextColor(Color.WHITE);
                }
                mViewHolder.mTitleTextView.setText(title);
            }
            if (!TextUtils.isEmpty(type)) {
                mViewHolder.mTypeTextView.setText(type);
            }

        }
        return convertView;
    }

    static class ViewHolder {
        TextView mTitleTextView;
        TextView mTypeTextView;
    }
}
