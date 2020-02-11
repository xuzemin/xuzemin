package com.hht.middleware.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hht.middleware.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: chenhu
 * Time: 2019/12/12 17:52
 * Description do somethings
 */
public class MainFragmentAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mListData;

    public MainFragmentAdapter(Context context, List<String> listData) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_main_item_layout, parent, false);
            mViewHolder.mTextView = convertView.findViewById(R.id.main_fragment_item_tv);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if (mListData != null && mListData.size() > 0) {
            mViewHolder.mTextView.setText(mListData.get(position));
        }
        return convertView;
    }

    static class ViewHolder {
        TextView mTextView;
    }
}
