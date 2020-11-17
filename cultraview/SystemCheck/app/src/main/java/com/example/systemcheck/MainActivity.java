package com.example.systemcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.service.fingerprint.IFingerprintService;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.example.systemcheck.mananage.SkinManager;
import com.example.systemcheck.uitil.AutoSizeUtils;
import com.example.systemcheck.uitil.CleanUtils;
import com.example.systemcheck.view.CircleView;
import com.example.systemcheck.view.Progress;
import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.ops.HHTOpsManager;
import com.hht.android.sdk.source.HHTSourceManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;


import java.io.File;
import java.text.DecimalFormat;

import static android.view.View.GONE;

public class MainActivity extends Activity implements View.OnClickListener ,DeleteCacheListener{
    public static TextView mCircleViewColorOne;
    public static TextView mCircleViewColorTwo;
    private String TAG ="nnng";
    private Button mDetectionBtn;
    private boolean mIsAutoClose = false;
    private boolean mIsDetecting = true;
    private boolean mIsPowerOn = false;
    private CleanUtils mCleanUtils;
    public Context mContext;
    private static final int CHECK_TOUCH_END = 1001;

    private static final int CHECK_TOUCH_START = 2002;

    private static final int CHECK_SENSE = 3003;

    private static final String IS_LIGHTSENSE = "IS_LIGHTSENSE";

