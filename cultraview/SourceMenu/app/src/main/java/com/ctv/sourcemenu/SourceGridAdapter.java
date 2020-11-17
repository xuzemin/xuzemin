package com.ctv.sourcemenu;


import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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

    private String update_name;


    private List<SourceBean> sourceBeans = new ArrayList<>();
    public SourceGridAdapter(@NonNull Context context, int resource, @NonNull List<SourceBean> sources) {
        super(context, resource, sources);
        resourceid =resource;
        sourceBeans =sources;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder;
     //   SourceBean item = getItem(position);
        SourceBean item = sourceBeans.get(position);
        String text ="11";
        if (convertView == null){
            viewHolder = new ViewHolder();

            view= LayoutInflater.from(getContext()).inflate(resourceid, parent, false);
            viewHolder.ahitem = view.findViewById(R.id.ahitem);
            viewHolder.rectView = view.findViewById(R.id.rect);
            // mrectView=viewHolder.rectView;
            viewHolder.mImage= view.findViewById(R.id.mImage);
            viewHolder.mText= view.findViewById(R.id.mText);

            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.mText.setText(item.getText());
            viewHolder.mImage.setImageResource(item.getIamgeid());
            viewHolder.idname = item.getId_name();
            text =  item.getId_name();
            L.debug("hongcc", "getView: idname ="+text +"   position ="+position+  "  sources.size ="+sourceBeans.size());
            L.debug("hongcc", "getView: update_name ="+update_name );
//            if (position == index){
//                viewHolder.mText.setSelected(true);
//            }else {
//                viewHolder.mText.setSelected(false);
//            }
        if (text.equals(update_name)){

            viewHolder.mText.setFocusable(true);
       //     boolean dd =  viewHolder.mText.requestFocus();
     //       Log.d("hongcc", "update   getView:   update_name ="+update_name +"   dd ="+dd);
            viewHolder.rectView.setProgress(100);
            viewHolder.mText.setSelected(true);
        }else {
            viewHolder.mText.setFocusable(false);
            viewHolder.mText.setSelected(false);
            viewHolder.rectView.setProgress(0);
          viewHolder.rectView.reDraw();
          viewHolder.rectView.clearAnimation();
        }

        return view;

    }


    class ViewHolder{
            View ahitem;
            ImageView mImage;
            TextView mText;
            RectView rectView;
            String idname;
    }


    public void setCurrentSourceid(String update_name){
        this.update_name=update_name;
        notifyDataSetChanged();
    }




}
