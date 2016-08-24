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

public class DrinkRemindActivity extends Activity implements OnClickListener,OnWheelScrollListener,OnCheckedChangeListener{
	private ImageView image;
	private TextView firstText,secondText,thirdText,fourText,fiveText,sixText,sevenText,eightText,circle;
	private WheelView first1,first2,second1,second2,third1,third2,four1,four2,five1,five2,six1,six2,seven1,seven2,eight1,eight2;
	private String[] hourItems = new String[24];
	private String[] minuteItems =new String[60];
	private DialogInterface.OnClickListener firstOnclik,secondOnclik,thirdOnclik,fourOnclik,fiveOnclik,sixOnclik,sevenOnclik,eightOnclik;
	private LayoutInflater inflater;
	private ToggleButton firstT,secondT,thirdT,fourT,fiveT,sixT,sevenT,eightT;
	private RemindDao remindDao;
	private Calendar firstC,secondC,thirdC,fourC,fiveC,sixC,sevenC,eightC;
	private RemindEntity firstEntity,secondEntity,thirdEntity,fourEntity,fiveEntity,sixEntity,sevenEntity,eightEntity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drink_remind_activity_layout);
		image = (ImageView) findViewById(R.id.image);
		image.setOnClickListener(this);
		circle = (TextView) findViewById(R.id.circle);
		circle.setOnClickListener(this);
		inintent();
		init();
		getdata();
	}
	private void getdata() {
		remindDao = new RemindDao(this);
		firstEntity = remindDao.getReminds().get(6);
		firstC = Calendar.getInstance();
		firstC.setTimeInMillis(firstEntity.getTime());
		firstT.setChecked(firstEntity.getActivate()==1?true:false);
		String hour = String.valueOf(firstC.get(Calendar.HOUR_OF_DAY));
		String minute = String.valueOf(firstC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		firstText.setText(hour+":"+minute);
		
		secondEntity = remindDao.getReminds().get(7);
		secondC = Calendar.getInstance();
		secondC.setTimeInMillis(secondEntity.getTime());
		secondT.setChecked(secondEntity.getActivate()==1?true:false);
		hour = String.valueOf(secondC.get(Calendar.HOUR_OF_DAY));
		minute = String.valueOf(secondC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		secondText.setText(hour+":"+minute);
		
		thirdEntity = remindDao.getReminds().get(8);
		thirdC = Calendar.getInstance();
		thirdC.setTimeInMillis(thirdEntity.getTime());
		thirdT.setChecked(thirdEntity.getActivate()==1?true:false);
		hour = String.valueOf(thirdC.get(Calendar.HOUR_OF_DAY));
		minute = String.valueOf(thirdC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		thirdText.setText(hour+":"+minute);
		
		fourEntity = remindDao.getReminds().get(9);
		fourC = Calendar.getInstance();
		fourC.setTimeInMillis(fourEntity.getTime());
		fourT.setChecked(fourEntity.getActivate()==1?true:false);
		hour = String.valueOf(fourC.get(Calendar.HOUR_OF_DAY));
		minute = String.valueOf(fourC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		fourText.setText(hour+":"+minute);
		
		fiveEntity = remindDao.getReminds().get(10);
		fiveC = Calendar.getInstance();
		fiveC.setTimeInMillis(fiveEntity.getTime());
		fiveT.setChecked(fiveEntity.getActivate()==1?true:false);
		hour = String.valueOf(fiveC.get(Calendar.HOUR_OF_DAY));
		minute = String.valueOf(fiveC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		fiveText.setText(hour+":"+minute);
		
		sixEntity = remindDao.getReminds().get(11);
		sixC = Calendar.getInstance();
		sixC.setTimeInMillis(sixEntity.getTime());
		sixT.setChecked(sixEntity.getActivate()==1?true:false);
		hour = String.valueOf(sixC.get(Calendar.HOUR_OF_DAY));
		minute = String.valueOf(sixC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		sixText.setText(hour+":"+minute);
		
		sevenEntity = remindDao.getReminds().get(12);
		sevenC = Calendar.getInstance();
		sevenC.setTimeInMillis(sevenEntity.getTime());
		sevenT.setChecked(sevenEntity.getActivate()==1?true:false);
		hour = String.valueOf(sevenC.get(Calendar.HOUR_OF_DAY));
		minute = String.valueOf(sevenC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		sevenText.setText(hour+":"+minute);
		
		eightEntity = remindDao.getReminds().get(13);
		eightC = Calendar.getInstance();
		eightC.setTimeInMillis(eightEntity.getTime());
		eightT.setChecked(eightEntity.getActivate()==1?true:false);
		hour = String.valueOf(eightC.get(Calendar.HOUR_OF_DAY));
		minute = String.valueOf(eightC.get(Calendar.MINUTE));
		if(hour.length() <= 1) {hour = "0"+hour;}
		if(minute.length() <= 1) {minute = "0"+minute;}
		eightText.setText(hour+":"+minute);
		
	}
	private void init() {
		firstOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(firstC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(firstC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				firstText.setText(hour+":"+minute);
				saveRemind(firstEntity,firstC);
				firstT.setChecked(true);
			}
		};
		secondOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(secondC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(secondC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				secondText.setText(hour+":"+minute);
				saveRemind(secondEntity,secondC);
				secondT.setChecked(true);
			}
		};
		thirdOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(thirdC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(thirdC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				thirdText.setText(hour+":"+minute);
				saveRemind(thirdEntity,thirdC);
				thirdT.setChecked(true);
			}
		};
		fourOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(fourC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(fourC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				fourText.setText(hour+":"+minute);
				fourT.setChecked(true);
			}
		};
		fiveOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(fiveC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(fiveC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				fiveText.setText(hour+":"+minute);
				saveRemind(fiveEntity,fiveC);
				fiveT.setChecked(true);
			}
		};
		sixOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(sixC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(sixC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				sixText.setText(hour+":"+minute);
				saveRemind(sixEntity,sixC);
				sixT.setChecked(true);
			}
		};
		sevenOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(sevenC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(sevenC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				sevenText.setText(hour+":"+minute);
				saveRemind(sevenEntity,sevenC);
				sevenT.setChecked(true);
			}
		};
		eightOnclik =new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hour = String.valueOf(eightC.get(Calendar.HOUR_OF_DAY));
				String minute = String.valueOf(eightC.get(Calendar.MINUTE));
				if(hour.length() <= 1) {
					hour = "0"+hour;
				}
				
				if(minute.length() <= 1) {
					minute = "0"+minute;
				}
				eightText.setText(hour+":"+minute);
				saveRemind(eightEntity,eightC);
				eightT.setChecked(true);
			}
		};
		
	}
	private void inintent() {
		firstText = (TextView) findViewById(R.id.firstText);
		firstText.setOnClickListener(this);
		secondText = (TextView) findViewById(R.id.secondText);
		secondText.setOnClickListener(this);
		thirdText = (TextView) findViewById(R.id.ThirdText);
		thirdText.setOnClickListener(this);
		fourText = (TextView) findViewById(R.id.fourText);
		fourText.setOnClickListener(this);
		fiveText = (TextView) findViewById(R.id.fiveText);
		fiveText.setOnClickListener(this);
		sixText = (TextView) findViewById(R.id.sixText);
		sixText.setOnClickListener(this);
		sevenText = (TextView) findViewById(R.id.sevenText);
		sevenText.setOnClickListener(this);
		eightText = (TextView) findViewById(R.id.eightText);
		eightText.setOnClickListener(this);
		firstT = (ToggleButton) findViewById(R.id.firstT);
		secondT = (ToggleButton) findViewById(R.id.secondT);
		thirdT = (ToggleButton) findViewById(R.id.thirdT);
		fourT = (ToggleButton) findViewById(R.id.fourT);
		fiveT = (ToggleButton) findViewById(R.id.fiveT);
		sixT = (ToggleButton) findViewById(R.id.sixT);
		sevenT = (ToggleButton) findViewById(R.id.sevenT);
		eightT = (ToggleButton) findViewById(R.id.eightT);
		firstT.setOnCheckedChangeListener(this);
		secondT.setOnCheckedChangeListener(this);
		thirdT.setOnCheckedChangeListener(this);
		fourT.setOnCheckedChangeListener(this);
		fiveT.setOnCheckedChangeListener(this);
		sixT.setOnCheckedChangeListener(this);
		sevenT.setOnCheckedChangeListener(this);
		eightT.setOnCheckedChangeListener(this);
	}
	@Override
	public void onScrollingStarted(WheelView wheel) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onScrollingFinished(WheelView wheel) {
		// TODO Auto-generated method stub
		if(wheel.equals(first1)){
			firstC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(first2)){
			firstC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(second1)){
			secondC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(second2)){
			secondC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(third1)){
			thirdC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(third2)){
			thirdC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(four1)){
			fourC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(four2)){
			fourC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(five1)){
			fiveC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(five2)){
			fiveC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(six1)){
			sixC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(six2)){
			sixC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(seven1)){
			sevenC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(seven2)){
			sevenC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(eight1)){
			eightC.set(Calendar.HOUR_OF_DAY,getWheelViewCurrentItemString(wheel));
		}else if(wheel.equals(eight2)){
			eightC.set(Calendar.MINUTE,getWheelViewCurrentItemString(wheel));
		}
		
	}
	private Integer getWheelViewCurrentItemString(WheelView wheelView) {
		int index = wheelView.getCurrentItem();
		return index;
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
		case R.id.firstText:
			 inflater = getLayoutInflater();
		  View layout = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  first1 = (WheelView) layout.findViewById(R.id.wheelview1);
		  first2 = (WheelView) layout.findViewById(R.id.wheelview2);
		  first1.addScrollingListener(this);
		  first2.addScrollingListener(this);
		  first1.setCyclic(true);
		  first2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			first1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			first1.setLabel("  时");
			first2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			first2.setLabel("  分");
			first1.setCurrentItem(firstC.get(Calendar.HOUR_OF_DAY));
			first2.setCurrentItem(firstC.get(Calendar.MINUTE));
			new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout)
		     .setPositiveButton("确定", firstOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		case R.id.secondText:
			 inflater = getLayoutInflater();
		  View layout1 = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  second1 = (WheelView) layout1.findViewById(R.id.wheelview1);
		  second2 = (WheelView) layout1.findViewById(R.id.wheelview2);
		  second1.addScrollingListener(this);
		  second2.addScrollingListener(this);
		  second1.setCyclic(true);
		  second2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			second1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			second1.setLabel("  时");
			second2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			second2.setLabel("  分");
			second1.setCurrentItem(secondC.get(Calendar.HOUR_OF_DAY));
			second2.setCurrentItem(secondC.get(Calendar.MINUTE));
			new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout1)
		     .setPositiveButton("确定", secondOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		case R.id.ThirdText:
			 inflater = getLayoutInflater();
		  View layout2 = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  third1 = (WheelView) layout2.findViewById(R.id.wheelview1);
		  third2 = (WheelView) layout2.findViewById(R.id.wheelview2);
		  third1.addScrollingListener(this);
		  third2.addScrollingListener(this);
		  third1.setCyclic(true);
		  third2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			third1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			third1.setLabel("  时");
			third2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			third2.setLabel("  分");
			third1.setCurrentItem(thirdC.get(Calendar.HOUR_OF_DAY));
			third2.setCurrentItem(thirdC.get(Calendar.MINUTE));
			new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout2)
		     .setPositiveButton("确定", thirdOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		case R.id.fourText:
			 inflater = getLayoutInflater();
		  View layout3 = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  four1 = (WheelView) layout3.findViewById(R.id.wheelview1);
		  four2 = (WheelView) layout3.findViewById(R.id.wheelview2);
		  four1.addScrollingListener(this);
		  four2.addScrollingListener(this);
		  four1.setCyclic(true);
		  four2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			four1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			four1.setLabel("  时");
			four2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			four2.setLabel("  分");
			four1.setCurrentItem(fourC.get(Calendar.HOUR_OF_DAY));
			four2.setCurrentItem(fourC.get(Calendar.MINUTE));
			new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout3)
		     .setPositiveButton("确定", fourOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		case R.id.fiveText:
			 inflater = getLayoutInflater();
		  View layout4 = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  five1 = (WheelView) layout4.findViewById(R.id.wheelview1);
		  five2 = (WheelView) layout4.findViewById(R.id.wheelview2);
		  five1.addScrollingListener(this);
		  five2.addScrollingListener(this);
		  five1.setCyclic(true);
		  five2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			five1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			five1.setLabel("  时");
			five2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			five2.setLabel("  分");
			five1.setCurrentItem(fiveC.get(Calendar.HOUR_OF_DAY));
			five2.setCurrentItem(fiveC.get(Calendar.MINUTE));
			new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout4)
		     .setPositiveButton("确定", fiveOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		case R.id.sixText:
			 inflater = getLayoutInflater();
		  View layout5 = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  six1 = (WheelView) layout5.findViewById(R.id.wheelview1);
		  six2 = (WheelView) layout5.findViewById(R.id.wheelview2);
		  six1.addScrollingListener(this);
		  six2.addScrollingListener(this);
		  six1.setCyclic(true);
		  six2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			six1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			six1.setLabel("  时");
			six2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			six2.setLabel("  分");
			six1.setCurrentItem(sixC.get(Calendar.HOUR_OF_DAY));
			six2.setCurrentItem(sixC.get(Calendar.MINUTE));
			new AlertDialog.Builder(this).setTitle("").setView(layout5)
		     .setPositiveButton("确定", sixOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		case R.id.sevenText:
			 inflater = getLayoutInflater();
		  View layout6 = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  seven1 = (WheelView) layout6.findViewById(R.id.wheelview1);
		  seven2 = (WheelView) layout6.findViewById(R.id.wheelview2);
		  seven1.addScrollingListener(this);
		  seven2.addScrollingListener(this);
		  seven1.setCyclic(true);
		  seven2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			seven1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			seven1.setLabel("  时");
			seven2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			seven2.setLabel("  分");
			seven1.setCurrentItem(sevenC.get(Calendar.HOUR_OF_DAY));
			seven2.setCurrentItem(sevenC.get(Calendar.MINUTE));
			new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout6)
		     .setPositiveButton("确定", sevenOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		case R.id.eightText:
			 inflater = getLayoutInflater();
		  View layout7 = inflater.inflate(R.layout.time_layout,
		     (ViewGroup) findViewById(R.id.dialog));
		  eight1 = (WheelView) layout7.findViewById(R.id.wheelview1);
		  eight2 = (WheelView) layout7.findViewById(R.id.wheelview2);
		  eight1.addScrollingListener(this);
		  eight2.addScrollingListener(this);
		  eight1.setCyclic(true);
		  eight2.setCyclic(true);
		  	for (int i = 0; i < hourItems.length; i++) {
				hourItems[i] = " " + i+" ";
			}
			for (int i = 0; i < minuteItems.length; i++) {
				minuteItems[i] = " " + i+" ";
			}
			eight1.setAdapter(new ArrayWheelAdapter<String>(hourItems));
			eight1.setLabel("  时");
			eight2.setAdapter(new ArrayWheelAdapter<String>(minuteItems));
			eight2.setLabel("  分");
			eight1.setCurrentItem(eightC.get(Calendar.HOUR_OF_DAY));
			eight2.setCurrentItem(eightC.get(Calendar.MINUTE));
			new AlertDialog.Builder(this).setTitle("自定义布局").setView(layout7)
		     .setPositiveButton("确定", eightOnclik)
		     .setNegativeButton("取消", null).show();
			break;
		default:
			break;
		}
		
	}
	protected void saveRemind(RemindEntity entity ,Calendar c) {
		entity.setTime(c.getTimeInMillis());
		int count = remindDao.updateRemind(entity,this);
		if(count<=0){
			Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
		}else{
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
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		int count;
		switch (buttonView.getId()) {
		case R.id.firstT:
			firstEntity.setActivate(isChecked==true?1:0);
			count =remindDao.updateRemind(firstEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(firstEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, firstEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                firstC.setTimeInMillis(firstEntity.getTime());
	                firstC.set(Calendar.SECOND, 0);
	                while(firstC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	firstC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, firstC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, firstEntity.getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
			break;
		case R.id.secondT:
			secondEntity.setActivate(isChecked==true?1:0);
			count =remindDao.updateRemind(secondEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(secondEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, secondEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                secondC.setTimeInMillis(secondEntity.getTime());
	                secondC.set(Calendar.SECOND, 0);
	                while(secondC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	secondC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, secondC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, secondEntity.getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
			break;
		case R.id.thirdT:
			thirdEntity.setActivate(isChecked==true?1:0);
			count = remindDao.updateRemind(thirdEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(thirdEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, thirdEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                thirdC.setTimeInMillis(thirdEntity.getTime());
	                thirdC.set(Calendar.SECOND, 0);
	                while(thirdC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	thirdC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, thirdC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, thirdEntity.getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
			break;
		case R.id.fourT:
			fourEntity.setActivate(isChecked==true?1:0);
			count =remindDao.updateRemind(fourEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(fourEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, fourEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                fourC.setTimeInMillis(fourEntity.getTime());
	                fourC.set(Calendar.SECOND, 0);
	                while(fourC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	fourC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, fourC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, fourEntity.getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
			break;
		case R.id.fiveT:
			fiveEntity.setActivate(isChecked==true?1:0);
			count = remindDao.updateRemind(fiveEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(fiveEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, fiveEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                fiveC.setTimeInMillis(fiveEntity.getTime());
	                fiveC.set(Calendar.SECOND, 0);
	                while(fiveC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	fiveC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, fiveC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, fiveEntity.getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
			break;
		case R.id.sixT:
			sixEntity.setActivate(isChecked==true?1:0);
			count =remindDao.updateRemind(sixEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(sixEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, sixEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                sixC.setTimeInMillis(sixEntity.getTime());
	                sixC.set(Calendar.SECOND, 0);
	                while(sixC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	sixC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, sixC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, sixEntity.getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
			break;
		case R.id.sevenT:
			sevenEntity.setActivate(isChecked==true?1:0);
			count = remindDao.updateRemind(sevenEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(sevenEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, sevenEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                sevenC.setTimeInMillis(sevenEntity.getTime());
	                sevenC.set(Calendar.SECOND, 0);
	                while(sevenC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	sevenC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, sevenC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, sevenEntity.getId(), intent, 0); 
		              AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
		              alarmManager.cancel(pendingIntent); 
				}
			}
			break;
		case R.id.eightT:
			eightEntity.setActivate(isChecked==true?1:0);
			count = remindDao.updateRemind(eightEntity,this);
			if(count<=0){
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}else{
				if(eightEntity.getActivate()==1){
					Intent intent = new Intent(this, AlarmKlaxonService.class); 
	                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, eightEntity.getId(), intent, 0); 
	                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	                alarmManager.cancel(pendingIntent); 
	                eightC.setTimeInMillis(secondEntity.getTime());
	                eightC.set(Calendar.SECOND, 0);
	                while(eightC.getTimeInMillis() <= System.currentTimeMillis() ){
	                	eightC.add(Calendar.DAY_OF_MONTH, 1);
	    			}
	                alarmManager.set(AlarmManager.RTC_WAKEUP, eightC.getTimeInMillis(), pendingIntent); 
	                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000),  (24 * 60 * 60 * 1000), pendingIntent); 
				}else{
					  Intent intent = new Intent(this, AlarmKlaxonService.class); 
		              PendingIntent pendingIntent = PendingIntent.getBroadcast(this, eightEntity.getId(), intent, 0); 
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
