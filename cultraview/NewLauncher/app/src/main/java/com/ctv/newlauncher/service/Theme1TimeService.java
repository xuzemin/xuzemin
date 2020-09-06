package com.ctv.newlauncher.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.ctv.ctvlauncher.R;
import com.ctv.newlauncher.reservice.NetworkStateReceiver;
import com.ctv.newlauncher.utils.BroadcastUtil;
import com.ctv.newlauncher.utils.TimeUtil;

import java.util.Calendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Theme1TimeService extends IntentService {
    public final static String ACTION_TYPE_TIME = "action.type.time";
    private static final String ACTION_FOO = "com.ctv.newlauncher.service.action.FOO";
    private static final String ACTION_BAZ = "com.ctv.newlauncher.service.action.BAZ";
    private NetworkStateReceiver mNetworkStateReceiver = new NetworkStateReceiver();


    private static final String EXTRA_PARAM1 = "com.ctv.newlauncher.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.ctv.newlauncher.service.extra.PARAM2";

    public Theme1TimeService() {
        super("Theme1TimeService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("hhc", "onCreate: cheng");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(mNetworkStateReceiver, filter);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            while(true){
                Log.d("TimeUtil", "NetworkStateReceiver.isNewWorkAvailable:" + NetworkStateReceiver.isNewWorkAvailable);
                if (NetworkStateReceiver.isNewWorkAvailable) {
                    Log.d("TimeUtil", "NetworkStateReceiver.isNewWorkAvailable:" + NetworkStateReceiver.isNewWorkAvailable);
                    if(TimeUtil.getTimeSettingAuto(this)){
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DATE);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        int second = calendar.get(Calendar.SECOND);
                        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                        int week_year = calendar.get(Calendar.WEEK_OF_YEAR);
                        String week_str = getResources().getStringArray(R.array.str_arr_week)[calendar.get(Calendar.DAY_OF_WEEK) - 1];
//                        BroadcastUtil.sendBroadcaset(this,year,month,day,hour,minute,week_str);
                        BroadcastUtil.sendnetbroad(this);
/*                            Intent time = new Intent();
                            time.setAction(ACTION_TYPE_TIME);
                            time.putExtra("year", year);
                            time.putExtra("month", month);
                            time.putExtra("day", day);
                            time.putExtra("hour", hour);
                            time.putExtra("minute", minute);
                            time.putExtra("network", NetWorkUtil.checkNetWorkState(this));
                            Log.d("TimeUtil", "week:" + weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ",NetworkStateReceiver.isNewWorkAvailable:" + NetworkStateReceiver.isNewWorkAvailable);
                            time.putExtra("week", week_str);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(time);*/
                   //     if (!TimeUtil.compareDateSame(year, month, day, hour, minute)) {
                     //      TimeUtil.initTime(this,true, 0);
                    //    }
                    }else{
                        Log.d("TimeUtil", "NetworkStateReceiver.isNewWorkAvailable:" + TimeUtil.getTimeSettingAuto(this));
                        BroadcastUtil.sendnetbroad(this);
                     //   TimeUtil.getRTCTime(this);
                    }

                } else {
                    Log.d("TimeUtil", "getTimeSettingAuto:" + NetworkStateReceiver.isNewWorkAvailable);
                    BroadcastUtil.sendnetbroad(this);

                    //  TimeUtil.getRTCTime(this);
                }
                Thread.sleep(60*1000);

            }
        } catch (Exception e) {
            Log.d("TimeUtil",e.toString());
            e.printStackTrace();
        }

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
