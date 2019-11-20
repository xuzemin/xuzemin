
package com.ctv.welcome.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ctv.welcome.R;

public class FileThumbnailHolder extends RecyclerView.ViewHolder {
    public TextView filename;

    public RelativeLayout relStorageBg;

    public ImageView thumbnail;

    public FileThumbnailHolder(View itemView) {
        super(itemView);
        this.relStorageBg = (RelativeLayout) itemView.findViewById(R.id.relative_file_type_bg);
        this.thumbnail = (ImageView) itemView.findViewById(R.id.adp_fb_image_thumbnail);
        this.filename = (TextView) itemView.findViewById(R.id.adp_fb_filename_thumbnail);
    }
}
