
package com.protruly.floatwindowlib.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.protruly.floatwindowlib.R;


public class PageItemAdapter extends BaseAdapter {
    private Context cvContext;// 上下文

    int select;

    int focus_select;

    String[] list_item_string;

    public PageItemAdapter(Context context, int select, String[] list_item_string) {
        cvContext = context;
        focus_select = select;
        this.select = select;
        this.list_item_string = list_item_string;
    }

    public void setSelectImage(int position) {
        select = position;
        focus_select = position;
    }

    @Override
    public int getCount() {
        return list_item_string.length;
    }

    @Override
    public long getItemId(int position) {
        focus_select = position;
        return position;
    }

    public int getCurrentFocus() {
        return focus_select;
    }

    @Override
    public View getView(int position, View contentview, ViewGroup parent) {
        if (contentview == null) {
            contentview = LayoutInflater.from(cvContext).inflate(R.layout.list_item, parent, false);
        }
        ImageView iv_menu_depth = (ImageView) contentview.findViewById(R.id.iv_page_item);
        TextView tv_menu_depth = (TextView) contentview.findViewById(R.id.tv_page_item);
        tv_menu_depth.setText(list_item_string[position]);
        if (position == select) {
            iv_menu_depth.setVisibility(View.VISIBLE);
        } else {
            iv_menu_depth.setVisibility(View.INVISIBLE);
        }
        return contentview;
    }

    @Override
    public Object getItem(int arg0) {
        return list_item_string[arg0];
    }

    public void setData(String[] list_item_string) {
        this.list_item_string = list_item_string;
        notifyDataSetChanged();
    }
}
