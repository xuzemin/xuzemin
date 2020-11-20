package com.mphotool.whiteboard.view.menuviews;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mphotool.whiteboard.R;

/**
 * Created by Dong.Daoping on 2018/4/11 0011
 * 说明：
 */
public class PreviewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
    Bitmap bitmap = null;
    View itemView;
    ImageView current = null;
    ImageView delete = null;
    ImageView image = null;
    int number = 0;
    TextView numberView = null;

    public PreviewHolder(View itemView)
    {
        super(itemView);
        this.itemView = itemView;
        this.image = (ImageView) itemView.findViewById(R.id.image_view);
        this.current = (ImageView) itemView.findViewById(R.id.page_flag);
        this.delete = (ImageView) itemView.findViewById(R.id.delete_panel);
        this.numberView = (TextView) itemView.findViewById(R.id.page_number);
//        this.delete.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v)
//            {
//                if (PreviewAdapter.this.mContext instanceof MainActivity)
//                {
//                    ((MainActivity) PreviewAdapter.this.mContext).deletePage(ViewHolder.this.number);
//                }
//            }
//        });
//        itemView.setOnClickListener(new View.OnClickListener(PreviewAdapter.this) {
//            public void onClick(View v)
//            {
//                if (PreviewAdapter.this.mContext instanceof MainActivity)
//                {
//                    MainActivity ma = (MainActivity) PreviewAdapter.this.mContext;
//                    int last = ma.getPageIndex();
//                    ma.setPage(ViewHolder.this.number);
//                    PreviewAdapter.this.notifyItemChanged(last);
//                    PreviewAdapter.this.notifyItemChanged(ViewHolder.this.number);
//                }
//            }
//        });
    }

    public void setDeleteClickListener(View.OnClickListener listener){
        this.delete.setOnClickListener(listener);
    }

    public void setItemviewClickListener(View.OnClickListener listener){
        this.itemView.setOnClickListener(listener);
    }
}
