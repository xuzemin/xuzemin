package com.android.srd.launcher.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.srd.launcher.Object.GridItem;
import com.android.srd.launcher.R;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<GridItem> {
    private Context mContext;
    private int  layoutResourceId;
    public ArrayList<GridItem> mGridData = new ArrayList<GridItem>();
    public GridViewAdapter(Context context, int resource, ArrayList<GridItem> objects) {
        super(context, resource, objects);
        this.mContext = context; this.layoutResourceId = resource;
        this.mGridData = objects;
    }
    public void setGridData(ArrayList<GridItem> mGridData) {
        this.mGridData = mGridData; notifyDataSetChanged();
    }
    @SuppressLint("NewApi")
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.textView =  convertView.findViewById(R.id.text);
            holder.imageView =  convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GridItem item = mGridData.get(position);
        holder.textView.setText(item.getTitle());
        holder.imageView.setBackground(item.getImage());
//        Picasso.with(mContext).load(item.getImage()).into(holder.imageView);
        return convertView;
    } private class ViewHolder {
        TextView textView; ImageView imageView;
    }
}

