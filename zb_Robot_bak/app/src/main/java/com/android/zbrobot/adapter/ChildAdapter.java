package com.android.zbrobot.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;

import com.android.zbrobot.R;
import com.android.zbrobot.util.NativeImageLoader;
import com.android.zbrobot.view.MyImageView;

public class ChildAdapter extends BaseAdapter {
	private Point mPoint = new Point(0, 0);
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private GridView mGridView;
	private List<String> list;
	public static int Current_INDEX = -1;
	protected LayoutInflater mInflater;

	public ChildAdapter(Context context, List<String> list, GridView mGridView) {
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
		Current_INDEX = -1;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = list.get(position);
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.zb_grid_child_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mCheckBox = (ImageView) convertView.findViewById(R.id.child_checkbox);
			
			viewHolder.mImageView.setOnMeasureListener(new MyImageView.OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.mipmap.friends_sends_pictures_no);
		}
		viewHolder.mImageView.setTag(path);
//		viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
//					addAnimation(viewHolder.mCheckBox);
//				}
//				mSelectMap.put(position, isChecked);
//
//				Current_INDEX = position;
//			}
//		});
		if(position == Current_INDEX){
			viewHolder.mCheckBox.setVisibility(View.VISIBLE);
		}else{
			viewHolder.mCheckBox.setVisibility(View.GONE);
		}
//		viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);
		
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageLoader.NativeImageCallBack() {
			
			@Override
			public void onImageLoader(Bitmap bitmap, String path) {
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if(bitmap != null && mImageView != null){
					mImageView.setImageBitmap(bitmap);
				}
			}
		});
		
		if(bitmap != null){
			viewHolder.mImageView.setImageBitmap(bitmap);
		}else{
			viewHolder.mImageView.setImageResource(R.mipmap.friends_sends_pictures_no);
		}
		
		return convertView;
	}
	
	private void addAnimation(View view){
		float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules), 
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
				set.setDuration(150);
		set.start();
	}
	
	
	public List<Integer> getSelectItems(){
		List<Integer> list = new ArrayList<Integer>();
		for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
			Map.Entry<Integer, Boolean> entry = it.next();
			if(entry.getValue()){
				list.add(entry.getKey());
			}
		}
		
		return list;
	}

	public static class ViewHolder{
		public MyImageView mImageView;
		public ImageView mCheckBox;
	}

}
