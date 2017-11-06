package com.example.carl.orderdishes.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.carl.orderdishes.R;
import com.example.carl.orderdishes.activity.FoodMenuActivity;
import com.example.carl.orderdishes.entity.Food;
import com.example.carl.orderdishes.util.Content;
import com.example.carl.orderdishes.util.CustomBitmapUtils;
import com.example.carl.orderdishes.view.CirclePointEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/9
 * 描述: 编辑桌子适配器
 */

public class IndexAdapter extends BaseAdapter {
    private String Tag = "IndexAdapter";
    private Context context;
    public List<String> list  = new ArrayList<>();
    private CustomBitmapUtils utils;
    public IndexAdapter(Context _context, List<String> _list) {
        this.list = _list;
        this.context = _context;
        utils = new CustomBitmapUtils();
    }

    /**
     * 在此适配器中所代表的数据集中的条目数
     *
     * @return size
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * 获取数据集中与指定索引对应的数据项
     *
     * @param position 获取下标
     * @return
     */
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    /**
     * 获取在列表中与指定索引对应的行id
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取一个在数据集中指定索引的视图来显示数据
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        // 如果缓存convertView为空，则需要创建View
        if (convertView == null) {
            // 根据context上下文加载布局，这里的是AreaAdapter本身，即this
            final LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.foodtype_item, null);
            viewHolder = new ViewHolder();
            // 根据自定义的Item布局加载布局
            viewHolder.text =  convertView.findViewById(R.id.name);
            viewHolder.state = convertView.findViewById(R.id.state);
            viewHolder.image = convertView.findViewById(R.id.img);
            viewHolder.add_food = convertView.findViewById(R.id.add_food);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Log.e(Tag,list.get(position));
        viewHolder.text.setText(list.get(position));


        return convertView;
    }

    // ViewHolder静态类
    public static class ViewHolder {
        public TextView text;
        public TextView state;
        public ImageView image;
        public ImageView bjzt;
        public CheckBox cb;
        public Button add_food;
        RelativeLayout back;
    }
}

