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
 * Created by jdrd on 2017/3/23.
 */

public class Constant {
    public static String grammar = "";
    public static final String GRAMMAR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test";
    public static final String APPID = "appid";
    public static final String NET_TYPE = "net_type";
    public static final String FORCE_LOGIN = "force_login";
    public static final String LIB_NAME = "lib_name";
    public static final String RESULT_TYPE = "result_type";
    public static final String RESULT_LEVEL = "result_level";
    public static final String LANGUAGE = "language";
    public static final String ACCENT = "accent";
    public static final String DOMAIN = "domain";
    public static final String VAD_ENABLE = "vad_enable";
    public static final String VAD_BOS = "vad_bos";
    public static final String VAD_EOS = "vad_eos";
    public static final String SAMPLE_RATE = "sample_rate";
    public static final String PARAMS = "params";
    public static final String NET_CHECK = "net_check";
    public static final String NET_TIMEOUT = "timeout";
    public static final String KEY_SPEECH_TIMEOUT = "speech_timeout";
    public static final String ENGINE_MODE = "engine_mode";
    public static final String ENGINE_TYPE = "engine_type";
    public static final String AUDIO_SOURCE = "audio_source";
    public static final String ASR_SOURCE_PATH = "asr_source_path";
    public static final String FILTER_AUDIO_TIME = "filter_audio_time";
    public static final String LOCAL_GRAMMAR = "local_grammar";
    public static final String CLOUD_GRAMMAR = "cloud_grammar";
    public static final String GRAMMAR_TYPE = "grammar_type";
    public static final String GRAMMAR_NAME = "grammar_name";
    public static final String GRAMMAR_LIST = "grammar_list";
    public static final String LOCAL_GRAMMAR_PACKAGE = "local_grammar_package";
    public static final String NOTIFY_RECORD_DATA = "notify_record_data";
    public static final String MIXED_THRESHOLD = "mixed_threshold";
    public static final String MIXED_TYPE = "mixed_type";
    public static final String MIXED_TIMEOUT = "mixed_timeout";
    public static final String ASR_THRESHOLD = "asr_threshold";
    public static final String LEXICON_TYPE = "lexicon_type";
    public static final String ASR_NBEST = "asr_nbest";
    public static final String ASR_WBEST = "asr_wbest";
    public static final String ASR_PTT = "asr_ptt";
    public static final String ASR_SCH = "asr_sch";
    public static final String ASR_DWA = "asr_dwa";
    public static final String NLP_VERSION = "nlp_version";
    public static final String TYPE_LOCAL = "local";
    public static final String TYPE_CLOUD = "cloud";
    public static final String TYPE_MIX = "mix";
    public static final String TYPE_AUTO = "auto";
    public static final String ISV_SST = "sst";
    public static final String ISV_PWDT = "pwdt";
    public static final String ISV_VID = "vid";
    public static final String ISV_RGN = "rgn";
    public static final String ISV_PWD = "ptxt";
    public static final String ISV_AUDIO_PATH = "isv_audio_path";
    public static final String ISV_CMD = "cmd";
    public static final String ISV_INTERRUPT_ERROR = "isv_interrupt_error";
    public static final String WFR_SST = "sst";
    public static final String ISE_USER_MODEL_ID = "user_model_id";
    public static final String ISE_CATEGORY = "category";
    public static final String ISE_ENT = "ent";
    public static final String ISE_PARSED = "parsed";
    public static final String ISE_AUTO_TRACKING = "auto_tracking";
    public static final String ISE_TRACK_TYPE = "track_type";
    public static final String ISE_INTERRUPT_ERROR = "ise_interrupt_error";
    public static final String ISE_AUDIO_PATH = "ise_audio_path";
    public static final String ISE_SOURCE_PATH = "ise_source_path";
    public static final String IVW_SST = "sst";
    public static final String IVW_WORD_PATH = "ivw_word_path";
    public static final String IVW_THRESHOLD = "ivw_threshold";
    public static final String KEEP_ALIVE = "keep_alive";
    public static final String IVW_SHOT_WORD = "ivw_shot_word";
    public static final String IVW_ENROLL_RES_PATH = "ivw_enroll_res_path";
    public static final String IVW_ENROLL_DEST_PATH = "ivw_enroll_dest_path";
    public static final String IVW_ENROLL_TMIN = "ivw_enroll_tmin";
    public static final String IVW_ENROLL_TMAX = "ivw_enroll_tmax";
    public static final String IVW_VOL_CHECK = "ivw_vol_check";
    public static final String IVW_ENROLL_TIMES = "ivw_enroll_times";
    public static final String IVW_RES_PATH = "ivw_res_path";
    public static final String IVW_NET_MODE = "ivw_net_mode";
    public static final String IVW_CHANNEL_NUM = "ivw_channel_num";
    public static final String VOICE_NAME = "voice_name";
    public static final String EMOT = "emot";
    public static final String NEXT_TEXT = "next_text";
    public static final String LOCAL_SPEAKERS = "local_speakers";
    public static final String SPEED = "speed";
    public static final String PITCH = "pitch";
    public static final String VOLUME = "volume";
    public static final String BACKGROUND_SOUND = "background_sound";
    public static final String TTS_BUFFER_TIME = "tts_buffer_time";
    public static final String TTS_PLAY_STATE = "tts_play_state";
    public static final String TTS_DATA_NOTIFY = "tts_data_notify";
    public static final String TTS_INTERRUPT_ERROR = "tts_interrupt_error";
    public static final String TTS_SPELL_INFO = "tts_spell_info";
    public static final String AUDIO_FORMAT = "audio_format";
    public static final String STREAM_TYPE = "stream_type";
    public static final String KEY_REQUEST_FOCUS = "request_audio_focus";
    public static final String TTS_AUDIO_PATH = "tts_audio_path";
    public static final String DATA_TYPE = "data_type";
    public static final String SUBJECT = "subject";
    public static final String ASR_AUDIO_PATH = "asr_audio_path";
    public static final String ASR_INTERRUPT_ERROR = "asr_interrupt_error";
    public static final String ASR_NOMATCH_ERROR = "asr_nomatch_error";
    public static final String ASR_NET_PERF = "asr_net_perf";
    public static final String ENG_ASR = "asr";
    public static final String ENG_TTS = "tts";
    public static final String ENG_NLU = "nlu";
    public static final String ENG_IVW = "ivw";
    public static final String ENG_IVP = "ivp";
    public static final String ENG_WFR = "wfr";
    public static final String ENG_EVA = "eva";
    public static final String MODE_MSC = "msc";
    public static final String MODE_PLUS = "plus";
    public static final String MODE_AUTO = "auto";
    public static final String TEXT_ENCODING = "text_encoding";
    public static final String TEXT_BOM = "text_bom";
    public static final String AUTH_ID = "auth_id";
    public static final String MFV_SST = "sst";
    public static final String MFV_VCM = "vcm";
    public static final String MFV_SCENES = "scenes";
    public static final String MFV_AFC = "afc";
    public static final String MFV_DATA_PATH = "mfv_data_path";
    public static final String MFV_INTERRUPT_ERROR = "mfv_interrupt_error";
    public static final String PROT_TYPE = "prot_type";
    public static final String PLUS_LOCAL_TTS = "tts";
    public static final String PLUS_LOCAL_ASR = "asr";
    public static final String PLUS_LOCAL_IVW = "ivw";
    public static final String PLUS_LOCAL_ALL = "all";
    public static final String IST_SESSION_ID = "sid";
    public static final String IST_SYNC_ID = "syncid";
    public static final String IST_AUDIO_UPLOADED = "spos";
    public static final String IST_AUDIO_PATH = "ist_audio_path";
    public static final String IST_SESSION_TRY = "ist_session_try";
    public static SpeechSynthesizer mTts;
    public static SpeechRecognizer mIat;
    public static Constant constant;
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

