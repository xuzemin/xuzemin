
package com.ctv.settings.language.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctv.settings.R;


public class LanguageDailogAdapter extends BaseAdapter {

    private final Context ctvContext;

    private final String[] language_strings;

    private int select;

    public LanguageDailogAdapter(Context context, String[] language_strings, int select) {
        ctvContext = context;
        this.language_strings = language_strings;
        this.select = select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    @Override
    public int getCount() {
        return language_strings.length;
    }

    @Override
    public Object getItem(int index) {
        return language_strings[index];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View contentview, ViewGroup parent) {
        if (contentview == null) {
            contentview = LayoutInflater.from(ctvContext).inflate(R.layout.language_dialog_item,
                    parent, false);
        }
        TextView language_dialog_item_tv = (TextView) contentview
                .findViewById(R.id.language_dialog_item_tv);
//        if (SystemPropertiesNew.get("ro.product.brand").equals("MITASHI")) {
            language_dialog_item_tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//        }
        language_dialog_item_tv.setText(language_strings[position].trim());
        ImageView language_dialog_item_ivs = (ImageView) contentview
                .findViewById(R.id.language_dialog_item_ivs);
        if (position == select) {
            language_dialog_item_ivs.setBackgroundResource(R.drawable.item_rigth_dot);
        } else {
            language_dialog_item_ivs.setBackgroundResource(R.drawable.transparency_bg);
        }
        return contentview;
    }
}
