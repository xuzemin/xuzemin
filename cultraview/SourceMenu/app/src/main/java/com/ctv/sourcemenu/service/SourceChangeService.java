package com.ctv.sourcemenu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ctv.sourcemenu.utils.AppUtils;
import com.ctv.sourcemenu.utils.BaseUtils;
import com.ctv.sourcemenu.utils.ScreenUtils;

import com.hht.android.sdk.ops.HHTOpsManager;
import com.hht.android.sdk.source.HHTSourceManager;
import com.mstar.android.tv.TvCommonManager;

public class SourceChangeService extends Service {
    private final static String TAG = SourceChangeService.class.getSimpleName();
    public final static String START_ACTION = "com.ctv.sourcemenu.action.SOURCE_KEY"; // 启动虚拟按键
    public final static String KEY_INPUTSOURCE_SWITCH = "KEY_INPUTSOURCE_SWITCH";
    public final static String KEY_PORT_SWITCH = "KEY_PORT_SWITCH";

    public String name = "";
    private int inputSourceID;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


                handleAction(intent);

        return super.onStartCommand(intent, flags, startId);


    }


    private void handleAction(Intent intent){
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        inputSourceID = intent.getIntExtra(KEY_INPUTSOURCE_SWITCH, 40);
        if (START_ACTION.equals(action)){
                BaseUtils.dbg("hong", "  ---inputSourceID--- ="+ inputSourceID);
            if (ScreenUtils.Is_AHboard()){
                BaseUtils.dbg("hong","----AH----" );
                if (inputSourceID >=0 && inputSourceID < 34) {
                    AppUtils.changeSignal(getApplicationContext(), inputSourceID);
                }
                return;
            }else if (ScreenUtils.Is_MHboard() || ScreenUtils.Is_BHboard()){ // MH
                BaseUtils.dbg("hong","----MH------inputSourceID--- ="+ inputSourceID);

                if (inputSourceID != 40) {
                    if  (inputSourceID == 16) {
                        TvCommonManager.getInstance().setTvosCommonCommand("SetVGA1");
                    } else if (inputSourceID == 0) {
                        TvCommonManager.getInstance().setTvosCommonCommand("SetVGA0");
                    }
                    if (inputSourceID > 0 && inputSourceID < 34) {
                        AppUtils.changeSignal(getApplicationContext(), inputSourceID);
                    }
                    return;
                }
                // KEY_PORT_SWITCH
                int port = intent.getIntExtra(KEY_PORT_SWITCH,-1);
                BaseUtils.dbg("hong","KEY_PORT_SWITCH port....." + port);
                if(port == 0 || port == 2 || port == 3){
                    TvCommonManager.getInstance().setTvosCommonCommand("SetTIPORT" + port);
                    TvCommonManager.getInstance().setInputSource(23);
                    AppUtils.changeSignal(getApplicationContext(), 23);
                    return;
                }
            }else if (ScreenUtils.Is_LHboard()){
  
                BaseUtils.dbg("hong","LH-----inputSourceID--- ="+ inputSourceID );
                if (inputSourceID != 40) {
                    if (inputSourceID >= 0 && inputSourceID < 34) {
                        AppUtils.changeSignal(getApplicationContext(), inputSourceID);
                    }
                    return;
                }

                // KEY_PORT_SWITCH
                int port = intent.getIntExtra(KEY_PORT_SWITCH,-1);
                BaseUtils.dbg("hong","KEY_PORT_SWITCH port....." + port);
                if(port == 0 || port == 2 || port == 3){
                    TvCommonManager.getInstance().setTvosCommonCommand("SetTIPORT" + port);
                    TvCommonManager.getInstance().setInputSource(25);
                    AppUtils.changeSignal(getApplicationContext(), 25);
                    return;
                }

            }else {
                BaseUtils.dbg("hong","----AH----" );
                if (inputSourceID >=0 && inputSourceID < 34) {
                    AppUtils.changeSignal(getApplicationContext(), inputSourceID);
                }
                return;
            }
//            HHTCommonManager.getInstance().registerHHTTvEventListener(new HHTTvEventListener() {
//                @Override
//                public void onTvEvent(int i, int i1, int i2, Object o) {
//                    ///BaseUtils.dbg("hong", "i2 ="+  i2);
//                    if (i2 == 1){
////                        BaseUtils.dbg("hong", "i ="+  i);
////                        BaseUtils.dbg("hong", "i1 ="+  i1);
//                        BaseUtils.dbg("hong", "i2 ="+  i2);
//                        BaseUtils.dbg("hong", "o ="+  o);
//                        name = (String) o;
//                        if (ScreenUtils.Is_AHboard()){
//                            BaseUtils.dbg("hong", "handleAction: AH"+"  name = "+name);
//                        }else if (ScreenUtils.Is_BHboard()){
//                            BaseUtils.dbg("hong", "handleAction: BH"+"  name = "+name);
//                        }else if (ScreenUtils.Is_MHboard()){
//                            BaseUtils.dbg("hong", "handleAction: MH"+"  name = "+name);
//                        }else if (ScreenUtils.Is_LHboard()){
//                            BaseUtils.dbg("hong", "handleAction: LH" +"  name = "+name);
//                            startkey(name);
//                        }else {
//
//                        }
//
//                    }else{
//
//                    }
//
//
//                }
//            });
        }

    }
    private void startkey(String name) {
        if (name.equals("OPS")){
            HHTOpsManager.getInstance().setOpsPowerTurnOn();
        }
        HHTSourceManager.getInstance().startSourcebyKey(name);
    }
}
