package net.nmss.nice.activity;

import net.nmss.nice.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AlarmDialogActivity extends BaseActivity {

	private TextView alarmtitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_actitivy_alarm_alart);
		initViews();
		String title = getIntent().getExtras().getString("title");
		alarmtitle.setText(title);
		playAlarm();
		disableAlarm();	
	}
	
	private void initViews() {
		alarmtitle = (TextView)findViewById(R.id.alarmtitle);
	}
	
	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.alarmwait:
			stopAlarm();
			break;
		case R.id.alarmstop:
			stopAlarm();

			disableAlarm();
			
			break;
		default:
			break;
		}
		finish();
	}
	
	private void stopAlarm() {
		Intent intent = new Intent();
		sendBroadcast(intent);
	}
	
	private void playAlarm() {
		Intent intent = new Intent();
		sendBroadcast(intent);
	}
	
	private void disableAlarm() {
		Intent intent = new Intent();
		sendBroadcast(intent);
	}
}
