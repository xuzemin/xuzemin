package net.nmss.nice.activity;

import net.nmss.nice.R;
import net.nmss.nice.utils.ExampleUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.InstrumentedActivity;

public class MessageActivity extends InstrumentedActivity implements OnClickListener{
	private RelativeLayout getzam,withme,tome,recommended,fans,nmbroat,clock;
	private TextView circle;
	private ImageView image;
	public static boolean isForeground = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_activity_layout);
		
		getzam = (RelativeLayout) findViewById(R.id.getzam);
		getzam.setOnClickListener(this);
		withme = (RelativeLayout) findViewById(R.id.withme);
		withme.setOnClickListener(this);
		tome = (RelativeLayout) findViewById(R.id.tome);
		tome.setOnClickListener(this);
		recommended = (RelativeLayout) findViewById(R.id.recommended);
		recommended.setOnClickListener(this);
		fans = (RelativeLayout) findViewById(R.id.fans);
		fans.setOnClickListener(this);
		nmbroat = (RelativeLayout) findViewById(R.id.nmbroat);
		nmbroat.setOnClickListener(this);
		clock = (RelativeLayout) findViewById(R.id.clock);
		clock.setOnClickListener(this);
		circle = (TextView) findViewById(R.id.circle);
		circle.setOnClickListener(this);
		nmbroat = (RelativeLayout) findViewById(R.id.nmbroat);
		nmbroat.setOnClickListener(this);
		image= (ImageView) findViewById(R.id.image);
		image.setOnClickListener(this);
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.getzam:
			Intent intent3= new Intent();
			intent3.setClass(this, GetDiamondActivity.class);
			startActivity(intent3);
			break;
		case R.id.withme:
			Intent intent2= new Intent();
			intent2.setClass(this, TalkWithActivity.class);
			startActivity(intent2);
			break;
		case R.id.tome:
			Intent intent4 = new Intent();
			intent4.setClass(this, TalkToMeActivity.class);
			startActivity(intent4);
			break;
		case R.id.recommended:
			Intent intent5 = new Intent();
			intent5.setClass(this, RecommendActivity.class);
			startActivity(intent5);
			break;
		case R.id.fans:
			Intent intent6 = new Intent();
			intent6.setClass(this, FansAddActivity.class);
			startActivity(intent6);
			break;
		case R.id.nmbroat:
			Intent intent7 = new Intent();
			intent7.setClass(this, BroatCastActivity.class);
			startActivity(intent7);
			break;
		case R.id.clock:
			Intent inte = new Intent();
			inte.setClass(this, NmssRemindActivity.class);
			startActivity(inte);
			break;
		case R.id.circle:
			Intent intent = new Intent();
			intent.setClass(this, HomeActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.image:
			Intent intent1 = new Intent();
			intent1.setClass(this, HomeActivity.class);
			startActivity(intent1);
			finish();
		default:
			break;
		}
		
	}
	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
	}


	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
              String messge = intent.getStringExtra(KEY_MESSAGE);
              String extras = intent.getStringExtra(KEY_EXTRAS);
              StringBuilder showMsg = new StringBuilder();
              showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
              if (!ExampleUtil.isEmpty(extras)) {
            	  showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
              }
			}
		}
	}
}
