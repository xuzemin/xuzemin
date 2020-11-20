package com.mphotool.whiteboard.view.menuviews;

import android.content.Context;
import android.view.View;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class ColorPickerAdapter extends CommonAdapter<ColorItem> {
    private int activePosition = 0;
    private int currentColor = 0;

    public ColorPickerAdapter(Context context, int layoutId, List<ColorItem> colorItemList) {
        super(context, layoutId, colorItemList);
    }

 /*   public void setOptions(int position){
        this.activePosition = position;
    }*/

    protected void convert(ViewHolder holder, ColorItem colorItem, int position) {

        switch (colorItem.getType()) {
            case 0:
                holder.setImageResource(R.id.color, 0);
                holder.setBackgroundColor(R.id.color, colorItem.getColor());
                break;
            case 1:
                holder.setImageResource(R.id.color, R.drawable.ic_color_pick);
                break;
        }
        View viewActive = holder.getView(R.id.active);
        if (viewActive != null) {
            if (currentColor != colorItem.getColor()) {
                viewActive.setVisibility(View.GONE);
            }else {
                viewActive.setVisibility(View.VISIBLE);
            }
        }
        if(activePosition == position && position == 11){
            viewActive.setVisibility(View.VISIBLE);
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
