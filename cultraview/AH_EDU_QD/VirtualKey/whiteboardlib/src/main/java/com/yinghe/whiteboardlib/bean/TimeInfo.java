package com.yinghe.whiteboardlib.bean;

/**
 * 时间信息
 */
public class TimeInfo {
	private String week;
	private String day;
	private String time;

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "TimeInfo{" +
				"week='" + week + '\'' +
				", day='" + day + '\'' +
				", time='" + time + '\'' +
				'}';
	}
}
