package com.yinghe.whiteboardlib.utils;

import android.content.Context;

import com.yinghe.whiteboardlib.R;
import com.yinghe.whiteboardlib.bean.TimeInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间处理工具
 * Created by Nereo on 2015/4/8.
 */
public class TimeUtils {
    public final static String DATE_PATTERN_DEFAULT = "yyyy-MM-dd";
    public final static String DATE_PATTERN_FILE_NAME = "yyyy-MM-dd_HHmmss";
    public final static String DATE_PATTERN_MMDD = "MMdd";

//    public final static String DATE_PATTERN_WEEK = "HH:mm";
    public final static String DATE_PATTERN_DAY = "yyyy/MM/dd";
    public final static String DATE_PATTERN_TIME = "HH:mm";

    public final static TimeInfo timeInfo = new TimeInfo();

    public static String timeFormat(long timeMillis, String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(new Date(timeMillis));
    }

    public static String timeFormat(Date date, String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(date);
    }

    public static String formatPhotoDate(long time){
        return timeFormat(time, DATE_PATTERN_DEFAULT);
    }

    public static String formatPhotoDate(String path){
        File file = new File(path);
        if(file.exists()){
            long time = file.lastModified();
            return formatPhotoDate(time);
        }
        return "1970-01-01";
    }

    /**
     * 获得时间字符串
     * @return
     */
    public static String getNowTime(String pattern) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        return sDateFormat.format(new Date());
    }

    /**
     * 获得时间字符串
     * @return
     */
    public static String getNowTimeString() {
        return getNowTime(DATE_PATTERN_FILE_NAME);
    }

    /**
     * 获得时间信息
     * @return
     */
    public static TimeInfo getTimeInfo(Context context) {
        Calendar c = Calendar.getInstance();
        Date now = Calendar.getInstance().getTime();
        String dayStr = timeFormat(now,DATE_PATTERN_DAY);
        String timeStr = timeFormat(now,DATE_PATTERN_TIME);
        int mWeek = c.get(Calendar.DAY_OF_WEEK);

        String[] weeks = context.getResources().getStringArray(R.array.week_info);

        timeInfo.setDay(dayStr);
        timeInfo.setTime(timeStr);
        if (mWeek > 0 && mWeek <= 7  && weeks.length == 7){
            timeInfo.setWeek(weeks[mWeek - 1]);
        }
        return timeInfo;
    }
}
