package net.nmss.nice.utils;

import android.content.Context;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateHelper {
	public static Date addDays(Date paramDate, int paramInt) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		localCalendar.add(5, paramInt);
		return localCalendar.getTime();
	}

	public static Date addMonths(Date paramDate, int paramInt) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		localCalendar.add(2, paramInt);
		return localCalendar.getTime();
	}

	public static int between(Date paramDate) {
		return (int) ((new Date().getTime() - paramDate.getTime()) / 86400000L);
	}

	public static int between(Date paramDate1, Date paramDate2) {
		return (int) ((paramDate1.getTime() - paramDate2.getTime()) / 86400000L);
	}

	public static Date defaultTargetDate() {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(new Date());
		localCalendar.set(2, 1 + localCalendar.get(2));
		localCalendar.set(5, -1 + localCalendar.get(5));
		return localCalendar.getTime();
	}

	public static String format(Date paramDate) {
		return format(paramDate, "yyyy-MM-dd");
	}

	public static String format(Date paramDate, String paramString) {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				paramString, Locale.getDefault());
		String str1;
		try {
			String str2 = localSimpleDateFormat.format(paramDate);
			str1 = str2;
			return str1;
		} catch (Exception localException) {
			str1 = localSimpleDateFormat.format(new Date());
		}
		return null;
	}

	public static String formatString(String paramString1, String paramString2) {
		Date localDate = parseFromString(paramString1, "yyyy-MM-dd");
		return new SimpleDateFormat(paramString2, Locale.getDefault())
				.format(localDate);
	}

	public static String getCurrentDateTime() {
		return format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss");
	}

	public static String getCurrentDateTime2() {
		return format(Calendar.getInstance().getTime(), "yyyy-MM-dd");
	}

	public static String getCurrentTimeMills() {
		return format(Calendar.getInstance().getTime(),
				"yyyy-MM-dd HH:mm:ss.SSS");
	}

	public static int getDay(Date paramDate) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		return localCalendar.get(5);
	}

	public static Date getFirstDay(Date paramDate) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		localCalendar.set(7, localCalendar.getFirstDayOfWeek());
		return localCalendar.getTime();
	}

	public static Date getFirstDay(Date paramDate, int paramInt) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		localCalendar.set(7, paramInt);
		return localCalendar.getTime();
	}

	public static Date getFirstDayOfMonth(Date paramDate) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		localCalendar.set(5, 1);
		return localCalendar.getTime();
	}

	public static Date getLastDayOfMonth(Date paramDate) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		localCalendar.set(5, localCalendar.getActualMaximum(5));
		return localCalendar.getTime();
	}

	public static int getMonth(Date paramDate) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		return 1 + localCalendar.get(2);
	}

	public static String getWeekOfDate(Date paramDate, Context paramContext) {
		String[] arrayOfString = paramContext.getResources().getStringArray(
				2131558406);
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		return arrayOfString[(-1 + localCalendar.get(7))];
	}

	public static int getYear(Date paramDate) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		return localCalendar.get(1);
	}

	public static String getYearMonth(Date paramDate) {
//		StringBuilder localStringBuilder = new StringBuilder(
//				String.valueOf(getYear(paramDate)));
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = Integer.valueOf(getMonth(paramDate));
		return String.format("%02d", arrayOfObject);
	}

	public static String monthDay() {
		return format(new Date(), "MMMdd日");
	}

	public static String monthDay(Date paramDate) {
		return format(paramDate, "MMMdd日");
	}

	public static Date parseFromString(String paramString1, String paramString2) {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				paramString2, Locale.getDefault());
		Date localDate1;
		try {
			Date localDate2 = localSimpleDateFormat.parse(paramString1);
			localDate1 = localDate2;
			return localDate1;
		} catch (ParseException localParseException) {
			localDate1 = null;
		}
		return null;
	}

	public static Date parseString(String paramString) {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd", Locale.getDefault());
		Date localDate1;
		try {
			Date localDate2 = localSimpleDateFormat.parse(paramString);
			localDate1 = localDate2;
			return localDate1;
		} catch (ParseException localParseException) {
			localDate1 = null;
		}
		return null;
	}

	public static String timezoneFormat(String paramString1, String paramString2) {
		Date localDate = parseFromString(paramString1, "yyyy-MM-dd'T'HH:mm:ss");
		return new SimpleDateFormat(paramString2, Locale.getDefault())
				.format(localDate);
	}

//	public static int week(Date paramDate) throws Throwable {
//		GregorianCalendar localGregorianCalendar = new GregorianCalendar();
//		localGregorianCalendar.set(paramDate.getYear(), paramDate.getMonth(),
//				paramDate.getDay());
//		return localGregorianCalendar.get(7);
//	}

	private static SimpleDateFormat sf = null;

	/* 时间戳转换成字符窜 */
	public static String getDateToString(long time) {
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(d);
	}

	public static String getDateToString2(long time) {
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyy-MM");
		return sf.format(d);
	}

	/* 将字符串转为时间戳 */
	public static long getStringToDate(String time) {
		sf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			date = sf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static long getStringToDate2(String time) {
		sf = new SimpleDateFormat("yyyy年MM月");
		Date date = new Date();
		try {
			date = sf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static String getCurrentHour() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		String date = sdf.format(new java.util.Date());
		return date;
	}

	public static String getPostMessageTome(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(time);
		return date;
	}

	public static String getyyyyMMddHHmm(String timeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date(Long.parseLong(timeStr)));
	}
}