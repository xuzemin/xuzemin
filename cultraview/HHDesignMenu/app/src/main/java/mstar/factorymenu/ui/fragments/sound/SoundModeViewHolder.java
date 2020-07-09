package mstar.factorymenu.ui.fragments.sound;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.cultraview.tv.CtvAudioManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.utils.LogUtils;

public class SoundModeViewHolder implements View.OnClickListener, View.OnFocusChangeListener, SeekBar.OnSeekBarChangeListener {
    private final  String  TAG= "SoundModeViewHolder";
    private Context context;
    private String[] soumodearray;
    private static final int MAXMODE = 5;

    private int soumodeindex = 0;

    private int data_position = 0;

    private int val_bass = 50;

    private int val_treble = 50;

    private int val_balance = 50;

    private int val_120 = 50;

    private int val_500 = 50;

    private int val_1500 = 50;

    private int val_5k = 50;

    private int val_10k = 50;

    private int val_bass_t = 50;

    private int val_treble_t = 50;

    private int val_balance_t = 50;

    private int val_120_t = 50;

    private int val_500_t = 50;

    private int val_1500_t = 50;

    private int val_5k_t = 50;

    private int val_10k_t = 50;


    JSONArray jsonArray = new JSONArray();

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    // JSONArray menujsonArray = new JSONArray();

    // SharedPreferences menuEharedPreferences;

    // Editor menuEditor;

    final static char[] HEX = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    int[][] eqNonlinearValue = {
            {
                    0, 25, 50, 75, 100
            }, {
            0, 25, 50, 75, 100
    }, {
            0, 25, 50, 75, 100
    }, {
            0, 25, 50, 75, 100
    }, {
            0, 25, 50, 75, 100
    }, {
            0, 25, 50, 75, 100
    }, {
            0, 25, 50, 75, 100
    }
    };

    public int[] CtvValue = {
            0, 0, 0, 0, 0, 0, 0, 0
    };

    int[][] osdValue = {
            {
                    0, 0, 0, 0, 0, 0, 0, 0
            }, {
            0, 0, 0, 0, 0, 0, 0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0
    }
    };

