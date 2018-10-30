package com.android.androidlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private GridView gameGird;
    private static LauncherVideoView videoView;
    private SimpleAdapter adapter;
    private static Thread thread = null;
    private LinearLayout ll_video,ll_game;
    private PackageManager packageManager;
    private Intent intent;
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
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                MyConstant.debugLog("onItemClick");
                MyConstant.isResetPlay = true;
                packageManager = getPackageManager();
                /**获得Intent*/
                intent = packageManager.getLaunchIntentForPackage("com.android.settings"); //com.xx.xx是我们获取到的包名 
                if(intent!=null){
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"包名不存在",Toast.LENGTH_SHORT).show();
                }
            }
        });
        ll_video.setOnClickListener(this);
        ll_game.setOnClickListener(this);
        setVideoPath();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessage(MyConstant.EVENT_START_VIDEO);
    }

    public void setVideoPath(){
        MyConstant.debugLog("Video"+MyConstant.Current_Video);
        switch (MyConstant.Current_Video){
            case 0:
                MyConstant.VideoPath = MyConstant.VideoDir+R.raw.one;
                break;
            case 1:
                MyConstant.VideoPath = MyConstant.VideoDir+R.raw.two;
                break;
            case 2:
                MyConstant.VideoPath = MyConstant.VideoDir+R.raw.three;
                break;
            case 3:
                MyConstant.VideoPath = MyConstant.VideoDir+R.raw.four;
                break;
            case 4:
                MyConstant.VideoPath = MyConstant.VideoDir+R.raw.five;
                break;
            case 5:
                MyConstant.VideoPath = MyConstant.VideoDir+R.raw.six;
                break;
            case 6:
                MyConstant.VideoPath = MyConstant.VideoDir+R.raw.seven;
                break;
            case 7:
                MyConstant.VideoPath = MyConstant.VideoDir+R.raw.eight;
                break;
            case 8:
                break;
        }
        videoView.setVideoURI(Uri.parse(MyConstant.VideoPath));
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
    }

    void initData() {
        //图标
        int icno[] = { R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
                R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
        };
        //图标下的文字
        String name[]={"游戏","游戏","游戏","游戏","游戏","游戏"};
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
}