    Handler mHandler =new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 7) {
                MainActivity.this.notifyDectComplete();
                MainActivity.this.finish();
            }
        }
    };
    Handler mainHandler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            int i = msg.what;
            String str = "skin:loading:background";
            String str2 = "skin:normal:background";
            String str3 = "";
            StringBuilder stringBuilder;
            String stringBuilder2;
            String cunchu_sum;
            TextView access$1900;
            StringBuilder stringBuilder3;
            StringBuilder stringBuildertv1;

            switch (msg.what){
                case  0:

                 //   openview();
                    MainActivity.this.mPercent.setVisibility(View.VISIBLE);
                    MainActivity.this.mCanuse.setVisibility(GONE);
                    MainActivity.this.mAlreadyuse.setVisibility(GONE);
                    MainActivity.this.mOptimizeone.setText(R.string.detecting);
                    stringBuilder = new StringBuilder();
//                    stringBuilder.append(((MainActivity.this.mCleanUtils.getTotalMemorySize(MainActivity.this.mContext)
//                            - MainActivity.this.mCleanUtils.getAvailableMemory(MainActivity.this.mContext)) * 100) / MainActivity.this.mCleanUtils.getTotalMemorySize(MainActivity.this.mContext));
                    long totalMemorySize = MainActivity.this.mCleanUtils.getTotalMemorySize(MainActivity.this.mContext);
                    long availableMemory1 = MainActivity.this.mCleanUtils.getAvailableMemory(MainActivity.this.mContext);
                    Log.d("memory", "handleMessage  1   -- totalMemorySize ="+totalMemorySize);
                    Log.d("memory", "handleMessage  1   -- availableMemory1 ="+availableMemory1);
                    stringBuilder.append((storge - availableMemory1)*100/3000);
                    stringBuilder.append(str3);
                    Log.d("anim", "handleMessage  1   -- str ="+stringBuilder.toString());
                    setfiveanim();
                    MainActivity.this.startAnimationPecent(stringBuilder.toString());
                    return;
                case 1:
                    //检测系统内存
                    if (!(MainActivity.this.mAnimatorMemory == null || MainActivity.this.mAnimatorMemory.isRunning())) {
                        MainActivity.this.mCanuse.setVisibility(0);
                        MainActivity.this.mAlreadyuse.setVisibility(0);
                        MainActivity.this.mPercent.setVisibility(8);
                        stringBuilder = new StringBuilder();
                    //    stringBuilder.append(MainActivity.this.mCleanUtils.getTotalMemorySize(MainActivity.this.mContext) - MainActivity.this.mCleanUtils.getAvailableMemory(MainActivity.this.mContext));
                        long availableMemory11 = MainActivity.this.mCleanUtils.getAvailableMemory(MainActivity.this.mContext);
                        stringBuilder.append(storge - availableMemory11);
                        stringBuilder.append(str3);
                        stringBuilder2 = stringBuilder.toString();
                        TextView access$1300 = MainActivity.this.mAlreadyuse;

                        StringBuilder stringBuilder4 = new StringBuilder();
//                        stringBuilder4.append(MainActivity.this.getResources().getString(R.string.alreadyuse));
//                        stringBuilder4.append(stringBuilder2);
//                        stringBuilder4.append(MainActivity.this.getResources().getString(R.string.mb));
                        access$1300.setText(stringBuilder4.toString());
                        String  dd=  MainActivity.this.mCleanUtils.getTotalM(mContext);
                        long availableMemory =MainActivity.this.mCleanUtils.getAvailableMemory(MainActivity.this.mContext);
                        String avai = MainActivity.this.mCleanUtils.getAvailableMemory_GB(MainActivity.this.mContext);
                        stringBuilder2 = String.valueOf(avai);
                        TextView access$1200 = MainActivity.this.mCanuse;
                        StringBuilder stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(MainActivity.this.getResources().getString(R.string.caunse));
                        stringBuilder5.append(stringBuilder2);
                        stringBuilder5.append("GB/");
                        stringBuilder5.append(dd);
                        stringBuilder5.append(MainActivity.this.getResources().getString(R.string.gb));
                        access$1200.setText(stringBuilder5.toString());
                        MainActivity.this.mCleanUtils.clearMemoryCache(MainActivity.this.mContext);
                        SystemClock.sleep(1000);
                        long availableMemory2 = MainActivity.this.mCleanUtils.getAvailableMemory(MainActivity.this.mContext) - availableMemory;
                        if (availableMemory2 < 0) {
                            availableMemory2 = 0;
                        }
                        StringBuilder stringBuilder6 = new StringBuilder();
                        stringBuilder6.append(availableMemory2);
                        stringBuilder6.append(str3);
                        String stringBuilder7 = stringBuilder6.toString();
                        if (availableMemory2 == 0) {
                            MainActivity.this.mOptimizeone.setText(MainActivity.this.getResources().getString(R.string.already_clean_all));
                        } else {
                            TextView access$1400 = MainActivity.this.mOptimizeone;
                            StringBuilder stringBuilder8 = new StringBuilder();
                            stringBuilder8.append(MainActivity.this.getResources().getString(R.string.optimized_success));
                            stringBuilder8.append(MainActivity.this.getResources().getString(R.string.clean));
                            stringBuilder8.append(stringBuilder7);
                            stringBuilder8.append(MainActivity.this.getResources().getString(R.string.mb));
                            access$1400.setText(stringBuilder8.toString());
                        }
                    }
                    MainActivity.this.mOptimizetwo.setText(R.string.detecting);
                    MainActivity.this.mPercentSD.setVisibility(0);
                    MainActivity.this.mCanuseSD.setVisibility(8);
                    MainActivity.this.mAlreadyuseSD.setVisibility(8);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(((MainActivity.this.mCleanUtils.getEmmcSize().longValue() - MainActivity.this.mCleanUtils.getAvailableExternalMemorySize(MainActivity.this.mContext)) * 100) / MainActivity.this.mCleanUtils.getEmmcSize().longValue());
                    stringBuilder.append(str3);
                    setsixaim();
                    MainActivity.this.startAnimationPecentSD(stringBuilder.toString());
                    return;
                case 2:
                    //检测系统存储

                    MainActivity.this.mIsDetecting = false;
                    MainActivity.this.mCleanUtils.startCleanCache();
                    if (!(MainActivity.this.mAanimatorSD == null || MainActivity.this.mAanimatorSD.isRunning())) {
                        MainActivity.this.mCanuseSD.setVisibility(0);
                        MainActivity.this.mAlreadyuseSD.setVisibility(0);
                        MainActivity.this.mPercentSD.setVisibility(8);
                       stringBuilder = new StringBuilder();
//                        stringBuilder.append(MainActivity.this.mCleanUtils.getEmmcSize().longValue() - MainActivity.this.mCleanUtils.getAvailableExternalMemorySize(MainActivity.this.mContext));
//                        stringBuilder.append(str3);
//                        stringBuilder2 = stringBuilder.toString();
                        cunchu_sum = stringBuilder.toString();
                        access$1900 = MainActivity.this.mAlreadyuseSD;
//                        stringBuilder3 = new StringBuilder();
//                        stringBuilder3.append(MainActivity.this.getResources().getString(R.string.alreadyuse));
//                        stringBuilder3.append(stringBuilder2);
//                        stringBuilder3.append(MainActivity.this.getResources().getString(R.string.mb));
                      //access$1900.setText(stringBuilder3.toString());
                        //.longValue()
                        int sum =MainActivity.this.mCleanUtils.getEmmcSize_gb();
                        String dd = MainActivity.this.mCleanUtils.getAvailableExternalMemorySize_gb(MainActivity.this.mContext);

                        stringBuilder2 = String.valueOf(dd);
                        cunchu_sum = String.valueOf(sum);

                        access$1900 = MainActivity.this.mCanuseSD;
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(MainActivity.this.getResources().getString(R.string.caunse));
                        stringBuilder3.append(stringBuilder2);
                        stringBuilder3.append("GB/");
                        stringBuilder3.append(cunchu_sum);
                        stringBuilder3.append(MainActivity.this.getResources().getString(R.string.gb));
                        access$1900.setText(stringBuilder3.toString());
                    }

                    return;
                case 3:
                    Log.d("chenSystemCheck","getTmpValue :start");
                    openview();
                    float tt=getTmpValue();
                   if (tt <= 0.01 || tt >100){
                                mTempretearLoading.clearAnimation();
                                mTempretearLoading.setBackground(getResources().getDrawable(R.drawable.wrong));
                               mTempretearDetection.setText(getResources().getText(R.string.fail_tm));
                        }else {
                            mTempretearLoading.clearAnimation();
                            mTempretearLoading.setBackground(getResources().getDrawable(R.drawable.right));
                            mTempretearDetection.setText(getResources().getText(R.string.succ_tm));
                          //  mTemperate.setText(tm);
                            stringBuildertv1 =new StringBuilder();
                            stringBuildertv1.append(tt);
                            stringBuildertv1.append("℃");
                            mTemperate.setVisibility(View.VISIBLE);
                            mTemperate.setText(stringBuildertv1);
                        }
                    Log.d("chenSystemCheck","getTmpValue :end");
                    setonepre();
                    return;
                case 4:
                    //开始检测触摸系统
                    Log.d("chenSystemCheck","checkTouch :start");
                     checkTouch();
                    return;
                case 5:
                    //开始检测光感系统
                    checkLightSense();
                    return;
                case 6:
                    //开始检测内置电脑
                    if (checkops_plugin()){
                        boolean opsStatus= checkOPS();
                        mPCLoading.clearAnimation();
                        mPCLoading.setBackground(getResources().getDrawable(opsStatus?R.drawable.right:R.drawable.wrong));
                        mPCDetection.setText(getResources().getText(opsStatus?R.string.isPC:R.string.opsin));
                        setfour();
                    }else {
                        mPCLoading.clearAnimation();
                        mPCLoading.setBackground(getResources().getDrawable(R.drawable.wrong));
                        mPCDetection.setText(getResources().getText(R.string.unPC));
                        setfour();
                    }


                    return;
                case 7777:
                    //检测光控系统
                    // MH_flag =true;
                    Log.d("chenSystemCheck", "handleMessage:检测光控系统  MH_flag = "+MH_flag);
                    if (MH_flag){
                          int mled =getLedValue();
                       // int mled =5;
                        Log.d("chenSystemCheck", "handleMessage:检测光控系统  ledvalue = "+mled);
                        if (mled == -1){
                            mled_load.clearAnimation();
                            mled_load.setBackground(getResources().getDrawable(R.drawable.wrong));
                            mLedDetection.setVisibility(View.VISIBLE);
                            mLedDetection.setText(R.string.led_detail);
                            next();
                        }else {
                            mled_load.clearAnimation();
                            mled_load.setBackground(getResources().getDrawable(R.drawable.right));
                            stringBuildertv1 =new StringBuilder();
                            stringBuildertv1.append(mled);
                            stringBuildertv1.append("LX");

                            mled_value.setVisibility(View.VISIBLE);
                            mLedDetection.setVisibility(View.VISIBLE);
                            mLedDetection.setText(R.string.led_item);
                            mled_value.setText(stringBuildertv1);
                            next();
                        }
                    }else {
                        next();
                    }
                    return;
                case 7 :
                    return;
                case 100:
                    mDetectionBtn.setBackgroundColor(getResources().getColor(R.color.bg_load));
                    mDetectionBtn.setEnabled(true);
                    MainActivity.this.mDetectionBtn.setText(R.string.again_detection);
                    return;
                case CHECK_SENSE:
                    Bundle bd = msg.getData();
                    int resIcon = bd.getInt("icon");
                    int result = bd.getInt("result");
                    mLightLoading.clearAnimation();
                    mLightLoading.setBackground(getResources().getDrawable(resIcon));
                    mLightDetection.setVisibility(View.VISIBLE);
                    mled_value.setVisibility(View.VISIBLE);
                    mLightDetection.setText(getResources().getText(result));
                    mled_value.setText(stringled);
                    setthree();
                    break;
                case 33:
                    Log.d("chenSystemCheck","checkTouchRet :"+checkTouchRet);
                    mTouchFrameLoading.clearAnimation();
                    mTouchFrameDetection.setVisibility(View.VISIBLE);
                    if(checkTouchRet){
                        mTouchFrameLoading.setBackground(getResources().getDrawable(R.drawable.right));
                        mTouchFrameDetection.setText(getResources().getText(R.string.istouch));
                    }else{
                        mTouchFrameLoading.setBackground(getResources().getDrawable(R.drawable.wrong));
                        mTouchFrameDetection.setText(getResources().getText(R.string.untouch));
                    }
                    settwopre();
                    break;
            }
        }
    };
    private Boolean MH_flag=false;
    private TextView mPercent;
    private TextView mCanuse;
    private TextView mAlreadyuse;
    private TextView mOptimizeone;
    private ValueAnimator mVanimprecent;
    private ValueAnimator mAanimatorSD;
    private ValueAnimator mAnimatorMemory;
    private CircleView mCircleOne;
    private TextView mTempretearLoading;
    private TextView mTouchFrameLoading;
    private TextView mLightLoading;
    private TextView mPCLoading;
    private TextView mPCDetail;
    private TextView mTempretearDetection;
    private TextView mTouchFrameDetection;
    private TextView mLightDetection;
    private TextView mPCDetection;
    private TextView mOptimizetwo;
    private TextView mCanuseSD;
    private TextView mAlreadyuseSD;
    private TextView mPercentSD;
    private CircleView mCircleTwo;
    private Animation mOperatingAnim;
    private DeleteCacheListener listener;
    private TextView mTemperate;
    private TextView mDegree;
    private Progress mProgress;
    private LinearLayout mchectsuccessed;
    private RelativeLayout mchecting;
    private Animation animation;
    private static  volatile boolean checkTouchRet = false;
    private TextView text_precent;
    private   StringBuilder textprexent_stringbulid;
    private long storge;
    private LinearLayout layout_led;
    private TextView mled_load;
    private TextView mled_degree;
    private TextView mled_value;

    private TextView mled_detail;
    private TextView mLedDetection;
    private boolean lightSenseEnable;
    private boolean lightSenseEnable1;
    private StringBuilder stringled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        initWindow();
        setContentView(R.layout.main_chect);
        this.mContext = getApplicationContext();
        mOperatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        inirighttview();
        initleftview();
        if (SystemProperties.get("ro.build.display.id").contains("CN8386_AH")){
         storge = 3000;
         layout_led.setVisibility(GONE);
         MH_flag=false;
        }else if (SystemProperties.get("ro.build.display.id").contains("CN8386_MH")){
         storge = 2000;
         MH_flag = true;
         layout_led.setVisibility(GONE);
        }else {
            storge = 3000;
            layout_led.setVisibility(GONE);
            MH_flag=false;
        }

        String romAvailableSize = getRomAvailableSize();
        Log.v("hggh", " AvailableSize:" + romAvailableSize);

        // layout_led.setVisibility(View.VISIBLE);
        initsetonclicklistener();
        startDetect();
    }
    private void startDetect() {
        this.mainHandler.sendEmptyMessage(3);
    }


    private void initWindow() {
        Window window = getWindow();
        // window.clearFlags(4);
        getWindow().clearFlags(4);
        WindowManager.LayoutParams attributes = window.getAttributes();

        attributes.gravity = 48;
        int integer = getResources().getDimensionPixelOffset(R.dimen.attributesyyy);
        //attributes.y=R.dimen.attributesyyy;
        attributes.y = integer;

        Log.d("hggh", "initWindow: attributes.y ="+integer);
        window.setAttributes(attributes);
        setFinishOnTouchOutside(true);

            }
    private void initleftview(){
        animation = AnimationUtils.loadAnimation(this, R.anim.tip);
//      ProgressView mprogressView = findViewById(R.id.mprogress);
//      mprogressView.startanim();
        mProgress = findViewById(R.id.progress);
        mchecting = findViewById(R.id.checting);
        mchectsuccessed = findViewById(R.id.chect_succed);

        layout_led = findViewById(R.id.layout_led);
        mTempretearLoading = (TextView) findViewById(R.id.screen_temperature_loading);
        mTouchFrameLoading = (TextView) findViewById(R.id.touchframe_loading);
        mLightLoading = (TextView) findViewById(R.id.light_loading);
        mPCLoading = (TextView) findViewById(R.id.pc_loading);
        mTouchFrameLoading.setVisibility(View.VISIBLE);
        mTouchFrameLoading.setVisibility(View.VISIBLE);
        mLightLoading.setVisibility(View.VISIBLE);
        mPCLoading.setVisibility(View.VISIBLE);
        mTempretearLoading.startAnimation(animation);
        mTouchFrameLoading.startAnimation(animation);
        mLightLoading.startAnimation(animation);
        mPCLoading.startAnimation(animation);

        mPCDetail = (TextView) findViewById(R.id.pc_detail);
        mTempretearDetection = findViewById(R.id.screen_temperature_normal);
        mTouchFrameDetection = (TextView) findViewById(R.id.touchframe_normal);
        mLightDetection = (TextView) findViewById(R.id.light_normal);
        mPCDetection = (TextView) findViewById(R.id.pc_normal);
        mTemperate = (TextView) findViewById(R.id.temperate);
        mDegree = (TextView) findViewById(R.id.degree);
        mLedDetection = findViewById(R.id.led_normal);

        mled_value = findViewById(R.id.led_value);
        mled_degree = findViewById(R.id.led_degree);
        mled_load = findViewById(R.id.led_loading);
        mled_detail = findViewById(R.id.led_detail);
        mled_load.startAnimation(animation);
    }

    private void inirighttview(){
        textprexent_stringbulid = new StringBuilder();
        text_precent = findViewById(R.id.precent_rl);
        mCircleViewColorOne = findViewById(R.id.circleview_color_one);
        mCircleViewColorTwo = findViewById(R.id.circleview_color_two);
        this.mCircleOne =  findViewById(R.id.circleview);
        mCanuseSD =  findViewById(R.id.canuse_sd);
        mAlreadyuseSD =  findViewById(R.id.alreadyuse_sd);
        mPercentSD =  findViewById(R.id.percent_sd);
        mPercent = findViewById(R.id.percent);
        mCanuse = findViewById(R.id.canuse);
        mOptimizeone = findViewById(R.id.optimizeone);
        mOptimizetwo = findViewById(R.id.optimizetwo);
        mAlreadyuse = findViewById(R.id.alreadyuse);
        mDetectionBtn = findViewById(R.id.detection);
        mCircleTwo = findViewById(R.id.circletwo);
        this.mCleanUtils = new CleanUtils(this);
        this.mCleanUtils.setDeleteFileListener(this);
    }
    private void initsetonclicklistener(){
        this.mDetectionBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detection:
                cancelAutoClose();
                    if (this.mIsDetecting) {
                        Log.d("hggh", "onClick1111 ");

                        mDetectionBtn.setBackgroundColor(getResources().getColor(R.color.bg_load));
                        this.mDetectionBtn.setEnabled(true);
                    this.mDetectionBtn.setText(R.string.again_detection);
                    stopDetect();
                    return;
                }

                Log.d("hggh", "onClick: 2222");
                this.mIsDetecting = true;
                mDetectionBtn.setBackgroundColor(getResources().getColor(R.color.view_balck));

                this.mDetectionBtn.setEnabled(false);
                this.mDetectionBtn.setText(R.string.stop);
                String str = "skin:wait:background";
                initloadinganim();

                this.mCanuseSD.setVisibility(View.INVISIBLE);
                this.mAlreadyuseSD.setVisibility(View.INVISIBLE);
                this.mPercentSD.setVisibility(View.INVISIBLE);
                mled_value.setVisibility(View.INVISIBLE);
                mCanuse.setVisibility(View.INVISIBLE);
                mPercent.setVisibility(View.INVISIBLE);
                mAlreadyuse.setVisibility(View.INVISIBLE);
                this.mCircleOne.setProgress(0);
                this.mCircleOne.reDraw();
                this.mCircleTwo.setProgress(0);
                this.mCircleTwo.reDraw();

                this.mOptimizeone.setText(R.string.waiting_detection);
                this.mOptimizetwo.setText(R.string.waiting_detection);

                this.mTempretearDetection.setText(R.string.waiting_detection);
                this.mTemperate.setVisibility(View.INVISIBLE);
                this.mDegree.setVisibility(View.INVISIBLE);
                this.mTouchFrameDetection.setText(R.string.waiting_detection);
                this.mLightDetection.setText(R.string.waiting_detection);
                this.mPCDetection.setText(R.string.waiting_detection);
                this.mled_value.setVisibility(View.INVISIBLE);
                this.mLedDetection.setText(R.string.waiting_detection);
                startDetect();
        }

    }
    public void  initloadinganim(){
        text_precent.setVisibility(View.VISIBLE);
        mTempretearLoading.setBackground(getResources().getDrawable(R.drawable.loading));
        mTouchFrameLoading.setBackground(getResources().getDrawable(R.drawable.loading));
        mLightLoading.setBackground(getResources().getDrawable(R.drawable.loading));
        mPCLoading.setBackground(getResources().getDrawable(R.drawable.loading));
        mled_load.setBackground(getResources().getDrawable(R.drawable.loading));
        mTouchFrameLoading.startAnimation(animation);
        mTempretearLoading.startAnimation(animation);
        mLightLoading.startAnimation(animation);
        mPCLoading.startAnimation(animation);
        mled_load.startAnimation(animation);
    }
    private  void next(){
        mainHandler.sendEmptyMessage(0);
    }
    private void stopDetect(){
        Log.d("hggh", "stopDetect: ");
                setzeropre();
                initloadinganim();
        if (this.mOptimizeone.getText().equals(getResources().getString(R.string.detecting))) {
            this.mOptimizeone.setText(R.string.waiting_detection);
            this.mAnimatorMemory.pause();
        } else if (this.mOptimizetwo.getText().equals(getResources().getString(R.string.detecting))) {
            this.mOptimizetwo.setText(R.string.waiting_detection);
            this.mAanimatorSD.pause();
        } else {
            String str = "skin:uncertain:background";
            if (this.mTempretearDetection.getText().equals(getResources().getString(R.string.detecting))) {
                this.mTempretearDetection.setText(R.string.uncertain);
                this.mTempretearLoading.clearAnimation();
                this.mTempretearLoading.setTag(str);
        //       SkinManager.getInstance().injectSkin(this.mTempretearLoading);
            } else if (this.mTouchFrameDetection.getText().equals(getResources().getString(R.string.detecting))) {
                this.mTouchFrameDetection.setText(R.string.uncertain);
                this.mTouchFrameLoading.clearAnimation();
                this.mTouchFrameLoading.setTag(str);
        //       SkinManager.getInstance().injectSkin(this.mTouchFrameLoading);
            } else if (this.mLightDetection.getText().equals(getResources().getString(R.string.detecting))) {
                this.mLightDetection.setText(R.string.uncertain);
                this.mLightLoading.clearAnimation();
                this.mLightLoading.setTag(str);
        //       SkinManager.getInstance().injectSkin(this.mLightLoading);
            } else if (this.mPCDetection.getText().equals(getResources().getString(R.string.detecting))) {
                this.mPCDetection.setText(R.string.uncertain);
                this.mPCLoading.clearAnimation();
                this.mPCLoading.setTag(str);
         //       SkinManager.getInstance().injectSkin(this.mPCLoading);
            }else if (this.mLedDetection.getText().equals(getResources().getString(R.string.detecting))){
                this.mLedDetection.setText(R.string.uncertain);
                this.mled_load.clearAnimation();
                this.mled_load.setTag(str);
            }
        }
        mDetectionBtn.setBackgroundColor(getResources().getColor(R.color.bg_load));
        this.mDetectionBtn.setEnabled(true);
        this.mDetectionBtn.setText(R.string.again_detection);

    }
    public void setzeropre(){
        mProgress.setProgress(0);
        text_precent.setVisibility(View.VISIBLE);
        textprexent_stringbulid.append(0);
        textprexent_stringbulid.append("%");
        text_precent.setText(textprexent_stringbulid);
        mProgress.reDraw();

    }

    public void  setonepre(){
        Log.d("chenSystemCheck","setonepre :start");
        ValueAnimator valueAnimator_one = ValueAnimator.ofInt(0, 15);
        valueAnimator_one.setDuration(1000);
        valueAnimator_one.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("chenSystemCheck","setonepre :start:onAnimationUpdate");
                TextView tv =MainActivity.this.text_precent;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append((Integer) animation.getAnimatedValue());
                stringBuilder.append("%");
                tv.setText(stringBuilder);
                mProgress.setProgress((Integer) animation.getAnimatedValue());
                mProgress.reDraw();
            }
        });
        valueAnimator_one.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("chenSystemCheck","setonepre :start:onAnimationEnd");
                    mainHandler.sendEmptyMessage(4);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator_one.start();
    }
    public void settwopre(){
        Log.d("chenSystemCheck","settwopre ");
        mVanimprecent =  ValueAnimator.ofInt(16,30);
        mVanimprecent.setDuration(1000);
        mVanimprecent.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator vanimation) {
                Log.d("chenSystemCheck","settwopre onAnimationUpdate");
                TextView tv =MainActivity.this.text_precent;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append((Integer) vanimation.getAnimatedValue());
                stringBuilder.append("%");
                tv.setText(stringBuilder);
                mProgress.setProgress((Integer) vanimation.getAnimatedValue());
                mProgress.reDraw();
            }
        });
       mVanimprecent.addListener(new Animator.AnimatorListener() {
           @Override
           public void onAnimationStart(Animator animation) {

           }

           @Override
           public void onAnimationEnd(Animator animation) {
               Log.d("chenSystemCheck","settwopre onAnimationEnd");
               MainActivity.this.mainHandler.sendEmptyMessageDelayed(5, 10);

           }

           @Override
           public void onAnimationCancel(Animator animation) {

           }

           @Override
           public void onAnimationRepeat(Animator animation) {

           }
       });
       mVanimprecent.start();
    }
    public void setthree(){
        ValueAnimator valueAnimator_three = ValueAnimator.ofInt(31, 45);
        valueAnimator_three.setDuration(1000);
        valueAnimator_three.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                TextView tv =MainActivity.this.text_precent;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append((Integer) animation.getAnimatedValue());
                stringBuilder.append("%");
                tv.setText(stringBuilder);
                mProgress.setProgress((Integer) animation.getAnimatedValue());
                mProgress.reDraw();
            }
        });
        valueAnimator_three.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mainHandler.sendEmptyMessageDelayed(6,10);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator_three.start();
    }
    public void setfour(){
        ValueAnimator valueAnimator_four = ValueAnimator.ofInt(45, 60);
        valueAnimator_four.setDuration(1000);
        valueAnimator_four.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                TextView tv =MainActivity.this.text_precent;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append((Integer) animation.getAnimatedValue());
                stringBuilder.append("%");
                tv.setText(stringBuilder);
                mProgress.setProgress((Integer) animation.getAnimatedValue());
                mProgress.reDraw();
            }
        });
        valueAnimator_four.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                    mainHandler.sendEmptyMessage(7777);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator_four.start();
    }

    public void setfiveanim(){
        ValueAnimator valueAnimator_five = ValueAnimator.ofInt(61, 80);
        valueAnimator_five.setDuration(1000);
        valueAnimator_five.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                TextView tv =MainActivity.this.text_precent;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append((Integer) animation.getAnimatedValue());
                stringBuilder.append("%");
                tv.setText(stringBuilder);
                mProgress.setProgress((Integer) animation.getAnimatedValue());
                mProgress.reDraw();
            }
        });
        valueAnimator_five.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator_five.start();

    }



    private void setsixaim()
    {
        ValueAnimator valueAnimator_six = ValueAnimator.ofInt(81, 100);
        valueAnimator_six.setDuration(1000);
        valueAnimator_six.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                TextView tv =MainActivity.this.text_precent;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append((Integer) animation.getAnimatedValue());
                stringBuilder.append("%");
                tv.setText(stringBuilder);
                mProgress.setProgress((Integer) animation.getAnimatedValue());
                mProgress.reDraw();
            }
        });
        valueAnimator_six.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator_six.start();
    }
    
    private void startAnimationPecentSD(String str) {
        this.mAanimatorSD = ValueAnimator.ofInt(new int[]{0, Integer.parseInt(str)});
        mAanimatorSD.setDuration(1000);
        this.mAanimatorSD.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextView access$500 = MainActivity.this.mPercentSD;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(valueAnimator.getAnimatedValue());
                stringBuilder.append("%");
                access$500.setText(stringBuilder.toString());
                MainActivity.this.mCircleTwo.setProgress(((Integer) valueAnimator.getAnimatedValue()).intValue());
                MainActivity.this.mCircleTwo.reDraw();
            }
        });
        this.mAanimatorSD.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                MainActivity.this.mainHandler.sendEmptyMessageDelayed(2, 10);
            }
        });
        this.mAanimatorSD.start();
    }

    private void startAnimationPecent(String str) {
        this.mAnimatorMemory = ValueAnimator.ofInt(new int[]{0, Integer.parseInt(str)});
        this.mAnimatorMemory.setDuration(1000);
        this.mAnimatorMemory.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextView access$300 = MainActivity.this.mPercent;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(valueAnimator.getAnimatedValue());
                stringBuilder.append("%");
                access$300.setText(stringBuilder.toString());
                Log.d("anim", "onAnimationUpdate:11  int i = "+((Integer) valueAnimator.getAnimatedValue()).intValue());
                MainActivity.this.mCircleOne.setProgress(((Integer) valueAnimator.getAnimatedValue()).intValue());
                MainActivity.this.mCircleOne.reDraw();
            }
        });
        this.mAnimatorMemory.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                MainActivity.this.mainHandler.sendEmptyMessageDelayed(1, 10);
            }
        });
        this.mAnimatorMemory.start();
    }

    private void cancelAutoClose() {
        if (this.mIsAutoClose) {
           this.mHandler.removeMessages(7);
            this.mIsAutoClose = false;
        }
    }
    private void notifyDectComplete() {
        if (this.mIsPowerOn) {
            Intent intent = new Intent();
            intent.setAction("com.hht.detect.DETECT_COMPLETED");
            sendBroadcast(intent);
            this.mIsPowerOn = false;
        }
    }

    @Override
    public void failed() {
        Log.d("hhh", "failed: ");
        this.mOptimizetwo.setText(this.mContext.getResources().getString(R.string.already_clean_all));
        stopview();
      // showOtherInfo();
    }

    private String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(MainActivity.this, blockSize * availableBlocks);
    }

    private void openview(){
        mchecting.setVisibility(View.VISIBLE);
        mchectsuccessed.setVisibility(GONE);

    }

    private void stopview(){
        text_precent.setVisibility(GONE);
        mchecting.setVisibility(GONE);
        mchectsuccessed.setVisibility(View.VISIBLE);
        mDetectionBtn.setBackgroundColor(getColor(R.color.bg_load));
        mDetectionBtn.setEnabled(true);
        mDetectionBtn.setText(R.string.again_detection);

    }

    private void showOtherInfo() {

        this.mTempretearDetection.setText(R.string.detecting);
        this.mTemperate.setVisibility(View.INVISIBLE);
        this.mDegree.setVisibility(View.INVISIBLE);
        if (this.mOperatingAnim != null) {
            this.mTempretearLoading.setTag("skin:loading:background");
         // SkinManager.getInstance().injectSkin(this.mTempretearLoading);
            this.mTempretearLoading.startAnimation(this.mOperatingAnim);
        }
        this.mainHandler.sendEmptyMessage(3);
    }

    //获得光控值
    public  int getLedValue(){
        int ledVaule = -1;
        int[] getLightCtlVals = TvCommonManager.getInstance().
                setTvosCommonCommand("GetLightCtlVal");
        if (getLightCtlVals!=null&& getLightCtlVals.length>0){
            ledVaule = getLightCtlVals[0];
        }
        return ledVaule;
    }
   //检查屏温
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

    public Boolean checkops_plugin() {
        boolean opsPlugIn = HHTOpsManager.getInstance().isOpsPlugIn();
       return opsPlugIn;
    }

    /**
     * @return flag true:ops正常 false:ops NG 0:ops接入 1:ops没有接入
     */
    public boolean checkOPS() {
        boolean flag = false;

        if (HHTSourceManager.getInstance().getSourcePlugStateByKey(HHTConstant.OPS).equals("IN")){
                flag = true;
         }else {
                flag = false;
         }
        return flag;
    }

    //检测触摸系统
    public void checkTouch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    short[] ret = CtvTvManager.getInstance().setTvosCommonCommand("CHECKTOUCH");
                    Log.d("chenSystemCheck","checkTouch :ret");
                    Log.d("chenSystemCheck", "zhu...checkTouchRet:" + ret[0]);
                    checkTouchRet = ret[0] == 1 ? true : false;
                    Log.d("chenSystemCheck","checkTouchRet :"+checkTouchRet);
                }   catch (CtvCommonException e) {
                    e.printStackTrace();
                }
                    mainHandler.sendEmptyMessageDelayed(33, 2000);
            }
        }).start();
    }

    // 检测光感系统

    public void checkLightSense() {
        boolean isLightSense = isled();
        int resIcon = R.drawable.normal;
        int result = R.string.unLightSense;
        if (!isLightSense) { // 是否支持光感硬件
            resIcon = R.drawable.wrong;
            result = R.string.led_detail;
        }  else { // 判断光感值
            int anInt = SystemProperties.getInt("persist.sys.eye_protection_mode", 0);
            Log.d("hggh", "checkLightSense: anInt ="+anInt);
            if(isLightSense && anInt == 1){ //光感正常
                resIcon = R.drawable.right;
                result = R.string.isLightSense;
                int ledValue = getLedValue();
                stringled =new StringBuilder();
                stringled.append(ledValue);
                stringled.append("LX");
            }else{ //光感未开启
                resIcon = R.drawable.wrong;
                result = R.string.unLightSense;
            }
        }
        Bundle bd = new Bundle();
        bd.putInt("icon",resIcon);
        bd.putInt("result",result);
        Message msg = new Message();
        msg.what = CHECK_SENSE;
        msg.setData(bd);
        mainHandler.sendMessageDelayed(msg,2000);
        //mHandler.sendEmptyMessageDelayed(CHECK_SENSE, 2000);
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessageDelayed(CHECK_SENSE, 2000);
            }
        }).start();*/
    }
    private boolean isled(){
        int l = getLedValue();
        Log.d("hggh", "isled: led  ="+l);
        if (l<=0){
            return false;
        }else {
            return true;
        }


    }
