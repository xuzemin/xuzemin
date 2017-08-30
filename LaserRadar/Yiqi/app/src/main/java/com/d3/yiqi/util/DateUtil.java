package com.d3.yiqi.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DateUtil {
	public final static String yyyy = "yyyy";
	public final static String yyyy_MM_dd = "yyyy-MM-dd";
	public final static String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
	public final static String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	public final static String yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss SSS";
	public final static String MM_dd_HH_mm_ss = "MM-dd  HH:mm:ss";

	public static String now(String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		return df.format(Calendar.getInstance().getTime());
	}

	public static String now_yyyy() {
		return now(yyyy);
	}

	public static String now_yyyy_MM_dd() {
		return now(yyyy_MM_dd);
	}

	public static String now_yyyy_MM_dd_HH_mm_ss() {
		return now(yyyy_MM_dd_HH_mm_ss);
	}

	public static String now_yyyy_MM_dd_HH_mm_ss_SSS() {
		return now(yyyy_MM_dd_HH_mm_ss_SSS);
	}
	
	public static String now_MM_dd_HH_mm_ss() {
		return now(MM_dd_HH_mm_ss);
	}

	/**
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String dateToStr(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * 将日期时间型转成字符串 如:" 2002-07-01 11:40:02"
	 * 
	 * @param inDate
	 *            日期时间 " 2002-07-01 11:40:02"
	 * @return String 转换后日期时间字符串
	 */
	public static String dateToStr_yyyy_MM_dd_HH_mm_ss(Date date) {
		return dateToStr(date, yyyy_MM_dd_HH_mm_ss);
	}
	
	/**
	 * 将日期时间型转成字符串 如:" 2002-07-01 11:40:02"
	 * 
	 * @param inDate
	 *            日期时间 " 2002-07-01 11:40:02"
	 * @return String 转换后日期时间字符串
	 */
	public static String dateToStr_MM_dd_HH_mm_ss(Date date) {
		return dateToStr(date, MM_dd_HH_mm_ss);
	}
	

	/**
	 * 将日期型转成字符串 如:"2002-07-01"
	 * 
	 * @param inDate
	 *            日期 "2002-07-01"
	 * @return String 转换后日期字符串
	 */
	public static String dateToStr_yyyy_MM_dd(Date date) {
		return dateToStr(date, yyyy_MM_dd);
	}

	/**
	 * 将字符串型(英文格式)转成日期型 如: "Tue Dec 26 14:45:20 CST 2000"
	 * 
	 * @param DateFormatStr
	 *            字符串 "Tue Dec 26 14:45:20 CST 2000"
	 * @return Date 日期
	 */
	public static Date strToDateEN(String shorDateStr) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"EEE MMM dd hh:mm:ss 'CST' yyyy", java.util.Locale.US);
			return sdf.parse(shorDateStr);
		} catch (Exception e) {
			return new Date();
		}
	}
	
	/**
	 * 将字符串型(中文格式)转成日期型 如:"2002-07-01 22:09:55"
	 * 
	 * @param datestr
	 *            字符串 "2002-07-01 22:09:55"
	 * @return Date 日期
	 */
	public static Date strToDateCN_yyyy_MM_dd_HH_mm_ss(String datestr) {
		Date date = null;
		try {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = fmt.parse(datestr);
		} catch (Exception e) {
			return date;
		}
		return date;
	}
	
	
	public static Date strToDateMM_dd(String datestr) {
		Date date = null;
		try {
			SimpleDateFormat fmt = new SimpleDateFormat(MM_dd_HH_mm_ss);
			date = fmt.parse(datestr);
		} catch (Exception e) {
			return date;
		}
		return date;
	}
	/**
	 * 
	 * @param datestr
	 * @return
	 */
	public static Date strToDateCN_yyyy_MM_dd(String datestr) {
		Date date = null;
		try {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			date = fmt.parse(datestr);
		} catch (Exception e) {
			return date;
		}
		return date;
	}
	/**
	 * 转换util.date-->sql.date
	 * 
	 * @param inDate
	 * @return
	 */
	public static java.sql.Date UtilDateToSqlDate(Date inDate) {
		return new java.sql.Date(getDateTime(inDate));
	}
	private static long getDateTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		cal.set(year, month, day, 0, 0, 0);
		long result = cal.getTimeInMillis();
		result = result / 1000 * 1000;
		return result;
	}

	/**
	 * 遍历刚从数据库里查出来的Map，将里面Timestamp格式化成指定的pattern
	 * 
	 * @param target
	 *            目标map,就是一般是刚从数据库里查出来的
	 * @param pattern
	 *            格式化规则，从自身取
	 */
	@Deprecated
	public static void formatMapDate(Map target, String pattern) {
		for (Object item : target.entrySet()) {
			Map.Entry entry = (Map.Entry) item;
			if (entry.getValue() instanceof Timestamp) {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				entry.setValue(sdf.format((Timestamp) entry.getValue()));
			}
		}
	}

	/**
	 * 日期转化为大小写 chenjiandong 20090609 add
	 * 
	 * @param date
	 * @param type
	 *            1;2两种样式1为简体中文，2为繁体中文
	 * @return
	 */
	public static String dataToUpper(Date date, int type) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH) + 1;
		int day = ca.get(Calendar.DAY_OF_MONTH);
		return numToUpper(year, type) + "年" + monthToUppder(month, type) + "月"
				+ dayToUppder(day, type) + "日";
	}

	/**
	 * 将数字转化为大写
	 * 
	 * @param num
	 * @param type
	 * @return
	 */
	public static String numToUpper(int num, int type) {// type为样式1;2
		String u1[] = { "〇", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
		String u2[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		char[] str = String.valueOf(num).toCharArray();
		String rstr = "";
		if (type == 1) {
			for (int i = 0; i < str.length; i++) {
				rstr = rstr + u1[Integer.parseInt(str[i] + "")];
			}
		} else if (type == 2) {
			for (int i = 0; i < str.length; i++) {
				rstr = rstr + u2[Integer.parseInt(str[i] + "")];
			}
		}
		return rstr;
	}

	/**
	 * 月转化为大写
	 * 
	 * @param month
	 * @param type
	 * @return
	 */
	public static String monthToUppder(int month, int type) {
		if (month < 10) {
			return numToUpper(month, type);
		} else if (month == 10) {
			return "十";
		} else {
			return "十" + numToUpper((month - 10), type);
		}
	}

	/**
	 * 日转化为大写
	 * 
	 * @param day
	 * @param type
	 * @return
	 */
	public static String dayToUppder(int day, int type) {
		if (day < 20) {
			return monthToUppder(day, type);
		} else {
			char[] str = String.valueOf(day).toCharArray();
			if (str[1] == '0') {
				return numToUpper(Integer.parseInt(str[0] + ""), type) + "十";
			} else {
				return numToUpper(Integer.parseInt(str[0] + ""), type) + "十"
						+ numToUpper(Integer.parseInt(str[1] + ""), type);
			}
		}
	}
}
