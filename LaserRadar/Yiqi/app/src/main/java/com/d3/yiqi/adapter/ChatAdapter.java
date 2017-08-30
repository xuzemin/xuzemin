package com.d3.yiqi.adapter;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.d3.yiqi.R;
import com.d3.yiqi.model.Msg;
import com.d3.yiqi.model.OneFridenMessages;
import com.d3.yiqi.service.XmppConnection;
import com.d3.yiqi.util.ExpressionUtil;

public class ChatAdapter extends BaseAdapter {
	private Context cxt;
	private LayoutInflater inflater;
	OneFridenMessages friendMessageBean;
	private int SCALE_SIZE = 5;  //足够大则会撑满屏幕
	
	public ChatAdapter(Context context,OneFridenMessages friendMessageBean) {
		this.cxt = context;
		this.friendMessageBean = friendMessageBean;
	}
	@Override
	public int getCount() {
		return friendMessageBean.MessageList.size();
	}
	@Override
	public Object getItem(int position) {
		return friendMessageBean.MessageList.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		this.inflater = (LayoutInflater) this.cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Msg nowMsg = friendMessageBean.MessageList.get(position);
		ProgressBar progressBar = null;
		if(nowMsg.getInOrOut().equals("IN")){
			convertView = this.inflater.inflate(R.layout.formclient_chat_in, null);
			progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar1);
		}else{
			convertView = this.inflater.inflate(R.layout.formclient_chat_out, null);
		}
		TextView useridView = (TextView) convertView.findViewById(R.id.formclient_row_userid);
		TextView dateView = (TextView) convertView.findViewById(R.id.formclient_row_date);
		final TextView msgView = (TextView) convertView.findViewById(R.id.formclient_row_msg);
		useridView.setText(nowMsg.getUsername());
		dateView.setText(nowMsg.getSendDate());
		//适配图片
		if(nowMsg.getMsg()!=null &&nowMsg.getMsg().contains(XmppConnection.SAVE_PATH)){
			final String picPath = nowMsg.getMsg(); 
			Drawable draw ;
			if(new File(picPath).length()!=0){
	        	draw = new BitmapDrawable(BitmapFactory.decodeFile(picPath));
		        draw.setBounds(0, 0, 0, 0);
		        msgView.setCompoundDrawablesWithIntrinsicBounds(null,draw, null,null); 
		        msgView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						initPopWindow(BitmapFactory.decodeFile(picPath),msgView);
					}
				});
			}
			else{
				msgView.setText("图片加载中...");
				if(progressBar!=null)
				progressBar.setVisibility(convertView.VISIBLE);
	        }
		}
		else{  //适配表情
		String zhengze = "f0[0-9]{2}|f10[0-7]"; // 正则表达式，用来判断消息内是否有表情
		try {
			SpannableString spannableString = ExpressionUtil
					.getExpressionString(cxt, nowMsg.getMsg(), zhengze);
			msgView.setText(spannableString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		
		return convertView;
	}
	
	 private void initPopWindow(Bitmap mBitmap,View view) {  
	        // 加载popupWindow的布局文件  
	        View contentView = LayoutInflater.from(cxt)  
	                .inflate(R.layout.popup, null);  
	        // 设置popupWindow的背景颜色  
	        contentView.setBackgroundColor(Color.BLACK);
	        // 声明一个弹出框  
	        final PopupWindow popupWindow = new PopupWindow(  
	        		 contentView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);  
	         popupWindow.setFocusable(true);
	         popupWindow.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);  
	         ImageView imageView=(ImageView) contentView.findViewById(R.id.logo_b);
	         int width = mBitmap.getWidth()*SCALE_SIZE;
	         int height = mBitmap.getHeight()*SCALE_SIZE;
	         //在这儿就可以实现图片的大小缩放
	          Bitmap resize=Bitmap.createScaledBitmap(mBitmap, height, width, true);
//	          Matrix m=new Matrix();
	          //做好旋转与大小之后，重新创建位图，0-width宽度上显示的区域，高度类似
	          Bitmap b=Bitmap.createBitmap(resize, 0, 0, height, width, null, true);
	          //显示图片
	         imageView.setImageBitmap(b);
	         imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					popupWindow.dismiss();
				}
			});
	    }
}
