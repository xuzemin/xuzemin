package mstar.factorymenu.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.bean.SoundVolOsdBean;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.factorymenu.ui.utils.Tools;

public class SubTitleAdapter extends ArrayAdapter<SoundVolOsdBean> {
    private int resourceId;
    private List<String> soundVolOsdBeans = new ArrayList<>();
    private Context context;
    private int mSelect = -1;

    public SubTitleAdapter(@NonNull Context context, int resource, List<String> soundVolOsdBeans) {
        super(context, resource);
        resourceId = resource;
        this.context = context;
        this.soundVolOsdBeans = soundVolOsdBeans;
    }

    public String getSelectString() {
        if (soundVolOsdBeans != null && mSelect != -1) {
            return soundVolOsdBeans.get(mSelect);
        }
        return "";
    }

    @Override
    public boolean isEnabled(int position) {
        LogUtils.d("soundVolOsdBeans.get(position):" + position + soundVolOsdBeans.get(position));
        if (!Tools.isADCFragmentShow() && soundVolOsdBeans.get(position).equals("ADC")) {
            LogUtils.d("isEnabled:");
            return false;
        }
        return true;
    }

    @Override
    public int getCount() {
        return soundVolOsdBeans.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder1 = null;
        if (null == convertView) {
            System.out.println("convertView == null" + " position:" + position);
            holder1 = new Holder();
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            holder1.view = convertView.findViewById(R.id.item);
            holder1.titleText = (TextView) convertView.findViewById(R.id.item_title);
            convertView.setTag(holder1);
        } else {
            holder1 = (Holder) convertView.getTag();
        }

        if (!Tools.isADCFragmentShow() && soundVolOsdBeans.get(position).equals("ADC")) {
            holder1.titleText.setTextColor(Color.parseColor("#696969"));
        } else {
            holder1.titleText.setTextColor(Color.parseColor("#ffffffff"));
        }


        holder1.titleText.setText(soundVolOsdBeans.get(position));
        if (mSelect == position) {
            holder1.view.setBackgroundResource(R.drawable.border_focus);
        } else {
            holder1.view.setBackgroundResource(R.drawable.border);
        }
        return convertView;
    }

    public void setData(List<String> list) {
        this.soundVolOsdBeans = list;
        mSelect=-1;
        notifyDataSetChanged();
    }

    public void changeSelected(int positon) { //刷新方法
        if (positon != mSelect) {
            mSelect = positon;
            notifyDataSetChanged();
        }
    }

    class Holder {
        public TextView titleText;
        public LinearLayout view;
        public TextView subTitleText;
        public TextView valueText;
    }

}
