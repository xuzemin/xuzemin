package com.example.carl.orderdishes.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carl.orderdishes.R;
import com.example.carl.orderdishes.activity.FoodMenuActivity;
import com.example.carl.orderdishes.entity.Food;
import com.example.carl.orderdishes.util.Content;
import com.example.carl.orderdishes.util.CustomBitmapUtils;
import com.example.carl.orderdishes.view.CirclePointEvaluator;

import java.util.List;

/**
 * Created by dingqiqi on 2016/12/1.
 *
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
    private String Tag = "RecyclerAdapter";
    private Context mContext;
    public List<Food> mList;
    private CustomBitmapUtils utils;
    private int[] location = new int[2];
    private Point mCircleMovePoint = new Point();
    private Point mCircleEndPoint = new Point();
    public RecyclerAdapter(Context mContext, List<Food> mList) {
        this.mContext = mContext;
        this.mList = mList;
        utils = new CustomBitmapUtils();
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //绑定parent，因为item宽度没绑定parent，match_parent没效果，item跟布局高度必须设置为wrap_content
        View view = LayoutInflater.from(mContext).inflate(R.layout.food_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        if(mList !=null && mList.size()>0){
            Food food = mList.get(position);
            holder.name.setText(food.getFname());
            utils.display(holder.img, Content.HEADURL+Content.picture + food.getFimgurl());
            holder.state.setText(String.valueOf(mList.get(position).getFprice()));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,""+position,Toast.LENGTH_SHORT).show();
                }
            });
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FoodMenuActivity.imageView.getLocationOnScreen(location);
                    mCircleEndPoint.x = location[0] + (FoodMenuActivity.imageView.getWidth()-60)/2;
                    mCircleEndPoint.y = location[1] + (FoodMenuActivity.imageView.getHeight()-60)/2;
                    holder.add.getLocationInWindow(location);
                    holder.add.getLocationOnScreen(location);
                    animation(location);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class Holder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView img;
        private TextView state;
        private Button add;
        private Button delete;

        public Holder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.img);
            state = itemView.findViewById(R.id.state);
            add = itemView.findViewById(R.id.add_food);
            delete = itemView.findViewById(R.id.delete_food);
        }
    }

    public void animation(int[] point){
        Point mCircleStartPoint = new Point();
        mCircleStartPoint.x = point[0];
        mCircleStartPoint.y = point[1];
        final ImageView img = new ImageView(mContext);
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
