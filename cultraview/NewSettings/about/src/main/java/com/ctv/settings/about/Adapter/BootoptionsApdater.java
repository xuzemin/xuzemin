package com.ctv.settings.about.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ctv.settings.about.R;

public class BootoptionsApdater extends BaseAdapter {
    private final Context ctvContext;

    private final String[] boot_option_strings;

    private int select;
    public BootoptionsApdater(Context context, String[] date_format_strings, int select) {
        ctvContext = context;
        this.boot_option_strings = date_format_strings;
        this.select = select;
    }
    public void setSelect(int select) {
        this.select = select;
    }
    @Override
    public int getCount() {
        return boot_option_strings.length;
    }

    @Override
    public Object getItem(int i) {
        return boot_option_strings[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(ctvContext).inflate(R.layout.boot_options_item,
                    viewGroup, false);
        }
        TextView date_format_item_tv = (TextView) view
                .findViewById(R.id.tv_item_boot_options);
        date_format_item_tv.setText(boot_option_strings[i]);
        ImageView date_format_item_ivs = (ImageView) view
                .findViewById(R.id.boot_option_item_ivs);
        if (i == select) {
            date_format_item_ivs.setBackgroundResource(R.drawable.item_rigth_dot);
        } else {
            date_format_item_ivs.setBackgroundResource(R.drawable.transparency_bg);
        }
        return view;
    }
}
