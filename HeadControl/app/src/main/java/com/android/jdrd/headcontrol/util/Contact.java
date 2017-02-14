package com.android.jdrd.headcontrol.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

import com.android.jdrd.headcontrol.dialog.CustomDialog;

/**
 * Created by Administrator on 2017/2/8.
 */

public class Contact {
    public static boolean isDebug = true;
    public static String TAG = "HeadControl";


    public static void debugLog(String string){
        if(isDebug){
            Log.e(TAG,string);
        }
    }
    public static void showWarntext(Context context, final Handler handler){
        CustomDialog dialog = new CustomDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.builder.setTitle("提醒")
                .setMessage("是否离开并删除路线规划")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Contact.debugLog("确定");
                        handler.sendEmptyMessage(1);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                handler.sendEmptyMessage(2);
                Contact.debugLog("取消");
            }
        }).create().show();
    }
}
