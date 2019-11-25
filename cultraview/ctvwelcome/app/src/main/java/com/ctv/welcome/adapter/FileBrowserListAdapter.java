
package com.ctv.welcome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ctv.welcome.R;
import com.ctv.welcome.listener.OnItemClickListener;
import com.ctv.welcome.listener.OnItemDoubleClickListener;
import com.ctv.welcome.util.LogUtils;
import com.ctv.welcome.vo.LocalFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileBrowserListAdapter extends RecyclerView.Adapter<FileBrowserListAdapter.ViewHolder> {
    private static final int CONTENT = 1;

    private static final int TITLE = 0;

    private Context context;

    private ArrayList<LocalFile> datas;

    private OnItemDoubleClickListener mDoubleClickListener;

    private OnItemClickListener mItemClickListener;

    private int selPosition = -1;

    public void setOnItemDoubleClickListener(OnItemDoubleClickListener onItemDoubleClickListener) {
        this.mDoubleClickListener = onItemDoubleClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public FileBrowserListAdapter(Context context, ArrayList<LocalFile> datas) {
        this.context = context;
        this.datas = datas;
        LogUtils.d("FileBrowserListAdapter", "datas size:" + datas.size());
    }

    public void setSelPosition(int selPosition) {
        this.selPosition = selPosition;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.d("FileBrowserListAdapter", "onCreateViewHolder,viewType:" + viewType);
        LayoutInflater mInflator = LayoutInflater.from(this.context);
        if (viewType == 0) {
            return new ViewHolder(
                    mInflator.inflate(R.layout.adapter_file_item_title, parent, false));
        }
        return new ViewHolder(mInflator.inflate(R.layout.adp_file_list_item, parent, false));
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        LogUtils.d("FileBrowserListAdapter", "onBindViewHolder,position:" + position);
        switch (getItemViewType(position)) {
            case 0:
                TextView txtDate = (TextView) holder.getView(R.id.txt_date);
                TextView txtSize = (TextView) holder.getView(R.id.txt_size);
                ((TextView) holder.getView(R.id.txt_name)).setText(this.context
                        .getString(R.string.name));
                txtDate.setText(this.context.getString(R.string.date));
                txtSize.setText(this.context.getString(R.string.size));
                return;
            case 1:
                final LocalFile localFile = (LocalFile) this.datas.get(position - 1);
                LinearLayout linearFileItem = (LinearLayout) holder
                        .getView(R.id.linear_file_item_list);
                ImageView imgFileIcon = (ImageView) holder.getView(R.id.img_file_icon);
                if (position == this.selPosition) {
                    linearFileItem.setBackgroundColor(this.context.getResources().getColor(
                            R.color.file_list_select_item));
                } else if (position % 2 == 0) {
                    linearFileItem.setBackgroundColor(-1);
                } else {
                    linearFileItem.setBackgroundColor(this.context.getResources().getColor(
                            R.color.file_list_light_gray_item));
                }
                if (localFile.getFileType() == 1) {
                    imgFileIcon.setBackgroundResource(R.drawable.folder_indicate);
                } else {
                    imgFileIcon.setBackgroundResource(R.drawable.picture_indicate);
                }
                ((TextView) holder.getView(R.id.txt_file_name)).setText(localFile.getFileName());
                ((TextView) holder.getView(R.id.txt_modify_date))
                        .setText(localFile.getModifyTime());
                ((TextView) holder.getView(R.id.txt_file_size)).setText(localFile.getSize());
                holder.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (localFile.getFileType() != 1) {
                            FileBrowserListAdapter.this.mItemClickListener.onItemClick(localFile);
                        } else if (FileBrowserListAdapter.this.selPosition == holder
                                .getAdapterPosition()) {
                            FileBrowserListAdapter.this.mDoubleClickListener
                                    .OnItemDoubleClick(localFile);
                        }
                        FileBrowserListAdapter.this.selPosition = holder.getAdapterPosition();
                        FileBrowserListAdapter.this.notifyDataSetChanged();
                    }
                });
                return;
            default:
                return;
        }
    }

    public int getItemViewType(int position) {
        LogUtils.d("FileBrowserListAdapter", "getItemViewType,position:" + position);
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    public int getItemCount() {
        LogUtils.d("FileBrowserListAdapter", "getItemCount,size:" + (this.datas.size() + 1));
        return this.datas.size() + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Map<Integer, View> mCacheView = new HashMap();

        ViewHolder(View itemView) {
            super(itemView);
        }

        View getView(int resId) {
            if (this.mCacheView.containsKey(Integer.valueOf(resId))) {
                return (View) this.mCacheView.get(Integer.valueOf(resId));
            }
            View view = this.itemView.findViewById(resId);
            this.mCacheView.put(Integer.valueOf(resId), view);
            return view;
        }
    }
}
