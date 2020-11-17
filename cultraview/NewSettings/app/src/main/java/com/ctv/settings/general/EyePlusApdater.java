package com.ctv.settings.general;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.utils.DataTool;


public class EyePlusApdater extends BaseAdapter {
    private final Context ctvContext;

    private final String[] eye_plus_strings;
    private String[] eye_plus_description;

    private int select;
    public EyePlusApdater(Context context, String[] date_format_strings, int select) {
        ctvContext = context;
        this.eye_plus_strings = date_format_strings;
        this.select = select;
        if (DataTool.IS_AH_EDU_QD) {
            eye_plus_description = context.getResources().getStringArray(R.array.eye_plus_description_Light);
        }else {
            eye_plus_description = context.getResources().getStringArray(R.array.eye_plus_description);
        }
    }
    public void setSelect(int select) {
        this.select = select;
    }
    @Override
    public int getCount() {
        return eye_plus_strings.length;
    }

    @Override
    public Object getItem(int i) {
        return eye_plus_strings[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(ctvContext).inflate(R.layout.eye_plus_item,
                    viewGroup, false);
        }
        TextView tv_item_eye_plus = (TextView) view
                .findViewById(R.id.tv_item_eye_plus);
        TextView tv_item_eye_plus_description = (TextView) view
                .findViewById(R.id.tv_item_eye_plus_description);
        tv_item_eye_plus.setText(eye_plus_strings[i]);
        tv_item_eye_plus_description.setText(eye_plus_description[i]);
        ImageView eye_plus_item_ivs = (ImageView) view
                .findViewById(R.id.eye_plus_item_ivs);
        if (i == select) {
            eye_plus_item_ivs.setBackgroundResource(R.drawable.item_rigth_dot);
        } else {
            eye_plus_item_ivs.setBackgroundResource(R.drawable.transparency_bg);
        }
        return view;
    }
}
