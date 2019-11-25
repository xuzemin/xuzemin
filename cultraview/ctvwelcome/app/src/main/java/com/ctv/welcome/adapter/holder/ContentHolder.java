
package com.ctv.welcome.adapter.holder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ctv.welcome.R;

public class ContentHolder extends RecyclerView.ViewHolder {
    public ImageView icon;

    public TextView name;

    public ContentHolder(View view) {
        super(view);
        this.name = (TextView) view.findViewById(R.id.adp_content_name_tv);
        this.icon = (ImageView) view.findViewById(R.id.adp_content_icon_iv);
    }
}
