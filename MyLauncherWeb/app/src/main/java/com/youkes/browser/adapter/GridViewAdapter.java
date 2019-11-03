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
import com.youkes.browser.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.youkes.browser.activity.MainActivity.mBackgroudData;
import static com.youkes.browser.activity.MainActivity.mGridData;
import static com.youkes.browser.activity.MainActivity.urlList;


public class GridViewAdapter extends ArrayAdapter<GridItem> {
    private Context mContext;
    private int  layoutResourceId;
    private boolean isBackground = false;
    int icno[] = {R.mipmap.settings,R.mipmap.google_play,R.mipmap.photo,
            R.mipmap.throme, R.mipmap.logo_amazon, R.mipmap.logo_bbc, R.mipmap.logo_cnn,
            R.mipmap.logo_discovery, R.mipmap.logo_economist, R.mipmap.logo_fb,
            R.mipmap.logo_googlenews, R.mipmap.logo_ig, R.mipmap.logo_mlb,
            R.mipmap.logo_nasa, R.mipmap.logo_nba, R.mipmap.logo_qiy,
            R.mipmap.logo_spotify, R.mipmap.logo_ted, R.mipmap.logo_twitter,
            R.mipmap.logo_weibo, R.mipmap.logo_youku, R.mipmap.logo_youtube
    };
    public GridViewAdapter(Context context, int resource, ArrayList<GridItem> objects,List<Map> urlList) {
        super(context, resource, objects);
        this.mContext = context; this.layoutResourceId = resource;
    }

    public GridViewAdapter(Context context, int resource, ArrayList<GridItem> objects) {
        super(context, resource, objects);
        this.mContext = context; this.layoutResourceId = resource;
    }


    public GridViewAdapter(Context context, int resource, ArrayList<GridItem> objects,boolean isBackground) {
        super(context, resource, objects);
        this.mContext = context; this.layoutResourceId = resource;
        this.isBackground = isBackground;
    }

    @Override
    public int getCount() {
        if(isBackground){
            if(mBackgroudData == null || mBackgroudData.size() == 0){
                return 0;
            }
            return mBackgroudData.size();
        }else{
            if(urlList == null || urlList.size() == 0){
                return 0;
            }
            return urlList.size();
        }
    }

    public boolean isBackground(){
        return isBackground;
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
            holder.delete = convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(!isBackground) {
            if (urlList != null && urlList.size() > 0) {
                LogUtil.e("position" + position);
                LogUtil.e("urlList.get(position).get(\"id\")" + urlList.get(position).get("id"));
                LogUtil.e("position name" + urlList.get(position).get("name"));
                holder.textView.setText(urlList.get(position).get("name").toString());
                if (MainActivity.applicationNumber != -1 && position <= MainActivity.applicationNumber) {
                    holder.textView.setText(urlList.get(position).get("name").toString());
                    holder.imageView.setBackground(mContext.getResources().getDrawable(icno[(int) urlList.get(position).get("id") - 1]));
                } else {
                    if (MainActivity.packageInfoList != null && MainActivity.packageInfoList.size() > 0) {
                        holder.imageView.setBackground(MainActivity.packageInfoList.get(position - MainActivity.applicationNumber - 1).applicationInfo.loadIcon(MainActivity.pm));
                    }
                }
                if (MainActivity.isAdmin) {
                    if (position <= MainActivity.applicationNumber) {
                        if ((int) (urlList.get(position).get("show")) == 0) {
                            holder.delete.setVisibility(View.GONE);
                        } else {
                            holder.delete.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.delete.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.delete.setVisibility(View.GONE);
                }
            }
        }else{
            GridItem item = mBackgroudData.get(position);
            holder.textView.setText(item.getTitle());
            holder.imageView.setBackground(item.getImage());
            holder.delete.setVisibility(View.GONE);
            if (isBackground) {
                if (MainActivity.isDelete) {
                    if (position != 0) {
                        holder.delete.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.delete.setVisibility(View.GONE);
                }
            }
        }
//        Picasso.with(mContext).load(item.getImage()).into(holder.imageView);
        return convertView;
    }
    private class ViewHolder {
        TextView textView; ImageView imageView;ImageView delete;
    }
}

