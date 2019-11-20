
package com.ctv.welcome.adapter;

import com.ctv.welcome.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;

public class TextSizeAdapter extends BaseQuickAdapter<String> {
    public TextSizeAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    protected void convert(BaseViewHolder holder, String s) {
        holder.setText(R.id.textsize_item_tv, s);
    }
}
