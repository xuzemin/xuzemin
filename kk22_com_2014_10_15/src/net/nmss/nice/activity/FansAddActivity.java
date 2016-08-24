package net.nmss.nice.activity;

import java.util.ArrayList;
import java.util.List;
import net.nmss.nice.R;
import net.nmss.nice.bean.Diamond;
import net.nmss.nice.bean.FansAdd;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FansAddActivity extends Activity implements OnClickListener{
	private ListView listView;
	private ImageView imageView;
	private DiamAdapter adapter;
	private List<FansAdd> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fansadd);
		imageView= (ImageView) findViewById(R.id.image);
		imageView.setOnClickListener((android.view.View.OnClickListener) this);
		listView = (ListView) findViewById(R.id.getdia);
		getdata();
		adapter = new DiamAdapter(list);
		listView.setAdapter(adapter);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image:
			finish();
			break;
		default:
			break;
		}
	}
	private void getdata() {
		list = new ArrayList<FansAdd>();
		for(int i = 0;i<8;i++){
			FansAdd diamond = new FansAdd(1,"","柠檬","10-25 11:38");
			list.add(diamond);
		}
	}
	public class DiamAdapter extends BaseAdapter{
		private List<FansAdd> list;
		public DiamAdapter(List<FansAdd> list) {
			this.list=list;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(FansAddActivity.this).inflate(
						R.layout.activity_fansadd_item, null);
				viewHolder = initViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.createTime.setText(list.get(position).getCreateTime());
			viewHolder.imgHeadPic.setBackgroundResource(R.drawable.broat);
			viewHolder.username.setText(list.get(position).getUsername());
			return convertView;
		}
		private ViewHolder initViewHolder(View convertView) {
			ViewHolder viewHolder ;
			viewHolder = new ViewHolder();
			viewHolder.imgHeadPic=(ImageView)convertView.findViewById(R.id.head);
			viewHolder.createTime=(TextView)convertView.findViewById(R.id.createtime);
			viewHolder.username=(TextView)convertView.findViewById(R.id.username);
			return viewHolder;
		}
		class ViewHolder {
			TextView username;
			TextView createTime;
			ImageView imgHeadPic;
		}

	}
}
