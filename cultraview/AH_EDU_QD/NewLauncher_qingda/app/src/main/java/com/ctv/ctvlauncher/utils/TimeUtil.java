package com.ctv.ctvlauncher.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ctv.ctvlauncher.R;
import com.ctv.ctvlauncher.reservice.NetworkStateReceiver;
import com.ctv.ctvlauncher.service.Theme1TimeService;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class TimeUtil {
    public static final String[] DATE_FORMAT_STRINGS = {
            "MM-dd-yyyy", "dd-MM-yyyy", "yyyy-MM-dd"
    };

    public final static String TAG ="TimeUtil";
    //   public static String[] weeks = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    // sleep_time_string = getResources().getStringArray(R.array.str_arr_sleep_mode_vals);
    public static void  getTime() throws Exception {
        URL url = null;//取得资源对象
        url = new URL("http://www.baidu.com");
        URLConnection uc = url.openConnection();//生成连接对象
        uc.connect(); //发出连接
        uc.setConnectTimeout(4000);
        long ld = uc.getDate(); //取得网站日期时间
        // initTime(true,ld);

    }

    public static int getDateFormat() {
        return Integer.parseInt(SystemProperties.get("persist.sys.dateformat", "2"));
    }

    public static void initTime(Context context, boolean isNetworkTime, long time) throws TvCommonException {
        Calendar calendar = Calendar.getInstance();
/*        if(isNetworkTime){
            calendar.setTimeInMillis(time);
            SystemClock.setCurrentTimeMillis(time);
        }*/
        int year =calendar.get(Calendar.YEAR);
        int month =calendar.get(Calendar.MONTH)+1;
        int day =calendar.get(Calendar.DATE);
        int hour =calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
        int week_year = calendar.get(Calendar.WEEK_OF_YEAR);
        String[] weeks = context.getResources().getStringArray(R.array.str_arr_week);
        String week_str = weeks[week];
        BroadcastUtil.sendBroadcaset(context,year,month,day,hour,minute,week_str);
        StringBuffer sb = new StringBuffer();
        sb.append("SetRTCTime");
        sb.append(changeTimeFormat(year));
        sb.append(changeTimeFormat(month));
        sb.append(changeTimeFormat(day));
        sb.append(week_year);
        sb.append(changeTimeFormat(hour));
        sb.append(changeTimeFormat(minute));
        sb.append(changeTimeFormat(second));
        Log.d(TAG,"settingtime:"+"isnewWork:"+isNetworkTime+","+"settingstr:"+sb.toString());
        TvManager.getInstance().setTvosCommonCommand(sb.toString());
    }

    public static String changeTimeFormat(int data) {
        if(data>9){
            return ""+data;
        }else{
            return "0"+data;
        }
    }
    public static boolean getTimeSettingAuto(Context context) {
        //Settings.System.AUTO_TIME was deprecated in API level 17.
        //Use Settings.Global.AUTO_TIME instead.
        int state = Settings.System.getInt(context.getContentResolver(), Settings.System.AUTO_TIME, 1);
        return (state == 1) ? true : false;
    }

    public static void getRTCTime(Context context) throws TvCommonException {
        short[] rTCTime = TvManager.getInstance().setTvosCommonCommand("GetRTCTime");
        if (rTCTime.length<7||rTCTime.length==0||rTCTime[0] == 0||rTCTime[4]>24||rTCTime[5]>60) {
         //   initTime(context,false, 0);
        } else {
            int year_rt = rTCTime[0];
            int month_rt = rTCTime[1];
            int day_rt = rTCTime[2];
            int week_rt = rTCTime[3];
            int hour_rt = rTCTime[4];
            int minute_rt = rTCTime[5];
            int second_rt = rTCTime[6];
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            int week_year = calendar.get(Calendar.WEEK_OF_YEAR);
            String[] weeks = context.getResources().getStringArray(R.array.str_arr_week);
            if (year_rt == 0) {
                Intent time = new Intent();
                time.setAction(Theme1TimeService.ACTION_TYPE_TIME);
                time.putExtra("year", year);
                time.putExtra("month", month);
                time.putExtra("day", day);
                time.putExtra("hour", hour);
                time.putExtra("minute", minute);
                Log.d("TimeUtil", "week:" + weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ",NetworkStateReceiver.isNewWorkAvailable:" + NetworkStateReceiver.isNewWorkAvailable);
                time.putExtra("week", weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
                time.putExtra("network", NetWorkUtil.checkNetWorkState(context));
                LocalBroadcastManager.getInstance(context).sendBroadcast(time);
                TimeUtil.initTime(context,false, 0);
            } else {
                if(!compareDateSame(year,month,day,hour,minute)){
                    setSysTime(year_rt,month_rt,day_rt,hour_rt,minute_rt,second_rt,context);
                }
                // compareDateSame(year,month,day,hour,minute)
                String week_str = CalculateWeekDay(context,year_rt,month_rt,day_rt);
                Log.d("TimeUtil", "week_rt:" + week_rt + ",NetworkStateReceiver.isNewWorkAvailable:" + NetworkStateReceiver.isNewWorkAvailable);
                //String week = weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1];
                Log.d("TimeUtil", "week:" + week + ",NetworkStateReceiver.isNewWorkAvailable:" + NetworkStateReceiver.isNewWorkAvailable);
                Intent time = new Intent();
                time.setAction(Theme1TimeService.ACTION_TYPE_TIME);
                time.putExtra("year", year_rt);
                time.putExtra("month", month_rt);
                time.putExtra("day", day_rt);
                time.putExtra("hour", hour_rt);
                time.putExtra("minute", minute_rt);
                time.putExtra("week", week_str);
                time.putExtra("network", NetWorkUtil.checkNetWorkState(context));
                LocalBroadcastManager.getInstance(context).sendBroadcast(time);
            }


            Log.d("TimeUtil", rTCTime.toString());
        }
    }


    public static boolean compareDateSame(int year, int month, int day, int hour, int minute) {

        try {
            short[] rTCTime = TvManager.getInstance().setTvosCommonCommand("GetRTCTime");
            if(rTCTime.length<7){
                return false;
            }
            int year_rt = rTCTime[0];
            int month_rt = rTCTime[1];
            int day_rt = rTCTime[2];
            int week_rt = rTCTime[3];
            int hour_rt = rTCTime[4];
            int minute_rt = rTCTime[5];
            int second_rt = rTCTime[6];
            Log.d("TimeUtil","年："+year_rt+","+",月："+month_rt+",日："+day_rt);
            if (year != year_rt || month != month_rt || day != day_rt || hour_rt != hour || Math.abs(minute - minute_rt) > 2) {
                return false;
            }
            return true;
        } catch (TvCommonException e) {
            e.printStackTrace();
            return true;
        }

    }
    public static void setSysTime(int year,int month,int day,int hour,int minute,int second,Context context){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month-1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, 0);
        long when = c.getTimeInMillis();
        if(when / 1000 < Integer.MAX_VALUE){
            ((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    private static String CalculateWeekDay(Context context,int y, int m, int d) {
        if(m==1||m==2) {
            m+=12;
            y--;
        }
        String[] weeks = context.getResources().getStringArray(R.array.str_arr_week);
        String week = null;
        int iWeek=(d+2*m+3*(m+1)/5+y+y/4-y/100+y/400)%7;
        switch(iWeek)
        {
            case 0: week = weeks[1]; break;
            case 1: week = weeks[2]; break;
            case 2: week = weeks[3]; break;
            case 3: week = weeks[4]; break;
            case 4: week = weeks[5]; break;
            case 5: week = weeks[6]; break;
            case 6: week = weeks[0]; break;
        }
        return week;
    }

    public static void initTimeData(Context context, TextView time_tv, TextView date_tv, TextView week_tv){

        try {
            Log.d("qkmin---time","initTimeData");
            String[] weeks = context.getResources().getStringArray(R.array.str_arr_week);
            if(NetworkStateReceiver.isNewWorkAvailable){
                Log.d("qkmin---time","initTimeData  Calendar ");
                Calendar calendar = Calendar.getInstance();
                // calendar.setTimeInMillis(ld);
                //  SystemClock.setCurrentTimeMillis(ld);
                int year =calendar.get(Calendar.YEAR);
                int month =calendar.get(Calendar.MONTH)+1;
                int day =calendar.get(Calendar.DATE);
                int hour =calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
                String week_str = weeks[week];
                hour = TimeUtil.checkHourStatus(context,hour);
                time_tv.setText(changeTimeFormat(hour)+":"+changeTimeFormat(minute));
                date_tv.setText(changeTimeFormat(year)+"/"+changeTimeFormat(month)+"/"+changeTimeFormat(day));
                week_tv.setText(week_str);
                initTime(context,false,0);
            }else{

                short[]  rTCTime = TvManager.getInstance().setTvosCommonCommand("GetRTCTime");
                Log.d("qkmin---time","initTimeData  GetRTCTime "+rTCTime);
                if(rTCTime.length<7||rTCTime.length==0||rTCTime[0] == 0||rTCTime[4]>24||rTCTime[5]>60){
                    Log.d("qkmin---time","initTimeData  GetRTCTime if "+rTCTime);
                    Calendar calendar = Calendar.getInstance();
                    int year =calendar.get(Calendar.YEAR);
                    int month =calendar.get(Calendar.MONTH)+1;
                    int day =calendar.get(Calendar.DATE);
                    int hour =calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
                    String week_str = weeks[week];
                    hour = TimeUtil.checkHourStatus(context,hour);
                    time_tv.setText(changeTimeFormat(hour)+":"+changeTimeFormat(minute));
                    date_tv.setText(changeTimeFormat(year)+"/"+changeTimeFormat(month)+"/"+changeTimeFormat(day));
                    week_tv.setText(week_str);
                    //     initTime(false,0);
                }else{

                    Log.d("qkmin---time","initTimeData  GetRTCTime else"+rTCTime);
                    int year_rt = rTCTime[0];
                    int month_rt = rTCTime[1];
                    int day_rt = rTCTime[2];
                    int week_rt = rTCTime[3];
                    int hour_rt = rTCTime[4];
                    int minute_rt = rTCTime[5];
                    int second_rt = rTCTime[6];
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year_rt,month_rt,day_rt);
                    String week = weeks[calendar.get(Calendar.DAY_OF_WEEK)-1];
                    hour_rt = TimeUtil.checkHourStatus(context,hour_rt);
                    time_tv.setText(changeTimeFormat(hour_rt)+":"+changeTimeFormat(minute_rt));
                    date_tv.setText(changeTimeFormat(year_rt)+"/"+changeTimeFormat(month_rt)+"/"+changeTimeFormat(day_rt));
                    week_tv.setText(week);
                }

            }

        } catch (TvCommonException e) {
            e.printStackTrace();
        }

    }

    public static int checkHourStatus(Context context,int hour){
        if(!DateFormat.is24HourFormat(context)){
            if(hour>12&&hour<=23){
                hour = hour -12;
            }
        }
        return hour;
    }


    public static int checkTimeAmPm(Context context, int hour) {
        int state = -1;
        if (hour > 12 && hour <= 23) {
            state = 1;
        }else{
            state = 0;
        }
        return state;
    }
    public static float getTmpValue() {
        float tmpValue = 0f;
        try {
            short[] shorts = TvManager.getInstance().setTvosCommonCommand("GetTmpValue");
            if (shorts != null && shorts.length >= 2) {
                tmpValue = shorts[0] + ((shorts[1] + 1f) / 100f);
            }
            Log.d("temp_value", shorts[0]+"." + shorts[1]);
            Log.d("temp_value", tmpValue+"");
            return tmpValue;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
