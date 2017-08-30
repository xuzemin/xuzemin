package com.d3.yiqi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.d3.yiqi.R;
import com.d3.yiqi.model.FriendInfo;
import com.d3.yiqi.service.XmppApplication;
import com.d3.yiqi.service.XmppService;

public class FriendListAdapter extends BaseAdapter {

	private LayoutInflater mChildInflater;
	private List<FriendInfo> friendList;
	Context context;
	
	public FriendListAdapter(Context context,List<FriendInfo> friendList){
		mChildInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.friendList = friendList;
		
	}
	class FriendHolder{
		TextView name;
		TextView mood;
		ImageView iv;
		TextView newMsgCount;
	}
	@Override
	public int getCount() {
		return friendList.size();
	}
	@Override
	public Object getItem(int position) {
		return friendList.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		    FriendHolder holder;
			if(convertView == null){
				holder = new FriendHolder();
				convertView = mChildInflater.inflate(R.layout.friend_child_item,null);
				holder.name =  (TextView) convertView.findViewById(R.id.friend_nickname);
				holder.mood =  (TextView) convertView.findViewById(R.id.friend_mood);
				holder.newMsgCount = (TextView) convertView.findViewById(R.id.new_msg_count);
				convertView.setTag(holder);
			}else{
				holder = (FriendHolder) convertView.getTag();
			}
	
			holder.newMsgCount.setVisibility(View.INVISIBLE);
			FriendInfo friendName = friendList.get(position);
			holder.name.setText(friendName.getUsername());
			holder.mood.setText(friendName.getMood());
			int newCount = XmppApplication.sharedPreferences.getInt(XmppService.getFullUsername(friendName.getUsername())
					+XmppApplication.user, 0);
			
			if(newCount!=0){
				holder.newMsgCount.setVisibility(View.VISIBLE);
				holder.newMsgCount.setText(String.valueOf(newCount));
			}
			return convertView;
	}
	

}
