package com.ctv.ctvlauncher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ctv.ctvlauncher.MainActivity;
import com.ctv.ctvlauncher.R;
import com.ctv.ctvlauncher.adapter.DialogGrivdViewAdapter;
import com.ctv.ctvlauncher.bean.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DialogSeletc extends Dialog implements AdapterView.OnItemClickListener {
    public static final int CANCEL_DIALOG = 0X0;
    public static final int CHANGE_PHOTO =0X1;
    private Context mcontext;
    private View mView;
    private PackageManager pm;
    public  static List<AppInfo>dialgapp;
    private DialogGrivdViewAdapter dialogGrivdViewAdapter;
    private GridView dlg_gv;
    private  Handler mhandler;
    private ImageView mllview;
    private TextView mtextView;
    public Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CHANGE_PHOTO:
                    if(appIcon == null ||appName == null){
                        Log.d("hhh", "handleMessage:appicon或者appname == null ");
                    }else {
                        Log.d("hhh", "handleMessage: change UI");
                        if (mllview ==null || mtextView == null){
                            Log.d("hhh", "handleMessage: mllview == null 或者 mtextView == null");
                        }else {

                            Log.d("hggh", "dialog set icon:else ");
                            mllview.setBackground(appIcon);
                            mtextView.setText(appName);
                        }

                    }




                    break;
            }
        }
    };
    private Drawable appIcon;
    private String appName;
    public static int  dialog_id  =-1;
    public static int dialog_id_1 =-1;
    public static int dialog_id_2 =-1;
    public static int dialog_id_3 =-1;
    public static int dialog_id_4 =-1;
    public static String packagename_1;
    public static String packagename_2;
    public static String packagename_3;
    public static String packagename_4;


    int flag=-1;
    private String name1;
    public String packname;
    private MainActivity mainActivity;
    public DialogSeletc(@NonNull Context context, Handler handler, ImageView view, TextView textView, int dflag, MainActivity activity) {
        super(context,R.style.XDialog);
        this.mcontext=context;
        this.mainActivity=  activity;
        this.mhandler=handler;
        this.mllview=view;
        this.mtextView=textView;
        this.flag =dflag;
        String s = SystemProperties.get("ro.build.display.id");


        if (SystemProperties.get("ro.build.display.id").equals("CN8386_AH_EDU")){
            Log.d("hggh", "DialogSeletc: edu "+s);
        //    Log.d("hggh", "DialogSeletc:packagename_1 =  "+packagename_1);
            dialgapp=dialog_scanInstallApp();
        }else {
            Log.d("hggh", "DialogSeletc: meet "+s);
            dialgapp = dialog_scanApp_MEET();
        }


    }

    public DialogSeletc(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mcontext=context;

    }

    public  List<AppInfo> dialog_scanApp_MEET() {
        ArrayList<AppInfo> meeting_appinfos = new ArrayList<>();
        pm = mainActivity.getPackageManager();
        List<ApplicationInfo> installedPackages = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(installedPackages,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        for (ApplicationInfo app : installedPackages) {
            Intent intent = pm.getLaunchIntentForPackage(app.packageName);
            if (intent!=null){
                Log.d("hhh", "dialog_scanInstallApp-------    list each app.packagename");

                if (

                                    app.packageName.equals("com.example.cutcapture")
                          //      || app.packageName.equals("com.android.camera2")
                                ||app.packageName.equals("com.tv.filemanager")
                                || app.packageName.equals("com.android.cultraview.launcher.whiteboard")
                                || app.packageName.equals("com.android.cultraview.floatbuttonview")
                                || app.packageName.equals("com.protruly.floatwindowlib")
                                || app.packageName.equals("com.ctv.easytouch")
                                ||    app.packageName.equals("com.ctv.imageselect")
                                ||   app.packageName.equals("com.cultraview.annotate")
                                ||   app.packageName.equals("com.example.launch_2")
                                ||   app.packageName.equals("com.ctv.newlauncher")
                                ||  app.packageName.equals("com.ctv.ctvlauncher")
                                 ||app.packageName.equals("mstar.factorymenu.ui.hh")
            // ||    app.packageName.equals("com.android.toofifi")
                                ||   app.packageName.equals("com.inpor.fastmeetingcloud")
                                ||     app.packageName.equals("com.bozee.meetingmark")
               ||       app.packageName.equals("com.example.newmagnifier")
                                ||     app.packageName.equals("com.bozee.remoteserver")
                                ||      app.packageName.equals("com.bozee.registerclient")
                                ||     app.packageName.equals("com.bozee.dlna.server")
                                ||       app.packageName.equals("com.mstar.tv.tvplayer.ui")
                                ||        app.packageName.equals("com.mstar.netplayer")
                                ||       app.packageName.equals("mstar.factorymenu.ui")
                                ||    app.packageName.equals("com.ctv.annotation")
             ||     app.packageName.equals("com.dazzlewisdom.screenrec")
             ||    app.packageName.equals("com.dazzle.timer")
                 //               ||    app.packageName.equals("com.android.camera2")
                                ||     app.packageName.equals("com.android.tv.settings")
                                ||     app.packageName.equals("com.ctv.sourcemenu")
                                ||   app.packageName.equals("com.protruly.floatwindowlib.virtualkey")
                                ||   app.packageName.equals("com.android.tv.settings")

                                ||       app.packageName.equals(packagename_1)
                                ||       app.packageName.equals(packagename_2)
                                //||       app.packageName.equals(packagename_3)
                                ||       app.packageName.equals(packagename_4)
                ){
                    Log.d("hhh", "dialog_scanInstallApp---------   list ---packagename ="+app.packageName);
                    continue;


                } else {
                    meeting_appinfos.add(getAppInfo(app,pm));
                }
            }
        }
        return meeting_appinfos;
    }

    public List<AppInfo> dialog_scanInstallApp() {
        Log.d("hhh", "dialog_scanInstallApp: get listapp");
        List<AppInfo> appInfos = new ArrayList<>();
        // 获得PackageManager对象
        pm = mainActivity.getPackageManager();
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        for (ApplicationInfo app : listAppcations) {
            Intent intent = pm.getLaunchIntentForPackage(app.packageName);
            if (intent!=null){
                Log.d("hhh", "dialog_scanInstallApp-------    list each app.packagename");
                if (
//                        app.packageName.equals("com.ctv.settings")
//                        ||app.packageName.equals("com.cv.apk.manager")
//                        ||app.packageName.equals("com.ctv.welcome")

//                        ||app.packageName.equals("com.jrm.localmm")
//                        ||app.packageName.equals("com.mphotool.whiteboard")

                 //       app.packageName.equals("com.mysher.mtalk")

                 //      app.packageName.equals("com.android.camera2")
                       app.packageName.equals("mstar.factorymenu.ui.hh")
                        ||app.packageName.equals("com.tv.filemanager")
                        || app.packageName.equals("com.android.cultraview.launcher.whiteboard")
                        || app.packageName.equals("com.android.cultraview.floatbuttonview")
                        || app.packageName.equals("com.protruly.floatwindowlib")
                        || app.packageName.equals("com.ctv.easytouch")
                        ||    app.packageName.equals("com.ctv.imageselect")
                        ||   app.packageName.equals("com.cultraview.annotate")
                        ||   app.packageName.equals("com.example.launch_2")
                        ||   app.packageName.equals("com.ctv.newlauncher")
                        ||  app.packageName.equals("com.ctv.ctvlauncher")
                //        ||    app.packageName.equals("com.android.toofifi")
                        ||   app.packageName.equals("com.inpor.fastmeetingcloud")
                        ||     app.packageName.equals("com.bozee.meetingmark")
 // ||       app.packageName.equals("com.example.newmagnifier")
                        ||     app.packageName.equals("com.bozee.remoteserver")
                        ||      app.packageName.equals("com.bozee.registerclient")
                        ||     app.packageName.equals("com.bozee.dlna.server")
                        ||       app.packageName.equals("com.mstar.tv.tvplayer.ui")
                        ||        app.packageName.equals("com.mstar.netplayer")
                        ||       app.packageName.equals("mstar.factorymenu.ui")
                        ||    app.packageName.equals("com.ctv.annotation")
               //         ||     app.packageName.equals("com.dazzlewisdom.screenrec")
               //         ||    app.packageName.equals("com.dazzle.timer")
                 //       ||    app.packageName.equals("com.android.camera2")
                        ||     app.packageName.equals("com.android.tv.settings")
                        ||     app.packageName.equals("com.ctv.sourcemenu")
                        ||   app.packageName.equals("com.protruly.floatwindowlib.virtualkey")
                        ||   app.packageName.equals("com.android.tv.settings")

                         ||       app.packageName.equals(packagename_1)
                        ||       app.packageName.equals(packagename_2)
                        //||       app.packageName.equals(packagename_3)
                        ||       app.packageName.equals(packagename_4)
                ){
                        Log.d("hhh", "dialog_scanInstallApp---------   list ---packagename ="+app.packageName);
                           continue;


                } else {
                    appInfos.add(getAppInfo(app,pm));
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
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.mipmap.bt_whiteboard_focus));
        }else if (app.packageName .equals( "com.jrm.localmm")){
            Log.d("hhh", " photo : com.jrm.localmm");
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.mipmap.bt_media_icon_normal));
        }else if (app.packageName .equals("com.mysher.mtalk")){
            Log.d("hhh", "com.mysher.mtalk");
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.mipmap.bt_video_normal));
            appInfo.setAppName(mcontext.getString(R.string.video_phone));
        }else if (app.packageName .equals("com.android.toofifi")){
            Log.d("hhh", "com.android.toofifi ");
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.drawable.feitu_dispaly));
        }
        else if (app.packageName.equals("com.ctv.settings")){
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.mipmap.bt_settings_icon_normal));
        }else if (app.packageName.equals("com.jxw.launcher")){
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.drawable.wiedu));
            appInfo.setAppName(mcontext.getString(R.string.wiedu));
        }else if (app.packageName.equals("com.example.newmagnifier")){
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.drawable.fangda));
        }else if (app.packageName.equals("com.android.calculator2")){
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.drawable.bt_calculater_normal));
        }else if (app.packageName.equals("com.xingen.camera")){
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.drawable.takephoto));
            appInfo.setAppName(mcontext.getString(R.string.camera));
        }else if (app.packageName.equals("cn.wps.moffice_eng")){
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.drawable.bt_wps_normal));
        }else if (app.packageName.equals("com.dazzlewisdom.screenrec")){
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.drawable.screenboard));
        }else if (app.packageName.equals("com.dazzle.timer")){
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.drawable.tmier));
        }else if (app.packageName.equals("com.tencent.androidqqmail")){
            appInfo.setAppIcon(mcontext.getResources().getDrawable(R.mipmap.qqyouxiang));
            appInfo.setAppName(mcontext.getString(R.string.qqemail));
        }
        else {
            Log.d("hhh", "getAppInfo: photo of system ");
            Log.d("hhh", "getAppInfo: Packagename ="+app.packageName);
            appInfo.setAppIcon(app.loadIcon(pm));
        }
        appInfo.setPackName(app.packageName);//应用包名，用来卸载
        return appInfo;
    }
    protected DialogSeletc(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("hhh", "Dialog------onCreate: ");

        initdialog_app();
        setDialogview();

        WindowManager.LayoutParams lp = getWindow().getAttributes();


        lp.width = getContext().getResources().getInteger(R.integer.dia_w);
        lp.height= getContext().getResources().getInteger(R.integer.dia_h);
        lp.y= getContext().getResources().getInteger(R.integer.dia_y);
        lp.x= getContext().getResources().getInteger(R.integer.dia_x);

        getWindow().setAttributes(lp);

    }
    public void setDialogview(){

        mView = LayoutInflater.from(getContext()).inflate(R.layout.dlgeletc, null);
        setContentView(mView);
        dlg_gv = mView.findViewById(R.id.dlg_gv);

        dialogGrivdViewAdapter = new DialogGrivdViewAdapter(mcontext, dialgapp);
        dlg_gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        dlg_gv.setAdapter(dialogGrivdViewAdapter);
        dlg_gv.setOnItemClickListener(this);

    }
    public void initdialog_app(){
        Iterator<AppInfo> iterator = dialgapp.iterator();
        Log.d("hhh", "mainActivity.bd_packName =: "+mainActivity.bd_packName);
        while (iterator.hasNext()){

            AppInfo next = iterator.next();

            if (next.getPackName().equals(mainActivity.bd_packName)){
                iterator.remove();
            } else if ( next.getPackName().equals(mainActivity.bs_packName)){
                iterator.remove();
            } else if (next.getPackName().equals(mainActivity.bw_packName)){
                iterator.remove();
            }else if (next.getPackName().equals(mainActivity.bm_packName)){
                iterator.remove();
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (dialgapp == null){
            return;
        }
        dialog_id = -1;
        packname = null;
        if (flag==1){
            dialog_id_1 = position;
            packagename_1 =dialgapp.get(position).getPackName();
            Settings.System.putString(getContext().getContentResolver(),"Launch_one",packagename_1);
            Log.d("hggh", "onItemClick:   packagename_1 = "+packagename_1);
            mainActivity.bw_packName =packagename_1;
            Log.d("hggh", "onItemClick: flag == 1   bm_packName ="+packagename_1);

        }else if (flag == 2){
            dialog_id_2 = position;
            packagename_2 =dialgapp.get(position).getPackName();
            Settings.System.putString(getContext().getContentResolver(),"Launch_two",packagename_2);
            mainActivity.bd_packName=packagename_2;
            Log.d("hggh", "onItemClick: flag == 2   bm_packName ="+packagename_2);

        }else  if (flag == 3){
            dialog_id_3 = position;
            packagename_3 =dialgapp.get(position).getPackName();
            Settings.System.putString(getContext().getContentResolver(),"Launch_three",packagename_3);
            mainActivity.bs_packName =packagename_3;
            Log.d("hggh", "onItemClick: flag == 3   bm_packName ="+packagename_3);

        }else if (flag == 4){
            dialog_id_4 = position;
            packagename_4 =dialgapp.get(position).getPackName();
            Settings.System.putString(getContext().getContentResolver(),"Launch_four",packagename_4);
            mainActivity.bm_packName =packagename_4;
            Log.d("hggh", "onItemClick: flag == 4   bm_packName ="+packagename_4);
        }
        name1 = dialgapp.get(position).getPackName();


        Log.d("hhh", "onItemClick: packname  ="+ name1+"   position   ="+position);
        appIcon = dialgapp.get(position).getAppIcon();
        appName = dialgapp.get(position).getAppName();
        Log.d(" hggh ", "onItemClick: appName  ="+ appName);
            mhandler.sendEmptyMessage(CANCEL_DIALOG);
            handler.sendEmptyMessage(CHANGE_PHOTO);

    }
    public int getPosition(int flag){

            if (flag == 1){
                return dialog_id_1;
            }else if (flag == 2){
                return dialog_id_2;
            }else if (flag ==3 ){
                return dialog_id_3;
            }else if (flag ==4 ){
                return dialog_id_4;
            }

        return dialog_id;
    }
    public String getdialog_Packname(int flag){
        if (flag == 1) {
         //   return packagename_1;
            return    Settings.System.getString(getContext().getContentResolver(),"Launch_one");

        }else if (flag ==2 ){
            return packagename_2;
        }else if (flag ==3){
            return packagename_3;
        }else if (flag ==4){
            return packagename_4;
        }


        return packname;
    }


}
