package com.hht.android.sdk.service.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Time;

import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.time.util.TimeUtil;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.vo.EnumTimerPeriod;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2020/3/6 11:35
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/3/6 11:35
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class TimeTools {
    private final static String TAG = TimeTools.class.getSimpleName();
    public final static String ACTION_TYPE_TIME = "action.type.time";

    // date format
    public final static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 判断是否指定同步网络时间
     * @param context
     * @return
     */
    public static boolean getTimeSettingAuto(Context context) {
        // Settings.System.AUTO_TIME was deprecated in API level 17.
        // Use Settings.Global.AUTO_TIME instead.
        int state = Settings.System.getInt(context.getContentResolver(), Settings.System.AUTO_TIME,
                1);
        return (state == 1);
    }

    public static String changeTimeFormat(int data) {
        if (data > 9) {
            return "" + data;
        } else {
            return "0" + data;
        }
    }

    /**
     * 设置网络世界到系统中
     */
    private static long getNetTime() {
        URL url = null;// 取得资源对象
        try {
            url = new URL("https://www.baidu.com/");
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect(); // 发出连接
            uc.setConnectTimeout(3000);
            long ld = uc.getDate(); // 取得网站日期时间
            return ld;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 系统初始化时，开始同步系统时间和RTC时间
     * @return
     */
    public static boolean syncNetTime(final Context context){
        long netTime = getNetTime();
        if (netTime <= 0){
            return false;
        }

        // get system time
        Calendar calendar = Calendar.getInstance();
        long systemTime = calendar.getTimeInMillis();

        // sync system time and rtc time
        boolean isNetTimeFront = ((netTime - systemTime) >= 1000 * 5); // net时间和系统时间相差5s
        if (isNetTimeFront) {
            syncSystemTime(netTime);
            setRTCTime(netTime);

            // send broadcast: update time
            context.sendBroadcast(new Intent(ACTION_TYPE_TIME));
        }
        return true;
    }

    /**
     * 系统初始化时，开始同步系统时间和RTC时间
     * @return
     */
    public static boolean syncRTCAndSystemTime(final Context context){
        // get system time
//        Calendar calendar = Calendar.getInstance();
//        long systemTime = calendar.getTimeInMillis();
        long systemTime = System.currentTimeMillis();

        // get rtc time
        long rtcTime = getRTCTime();

        // sync rtc time or system time
        boolean isRTCTimeFront = ((rtcTime - systemTime) >= 1000 * 60 * 2); // rtc时间和系统时间相差2min
        if (isRTCTimeFront){ // sync system time
            syncSystemTime(rtcTime);

            // send broadcast: update time
            context.sendBroadcast(new Intent(ACTION_TYPE_TIME));
        } else { // sync rtc time
            setRTCTime(systemTime);
        }
        return true;
    }

    /**
     * 判断RTC时间是否超前系统时间
     *
     * @return true rtcTime >= systemTime; false rtcTime < systemTime
     */
    public static boolean isRTCTimeFront() {
        // get system time
        long systemTime = System.currentTimeMillis();

        // get rtc time
        long rtcTime = getRTCTime();

        return ((rtcTime - systemTime) >= 1000 * 60 * 2); // rtc时间和系统时间相差2min
    }

    /**
     * 将指定的时间同步到系统时间
     * @param millis
     * @return
     */
    public static boolean syncSystemTime(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        if (millis / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(millis);

            L.d(TAG, "syncSystemTime time:" + calendar.getTime());
            return true;
        }
        return false;
    }

    /**
     * 获得RTC时间
     * @return
     */
    public static long getRTCTime() {
        int[] rTCTime = TvCommonManager.getInstance().setTvosCommonCommand(TvosCommand.TVOS_COMMON_CMD_GET_RTCTIME);
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

        // "yyyy-MM-dd HH:mm:ss"   // "%s-%s-%s %s:%s:%s"
        String format = "%04d-%02d-%02d %02d:%02d:%02d";
        String timeStr = String.format(Locale.getDefault(),
                format,
                year_rt,
                month_rt,
                day_rt,
                hour_rt,
                minute_rt,
                second_rt);
        try {
            // 设置传入的时间格式
            // "yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault());
//            String time = sb.toString();
            // 指定一个日期
            Date date = dateFormat.parse(timeStr);// "2019-06-01 13:24:16"
            // 对 calendar 设置为 date 所定的日期
            L.d(TAG, "getRTCTime:" + timeStr);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 设置RTC时间
     * @param millis
     */
    public static void setRTCTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int week_month = calendar.get(Calendar.WEEK_OF_MONTH);
//        int week_year = calendar.get(Calendar.WEEK_OF_YEAR);

        StringBuffer sb = new StringBuffer();
        sb.append(TvosCommand.TVOS_COMMON_CMD_SET_RTCTIME);
        sb.append(changeTimeFormat(year));
        sb.append(changeTimeFormat(month));
        sb.append(changeTimeFormat(day));
        sb.append(changeTimeFormat(week_month));
        sb.append(changeTimeFormat(hour));
        sb.append(changeTimeFormat(minute));
        sb.append(changeTimeFormat(second));

        TvCommonManager.getInstance().setTvosCommonCommand(sb.toString());
        L.d(TAG, "setting rtc time:" + sb.toString());
    }


    /**
     * 取得当前时间戳（精确到秒）
     *
     * @return nowTimeStamp
     */
    public static String getNowTimeStamp() {
        long time = System.currentTimeMillis();
        String nowTimeStamp = String.valueOf(time / 1000);
        return nowTimeStamp;
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param dateStr 字符串日期
     * @param format   如：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String Date2TimeStamp(String dateStr, String format) {
        if (TextUtils.isEmpty(format)){
            format = "yyyy-MM-dd HH:mm:ss";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(dateStr).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Java将Unix时间戳转换成指定格式日期字符串
     * @param timestampString 时间戳 如："1473048265";
     * @param formats 要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
     *
     * @return 返回结果 如："2016-09-05 16:06:42";
     */
    public static String TimeStamp2Date(String timestampString, String formats) {
        if (TextUtils.isEmpty(formats)){
            formats = "yyyy-MM-dd HH:mm:ss";
        }
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return date;
    }

    /**
     * 获得休眠时间模式
     * @return
     */
    public static int getSleepTimeMode() {
        int pos = 0;
        switch (TvTimerManager.getInstance().getSleepTimeMode()) {
            case TvTimerManager.SLEEP_TIME_OFF:
                pos = 0;
                break;
            case TvTimerManager.SLEEP_TIME_10MIN:
                pos = 1;
                break;
            case TvTimerManager.SLEEP_TIME_20MIN:
                pos = 2;
                break;
            case TvTimerManager.SLEEP_TIME_30MIN:
                pos = 3;
                break;
            case TvTimerManager.SLEEP_TIME_60MIN:
                pos = 4;
                break;
            case TvTimerManager.SLEEP_TIME_90MIN:
                pos = 5;
                break;
            case TvTimerManager.SLEEP_TIME_120MIN:
                pos = 6;
                break;
            case TvTimerManager.SLEEP_TIME_180MIN:
                pos = 7;
                break;
            case TvTimerManager.SLEEP_TIME_240MIN:
                pos = 8;
                break;

            default:
                break;
        }
        return pos;
    }

    /**
     * 设置休眠时间模式
     *
     * @param context
     * @param pos
     */
    public static void setSleepTimeMode(Context context, int pos) {
        int sleepTimeMode = TvTimerManager.SLEEP_TIME_OFF;
        switch (pos) {
            case 0:
                sleepTimeMode = TvTimerManager.SLEEP_TIME_OFF;
                break;
            case 1:
                sleepTimeMode = TvTimerManager.SLEEP_TIME_10MIN;
                break;
            case 2:
                sleepTimeMode = TvTimerManager.SLEEP_TIME_20MIN;
                break;
            case 3:
                sleepTimeMode = TvTimerManager.SLEEP_TIME_30MIN;
                break;
            case 4:
                sleepTimeMode = TvTimerManager.SLEEP_TIME_60MIN;
                break;
            case 5:
                sleepTimeMode = TvTimerManager.SLEEP_TIME_90MIN;
                break;
            case 6:
                sleepTimeMode = TvTimerManager.SLEEP_TIME_120MIN;
                break;
            case 7:
                sleepTimeMode = TvTimerManager.SLEEP_TIME_180MIN;
                break;
            case 8:
                sleepTimeMode = TvTimerManager.SLEEP_TIME_240MIN;
                break;

            default:
                break;
        }

        String sleepAction = "com.mstar.android.intent.action.SLEEP_BUTTON";

        TvTimerManager.getInstance().setSleepTimeMode(sleepTimeMode);
        Intent intent = new Intent(sleepAction);
        intent.putExtra("ShowUI", "NOUI");
        context.sendBroadcast(intent);
    }

    // 定时开机状态
    private static boolean isOnTimerEnable = false;
    private static Runnable mOnTimerEnable = new Runnable() {
        @Override
        public void run() {
            L.d(TAG, "mOnTimerEnable setOnTimerEnable :" + isOnTimerEnable);
            SystemClock.sleep(500);
            TvTimerManager.getInstance().setOnTimerEnable(isOnTimerEnable);
        }
    };

    // 定时关机状态
    private static boolean isOffTimerEnable = false;
    private static Runnable mOffTimerEnable = new Runnable() {
        @Override
        public void run() {
            SystemClock.sleep(500);
            L.d(TAG, "mOffTimerEnable setOffTimerEnable isOffTimerEnable:" + isOffTimerEnable);
            TvTimerManager.getInstance().setOffTimerEnable(isOffTimerEnable);
        }
    };

    /**
     * 设置定时开机状态
     * @param enable
     */
    public static boolean setScheduleTimeBootEnable(final boolean enable){
        isOnTimerEnable = enable;
        new Thread(mOnTimerEnable).start();
        return isOnTimerEnable;
    }

    /**
     * 设置定时关机状态
     * @param enable
     */
    public static boolean setScheduleTimeShutdownEnable(final boolean enable){
        isOffTimerEnable = enable;
        new Thread(mOffTimerEnable).start();
        return isOffTimerEnable;
    }

    /**
     * 设置定时开机时间
     * @param timer
     */
    public static boolean setTvOnTimer(TimeUtil timer, Handler handler){
        L.d(TAG, "setTvOnTimer start");
        if (timer == null || handler == null){
            L.d(TAG, "setTvOnTimer end, timer == null || handler == null");
            return false;
        }

        Time currentTvTime = TvTimerManager.getInstance().getCurrentTvTime();
        currentTvTime.hour = timer.hour;
        currentTvTime.minute = timer.min;
        currentTvTime.second = 0;

        boolean flag = TvTimerManager.getInstance().setTvOnTimer(currentTvTime);

        // save weekList
        int weekInts = weekList2Ints(timer.week);
        SystemProperties.set(HHTConstant.SYS_ON_TIME_PERIOD_WEEK, "" + weekInts);

        // update EnumTimerPeriod and set OnTimerEnable true
        final EnumTimerPeriod enumTimerPeriod = ints2EnumTimerPeriod(weekInts);
        if (enumTimerPeriod != EnumTimerPeriod.EN_Timer_Off){// update mode
            SystemProperties.set(HHTConstant.SYS_ON_TIME_PERIOD_MODE, enumTimerPeriod.ordinal() + "");
            setScheduleTimeBootEnable(true);
        }

        L.d(TAG, "setTvOnTimer, TimeUtil:" + timer);
        return flag;
    }

    /**
     * 获得定时关机时间
     */
    public static TimeUtil getTvOnTimer(){
        L.d(TAG, "getTvOnTimer start");
        TimeUtil timeUtil = new TimeUtil();
        Time currentTvTime = TvTimerManager.getInstance().getTvOnTimer();
        if (currentTvTime != null){
            timeUtil.hour = currentTvTime.hour;
            timeUtil.min = currentTvTime.minute;
            timeUtil.enable = TvTimerManager.getInstance().isOnTimerEnable();

            // get week
            int weekInts = SystemProperties.getInt(HHTConstant.SYS_ON_TIME_PERIOD_WEEK, -1);
            timeUtil.week = weekInts2List(weekInts);
        }

        L.d(TAG, "getTvOnTimer timeUtil:" + timeUtil);
        return timeUtil;
    }

    /**
     * 设置定时关机时间
     * @param timer
     */
    public static boolean setTvOffTimer(TimeUtil timer, Handler handler){
        L.d(TAG, "setTvOffTimer start" );
        if (timer == null || handler == null){
            L.d(TAG, "setTvOffTimer end ,timer == null || handler == null" );
            return false;
        }

        Time currentTvTime = TvTimerManager.getInstance().getCurrentTvTime();
        currentTvTime.hour = timer.hour;
        currentTvTime.minute = timer.min;
        currentTvTime.second = 0;
        boolean flag = TvTimerManager.getInstance().setTvOffTimer(currentTvTime);

        // save weekList
        L.d(TAG, "setTvOffTimer, weekList2Ints: start:"+ timer.week);
        int weekInts = weekList2Ints(timer.week);
        SystemProperties.set(HHTConstant.SYS_OFF_TIME_PERIOD_WEEK, "" + weekInts);

        // update EnumTimerPeriod and set OffTimerEnable true
        final EnumTimerPeriod enumTimerPeriod = ints2EnumTimerPeriod(weekInts);
        if (enumTimerPeriod != EnumTimerPeriod.EN_Timer_Off){// update mode
            timer.enable = true;
            SystemProperties.set(HHTConstant.SYS_OFF_TIME_PERIOD_MODE, enumTimerPeriod.ordinal() + "");
            setScheduleTimeShutdownEnable(true);
        }

        L.d(TAG, "setTvOffTimer, TimeUtil:" + timer);
        return flag;
    }

    /**
     * 获得定时关机时间
     */
    public static TimeUtil getTvOffTimer(){
        L.d(TAG, "getTvOffTimer start" );
        TimeUtil timeUtil = new TimeUtil();
        Time currentTvTime = TvTimerManager.getInstance().getTvOffTimer();
        if (currentTvTime != null){
            timeUtil.hour = currentTvTime.hour;
            timeUtil.min = currentTvTime.minute;
            timeUtil.enable = TvTimerManager.getInstance().isOffTimerEnable();

            int weekInts = SystemProperties.getInt(HHTConstant.SYS_OFF_TIME_PERIOD_WEEK, -1);
            timeUtil.week = weekInts2List(weekInts);
        }

        L.d(TAG, "getTvOffTimer timeUtil:" + timeUtil);
        return timeUtil;
    }

    /**
     * weekInts to week list
     *
     * @param daysOfWeek
     * @return
     */
    public static List<String> weekInts2List(int daysOfWeek) {
        List<String> weekList = new ArrayList<>();
        if (daysOfWeek <= 0){
            return weekList;
        }

        TimeUtil.EnumWeek[] enumWeeks = TimeUtil.EnumWeek.values();
        L.d(TAG, "weekInts2List weekInts is:%s", Integer.toBinaryString(daysOfWeek));

        // from SUN to SAT
        int dayWeek = 0x01;
        for (int i = 0; i < HHTConstant.DAYS_PER_WEEK; i++ ){
            boolean isWeekExit = (daysOfWeek & dayWeek) != 0;

            if (isWeekExit){
                int index = (7 - i) % 7;
                L.d(TAG, "weekInts2List weekStr is:" + enumWeeks[index].toString());
                weekList.add(enumWeeks[index].toString());
            }

            dayWeek = dayWeek << 1;// Mov to next day
        }

        L.d(TAG, "weekInts2List weekList size:" + weekList.size());
        return weekList;
    }

    /**
     * weekList to Ints
     *
     * @param weekList
     * @return
     */
    public static int weekList2Ints(List<String> weekList) {
        int daysOfWeek = 0x00;
        if (weekList == null || weekList.size() == 0){
            return daysOfWeek;
        }
        TimeUtil.EnumWeek[] enumWeeks = TimeUtil.EnumWeek.values();

        // SUN to SAT
        int dayWeek = 0x01;
        for (int i = 0; i < HHTConstant.DAYS_PER_WEEK; i++ ){
            int index = (7 - i) % 7;
            String weekStr = enumWeeks[index].toString();
            if (weekList.contains(weekStr)){
                L.d(TAG, "weekList2Ints weekStr is:" + weekStr);
                daysOfWeek |= dayWeek;
            }

            dayWeek = dayWeek << 1;// Mov to next day
        }
        L.d(TAG, "weekList2Ints daysOfWeek is:%s", Integer.toBinaryString(daysOfWeek));
        return daysOfWeek;
    }

    /**
     * daysOfWeek to EnumTimerPeriod
     *
     * @param daysOfWeek
     * @return
     */
    public static EnumTimerPeriod ints2EnumTimerPeriod(int daysOfWeek) {
        // EN_Timer_Off
        //boolean onTimerEnable = TvTimerManager.getInstance().isOnTimerEnable();
        //L.d(TAG, "onTimerEnable:"+onTimerEnable);
        // if (!onTimerEnable){
        //     return EnumTimerPeriod.EN_Timer_Off;
        // }

        // EN_Timer_Once
        if (daysOfWeek <= 0 || daysOfWeek > HHTConstant.REPEAT_DAILY){
            return EnumTimerPeriod.EN_Timer_Once;
        }

        // EN_Timer_User_Defined
        EnumTimerPeriod timerPeriod = EnumTimerPeriod.EN_Timer_Everyday;
        // switch (daysOfWeek){
        //     case HHTConstant.REPEAT_SUN:{ // EN_Timer_Sun
        //         timerPeriod = EnumTimerPeriod.EN_Timer_Sun;
        //         break;
        //     }
        //     case HHTConstant.REPEAT_SAT_SUN:{ // EN_Timer_Sat2Sun
        //         timerPeriod = EnumTimerPeriod.EN_Timer_Sat2Sun;
        //         break;
        //     }
        //     case HHTConstant.REPEAT_MON_FRI:{ // EN_Timer_Mon2Fri
        //         timerPeriod = EnumTimerPeriod.EN_Timer_Mon2Fri;
        //         break;
        //     }
        //     case HHTConstant.REPEAT_MON_SAT:{ // EN_Timer_Mon2Sat
        //         timerPeriod = EnumTimerPeriod.EN_Timer_Mon2Sat;
        //         break;
        //     }
        //     case HHTConstant.REPEAT_DAILY:{ // EN_Timer_Everyday
        //         timerPeriod = EnumTimerPeriod.EN_Timer_Everyday;
        //         break;
        //     }
        //     default:{ // EN_Timer_Num
        //         timerPeriod = EnumTimerPeriod.EN_Timer_Num;//EnumTimerPeriod.EN_Timer_User_Defined;
        //     }
        // }

        L.d(TAG, "ints2EnumTimerPeriod EnumTimerPeriod:%s,  weekInts is:%s", timerPeriod.ordinal(),
                Integer.toBinaryString(daysOfWeek));

        return timerPeriod;
    }
}
