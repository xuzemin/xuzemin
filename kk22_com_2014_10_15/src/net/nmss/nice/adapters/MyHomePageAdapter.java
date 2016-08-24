package net.nmss.nice.adapters;

import java.util.List;

import net.nmss.nice.R;
import net.nmss.nice.activity.PhotoViewActivity;
import net.nmss.nice.bean.RoomBean;
import net.nmss.nice.utils.ImageLoaderUtils;
import net.nmss.nice.utils.NiceConstants;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyHomePageAdapter extends BaseAdapter implements
		View.OnClickListener {
	private Activity context;
	private List<RoomBean> list;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NiceConstants.ON_FAILURE:
				Toast.makeText(context, "网络不给力...", Toast.LENGTH_SHORT).show();
				break;
			case NiceConstants.ON_SUCCESS:
				// parseDiggJson(msg);
				break;
			default:
				break;
			}

		}
	};

	public MyHomePageAdapter(Activity context, List<RoomBean> list) {
		// TODO 自动生成的构造函数存根
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return (list == null || list.isEmpty()) ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.myhomepage_adapter_layout, null);
			holder = new ViewHolder();
			holder.username = (TextView) convertView
					.findViewById(R.id.myhomepage_adapter_name);
			holder.time = (TextView) convertView
					.findViewById(R.id.myhomepage_adapter_time);
			holder.content_description = (TextView) convertView
					.findViewById(R.id.myhomepage_adapter_content);
			holder.content_pic = (ImageView) convertView
					.findViewById(R.id.myhomepage_adapter_pic);
			holder.tvComm = (TextView) convertView
					.findViewById(R.id.myhomepage_adapter_comment);
			holder.tvDigg = (TextView) convertView
					.findViewById(R.id.myhomepage_adapter_praise);
			holder.tvShare = (TextView) convertView
					.findViewById(R.id.myhomepage_adapter_share);
			holder.tvRelay = (TextView) convertView
					.findViewById(R.id.myhomepage_adapter_relay);
			holder.tvDel = (TextView) convertView
					.findViewById(R.id.myhomepage_adapter_del);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		RoomBean bean = list.get(position);
		holder.username.setText(bean.getNickname());
		holder.content_description.setText(bean.getMessageContent());
		holder.time.setText(bean.getMessageTime());
		holder.content_pic.setTag(position);
		String url = bean.getSmallPic();
		if (!TextUtils.isEmpty(url)) {
			holder.content_pic.setVisibility(View.VISIBLE);
			holder.content_pic.setOnClickListener(this);
			ImageLoaderUtils.getInstance()
					.displayImage(url, holder.content_pic);
		} else {
			holder.content_pic.setVisibility(View.GONE);
		}
		holder.tvComm.setTag(position);
		holder.tvDigg.setTag(position);
		holder.tvDigg.setText("  " + bean.getDiggNum());
		holder.tvShare.setTag(position);
		holder.tvComm.setOnClickListener(this);
		holder.tvComm.setText("  " + bean.getCommentNum());
		holder.tvDigg.setOnClickListener(this);
		holder.tvShare.setOnClickListener(this);
		holder.tvRelay.setOnClickListener(this);
		holder.tvDel.setOnClickListener(this);
		return convertView;
	}

	class ViewHolder {
		private TextView username;// 用户名
		private TextView time;// 发表时间
		private TextView content_description;// 发表内容
		private ImageView content_pic;// 发表图片
		private TextView tvDigg;
		private TextView tvComm;
		private TextView tvShare;
		private TextView tvRelay;
		private TextView tvDel;
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch (v.getId()) {
		case R.id.myhomepage_adapter_pic:
			Intent intent2 = new Intent(context, PhotoViewActivity.class);
			intent2.putExtra(NiceConstants.BIG_PIC,
					list.get((Integer) v.getTag()).getBigPic());
			context.startActivity(intent2);
			break;

		case R.id.myhomepage_adapter_praise:
			break;

		case R.id.myhomepage_adapter_comment:
			break;

		case R.id.myhomepage_adapter_share:
			break;
			
		case R.id.myhomepage_adapter_relay:
			break;
			
		case R.id.myhomepage_adapter_del:
			break;
		default:
			break;
		}
	}

}
