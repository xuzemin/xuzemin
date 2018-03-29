/*
 * Copyright (c) 2014 Yrom Wang <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.yrom.screenrecorder.task;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.Surface;

import net.yrom.screenrecorder.core.Packager;
import net.yrom.screenrecorder.rtmp.RESFlvData;
import net.yrom.screenrecorder.rtmp.RESFlvDataCollecter;
import net.yrom.screenrecorder.rtp.RtpSenderWrapper;
import net.yrom.screenrecorder.rtp.RtpUdp;
import net.yrom.screenrecorder.tools.LogTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.ContentValues.TAG;
import static net.yrom.screenrecorder.rtmp.RESFlvData.FLV_RTMP_PACKET_TYPE_VIDEO;

/**
 * @author Yrom
 * Modified by raomengyang 2017-03-12
 */
public class ScreenRecorder extends Thread {
    private static final String TAG = "ScreenRecorder";
    private int mWidth;
    private int mHeight;
    private int mDpi;
    private  byte[] m_info = null;
    private MediaProjection mMediaProjection;
    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final int TIMEOUT_US = 10000;
    private RtpSenderWrapper mRtpSenderWrapper;
    private MediaCodec mEncoder;
    private Surface mSurface;
    private long startTime = 0;
    private AtomicBoolean mQuit = new AtomicBoolean(false);
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private VirtualDisplay mVirtualDisplay;
    private RESFlvDataCollecter mDataCollecter;

    public ScreenRecorder(RESFlvDataCollecter dataCollecter, int width, int height, int bitrate, int dpi, MediaProjection mp) {
        super(TAG);
        mWidth = width;
        mHeight = height;
        mDpi = dpi;
        mMediaProjection = mp;
        startTime = 0;
        mDataCollecter = dataCollecter;
        mRtpSenderWrapper = new RtpSenderWrapper(RESFlvData.Server_IP, RESFlvData.Server_PORT, false);
    }

    /**
     * stop task
     */
    public final void quit() {
        mQuit.set(true);
    }

    @Override
    public void run() {
        try {
            try {
                prepareEncoder();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display",
                    mWidth, mHeight, mDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    mSurface, null, null);
            Log.d(TAG, "created virtual display: " + mVirtualDisplay);
            recordVirtualDisplay();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release();
        }
    }


    private void prepareEncoder() throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, RESFlvData.VIDEO_BITRATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, RESFlvData.FPS);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, RESFlvData.VIDEO_I_FRAME_INTERVAL);
        Log.d(TAG, "created video format: " + format);
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mEncoder.createInputSurface();
        Log.d(TAG, "created input surface: " + mSurface);
        mEncoder.start();
    }

    private void recordVirtualDisplay() {
        int pos;
        ByteBuffer outputBuffer;
        byte[] outBytes = new byte[mWidth*mHeight*3];
        while (!mQuit.get()) {
            int eobIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);
            pos = 0;
            while (eobIndex>=0) {
                outputBuffer = mEncoder.getOutputBuffer(eobIndex);
                byte[] outData = new byte[mBufferInfo.size];
                outputBuffer.get(outData);

                if(m_info != null){
                    System.arraycopy(outData, 0,  outBytes, pos, outData.length);
                    pos += outData.length;

                }else{//保存pps sps 只有开始时 第一个帧里有， 保存起来后面用
                    ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
                    if (spsPpsBuffer.getInt() == 0x00000001) {
                        m_info = new byte[outData.length];
                        System.arraycopy(outData, 0, m_info, 0, outData.length);
                    }else{
                        pos = -1;
                    }
                }
                if(outBytes[4] == 0x65) {//key frame 编码器生成关键帧时只有 00 00 00 01 65 没有pps sps， 要加上
                    System.arraycopy(m_info, 0,  outBytes, 0, m_info.length);
                    System.arraycopy(outData, 0,  outBytes, m_info.length, outData.length);
                }
                mEncoder.releaseOutputBuffer(eobIndex, false);
                eobIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
            }
            mRtpSenderWrapper.sendAvcPacket(outBytes, 0, pos, 0);
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
    }


    public final boolean getStatus() {
        return !mQuit.get();
    }

}
