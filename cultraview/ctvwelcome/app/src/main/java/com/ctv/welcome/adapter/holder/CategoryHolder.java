
package com.ctv.welcome.adapter.holder;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ctv.welcome.R;

public class CategoryHolder extends ViewHolder {
    public ImageView icon;

    public TextView name;

    public CategoryHolder(View view) {
        super(view);
        this.name = (TextView) view.findViewById(R.id.adp_category_name_tv);
        this.icon = (ImageView) view.findViewById(R.id.adp_category_icon_iv);
    }
}
