package com.ctv.settings.about.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ctv.settings.about.R;

public class UpdateAdapter extends BaseAdapter {
    private Context ctvContext;

    private String[] update_strings;
    public UpdateAdapter(Context context,String[] update_strings){
        ctvContext = context;
        this.update_strings = update_strings;
    }
    @Override
    public int getCount() {
        return update_strings.length;
    }

    @Override
    public Object getItem(int i) {
        return update_strings[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(ctvContext).inflate(R.layout.page_update_item,
                    viewGroup, false);
        }
        TextView update_item_tv = (TextView) view.findViewById(R.id.tv_update_item);
        update_item_tv.setText(update_strings[i]);
        return view;

    }
}
