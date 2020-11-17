
package com.ctv.welcome.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ctv.welcome.R;
import com.bumptech.glide.Glide;
import com.ctv.welcome.adapter.CategoryAdapter.OnCategoryClickListener;
import com.ctv.welcome.vo.IndexData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    final int NORMAL_ITEM = 0;

    final int SPEICAL_ITEM = 1;

    private ImageView back;

    private Context context;

    private List<IndexData> dataList;

    private ImageView font;

    boolean isChange = false;

    public boolean isSelected = false;

    private boolean isSetBoolean = false;

    private LayoutInflater layoutInflater;

    private int mCurrentSelected = 0;

    private OnCategoryClickListener mOnCategoryClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Map<Integer, View> mCacheView = new HashMap();

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public View getView(int resId) {
            if (this.mCacheView.containsKey(Integer.valueOf(resId))) {
                return (View) this.mCacheView.get(Integer.valueOf(resId));
            }
            View view = this.itemView.findViewById(resId);
            this.mCacheView.put(Integer.valueOf(resId), view);
            return view;
        }
    }

    public CustomAdapter(List<IndexData> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    public void addDatas(List<IndexData> datas) {
        this.dataList = datas;
        notifyDataSetChanged();
    }

    public void delData(int position) {
        this.dataList.remove(position);
        notifyDataSetChanged();
    }

    public ImageView getFont() {
        return this.font;
    }

    public ImageView getBack() {
        this.isChange = true;
        return this.back;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (this.layoutInflater == null) {
            this.layoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = null;
        switch (viewType) {
            case 0:
                view = this.layoutInflater.inflate(R.layout.adp_custom_item, parent, false);
                break;
            case 1:
                view = this.layoutInflater.inflate(R.layout.adp_custom_pic_item, parent, false);
                break;
        }
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, final int position) {
        boolean z;
        IndexData data = (IndexData) this.dataList.get(position);
        switch (getItemViewType(position)) {
            case 0:
                TextView name = (TextView) holder.getView(R.id.adp_category_name_tv);
                ImageView image = (ImageView) holder.getView(R.id.adp_category_icon_iv);
                image.setScaleType(ScaleType.CENTER);
                name.setText(data.getName());
                Object icon = data.getIcon();
                if (icon instanceof Integer) {
                    Glide.with(this.context).load(Integer.valueOf(((Integer) icon).intValue()))
                            .into(image);
                    break;
                }
                break;
            case 1:
                this.back = (ImageView) holder.getView(R.id.adp_custom_back_icon_iv);
                this.font = (ImageView) holder.getView(R.id.adp_custom_font_icon_iv);
                TextView picname = (TextView) holder.getView(R.id.adp_custom_name_tv);
                String picIcon =(String) data.getIcon();
                if (picIcon instanceof String) {
                    String strPaht = picIcon;
                    String[] split = strPaht.split(",");
                    if (split.length == 2) {
                        if (!TextUtils.isEmpty(split[1])) {
                            Glide.with(this.context).load(split[1]).into(this.font);
                        }
                        if (!(this.isChange || TextUtils.isEmpty(split[0]))) {
                            Glide.with(this.context).load(split[0]).into(this.back);
                        }
                    } else {
                        Glide.with(this.context).load(strPaht).into(this.back);
                    }
                }
                picname.setText(data.getName());
                break;
        }
        holder.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                boolean z = false;
                CustomAdapter.this.isSetBoolean = false;
                CustomAdapter.this.mCurrentSelected = position;
                CustomAdapter customAdapter = CustomAdapter.this;
                int i = position;
                if (CustomAdapter.this.mCurrentSelected == position) {
                    z = true;
                }
                customAdapter.handleClick(view, i, z);
            }
        });
        if (this.isSetBoolean || this.mCurrentSelected != position) {
            z = false;
        } else {
            z = true;
        }
        this.isSelected = z;
        handleSelected(this.isSelected, holder.itemView);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.mOnCategoryClickListener = listener;
    }

    private void handleClick(View view, int position, boolean isSelected) {
        if (this.mOnCategoryClickListener != null) {
            this.mOnCategoryClickListener.onCategoryClick(view, position, isSelected);
        }
        notifyDataSetChanged();
    }

    private void handleSelected(boolean isSelected, View view) {
        view.setSelected(isSelected);
        ViewGroup group = (ViewGroup) view;
        int count = group.getChildCount();
        for (int in = 0; in < count; in++) {
            group.getChildAt(in).setSelected(isSelected);
        }
    }

    public void setSelected(boolean isSet) {
        this.isSetBoolean = isSet;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.dataList.size();
    }

    public int getItemViewType(int position) {
        return 0;
    }
}
