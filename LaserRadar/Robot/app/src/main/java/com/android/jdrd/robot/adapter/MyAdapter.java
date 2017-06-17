package com.android.jdrd.robot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.jdrd.robot.R;

import java.util.List;
import java.util.Map;

/**
 * Created by jdrd on 2017/6/15.
 *
 */

public class MyAdapter extends BaseAdapter {
    Context context;
    List<Map> list;

    public MyAdapter(Context _context, List<Map>  _list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            final LayoutInflater inflater = (LayoutInflater) context
                           .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.command_name);
            convertView.setTag(viewHolder);//讲ViewHolder存储在View中

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        switch ((int)list.get(position).get("type")){
            case 0:
                viewHolder.text.setText(R.string.straight);
                break;
            case 1:
                viewHolder.text.setText(R.string.derail);
                break;
            case 2:
                viewHolder.text.setText(R.string.rotato);
                break;
            case 3:
                viewHolder.text.setText(R.string.wait);
                break;
            case 4:
                viewHolder.text.setText(R.string.puthook);
                break;
            case 5:
                viewHolder.text.setText(R.string.lockhook);
                break;
        }
        return convertView;
    }

    //内部类
    class ViewHolder{
        TextView text;
    }

}

