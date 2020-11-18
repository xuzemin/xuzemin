package com.ctv.settings.device.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctv.settings.R;
import com.ctv.settings.device.bean.ThemeBgItem;

import java.util.List;

public class ThemeBgAdapter extends BaseQuickAdapter<ThemeBgItem, BaseViewHolder> {
    private int lastPosition = 0;

    public ThemeBgAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }
    public int getLastPosition(){
        return lastPosition;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ThemeBgItem item) {
        helper.setText(R.id.text, item.getTitle());
        // Glide.with(mContext).load(item.getImageResource()).into((ImageView) helper.getView(R.id.icon));
//        helper.setImageResource(R.id.icon,item.getImageResource());
//        helper.setBackgroundRes(R.id.icon, item.getImageResource());
        Glide.with(mContext).load(item.getImageResource()).into((ImageView) helper.getView(R.id.icon));
        if (lastPosition == helper.getAdapterPosition()) {
            helper.setVisible(R.id.item_img_theme_bg, true);
        } else {
            helper.setVisible(R.id.item_img_theme_bg, false);

        }
        helper.getView(R.id.item_ll).setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    ViewCompat.animate(view)
                            .setDuration(200)
                            .scaleX(1.08f)
                            .scaleY(1.08f)
                            .start();
                }else {
                    ViewCompat.animate(view)
                            .setDuration(200)
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .start();
                }
            }
        });

    }
}
