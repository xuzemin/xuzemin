
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

public class InputMethodAdapter extends BaseAdapter {
    private Context ctvContext;

    private List<InputMethodInfo> inputMethodInfoList;

    private int index = -1;

    public InputMethodAdapter(Context context, List<InputMethodInfo> inputMethodInfoList) {
        ctvContext = context;
        this.inputMethodInfoList = inputMethodInfoList;
    }

    @Override
    public int getCount() {
        return inputMethodInfoList.size();
    }

    public void setIndex(int index) {
        this.index = index;
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
            contentview = LayoutInflater.from(ctvContext).inflate(
                    R.layout.language_and_inputmethod_item, parent, false);
        }
        TextView inputmethod_tv = (TextView) contentview
                .findViewById(R.id.language_inputmethod_item_tv);
        ImageView inputmethod_iv = (ImageView) contentview
                .findViewById(R.id.language_inputmethod_item_iv);

        // inputMethod app name
        CharSequence label = inputMethodInfoList.get(position).loadLabel(
                ctvContext.getPackageManager());
        inputmethod_tv.setText(label + ctvContext.getResources().getString(R.string.setting));
        if (position == index) {
            inputmethod_iv.setBackgroundResource(R.mipmap.item_rigth_focus);
            inputmethod_tv.setTextColor(ctvContext.getResources().getColor(R.color.white));
        } else {
            inputmethod_iv.setBackgroundResource(R.mipmap.item_rigth_unfocus);
            inputmethod_tv.setTextColor(ctvContext.getResources().getColor(R.color.half_white));
        }
        return contentview;
    }
}
