package com.ctv.newlauncher.adapter;

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
import com.ctv.newlauncher.bean.AppInfo;
import com.ctv.newlauncher.imp.OnGridviewItemChangeListener;

import java.util.List;


/**
 * Created by chenweiguo on 2018/3/5.
 */

public class OthersGridviewAadpter extends BaseAdapter implements OnGridviewItemChangeListener {
    private LayoutInflater inflater;
    private List<AppInfo> packages;
    private int selectedId = -1;
    private int selectedId_now = -1;

    public void setAppData(List<AppInfo> packages){
        this.packages = packages;
        notifyDataSetChanged();
    }

    public OthersGridviewAadpter(Context context, List<AppInfo> packages){
        this.packages = packages;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return packages == null?0:packages.size();
    }

    @Override
    public Object getItem(int i) {
        return packages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setSelectedId(int selectedId){
        this.selectedId = selectedId;
        notifyDataSetChanged();
        Log.d("hhh", "setSelectedId ="+selectedId);
    }
    public void setSelectedId_now(int selectedId_now){
        Log.d("hhh", "setSelectedId_now: ="+selectedId_now);
        this.selectedId_now = selectedId_now;
        notifyDataSetChanged();
    }


    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;

        AppInfo appInfo = packages.get(i);
        String appName = appInfo.getAppName();
        Drawable icon = appInfo.getAppIcon();
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.others_gridview_item,null);
            //  convertView.setAlpha(0.5f);
            holder.tv =  convertView.findViewById(R.id.others_item_tv);
            holder.iv =  convertView.findViewById(R.id.others_item_iv);
            convertView.setTag(holder);
            // convertView.clearAnimation();
        }else{
            //convertView.clearAnimation();
            holder =(ViewHolder) convertView.getTag();
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

    @Override
    public void onGridviewItemChangeListener(int state) {

    }

    class ViewHolder{
        ImageView iv ;
        TextView tv;
    }

}
