package com.ctv.settings.about.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ctv.settings.about.Bean.AboutTvInfo;
import com.ctv.settings.about.R;

import java.util.List;

public class AboutTvAdapter extends BaseAdapter {
            private Context context;
        private List<AboutTvInfo>aboutTvInfos;
        public AboutTvAdapter(Context context,List<AboutTvInfo>aboutTvInfos){
            this.context=context;
            this.aboutTvInfos=aboutTvInfos;

        }
    public void setAboutTvInfos(List<AboutTvInfo> aboutTvInfos) {
        this.aboutTvInfos = aboutTvInfos;
    }

    public void setAboutTvInfo(AboutTvInfo aboutTvInfo, int position) {
        aboutTvInfos.set(position, aboutTvInfo);
    }
    @Override
    public int getCount() {
        return aboutTvInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return aboutTvInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.about_tv_item,viewGroup, false);
        }
        AboutTvInfo aboutTvInfo = aboutTvInfos.get(i);
        TextView about_tv_item_tv = (TextView) view.findViewById(R.id.tv_about_tv_item);
        TextView about_tv_value_tv = (TextView) view.findViewById(R.id.tv_about_tv_value);
        about_tv_item_tv.setText(aboutTvInfo.getAboutString());
        about_tv_value_tv.setText(aboutTvInfo.getAboutValue());
        return view;

    }
}
