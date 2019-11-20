
package com.ctv.welcome.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtil {
    public static String longToDate(long milliseconds) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(milliseconds));
    }

    public static String durationToTime(long duration) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(Long.valueOf(duration));
    }

    public static String longToDetailTime(long milliseconds) {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(milliseconds));
    }
}
