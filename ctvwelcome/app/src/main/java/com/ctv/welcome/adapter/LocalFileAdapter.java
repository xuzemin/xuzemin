
package com.ctv.welcome.adapter;

import android.content.Context;
import android.widget.ImageView;
import com.ctv.welcome.R;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctv.welcome.vo.CustomContentData;
import java.util.List;

public class LocalFileAdapter extends BaseQuickAdapter<CustomContentData> {
    private final Context context;

    private List<CustomContentData> data;

    private final int layoutResId;

    public LocalFileAdapter(int layoutResId, List<CustomContentData> data, Context context) {
        super(layoutResId, (List) data);
        this.layoutResId = layoutResId;
        this.data = data;
        this.context = context;
    }

    protected void convert(BaseViewHolder holder, CustomContentData data) {
        holder.setText(R.id.adp_local_name_tv, data.getFilename());
        Glide.with(this.context).load(data.getFilepath())
                .into((ImageView) holder.getView(R.id.adp_local_icon_iv));
    }

    public void setDatas(List<CustomContentData> datas) {
        this.data = datas;
        notifyDataSetChanged();
    }
}
