package com.android.androidlauncher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.androidlauncher.utils.MyConstant;
import com.android.androidlauncher.view.GridItem;
import com.android.androidlauncher.view.GridViewAdapter;
import com.android.androidlauncher.view.LauncherVideoView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private GridViewAdapter mGridViewAdapter = null;
    private ArrayList<GridItem> mGridData = null;
    private GridView gameGird;
    private ImageView img;
    private static LauncherVideoView videoView;
    private static Thread thread = null;
    private LinearLayout ll_video;
    private RelativeLayout ll_game;
    private static int REQUEST_EXTERNAL_STRONGE = 1;
    private PackageManager packageManager;
    private Intent intent;
    private static ArrayList<String> packageNameList;
    private static ArrayList<String> VideoNameList;
    private static ArrayList<String> ImageNameList;
    private ArrayList<Map<String, Object>> dataList;
    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MyConstant.EVENT_START_VIDEO:
                    startPlay();
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_main);
        gameGird = findViewById(R.id.gameGird);
        videoView = findViewById(R.id.videoView);
        ll_video = findViewById(R.id.ll_video);
        img = findViewById(R.id.img);
        ll_game = findViewById(R.id.ll_game);
        initData();

        mGridViewAdapter = new GridViewAdapter(this, R.layout.gridview_item, mGridData);
        gameGird.setAdapter(mGridViewAdapter);
        gameGird.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyConstant.debugLog("onItemClick");
                MyConstant.isResetPlay = true;
                packageManager = getPackageManager();
                /**获得Intent*/
                intent = packageManager.getLaunchIntentForPackage(packageNameList.get(position)); //com.xx.xx是我们获取到的包名 
                if(intent!=null){
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"包名不存在",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ll_video.setOnClickListener(this);
        ll_game.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STRONGE);
        }//REQUEST_EXTERNAL_STRONGE是自定义个的一个对应码，用来验证请求是否通过
        else {
            MyConstant.debugLog(MyConstant.isInit+""+videoView.isPlaying());
            if(MyConstant.isInit ) {
                ll_video.setVisibility(View.VISIBLE);
                ll_game.setVisibility(View.GONE);
                MyConstant.CurrentNumber = 0;
                MyConstant.isVideoPlay = true;
                videoView.start();
            }else{
                setVideoPath();
                MyConstant.isInit = true;
                startPlay();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//根据请求是否通过的返回码进行判断，然后进一步运行程序
//        if (grantResults.length > 0 && requestCode == REQUEST_EXTERNAL_STRONGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            setVideoPath();
//            handler.sendEmptyMessage(MyConstant.EVENT_START_VIDEO);
//        }

    }

    @SuppressLint("NewApi")
    public void setVideoPath(){
        MyConstant.debugLog("Video"+MyConstant.Current_Video);
        if(VideoNameList!=null && VideoNameList.size()>0){
            if(MyConstant.Current_Video < VideoNameList.size()-1){
                videoView.setVideoPath(VideoNameList.get(MyConstant.Current_Video));
            }else{
                MyConstant.Current_Video = 0;
                videoView.setVideoPath(VideoNameList.get(0));
            }
        }
        if(ImageNameList!=null && ImageNameList.size()>0){
            if(MyConstant.Current_Image < ImageNameList.size()-1){
                img.setImageURI(Uri.fromFile(new File(ImageNameList.get(MyConstant.Current_Image))));
            }else{
                MyConstant.Current_Image = 0;
                img.setImageURI(Uri.fromFile(new File(ImageNameList.get(0))));
            }
        }
    }

    public void startPlay() {
        ll_video.setVisibility(View.VISIBLE);
        ll_game.setVisibility(View.GONE);
        MyConstant.CurrentNumber = 0;
        MyConstant.isVideoPlay = true;
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                MyConstant.debugLog("播放完毕");
                if(MyConstant.Current_Video < 7){
                    MyConstant.Current_Video ++;
                    MyConstant.Current_Image ++;
                }else{
                    MyConstant.Current_Video = 0;
                }
                setVideoPath();
                videoView.start();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    @SuppressLint("NewApi")
    void initData() {
        packageNameList = new ArrayList<>();
        packageNameList.add("com.carrot.iceworld");
        packageNameList.add("com.gameloft.android.ANMP.GloftA8CN");
        packageNameList.add("com.ipeaksoft.pitDadGame2");
        packageNameList.add("com.yodo1.sniper3dv2.uc");
        packageNameList.add("com.DefiantDev.SkiSafari");
        packageNameList.add("com.szgd.GodBeastKingKong.egame");
        //图标
        int icno[] = { R.mipmap.luobo, R.mipmap.kuangye, R.mipmap.shishang,
                R.mipmap.juji, R.mipmap.huaxue, R.mipmap.shenshou,
        };
        //图标下的文字
        String name[]={"保卫萝卜2","狂野飚车","史上最坑爹游戏2","狙击行动","滑雪大冒险","神兽金刚"};
        dataList = new ArrayList<>();
        for (int i = 0; i <icno.length; i++) {
            Map<String, Object> map=new HashMap<>();
            map.put("img", icno[i]);
            map.put("text",name[i]);
            dataList.add(map);
        }
        mGridData = new ArrayList<>();
        for (int i=0; i<icno.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(name[i]);
            item.setImage(getDrawable(icno[i]));
            mGridData.add(item);
        }
        getAPKPath();
        getVideoPath();
        getPhotoVideoPath();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_video:
                startThread();
                break;
            case R.id.ll_game:
                MyConstant.debugLog("ll_game");
                MyConstant.isResetPlay = true;
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        MyConstant.CurrentNumber = 0;
        MyConstant.isVideoPlay = false;
        videoView.pause();
        ll_video.setVisibility(View.GONE);
        ll_game.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void startThread(){
        videoView.pause();
        MyConstant.isVideoPlay = false;
        ll_video.setVisibility(View.GONE);
        ll_game.setVisibility(View.VISIBLE);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!MyConstant.isVideoPlay){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(MyConstant.isResetPlay){
                        MyConstant.CurrentNumber = 0;
                        MyConstant.isResetPlay = false;
                    }
                    if(MyConstant.CurrentNumber >= MyConstant.OUTTIME){
                        MyConstant.CurrentNumber = 0;
                        handler.sendEmptyMessage(MyConstant.EVENT_START_VIDEO);
                        MyConstant.isVideoPlay = true;
                    }else{
                        MyConstant.CurrentNumber ++;
                    }
                    MyConstant.debugLog("MyConstant.CurrentNumber"+MyConstant.CurrentNumber);
                }
            }
        });
        thread.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MyConstant.debugLog( "onKeyDown: keyCode -- " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                MyConstant.CurrentNumber = 0;
                MyConstant.debugLog("KeyEvent.KEYCODE_BACK");
//                break;
                return true;//拦截事件
            case KeyEvent.KEYCODE_MENU:
                MyConstant.debugLog("KeyEvent.KEYCODE_MENU");
                return true;
            case KeyEvent.KEYCODE_HOME:
                MyConstant.CurrentNumber = 0;
                MyConstant.debugLog("KeyEvent.KEYCODE_HOME");
                // 收不到
                return true;
            case KeyEvent.KEYCODE_APP_SWITCH:
                MyConstant.debugLog("KeyEvent.KEYCODE_APP_SWITCH");
                // 收不到
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("NewApi")
    public String getAPKPath(){
        File path = new File(MyConstant.ApkDir);
        if (Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)) {
            File[] files = path.listFiles();// 读取文件夹下文件
            getApkName(files);
        }
        return null;
    }

    protected void getApkName(File[] files) {
        if(files != null){
            PackageManager pm = getPackageManager();
            packageNameList = new ArrayList<>();
            mGridData = new ArrayList<>();
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (file.getName().endsWith(".apk")){
                        PackageInfo pi = pm.getPackageArchiveInfo(MyConstant.ApkDir+file.getName(), 0);
                        ApplicationInfo applicationInfo = pi.applicationInfo;
                        MyConstant.debugLog(applicationInfo.packageName);
                        packageNameList.add(applicationInfo.packageName);
                        try {
                            String Name=pm.getApplicationLabel(pm.getApplicationInfo(applicationInfo.packageName,PackageManager.GET_META_DATA)).toString();
                            MyConstant.debugLog("Name"+Name);
                            GridItem item = new GridItem();
                            item.setTitle(Name);
                            item.setImage(applicationInfo.loadIcon(pm));
                            mGridData.add(item);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    }

    public String getVideoPath(){
        File path = new File(MyConstant.VideoDir);
        if (Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)) {
            File[] files = path.listFiles();// 读取文件夹下文件
            getVideoName(files);
        }
        return null;
    }

    protected void getVideoName(File[] files) {
        if(files != null){
            VideoNameList = new ArrayList<>();
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (file.getName().endsWith(".mp4")){
                        VideoNameList.add(MyConstant.VideoDir+file.getName());
                        MyConstant.debugLog(MyConstant.VideoDir+file.getName()+"");
                    }
                }
            }
        }
    }
    public String getPhotoVideoPath(){
        File path = new File(MyConstant.ImgDir);
        if (Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)) {
            File[] files = path.listFiles();// 读取文件夹下文件
            getPhotoName(files);
        }
        return null;
    }

    protected void getPhotoName(File[] files) {
        if(files != null){
            ImageNameList = new ArrayList<>();
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (file.getName().endsWith(".jpg")){
                        ImageNameList.add(MyConstant.ImgDir+file.getName());
                        MyConstant.debugLog(MyConstant.ImgDir+file.getName()+"");
                    }
                }
            }
        }
    }
}
