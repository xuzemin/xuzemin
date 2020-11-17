package com.ctv.ctvlauncher;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ctv.ctvlauncher.adapter.AppPageAdapter;
import com.ctv.ctvlauncher.bean.AppInfo;
import com.ctv.ctvlauncher.dialog.DialogSeletc;
import com.ctv.ctvlauncher.fragment.FragmentAppGride;
import com.ctv.ctvlauncher.fragment.FragmentLauncher;

import com.ctv.ctvlauncher.reservice.NetworkStateReceiver;
import com.ctv.ctvlauncher.service.Theme1TimeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener,View.OnFocusChangeListener, View.OnLongClickListener {
    private static final String DOCUMENT_PKG = "com.jrm.localmm";
    private static final String TAG = "hggh";
    private static final String WHITEBOARD_PKG = "com.mphotool.whiteboard";
    private static final String SCREEN = "com.android.toofifi";
    private static final String NETWORKSET= "com.ctv.settings";
    public static final int CANCEL_DIALOG = 0X0;
    public static final int CHANGE_PHOTO =0X1;
    private FragmentAppGride fragmentAppGride;
    private FragmentLauncher fragmentLauncher;
    private View viewLauncher;
    private View viewMore;
    private int bd_position;
    private int bw_position;
    private int bm_position;
    private int bs_position;
    private View rootLayout;
    private AnimationSet animationSet;
    private PackageManager pm;
    private LanguageChangeReceicer mLanguageRecever = new LanguageChangeReceicer();
    private TimeBroadcastReceiver mReceiver = new TimeBroadcastReceiver();
    private DialogSeletc dialogSeletc;
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CANCEL_DIALOG:
                    Log.d(TAG, "handleMessage: visibile");
                    fragmentLauncher.textviewData.setVisibility(View.VISIBLE);
                    fragmentLauncher.date_apm.setVisibility(View.VISIBLE);
                    dialogSeletc.dismiss();
                    break;
                case CHANGE_PHOTO:
                    break;
                default:
            }
        }
    };
    public View llview;
    private ImageView bd_im;
    private TextView bd_tv;
    private ImageView bm_im;
    private TextView bm_tv;
    private ImageView bs_im;
    private TextView bs_tv;
    private ImageView bw_im;
    private TextView bw_tv;
    private NetworkStateReceiver mNetworkStateReceiver = new NetworkStateReceiver();
    public static String bd_packName ;
    public static String bw_packName ;
    public static String bs_packName ;
    public static String bm_packName ;
    private List<AppInfo> main_app;
    private List<AppInfo> main_appInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ctvtheme", "oncreate: ");
        setContentView(R.layout.activity_main);
        this.viewLauncher = findViewById(R.id.layout_launcher);
        this.viewMore = findViewById(R.id.layout_grid);
        //starttimeservice();
        pm = getPackageManager();
        initview();
        this.fragmentLauncher = new FragmentLauncher(this.viewLauncher);
        if(fragmentAppGride == null){
            this.fragmentAppGride = new FragmentAppGride(this.viewMore,this,this,this);
        }
        this.fragmentLauncher.setOnButtonClickListener(this);
        this.fragmentLauncher.setButtonFooucesListener(this);
        this.fragmentLauncher.setonlongbuttonclicklistener(this);
        this.fragmentAppGride.set_gdonButtonCLickListener(this);
        rootLayout = findViewById(R.id.main);

        initamin();
        registerLoginBroadcast();
    }

    private void initamin(){
        animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.15f, 1.0f, 1.15f, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setDuration(100);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setFillAfter(true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        inithonmelable();
       // starttimeservice();
        //initnamelabel();
        if(fragmentAppGride == null){
            Log.d("ctvtheme", "onResume: fragmentAppGride == null");
            this.fragmentAppGride = new FragmentAppGride(this.viewMore,this,this,this);
        }
        final Drawable drawable = WallpaperManager.getInstance(this).getDrawable();
        rootLayout.setBackground(drawable);
        Log.i("gyx","onResume");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (viewLauncher.getVisibility() == View.INVISIBLE){
            exitViewMore();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gd_bottom_ll:
                exitViewMore();
                break;
            case R.id.bottom_ll:
                enterViewMore();
                break;
            case R.id.button_more:

                enterSetting();
                break;
            case R.id.button_whiteboard:
                enterWhiteboard();
                break;
            case R.id.button_document:

                enterDocument();
                break;
            case R.id.button_sharescreen:

                enterNetMeeting();
                break;
            case R.id.network_state_iv:
                enterNetWork();
                break;
            default:

        }

    }

    public void initview(){
        bm_im = findViewById(R.id.bm_im);
        bm_tv = findViewById(R.id.bm_tv);
        bd_im = findViewById(R.id.bd_im);
        bd_tv = findViewById(R.id.bd_tv);
        bs_im = findViewById(R.id.bs_im);
        bs_tv = findViewById(R.id.bs_tv);
        bw_im = findViewById(R.id.bw_im);
        bw_tv = findViewById(R.id.bw_tv);


        if (Settings.System.getString(getContentResolver(),"Launch_one") == null){
            Settings.System.putString(getContentResolver(),"Launch_one","com.mphotool.whiteboard");
            bw_packName =  Settings.System.getString(getContentResolver(),"Launch_one");
        }else {
            bw_packName =  Settings.System.getString(getContentResolver(),"Launch_one");
            Log.d(TAG, "initview: launch_one  ="+Settings.System.getString(getContentResolver(),"Launch_one") );
        }
        if (Settings.System.getString(getContentResolver(),"Launch_two") == null){
            Settings.System.putString(getContentResolver(),"Launch_two","com.jrm.localmm");
            bd_packName =Settings.System.getString(getContentResolver(),"Launch_two");
        }else{
            bd_packName =Settings.System.getString(getContentResolver(),"Launch_two");
        }

        if (Settings.System.getString(getContentResolver(),"Launch_three")== null){
            Log.d(TAG, "initview: Launch_three  ="+null);
            Settings.System.putString(getContentResolver(),"Launch_three","com.android.toofifi");
            bs_packName = Settings.System.getString(getContentResolver(),"Launch_three");
        }else{
            bs_packName = Settings.System.getString(getContentResolver(),"Launch_three");
            Log.d(TAG, "initview: Launch_three  ="+bs_packName );
        }

        if (Settings.System.getString(getContentResolver(),"Launch_four") == null){
            Settings.System.putString(getContentResolver(),"Launch_four","com.ctv.settings");
            bm_packName=Settings.System.getString(getContentResolver(),"Launch_four");
        }else {
            bm_packName=Settings.System.getString(getContentResolver(),"Launch_four");
        }
        Log.d(TAG, "initview: s1 ="+bd_packName+"   s2 ="+bw_packName+"   s3 ="+bs_packName+"    s4="+bm_packName);
        main_appInfos = dialog_scanInstallApp(pm);
    }

    private void inithonmelable(){
        for (AppInfo appInfo : main_appInfos){
            if (appInfo.getPackName().equals("com.mysher.mtalk")){
                appInfo.setAppName(getResources().getString(R.string.video_phone));
                appInfo.setAppIcon(getResources().getDrawable(R.mipmap.bt_video_normal));
            }
            if (appInfo.getPackName().equals("com.android.toofifi")){
                appInfo.setAppIcon(getResources().getDrawable(R.drawable.feitu_dispaly));
            }

            if (appInfo.getPackName().equals(bw_packName)){
                bw_im.setBackground(appInfo.getAppIcon());
                bw_tv.setText(appInfo.getAppName());
            }
            if (appInfo.getPackName().equals(bd_packName)){
                bd_im.setBackground(appInfo.getAppIcon());
                bd_tv.setText(appInfo.getAppName());
            }
            if (appInfo.getPackName().equals(bs_packName)){
                bs_im.setBackground(appInfo.getAppIcon());
                bs_tv.setText(appInfo.getAppName());
            }
            if (appInfo.getPackName().equals(bm_packName)){
                bm_im.setBackground(appInfo.getAppIcon());
                bm_tv.setText(appInfo.getAppName());
            }
        }

    }

    public List<AppInfo> dialog_scanInstallApp(PackageManager pm) {
        Log.d(TAG, "dialog_scanInstallApp: get listapp");
        List<AppInfo> appInfos = new ArrayList<>();
        // 获得PackageManager对象

        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        Log.d(TAG, "dialog_scanInstallApp: listAppcations.size ="+listAppcations.size());
        for (ApplicationInfo app : listAppcations) {
            Intent intent = pm.getLaunchIntentForPackage(app.packageName);
            if (intent!=null){
                Log.d("hhh", "dialog_scanInstallApp-------    list each app.packagename");
                if (
                                 app.packageName.equals(bm_packName)
                        ||       app.packageName.equals(bd_packName)
                        ||       app.packageName.equals(bs_packName)
                        ||       app.packageName.equals(bw_packName)
                ){
                    Log.d("hhh", "dialog_scanInstallApp---------   list ---packagename ="+app.packageName);

                    appInfos.add(getAppInfo(app,pm));

                } else {

                }
            }
        }
        return appInfos;
    }
    private AppInfo getAppInfo(ApplicationInfo app, PackageManager pm) {
        Log.d("hhh", "-----getAppInfo---- set photo");
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName(pm.getApplicationLabel(app).toString());//应用名称
        if (app.packageName .equals("com.mphotool.whiteboard")){
            Log.d("hhh", "photo: com.mphotool.whiteboard ");
            appInfo.setAppIcon(getResources().getDrawable(R.mipmap.bt_whiteboard_focus));
        }else if (app.packageName .equals( "com.jrm.localmm")){
            Log.d("hhh", " photo : com.jrm.localmm");
            appInfo.setAppIcon(getResources().getDrawable(R.mipmap.bt_media_icon_normal));
        }else if (app.packageName .equals("com.mysher.mtalk")){
            Log.d("hhh", "com.mysher.mtalk ");
            appInfo.setAppIcon(getResources().getDrawable(R.mipmap.bt_video_normal));
        }else if (app.packageName.equals("com.ctv.settings")){
            appInfo.setAppIcon(getResources().getDrawable(R.mipmap.bt_settings_icon_normal));
        }else if (app.packageName.equals("com.jxw.launcher")){
            appInfo.setAppIcon(getResources().getDrawable(R.drawable.wiedu));
            appInfo.setAppName(getString(R.string.wiedu));
        }else if (app.packageName.equals("com.example.newmagnifier")){
            appInfo.setAppIcon(getResources().getDrawable(R.drawable.fangda));
        }else if (app.packageName.equals("com.android.calculator2")){
            appInfo.setAppIcon(getResources().getDrawable(R.drawable.bt_calculater_normal));
        }else if (app.packageName.equals("com.xingen.camera")){
            appInfo.setAppIcon(getResources().getDrawable(R.drawable.takephoto));
            appInfo.setAppName(getString(R.string.camera));
        }else if (app.packageName.equals("cn.wps.moffice_eng")){
            appInfo.setAppIcon(getResources().getDrawable(R.drawable.bt_wps_normal));
        }else if (app.packageName.equals("com.dazzlewisdom.screenrec")){
            appInfo.setAppIcon(getResources().getDrawable(R.drawable.screenboard));
        }else if (app.packageName.equals("com.dazzle.timer")){
            appInfo.setAppIcon(getResources().getDrawable(R.drawable.tmier));
        }else if(app.packageName.equals("com.tencent.androidqqmail")){
            appInfo.setAppIcon(getResources().getDrawable(R.mipmap.qqyouxiang));
            appInfo.setPackName(getResources().getString(R.string.qqemail));
        }
        else {
            Log.d("hhh", "getAppInfo: photo of system ");
            Log.d("hhh", "getAppInfo: Packagename ="+app.packageName);
            appInfo.setAppIcon(app.loadIcon(pm));
        }
        appInfo.setPackName(app.packageName);//应用包名，用来卸载
        return appInfo;
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.button_more:
//               bm_im = v.findViewById(R.id.bm_im);
//               bm_tv = v.findViewById(R.id.bm_tv);
                onCreatDialog(bm_im,bm_tv,4);
                break;
            case R.id.button_document:
//               bd_im = v.findViewById(R.id.bd_im);
//               bd_tv = v.findViewById(R.id.bd_tv);
                onCreatDialog(bd_im, bd_tv,2);
                break;
            case R.id.button_sharescreen:

//               bs_im = v.findViewById(R.id.bs_im);
//               bs_tv = v.findViewById(R.id.bs_tv);
                onCreatDialog(bs_im, bs_tv,3);

                Log.d(TAG, "onLongClick: sharescreen");
                break;
            case R.id.button_whiteboard:

//               bw_im = v.findViewById(R.id.bw_im);
//               bw_tv = v.findViewById(R.id.bw_tv);
                onCreatDialog(bw_im, bw_tv,1);

                Log.d(TAG, "onLongClick: whiteboard");
                break;
            default:
        }
        return true;
    }

    private void onCreatDialog(ImageView view,TextView textView,int flag){

        Log.d(TAG, "onCreatDialog: ");
        if (this.viewLauncher.getVisibility() == View.VISIBLE){
            this.fragmentLauncher.textviewData.setVisibility(View.GONE);
            this.fragmentLauncher.date_apm.setVisibility(View.GONE);
        }

        dialogSeletc = new DialogSeletc(this,handler,view,textView,flag,this);
        dialogSeletc.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                handler.sendEmptyMessage(CANCEL_DIALOG);
            }
        });
        dialogSeletc.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogSeletc.show();
    }


    class LanguageChangeReceicer extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("hggh", " LanguageChangeReceicer onReceive: ");
            if((intent.getAction()).equals(Intent.ACTION_LOCALE_CHANGED)){
                Log.d("hggh", "onReceive: language changesucced");
                //  System.exit(0);
            }

        }
    }


    class TimeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            int network = intent.getIntExtra("network", 0);

            if (fragmentLauncher != null) {
                Log.d("TimeUtil", "onReceive:network ="+network);
                fragmentLauncher.updateTime( network );
            }
        }
    }
    private void registerLoginBroadcast() {
        IntentFilter intentFilter = new IntentFilter(Theme1TimeService.ACTION_TYPE_TIME);
        IntentFilter mlanguiagechangeInten = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLanguageRecever,mlanguiagechangeInten);

        IntentFilter filter = new IntentFilter();

        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);
    }

    private void unRegisterLoginBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLanguageRecever);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNetworkStateReceiver);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        String string = Settings.System.getString(getContentResolver(), Settings.System.TIME_12_24);
        fragmentLauncher.updateTime();

    }

    @Override
    protected void onPause() {
        super.onPause();
        resetDelete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterLoginBroadcast();
    }

    private void enterDocument() {
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(bd_packName);
        if (launchIntentForPackage == null) {
            Toast.makeText(this, "未安装此应用，请先安装", Toast.LENGTH_LONG).show();
        } else {
            startActivity(launchIntentForPackage);
        }
    }
    private void enterNetWork(){
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(NETWORKSET);
        if (launchIntentForPackage == null) {
            Toast.makeText(this, "未安装此应用，请先安装", Toast.LENGTH_LONG).show();
        } else {
            startActivity(launchIntentForPackage);
        }
    }
    private void enterSetting(){
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(bm_packName);

        if (launchIntentForPackage == null) {
            Toast.makeText(this, "未安装设置，请先安装设置应用", Toast.LENGTH_LONG).show();
        } else {
            startActivity(launchIntentForPackage);
        }
    }

    private void enterWhiteboard() {
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(bw_packName);
        if (launchIntentForPackage == null) {
            Toast.makeText(this, "未安装此应用，请先安装", Toast.LENGTH_LONG).show();
        } else {
            startActivity(launchIntentForPackage);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 4 || this.viewLauncher.getVisibility() ==View.VISIBLE) {
            return super.onKeyUp(keyCode, event);
        }
        if(this.viewMore.getVisibility() == View.VISIBLE){
            if(AppPageAdapter.isDelete){
                resetDelete();
            }else{
                exitViewMore();
            }
        }
        return true;
    }

    public void resetDelete(){
        AppPageAdapter.isDelete = false;
        fragmentAppGride.updateapp();
    }

    private void exitViewMore() {
        this.viewLauncher.setVisibility(View.VISIBLE);
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.fragment_leftin);
        Animation loadAnimation2 = AnimationUtils.loadAnimation(this, R.anim.fragment_rightout);
        loadAnimation2.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
                resetDelete();
            }

            public void onAnimationEnd(Animation animation) {
                MainActivity.this.viewMore.setVisibility(View.INVISIBLE);
            }
        });
        this.viewLauncher.startAnimation(loadAnimation);
        this.viewMore.startAnimation(loadAnimation2);
        this.viewMore.requestFocus();
    }

    private void enterViewMore() {
        this.viewMore.setVisibility(View.VISIBLE);
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.fragment_rightin);
        Animation loadAnimation2 = AnimationUtils.loadAnimation(this, R.anim.fragment_leftout);
        loadAnimation2.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                MainActivity.this.viewLauncher.setVisibility(View.INVISIBLE);
            }
        });
        this.viewLauncher.startAnimation(loadAnimation2);
        this.viewMore.startAnimation(loadAnimation);
        this.fragmentAppGride.registerLoginBroadcast();
        this.fragmentAppGride.updateapp();
        this.viewLauncher.requestFocus();
        Log.d("startgride", "打开grid");
    }

    private void enterNetMeeting() {
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(bs_packName);
        if (launchIntentForPackage == null) {
            Toast.makeText(this, "未安装此应用，请先安装后使用", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "enterNetMeeting:::::::::::::::::::::::");
        startActivity(launchIntentForPackage);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
      //  Log.e(TAG,"v"+v.getAutofillId());
        Log.e(TAG,"v"+v.getId());
        if(hasFocus){
            v.startAnimation(animationSet);
        }else{
            v.clearAnimation();
        }
    }
}
