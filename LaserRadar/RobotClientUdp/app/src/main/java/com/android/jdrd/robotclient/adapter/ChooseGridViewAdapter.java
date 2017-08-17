package com.android.jdrd.robotclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jdrd.robotclient.R;

import java.util.List;
import java.util.Map;

/**
 * Created by jdrd on 2017/6/15.
 *
 */

public class ChooseGridViewAdapter extends BaseAdapter {
    Context context;
    List<Map> list;
    public static int Current_Index = -1;
    public ChooseGridViewAdapter(Context _context, List<Map>  _list) {
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
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_dialog_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageview);
            viewHolder.chooseImage = (ImageView) convertView.findViewById(R.id.chooseImage);
            viewHolder.imageback = (ImageView) convertView.findViewById(R.id.imageback);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);//讲ViewHolder存储在View中
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Map map = list.get(position);
//        if(("1").equals(map.get("outline").toString())){
            viewHolder.imageView.setImageResource(R.mipmap.zaixian);
//        }else{
//            viewHolder.imageView.setImageResource(R.mipmap.lixiang02);
//        }
//        viewHolder.name.setText(map.get("name").toString());
        viewHolder.name.setText(map.get("id").toString()+"号机器人");
//        switch ((int)map.get("robotstate")){
//            case 0:
        if("".equals(map.get("obstacle"))){
            if(map.get("state").equals("空闲")){
                viewHolder.text.setText("空闲");
                viewHolder.imageback.setImageResource(R.mipmap.kongxian);
            }else if(map.get("state").equals("执行线路指令")){
                viewHolder.text.setText("执行线路");
                viewHolder.imageback.setImageResource(R.mipmap.fuwuzhong);
            }else if(map.get("state").equals("故障")){
                viewHolder.text.setText("故障");
                viewHolder.imageback.setImageResource(R.mipmap.guzhang);
            }
        }else if("脱轨,".equals(map.get("obstacle"))){
            viewHolder.text.setText("脱轨");
            viewHolder.imageback.setImageResource(R.mipmap.guzhang);
        }else{
            viewHolder.text.setText(map.get("obstacle").toString());
            viewHolder.imageback.setImageResource(R.mipmap.guzhang);
        }

//                break;
//            case 1:
//                viewHolder.text.setText("送餐");
//                viewHolder.imageback.setImageResource(R.mipmap.fuwuzhong);
//                break;
//            case 2:
//                viewHolder.text.setText("故障");
//                viewHolder.imageback.setImageResource(R.mipmap.guzhang);
//                break;
//        }
        if(position == Current_Index){
            viewHolder.chooseImage.setVisibility(View.VISIBLE);
        }else{
            viewHolder.chooseImage.setVisibility(View.GONE);
        }
        return convertView;
    }

    //内部类
    class ViewHolder{
        ImageView imageView;
        TextView text;
        TextView name;
        ImageView imageback;
        ImageView chooseImage;
    }

}

