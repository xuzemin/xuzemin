package com.android.jdrd.ijkplayer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import application.Settings;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import widget.media.IjkVideoView;

public class MainActivity extends Activity {
    private IjkVideoView videoView;
    private Settings mSettings;
    private String URL_AM_ZH = "rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp";
    private String Url_HKSTV = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";
    private String Url_CCTV1 = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
    private String Url_CCTV3 = "http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8";
    private String Url_CCTV5 = "http://ivi.bupt.edu.cn/hls/cctv5hd.m3u8";
    private String Url_CCTV6 = "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8";
    private String Url_CCTV5HD = "http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8";
    private String URL_APPLE = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8";
    private String URL_HK = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        mSettings = new Settings(this);
        videoView = findViewById(R.id.videoview);
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
//        videoView.setVideoURI(Uri.parse("http://106.36.45.36/live.aishang.ctlcdn.com/00000110240001_1/encoder/1/playlist.m3u8"));
        videoView.setVideoURI(Uri.parse(Url_CCTV6));

        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                videoView.start();
            }
        });

    }
}
