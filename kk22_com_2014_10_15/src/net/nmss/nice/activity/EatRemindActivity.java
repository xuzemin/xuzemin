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

public class EatRemindActivity extends Activity implements OnClickListener,OnWheelScrollListener,OnCheckedChangeListener{
	private ImageView image;
	private TextView st,h,er,at,circle;
	private WheelView breakfast1,breakfast2,lunch1,lunch2,supper1,supper2,addeat1,addeat2;
	private String[] hourItems = new String[24];
	private String[] minuteItems =new String[60];
	private DialogInterface.OnClickListener breakfastOnclik,lunchOnclik,supperOnclik,addeatOnclik;
	private LayoutInflater inflater;
	private ToggleButton breakfastT,lunchT,supperT,addeatT;
	private RemindDao remindDao;
	private Calendar breakfastC,lunchC,supperC,addeatC;
	private RemindEntity breakfasteEntity,luncheEntity,supperEntity,addeatEntity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eat_remind_activity_layout);
		image = (ImageView) findViewById(R.id.image);
		circle = (TextView) findViewById(R.id.circle);
		image.setOnClickListener(this);
		circle.setOnClickListener(this);
		breakfastC = Calendar.getInstance();
		breakfastC.setTimeInMillis(System.currentTimeMillis());
		supperC = Calendar.getInstance();
		supperC.setTimeInMillis(System.currentTimeMillis());
		addeatC = Calendar.getInstance();
		addeatC.setTimeInMillis(System.currentTimeMillis());
		lunchC = Calendar.getInstance();
		lunchC.setTimeInMillis(System.currentTimeMillis());
		inintent();
		init();
		getdata();
	}
	private void getdata(){
		remindDao = new RemindDao(this);
		
		addeatEntity = remindDao.getReminds().get(4);
		addeatC.setTimeInMillis(addeatEntity.getTime());
		addeatT.setChecked(addeatEntity.getActivate()==1?true:false);
		String hour = String.valueOf(addeatC.get(Calendar.HOUR_OF_DAY));
		String minute = String.valueOf(addeatC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		at.setText(hour+":"+minute);
		
		supperEntity = remindDao.getReminds().get(3);
		supperC.setTimeInMillis(supperEntity.getTime());
		supperT.setChecked(supperEntity.getActivate()==1?true:false);
		hour = String.valueOf(supperC.get(Calendar.HOUR_OF_DAY));
		minute = String.valueOf(supperC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		er.setText(hour+":"+minute);
		
		luncheEntity = remindDao.getReminds().get(2);
		lunchC = Calendar.getInstance();
		lunchC.setTimeInMillis(luncheEntity.getTime());
		lunchT.setChecked(luncheEntity.getActivate()==1?true:false);
		hour = String.valueOf(lunchC.get(Calendar.HOUR_OF_DAY));
		minute = String.valueOf(lunchC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		h.setText(hour+":"+minute);
		
		breakfasteEntity = remindDao.getReminds().get(1);
		breakfastC.setTimeInMillis(breakfasteEntity.getTime());
		breakfastT.setChecked(breakfasteEntity.getActivate()==1?true:false);
		hour = String.valueOf(breakfastC.get(Calendar.HOUR_OF_DAY));
		minute = String.valueOf(breakfastC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		st.setText(hour+":"+minute);
	}

	private void inintent() {
		st = (TextView) findViewById(R.id.st);
		st.setOnClickListener(this);
		h = (TextView) findViewById(R.id.h);
		h.setOnClickListener(this);
		er = (TextView) findViewById(R.id.er);
		er.setOnClickListener(this);
		at = (TextView) findViewById(R.id.at);
		at.setOnClickListener(this);
		breakfastT = (ToggleButton) findViewById(R.id.fa);
		breakfastT.setOnCheckedChangeListener(this);
		lunchT=(ToggleButton) findViewById(R.id.nc);
		lunchT.setOnCheckedChangeListener(this);
		supperT = (ToggleButton) findViewById(R.id.pp);
		supperT.setOnCheckedChangeListener(this);
		addeatT = (ToggleButton) findViewById(R.id.de);
		addeatT.setOnCheckedChangeListener(this);
	}

	private void init() {
		breakfastOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(breakfastC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(breakfastC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				st.setText(hour+":"+minute);
				saveRemind(breakfasteEntity,breakfastC);
				breakfastT.setChecked(true);
			}
		};
		lunchOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(lunchC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(lunchC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				h.setText(hour+":"+minute);
				saveRemind(luncheEntity,lunchC);
				lunchT.setChecked(true);
			}
		};
		supperOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(supperC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(supperC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				er.setText(hour+":"+minute);
				saveRemind(supperEntity,supperC);
				supperT.setChecked(true);
			}
		};
		addeatOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(addeatC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(addeatC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				at.setText(hour+":"+minute);
				saveRemind(addeatEntity,addeatC);
				addeatT.setChecked(true);
			}
		};
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
		case R.id.st:
			 inflater = getLayoutInflater();
		  View layout = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  breakfast1 = (WheelView) layout.findViewById(R.id.wheelview1);
		  breakfast2 = (WheelView) layout.findViewById(R.id.wheelview2);
		  breakfast1.addScrollingListener(this);
		  breakfast2.addScrollingListener(this);
		  breakfast1.setCyclic(true);
		  breakfast2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			breakfast1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			breakfast1.setLabel("  时");
			breakfast2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			breakfast2.setLabel("  分");
			breakfast1.setCurrentItem(breakfastC.get(Calendar.HOUR_OF_DAY));
			breakfast2.setCurrentItem(breakfastC.get(Calendar.MINUTE));
			new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout)
		     .setPositiveButton("确定", breakfastOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		case R.id.h:
			inflater = getLayoutInflater();
		  View layout1 = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  lunch1 = (WheelView) layout1.findViewById(R.id.wheelview1);
		  lunch2 = (WheelView) layout1.findViewById(R.id.wheelview2);
		  lunch1.addScrollingListener(this);
		  lunch2.addScrollingListener(this);
		  lunch1.setCyclic(true);
		  lunch2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			lunch1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			lunch1.setLabel("  时");
			lunch2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			lunch2.setLabel("  分");
			lunch1.setCurrentItem(lunchC.get(Calendar.HOUR_OF_DAY));
			lunch2.setCurrentItem(lunchC.get(Calendar.MINUTE));
		  new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout1)
		     .setPositiveButton("确定", lunchOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		case R.id.er:
			 inflater = getLayoutInflater();
		  View layout2 = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  supper1 = (WheelView) layout2.findViewById(R.id.wheelview1);
		  supper2 = (WheelView) layout2.findViewById(R.id.wheelview2);
		  supper1.addScrollingListener(this);
		  supper2.addScrollingListener(this);
		  supper1.setCyclic(true);
		  supper2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			supper1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			supper1.setLabel("  时");
			supper2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			supper2.setLabel("  分");
			supper1.setCurrentItem(supperC.get(Calendar.HOUR_OF_DAY));
			supper2.setCurrentItem(supperC.get(Calendar.MINUTE));
			new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout2)
		     .setPositiveButton("确定", supperOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		case R.id.at:
		inflater = getLayoutInflater();
		  View layout3 = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  addeat1 = (WheelView) layout3.findViewById(R.id.wheelview1);
		  addeat2 = (WheelView) layout3.findViewById(R.id.wheelview2);
		  addeat1.addScrollingListener(this);
		  addeat2.addScrollingListener(this);
		  addeat1.setCyclic(true);
		  addeat2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			addeat1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			addeat1.setLabel("  时");
			addeat2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			addeat2.setLabel("  分");
			addeat1.setCurrentItem(addeatC.get(Calendar.HOUR_OF_DAY));
			addeat2.setCurrentItem(addeatC.get(Calendar.MINUTE));
			new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout3)
		     .setPositiveButton("确定", addeatOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		default:
			break;
		}
	}
	@Override
	public void onScrollingStarted(WheelView wheel) {
	}
	@Override
	public void onScrollingFinished(WheelView wheel) {
		// TODO Auto-generated method stub
		if(wheel.equals(breakfast1)){
			breakfastC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(breakfast2)){
			breakfastC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(lunch1)){
			lunchC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(lunch2)){
			lunchC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(supper1)){
			supperC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(supper2)){
			supperC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(addeat1)){
			addeatC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(addeat2)){
			addeatC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}
	}
	private Integer getWheelViewCurrentItemString(WheelView wheelView) {
		int index = wheelView.getCurrentItem();
		return index;
	}
	protected void saveRemind(RemindEntity entity ,Calendar c) {
		entity.setTime(c.getTimeInMillis());
		int count = remindDao.updateRemind(entity,this);
		if(count <=0 ) {
			Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
		}
		if(entity.getActivate()==1){
			Intent intent = new Intent(this, AlarmKlaxonService.class); 
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, entity.getId(), intent, 0); 
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
            alarmManager.cancel(pendingIntent); 
            c.setTimeInMillis(entity.getTime());
            c.set(Calendar.SECOND, 0);
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
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		int count;
		switch (buttonView.getId()) {
		case R.id.fa:
			breakfasteEntity.setActivate(isChecked==true?1:0);
			count =remindDao.updateRemind(breakfasteEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(breakfasteEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, breakfasteEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                breakfastC.setTimeInMillis(breakfasteEntity.getTime());
	                breakfastC.set(Calendar.SECOND, 0);
	                while(breakfastC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	breakfastC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, breakfastC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, breakfasteEntity.getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
		break;
		case R.id.nc:
			luncheEntity.setActivate(isChecked==true?1:0);
			count = remindDao.updateRemind(luncheEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(luncheEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, luncheEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                lunchC.setTimeInMillis(luncheEntity.getTime());
	                lunchC.set(Calendar.SECOND, 0);
	                while(lunchC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	lunchC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, lunchC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, luncheEntity.getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
		break;
		case R.id.pp:
			supperEntity.setActivate(isChecked==true?1:0);
			count =remindDao.updateRemind(supperEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(supperEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, supperEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                supperC.setTimeInMillis(supperEntity.getTime());
	                supperC.set(Calendar.SECOND, 0);
	                while(supperC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	supperC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, supperC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, supperEntity.getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
		break;
		case R.id.de:
			addeatEntity.setActivate(isChecked==true?1:0);
			count = remindDao.updateRemind(addeatEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(addeatEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, addeatEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                addeatC.setTimeInMillis(addeatEntity.getTime());
	                addeatC.set(Calendar.SECOND, 0);
	                while(addeatC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	addeatC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, addeatC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, addeatEntity.getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
		break;
		default:
			break;
		}
	}
}
