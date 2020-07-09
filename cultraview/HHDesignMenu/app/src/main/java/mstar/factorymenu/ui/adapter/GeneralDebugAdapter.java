package mstar.factorymenu.ui.adapter;

import android.content.Context;
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
import mstar.factorymenu.ui.utils.LogUtils;

public class GeneralDebugAdapter extends ArrayAdapter<SoundVolOsdBean> {
    private int resourceId;
    private List<SoundVolOsdBean> soundVolOsdBeans = new ArrayList<>();

    public GeneralDebugAdapter(@NonNull Context context, int resource, List<SoundVolOsdBean> soundVolOsdBeans) {
        super(context, resource);
        resourceId = resource;
        this.soundVolOsdBeans = soundVolOsdBeans;
    }

    @Override
    public int getCount() {
        return soundVolOsdBeans.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView name = (TextView) view.findViewById(R.id.item_sound_vol_osd_text);
        name.setText(soundVolOsdBeans.get(position).getProgressText());
        return view;
    }
}