    public static void parseGson(String resultJson){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(resultJson);
            answerText(jsonObject);
        } catch (JSONException e) {
            LogUtils.e("Result"+e.toString());
            LogUtils.e("moreResults");
            JSONArray moreResults = null;
            try {
                moreResults = jsonObject.getJSONArray("moreResults");
                Random random=new Random();
                int randomInt=random.nextInt(moreResults.length());
                answerText((JSONObject) moreResults.get(randomInt));
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

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
    public static RecognizerListener mRecoListener = new RecognizerListener(){
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
        //音量值0~30
        public void onVolumeChanged(int volume){

        }
        //结束录音
        public void onEndOfSpeech() {

        }
        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
    public static void setmTts(){
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
    }
    public static void setmIat(){
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, "mix");
        mIat.setParameter("asr_sch", "1");
        mIat.setParameter(SpeechConstant.NLP_VERSION, "2.0");
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mIat.setParameter("mixed_type", "realtime");
        mIat.setParameter("mixed_timeout", "2500");
    }

    public static void answerText(JSONObject jsonObject){
        try {
            if(jsonObject.getInt("rc")==0){
                if(jsonObject.getString("operation").equals("ANSWER")){
                    String text = new JSONObject(jsonObject.getString("answer")).getString("text");
                    String type = new JSONObject(jsonObject.getString("answer")).getString("type");
                    if(type.contains("T")){
                        mTts.startSpeaking(text, mSynListener);
                    }else{
                        mTts.startSpeaking("对不起，暂时无法展示网络数据", mSynListener);
                    }
                }else if(!jsonObject.getString("operation").equals("ANSWER")){
                    mIat.startListening(mRecoListener);
                }else{
                    mIat.startListening(mRecoListener);
                }
            }else if(jsonObject.getInt("rc")==1){
                mTts.startSpeaking("对不起，我没听明白您的意思", mSynListener);
            }else if(jsonObject.getInt("rc")==2){
                mTts.startSpeaking("对不起，服务器暂时无法回答您的要求", mSynListener);
            }else if(jsonObject.getInt("rc")==2){
                mTts.startSpeaking(new JSONObject(jsonObject.getString("error")).getString("message"), mSynListener);
            }else{
                mTts.startSpeaking("对不起，暂时无法支持这种场景", mSynListener);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
