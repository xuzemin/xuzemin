package com.android.jdrd.ijkplayer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import application.Settings;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import widget.media.IRenderView;
import widget.media.IjkVideoView;

public class MainActivity extends AppCompatActivity {
    private IjkVideoView videoView;
    private Settings mSettings;
    private String URL = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = new Settings(this);
        videoView = (IjkVideoView) findViewById(R.id.videoview);
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
//        videoView.setVideoURI(Uri.parse("http://106.36.45.36/live.aishang.ctlcdn.com/00000110240001_1/encoder/1/playlist.m3u8"));
        videoView.setVideoURI(Uri.parse(URL));

        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                videoView.start();
            }
        });

    }
}
