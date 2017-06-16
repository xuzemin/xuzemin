package com.android.jdrd.robot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.jdrd.robot.R;
import java.util.List;
import java.util.Map;

/**
 * Created by jdrd on 2017/6/15.
 *
 */

public class GridViewAdapter extends BaseAdapter {
    Context context;
    List<Map> list;

    public GridViewAdapter(Context _context,List<Map>  _list) {
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
            convertView = inflater.inflate(R.layout.grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageview);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);//讲ViewHolder存储在View中

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(("1").equals(list.get(position).get("outline").toString())){
            viewHolder.imageView.setImageResource(R.mipmap.bg);
        }else{
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
        viewHolder.text.setText(list.get(position).get("lastlocation").toString());
        viewHolder.name.setText(list.get(position).get("name").toString());
        return convertView;
    }

    //内部类
    class ViewHolder{
        ImageView imageView;
        TextView text;
        TextView name;
    }

}

