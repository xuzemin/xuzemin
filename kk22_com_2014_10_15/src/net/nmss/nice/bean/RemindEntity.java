package net.nmss.nice.bean;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;

public class RemindEntity {
	private String name = null;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private int id;
	private long time;
	private int activate;
	public RemindEntity() {
		// TODO Auto-generated constructor stub
		activate = 0;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getActivate() {
		return activate;
	}
	public void setActivate(int activate) {
		this.activate = activate;
	}
	public ContentValues parseToDBEntity() {
		ContentValues values = new ContentValues();
		values.put("remind_time", time);
		values.put("status", activate);		
		return values;
	}
	
	public void setName() {
		// TODO Auto-generated method stub
		
	}
}
