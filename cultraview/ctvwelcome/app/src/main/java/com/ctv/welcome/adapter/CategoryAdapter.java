
package com.ctv.welcome.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.ctv.welcome.R;
import com.ctv.welcome.activity.IndexActivity;
import com.ctv.welcome.adapter.holder.CategoryHolder;
import com.ctv.welcome.vo.CategoryIndexData;
import java.util.List;

public class CategoryAdapter extends Adapter<CategoryHolder> {
    private final int NORMAL_ITEM = 0;

    private final int SPEICAL_ITEM = 1;

    private Context context;

    private List<CategoryIndexData> dataList;

    private boolean isSelected = false;

    private boolean isSetBoolean = false;

    private LayoutInflater layoutInflater;

    private int mCurrentSelected = -1;

    private OnCategoryClickListener mOnCategoryClickListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(View view, int i, boolean z);
    }

    public CategoryAdapter(List<CategoryIndexData> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = null;
        switch (viewType) {
            case 0:
                if (!IndexActivity.mIsWindowMode) {
                    view = layoutInflater.inflate(R.layout.adp_category_item, parent, false);
                    break;
                }
                view = layoutInflater
                        .inflate(R.layout.adp_category_item_window, parent, false);
                break;
            case 1:
                view = layoutInflater.inflate(R.layout.adp_category_item_line, parent, false);
                break;
        }
        return new CategoryHolder(view);
    }

    private void handleClick(View view, int position, boolean isSelected) {
        if (mOnCategoryClickListener != null) {
            mOnCategoryClickListener.onCategoryClick(view, position,
                    mCurrentSelected == position);
        }
        mCurrentSelected = position;
        notifyDataSetChanged();
    }
    public int getSelectId(){
        return mCurrentSelected;
    }
    public void  setSelectId(int position){
        mCurrentSelected = position;
    }
    public void setmCurrentSelected(View view ,int position){
        if (mOnCategoryClickListener != null) {
            isSetBoolean = false;
            mOnCategoryClickListener.onCategoryClick(view, position,
                    mCurrentSelected == position);
        }
        mCurrentSelected = position;
        notifyDataSetChanged();
    }
    public void onBindViewHolder(CategoryHolder holder, final int position) {
        boolean isSelect = false;
        switch (getItemViewType(position)) {
            case 0:
                CategoryIndexData data = (CategoryIndexData) dataList.get(position);
                holder.name.setText(data.getName());
                holder.itemView.setTag(data);
                holder.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        boolean z = false;
                        isSetBoolean = false;
                        mCurrentSelected = position;
                        CategoryAdapter categoryAdapter = CategoryAdapter.this;
                        int i = position;
                        if (mCurrentSelected == position) {
                            z = true;
                        }
                        categoryAdapter.handleClick(view, i, z);
                    }
                });
                if (isSetBoolean) {
                    isSelected = false;
                } else {
                    if (mCurrentSelected == position) {
                        isSelect = true;
                    }
                    isSelected = isSelect;
                }
                Object icon = data.getIcon();
                Object selIcon = data.getSelIcon();
                handleSelected(isSelected, holder.itemView);
                if (isSelected) {
                    if (selIcon instanceof Integer) {
                        holder.icon.setBackgroundResource(((Integer) selIcon).intValue());
                        return;
                    }
                    return;
                } else if (icon instanceof Integer) {
                    holder.icon.setBackgroundResource(((Integer) icon).intValue());
                    return;
                } else {
                    return;
                }
            default:
                return;
        }
    }

    private void handleSelected(boolean isSelected, View view) {
        view.setSelected(isSelected);
        ViewGroup group = (ViewGroup) view;
        int count = group.getChildCount();
        for (int in = 0; in < count; in++) {
            group.getChildAt(in).setSelected(isSelected);
        }
    }

    public void setSelected(boolean selected) {
        isSetBoolean = selected;
        isSelected = true;
        notifyDataSetChanged();
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getItemCount() {
        return dataList.size();
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        mOnCategoryClickListener = listener;
    }
}
