
package com.ctv.welcome.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.ctv.welcome.R;
import com.bumptech.glide.Glide;
import com.ctv.welcome.adapter.holder.FileThumbnailHolder;
import com.ctv.welcome.listener.OnItemClickListener;
import com.ctv.welcome.listener.OnItemDoubleClickListener;
import com.ctv.welcome.vo.LocalFile;
import java.io.File;
import java.util.ArrayList;

public class FileBrowserAdapter extends Adapter<ViewHolder> {
    private int cachePosition = -1;

    private ArrayList<LocalFile> datas = new ArrayList();

    private boolean isBigIcon = true;

    private Context mContext;

    private OnItemDoubleClickListener mDoubleClickListener;

    private OnItemClickListener mItemClickListener;

    public FileBrowserAdapter(Context context, boolean isBigIcon) {
        this.mContext = context;
        this.isBigIcon = isBigIcon;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        if (this.isBigIcon) {
            layoutId = R.layout.adpater_filebrowser_item_thumbnail;
        } else {
            layoutId = R.layout.adpater_filebrowser_smallitem_thumbnail;
        }
        return new FileThumbnailHolder(LayoutInflater.from(this.mContext).inflate(layoutId, null));
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LocalFile localFile = (LocalFile) this.datas.get(position);
        FileThumbnailHolder holder1 = (FileThumbnailHolder) holder;
        if (localFile.getFileType() == 0) {
            Glide.with(this.mContext).load(new File(localFile.getFilePath()))
                    .into(holder1.thumbnail);
        } else {
            holder1.thumbnail.setImageResource(R.drawable.ic_fb_thumbnail_folder);
        }
        holder1.filename.setText(localFile.getFileName());
        if (this.cachePosition == position) {
            ((FileThumbnailHolder) holder).relStorageBg.setSelected(true);
        } else {
            ((FileThumbnailHolder) holder).relStorageBg.setSelected(false);
        }
        holder.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (localFile.getFileType() != 1) {
                    FileBrowserAdapter.this.mItemClickListener.onItemClick(localFile);
                } else if (FileBrowserAdapter.this.cachePosition == holder.getAdapterPosition()) {
                    FileBrowserAdapter.this.mDoubleClickListener.OnItemDoubleClick(localFile);
                }
                FileBrowserAdapter.this.cachePosition = holder.getAdapterPosition();
                FileBrowserAdapter.this.notifyDataSetChanged();
            }
        });
    }

    public int getItemCount() {
        return this.datas.size();
    }

    public String getFilePath() {
        return ((LocalFile) this.datas.get(this.cachePosition)).getFilePath();
    }

    public void replaceData(ArrayList<LocalFile> cacheData) {
        this.datas = cacheData;
    }

    public void setOnItemDoubleClickListener(OnItemDoubleClickListener onItemDoubleClickListener) {
        this.mDoubleClickListener = onItemDoubleClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }
}
