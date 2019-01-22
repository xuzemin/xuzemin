package com.youkes.browser.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youkes.browser.R;
import com.youkes.browser.activity.MainActivity;
import com.youkes.browser.object.GridItem;

import java.util.ArrayList;


public class GridViewAdapter extends ArrayAdapter<GridItem> {
    private Context mContext;
    private int  layoutResourceId;
    private boolean isBackground = false;
    public ArrayList<GridItem> mGridData;
    public GridViewAdapter(Context context, int resource, ArrayList<GridItem> objects) {
        super(context, resource, objects);
        this.mContext = context; this.layoutResourceId = resource;
        this.mGridData = objects;
    }

    public GridViewAdapter(Context context, int resource, ArrayList<GridItem> objects,boolean isBackground) {
        super(context, resource, objects);
        this.mContext = context; this.layoutResourceId = resource;
        this.mGridData = objects;
        this.isBackground = isBackground;
    }

    public boolean isBackground(){
        return isBackground;
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
            if(isBackground){
                holder.delete = convertView.findViewById(R.id.delete);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GridItem item = mGridData.get(position);
        holder.textView.setText(item.getTitle());
        holder.imageView.setBackground(item.getImage());
        if(isBackground) {
            if (MainActivity.isDelete) {
                if (position != 0) {
                    holder.delete.setVisibility(View.VISIBLE);
                }
            } else {
                holder.delete.setVisibility(View.GONE);
            }
        }
//        Picasso.with(mContext).load(item.getImage()).into(holder.imageView);
        return convertView;
    } private class ViewHolder {
        TextView textView; ImageView imageView;ImageView delete;
    }
}

