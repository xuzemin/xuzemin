package com.ctv.sourcemenu;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SourceGridAdapter extends ArrayAdapter<SourceBean> {
    private int resourceid;
    int i= 0;
    private RectView mrectView;
    private int index = -1;
    private int sourceid;
    private ValueAnimator valueAnimator_first;

    public SourceGridAdapter(@NonNull Context context, int resource, @NonNull List<SourceBean> objects) {
        super(context, resource, objects);
        resourceid =resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder;
        SourceBean item = getItem(position);
        if (convertView == null){
            viewHolder = new ViewHolder();
            view= LayoutInflater.from(getContext()).inflate(resourceid, parent, false);
            viewHolder.rectView = view.findViewById(R.id.rect);
            mrectView=viewHolder.rectView;
            viewHolder.mImage= view.findViewById(R.id.mImage);
            viewHolder.mText= view.findViewById(R.id.mText);
            view.setTag(viewHolder);
        }else {
            view = convertView;

            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.mText.setText(item.getText());
            viewHolder.mImage.setImageResource(item.getIamgeid());
            viewHolder.setposition(item.getPosition());
//        if (position==0){
//            viewHolder.mText.setSelected(true);
//        }
        if (position == index){
            //startanim_first(viewHolder.rectView);
            viewHolder.mText.setSelected(true);
            Log.d("hhc", "getView:   true--------position ="+position);

        }else {
            Log.d("hhc", "getView: false-------position ="+position);
            viewHolder.mText.setSelected(false);
            viewHolder.rectView.setProgress(0);
            viewHolder.rectView.reDraw();
            viewHolder.rectView.clearAnimation();

           // stopAnimation();
        }
        if ((position+1) == sourceid){
            viewHolder.rectView.setProgress(100);
            viewHolder.mText.setSelected(true);
            viewHolder.rectView.reDraw();
        }
        return view;


    }
    class ViewHolder{
            ImageView mImage;
            TextView mText;
            RectView rectView;
            int position;
            public void setposition(int i){
                position = i;
            }

    }
    public void setCurrentPosition(int index){
        Log.d("hhc", "setCurrentPosition:  index ="+index);
        this.index =index;
        notifyDataSetChanged();
    }

    public int getCurrentPosition(){
        return index;
    }
    public void setCurrentSourceid(int sourceid){
        this.sourceid=sourceid;
        notifyDataSetChanged();
    }




}
