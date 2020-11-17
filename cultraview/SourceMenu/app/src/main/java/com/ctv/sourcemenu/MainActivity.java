package com.ctv.sourcemenu;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.ISyncAdapter;
import android.content.Intent;
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.os.SystemProperties;
import android.service.fingerprint.IFingerprintService;
import android.service.voice.AlwaysOnHotwordDetector;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import com.ctv.sourcemenu.utils.ScreenUtils;
import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.device.HHTDeviceManager;
import com.hht.android.sdk.device.HHTTvEventListener;
import com.hht.android.sdk.ops.HHTOpsManager;
import com.hht.android.sdk.source.HHTSourceManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity  implements View.OnFocusChangeListener {

    private static final CharSequence TAG = "MainActivity";
    private List<SourceBean> sourceBeans = new ArrayList<>();
    private Window window;
    private GridView mgridView;
    // "DTV" "ATV" "VGA" "OPS"
    // "HDMI1" "HDMI2" "HDMI3" "HDMI4"
    // "TYPEC" "DP" "AV" "YPBPR"
    // "ANDROID" "STORAGE"
    public final static String ATV = HHTConstant.ATV;
    public final static String DTV = HHTConstant.DTV;
    public final static String OPS = HHTConstant.OPS;
    public final static String HDMI1 = HHTConstant.HDMI1;
    public final static String HDMI2 = HHTConstant.HDMI2;
    public final static String HDMI3 = HHTConstant.HDMI3;
    public final static String AV = HHTConstant.AV;
    public final static String ANDROID = HHTConstant.ANDROID;
    public final static String YPBPR = HHTConstant.YPBPR;
    public final static String VGA = HHTConstant.VGA;
    public final static String TYPEC = HHTConstant.TYPEC;
    public final static String LH_DP = HHTConstant.DP;
    public final static String LH_TYPEC =HHTConstant.TYPEC;
    public final static String LH_FRONT_HDMI = HHTConstant.FRONT_HDMI;

    public final static boolean IS_AH_board = ScreenUtils.Is_AHboard();
    public final static boolean IS_LH_board = ScreenUtils.Is_LHboard();
    public final static boolean IS_MH_board = ScreenUtils.Is_MHboard();
    public final static boolean IS_BH_board = ScreenUtils.Is_BHboard();
    public final static boolean IS_AH_CX = TextUtils.equals(SystemProperties.get("ro.build.display.id", ""), "CN8386_AH_CX");

    //   public final static String COMPUTER = "VGA";
    //  public final  static String YPBPR="YPBPR";
    private String flage;
    private static int  click_id = 0;
    private SourceBean sourceBean4;
    private SourceBean sourceBean41;
    private SourceBean sourceBean1;
    private SourceBean sourceBean2;
    private SourceBean sourceBean3;
    private final static String FALSE = "off";
    private final static String TRUE = "IN";
    private TextView viewById;
    private SourceGridAdapter sourceGridAdapter;
    private SourceBean sourceBean5;
    private SourceBean sourceBean6;
    private SourceBean sourceBean7;
    private SourceBean sourceBean8;
    private SourceBean sourceBean9;
    private SourceBean sourceBean10;
    private SourceBean sourceBean11;
    private ValueAnimator valueAnimator_first;
    private RectView lastrectView;
    private RectView rect;
    private final static int FINISH =0X11;



   Handler mhandler = new Handler(){
       @Override
       public void handleMessage(@NonNull Message msg) {

           switch (msg.what){

               case FINISH:

                   finish();

           }

       }
   };
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initWindow();

        //initmap();
        int resItem = 0;
        if (IS_AH_board) {
            setContentView(R.layout.activity_main_ah);
            resItem = R.layout.source_item_ah;

            L.debug("wang", "onCreate: AH ");
            initAHlist();
//            setContentView(R.layout.activity_main_lh);
//            resItem = R.layout.source_item_lh;
//            Log.d("wang", "onCreate: MH ");
//            initMHlist();
        } else if (IS_LH_board){
            setContentView(R.layout.activity_main_lh);
            resItem = R.layout.source_item_lh;
            L.debug("hongcc", "oncreat-----------LH ");
            initLHlist();

        }else if (IS_MH_board){
            setContentView(R.layout.activity_main_lh);
            resItem = R.layout.source_item_lh;
            L.debug("wang", "onCreate: MH ");
            initMHlist();

        }else if (IS_BH_board){

            setContentView(R.layout.activity_main_lh);
            resItem = R.layout.source_item_bh;
            L.debug("wang", "onCreate: BH ");
            initMHlist();

        } else {
            setContentView(R.layout.activity_main_ah);
            resItem = R.layout.source_item_ah;
            L.debug("wang", "onCreate: AH ");
            initAHlist();

        }
        sourceGridAdapter = new SourceGridAdapter(this, resItem, sourceBeans);
        mgridView = findViewById(R.id.mGridview);
        mgridView.setAdapter(sourceGridAdapter);
     //  去掉默认选中背景色
        mgridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        click();
        mhandler.sendEmptyMessageDelayed(FINISH,5000);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    //    HHTCommonManager.getInstance().unregisterHHTTvEventListener(hhtTvEventListener);
        mhandler.removeCallbacksAndMessages(null);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_OUTSIDE:
                L.debug("hggh", "onTouchEvent:  ACTION_OUTSIDE");
                finish();
                return true;
            case MotionEvent.ACTION_DOWN:
                L.debug("hggh", "onTouchEvent:  down");
                mhandler.removeCallbacksAndMessages(null);
                    break;
            case MotionEvent.ACTION_UP:
                L.debug("hggh", "onTouchEvent:  up");
                mhandler.sendEmptyMessageDelayed(FINISH,5000);
                    break;

        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateanima();

        L.debug("hongcc", "onStart:  HDMI1 = " + get(HDMI1) + "  HDMI2 =" + get(HDMI2) + "    HDMI3 =" + get(HDMI3));

        L.debug("hongcc", "onStart:  VGA = " + get(VGA) + "  VGA1 =" + get("VGA1") + "    VGA2 =" + get("VGA2"));
        L.debug("hongcc", "onStart:  YPBPR = " + get(YPBPR) + "  YPBPR1 =" + get("YPBPR1") + "    YPBPR2 =" + get("YPBPR2"));

        L.debug("hongcc", "onStart--------- CurrentSourceName " + getCurrentSourceName());

    }

    private void updateanima() {
        L.debug("hongcc", "updateanima: ");
        String currentSourceName = getCurrentSourceName();

        if (currentSourceName == null) {

            currentSourceName = ANDROID;
            sourceGridAdapter.setCurrentSourceid(currentSourceName);
        } else {
            sourceGridAdapter.setCurrentSourceid(currentSourceName);
        }
        L.debug("hongcc","onStart: current =  " + currentSourceName);
    }


    private void initWindow() {

        window = getWindow();
        window.clearFlags(4);
//        getWindow().clearFlags(4);
        WindowManager.LayoutParams attributes = window.getAttributes();

        attributes.gravity = 48;
        // attributes.y = AutoSizeUtils.dp2px(this, 35.0f);
        // attributes.x = 100;

        float dp700 = getResources().getDimension(R.dimen.dp_700);
        attributes.y = (int) dp700;
//        attributes.y = 700;
        float densityDpi = getResources().getDisplayMetrics().densityDpi;
        float scale = getResources().getDisplayMetrics().density;
        float widthPixels = getResources().getDisplayMetrics().widthPixels;
        float heightPixels = getResources().getDisplayMetrics().heightPixels;
        float xdpi = getResources().getDisplayMetrics().xdpi;
        float ydpi = getResources().getDisplayMetrics().ydpi;

        L.debug("hhc","initWindow: attributes.y =" + attributes.y + ", sw=" + ScreenUtils.getSW(this));
        window.setAttributes(attributes);
        setFinishOnTouchOutside(true);

    }

    private String get(String nameid) {

        String sourcePlugStateByKey = HHTSourceManager.getInstance().getSourcePlugStateByKey(nameid);
        flage = sourcePlugStateByKey;
        return flage;
    }
    public void initLHlist() {

        sourceBeans.clear();
        sourceBean1 = new SourceBean(R.mipmap.androidr_noar, getResources().getString(R.string.ANDROID), 1,ANDROID);
        sourceBeans.add(sourceBean1);
        if (get(LH_FRONT_HDMI).equals(TRUE)){
            sourceBean2 = new SourceBean(R.mipmap.hdmi1_noar, getResources().getString(R.string.FRONT_HDMI), 2,LH_FRONT_HDMI);
        }else {
            sourceBean2 = new SourceBean(R.mipmap.hdmi1_n, getResources().getString(R.string.FRONT_HDMI), 2,LH_FRONT_HDMI);
        }
        sourceBeans.add(sourceBean2);
        if (get(HDMI2).equals(TRUE)){
            sourceBean3 = new SourceBean(R.mipmap.hdmi1_noar, getResources().getString(R.string.HDMI), 3,HDMI2);
        }else {
            sourceBean3 = new SourceBean(R.mipmap.hdmi1_n, getResources().getString(R.string.HDMI), 3,HDMI2);
        }
        sourceBeans.add(sourceBean3);
        if(get(LH_TYPEC).equals(TRUE)){
            sourceBean4 = new SourceBean(R.mipmap.typc_noar, getResources().getString(R.string.TYPEC), 4,LH_TYPEC);
        }else {
            sourceBean4 = new SourceBean(R.mipmap.typc, getResources().getString(R.string.TYPEC), 4,LH_TYPEC);
        }
        sourceBeans.add(sourceBean4);
        if (get(VGA).equals(TRUE)){
            sourceBean5 = new SourceBean(R.mipmap.vga_noar, getResources().getString(R.string.VGA), 5,VGA);
        }else {
            sourceBean5 = new SourceBean(R.mipmap.vga_n, getResources().getString(R.string.VGA), 5,VGA);
        }
        sourceBeans.add(sourceBean5);
        if (get(YPBPR).equals(TRUE)){
            sourceBean6 = new SourceBean(R.mipmap.ypbr_noar, getResources().getString(R.string.YPBPR), 6,YPBPR);
        }else {
            sourceBean6 = new SourceBean(R.mipmap.ybpr_n, getResources().getString(R.string.YPBPR), 6,YPBPR);
        }
        sourceBeans.add(sourceBean6);
        if (get(OPS).equals(TRUE)){
            sourceBean7 = new SourceBean(R.mipmap.ops_noar, getResources().getString(R.string.OPS), 7,OPS);
        }else {
            sourceBean7 = new SourceBean(R.mipmap.ops_n, getResources().getString(R.string.OPS), 7,OPS);
        }
        sourceBeans.add(sourceBean7);
        if (get(LH_DP).equals(TRUE)){
            sourceBean8 = new SourceBean(R.mipmap.dp_noar, getResources().getString(R.string.DP), 8,LH_DP);
        }else {
            sourceBean8 = new SourceBean(R.mipmap.dp_n, getResources().getString(R.string.DP), 8,LH_DP);
        }
        sourceBeans.add(sourceBean8);
        if (get(AV).equals(TRUE)){
            sourceBean9 = new SourceBean(R.mipmap.dp_noar, getResources().getString(R.string.AV), 9,AV);
        }else {
            sourceBean9 = new SourceBean(R.mipmap.dp_n, getResources().getString(R.string.AV), 9,AV);
        }
        sourceBeans.add(sourceBean9);
        if (get(DTV).equals(TRUE)){
            sourceBean10 = new SourceBean(R.mipmap.dtv_noar, getResources().getString(R.string.DTV), 10,DTV);
        }else {
            sourceBean10 = new SourceBean(R.mipmap.dtv_n, getResources().getString(R.string.DTV), 10,DTV);
        }
        sourceBeans.add(sourceBean10);
        if (get(ATV).equals(TRUE)){
           sourceBean11= new SourceBean(R.mipmap.atv_noar,getResources().getString(R.string.ATV),11,ATV);
        }else {
            sourceBean11= new SourceBean(R.mipmap.atv_n,getResources().getString(R.string.ATV),11,ATV);

        }
        sourceBeans.add(sourceBean11);
    }


    public void initMHlist() {
        Log.d("hongcc", "initlist: MH");
        sourceBeans.clear();
        sourceBean1 = new SourceBean(R.mipmap.androidr_noar, getResources().getString(R.string.ANDROID), 1,ANDROID);
        sourceBeans.add(sourceBean1);

        if (get(OPS).equals(TRUE)) {
            sourceBean2 = new SourceBean(R.mipmap.ops_noar, getResources().getString(R.string.OPS), 2,OPS);
        } else {
            sourceBean2 = new SourceBean(R.mipmap.ops_n, getResources().getString(R.string.OPS), 2,OPS);
        }
        sourceBeans.add(sourceBean2);

        if (get(HDMI1).equals(TRUE)) {
            Log.d("hhc", "initlist:HDMI1-----true");
            sourceBean3 = new SourceBean(R.mipmap.hdmi1_noar, getResources().getString(R.string.FRONT_HDMI), 3,HDMI1);
        } else {
            Log.d("hhc", "initlist:HDMI1-----false");
            sourceBean3 = new SourceBean(R.mipmap.hdmi1_n, getResources().getString(R.string.FRONT_HDMI), 3,HDMI1);
        }
        sourceBeans.add(sourceBean3);

        if (get(HDMI2).equals(TRUE)) {
            Log.d("hhc", "initlist:HDMI2-----true");
            sourceBean4 = new SourceBean(R.mipmap.hdmi2_noar, getResources().getString(R.string.HDMI2), 4,HDMI2);
        } else {
            Log.d("hhc", "initlist:HDMI2-----false");
            sourceBean4 = new SourceBean(R.mipmap.hdmi2_n, getResources().getString(R.string.HDMI2), 4,HDMI2);
        }
        sourceBeans.add(sourceBean4);

        if (get(HDMI3).equals(TRUE)) {
            sourceBean5 = new SourceBean(R.mipmap.hdmi3_noar, getResources().getString(R.string.HDMI3), 5,HDMI3);
        } else {
            sourceBean5 = new SourceBean(R.mipmap.hdmi3_n, getResources().getString(R.string.HDMI3), 5,HDMI3);
        }
        sourceBeans.add(sourceBean5);

        if (get(VGA).equals(TRUE)) {
            sourceBean6 = new SourceBean(R.mipmap.vga_noar, getResources().getString(R.string.VGA), 6,VGA);
        } else {
            sourceBean6 = new SourceBean(R.mipmap.vga_n, getResources().getString(R.string.VGA), 6,VGA);
        }
        sourceBeans.add(sourceBean6);

        if (get(TYPEC).equals(TRUE)) {
            sourceBean7 = new SourceBean(R.mipmap.typc_noar, getResources().getString(R.string.TYPEC), 7,TYPEC);
        } else {
            sourceBean7 = new SourceBean(R.mipmap.typc, getResources().getString(R.string.TYPEC), 7,TYPEC);
        }
        sourceBeans.add(sourceBean7);

        if (get(AV).equals(TRUE)) {
            sourceBean8 = new SourceBean(R.mipmap.dp_noar, getResources().getString(R.string.AV), 8,AV);
        } else {
            sourceBean8 = new SourceBean(R.mipmap.dp_n, getResources().getString(R.string.AV), 8,AV);
        }
        sourceBeans.add(sourceBean8);
        if (get("YPBPR").equals(TRUE)) {
            sourceBean9 = new SourceBean(R.mipmap.ypbr_noar, getResources().getString(R.string.YPBPR), 9,YPBPR);
        } else {
            sourceBean9 = new SourceBean(R.mipmap.ybpr_n, getResources().getString(R.string.YPBPR), 9,YPBPR);
        }
        sourceBeans.add(sourceBean9);

        if (get(ATV).equals(TRUE)) {
            sourceBean10 = new SourceBean(R.mipmap.atv_noar, getResources().getString(R.string.ATV), 10,ATV);
        } else {
            sourceBean10 = new SourceBean(R.mipmap.atv_n, getResources().getString(R.string.ATV), 10,ATV);
        }
        sourceBeans.add(sourceBean10);
        if (get(DTV).equals(TRUE)) {
            sourceBean11 = new SourceBean(R.mipmap.dtv_noar, getResources().getString(R.string.DTV), 11,DTV);
        } else {
            sourceBean11 = new SourceBean(R.mipmap.dtv_n, getResources().getString(R.string.DTV), 11,DTV);
        }

        sourceBeans.add(sourceBean11);
    }

    public void initAHlist() {
        Log.d("hongcc", "initlist: AH");
        sourceBeans.clear();
        sourceBean1 = new SourceBean(R.mipmap.androidr_noar, getResources().getString(R.string.ANDROID), 1,ANDROID);
        sourceBeans.add(sourceBean1);

        Map<String, String> stateMap = HHTSourceManager.getInstance().getInputSrcPlugStateMap();

        if (TextUtils.equals(stateMap.get(OPS), TRUE)) {
            sourceBean2 = new SourceBean(R.mipmap.ops_noar, getResources().getString(R.string.OPS), 2,OPS);
        } else {
            sourceBean2 = new SourceBean(R.mipmap.ops_n, getResources().getString(R.string.OPS), 2,OPS);
        }
        sourceBeans.add(sourceBean2);
        if(!IS_AH_CX){
        if (TextUtils.equals(stateMap.get(HDMI1), TRUE)) {
            Log.d("hhc", "initlist:HDMI1-----true");
            sourceBean3 = new SourceBean(R.mipmap.hdmi1_noar, getResources().getString(R.string.FRONT_HDMI), 3,HDMI1);
        } else {
            Log.d("hhc", "initlist:HDMI1-----false");
            sourceBean3 = new SourceBean(R.mipmap.hdmi1_n, getResources().getString(R.string.FRONT_HDMI), 3,HDMI1);
        }
        sourceBeans.add(sourceBean3);
        }

        if (TextUtils.equals(stateMap.get(HDMI3), TRUE)) {
            Log.d("hhc", "initlist:HDMI3-----true");
            //keww 修改显示为HDMI1跟随板卡丝印，实际通道为HDMI3
            sourceBean5 = new SourceBean(R.mipmap.hdmi3_noar, getResources().getString(R.string.AH_HDMI3), 5,HDMI3);
        } else {
            sourceBean5 = new SourceBean(R.mipmap.hdmi3_n, getResources().getString(R.string.AH_HDMI3), 5,HDMI3);
        }
        sourceBeans.add(sourceBean5);
        if (TextUtils.equals(stateMap.get(HDMI2), TRUE)) {

            Log.d("hhc", "initlist:HDMI2-----true");
            sourceBean4 = new SourceBean(R.mipmap.hdmi2_noar, getResources().getString(R.string.HDMI2), 4,HDMI2);

        } else {

            Log.d("hhc", "initlist:HDMI2-----false");
            sourceBean4 = new SourceBean(R.mipmap.hdmi2_n, getResources().getString(R.string.HDMI2), 4,HDMI2);
        }

        sourceBeans.add(sourceBean4);

        if (TextUtils.equals(stateMap.get(VGA), TRUE)) {
            sourceBean6 = new SourceBean(R.mipmap.vga_noar, getResources().getString(R.string.VGA), 6,VGA);
        } else {
            sourceBean6 = new SourceBean(R.mipmap.vga_n, getResources().getString(R.string.VGA), 6,VGA);
        }
        sourceBeans.add(sourceBean6);
    }

    public void click() {
        mgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SourceBean sourceBean = sourceBeans.get(position);
                String name = sourceBean.getText();
                int sourcePos = sourceBean.getPosition();

                rect = view.findViewById(R.id.rect);
                lastrectView = rect;
                mhandler.removeCallbacksAndMessages(null);
                mhandler.sendEmptyMessageDelayed(FINISH,5000);

                String currentSourceKey = getCurrentSourceName();
                L.debug("hongcc", "onItemClick: name = " + name +"  sourcePos = "+sourcePos);
                L.debug("hongcc", "onItemClick: currentSourcName = " + currentSourceKey );

                if (IS_LH_board){
                        switch (sourcePos){
                            case 1:
                                if (!currentSourceKey.equals(ANDROID)) {
                                    Log.d("hhc", "onItemClick: Android");
                                    sourceGridAdapter.setCurrentSourceid("update");
                                    startanim(ANDROID);
                                }
                                break;
                            case 2:
                                if(!currentSourceKey.equals(LH_FRONT_HDMI)){
                                    sourceGridAdapter.setCurrentSourceid("update");
                                    startanim(LH_FRONT_HDMI);
                                }
                                break;
                            case 3:
                                if (!currentSourceKey.equals(HDMI2)){
                                    sourceGridAdapter.setCurrentSourceid("update");
                                    startanim(HDMI2);
                                }
                                break;

                            case 4:
                                if (!currentSourceKey.equals(LH_TYPEC)){
                                    sourceGridAdapter.setCurrentSourceid("update");
                                    startanim(LH_TYPEC);
                                }

                                break;
                            case 5:
                                if (!currentSourceKey.equals(VGA)){
                                    sourceGridAdapter.setCurrentSourceid("update");
                                    startanim(VGA);
                                }

                                break;
                            case 6:

                                if (!currentSourceKey.equals(YPBPR)){
                                    sourceGridAdapter.setCurrentSourceid("update");
                                    startanim(YPBPR);
                                }
                                break;
                            case 7:

                                if (!currentSourceKey.equals(OPS)){
                                    sourceGridAdapter.setCurrentSourceid("update");
                                    startanim(OPS);
                                }
                                break;
                            case 8:

                                if (!currentSourceKey.equals(LH_DP)){
                                    sourceGridAdapter.setCurrentSourceid("update");
                                    startanim(LH_DP);
                                }
                                break;
                            case 9:

                                if (!currentSourceKey.equals(AV)){
                                    sourceGridAdapter.setCurrentSourceid("update");
                                    startanim(AV);
                                }
                                break;
                            case 10:
                                if (!currentSourceKey.equals(DTV)){
                                    sourceGridAdapter.setCurrentSourceid("update");
                                    startanim(DTV);
                                }

                                break;
                            case 11:


                                if (!currentSourceKey.equals(ATV)){
                                    sourceGridAdapter.setCurrentSourceid("update");
                                    startanim(ATV);
                                }


                            default:



                        }
                }else if (IS_MH_board){
                    switch (sourcePos) {
                        case 1:

                            L.debug("hhc", "onItemClick:1 ");

                            if (!currentSourceKey.equals(ANDROID)) {
                                Log.d("hhc", "onItemClick: Android");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(ANDROID);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey+"   position ="+position);

                            break;
                        case 2:

                            if (!currentSourceKey.equals(OPS)) {
                                Log.d("hhc", "onItemClick: ops");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(OPS);
                            }
                            Log.d("hhc", "onItemClick:2 ");
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);


                            break;
                        case 3:

                            if (!currentSourceKey.equals(HDMI1)) {
                                Log.d("hhc", "onItemClick: hdmi1");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(HDMI1);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);

                            break;
                        case 4:

                            if (!currentSourceKey.equals(HDMI2)) {
                                Log.d("hhc", "onItemClick: hdmi2");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(HDMI2);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);
                            break;
                        case 5:

                            if(!currentSourceKey.equals(HDMI3)) {
                                Log.d("hhc", "onItemClick: hdmi3");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(HDMI3);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey+"  position ="+position);
                            break;
                        case 6:

                            if (!currentSourceKey.equals(VGA)) {
                                Log.d("hongcc", "onItemClick: go to VGA");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(VGA);
                            }
                            break;
                        case 7:
                            if (!currentSourceKey.equals(TYPEC)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(TYPEC);
                            }

                            break;
                        case 8:

                            if (!currentSourceKey.equals(AV)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(AV);
                            }

                            break;
                        case 9:
                            if (!currentSourceKey.equals(YPBPR)) {
                                Log.d("hongcc", "onItemClick: go to VGA");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(YPBPR);
                            }

                            break;
                        case 10:
                            if (!currentSourceKey.equals(ATV)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(ATV);
                            }

                            break;
                        case 11:
                            if (!currentSourceKey.equals(DTV)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(DTV);
                            }
                            break;
                        default:

                    }
                }else if (IS_BH_board){
                    switch (sourcePos) {
                        case 1:

                            L.debug("hhc", "onItemClick:1 ");
                            if (!currentSourceKey.equals(ANDROID)) {
                                Log.d("hhc", "onItemClick: Android");
                                sourceGridAdapter.setCurrentSourceid("update");
                                // startanim(ANDROID);
                                keyEventBySystem(KeyEvent.KEYCODE_HOME);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey+"   position ="+position);
                            break;
                        case 2:

                            if (!currentSourceKey.equals(OPS)) {
                                Log.d("hhc", "onItemClick: ops");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startkey(OPS);
                            }
                            Log.d("hhc", "onItemClick:2 ");
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);


                            break;
                        case 3:

                            if (!currentSourceKey.equals(HDMI1)) {
                                Log.d("hhc", "onItemClick: hdmi1");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startkey(HDMI1);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);

                            break;
                        case 4:

                            if (!currentSourceKey.equals(HDMI2)) {
                                Log.d("hhc", "onItemClick: hdmi2");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startkey(HDMI2);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);
                            break;
                        case 5:

                            if(!currentSourceKey.equals(HDMI3)) {
                                Log.d("hhc", "onItemClick: hdmi3");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startkey(HDMI3);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey+"  position ="+position);
                            break;
                        case 6:

                            if (!currentSourceKey.equals(VGA)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startkey(VGA);
                            }
                            break;
                        case 7:

                            if (!currentSourceKey.equals(TYPEC)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startkey(TYPEC);
                            }
                            break;
                        case 8:

                            if (!currentSourceKey.equals(AV)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startkey(AV);
                            }
                            break;
                        case 9:

                            if (!currentSourceKey.equals(YPBPR)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startkey(YPBPR);
                            }

                            break;
                        case 10:
                            if (!currentSourceKey.equals(ATV)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startkey(ATV);
                            }

                            break;
                        case 11:
                            if (!currentSourceKey.equals(DTV)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startkey(DTV);
                            }

                            break;
                        default:

                    }
                } else if (IS_AH_board){
                    switch (sourcePos){
                        case 1:
                            L.debug("hhc", "onItemClick:1 ");
                            if (!currentSourceKey.equals(ANDROID)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(ANDROID);
                            }
                            L.debug("hhc", "onItemClick: currentSourcename=" + currentSourceKey+"   position ="+position);
                            break;
                        case 2:
                            if (!currentSourceKey.equals(OPS)) {
                                L.debug("hhc", "onItemClick: ops");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(OPS);
                            }
                            break;
                        case 3:
                            if (!currentSourceKey.equals(HDMI1)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(HDMI1);
                            }
                            L.debug("hhc", "onItemClick: currentSourcename=" + currentSourceKey);
                            break;
                        case 4:
                            if (!currentSourceKey.equals(HDMI2)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(HDMI2);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);
                            break;
                        case 5:
                            if (!currentSourceKey.equals(HDMI3)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(HDMI3);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey+"  position ="+position);
                            break;
                        case 6:
                            if (!currentSourceKey.equals(VGA)) {
                                Log.d("hongcc", "onItemClick: go to VGA");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(VGA);
                            }

                            break;
                            default:
                    }
                }else {
                    switch (sourcePos) {
                        case 1:
                            Log.d("hhc", "onItemClick:1 ");
                            if (!currentSourceKey.equals(ANDROID)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(ANDROID);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);

                            break;
                        case 2:

                            if (!currentSourceKey.equals(OPS)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(OPS);
                            }

                            break;
                        case 3:

                            if (!currentSourceKey.equals(HDMI1)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(HDMI1);
                            }
                            Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);

                            break;
                        case 4:

                            if (!currentSourceKey.equals(HDMI2)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(HDMI2);
                            }
                            break;
                        case 5:

                            if (!currentSourceKey.equals(HDMI3)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(HDMI3);
                            }
                            break;
                        case 6:
                            if (!currentSourceKey.equals(VGA)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(VGA);
                            }

                            break;
                        case 7:
                            if (!currentSourceKey.equals(TYPEC)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(TYPEC);
                            }

                            break;
                        case 8:

                            if (!currentSourceKey.equals(AV)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(AV);
                            }
                            break;
                        case 9:
                            if (!currentSourceKey.equals(YPBPR)) {
                                Log.d("hongcc", "onItemClick: in ypbpr");
                                Log.d("hongcc", "onItemClick: go to VGA");
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(YPBPR);
                            }

                            break;
                        case 10:
                            if (!currentSourceKey.equals(ATV)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(ATV);
                            }

                            break;
                        case 11:

                            if (!currentSourceKey.equals(DTV)) {
                                sourceGridAdapter.setCurrentSourceid("update");
                                startanim(DTV);
                            }

                            break;
                        default:

                    }
                }


            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onPause() {
        Log.d("hggh", "onPause: ");
        if (!isTopActivity()){
            finish();
        }
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean isTopActivity(){
        boolean isTop = false;
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
       // DebugLog.d(TAG, "isTopActivity = " + cn.getClassName());
        if (cn.getClassName().contains(TAG))
        {
            isTop = true;
        }
      //  DebugLog.d(TAG, "isTop = " + isTop);
        return isTop;
    }

    public void startanim(final String name) {
        valueAnimator_first = ValueAnimator.ofInt(new int[]{0, 100});
        valueAnimator_first.setDuration(1500);

        valueAnimator_first.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                lastrectView.setProgress(((Integer) animation.getAnimatedValue()).intValue());
                lastrectView.reDraw();

            }
        });
        valueAnimator_first.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
            //    Log.d("hongcc", "onAnimationEnd:  name =" + name);
                if (name.equals(ANDROID)) {
                    Log.d("hhc", "onAnimationEnd: go to android ");
                    keyEventBySystem(KeyEvent.KEYCODE_HOME);
                    //noticeChangeSignal(getApplicationContext(),34);
                } else {
                    Log.d("hongcc", "onAnimationEnd:  name =" + name);
                    startkey(name);
                }
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator_first.start();
    }






    HHTTvEventListener hhtTvEventListener = (int i, int i1, int i2, Object o)->{
        Log.d("hggh", "run:   chaba__event  ");
        if (i == HHTCommonManager.HHT_INPUTSOURCE_PLUG_STATUS) {
//
//            handler.post(() -> {
//                Log.d("hggh", "run:   chaba__event  ");
//              //  upDateAdapter();
//            });
        }
    };

    private void registerHHTTvEventListener() {
        HHTCommonManager.getInstance().registerHHTTvEventListener(hhtTvEventListener);
    }

    private void upDateAdapter() {
        sourceBeans.clear();
        if (IS_LH_board) {
          initLHlist();
        } else if (IS_AH_board){
          initAHlist();
        }else if (IS_MH_board){
          initMHlist();
        }else {
          initMHlist();
        }
        //  updateanima();
       // sourceGridAdapter.notifyDataSetChanged();
    }


    private void startkey(String name) {
        if (name.equals(OPS)){
            HHTOpsManager.getInstance().setOpsPowerTurnOn();
        }
            HHTSourceManager.getInstance().startSourcebyKey(name);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        L.debug("hongcc", "onKeyUp: ");
        mhandler.removeCallbacksAndMessages(null);
        mhandler.sendEmptyMessageDelayed(FINISH,5000);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        L.debug("hongcc", "onKeyDown: ");
        mhandler.removeCallbacksAndMessages(null);
        mhandler.sendEmptyMessageDelayed(FINISH,5000);

        return super.onKeyDown(keyCode, event);
    }



    private String getCurrentSourceName() {

        String curSource = HHTSourceManager.getInstance().getCurSource();
        return curSource;


    }

    public static void noticeChangeSignal(Context context, int source) {
        // 发送SOURCE广播
        Intent intent = new Intent("android.intent.action.USB_UART_TOUCH");
        intent.putExtra("type", "Android");
        if (source >= 0 && source != 34) { // 其他通道
            intent.putExtra("type", "Other");
            intent.putExtra("Source", source + "");
        }
        context.sendBroadcast(intent);
    }

    public static void keyEventBySystem(final int keycode) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keycode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        L.debug("hongcc","v"+v.getId());

        if(hasFocus){
            v.setSelected(true);

        }else{
            v.setSelected(false);
        }
    }
}
