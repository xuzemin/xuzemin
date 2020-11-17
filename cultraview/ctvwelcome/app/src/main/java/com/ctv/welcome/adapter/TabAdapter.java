
package com.ctv.welcome.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;
import java.util.ArrayList;

public class TabAdapter extends BaseAdapter {
    private ArrayList<Integer> data = new ArrayList();

    class ViewHolder {
        ImageView titleIv;

        TextView txtStorageType;

        ViewHolder() {
        }
    }

    public TabAdapter(ArrayList<Integer> tabs) {
        this.data = tabs;
    }

    public int getCount() {
        return this.data.size();
    }

    public Object getItem(int position) {
        return this.data.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(VIPApplication.getContext()).inflate(
                    R.layout.listview_item_fb_tab, null);
            holder.titleIv = (ImageView) convertView.findViewById(R.id.adp_fb_tab_title);
            holder.txtStorageType = (TextView) convertView.findViewById(R.id.txt_storeage_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.titleIv.setImageResource(((Integer) this.data.get(position)).intValue());
        if (position == 0) {
            holder.txtStorageType.setText(VIPApplication.getContext().getString(
                    R.string.local_storage));
        } else if (position == 1) {
            holder.txtStorageType.setText(VIPApplication.getContext().getString(
                    R.string.mobile_device));
        }
        return convertView;
    }
}
