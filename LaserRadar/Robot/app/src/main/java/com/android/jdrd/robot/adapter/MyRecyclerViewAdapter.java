package com.android.jdrd.robot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.robot.R;

import java.util.List;
import java.util.Map;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyHolder> {

    private Context mContext;
    private List<Map> mList;

    public MyRecyclerViewAdapter(Context context,List<Map> list) {
        mContext = context;
        mList=list;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = View.inflate(mContext, R.layout.grid_item,null);

        return new MyHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {

        holder.tv.setText(mList.get(position).get("name").toString());

        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "第"+position+"个被点击了", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv;

        private static int count = 0;
        public MyHolder(View itemView) {
            super(itemView);
            System.out.println("count:"+(++count));
            tv= (TextView) itemView.findViewById(R.id.name);
        }
    }
}