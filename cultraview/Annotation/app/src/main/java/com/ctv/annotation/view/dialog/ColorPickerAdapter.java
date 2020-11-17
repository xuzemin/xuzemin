package com.ctv.annotation.view.dialog;

import android.content.Context;
import android.view.View;

import com.ctv.annotation.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class ColorPickerAdapter extends CommonAdapter<ColorItem> {
    private int activePosition = 0;
    private int currentColor = 0;

    public ColorPickerAdapter(Context context, int layoutId, List<ColorItem> colorItemList) {
        super(context, layoutId, colorItemList);
    }

    protected void convert(ViewHolder holder, ColorItem colorItem, int position) {
        switch (colorItem.getType()) {
            case 0:
                holder.setImageResource(R.id.color, 0);
                holder.setBackgroundColor(R.id.color, colorItem.getColor());
                break;
            case 1:
                holder.setImageResource(R.id.color, R.mipmap.ic_color_pick);
                break;
        }
        View viewActive = holder.getView(R.id.active);
        if (viewActive != null) {
            if (this.activePosition != position) {
                viewActive.setVisibility(View.GONE);
            }else{
                viewActive.setVisibility(View.VISIBLE);
            }
        }
    }

    public int getActivePosition() {
        return this.activePosition;
    }

    public void setActivePosition(int activePosition) {
        this.activePosition = activePosition;
    }

    public int getCurrentColor() {
        return this.currentColor;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

}
