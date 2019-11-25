
package com.ctv.welcome.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ctv.welcome.R;

public class CustomContentHolder extends RecyclerView.ViewHolder {
    public final ImageView delete;

    public final ImageView image;

    public final TextView imagename;

    public CustomContentHolder(View itemView) {
        super(itemView);
        this.image = (ImageView) itemView.findViewById(R.id.adp_custom_content_icon_iv);
        this.delete = (ImageView) itemView.findViewById(R.id.adp_custom_content_delete_iv);
        this.imagename = (TextView) itemView.findViewById(R.id.adp_custom_content_name_tv);
    }
}
