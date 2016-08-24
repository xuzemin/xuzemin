package net.nmss.nice.bean;

import java.io.Serializable;
import java.util.Arrays;

import android.R.integer;
import android.net.Uri;

public class AlarmModel implements Serializable{

	private static final long serialVersionUID = -7241565064865775550L;
	public static final int SUNDAY = 0;
	public static final int MONDAY = 1;
	public static final int TUESDAY = 2;
	public static final int WEDNESDAY = 3;
	public static final int THURSDAY = 4;
	public static final int FRDIAY = 5;
	public static final int SATURDAY = 6;
	
	public long id = -1;
	public int timeHour;
	public int timeMinute;
	private boolean repeatingDays[];
	public boolean repeatWeekly;
	private int interval;
	public Uri alarmTone;
	public String name;
	public boolean isEnabled;
	public long createTime;
	public AlarmModel() {
		repeatingDays = new boolean[7];
		Arrays.fill(repeatingDays, false);
		repeatWeekly = true;
	}
	
	public void setRepeatingDay(int dayOfWeek, boolean value) {
		repeatingDays[dayOfWeek] = value;
	}
	
	public boolean getRepeatingDay(int dayOfWeek) {
		return repeatingDays[dayOfWeek];
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getTimeHour() {
		return timeHour;
	}

	public void setTimeHour(int timeHour) {
		this.timeHour = timeHour;
	}

	public int getTimeMinute() {
		return timeMinute;
	}

	public void setTimeMinute(int timeMinute) {
		this.timeMinute = timeMinute;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public boolean isRepeatWeekly() {
		return repeatWeekly;
	}

	public void setRepeatWeekly(boolean repeatWeekly) {
		this.repeatWeekly = repeatWeekly;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
}
