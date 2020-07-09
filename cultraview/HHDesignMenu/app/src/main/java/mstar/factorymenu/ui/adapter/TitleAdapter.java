package mstar.factorymenu.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
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
import mstar.factorymenu.ui.bean.SystemBean;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.factorymenu.ui.utils.Tools;

public class TitleAdapter extends ArrayAdapter<SoundVolOsdBean> {
    private int resourceId;
    private List<String> soundVolOsdBeans = new ArrayList<>();
    private Context context;
    private int mSelect = -1;
    public TitleAdapter(@NonNull Context context, int resource, List<String> soundVolOsdBeans) {
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
            holder1.view = convertView.findViewById(R.id.item);
            holder1.titleText = (TextView) convertView.findViewById(R.id.item_title);
            convertView.setTag(holder1);
        } else {
            holder1 = (Holder) convertView.getTag();
        }
        holder1.titleText.setText(soundVolOsdBeans.get(position));
        if (mSelect==position){
            holder1.view.setBackgroundResource(R.drawable.border_focus);
        }else {
            holder1.view.setBackgroundResource(R.drawable.border);
        }
        return convertView;
    }

    public void changeSelected(int position) {
        if (position != mSelect) {
            mSelect = position;
            notifyDataSetChanged();
        }
    }

    public void setData(List<String> list) {
        this.soundVolOsdBeans = list;
        notifyDataSetChanged();
    }

    class Holder {
        public TextView titleText;
        public LinearLayout view;
        public TextView subTitleText;
        public TextView valueText;
    }

}
