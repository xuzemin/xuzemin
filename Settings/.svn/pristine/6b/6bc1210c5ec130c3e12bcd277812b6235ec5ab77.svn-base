package com.ctv.settings.device.adapter;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctv.settings.device.bean.ThemeBgItem;
import com.ctv.settings.device.bean.ThemeStyleItem;
import com.ctv.settings.security.R;

import java.util.List;

public class ThemeStyleAdapter extends BaseQuickAdapter<ThemeStyleItem, BaseViewHolder> {
    public ThemeStyleAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ThemeStyleItem item) {
        helper.setText(R.id.text, item.getTitle());


        //helper.setImageResource(R.id.icon, item.getImageResource());
//        Glide.with(mContext).load(item.getUserAvatar()).crossFade().placeholder(R.mipmap.def_head).transform(new GlideCircleTransform(mContext)).into((ImageView) helper.getView(R.id.tweetAvatar));
    }
}
