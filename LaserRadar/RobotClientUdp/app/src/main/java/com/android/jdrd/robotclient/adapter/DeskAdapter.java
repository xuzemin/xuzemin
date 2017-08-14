package com.android.jdrd.robotclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.jdrd.robotclient.R;
import com.android.jdrd.robotclient.activity.MainActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by jdrd on 2017/6/15.
 *
 */

public class DeskAdapter extends BaseAdapter {
    Context context;
    List<Map<String, Object>> list;

    public DeskAdapter(Context _context, List<Map<String, Object>> _list) {
        this.list = _list;
        this.context = _context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            final LayoutInflater inflater = (LayoutInflater) context
                           .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.desk_item, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.name);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.bjzt = (ImageView) convertView.findViewById(R.id.bjzt);
            convertView.setTag(viewHolder);//讲ViewHolder存储在View中
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bjzt.setVisibility(View.GONE);
        if(list.get(position).get("image") != null){
            viewHolder.image.setVisibility(View.VISIBLE);
            viewHolder.text.setVisibility(View.GONE);
            if(position == 0){
                viewHolder.image.setImageResource(R.animator.btn_add_desk_selector);
            }else{
                if(MainActivity.DeskIsEdit){
                    viewHolder.bjzt.setVisibility(View.VISIBLE);
                }
                viewHolder.image.setImageResource(R.animator.btn_add_selector);
            }
        }else{
            if(MainActivity.DeskIsEdit){
                viewHolder.bjzt.setVisibility(View.VISIBLE);
            }
            viewHolder.text.setVisibility(View.VISIBLE);
            viewHolder.image.setVisibility(View.GONE);
            String str = list.get(position).get("name").toString().trim();
            if(str.length()>3){
                StringBuilder sb=new StringBuilder(str);
                sb.insert(3,"\n");
                viewHolder.text.setText(sb);
            }else{
                viewHolder.text.setText(str);
            }
        }
        return convertView;
    }

    //内部类
    static class ViewHolder{
        TextView text;
        ImageView image;
        ImageView bjzt;
        RelativeLayout back;
    }

}

