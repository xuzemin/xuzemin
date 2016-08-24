package net.nmss.nice.activity;

import java.util.ArrayList;
import java.util.List;
import net.nmss.nice.R;
import net.nmss.nice.bean.Diamond;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EmailActivity extends Activity implements OnClickListener{
	private ListView listView;
	private ImageView imageView;
	private DiamAdapter adapter;
	private List<enpty> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email);
		imageView= (ImageView) findViewById(R.id.image);
		imageView.setOnClickListener((android.view.View.OnClickListener) this);
		listView = (ListView) findViewById(R.id.getdia);
		getdata();
		adapter = new DiamAdapter(list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(EmailActivity.this, InfoActivity.class);
				Bundle bundle = new Bundle();     
				bundle.putString("something", list.get(position).getText());  
				intent.putExtras(bundle); 
				startActivity(intent);
			}
		});
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
		list = new ArrayList<enpty>();
		for(int i = 0;i<8;i++){
			enpty diamond = new enpty("飞机天挖掘的就瓦解的挖掘的哭垃圾啊网");
			list.add(diamond);
		}
	}
	public class DiamAdapter extends BaseAdapter{
		private List<enpty> list;
		public DiamAdapter(List<enpty> list) {
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
				convertView = LayoutInflater.from(EmailActivity.this).inflate(
						R.layout.activity_email_item, null);
				viewHolder = initViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.text.setText(list.get(position).getText());
			return convertView;
		}
		private ViewHolder initViewHolder(View convertView) {
			ViewHolder viewHolder ;
			viewHolder = new ViewHolder();
			viewHolder.text=(TextView)convertView.findViewById(R.id.text);
			return viewHolder;
		}
		class ViewHolder {
			TextView text;
		}
	}
	public class enpty{
		int id;
		String text;
		public enpty(String text) {
			// TODO Auto-generated constructor stub
			this.text = text;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	}
}
