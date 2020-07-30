package com.ctv.settings.general;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctv.settings.R;


public class ShareUsbApdater extends BaseAdapter {
    private final Context ctvContext;

    private final String[] share_usb_strings;

    private int select;
    public ShareUsbApdater(Context context, String[] date_format_strings, int select) {
        ctvContext = context;
        this.share_usb_strings = date_format_strings;
        this.select = select;
    }
    public void setSelect(int select) {
        this.select = select;
    }
    @Override
    public int getCount() {
        return share_usb_strings.length;
    }

    @Override
    public Object getItem(int i) {
        return share_usb_strings[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(ctvContext).inflate(R.layout.share_usb_item,
                    viewGroup, false);
        }
        TextView date_format_item_tv = (TextView) view
                .findViewById(R.id.tv_item_share_usb);
        date_format_item_tv.setText(share_usb_strings[i]);
        ImageView date_format_item_ivs = (ImageView) view
                .findViewById(R.id.share_usb_item_ivs);
        if (i == select) {
            date_format_item_ivs.setBackgroundResource(R.drawable.item_rigth_dot);
        } else {
            date_format_item_ivs.setBackgroundResource(R.drawable.transparency_bg);
        }
        return view;
    }
}
