
package com.ctv.settings.timeanddate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.utils.CommonConsts;

import java.util.HashMap;
import java.util.List;

public class TimeZoneAdapter extends BaseAdapter {

    private Context ctvContext;

    private List<HashMap<String, String>> timezoneSortedList;

    private int select;

    public TimeZoneAdapter(Context context, List<HashMap<String, String>> timezoneSortedList,
            int select) {
        ctvContext = context;
        this.timezoneSortedList = timezoneSortedList;
        this.select = select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    @Override
    public int getCount() {
        return timezoneSortedList.size();
    }

    @Override
    public Object getItem(int index) {
        return timezoneSortedList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View contentview, ViewGroup parent) {
        ViewHolder holder;
        if (contentview == null) {
            holder = new ViewHolder();
            contentview = LayoutInflater.from(ctvContext).inflate(R.layout.timezone_select_item,
                    parent, false);
            holder.zone_select_item_tv = (TextView) contentview
                    .findViewById(R.id.timezone_select_item_tv);
            holder.zone_select_value_tv = (TextView) contentview
                    .findViewById(R.id.timezone_select_value_tv);
            holder.zone_select_item_ivs = (ImageView) contentview
                    .findViewById(R.id.timezone_select_item_ivs);
            contentview.setTag(holder);
        } else {
            holder = (ViewHolder) contentview.getTag();
        }
        HashMap<String, String> zoneMap = timezoneSortedList.get(position);
        holder.zone_select_item_tv.setText(zoneMap.get(CommonConsts.KEY_DISPLAYNAME));
        holder.zone_select_value_tv.setText(zoneMap.get(CommonConsts.KEY_GMT));
        if (position == select) {
            holder.zone_select_item_ivs.setBackgroundResource(R.drawable.item_rigth_dot);
        } else {
            holder.zone_select_item_ivs.setBackgroundResource(R.drawable.transparency_bg);
        }
        return contentview;
    }

    private class ViewHolder {
        TextView zone_select_item_tv;

        TextView zone_select_value_tv;

        ImageView zone_select_item_ivs;
    }
}
