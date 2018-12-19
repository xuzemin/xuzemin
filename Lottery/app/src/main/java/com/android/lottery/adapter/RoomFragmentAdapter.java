package com.android.lottery.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.lottery.R;
import com.android.lottery.bean.MsgForPDBean;
import com.android.lottery.bean.NiceUserInfo;
import com.android.lottery.bean.RoomBean;
import com.android.lottery.util.HttpHelpers;
import com.android.lottery.util.ImageLoaderUtils;
import com.android.lottery.util.LogUtil;
import com.android.lottery.util.NiceConstants;
import com.android.lottery.view.RoundImageView;

@SuppressLint("HandlerLeak")
public class RoomFragmentAdapter extends BaseAdapter implements
		View.OnClickListener {

	private final static String LOG_TAG = "PartnersDynamicAdapter";
	private Activity mContext;
	private List<RoomBean> moodMsgs;

	private Handler mHandler;

	public RoomFragmentAdapter(Activity context) {
		this.mContext = context;
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case NiceConstants.ON_FAILURE:
					Toast.makeText(mContext, "网络不给力...", Toast.LENGTH_SHORT)
							.show();
					break;
				case NiceConstants.ON_SUCCESS:
					parseDiggJson(msg);
					break;
				default:
					break;
				}

			}
		};
	}

	private void parseDiggJson(Message msg) {
		try {
			JSONObject json = new JSONObject((String) msg.obj);
			String rc = json.getString(NiceConstants.RECONTENT);
			int rCode = json.getInt(NiceConstants.RECODE);

			MsgForPDBean bean = new MsgForPDBean();
			bean.setContent(rc);
			bean.setPosition(msg.arg1);
			bean.setrCode(rCode);

			int position = bean.getPosition();
			RoomBean moodMsgBean = moodMsgs.get(position);
			if (bean.getrCode() == 200000) {
				moodMsgBean.setDiggNum(moodMsgBean.getDiggNum() + 1);
				moodMsgBean.setDigg(true);
				notifyDataSetChanged();
			} else {
				moodMsgBean.setDiggNum(moodMsgBean.getDiggNum() - 1);
				moodMsgBean.setDigg(true);
				notifyDataSetChanged();
			}
			Toast.makeText(mContext, bean.getContent(), Toast.LENGTH_SHORT)
					.show();
			if (rc.contains("成功")) {
				bean.setState(1);
			} else {
				bean.setState(0);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void appendMood(List<RoomBean> beans) {
		this.moodMsgs.addAll(beans);
		notifyDataSetChanged();
	}

	public void updateMoods(List<RoomBean> beans) {
		this.moodMsgs = beans;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return (moodMsgs == null || moodMsgs.isEmpty()) ? 0 : moodMsgs.size();
	}

	@Override
	public RoomBean getItem(int position) {
		return moodMsgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * uid
	 * 
	 * head_pic
	 * 
	 * nickname --
	 * 
	 * message_time --
	 * 
	 * message_id
	 * 
	 * message_content --
	 * 
	 * type
	 * 
	 * 
	 * digg_num//赞个数 --
	 * 
	 * comment_num --
	 * 
	 * big_pic
	 * 
	 * small_pic
	 * */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.room_fragment_adapter_layout, null);
			viewHolder = initViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		RoomBean bean = moodMsgs.get(position);
		setTextViewContent(viewHolder.tvDiggNum,
				String.valueOf(bean.getDiggNum()), position);
		setTextViewContent(viewHolder.tvCommentNum,
				String.valueOf(bean.getCommentNum()), position);
		viewHolder.tvShare.setOnClickListener(this);
		viewHolder.tvShare.setTag(position);
		viewHolder.tvMessageTime.setText(bean.getMessageTime());
		viewHolder.tvNickName.setText(bean.getNickname());
		viewHolder.imgHeadPic.setTag(position);
		viewHolder.imgHeadPic.setOnClickListener(this);
		ImageLoaderUtils.getInstance().displayUserHeadPic(bean.getHeadPic(),
				viewHolder.imgHeadPic);
		LogUtil.i(LOG_TAG, bean.getHeadPic());
		String small_pic = bean.getSmallPic();
		if (small_pic != null && !"".equals(small_pic)) {
			viewHolder.img_pic.setVisibility(View.VISIBLE);
			viewHolder.img_pic.setOnClickListener(this);
			viewHolder.img_pic.setTag(position);
			ImageLoaderUtils.getInstance().displayImageWithCache(small_pic,
					viewHolder.img_pic);
		} else {
			viewHolder.img_pic.setVisibility(View.GONE);
		}
		
		viewHolder.tvCommentNum.setOnClickListener(this);
		
		String info = bean.getMessageContent();
		Matcher mt = Pattern.compile("#(.*?)#").matcher(info);
		SpannableString ss = new SpannableString(info);
		if(mt.find()){
			ss.setSpan(
					new ClickableSpan() {
						@Override
						public void onClick(View widget) {
							// TODO Auto-generated method stub
//							mContext.startActivity(new Intent(mContext,
//									MyYesActivity.class));
//							mContext.finish();
						}
					}, info.indexOf("#"), mt.group(1).length() + 2,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new ForegroundColorSpan(R.color.theme_color),
					info.indexOf("#"), mt.group(1).length() + 2,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		viewHolder.tvContent.setText(ss);
		return convertView;
	}

	private ViewHolder initViewHolder(View convertView) {
		ViewHolder viewHolder;
		viewHolder = new ViewHolder();
		viewHolder.tvDiggNum = (TextView) convertView
				.findViewById(R.id.room_fragment_adapter_praise);
		viewHolder.tvCommentNum = (TextView) convertView
				.findViewById(R.id.room_fragment_adapter_comment);
		viewHolder.tvShare = (TextView) convertView
				.findViewById(R.id.room_fragment_adapter_share);
		viewHolder.tvContent = (TextView) convertView
				.findViewById(R.id.room_fragment_adapter_content);
		viewHolder.tvMessageTime = (TextView) convertView
				.findViewById(R.id.room_fragment_adapter_time);
		viewHolder.tvNickName = (TextView) convertView
				.findViewById(R.id.room_fragment_adapter_name);
		viewHolder.tvType = (TextView) convertView.findViewById(R.id.room_fragment_adapter_type);
		viewHolder.imgHeadPic = (RoundImageView) convertView
				.findViewById(R.id.room_fragment_adapter_head);
		viewHolder.img_pic = (ImageView) convertView
				.findViewById(R.id.room_fragment_adapter_pic);
		return viewHolder;
	}

	@Override
	public void onClick(View v) {
		Object obj = v.getTag();
		switch (v.getId()) {
		case R.id.room_fragment_adapter_praise:// 赞
			boolean isDigg = this.moodMsgs.get((Integer) obj).isDigg();
			LogUtil.i(LOG_TAG, "赞->position:" + v.getTag());
			if (isDigg == true) {
				Toast.makeText(mContext, "你已经赞过这条心情了...", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			String messageId = this.moodMsgs.get((Integer) obj).getMessageId();
			HttpHelpers.makeDigg(messageId, (Integer) obj, 5, mHandler);
			break;
		case R.id.room_fragment_adapter_comment:// 评论
//			Intent intent = new Intent(mContext,
//					RoomCommentActivity.class);
//			intent.putExtra(NiceConstants.PARTNERS_DYNAMIC_FLAG,
//					moodMsgs.get((Integer) v.getTag()));
//			mContext.startActivity(intent);
			break;
		case R.id.room_fragment_adapter_share:// 分享
			break;
		case R.id.room_fragment_adapter_head:// 头像
//			if (NiceUserInfo.getInstance().getUId()
//					.equals(moodMsgs.get((Integer) v.getTag()).getUid())) {
//				mContext.startActivity(new Intent(mContext,MyHomePageActivity.class));
//			}else{
//				Intent intent3 = new Intent(mContext, OtherHomePageActivity.class);
//				intent3.putExtra("uid", moodMsgs.get((Integer) v.getTag()).getUid());
//				mContext.startActivity(intent3);
//			}
			break;
		case R.id.room_fragment_adapter_pic://图片
//			Intent intent2 = new Intent(mContext, PhotoViewActivity.class);
//			intent2.putExtra(NiceConstants.BIG_PIC,
//					moodMsgs.get((Integer) v.getTag()).getBigPic());
//			mContext.startActivity(intent2);
			break;
		default:
			break;
		}
	}

	class ViewHolder {
		TextView tvDiggNum;// 赞
		TextView tvCommentNum;// 评论
		TextView tvShare;// 分享
		TextView tvContent;
		TextView tvMessageTime;
		TextView tvNickName;
		TextView tvType;// 由xxx推荐
		RoundImageView imgHeadPic;
		ImageView img_pic;
	}

	private void setTextViewContent(TextView tv, String content, int position) {
		if (content == null || "".equals(content)) {
			tv.setText(" ");
			return;
		}
		tv.setText("  " + content);
		tv.setTag(position);
		tv.setOnClickListener(this);
	}
}
