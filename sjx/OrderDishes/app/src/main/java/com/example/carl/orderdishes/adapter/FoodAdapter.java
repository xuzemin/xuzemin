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

public class FoodAdapter extends BaseAdapter {
    private String Tag = "FoodAdapter";
    private Context context;
    public List<Food> list  = new ArrayList<>();
    private CustomBitmapUtils utils;
    private int[] location = new int[2];
    public FoodAdapter(Context _context, List<Food> _list) {
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
            convertView = inflater.inflate(R.layout.food_item, null);
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
        Food food = list.get(position);
        viewHolder.state.setText(String.valueOf(food.getFprice()));
        viewHolder.text.setVisibility(View.VISIBLE);
        String str = food.getFname().trim();
        Log.e("str",str);
        if (str.length() > 3) {
            StringBuilder sb = new StringBuilder(str);
            sb.insert(3, "\n");
            viewHolder.text.setText(sb);
        } else {
            viewHolder.text.setText(str);
        }

        utils.display(viewHolder.image, Content.HEADURL+food.getFimgurl());

        viewHolder.add_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodMenuActivity.imageView.getLocationOnScreen(location);
                Log.e(Tag,FoodMenuActivity.imageView.getWidth()+""+FoodMenuActivity.imageView.getHeight());
                mCircleEndPoint.x = location[0] + (FoodMenuActivity.imageView.getWidth()-60)/2;
                mCircleEndPoint.y = location[1] + (FoodMenuActivity.imageView.getHeight()-60)/2;
                viewHolder.add_food.getLocationInWindow(location);
                Log.e(Tag,location[0]+""+location[1]);
                viewHolder.add_food.getLocationOnScreen(location);
                Log.e(Tag,location[0]+""+location[1]);
                animation(location);
            }
        });

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
    private Point mCircleMovePoint = new Point();
    private Point mCircleEndPoint = new Point();
    private void animation(int[] point){
        Point mCircleStartPoint = new Point();
        mCircleStartPoint.x = point[0];
        mCircleStartPoint.y = point[1];
        final ImageView img = new ImageView(context);
        img.setImageResource(R.mipmap.ic_launcher);
        img.setVisibility(View.VISIBLE);
        FoodMenuActivity.relativeLayout.addView(img,new ViewGroup.LayoutParams(60, 60));

        //设置值动画
        final ValueAnimator valueAnimator = ValueAnimator.ofObject(new CirclePointEvaluator(), mCircleStartPoint, mCircleEndPoint);
        valueAnimator.setDuration(600);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point goodsViewPoint = (Point) animation.getAnimatedValue();
                mCircleMovePoint.x = goodsViewPoint.x;
                mCircleMovePoint.y = goodsViewPoint.y;
                invalidate(img,valueAnimator);
            }
        });
        valueAnimator.start();
    }
    public void invalidate(ImageView img,ValueAnimator valueAnimator){
        img.setX(mCircleMovePoint.x);
        img.setY(mCircleMovePoint.y);
        if(mCircleMovePoint.x == mCircleEndPoint.x){
            valueAnimator.cancel();
            img.setVisibility(View.GONE);
            img.destroyDrawingCache();
        }
    }
}

