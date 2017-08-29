package com.xuzemin.test;

import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private static final int RECORDER_CODE = 0;
    private static final String TAG = "TAG";

    int width;
    int height;
    int dpi;

    MediaProjectionManager projectionManager;
    MediaProjection mediaProjection;
    MediaCodec mediaCodec;
    MediaMuxer mediaMuxer;

    Surface surface;
    VirtualDisplay virtualDisplay;
    private MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    private int videoTrackIndex = -1;

    String filePath;
    private AtomicBoolean mQuit = new AtomicBoolean(false);
    private boolean muxerStarted = false;
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
        height = metric.heightPixels;
        dpi = metric.densityDpi;
//        width = 720;
//        height = 1280;
//        dpi = 1;

        File file = new File(Environment.getExternalStorageDirectory(),
                "record-" + width + "x" + height + "-" + System.currentTimeMillis() + ".mp4");
        filePath = file.getAbsolutePath();

        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        findViewById(R.id.buttonPanel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartRecorder(view);
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopRecorder(view);
            }
        });
        surfaceView = (SurfaceView) findViewById(R.id.surface);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mediaProjection = projectionManager.getMediaProjection(resultCode,data);
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                try {
                    try {
                        prepareEncoder();
//                        mediaMuxer = new MediaMuxer(filePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    virtualDisplay = mediaProjection.createVirtualDisplay(TAG + "-display",
                            width, height, dpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                            surfaceView.getHolder().getSurface(), null, null);
                    recordVirtualDisplay();

                } finally {
                    release();
                }
            }
        }.start();

        Toast.makeText(this, "Recorder is running...", Toast.LENGTH_SHORT).show();
//        moveTaskToBack(true);
    }

    private void recordVirtualDisplay() {
        while (!mQuit.get()) {
            int index = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000);
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                resetOutputFormat();
            } else if (index >= 0) {
                encodeToVideoTrack(index);
                mediaCodec.releaseOutputBuffer(index, false);
            }
        }
    }

    private void encodeToVideoTrack(int index) {
        ByteBuffer encodedData = mediaCodec.getOutputBuffer(index);

        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            bufferInfo.size = 0;
        }
        if (bufferInfo.size == 0) {
            encodedData = null;
        }
        if (encodedData != null) {
            encodedData.position(bufferInfo.offset);
            encodedData.limit(bufferInfo.offset + bufferInfo.size);
            mediaMuxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo);
        }
    }

    private void resetOutputFormat() {
        MediaFormat newFormat = mediaCodec.getOutputFormat();
        videoTrackIndex = mediaMuxer.addTrack(newFormat);
        mediaMuxer.start();
        muxerStarted = true;
    }

    private void prepareEncoder() throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", width, height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 6000000);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);

        mediaCodec = MediaCodec.createEncoderByType("video/avc");
        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        surface = mediaCodec.createInputSurface();
        mediaCodec.start();
    }

    public void StartRecorder(View view) {
        startActivityForResult(projectionManager.createScreenCaptureIntent(),RECORDER_CODE);
    }

    public void StopRecorder(View view) {
        mQuit.set(true);
        Toast.makeText(this, "Recorder stop", Toast.LENGTH_SHORT).show();
    }

    private void release() {
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
        if (mediaMuxer != null) {
            mediaMuxer.release();
            mediaMuxer = null;
        }
    }
}