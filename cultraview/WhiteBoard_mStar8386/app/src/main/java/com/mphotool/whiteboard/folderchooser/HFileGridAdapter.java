
package com.mphotool.whiteboard.folderchooser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mphotool.whiteboard.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HFileGridAdapter extends BaseAdapter {
    private List<FileUtils> mfiles = new ArrayList();

    private Context mContext;

    class ViewHolder {
        ImageView mFileIcon;

        LinearLayout mFileLayout;

        TextView mFileName;

        ViewHolder() {
        }
    }

    public HFileGridAdapter(Context context, List<FileUtils> files) {
        this.mfiles = files;
        this.mContext = context;
    }

    public void notifyDataSetChanged(List<FileUtils> files) {
        this.mfiles = files;
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
            this.mfiles = files;
        } else {
            Collections.sort(files, new Comparator<FileUtils>() {
                public int compare(FileUtils o1, FileUtils o2) {
                    if (o1.getCreateTime() < o2.getCreateTime()) {
                        return -1;
                    }
                    return 0;
                }
            });
            this.mfiles = files;
        }
        super.notifyDataSetChanged();
    }

    public int getCount() {
        return this.mfiles.size();
    }

    public Object getItem(int position) {
            return this.mfiles.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = View.inflate(this.mContext, R.layout.file_grid_item, null);
            mViewHolder.mFileIcon = (ImageView) convertView.findViewById(R.id.file_icon);
            mViewHolder.mFileName = (TextView) convertView.findViewById(R.id.file_name);
            mViewHolder.mFileLayout = (LinearLayout) convertView.findViewById(R.id.file_layout);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        if(mfiles.size() > position) {
            FileUtils hFile = mfiles.get(position);
            if (hFile.getFileType() == 4) {
                mViewHolder.mFileIcon.setImageResource(R.drawable.ic_fb_thumbnail_folder);
            } else if (hFile.getFileType() == 1) {
                mViewHolder.mFileIcon.setImageResource(R.drawable.image_active);
            } else {
                mViewHolder.mFileIcon.setImageResource(R.drawable.note_file_icon);
            }
            if (!TextUtils.isEmpty(hFile.getFileName())) {
                mViewHolder.mFileName.setText(hFile.getFileName());
            }
            if (hFile.isSelect) {
                mViewHolder.mFileLayout.setBackgroundResource(R.drawable.fb_item_list_select);
            } else {
                mViewHolder.mFileLayout.setBackgroundColor(0);
            }
        }
        return convertView;
    }

    public Bitmap revitionImageSize(String path, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int i = 0;
            while (true) {
                if ((options.outWidth >> i) > maxWidth || (options.outHeight >> i) > maxHeight) {
                    i++;
                } else {
                    in = new BufferedInputStream(new FileInputStream(new File(path)));
                    options.inSampleSize = (int) Math.pow(2.0d, (double) i);
                    options.inJustDecodeBounds = false;
                    return BitmapFactory.decodeStream(in, null, options);
                }
            }
        } catch (Exception e) {
            return null;
        }
    }
}
