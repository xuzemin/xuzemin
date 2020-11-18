
package com.ctv.settings.security.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.format.Time;
import android.util.Log;

import com.ctv.settings.security.activity.USBLockService;
import com.ctv.settings.security.utils.GreneralUtils;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.TvManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class USBStateRec extends BroadcastReceiver {
    private static final String TAG = "ACTION_BOOT_COMPLETED";
    // date format
    public final static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final static String TVOS_COMMON_CMD_SET_RTCTIME = "SetRTCTime"; // SetRTCTime2020030501175901
    private final static String TVOS_COMMON_CMD_GET_RTCTIME = "GetRTCTime";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("zhu...usbstateRec", "action:::" + intent.getAction());
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            Intent intent2 = new Intent(context, USBLockService.class);
            intent2.setAction("com.cultraview.settings.action.usblock");
            context.startService(intent2);
            syncSystemTime(context,getRTCTime());
        } else {
            if (!"1".equals(SystemProperties.get("sys.boot_completed"))) {
                return;
            }
            if ("on".equals(SystemProperties.get("persist.sys.usbLock.status"))) {
                GreneralUtils.getInstance(context).checkFile(true, false);
            } else {
                GreneralUtils.getInstance(context).checkFile(false, false);
            }
            Intent intent2 = new Intent(context, USBLockService.class);
            intent2.putExtra("usblock", "setting_switch");
            context.startService(intent2);
        }
    }
    /**
     * 将指定的时间同步到系统时间
     * @param time
     * @return
     */
    public static boolean syncSystemTime(Context context,long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        if (time / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(time);
            long times =calendar.getTimeInMillis();
            Log.d(TAG, "syncSystemTime time:" + time);
            TvTimerManager.getInstance().setClkTime(time,true);//1041392153579
            TvTimerManager.getInstance().getCurrentTvTime();
            return true;
        }
        return false;
    }

    /**
     * 获得RTC时间
     * @return
     */
    public static long getRTCTime() {
        try {
            short[] rTCTime = TvManager.getInstance().setTvosCommonCommand(TVOS_COMMON_CMD_GET_RTCTIME);
            int year_rt = rTCTime[0];
            if (year_rt < 1970 || year_rt > 2200) {
                return 0;
            }
            int month_rt = rTCTime[1];
            int day_rt = rTCTime[2];
            int week_rt = rTCTime[3];
            int hour_rt = rTCTime[4];
            int minute_rt = rTCTime[5];
            int second_rt = rTCTime[6];

            // "yyyy-MM-dd HH:mm:ss"
            String timeStr = String.format("%s-%s-%s %s:%s:%s",
                    changeTimeFormat(year_rt),
                    changeTimeFormat(month_rt),
                    changeTimeFormat(day_rt),
                    changeTimeFormat(hour_rt),
                    changeTimeFormat(minute_rt),
                    changeTimeFormat(second_rt));
//            StringBuffer sb = new StringBuffer();
//            sb.append(changeTimeFormat(year_rt) + "-");
//            sb.append(changeTimeFormat(month_rt) + "-");
//            sb.append(changeTimeFormat(day_rt));
//            sb.append(changeTimeFormat(hour_rt) + ":");
//            sb.append(changeTimeFormat(minute_rt) + ":");
//            sb.append(changeTimeFormat(second_rt));

            // 对 calendar 设置时间的方法
            // 设置传入的时间格式
            SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_FORMAT);
//            String time = sb.toString();
            // 指定一个日期
            Date date = dateFormat.parse(timeStr);// "2019-06-01 13:24:16"
            // 对 calendar 设置为 date 所定的日期
            Log.d(TAG, "getRTCTime:" + timeStr);
            return date.getTime();
        } catch (Exception e) {
            Log.e(TAG, "getRTCTime: Exception" + e);
            e.printStackTrace();
            return 0;
        }
    }

    public static String changeTimeFormat(int data) {
        if (data > 9) {
            return "" + data;
        } else {
            return "0" + data;
        }
    }
}