    int[][] realValue = {
            {
                    0, 0, 0, 0, 0, 0, 0, 0
            }, {
            0, 0, 0, 0, 0, 0, 0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0
    }
    };
    private final CtvAudioManager tvAudioManager;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    editor.putString("osdValue", jsonArray.toString());
                    editor.commit();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };
    private Button btn_sound_mode_standard;
    private Button btn_sound_mode_music;
    private Button btn_sound_mode_sports;
    private Button btn_sound_mode_user;
    private SeekBar seekBar_sound_mode_bass;
    private SeekBar seekBar_sound_mode_treble;
    private SeekBar seekBar_sound_mode_eq1;
    private SeekBar seekBar_sound_mode_eq2;
    private SeekBar seekBar_sound_mode_eq3;
    private SeekBar seekBar_sound_mode_eq4;
    private SeekBar seekBar_sound_mode_eq5;
    private TextView textView_sound_mode_bass_text;
    private TextView textView_sound_mode_treble_text;
    private TextView textView_sound_mode_eq1_text;
    private TextView textView_sound_mode_eq2_text;
    private TextView textView_sound_mode_eq3_text;
    private TextView textView_sound_mode_eq4_text, textView_sound_mode_eq5_text;
    private LinearLayout ll_sound_mode_bass;
    private LinearLayout ll_sound_mode_treble;
    private LinearLayout ll_sound_mode_eq1;
    private LinearLayout ll_sound_mode_eq2;
    private LinearLayout ll_sound_mode_eq3;
    private LinearLayout ll_sound_mode_eq4;
    private LinearLayout ll_sound_mode_eq5;

    public SoundModeViewHolder(Context activity, View view) {
        this.context = activity;

        tvAudioManager = CtvAudioManager.getInstance();
        soumodearray = context.getResources().getStringArray(
                R.array.str_arr_sound_mode_vals);
        findView(view);
        onCreate();
    }

    private void findView(View view) {
        btn_sound_mode_standard = view.findViewById(R.id.sound_mode_standard);
        btn_sound_mode_music = view.findViewById(R.id.sound_mode_music);
        btn_sound_mode_sports = view.findViewById(R.id.sound_mode_sports);
        btn_sound_mode_user = view.findViewById(R.id.sound_mode_user);

        seekBar_sound_mode_bass = view.findViewById(R.id.sound_mode_bass);
        seekBar_sound_mode_treble = view.findViewById(R.id.sound_mode_treble);
        seekBar_sound_mode_eq1 = view.findViewById(R.id.sound_mode_eq1);
        seekBar_sound_mode_eq2 = view.findViewById(R.id.sound_mode_eq2);
        seekBar_sound_mode_eq3 = view.findViewById(R.id.sound_mode_eq3);
        seekBar_sound_mode_eq4 = view.findViewById(R.id.sound_mode_eq4);
        seekBar_sound_mode_eq5 = view.findViewById(R.id.sound_mode_eq5);

        textView_sound_mode_bass_text = view.findViewById(R.id.sound_mode_bass_text);
        textView_sound_mode_treble_text = view.findViewById(R.id.sound_mode_treble_text);
        textView_sound_mode_eq1_text = view.findViewById(R.id.sound_mode_eq1_text);
        textView_sound_mode_eq2_text = view.findViewById(R.id.sound_mode_eq2_text);
        textView_sound_mode_eq3_text = view.findViewById(R.id.sound_mode_eq3_text);
        textView_sound_mode_eq4_text = view.findViewById(R.id.sound_mode_eq4_text);
        textView_sound_mode_eq5_text = view.findViewById(R.id.sound_mode_eq5_text);


        ll_sound_mode_bass = view.findViewById(R.id.ll_sound_mode_bass);
        ll_sound_mode_treble = view.findViewById(R.id.ll_sound_mode_treble);
        ll_sound_mode_eq1 = view.findViewById(R.id.ll_sound_mode_eq1);
        ll_sound_mode_eq2 = view.findViewById(R.id.ll_sound_mode_eq2);
        ll_sound_mode_eq3 = view.findViewById(R.id.ll_sound_mode_eq3);
        ll_sound_mode_eq4 = view.findViewById(R.id.ll_sound_mode_eq4);
        ll_sound_mode_eq5 = view.findViewById(R.id.ll_sound_mode_eq5);


        btn_sound_mode_standard.setOnClickListener(this);
        btn_sound_mode_standard.setOnFocusChangeListener(this);
        btn_sound_mode_music.setOnClickListener(this);
        btn_sound_mode_music.setOnFocusChangeListener(this);
        btn_sound_mode_sports.setOnClickListener(this);
        btn_sound_mode_sports.setOnFocusChangeListener(this);
        btn_sound_mode_user.setOnClickListener(this);
        btn_sound_mode_user.setOnFocusChangeListener(this);


        seekBar_sound_mode_bass.setOnSeekBarChangeListener(this);
        seekBar_sound_mode_treble.setOnSeekBarChangeListener(this);
        seekBar_sound_mode_eq1.setOnSeekBarChangeListener(this);
        seekBar_sound_mode_eq2.setOnSeekBarChangeListener(this);
        seekBar_sound_mode_eq3.setOnSeekBarChangeListener(this);
        seekBar_sound_mode_eq4.setOnSeekBarChangeListener(this);
        seekBar_sound_mode_eq5.setOnSeekBarChangeListener(this);

        seekBar_sound_mode_bass.setOnFocusChangeListener(this);
        seekBar_sound_mode_treble.setOnFocusChangeListener(this);
        seekBar_sound_mode_eq1.setOnFocusChangeListener(this);
        seekBar_sound_mode_eq2.setOnFocusChangeListener(this);
        seekBar_sound_mode_eq3.setOnFocusChangeListener(this);
        seekBar_sound_mode_eq4.setOnFocusChangeListener(this);
        seekBar_sound_mode_eq5.setOnFocusChangeListener(this);

    }


    public boolean onCreate() {
        sharedPreferences = context.getSharedPreferences("OsdValue",
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        getEqNonlinearValue();
        soumodeindex = tvAudioManager.getAudioSoundMode();
        // getCtvMenuValue();

        boolean ret = getOSDValue();
        if (ret) {
            val_120 = osdValue[soumodeindex][0];
            val_500 = osdValue[soumodeindex][1];
            val_1500 = osdValue[soumodeindex][2];
            val_5k = osdValue[soumodeindex][3];
            val_10k = osdValue[soumodeindex][4];
            val_bass = osdValue[soumodeindex][5];
            val_treble = osdValue[soumodeindex][6];

            val_120_t = tvAudioManager.getEqBand120();
            val_500_t = tvAudioManager.getEqBand500();
            val_1500_t = tvAudioManager.getEqBand1500();
            val_5k_t = tvAudioManager.getEqBand5k();
            val_10k_t = tvAudioManager.getEqBand10k();
            val_bass_t = tvAudioManager.getBass();
            val_treble_t = tvAudioManager.getTreble();
            val_balance_t = tvAudioManager.getBalance();

        } else {
            LogUtils.d(TAG, "sharedpreference has not build");
            try {
                jsonArray = new JSONArray(
                        "[[16,25,50,75,100,100,100],[16,25,50,75,100,100,100],[16,25,50,75,100,100,100],[16,25,50,75,100,100,100],"
                                + "[16,25,50,75,100,100,100]]");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LogUtils.d(TAG, "jsonArray i2s  before" + jsonArray.toString());
            getTrueVal();
            for (int soumodeindex1 = 0; soumodeindex1 < 5; soumodeindex1++) {
                setData(soumodeindex1, 5, toOsdValue(realValue[soumodeindex1][5], 5));
                setData(soumodeindex1, 6, toOsdValue(realValue[soumodeindex1][6], 6));
                setData(soumodeindex1, 0, toOsdValue(realValue[soumodeindex1][0], 0));
                setData(soumodeindex1, 1, toOsdValue(realValue[soumodeindex1][1], 1));
                setData(soumodeindex1, 2, toOsdValue(realValue[soumodeindex1][2], 2));
                setData(soumodeindex1, 3, toOsdValue(realValue[soumodeindex1][3], 3));
                setData(soumodeindex1, 4, toOsdValue(realValue[soumodeindex1][4], 4));
                LogUtils.d(TAG, "soumodeindex1 is" + soumodeindex1);
            }
            mHandler.sendEmptyMessageDelayed(5, 300);

            val_120_t = tvAudioManager.getEqBand120();
            val_500_t = tvAudioManager.getEqBand500();
            val_1500_t = tvAudioManager.getEqBand1500();
            val_5k_t = tvAudioManager.getEqBand5k();
            val_10k_t = tvAudioManager.getEqBand10k();
            val_bass_t = tvAudioManager.getBass();
            val_treble_t = tvAudioManager.getTreble();
            val_balance_t = tvAudioManager.getBalance();

            val_120 = toOsdValue(val_120_t, 0);
            val_500 = toOsdValue(val_500_t, 1);
            val_1500 = toOsdValue(val_1500_t, 2);
            val_5k = toOsdValue(val_5k_t, 3);
            val_10k = toOsdValue(val_10k_t, 4);
            val_bass = toOsdValue(val_bass_t, 5);
            val_treble = toOsdValue(val_treble_t, 6);
            val_balance = tvAudioManager.getBalance();
        }

        LogUtils.d(TAG, "val_bass is " + val_bass);
        LogUtils.d(TAG, "val_treble is " + val_treble);
        LogUtils.d(TAG, "val_120 is " + val_120);
        LogUtils.d(TAG, "val_500 is " + val_500);
        LogUtils.d(TAG, "val_1500 is " + val_1500);
        LogUtils.d(TAG, "val_5k is " + val_5k);
        LogUtils.d(TAG, "val_10k is " + val_10k);

        clearBtnState();
        if (soumodeindex == 0) {
            btn_sound_mode_standard.setBackgroundResource(R.color.colorFocus);
        } else if (soumodeindex == 1) {
            btn_sound_mode_music.setBackgroundResource(R.color.colorFocus);
        } else if (soumodeindex == 3) {
            btn_sound_mode_sports.setBackgroundResource(R.color.colorFocus);
        } else {
            btn_sound_mode_user.setBackgroundResource(R.color.colorFocus);
        }


//        textview_volume_mode_val.setText(soumodearray[soumodeindex]);
        textView_sound_mode_bass_text.setText(Integer.toString(val_bass));
        textView_sound_mode_treble_text.setText(Integer.toString(val_treble));
//        textview_volume_balance_val.setText(Integer.toString(val_balance));
        textView_sound_mode_eq1_text.setText(Integer.toString(val_120));
        textView_sound_mode_eq2_text.setText(Integer.toString(val_500));
        textView_sound_mode_eq3_text.setText(Integer.toString(val_1500));
        textView_sound_mode_eq4_text.setText(Integer.toString(val_5k));
        textView_sound_mode_eq5_text.setText(Integer.toString(val_10k));

        seekBar_sound_mode_bass.setProgress(val_bass);
        seekBar_sound_mode_treble.setProgress(val_treble);
        seekBar_sound_mode_eq1.setProgress(val_120);
        seekBar_sound_mode_eq2.setProgress(val_500);
        seekBar_sound_mode_eq3.setProgress(val_1500);
        seekBar_sound_mode_eq4.setProgress(val_5k);
        seekBar_sound_mode_eq5.setProgress(val_10k);


        return true;
    }

    public void clearBtnState() {
        btn_sound_mode_standard.setBackgroundResource(R.color.colorUnFocus);
        btn_sound_mode_music.setBackgroundResource(R.color.colorUnFocus);
        btn_sound_mode_sports.setBackgroundResource(R.color.colorUnFocus);
        btn_sound_mode_user.setBackgroundResource(R.color.colorUnFocus);
    }


    private void setData(int i, int j, int value) {

        try {
            // jsonArray = jsonArray.put(i, value);

            jsonArray = jsonArray.put(i, jsonArray.getJSONArray(i).put(j, value));
            osdValue[i][j] = value;
            LogUtils.d(TAG, "i is" + i + "j is" + j + "osdValue[i][j] is" + osdValue[i][j]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int toOsdValue(int real, int index) {
        int data = 0;
        if (real < eqNonlinearValue[index][0]) {
            data = 0;
        } else if (real < eqNonlinearValue[index][1]) {
            data = (int) (real - Math.round(eqNonlinearValue[index][0]) * 25.0
                    / (eqNonlinearValue[index][1] - eqNonlinearValue[index][0]));
        } else if (real < eqNonlinearValue[index][2]) {
            data = (int) (25 + Math.round((real - eqNonlinearValue[index][1]) * 25.0
                    / (eqNonlinearValue[index][2] - eqNonlinearValue[index][1])));
        } else if (real < eqNonlinearValue[index][3]) {
            data = (int) (50 + Math.round((real - eqNonlinearValue[index][2]) * 25.0
                    / (eqNonlinearValue[index][3] - eqNonlinearValue[index][2])));
        } else if (real < eqNonlinearValue[index][4]) {
            data = (int) (75 + Math.round((real - eqNonlinearValue[index][3]) * 25.0
                    / (eqNonlinearValue[index][4] - eqNonlinearValue[index][3])));
        } else {
            data = 100;
        }
        return data;
    }


    private void getTrueVal() {
        String strHotel = null;
        String sql = "select * from tbl_SoundModeSetting";
        SQLiteDatabase projectInfoDB = SQLiteDatabase.openDatabase(
                "/tvdatabase/Database/user_setting.db", null, SQLiteDatabase.OPEN_READONLY
                        | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        Cursor cursor = projectInfoDB.rawQuery(sql, null);
        int i = 0;
        while (cursor.moveToNext()) {
            realValue[i][0] = cursor.getInt(3);
            realValue[i][1] = cursor.getInt(4);
            realValue[i][2] = cursor.getInt(5);
            realValue[i][3] = cursor.getInt(6);
            realValue[i][4] = cursor.getInt(7);
            realValue[i][5] = cursor.getInt(1);
            realValue[i][6] = cursor.getInt(2);
            i++;
        }

        for (i = 0; i < 5; i++) {
            for (int j = 0; j < 8; j++) {
                LogUtils.d(TAG, "realvalue is " + realValue[i][j]);
            }
        }
        cursor.close();
        projectInfoDB.close();
        LogUtils.d(TAG, "--strHotel:" + strHotel);
    }


    public void getEqNonlinearValue() {
        try {
            Context MTvFactoryContext = context.createPackageContext(
                    "mstar.factorymenu.ui", Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences sharedPreferences = MTvFactoryContext.getSharedPreferences(
                    "EqNonlinear", Context.MODE_PRIVATE);
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(sharedPreferences.getString("data", "[]"));
                if (!jsonArray.isNull(0)) {
                    for (int i = 0; i < 7; i++) {
                        for (int j = 0; j < 5; j++) {
                            eqNonlinearValue[i][j] = jsonArray.getJSONArray(i).getInt(j);
                        }
                    }
                } else {
                    try {
                        String br = read2String("/tvcustomer/Customer/eq.xml");
                        LogUtils.d(TAG, "string is" + br);
                        LogUtils.d(TAG, "1");
                        jsonArray = new JSONArray(br);
                        LogUtils.d(TAG, "2");
                        for (int i = 0; i < 7; i++) {
                            for (int j = 0; j < 5; j++) {
                                eqNonlinearValue[i][j] = jsonArray.getJSONArray(i).getInt(j);
                                LogUtils.d(TAG, "eqNonlinearValue[i][j] i1s" + eqNonlinearValue[i][j]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogUtils.d(TAG, "" + eqNonlinearValue);
    }


    // CtvPatch Begin

    public String read2String(String filePath) {
        String ioStr = "";
        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
            byte[] in = new byte[is.available()];
            is.read(in);
            ioStr = hexEncode(in);
            ioStr = hexStr2Str(ioStr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ioStr;
    }

    public static String hexEncode(byte[] buffer) {
        if (buffer.length == 0) {
            return "";
        }
        int holder = 0;
        char[] chars = new char[buffer.length * 2];
        for (int i = 0; i < buffer.length; i++) {
            holder = (buffer[i] & 0xf0) >> 4;
            chars[i * 2] = HEX[holder];
            holder = buffer[i] & 0x0f;
            chars[(i * 2) + 1] = HEX[holder];
        }
        return new String(chars);
    }

    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    // CtvPatch End

    public boolean getOSDValue() {
        // JSONArray jsonArray;
        try {
            // jsonArray = new JSONArray(
            // "[[16,25,50,75,100,100,100]]");
            jsonArray = new JSONArray(sharedPreferences.getString("osdValue", "[]"));
            LogUtils.d(TAG, "osdValue is" + jsonArray.toString());
            if (!jsonArray.isNull(0)) {
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 7; j++) {
                        osdValue[i][j] = jsonArray.getJSONArray(i).getInt(j);
                        LogUtils.d(TAG, "osdValue[i][j] is" + osdValue[i][j]);
                    }
                }
                LogUtils.d(TAG, "oreturn true");
                return true;
            } else {
                LogUtils.d(TAG, "oreturn false");
                return false;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    public void setEqBand(int index, int value) {
        int data = 0;
        if (value < 25) {
            data = (int) (eqNonlinearValue[index][0] + Math
                    .round((eqNonlinearValue[index][1] - eqNonlinearValue[index][0]) * value / 25.0));
        } else if (value < 50) {
            data = (int) (eqNonlinearValue[index][1] + Math
                    .round((eqNonlinearValue[index][2] - eqNonlinearValue[index][1]) * (value - 25)
                            / 25.0));
        } else if (value < 75) {
            data = (int) (eqNonlinearValue[index][2] + Math
                    .round((eqNonlinearValue[index][3] - eqNonlinearValue[index][2]) * (value - 50)
                            / 25.0));
        } else if (value <= 100) {
            data = (int) (eqNonlinearValue[index][3] + Math
                    .round((eqNonlinearValue[index][4] - eqNonlinearValue[index][3]) * (value - 75)
                            / 25.0));
        }
        LogUtils.d(TAG, "data in real is" + data);
        switch (index) {
            case 0:
                tvAudioManager.setEqBand120(data);
                break;
            case 1:
                tvAudioManager.setEqBand500(data);
                break;
            case 2:
                tvAudioManager.setEqBand1500(data);
                break;
            case 3:
                tvAudioManager.setEqBand5k(data);
                break;
            case 4:
                tvAudioManager.setEqBand10k(data);
                break;
            case 5:
                tvAudioManager.setBass(data);
                break;
            case 6:
                tvAudioManager.setTreble(data);
                break;

            default:
                break;
        }
    }

    public boolean freshDataToUI(int index) {

        val_balance = tvAudioManager.getBalance();
        val_120 = osdValue[index][0];
        val_500 = osdValue[index][1];
        val_1500 = osdValue[index][2];
        val_5k = osdValue[index][3];
        val_10k = osdValue[index][4];
        val_bass = osdValue[index][5];
        val_treble = osdValue[index][6];

        val_120_t = tvAudioManager.getEqBand120();
        val_500_t = tvAudioManager.getEqBand500();
        val_1500_t = tvAudioManager.getEqBand1500();
        val_5k_t = tvAudioManager.getEqBand5k();
        val_10k_t = tvAudioManager.getEqBand10k();
        val_bass_t = tvAudioManager.getBass();
        val_treble_t = tvAudioManager.getTreble();
        val_balance_t = tvAudioManager.getBalance();


        textView_sound_mode_bass_text.setText(Integer.toString(val_bass));
        textView_sound_mode_treble_text.setText(Integer.toString(val_treble));
//        textview_volume_balance_val.setText(Integer.toString(val_balance));
        textView_sound_mode_eq1_text.setText(Integer.toString(val_120));
        textView_sound_mode_eq2_text.setText(Integer.toString(val_500));
        textView_sound_mode_eq3_text.setText(Integer.toString(val_1500));
        textView_sound_mode_eq4_text.setText(Integer.toString(val_5k));
        textView_sound_mode_eq5_text.setText(Integer.toString(val_10k));

        seekBar_sound_mode_bass.setProgress(val_bass);
        seekBar_sound_mode_treble.setProgress(val_treble);
        seekBar_sound_mode_eq1.setProgress(val_120);
        seekBar_sound_mode_eq2.setProgress(val_500);
        seekBar_sound_mode_eq3.setProgress(val_1500);
        seekBar_sound_mode_eq4.setProgress(val_5k);
        seekBar_sound_mode_eq5.setProgress(val_10k);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sound_mode_standard:
                soumodeindex = 0;
                tvAudioManager.setAudioSoundMode(soumodeindex);
                freshDataToUI(soumodeindex);
                clearBtnState();
                btn_sound_mode_standard.setBackgroundResource(R.color.colorFocus);
                break;
            case R.id.sound_mode_music:
                soumodeindex = 1;
                tvAudioManager.setAudioSoundMode(soumodeindex);
                freshDataToUI(soumodeindex);
                clearBtnState();
                btn_sound_mode_music.setBackgroundResource(R.color.colorFocus);
                break;
            case R.id.sound_mode_sports:
                soumodeindex = 3;
                tvAudioManager.setAudioSoundMode(soumodeindex);
                freshDataToUI(soumodeindex);

                clearBtnState();
                btn_sound_mode_sports.setBackgroundResource(R.color.colorFocus);
                break;
            case R.id.sound_mode_user:
                soumodeindex = 4;
                tvAudioManager.setAudioSoundMode(soumodeindex);
                freshDataToUI(soumodeindex);
                clearBtnState();
                btn_sound_mode_user.setBackgroundResource(R.color.colorFocus);
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b){
            switch (view.getId()) {
                case R.id.sound_mode_standard:
                    clearBtnState();
                    btn_sound_mode_standard.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.sound_mode_music:
                    clearBtnState();
                    btn_sound_mode_music.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.sound_mode_sports:
                    clearBtnState();
                    btn_sound_mode_sports.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.sound_mode_user:
                    clearBtnState();
                    btn_sound_mode_user.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.sound_mode_bass:
                    clearLLState();
                    ll_sound_mode_bass.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.sound_mode_treble:
                    clearLLState();
                    ll_sound_mode_treble.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.sound_mode_eq1:
                    clearLLState();
                    ll_sound_mode_eq1.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.sound_mode_eq2:
                    clearLLState();
                    ll_sound_mode_eq2.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.sound_mode_eq3:
                    clearLLState();
                    ll_sound_mode_eq3.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.sound_mode_eq4:
                    clearLLState();
                    ll_sound_mode_eq4.setBackgroundResource(R.color.colorFocus);
                    break;
                case R.id.sound_mode_eq5:
                    clearLLState();
                    ll_sound_mode_eq5.setBackgroundResource(R.color.colorFocus);
                    break;
            }
        }else {
            switch (view.getId()){
                case R.id.sound_mode_bass:
                case R.id.sound_mode_treble:
                case R.id.sound_mode_eq1:
                case R.id.sound_mode_eq2:
                case R.id.sound_mode_eq3:
                case R.id.sound_mode_eq4:
                case R.id.sound_mode_eq5:
                    clearLLState();
                    break;
            }
        }

    }

    private void clearLLState() {
        ll_sound_mode_eq1.setBackgroundResource(R.color.colorUnFocus);
        ll_sound_mode_eq2.setBackgroundResource(R.color.colorUnFocus);
        ll_sound_mode_eq3.setBackgroundResource(R.color.colorUnFocus);
        ll_sound_mode_eq4.setBackgroundResource(R.color.colorUnFocus);
        ll_sound_mode_eq5.setBackgroundResource(R.color.colorUnFocus);
        ll_sound_mode_bass.setBackgroundResource(R.color.colorUnFocus);
        ll_sound_mode_treble.setBackgroundResource(R.color.colorUnFocus);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sound_mode_bass:
                LogUtils.d("onProgressChanged-->sound_mode_bass");
                val_bass = i;
                data_position = 5;
                textView_sound_mode_bass_text.setText(Integer.toString(val_bass));
                setData(soumodeindex, data_position, val_bass);
                setEqBand(5, val_bass);
                mHandler.sendEmptyMessageDelayed(5, 300);
                mHandler.sendEmptyMessageDelayed(6, 300);
                break;
            case R.id.sound_mode_treble:
                data_position = 6;
                val_treble = i;
                textView_sound_mode_treble_text.setText(Integer.toString(val_treble));
                setData(soumodeindex, data_position, val_treble);
                setEqBand(6, val_treble);
                mHandler.sendEmptyMessageDelayed(5, 300);
                mHandler.sendEmptyMessageDelayed(6, 300);
                break;
            case R.id.sound_mode_eq1:
                data_position = 0;
                val_120 = i;

                textView_sound_mode_eq1_text.setText(Integer.toString(val_120));
                setData(soumodeindex, data_position, val_120);
                setEqBand(0, val_120);
                mHandler.sendEmptyMessageDelayed(5, 300);
                mHandler.sendEmptyMessageDelayed(6, 300);
                break;
            case R.id.sound_mode_eq2:

                data_position = 1;
                val_500 = i;
                textView_sound_mode_eq2_text.setText(Integer.toString(val_500));
                setData(soumodeindex, data_position, val_500);
                setEqBand(1, val_500);
                mHandler.sendEmptyMessageDelayed(5, 300);
                mHandler.sendEmptyMessageDelayed(6, 300);
                break;
            case R.id.sound_mode_eq3:
                data_position = 2;
                val_1500 = i;
                textView_sound_mode_eq3_text.setText(Integer.toString(val_1500));
                setData(soumodeindex, data_position, val_1500);
                setEqBand(2, val_1500);
                mHandler.sendEmptyMessageDelayed(5, 300);
                mHandler.sendEmptyMessageDelayed(6, 300);
                break;
            case R.id.sound_mode_eq4:
                data_position = 3;
                val_5k = i;
                textView_sound_mode_eq4_text.setText(Integer.toString(val_5k));
                if (soumodeindex < 4) {
                    setData(soumodeindex, data_position, val_5k);
                } else {
                    // setMenudata(data_position, val_5k);
                }
                setEqBand(3, val_5k);
                mHandler.sendEmptyMessageDelayed(5, 300);
                mHandler.sendEmptyMessageDelayed(6, 300);
                break;
            case R.id.sound_mode_eq5:
                data_position = 4;
                val_10k = i;
                textView_sound_mode_eq5_text.setText(Integer.toString(val_10k));
                setData(soumodeindex, data_position, val_10k);
                setEqBand(4, val_10k);
                mHandler.sendEmptyMessageDelayed(5, 300);
                mHandler.sendEmptyMessageDelayed(6, 300);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void onHiddenChanged(boolean hidden) {
        clearBtnState();
        if (soumodeindex == 0) {
            btn_sound_mode_standard.setBackgroundResource(R.color.colorFocus);
        } else if (soumodeindex == 1) {
            btn_sound_mode_music.setBackgroundResource(R.color.colorFocus);
        } else if (soumodeindex == 3) {
            btn_sound_mode_sports.setBackgroundResource(R.color.colorFocus);
        } else {
            btn_sound_mode_user.setBackgroundResource(R.color.colorFocus);
        }
    }
}
