package mstar.factorymenu.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.bean.SoundVolOsdBean;
import mstar.factorymenu.ui.bean.SystemBean;
import mstar.factorymenu.ui.utils.LogUtils;

public class SystemAdapter extends ArrayAdapter<SoundVolOsdBean> {
    private int resourceId;
    private List<SystemBean> soundVolOsdBeans = new ArrayList<>();
    private Context context;
    private int mSelect = -1;

    public SystemAdapter(@NonNull Context context, int resource, List<SystemBean> soundVolOsdBeans) {
        super(context, resource);
        resourceId = resource;
        this.context = context;
        this.soundVolOsdBeans = soundVolOsdBeans;
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
            holder1.titleText = (TextView) convertView.findViewById(R.id.item_title);
            holder1.valueText = (TextView) convertView.findViewById(R.id.item_value_text);
            holder1.subTitleText = (TextView) convertView.findViewById(R.id.item_subtitle);
            convertView.setTag(holder1);
        } else {
            holder1 = (Holder) convertView.getTag();
        }
        holder1.titleText.setText(soundVolOsdBeans.get(position).getTitle());
        holder1.valueText.setText(soundVolOsdBeans.get(position).getValue());
        if (soundVolOsdBeans.get(position).getId() == 1) {
            holder1.subTitleText.setVisibility(View.VISIBLE);
        } else {
            holder1.subTitleText.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setData(List<SystemBean> list) {
        this.soundVolOsdBeans = list;
        notifyDataSetChanged();
    }

    public void changeSelected(int position) {
        if (position != mSelect) {
            mSelect = position;
            notifyDataSetChanged();
        }
    }

    class Holder {
        public TextView titleText;
        public TextView subTitleText;
        public TextView valueText;
    }

}
