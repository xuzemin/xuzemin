
package com.mphotool.whiteboard.folderchooser;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mphotool.whiteboard.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HFileListAdapter extends BaseAdapter {
    private List<FileUtils> files = new ArrayList();

    private SimpleDateFormat format = new SimpleDateFormat("y/MM/dd hh:mm");

    private Context mContext;

    class ViewHolder {
        ImageView mFileIcon;

        RelativeLayout mFileLayout;

        TextView mFileName;

        TextView mFileSize;

        TextView mFileTime;

        ViewHolder() {
        }
    }

    public HFileListAdapter(Context context, List<FileUtils> files) {
        this.files = files;
        this.mContext = context;
    }

    public void notifyDataSetChanged(List<FileUtils> files) {
        this.files = files;
        super.notifyDataSetChanged();
    }

    public void notifyDataSetChanged(List<FileUtils> files, boolean needSort) {
        if (needSort) {
            Collections.sort(files, new Comparator<FileUtils>() {
                public int compare(FileUtils o1, FileUtils o2) {
                    if (o1.getCreateTime() > o2.getCreateTime()) {
                        return -1;
                    }
                    return 0;
                }
            });
            this.files = files;
        } else {
            Collections.sort(files, new Comparator<FileUtils>() {
                public int compare(FileUtils o1, FileUtils o2) {
                    if (o1.getCreateTime() < o2.getCreateTime()) {
                        return -1;
                    }
                    return 0;
                }
            });
            this.files = files;
        }
        super.notifyDataSetChanged();
    }

    public int getCount() {
        return this.files.size();
    }

    public Object getItem(int position) {
        return this.files.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = View.inflate(this.mContext, R.layout.file_list_item, null);
            mViewHolder.mFileIcon = (ImageView) convertView.findViewById(R.id.file_icon);
            mViewHolder.mFileName = (TextView) convertView.findViewById(R.id.file_name);
            mViewHolder.mFileTime = (TextView) convertView.findViewById(R.id.file_time);
            mViewHolder.mFileSize = (TextView) convertView.findViewById(R.id.file_size);
            mViewHolder.mFileLayout = (RelativeLayout) convertView.findViewById(R.id.file_layout);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if(files.size() > position) {
            FileUtils hFile = (FileUtils) this.files.get(position);
            if (hFile.getFileType() == 4) {
                mViewHolder.mFileIcon.setImageResource(R.drawable.ic_fb_list_folder);
            } else if (hFile.getFileType() == 1) {
                mViewHolder.mFileIcon.setImageResource(R.drawable.filetype_pic);
            } else {
                mViewHolder.mFileIcon.setImageResource(R.drawable.filetype_dzd);
            }
            if (!TextUtils.isEmpty(hFile.getFileName())) {
                mViewHolder.mFileName.setText(hFile.getFileName());
            }
            String time = this.format.format(new Date(hFile.getCreateTime()));
            if (!TextUtils.isEmpty(time)) {
                mViewHolder.mFileTime.setText(time);
            }
            if (hFile.isSelect) {
                mViewHolder.mFileLayout.setBackgroundResource(R.drawable.fb_item_list_select);
            } else {
                mViewHolder.mFileLayout.setBackgroundColor(0);
            }
        }
        return convertView;
    }
}
