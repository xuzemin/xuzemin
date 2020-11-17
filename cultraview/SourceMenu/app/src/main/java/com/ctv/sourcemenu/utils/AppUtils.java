package com.ctv.sourcemenu.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppUtils {

    /**
     * 切换信号通道
     *
     * @param inputSource
     * @param context
     */
    public static void changeSignal(Context context, int inputSource){
        new Thread(()->{
            Intent intent = new Intent(MstarConst.ACTION_START_TV_PLAYER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.putExtra(MstarConst.KEY_START_TV_PLAYER_TASK_TAG, MstarConst.VALUE_START_TV_PLAYER_TASK_TAG);
            intent.putExtra(MstarConst.KEY_START_TV_PLAYER_INPUT_SRC, inputSource);
            Log.i("CommonCommandsourceInde","changeSignal->" +inputSource);
            try {
                context.startActivity(intent);
            } catch (Exception e){
                e.printStackTrace();
                BaseUtils.dbg("CommonCommand","changeSignal error->" + e.getMessage());
            }
        }).start();
    }

}