//    public void checkLightSense() {
//        boolean isLightSense = SystemProperties.get("service.light.sense.enable", "0")


    /**
     * 获得光感值
     * @return
     */
    private boolean getLightSense() {
       // int num = 0;
        //String numStr = SystemProperties.get("LIGHT_SENSE", "0");
        try {
           short[] lightenable= TvManager.getInstance().setTvosCommonCommand("GetLightSensorEnable");
           if(lightenable[0] == 1){
               return true;
           }else{
               return false;
           }
        } catch (TvCommonException e) {
            return false;
        }
/*        GetLightSensorEnable
        try {
            num = Integer.parseInt(numStr);
//            if (num != 0) {
//                num = num >= 180 ? 180 : num;
//                num = 100 - (int) (num / 180F * 100);
//                if (num == 50) {
//                    num = 51;
//                }
//            }
        } catch (Exception e) {
            num = 0;
            e.printStackTrace();
            return num;
        }
        return num;*/
    }
    @Override
    public void success(Long l) {
        Log.d("hhh", "success: ");
        if (l.longValue() < (1 << 10)) {
            TextView textView = this.mOptimizetwo;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.mContext.getResources().getString(R.string.optimized_success));
            stringBuilder.append(this.mContext.getResources().getString(R.string.clean));
            stringBuilder.append(l);
            stringBuilder.append(this.mContext.getResources().getString(R.string.kb));
            textView.setText(stringBuilder.toString());
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            TextView textView2 = this.mOptimizetwo;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(this.mContext.getResources().getString(R.string.optimized_success));
            stringBuilder2.append(this.mContext.getResources().getString(R.string.clean));
            stringBuilder2.append(decimalFormat.format(l.longValue() / ( (1 << 10))));
            stringBuilder2.append(this.mContext.getResources().getString(R.string.mb));
            textView2.setText(stringBuilder2.toString());
        }
       // showOtherInfo();
        stopview();
    }



}
