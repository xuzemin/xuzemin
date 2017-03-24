package com.android.jdrd.dudu.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by xuzemin on 2017/3/23.
 * recognition
 */

public class Constant {
    private static SpeechSynthesizer mTts;
    private static SpeechRecognizer mIat;
    private static Constant constant;
    public static Constant getConstant(Context context){
        if(constant==null){
            constant = new Constant();
            mIat= SpeechRecognizer.createRecognizer(context, null);
            mTts= SpeechSynthesizer.createSynthesizer(context, null);
            setmIat();
            setmTts();
            mIat.startListening(mRecoListener);
            return constant;
        }
        return constant;
    }

    private static void parseGson(String resultJson){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(resultJson);
            answerText(jsonObject);
        } catch (JSONException e) {
            LogUtils.e("Result"+e.toString());
            LogUtils.e("moreResults");
        }
    }

    private static SynthesizerListener mSynListener = new SynthesizerListener(){
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            mIat.startListening(mRecoListener);
        }
        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}
        //开始播放
        public void onSpeakBegin() {}
        //暂停播放
        public void onSpeakPaused() {}
        //播放进度回调
        public void onSpeakProgress(int percent, int beginPos, int endPos) {}
        //恢复播放回调接口
        public void onSpeakResumed() {}
        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };
    //听写监听器
    private static RecognizerListener mRecoListener = new RecognizerListener(){
        public void onResult(RecognizerResult results, boolean isLast) {
            LogUtils.e(results.getResultString ());
            parseGson(results.getResultString());
        }
        public void onError(SpeechError error) {
            error.getPlainDescription(true);
            mIat.startListening(mRecoListener);
            LogUtils.e(error.toString());
        }
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }
        public void onBeginOfSpeech(){

        }
        public void onEndOfSpeech() {

        }
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
    private static void setmTts(){
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
    }
    private static void setmIat(){
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, "mix");
        mIat.setParameter("asr_sch", "1");
        mIat.setParameter(SpeechConstant.NLP_VERSION, "2.0");
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mIat.setParameter("mixed_type", "realtime");
        mIat.setParameter("mixed_timeout", "2500");
    }

    private static void answerText(JSONObject jsonObject){
        try {
            int rc = jsonObject.getInt("rc");
            switch (rc) {
                case 0:

                    /**
                     * 应用
                     */
                    JSONArray moreResults;
                    moreResults = jsonObject.getJSONArray("moreResults");
                    if (moreResults.length() > 0) {
                        for (int i=0,length = moreResults.length();i<length;i++){
                            JSONObject object = (JSONObject) moreResults.get(i);
                            String str = object.getString("answer");
                            if(str !=null && !str.equals("")){
                                mTts.startSpeaking(new JSONObject(str).getString("text"), mSynListener);
                            }else{
                                parseInfo(jsonObject);
                            }
                        }
                    } else {
                        parseInfo(jsonObject);
                    }
                    break;
                case 1:
                    mTts.startSpeaking("对不起，我没听明白您的意思", mSynListener);
                    break;
                case 2:
                    mTts.startSpeaking("对不起，服务器暂时无法回答您的要求", mSynListener);
                    break;
                case 3:
                    mTts.startSpeaking("对不起，暂时无法支持这种场景", mSynListener);
                    break;
                case 4:
                    mTts.startSpeaking(jsonObject.getString("text"), mSynListener);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private static void parseAPP(String type,String name){
        if(type.equals("LAUNCH")){
            mTts.startSpeaking("打开"+name, mSynListener);
        }else if(type.equals("UNINSTALL")){
            mTts.startSpeaking("卸载"+name, mSynListener);
        }else if(type.equals("DOWNLOAD")){
            mTts.startSpeaking("下载"+name, mSynListener);
        }else if(type.equals("INSTALL")){
            mTts.startSpeaking("安装"+name, mSynListener);
        }else if(type.equals("QUERY")){
            mTts.startSpeaking("搜索"+name, mSynListener);
        }else if(type.equals("QUERY")){
            mTts.startSpeaking("退出"+name, mSynListener);
        }
    }
    public static void parseANSWER(String text){
        mTts.startSpeaking(text, mSynListener);
    }
    private static void parseInfo(JSONObject jsonObject){
        try {
            if (jsonObject.getString("service").equals("app")) {
                parseAPP(jsonObject.getString("operation"), new JSONObject(new JSONObject(jsonObject.getString("semantic")).getString("slots")).getString("name"));
                /**
                 * 百科
                 */
            } else if (jsonObject.getString("service").equals("baike")) {
                parseANSWER(new JSONObject(jsonObject.getString("answer")).getString("text"));
                /**
                 * 计算
                 */
            } else if (jsonObject.getString("service").equals("calc")) {
                parseANSWER(new JSONObject(jsonObject.getString("answer")).getString("text"));
                /**
                 * 菜谱
                 */
            } else if (jsonObject.getString("service").equals("cookbook")) {
                mTts.startSpeaking("菜谱暂不支持查询", mSynListener);
                /**
                 * 日期
                 */
            } else if (jsonObject.getString("service").equals("datetime")) {
                parseANSWER(new JSONObject(jsonObject.getString("answer")).getString("text"));
                /**
                 * 问答
                 */
            } else if (jsonObject.getString("service").equals("faq")) {
                parseANSWER(new JSONObject(jsonObject.getString("answer")).getString("text"));
                /**
                 * 航班
                 */
            } else if (jsonObject.getString("service").equals("flight")) {
                mTts.startSpeaking("航班暂不支持查询", mSynListener);
                //查询时间
                if (jsonObject.getString("operation").equals("QUERY_TIME")) {
                    //查询航班信息
                } else if (jsonObject.getString("operation").equals("QUERY")) {
                } else {
                }
                /**
                 * 酒店
                 */
            } else if (jsonObject.getString("service").equals("hotel")) {
                mTts.startSpeaking("酒店暂不支持查询", mSynListener);
                //预定
                if (jsonObject.getString("operation").equals("BOOK")) {
                    //查询
                } else if (jsonObject.getString("operation").equals("QUERY")) {
                } else {
                }
                /**
                 * 地图
                 */
            } else if (jsonObject.getString("service").equals("map")) {
                //路线
                if (jsonObject.getString("operation").equals("ROUTE")) {
                    mTts.startSpeaking("地图路线暂不支持查询", mSynListener);
                    //地点
                } else if (jsonObject.getString("operation").equals("POSITION")) {
                    JSONObject object = new JSONObject(new JSONObject(new JSONObject(jsonObject.getString("semantic")).getString("slots")).getString("location"));
                    parseANSWER(object.getString("city") + object.getString("cityAddr"));
                } else {
                    mIat.startListening(mRecoListener);
                }
                /**
                 * 音乐
                 */
            } else if (jsonObject.getString("service").equals("music")) {
                //播放
                if (jsonObject.getString("operation").equals("PLAY")) {
                    mTts.startSpeaking("播放音乐暂不支持", mSynListener);
                    //搜索
                } else if (jsonObject.getString("operation").equals("SEARCH")) {
                    JSONObject object = new JSONObject(new JSONObject(new JSONObject(jsonObject.getString("semantic")).getString("slots")).getString("location"));
                    parseANSWER(object.getString("city") + object.getString("cityAddr"));
                    //查询歌曲信息
                } else if (jsonObject.getString("operation").equals("QUERY")) {
                    mTts.startSpeaking("歌曲信息暂不支持查询", mSynListener);
    //                            mIat.startListening(mRecoListener);
                }
                /**
                 * 火车
                 */
            } else if (jsonObject.getString("service").equals("train")) {
                mTts.startSpeaking("火车暂不支持查询", mSynListener);
                /**
                 * 翻译
                 */
            } else if (jsonObject.getString("service").equals("translation")) {
                mTts.startSpeaking("翻译暂不支持查询", mSynListener);
                /**
                 * 天气
                 */

            } else if (jsonObject.getString("service").equals("weather")) {
                mTts.startSpeaking("翻译暂不支持查询", mSynListener);
                /**
                 * 褒贬
                 */
            } else if (jsonObject.getString("service").equals("openQA")) {
                parseANSWER(new JSONObject(jsonObject.getString("answer")).getString("text"));
                /**
                 * 聊天
                 */
            } else if (jsonObject.getString("service").equals("chat")) {
                parseANSWER(new JSONObject(jsonObject.getString("answer")).getString("text"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
