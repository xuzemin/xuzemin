package net.nmss.nice.activity;

import java.util.ArrayList;
import java.util.List;

import net.nmss.nice.R;
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

public class BroatCastActivity extends Activity implements OnClickListener{
	private ListView listView;
	private ImageView imageView;
	private DiamAdapter adapter;
	private List<Enpty> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_broatcast);
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
		list = new ArrayList<Enpty>();
		for(int i = 0;i<8;i++){
			Enpty diamond = new Enpty("10-25 11:38","飞机天挖掘的就瓦解的挖掘的哭垃圾啊网");
			list.add(diamond);
		}
	}
	public class DiamAdapter extends BaseAdapter{
		private List<Enpty> list;
		public DiamAdapter(List<Enpty> list) {
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
				convertView = LayoutInflater.from(BroatCastActivity.this).inflate(
						R.layout.activity_broatcast_item, null);
				viewHolder = initViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.title.setText(list.get(position).getTitle());
			viewHolder.text.setText(list.get(position).getText());
			return convertView;
		}
		private ViewHolder initViewHolder(View convertView) {
			ViewHolder viewHolder ;
			viewHolder = new ViewHolder();
			viewHolder.title=(TextView)convertView.findViewById(R.id.title);
			viewHolder.text=(TextView)convertView.findViewById(R.id.text);
			return viewHolder;
		}
		class ViewHolder {
			TextView title;
			TextView text;
		}

	}
	public class Enpty{
		int id;
		String title;
		String text;
		public Enpty(String title,String text) {
			this.title = title;
			this.text = text;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	}
}
