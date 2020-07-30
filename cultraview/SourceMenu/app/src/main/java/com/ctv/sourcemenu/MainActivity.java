package com.ctv.sourcemenu;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.os.SystemProperties;
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

import com.ctv.sourcemenu.utils.ScreenUtils;
import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.device.HHTDeviceManager;
import com.hht.android.sdk.device.HHTTvEventListener;
import com.hht.android.sdk.source.HHTSourceManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
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
    public final static String TYPEC = HHTConstant.DP;
    //   public final static String COMPUTER = "VGA";
    //  public final  static String YPBPR="YPBPR";
    private String flage;
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

    Handler handler = new Handler();
    private HashMap<String, Integer> sourceidMap;
    private String produce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initWindow();

        produce = getProduce();
//        produce = "CV8386H_MH";// getProduce();
        int resItem;
        if (produce.equals("CV8386H_MH")) {
            setContentView(R.layout.activity_main_mh);
            resItem = R.layout.source_item_mh;
            Log.d("wang", "onCreate: MH ");
            initMHlist();
        } else {
            setContentView(R.layout.activity_main_ah);
            resItem = R.layout.source_item_ah;
            Log.d("wang", "onCreate: AH ");
            initAHlist();
        }


        initmap();


        sourceGridAdapter = new SourceGridAdapter(this, resItem, sourceBeans);
        mgridView = findViewById(R.id.mGridview);
        registerHHTTvEventListener();
        //  mgridView.setSelected(Color.TRANSPARENT);
        mgridView.setAdapter(sourceGridAdapter);
        // 去掉默认选中背景色
        mgridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        click();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        HHTCommonManager.getInstance().unregisterHHTTvEventListener(hhtTvEventListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            L.d("点击了外部");
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        registerHHTTvEventListener();
        updateanima();

        Log.d("hongcc", "onStart:  HDMI1 = " + get(HDMI1) + "  HDMI2 =" + get(HDMI2) + "    HDMI3 =" + get(HDMI3));

        Log.d("hongcc", "onStart:  VGA = " + get(VGA) + "  VGA1 =" + get("VGA1") + "    VGA2 =" + get("VGA2"));
        Log.d("hongcc", "onStart:  YPBPR = " + get(YPBPR) + "  YPBPR1 =" + get("YPBPR1") + "    YPBPR2 =" + get("YPBPR2"));


    }

    private void updateanima() {
        Log.d("hongcc", "updateanima: ");
        String currentSourceName = getCurrentSourceName();
        Integer id = sourceidMap.get(currentSourceName);
        if (id == null) {
            id = 1;
            sourceGridAdapter.setCurrentSourceid(id);
        } else {
            sourceGridAdapter.setCurrentSourceid(id);
        }
        Log.d("hongcc", "onStart: current =  " + currentSourceName + " id =" + id);
    }

    private void initmap() {
        sourceidMap = new HashMap<>();
        sourceidMap.put(ANDROID, 1);
        sourceidMap.put(OPS, 2);
        sourceidMap.put(HDMI1, 3);
        sourceidMap.put(HDMI2, 4);
        sourceidMap.put(HDMI3, 5);
        sourceidMap.put(VGA, 6);
        sourceidMap.put(TYPEC, 7);
        sourceidMap.put(AV, 8);
        sourceidMap.put(YPBPR, 9);
        sourceidMap.put(ATV, 10);
        sourceidMap.put(DTV, 11);
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
        L.d("hhc", "initWindow:  widthPixels*heightPixels=%s*%s, xdpi*ydpi=%s*%s, scale=%s, densityDpi=%s",
                widthPixels, heightPixels, xdpi, ydpi, scale, densityDpi);
        L.d("hhc", "initWindow: attributes.y =" + attributes.y + ", sw=" + ScreenUtils.getSW(this));
        window.setAttributes(attributes);
        setFinishOnTouchOutside(true);

    }

    private String get(String nameid) {
        // Map<String, String> inputSrcPlugStateMap = HHTSourceManager.getInstance().getInputSrcPlugStateMap();
        // flage = inputSrcPlugStateMap.get(nameid);
        String sourcePlugStateByKey = HHTSourceManager.getInstance().getSourcePlugStateByKey(nameid);
        flage = sourcePlugStateByKey;
        return flage;
    }

    public void initMHlist() {
        Log.d("hongcc", "initlist: MH");
        sourceBeans.clear();
        sourceBean1 = new SourceBean(R.mipmap.androidr_noar, "ANDROID", 1);
        sourceBeans.add(sourceBean1);

        if (get(OPS).equals(TRUE)) {
            sourceBean2 = new SourceBean(R.mipmap.ops_noar, "OPS", 2);
        } else {
            sourceBean2 = new SourceBean(R.mipmap.ops_n, "OPS", 2);
        }
        sourceBeans.add(sourceBean2);

        if (get(HDMI1).equals(TRUE)) {
            Log.d("hhc", "initlist:HDMI1-----true");
            sourceBean3 = new SourceBean(R.mipmap.hdmi1_noar, "高清1", 3);
        } else {
            Log.d("hhc", "initlist:HDMI1-----false");
            sourceBean3 = new SourceBean(R.mipmap.hdmi1_n, "高清1", 3);
        }
        sourceBeans.add(sourceBean3);

        if (get(HDMI2).equals(TRUE)) {
            Log.d("hhc", "initlist:HDMI2-----true");
            sourceBean4 = new SourceBean(R.mipmap.hdmi2_noar, "高清2", 4);
        } else {
            Log.d("hhc", "initlist:HDMI2-----false");
            sourceBean4 = new SourceBean(R.mipmap.hdmi2_n, "高清2", 4);
        }
        sourceBeans.add(sourceBean4);

        if (get(HDMI3).equals(TRUE)) {
            sourceBean5 = new SourceBean(R.mipmap.hdmi3_noar, "高清3", 5);
        } else {
            sourceBean5 = new SourceBean(R.mipmap.hdmi3_n, "高清3", 5);
        }
        sourceBeans.add(sourceBean5);

        if (get(VGA).equals(TRUE)) {
            sourceBean6 = new SourceBean(R.mipmap.vga_noar, "VGA", 6);
        } else {
            sourceBean6 = new SourceBean(R.mipmap.vga_n, "VGA", 6);
        }
        sourceBeans.add(sourceBean6);

        if (get(TYPEC).equals(TYPEC)) {
            sourceBean7 = new SourceBean(R.mipmap.typc_noar, "TYPC", 7);
        } else {
            sourceBean7 = new SourceBean(R.mipmap.typc, "TYPC", 7);
        }
        sourceBeans.add(sourceBean7);

        if (get(AV).equals(TRUE)) {
            sourceBean8 = new SourceBean(R.mipmap.dp_noar, "视频", 8);
        } else {
            sourceBean8 = new SourceBean(R.mipmap.dp_n, "视频", 8);
        }
        sourceBeans.add(sourceBean8);
        if (get("YPBPR").equals(TRUE)) {
            sourceBean9 = new SourceBean(R.mipmap.ypbr_noar, "分量", 9);
        } else {
            sourceBean9 = new SourceBean(R.mipmap.ybpr_n, "分量", 9);
        }
        sourceBeans.add(sourceBean9);

        if (get(ATV).equals(TRUE)) {
            sourceBean10 = new SourceBean(R.mipmap.atv_noar, "模拟电视", 10);
        } else {
            sourceBean10 = new SourceBean(R.mipmap.atv_n, "模拟电视", 10);
        }
        sourceBeans.add(sourceBean10);
        if (get(DTV).equals(TRUE)) {
            sourceBean11 = new SourceBean(R.mipmap.dtv_noar, "数字电视", 11);
        } else {
            sourceBean11 = new SourceBean(R.mipmap.dtv_n, "数字电视", 11);
        }

        sourceBeans.add(sourceBean11);
    }

    public void initAHlist() {
        Log.d("hongcc", "initlist: AH");
        sourceBeans.clear();
        sourceBean1 = new SourceBean(R.mipmap.androidr_noar, "ANDROID", 1);
        sourceBeans.add(sourceBean1);

         Map<String, String> stateMap = HHTSourceManager.getInstance().getInputSrcPlugStateMap();

        if (TextUtils.equals(stateMap.get(OPS), TRUE)) {
            sourceBean2 = new SourceBean(R.mipmap.ops_noar, "OPS", 2);
        } else {
            sourceBean2 = new SourceBean(R.mipmap.ops_n, "OPS", 2);
        }
        sourceBeans.add(sourceBean2);

        if (TextUtils.equals(stateMap.get(HDMI1), TRUE)) {
            Log.d("hhc", "initlist:HDMI1-----true");
            sourceBean3 = new SourceBean(R.mipmap.hdmi1_noar, "高清1", 3);
        } else {
            Log.d("hhc", "initlist:HDMI1-----false");
            sourceBean3 = new SourceBean(R.mipmap.hdmi1_n, "高清1", 3);
        }
        sourceBeans.add(sourceBean3);

        if (TextUtils.equals(stateMap.get(HDMI2), TRUE)) {
            Log.d("hhc", "initlist:HDMI2-----true");
            sourceBean4 = new SourceBean(R.mipmap.hdmi2_noar, "高清2", 4);
        } else {
            Log.d("hhc", "initlist:HDMI2-----false");
            sourceBean4 = new SourceBean(R.mipmap.hdmi2_n, "高清2", 4);
        }
        sourceBeans.add(sourceBean4);

        if (TextUtils.equals(stateMap.get(HDMI3), TRUE)) {
            sourceBean5 = new SourceBean(R.mipmap.hdmi3_noar, "高清3", 5);
        } else {
            sourceBean5 = new SourceBean(R.mipmap.hdmi3_n, "高清3", 5);
        }
        sourceBeans.add(sourceBean5);

        if (TextUtils.equals(stateMap.get(VGA), TRUE)) {
            sourceBean6 = new SourceBean(R.mipmap.vga_noar, "VGA", 6);
        } else {
            sourceBean6 = new SourceBean(R.mipmap.vga_n, "VGA", 6);
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

                // current source key
                String currentSourceKey = getCurrentSourceName();

                Log.d("hongcc", "onItemClick: name = " + name);
                switch (sourcePos) {
                    case 1:
                        Log.d("hhc", "onItemClick:1 ");
                        if (currentSourceKey.equals(ANDROID)) {
                            Log.d("hhc", "onItemClick: Android");
                        } else {
                            sourceGridAdapter.setCurrentPosition(position);
                            sourceGridAdapter.setCurrentSourceid(-3);
                            startanim(ANDROID);
                        }
                        Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);

                        break;
                    case 2:
                        if (currentSourceKey.equals(OPS)) {
                            Log.d("hhc", "onItemClick: ops");
                        } else {
                            sourceGridAdapter.setCurrentPosition(position);
                            sourceGridAdapter.setCurrentSourceid(-3);
                            startanim(OPS);
                        }
                        Log.d("hhc", "onItemClick:2 ");
                        Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);


                        break;
                    case 3:
                        if (currentSourceKey.equals(HDMI1)) {
                            Log.d("hhc", "onItemClick: hdmi1");
                        } else {
                            sourceGridAdapter.setCurrentPosition(position);
                            sourceGridAdapter.setCurrentSourceid(-3);
                            startanim(HDMI1);
                        }
                        Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);

                        break;
                    case 4:
                        if (currentSourceKey.equals(HDMI2)) {
                            Log.d("hhc", "onItemClick: hdmi2");
                        } else {
                            sourceGridAdapter.setCurrentPosition(position);
                            sourceGridAdapter.setCurrentSourceid(-3);
                            startanim(HDMI2);
                        }
                        Log.d("hhc", "onItemClick: currentSourcename=" + currentSourceKey);

                        break;
                    case 5:
                        if (currentSourceKey.equals(HDMI3)) {
                            Log.d("hhc", "onItemClick: hdmi3");
                        } else {
                            sourceGridAdapter.setCurrentPosition(position);
                            sourceGridAdapter.setCurrentSourceid(-3);
                            startanim(HDMI3);
                        }

                        break;
                    case 6:
                        if (currentSourceKey.equals(VGA)) {
                            Log.d("hongcc", "onItemClick: in VGA");
                        } else {
                            Log.d("hongcc", "onItemClick: go to VGA");
                            sourceGridAdapter.setCurrentPosition(position);
                            sourceGridAdapter.setCurrentSourceid(-3);
                            startanim(VGA);
                        }

                        break;
                    case 7:
                        if (currentSourceKey.equals(TYPEC)) {

                        } else {
                            sourceGridAdapter.setCurrentPosition(position);
                            sourceGridAdapter.setCurrentSourceid(-3);
                            startanim(TYPEC);
                        }

                        break;
                    case 8:
                        if (currentSourceKey.equals(AV)) {

                        } else {
                            sourceGridAdapter.setCurrentPosition(position);
                            sourceGridAdapter.setCurrentSourceid(-3);
                            startanim(AV);
                        }

                        break;
                    case 9:
                        if (currentSourceKey.equals(YPBPR)) {
                            Log.d("hongcc", "onItemClick: in ypbpr");
                        } else {
                            Log.d("hongcc", "onItemClick: go to VGA");

                            sourceGridAdapter.setCurrentPosition(position);
                            sourceGridAdapter.setCurrentSourceid(-3);
                            startanim(YPBPR);
                        }

                        break;
                    case 10:
                        if (currentSourceKey.equals(ATV)) {

                        } else {
                            sourceGridAdapter.setCurrentPosition(position);
                            sourceGridAdapter.setCurrentSourceid(-3);
                            startanim(ATV);
                        }

                        break;
                    case 11:
                        if (currentSourceKey.equals(DTV)) {

                        } else {
                            sourceGridAdapter.setCurrentPosition(position);
                            sourceGridAdapter.setCurrentSourceid(-3);
                            startanim(DTV);
                        }

                        break;
                    default:

                }

            }
        });
    }

    private String getProduce() {
        String s = SystemProperties.get("ro.product.board", "");
        //System. get("ro.product.board",""）;
        return s;
    }

    private void startanim(final String name) {
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
                Log.d("hongcc", "onAnimationEnd:  name =" + name);
                if (name.equals(ANDROID)) {
                    Log.d("hhc", "onAnimationEnd: go to android ");
                    keyEventBySystem(KeyEvent.KEYCODE_HOME);
                    //  noticeChangeSignal(getApplicationContext(),34);
                } else {
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
        if (i == HHTCommonManager.HHT_INPUTSOURCE_PLUG_STATUS) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    upDateAdapter();
                }
            }, 2000);
        }
    };

    private void registerHHTTvEventListener() {
        HHTCommonManager.getInstance().registerHHTTvEventListener(hhtTvEventListener);
    }

    private void upDateAdapter() {
        sourceBeans.clear();
        if (produce.equals("CV8386H_MH")) {
            initMHlist();
        } else {
            initAHlist();
        }
        updateanima();
        sourceGridAdapter.notifyDataSetChanged();
    }

    private void startkey(String name) {
        HHTSourceManager.getInstance().startSourcebyKey(name);
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


}
