package com.mphotool.whiteboard.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mphotool.whiteboard.R;

import java.util.List;

public class NoteAdapter extends BaseAdapter {
    private static String TAG = "NoteAdapter";
    private LayoutInflater inflater;
    private int last = -1;
    List<Noteinfo> notelist = null;

    public NoteAdapter(Context context, List<Noteinfo> list) {
        inflater = LayoutInflater.from(context);
        notelist = list;
    }

    @SuppressLint("NewApi")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.note_item, null);
            viewHolder = new ViewHolder();
            viewHolder.note_name = (TextView) convertView
                    .findViewById(R.id.note_name);
//            viewHolder.note_id = (TextView) convertView.findViewById(R.id.note_id);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (last == position) {
            viewHolder.note_name.setTextColor(Color.YELLOW);
//            convertView.setBackgroundResource(R.drawable.grid_selected);
        } else {
            viewHolder.note_name.setTextColor(Color.WHITE);
//            convertView.setBackground(null);
        }
//        viewHolder.note_id.setText(String.valueOf(position+1));
        viewHolder.note_name.setText(notelist.get(position).getName());
     /*       viewHolder.image.setImageDrawable(info.activityInfo
                    .loadIcon(getPackageManager()));*/
        return convertView;
    }

    public final int getCount() {

        return notelist.size();
    }

    public final Object getItem(int position) {

        return notelist.get(position);
    }

    public void setDataChanged(List<Noteinfo> data) {
        notelist = data;
        notifyDataSetChanged();
    }

    public void setIdChanged(int position) {
        last = position;
        notifyDataSetChanged();
    }

    public final long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        public TextView note_name;
//        public TextView note_id;
//            public ImageView image;
    }

}