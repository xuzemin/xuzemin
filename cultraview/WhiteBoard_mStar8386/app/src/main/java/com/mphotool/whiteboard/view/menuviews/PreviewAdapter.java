package com.mphotool.whiteboard.view.menuviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.activity.MainActivity;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.view.PanelManager;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewHolder> {
    private final static String TAG = "PreviewAdapter";
    private Context mContext;
    private PanelManager mPanelmanager;


    public PreviewAdapter(Context context, PanelManager manager)
    {
        this.mPanelmanager = manager;
        this.mContext = context;
    }

    public PreviewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new PreviewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.preview_item, parent, false));
    }

    public void onBindViewHolder(PreviewHolder holder, final int position)
    {
        if (holder.image != null)
        {
            Bitmap bitmap = this.mPanelmanager.getSuitBitmap(position, this.mContext.getResources().getDimensionPixelSize(R.dimen.preview_image_width), this.mContext.getResources().getDimensionPixelSize(R.dimen.preview_image_height),true);
            holder.image.setImageBitmap(bitmap);
            if (holder.bitmap != null)
            {
                holder.bitmap.recycle();
            }
            holder.bitmap = bitmap;
            holder.number = position;
            holder.numberView.setText("" + (position + 1));
//            if(getItemCount() == 1 && position == 0){
//                holder.delete.setBackgroundResource(R.drawable.delete_page_disabled);
//            }
            if (this.mPanelmanager.getCurrentPageIndex() == holder.number)
            {
                holder.current.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.current.setVisibility(View.GONE);
            }
            holder.setDeleteClickListener(new View.OnClickListener() {
                @Override public void onClick(View v)
                {
                    if (PreviewAdapter.this.mContext instanceof MainActivity)
                {
                    ((MainActivity) PreviewAdapter.this.mContext).deletePage(position);
                }
                }
            });
            holder.setItemviewClickListener(new View.OnClickListener() {

                @Override public void onClick(View v)
                {
                    if (PreviewAdapter.this.mContext instanceof MainActivity)
                    {
//                        BaseUtils.dbg(TAG, "position=" + position);
                        MainActivity ma = (MainActivity) PreviewAdapter.this.mContext;
                        int last = ma.getPageIndex();
                        ma.setPage(position);
                        PreviewAdapter.this.notifyItemChanged(last);
                        PreviewAdapter.this.notifyItemChanged(position);
                    }
                }
            });
        }
    }

    public int getItemCount()
    {
        return this.mPanelmanager.getPageCount();
    }
}
