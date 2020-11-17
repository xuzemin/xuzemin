
package com.ctv.settings.timeanddate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctv.settings.R;


public class DateFormatAdapter extends BaseAdapter {

    private final Context ctvContext;

    private final String[] date_format_strings;

    private int select;

    public DateFormatAdapter(Context context, String[] date_format_strings, int select) {
        ctvContext = context;
        this.date_format_strings = date_format_strings;
        this.select = select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    @Override
    public int getCount() {
        return date_format_strings.length;
    }

    @Override
    public Object getItem(int index) {
        return date_format_strings[index];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View contentview, ViewGroup parent) {
        if (contentview == null) {
            contentview = LayoutInflater.from(ctvContext).inflate(R.layout.date_format_item,
                    parent, false);
        }
        TextView date_format_item_tv = (TextView) contentview
                .findViewById(R.id.date_format_item_tv);
        date_format_item_tv.setText(date_format_strings[position]);
        ImageView date_format_item_ivs = (ImageView) contentview
                .findViewById(R.id.date_format_item_ivs);
        if (position == select) {
            date_format_item_ivs.setBackgroundResource(R.drawable.item_rigth_dot);
        } else {
            date_format_item_ivs.setBackgroundResource(R.drawable.transparency_bg);
        }
        return contentview;
    }
}
