package com.android.androidlauncher;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.androidlauncher.utils.MyConstant;
import com.android.androidlauncher.view.LauncherVideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private GridView gameGird;
    private static LauncherVideoView videoView;
    private SimpleAdapter adapter;
    private static Thread thread = null;
    private LinearLayout ll_video,ll_game;
    private static int REQUEST_EXTERNAL_STRONGE = 1;
    private PackageManager packageManager;
    private Intent intent;
    private static ArrayList<String> packageNameList;
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
        ll_game = findViewById(R.id.ll_game);
        initData();
        String[] from={"img","text"};
        int[] to={R.id.img,R.id.text};



        adapter=new SimpleAdapter(this, dataList, R.layout.gridview_item, from, to);
        gameGird.setAdapter(adapter);
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
//        getSDPath();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STRONGE);
        }//REQUEST_EXTERNAL_STRONGE是自定义个的一个对应码，用来验证请求是否通过
        else {
            setVideoPath();
            handler.sendEmptyMessage(MyConstant.EVENT_START_VIDEO);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//根据请求是否通过的返回码进行判断，然后进一步运行程序
        if (grantResults.length > 0 && requestCode == REQUEST_EXTERNAL_STRONGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setVideoPath();
            handler.sendEmptyMessage(MyConstant.EVENT_START_VIDEO);
        }

    }

    public void setVideoPath(){
        MyConstant.debugLog("Video"+MyConstant.Current_Video);
        File file = null;
        switch (MyConstant.Current_Video){
            case 0:
                MyConstant.VideoPath = MyConstant.VideoDir+"video.mp4";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ll_game.setBackground(getDrawable(R.mipmap.background));
                }
                break;
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ll_game.setBackground(getDrawable(R.mipmap.background1));
                }
                MyConstant.VideoPath = MyConstant.VideoDir+"video"+MyConstant.Current_Video+".mp4";
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ll_game.setBackground(getDrawable(R.mipmap.background2));
                }
                MyConstant.VideoPath = MyConstant.VideoDir+"video"+MyConstant.Current_Video+".mp4";
                break;
            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ll_game.setBackground(getDrawable(R.mipmap.background3));
                }
                MyConstant.VideoPath = MyConstant.VideoDir+"video"+MyConstant.Current_Video+".mp4";
                break;
            case 4:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ll_game.setBackground(getDrawable(R.mipmap.background4));
                }
                MyConstant.VideoPath = MyConstant.VideoDir+"video"+MyConstant.Current_Video+".mp4";
                break;
            case 5:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ll_game.setBackground(getDrawable(R.mipmap.background1));
                }
                MyConstant.VideoPath = MyConstant.VideoDir+"video"+MyConstant.Current_Video+".mp4";
                break;
            case 6:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ll_game.setBackground(getDrawable(R.mipmap.background2));
                }
                MyConstant.VideoPath = MyConstant.VideoDir+"video"+MyConstant.Current_Video+".mp4";
                break;
            case 7:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ll_game.setBackground(getDrawable(R.mipmap.background3));
                }
                MyConstant.VideoPath = MyConstant.VideoDir+"video"+MyConstant.Current_Video+".mp4";
                break;
            case 8:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ll_game.setBackground(getDrawable(R.mipmap.background4));
                }
                MyConstant.VideoPath = MyConstant.VideoDir+"video"+MyConstant.Current_Video+".mp4";
                break;
        }
        MyConstant.debugLog("file"+MyConstant.VideoPath);
        file = new File(MyConstant.VideoPath);
        MyConstant.debugLog("file"+file.exists());
        if(file.exists()) {
            videoView.setVideoPath(MyConstant.VideoPath);
        }else{
            MyConstant.Current_Video = 0;
            setVideoPath();
        }

    }

    public void startPlay() {
        MyConstant.CurrentNumber = 0;
        MyConstant.isVideoPlay = true;
        videoView.start();
        ll_video.setVisibility(View.VISIBLE);
        ll_game.setVisibility(View.GONE);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                MyConstant.debugLog("播放完毕");
                if(MyConstant.Current_Video < 7){
                    MyConstant.Current_Video ++;
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyConstant.CurrentNumber = 0;
        MyConstant.isVideoPlay = true;
        videoView.pause();
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

    public String getSDPath(){
        MyConstant.debugLog("3"+Environment.getRootDirectory());
        MyConstant.debugLog("21"+Environment.getExternalStorageDirectory());
        MyConstant.debugLog("2"+this.getExternalFilesDir("Video"));
        return null;
    }
}
