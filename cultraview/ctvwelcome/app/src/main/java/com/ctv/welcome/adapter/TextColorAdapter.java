
package com.ctv.welcome.adapter;

import android.graphics.drawable.Drawable;
import com.ctv.welcome.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;

public class TextColorAdapter extends BaseQuickAdapter<Drawable> {
    public int selectItem = -1;

    public TextColorAdapter(int layoutResId, List<Drawable> data) {
        super(layoutResId, (List) data);
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    protected void convert(BaseViewHolder baseViewHolder, Drawable drawable) {
        if (baseViewHolder.getLayoutPosition() == this.selectItem) {
            baseViewHolder.getView(R.id.textcolor_item_iv).setSelected(true);
        } else {
            baseViewHolder.getView(R.id.textcolor_item_iv).setSelected(false);
        }
        baseViewHolder.setImageDrawable(R.id.textcolor_item_iv, drawable);
    }
}
