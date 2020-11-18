package com.protruly.floatwindowlib.adapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.been.NotificationInfo;
import com.protruly.floatwindowlib.callback.TouchCallBack;
import com.protruly.floatwindowlib.control.FloatWindowManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyHolder> implements TouchCallBack {
    private Context mContext;
    ArrayList<NotificationInfo> mList;

    public NotificationAdapter(Context contetx, ArrayList<NotificationInfo> list) {
        this.mContext = contetx;
        this.mList = list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_notification, null);
        MyHolder mMyHolder = new MyHolder(view);
        return mMyHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        NotificationInfo notificationInfo = mList.get(position);
        holder.title.setText(notificationInfo.getTitle());
        holder.text.setText(notificationInfo.getText());
        holder.mIvItem.setImageDrawable(notificationInfo.getDrawable());
        Date now = Calendar.getInstance().getTime();
        String time = DateFormat.getTimeFormat(mContext).format(now);
        holder.time.setText(time);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PendingIntent pendingIntent = notificationInfo.getPendingIntent();
                if (pendingIntent != null) {
                    try {
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onItemDelete(int position) {
        String key = mList.get(position).getSbn().getKey();
        mList.remove(position);
        notifyItemRemoved(position);
       FloatWindowManager.updateNotificationList(mContext,mList,key);
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView text;
        TextView time;
        ImageView mIvItem;
        View view;

        public MyHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            text = (TextView) itemView.findViewById(R.id.text);
            mIvItem = (ImageView) itemView.findViewById(R.id.iv_item);
            time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
