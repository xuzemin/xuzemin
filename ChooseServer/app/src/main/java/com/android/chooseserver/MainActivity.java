package com.android.chooseserver;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.android.chooseserver.service.ServerSocketUtil;
import com.android.chooseserver.utils.Constant;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 启动后台通讯服务
        Intent serverSocket = new Intent(this, ServerSocketUtil.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serverSocket);
        }else{
            startService(serverSocket);
        }
    }

    // 注册广播
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String StringE = intent.getStringExtra("msg");
            // 打印log
            Constant.debugLog("msg----->" + StringE);
            if (StringE != null && !StringE.equals("")) {
                // 解析命令
                parseJson(StringE);
            }
        }
    }

    // 解析命令
    public void parseJson(String string) {
        if (string.equals("robot_connect") || string.equals("robot_unconnect")) {
            // 获取机器人数据
//            getRobotData();
//            // 刷新
//            SJXGridViewAdapter.notifyDataSetInvalidated();
            Constant.debugLog("=====连接成功=====");
        } else if (string.equals("robot_receive_succus")) {
            Constant.debugLog("=====收到指令成功=====");
//            synchronized (ZB_RobotDialog.thread) {
//                if (!SJXRobotDialog.IsCoordinate) {
//                    if (ZB_RobotDialog.CurrentIndex == -1) {
//                        ZB_RobotDialog.CurrentIndex = 0;
//                    }
//                    ZB_RobotDialog.thread.notify();
//                } else {
//                    ZB_RobotDialog.thread.notify();
//                }
//            }
        } else if (string.equals("robot_receive_fail")) {
            Constant.debugLog("=====收到指令失败=====");
//            if (!SJXRobotDialog.IsCoordinate) {
//                if (ZB_RobotDialog.flag) {
//                    ZB_RobotDialog.sendCommandList(isList);
//                } else {
//                    ZB_RobotDialog.sendCommand();
//                }
//            } else {
                //                SJX_RobotDialog.sendCommandCoordinate();
//            }
        } else if (string.equals("robot_destory")) {
//            Constant.debugLog("=====销毁机器人=====");
//            robotDBHelper.execSQL("update robot set outline= '0' ");
        } else {
        }
    }
}
