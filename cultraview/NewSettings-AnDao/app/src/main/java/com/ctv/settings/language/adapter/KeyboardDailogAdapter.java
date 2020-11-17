
package com.ctv.settings.language.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctv.settings.R;

import java.util.List;


public class KeyboardDailogAdapter extends BaseAdapter {

    private final Context ctvContext;

    List<InputMethodInfo> inputMethodInfoList;

    private final String default_input_method;

    private int select;

    public KeyboardDailogAdapter(Context context, List<InputMethodInfo> inputMethodInfoList,
            String default_input_method) {
        ctvContext = context;
        this.inputMethodInfoList = inputMethodInfoList;
        this.default_input_method = default_input_method;
        select = -1;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    @Override
    public int getCount() {
        return inputMethodInfoList.size();
    }

    @Override
    public Object getItem(int index) {
        return inputMethodInfoList.get(index);
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
        ImageView language_dialog_item_ivs = (ImageView) contentview
                .findViewById(R.id.language_dialog_item_ivs);
        InputMethodInfo inputMethodInfo = inputMethodInfoList.get(position);
        // inputMethod app name
        language_dialog_item_tv.setText(inputMethodInfo.loadLabel(ctvContext.getPackageManager())
                + "");
        if (select == -1) {
            if (default_input_method.equals(inputMethodInfo.getId())) {
                language_dialog_item_ivs.setBackgroundResource(R.drawable.item_rigth_dot);
            } else {
                language_dialog_item_ivs.setBackgroundResource(R.drawable.transparency_bg);
            }
        } else {
            if (select == position) {
                language_dialog_item_ivs.setBackgroundResource(R.drawable.item_rigth_dot);
            } else {
                language_dialog_item_ivs.setBackgroundResource(R.drawable.transparency_bg);
            }
        }

        return contentview;
    }
}
