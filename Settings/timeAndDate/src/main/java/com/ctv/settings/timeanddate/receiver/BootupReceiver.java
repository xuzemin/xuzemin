package com.ctv.settings.timeanddate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import com.mstar.android.tv.TvTimerManager;

/*
 *  监听开机广播广播 获取定时开关及状态
 *
 * */

public class BootupReceiver extends BroadcastReceiver {

    private Handler mHandler =new Handler(){
        @Override
        public String getMessageName(Message message) {
            return super.getMessageName(message);
        }
    };
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("gyx","onReceive");

        //获取定时关机时间 重复性
        int shutdown_repeat = Settings.System.getInt(context.getContentResolver(), "shutdown_repeat", 0);
        int shutdown_hour = Settings.System.getInt(context.getContentResolver(), "shutdown_hour", 0);
        int shutdown_minute = Settings.System.getInt(context.getContentResolver(), "shutdown_minute", 0);
        int auto_shutdown = Settings.System.getInt(context.getContentResolver(), "auto_shutdown", 0);
        boolean isOffTimerEnable = TvTimerManager.getInstance().isOffTimerEnable();
        if(auto_shutdown==1&&shutdown_repeat==1){
            Time mShutdownTime = TvTimerManager.getInstance().getCurrentTvTime();
            mShutdownTime.minute=shutdown_minute;
            mShutdownTime.hour=shutdown_hour;
            TvTimerManager.getInstance().setTvOffTimer(mShutdownTime);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TvTimerManager.getInstance().setOffTimerEnable(true);
                    Log.i("gyx","onReceive ---setOffTimerEnable true");
                }
            },500);
        }else{
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TvTimerManager.getInstance().setOffTimerEnable(false);
                    Log.i("gyx","onReceive ---setOffTimerEnable false");
                }
            },500);

        }

        //获取定时开机时间 重复性

        int bootup_repeat = Settings.System.getInt(context.getContentResolver(), "bootup_repeat", 0);
        int bootup_hour = Settings.System.getInt(context.getContentResolver(), "bootup_hour", 0);
        int bootup_minute = Settings.System.getInt(context.getContentResolver(), "bootup_minute", 0);
        int auto_bootup = Settings.System.getInt(context.getContentResolver(), "auto_bootup", 0);

        if(auto_bootup==1&&bootup_repeat==1){
            Time mBootupTime= TvTimerManager.getInstance().getCurrentTvTime();
            mBootupTime.minute=bootup_minute;
            mBootupTime.hour=bootup_hour;
            TvTimerManager.getInstance().setTvOnTimer(mBootupTime);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TvTimerManager.getInstance().setOnTimerEnable(true);
                    Log.i("gyx","onReceive ---setOnTimerEnable true");
                }
            },500);
        }else{
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TvTimerManager.getInstance().setOnTimerEnable(false);
                    Log.i("gyx","onReceive ---setOnTimerEnable false");
                }
            },500);

        }
    }
}
