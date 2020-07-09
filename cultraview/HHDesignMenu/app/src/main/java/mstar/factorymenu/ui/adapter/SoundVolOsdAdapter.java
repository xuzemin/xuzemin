package mstar.factorymenu.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.bean.SoundVolOsdBean;
import mstar.factorymenu.ui.listener.SeekBarListener;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.tvsetting.factory.desk.IFactoryDesk;

public class SoundVolOsdAdapter extends ArrayAdapter<SoundVolOsdBean> implements SeekBar.OnSeekBarChangeListener {
    private int resourceId;
    private List<Map<String, Object>> soundVolOsdBeans = new ArrayList<>();
    private SeekBarListener listener;
    private int maxSeekBar = 1016;
    private IFactoryDesk factoryManager;

    public SoundVolOsdAdapter(@NonNull Context context, int resource, List<Map<String, Object>> soundVolOsdBeans, SeekBarListener listener, IFactoryDesk factoryManager) {
        super(context, resource);
        resourceId = resource;
        this.soundVolOsdBeans = soundVolOsdBeans;
        this.listener = listener;
        this.factoryManager = factoryManager;
    }

    @Override
    public int getCount() {
        return soundVolOsdBeans.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder1 = null;
        if (null == convertView) {
            System.out.println("convertView == null" + " position:" + position);
            holder1 = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            holder1.textView_title = convertView.findViewById(R.id.item_sound_vol_osd_title);
            holder1.textView_osd = (TextView) convertView.findViewById(R.id.item_sound_vol_osd_text);
            holder1.seekBar = (SeekBar) convertView.findViewById(R.id.item_sound_vol_osd);
            holder1.item = convertView.findViewById(R.id.item);
            holder1.seekBar.setId(100 + position);
            holder1.seekBar.setMax(maxSeekBar);
            convertView.setTag(holder1);
        } else {
            holder1 = (ViewHolder) convertView.getTag();
        }
        holder1.textView_title.setText((String) soundVolOsdBeans.get(position).get("volume_nonlinear_list_name"));
        holder1.textView_osd.setText((String) soundVolOsdBeans.get(position).get("volume_nonlinear_list_value"));
        holder1.seekBar.setProgress(Integer.parseInt((String) soundVolOsdBeans.get(position).get("volume_nonlinear_list_value")));

        maxSeekBar = 1016;


        final ViewHolder finalHolder = holder1;
        holder1.seekBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    finalHolder.item.setBackgroundResource(R.color.colorFocus);
                } else {
                    finalHolder.item.setBackgroundResource(R.color.colorUnFocus);
                }
            }
        });
        holder1.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                finalHolder.textView_osd.setText(i + "");
                switch (seekBar.getId()) {
                    case 100:
                        factoryManager.setOsdVolumeNonlinear(1, i);
                        break;
                    case 101:
                        factoryManager.setOsdVolumeNonlinear(10, i);
                        break;
                    case 102:
                        factoryManager.setOsdVolumeNonlinear(20, i);
                        break;
                    case 103:
                        factoryManager.setOsdVolumeNonlinear(30, i);
                        break;
                    case 104:
                        factoryManager.setOsdVolumeNonlinear(40, i);
                        break;
                    case 105:
                        factoryManager.setOsdVolumeNonlinear(50, i);
                        break;
                    case 106:
                        factoryManager.setOsdVolumeNonlinear(60, i);
                        break;
                    case 107:
                        factoryManager.setOsdVolumeNonlinear(70, i);
                        break;
                    case 108:
                        factoryManager.setOsdVolumeNonlinear(80, i);
                        break;
                    case 109:
                        factoryManager.setOsdVolumeNonlinear(90, i);
                        break;
                    case 110:
                        factoryManager.setOsdVolumeNonlinear(100, i);
                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return convertView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        listener.ItemOnProgressChanged(seekBar, i, b);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        listener.ItemOnStartTrackingTouch(seekBar);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        listener.ItemOnStopTrackingTouch(seekBar);

    }

    class ViewHolder {
        TextView textView_title;
        TextView textView_osd;
        SeekBar seekBar;
        LinearLayout item;
    }
}
