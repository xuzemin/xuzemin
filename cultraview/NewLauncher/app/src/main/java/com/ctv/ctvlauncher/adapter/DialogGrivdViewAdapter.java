package com.ctv.ctvlauncher.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctv.ctvlauncher.R;
import com.ctv.ctvlauncher.bean.AppInfo;

import java.util.List;

public class DialogGrivdViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<AppInfo> packages;
    private int selectedId = -1;
    private int selectedId_now = -1;
    public void setAppData(List<AppInfo> packages){
        this.packages = packages;
        notifyDataSetChanged();
    }
    public DialogGrivdViewAdapter(Context context, List<AppInfo> packages){
        this.packages = packages;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return packages == null?0:packages.size();
    }

    @Override
    public Object getItem(int position) {
        return packages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        DialogViewHolder holder;

        AppInfo appInfo = packages.get(i);
        String appName = appInfo.getAppName();
        Drawable icon = appInfo.getAppIcon();
        if(convertView == null){
            holder = new DialogViewHolder();
            convertView = inflater.inflate(R.layout.others_gridview_item,null);
            //  convertView.setAlpha(0.5f);
            holder.tv =  convertView.findViewById(R.id.others_item_tv);
            holder.iv =  convertView.findViewById(R.id.others_item_iv);
            convertView.setTag(holder);
            // convertView.clearAnimation();
        }else{
            //convertView.clearAnimation();
            holder =(DialogViewHolder) convertView.getTag();
        }
        holder.iv.setImageDrawable(icon);
        //  Glide.with(context).load(icon).into( holder.iv);
        holder.tv.setText(appName);
        convertView.clearAnimation();
        if (i==selectedId_now){
            convertView.setScaleY(1f);
            convertView.setScaleX(1f);
        }
        if(i == selectedId){

            Log.d("hhh", "adapter--- getView: i="+i);
            convertView.setScaleY(1.25f);
            convertView.setScaleX(1.25f);

        }
        return convertView;
    }
    class DialogViewHolder{
        ImageView iv ;
        TextView tv;
    }
}
