package net.nmss.nice.activity;

import java.util.Calendar;

import net.nmss.nice.R;
import net.nmss.nice.bean.RemindEntity;
import net.nmss.nice.dao.RemindDao;
import net.nmss.nice.service.AlarmKlaxonService;
import net.nmss.nice.widget.wheel_view.ArrayWheelAdapter;
import net.nmss.nice.widget.wheel_view.OnWheelScrollListener;
import net.nmss.nice.widget.wheel_view.WheelView;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MorningRemingAcitivity extends Activity implements OnClickListener,OnWheelScrollListener,OnCheckedChangeListener{
	private ImageView image;
	private TextView morning,circle;
	private Calendar c = Calendar.getInstance();
	private WheelView view1,view2;
	private String[] hourItems = new String[24];
	private String[] minuteItems =new String[60];
	private RemindEntity entity;
	private ToggleButton mori;
	private RemindDao remindDao;
	private boolean touch = false;
	private DialogInterface.OnClickListener clickListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.morning_remind_activity_layout);
		c.setTimeInMillis(System.currentTimeMillis());
		image = (ImageView) findViewById(R.id.image);
		image.setOnClickListener(this);
		circle = (TextView) findViewById(R.id.circle);
		circle.setOnClickListener(this);
		morning = (TextView) findViewById(R.id.morning);
		morning.setOnClickListener(this);
		mori = (ToggleButton) findViewById(R.id.mori);
		mori.setOnCheckedChangeListener(this);
		remindDao = new RemindDao(this);
		entity = remindDao.getReminds().get(0);
		c.setTimeInMillis(entity.getTime());
		if(entity.getActivate()==1){
			mori.setChecked(true);
		}else{
			mori.setChecked(false);
		}
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String minute = String.valueOf(c.get(Calendar.MINUTE));
		if(hour.length() <= 1) {
			hour = "0"+hour;
		}
		
		if(minute.length() <= 1) {
			minute = "0"+minute;
		}
		morning.setText(hour+":"+minute);
		clickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(c.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				morning.setText(hour+":"+minute);
				mori.setChecked(true);
				saveRemind();
			}
		};
	}

	protected void saveRemind() {
		entity.setTime(c.getTimeInMillis());
		int count = remindDao.updateRemind(entity, this);
		if(count <=0 ) {
			Toast.makeText(this, "更改失败", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "更改成功", Toast.LENGTH_SHORT).show();
			c.setTimeInMillis(System.currentTimeMillis());
			c.setTimeInMillis(entity.getTime());
			c.set(Calendar.SECOND, 0);
			while(c.getTimeInMillis() <= System.currentTimeMillis() ){
				c.add(Calendar.DAY_OF_MONTH, 1);
			}
			Intent intent = new Intent(this, AlarmKlaxonService.class); 
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entity.getId(), intent, 0); 
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
            alarmManager.cancel(pendingIntent); 
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent); 
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
            touch = true;
			mori.setChecked(true);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.image:
			finish();
			break;
		case R.id.circle:
			finish();
			break;
		case R.id.morning:
			LayoutInflater inflater = getLayoutInflater();
		  View layout = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  view1 = (WheelView) layout.findViewById(R.id.wheelview1);
		  view2 = (WheelView) layout.findViewById(R.id.wheelview2);
		  view1.addScrollingListener(this);
		  view2.addScrollingListener(this);
		  view1.setCyclic(true);
		  view2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			view1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			view1.setLabel("  时");
			view2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			view2.setLabel("  分");
			view1.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));
			view2.setCurrentItem(c.get(Calendar.MINUTE));
		  new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout)
		     .setPositiveButton("确定", clickListener)
		     .setNegativeButton("取消", null).show();
			break;
		default:
			break;
		}
	}
	@Override
	public void onScrollingStarted(WheelView wheel) {
		// TODO Auto-generated method stub
	}
	private Integer getWheelViewCurrentItemString(WheelView wheelView) {
		int index = wheelView.getCurrentItem();
		return index;
	}

	@Override
	public void onScrollingFinished(WheelView wheel) {
		if(wheel.equals(view1)){
			c.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else{
			c.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		entity.setActivate(isChecked==true?1:0);
		if(touch == false){
		int count = remindDao.updateRemind(entity,this);
		if(count <=0) {
			Toast.makeText(this, "更改失败", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "更改成功", Toast.LENGTH_SHORT).show();
			if(entity.getActivate()==1){
				Intent intent = new Intent(this, AlarmKlaxonService.class); 
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entity.getId(), intent, 0); 
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
                alarmManager.cancel(pendingIntent); 
                while(c.getTimeInMillis() <= System.currentTimeMillis() ){
    				c.add(Calendar.DAY_OF_MONTH, 1);
    			}
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent); 
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
			}else{
				  Intent intent = new Intent(this, AlarmKlaxonService.class); 
	              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entity.getId(), intent, 0); 
	              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	              alarmManager.cancel(pendingIntent); 
			}
		}
		touch = false;
		}
	}
}
