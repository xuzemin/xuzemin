package com.android.jdrd.dudu.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.lidroid.xutils.util.LogUtils;

/**
 * Created by jdrd on 2017/3/23.
 */

public class IflyVoiceService extends Service {
    public SpeechRecognizer mIat;
    //	语音合成的stringbuffer
    private StringBuffer sbspeak = new StringBuffer();
    //	语音听写的stringbuffer
    private StringBuffer sblisten = new StringBuffer();
    private Context context;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    break;
            }
        }
    };
    public IflyVoiceService(Context context){
        this.context = context;
        mIat= SpeechRecognizer.createRecognizer(context, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        init();
        //开启线程,监听语音识别
        new Thread() {
            public void run() {
                LogUtils.e("开始识别");
                mIat.startListening(mRecoListener);
            }
        }.start();
        super.onCreate();
    }
    public void init() {
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
    }

    //听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            LogUtils.e(results.getResultString());
//		     说明回话结束
            if (isLast) {
                String backword = (results.getResultString());
                LogUtils.e("源数据：" + results.getResultString());
                LogUtils.e("解析后数据：" + backword);
            }
        }

        //会话发生错误回调接口
        public void onError(SpeechError error) {
            //发生错误时继续调用识别监听
            LogUtils.e(error.toString());
//            if (error.getErrorCode() == 20002) {
//                mXunFeiBdhc.start("网络连接超时，正在重新识别");
//            } else if (error.getErrorCode() == 10114) {
//                mXunFeiBdhc.start("网络发生异常，正在重新识别");
//            } else {
//                yytx();
//            }
        }
        //开始录音
        public void onBeginOfSpeech() {
            LogUtils.e("开始录音");
        }
        //结束录音
        public void onEndOfSpeech() {
            LogUtils.e("结束录音");
        }
        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
        @Override
        public void onVolumeChanged(int i, byte[] arg1) {
        }
    };
}
