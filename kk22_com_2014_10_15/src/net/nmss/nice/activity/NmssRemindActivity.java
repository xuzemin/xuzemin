package net.nmss.nice.activity;

import java.util.Calendar;
import java.util.List;

import net.nmss.nice.R;
import net.nmss.nice.bean.RemindEntity;
import net.nmss.nice.dao.RemindDao;
import net.nmss.nice.service.AlarmKlaxonService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NmssRemindActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener{
	private RelativeLayout mo,ea,ru,drin,emai;
	private TextView morning,eating,runing,drinking,circle;
	private ImageView image;
	public ToggleButton mori,eati,runi,drinki;
	private RemindDao dao;
	private Calendar c = Calendar.getInstance();
	private List<RemindEntity> entities;
	private boolean touch = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remind_set_layout);
		c.setTimeInMillis(System.currentTimeMillis());
		image = (ImageView) findViewById(R.id.image);
		image.setOnClickListener(this);
		circle = (TextView) findViewById(R.id.circle);
		circle.setOnClickListener(this);
		mo = (RelativeLayout) findViewById(R.id.mo);
		mo.setOnClickListener(this);
		ea = (RelativeLayout) findViewById(R.id.ea);
		ea.setOnClickListener(this);
		ru = (RelativeLayout) findViewById(R.id.ru);
		ru.setOnClickListener(this);
		drin = (RelativeLayout) findViewById(R.id.drin);
		drin.setOnClickListener(this);
		emai = (RelativeLayout) findViewById(R.id.emai);
		emai.setOnClickListener(this);
		morning = (TextView) findViewById(R.id.morning);
		eating = (TextView) findViewById(R.id.eating);
		runing = (TextView) findViewById(R.id.runing);
		drinking = (TextView) findViewById(R.id.drinking);
		mori = (ToggleButton) findViewById(R.id.mori);
		eati = (ToggleButton) findViewById(R.id.eati);
		runi = (ToggleButton) findViewById(R.id.runi);
		drinki = (ToggleButton) findViewById(R.id.drinki);
		mori.setOnCheckedChangeListener(this);
		eati.setOnCheckedChangeListener(this);
		runi.setOnCheckedChangeListener(this);
		drinki.setOnCheckedChangeListener(this);
		Remind();
	}
	private void Remind() {
		dao = new RemindDao(this);
		entities = dao.getReminds();
		while(entities == null){
			Calendar c = Calendar.getInstance();
			RemindEntity entity = new RemindEntity();
			entity.setId(1);
			entity.setName("早安提醒");
		   c.set(Calendar.HOUR_OF_DAY, 8);
		   c.set(Calendar.MINUTE,0);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(2);
			entity.setName("早餐提醒");
			c.set(Calendar.HOUR_OF_DAY,7);
			c.set(Calendar.MINUTE,20);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(3);
			entity.setName("中餐提醒");
			c.set(Calendar.HOUR_OF_DAY,12);
			c.set(Calendar.MINUTE,20);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(4);
			entity.setName("晚餐提醒");
			c.set(Calendar.HOUR_OF_DAY,18);
			c.set(Calendar.MINUTE,20);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(5);
			entity.setName("加餐提醒");
			c.set(Calendar.HOUR_OF_DAY,15);
			c.set(Calendar.MINUTE,30);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(6);
			entity.setName("运动提醒");
			c.set(Calendar.HOUR_OF_DAY,20);
			c.set(Calendar.MINUTE,0);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(7);
			entity.setName("第一杯水提醒");
			c.set(Calendar.HOUR_OF_DAY,6);
			c.set(Calendar.MINUTE,30);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(8);
			entity.setName("第二杯水提醒");
			c.set(Calendar.HOUR_OF_DAY,8);
			c.set(Calendar.MINUTE,30);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(9);
			entity.setName("第三杯水提醒");
			c.set(Calendar.HOUR_OF_DAY,11);
			c.set(Calendar.MINUTE,0);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(10);
			entity.setName("第四杯水提醒");
			c.set(Calendar.HOUR_OF_DAY,12);
			c.set(Calendar.MINUTE,40);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(11);
			entity.setName("第五杯水提醒");
			c.set(Calendar.HOUR_OF_DAY,14);
			c.set(Calendar.MINUTE,40);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(12);
			entity.setName("第六杯水提醒");
			c.set(Calendar.HOUR_OF_DAY,17);
			c.set(Calendar.MINUTE,0);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(13);
			entity.setName("第七杯水提醒");
			c.set(Calendar.HOUR_OF_DAY,19);
			c.set(Calendar.MINUTE,40);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entity.setId(14);
			entity.setName("第八杯水提醒");
			c.set(Calendar.HOUR_OF_DAY,22);
			c.set(Calendar.MINUTE,0);
			entity.setTime(c.getTimeInMillis());
			dao.addRemind(entity);
			entities = dao.getReminds();
		}
		moriningNotify();
		eatingNotify();
		runningNotify();
		drinkingNotify();
	}
	private void drinkingNotify() {
		// TODO Auto-generated method stub
		String str = "每天 ";
		Boolean flag = false;
		for(int i = 6;i<=13;i++){
			if(entities.get(i).getActivate() == 1){
				Calendar c =Calendar.getInstance();
				c.setTimeInMillis(entities.get(i).getTime());
				String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(c.get(Calendar.MINUTE));
				flag = true;
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				str = str + hour+":"+minute+" ";
				}
		}if(flag){
			if(!drinki.isChecked()){
			touch = true;
			}
			drinki.setChecked(true);
			drinking.setText(str);
		}else{
			if(drinki.isChecked()){
			touch = true;
			}
			drinki.setChecked(false);
			drinking.setText("未开启");
		}
	}
	private void runningNotify() {
		// TODO Auto-generated method stub
		String str = "每天 ";
		if(entities.get(5).getActivate() == 1){
			Calendar c =Calendar.getInstance();
			c.setTimeInMillis(entities.get(5).getTime());
			String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
			String minute = String.valueOf(c.get(Calendar.MINUTE));
			if(!runi.isChecked()){
			touch = true;
			}
			runi.setChecked(true);
			if(hour.length() <= 1) {
				hour = "0"+hour;
			}
			if(minute.length() <= 1) {
				minute = "0"+minute;
			}
			runing.setText(str+hour+":"+minute);
			}else{
				if(runi.isChecked()){
				touch = true;
				}
				runing.setText("未开启");
				runi.setChecked(false);
			}
	}
	private void moriningNotify(){
		String str = "每天 ";
		if(entities.get(0).getActivate() == 1){
			Calendar c =Calendar.getInstance();
			c.setTimeInMillis(entities.get(0).getTime());
			String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
			String minute = String.valueOf(c.get(Calendar.MINUTE));
			if(!mori.isChecked()){
			touch = true;
			}
			mori.setChecked(true);
			if(hour.length() <= 1) {
				hour = "0"+hour;
			}
			if(minute.length() <= 1) {
				minute = "0"+minute;
			}
			morning.setText(str+hour+":"+minute);
			}else{
				if(mori.isChecked()){
				touch = true;
				}
				mori.setChecked(false);
				morning.setText("未开启");
			}
	}
	private void eatingNotify(){
		String str = "每天 ";
		boolean flag = false;
		for(int i = 1;i<5;i++){
			if(entities.get(i).getActivate() == 1){
				Calendar c =Calendar.getInstance();
				c.setTimeInMillis(entities.get(i).getTime());
				String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(c.get(Calendar.MINUTE));
				flag = true;
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				str = str + hour+":"+minute+" ";
				}
		}if(flag){
			if(!eati.isChecked()){
			touch = true;
			}
			eati.setChecked(true);
			eating.setText(str);
		}else{
			if(eati.isChecked()){
			touch = true;
			}
			eati.setChecked(false);
			eating.setText("未开启");
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.circle:
			finish();
			break;
		case R.id.image:
			finish();
			break;
		case R.id.mo:
			Intent intent = new Intent();
			intent.setClass(this, MorningRemingAcitivity.class);
			startActivity(intent);
			break;
		case R.id.ea:
			Intent intent1 = new Intent();
			intent1.setClass(this, EatRemindActivity.class);
			startActivity(intent1);
			break;
		case R.id.ru:
			Intent intent2 = new Intent();
			intent2.setClass(this, RunRemindActivity.class);
			startActivity(intent2);
			break;
		case R.id.drin:
			Intent intent3 = new Intent();
			intent3.setClass(this, DrinkRemindActivity.class);
			startActivity(intent3);
			break;
		case R.id.emai:
			Intent intent4 =new Intent();
			intent4.setClass(this, EmailActivity.class);
			startActivity(intent4);
			break;
		default:
			break;
		}
	}
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(touch == false){
			int count;
		switch (buttonView.getId()) {
		case R.id.mori:
			entities.get(0).setActivate(isChecked==true?1:0);
				count = dao.updateRemind(entities.get(0),this);
				if(count <=0 ) {
					Toast.makeText(this, "更改失败", Toast.LENGTH_SHORT).show();
				} else {
					if(entities.get(0).getActivate()==1){
						Intent intent = new Intent(this, AlarmKlaxonService.class); 
		                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entities.get(0).getId(), intent, 0); 
		                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		                alarmManager.cancel(pendingIntent); 
		                c.setTimeInMillis(System.currentTimeMillis());
		                c.setTimeInMillis(entities.get(0).getTime());
		                c.set(Calendar.SECOND, 0);
		                while(c.getTimeInMillis() <= System.currentTimeMillis() ){
		    				c.add(Calendar.DAY_OF_MONTH, 1);
		    			}
		                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent); 
		                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
					}else{
						  Intent intent = new Intent(this, AlarmKlaxonService.class); 
			              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entities.get(0).getId(), intent, 0); 
			              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
			              alarmManager.cancel(pendingIntent); 
					}
				}
			moriningNotify();
			break;
		case R.id.eati:
				for(int i = 1;i<5;i++){
					entities.get(i).setActivate(isChecked==true?1:0);
					count = dao.updateRemind(entities.get(i),this);
					if(count <=0 ) {
						Toast.makeText(this, "更改失败", Toast.LENGTH_SHORT).show();
					} else {
						if(entities.get(i).getActivate()==1){
							Intent intent = new Intent(this, AlarmKlaxonService.class); 
			                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entities.get(i).getId(), intent, 0); 
			                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
			                alarmManager.cancel(pendingIntent); 
			                c.setTimeInMillis(System.currentTimeMillis());
			                c.setTimeInMillis(entities.get(i).getTime());
			                c.set(Calendar.SECOND, 0);
			                while(c.getTimeInMillis() <= System.currentTimeMillis() ){
			    				c.add(Calendar.DAY_OF_MONTH, 1);
			    			}
			                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent); 
			                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
						}else{
							  Intent intent = new Intent(this, AlarmKlaxonService.class); 
				              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entities.get(i).getId(), intent, 0); 
				              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
				              alarmManager.cancel(pendingIntent); 
						}
					}
				}
				eatingNotify();
				break;
		case R.id.runi:
			entities.get(5).setActivate(isChecked==true?1:0);
			count = dao.updateRemind(entities.get(5),this);
			if(count <=0) {
				Toast.makeText(this, "更改失败", Toast.LENGTH_SHORT).show();
			} else {
				if(entities.get(5).getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entities.get(5).getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                c.setTimeInMillis(System.currentTimeMillis());
	                c.setTimeInMillis(entities.get(5).getTime());
	                c.set(Calendar.SECOND, 0);
	                while(c.getTimeInMillis() <= System.currentTimeMillis() ){
	    				c.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entities.get(5).getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
			runningNotify();
				break;
		case R.id.drinki:
				for(int i = 6;i<=13;i++){
					entities.get(i).setActivate(isChecked==true?1:0);
					count = dao.updateRemind(entities.get(i),this);
					if(count <=0) {
						Toast.makeText(this, "更改失败", Toast.LENGTH_SHORT).show();
					} else {
						if(entities.get(i).getActivate()==1){
							Intent intent = new Intent(this, AlarmKlaxonService.class); 
			                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entities.get(i).getId(), intent, 0); 
			                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
			                alarmManager.cancel(pendingIntent); 
			                c.setTimeInMillis(System.currentTimeMillis());
			                c.setTimeInMillis(entities.get(i).getTime());
			                c.set(Calendar.SECOND, 0);
			                while(c.getTimeInMillis() <= System.currentTimeMillis() ){
			    				c.add(Calendar.DAY_OF_MONTH, 1);
			    			}
			                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent); 
			                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
						}else{
							  Intent intent = new Intent(this, AlarmKlaxonService.class); 
				              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entities.get(i).getId(), intent, 0); 
				              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
				              alarmManager.cancel(pendingIntent); 
						}
					}
				}
				drinkingNotify();
				break;
		default:
			break;
		}
		}
		touch = false;
	}
	@Override
	protected void onResume() {
		super.onResume();
		entities = dao.getReminds();
		moriningNotify();
		eatingNotify();
		runningNotify();
		drinkingNotify();
	}
}
