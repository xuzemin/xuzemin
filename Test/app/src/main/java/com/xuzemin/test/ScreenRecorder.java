package com.xuzemin.test;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by xuzemin on 2017/8/28.
 */

public class ScreenRecorder extends Thread {
    private String TAG = "Screen";
    private MediaMuxer mMuxer;
    private AtomicBoolean mQuit = new AtomicBoolean(false);
    private VirtualDisplay mVirtualDisplay;
    private MediaProjection mMediaProjection;
    private MediaCodec mEncoder;
    private Surface mSurface;
    private static final String MIME_TYPE = "video/avc";
    private MediaCodec.BufferInfo mBufferInfo;
    private boolean mMuxerStarted = false;
    private int mWidth,mHeight,mDpi,mBitRate;
    private String mDstPath;
    private int mVideoTrackIndex;
    public ScreenRecorder(int width, int height,int  bitrate, int number,MediaProjection mediaProjection,String mDstPath){
        mWidth = width;
        mHeight = height;
        this.mDstPath = mDstPath;
        mBitRate = bitrate;
        mDpi = number;
        this.mMediaProjection = mediaProjection;
    }

    @Override
    public void run() {
        try {
            try {
                prepareEncoder();
                mMuxer = new MediaMuxer(mDstPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display",
                    mWidth, mHeight, mDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    mSurface, null, null);
            Log.d(TAG, "created virtual display: " + mVirtualDisplay);
            recordVirtualDisplay();
        } finally {
            release();
        }
    }

    private void prepareEncoder() throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface); // 录屏必须配置的参数
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        Log.d(TAG, "created video format: " + format);
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mEncoder.createInputSurface(); // 需要在createEncoderByType之后和start()之前才能创建，源码注释写的很清楚
        Log.d(TAG, "created input surface: " + mSurface);
        mEncoder.start();
    }

    private void recordVirtualDisplay() {
        while (!mQuit.get()) {
            int index = mEncoder.dequeueOutputBuffer(mBufferInfo, 10000);
            Log.i(TAG, "dequeue output buffer index=" + index);
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                resetOutputFormat();
            } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d(TAG, "retrieving buffers time out!");
                try {
                    // wait 10ms
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            } else if (index >= 0) {
                if (!mMuxerStarted) {
                    throw new IllegalStateException("MediaMuxer dose not call addTrack(format) ");
                }
                encodeToVideoTrack(index);
                mEncoder.releaseOutputBuffer(index, false);
            }
        }
    }
    private void resetOutputFormat() {
        // should happen before receiving buffers, and should only happen once
        if (mMuxerStarted) {
            throw new IllegalStateException("output format already changed!");
        }
        MediaFormat newFormat = mEncoder.getOutputFormat();
        // 在此也可以进行sps与pps的获取，获取方式参见方法getSpsPpsByteBuffer()
        Log.i(TAG, "output format changed.\n new format: " + newFormat.toString());
        mVideoTrackIndex = mMuxer.addTrack(newFormat);
        mMuxer.start();
        mMuxerStarted = true;
        Log.i(TAG, "started media muxer, videoIndex=" + mVideoTrackIndex);
    }
    private void getSpsPpsByteBuffer(MediaFormat newFormat) {
        ByteBuffer rawSps = newFormat.getByteBuffer("csd-0");
        ByteBuffer rawPps = newFormat.getByteBuffer("csd-1");
    }

    private void encodeToVideoTrack(int index) {
        ByteBuffer encodedData = mEncoder.getOutputBuffer(index);
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            // The codec config data was pulled out and fed to the muxer when we got
            // the INFO_OUTPUT_FORMAT_CHANGED status.
            // Ignore it.
            // 大致意思就是配置信息(avcc)已经在之前的resetOutputFormat()中喂给了Muxer，此处已经用不到了，然而在我的项目中这一步却是十分重要的一步，因为我需要手动提前实现sps, pps的合成发送给流媒体服务器
            Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            mBufferInfo.size = 0;
        }
        if (mBufferInfo.size == 0) {
            Log.d(TAG, "info.size == 0, drop it.");
            encodedData = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + mBufferInfo.size
                    + ", presentationTimeUs=" + mBufferInfo.presentationTimeUs
                    + ", offset=" + mBufferInfo.offset);
        }
        if (encodedData != null) {
            encodedData.position(mBufferInfo.offset);
            encodedData.limit(mBufferInfo.offset + mBufferInfo.size); // encodedData是编码后的视频帧，但注意作者在此并没有进行关键帧与普通视频帧的区别，统一将数据写入Muxer
            mMuxer.writeSampleData(mVideoTrackIndex, encodedData, mBufferInfo);
            Log.i(TAG, "sent " + mBufferInfo.size + " bytes to muxer...");
        }
    }
    private void release() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
        if (mMuxer != null) {
            mMuxer.release();
            mMuxer = null;
        }
    }
}
