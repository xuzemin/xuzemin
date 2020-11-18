package com.ctv.settings.security.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;

public class LockScreenActivity extends BaseActivity {

    private static final String TAG = "LockScreenDialog";

    private LinearLayout lockscreen_ly;

    private RelativeLayout relativeLayout;

    private ImageView sys_swich_iv;

    private RelativeLayout pass_change;


    private String mPassWord;

    private LinearLayout pwd_ly;

    private static final String PWKEY = "password";

//   private SharedPreferences mPWPreferences;

    private LinearLayout old_pwd, first_pwd, second_pwd;

    private EditText et_old_pwd, et_first_pwd, et_sec_pwd;

    private Button pwd_save, pwd_cancel;

    private View old_view;

    private String mOldStr;

    private String mFirstStr;

    private String mSecStr;

    private final String Admin_Sec = "666888";

    private String mSetPassWord;
    private ImageView back_btn;
    private TextView back_title;
    private LinearLayout general_top_back_layout;
    private Context mPswContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.greneral_screen_main_layout);
        //setWindowStyle();
        findViews();
        findPswViews();
        isShow(true);
    }
    /**
     * findViews(The function of the method)
     *
     * @Title: findViews
     * @Description: TODO
     */
    private void findPswViews() {
        old_pwd = (LinearLayout)findViewById(R.id.old_pwd);
        old_view = findViewById(R.id.old_view);
        et_old_pwd = (EditText) findViewById(R.id.et_old_pwd);
        first_pwd = (LinearLayout) findViewById(R.id.first_pwd);
        et_first_pwd = (EditText) findViewById(R.id.et_first_pwd);
        second_pwd = (LinearLayout) findViewById(R.id.second_pwd);
        et_sec_pwd = (EditText) findViewById(R.id.et_sec_pwd);
        pwd_save = (Button) findViewById(R.id.pwd_save);
        pwd_cancel = (Button) findViewById(R.id.pwd_cancel);
        mPswContext = getTargetContext();
//        if(mPswContext != null){
//            mPWPreferences = getTargetContext().getSharedPreferences("lock-screen", Context.MODE_PRIVATE);
//        }
    }
    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = getResources();
        Drawable drab = res.getDrawable(R.drawable.transparency_bg);
        w.setBackgroundDrawable(drab);
        WindowManager.LayoutParams lp = w.getAttributes();
        final float scale = res.getDisplayMetrics().density;
        // In the mid-point to calculate the offset x and y
        lp.y = (int) (-36 * scale + 0.5f);
        lp.width = (int) (680 * scale + 0.5f);
        lp.height = (int) (408 * scale + 0.5f);
        // Range is from 1.0 for completely opaque to 0.0 for no dim.
        w.setDimAmount(0.0f);
        w.setAttributes(lp);
    }

    /**
     * findViews(The function of the method)
     *
     * @Title: findViews
     * @Description: TODO
     */
    private void findViews() {
        lockscreen_ly = (LinearLayout) findViewById(R.id.lockscreen_ly);
        general_top_back_layout = (LinearLayout)findViewById(R.id.general_top_back_layout);
        back_btn = (ImageView)findViewById(R.id.back_btn);
        back_title = (TextView)findViewById(R.id.back_title);
        general_top_back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pwd_ly.getVisibility() == View.VISIBLE){
                    isShow(true);
                }else{
                    finish();
                }
            }
        });
        pwd_ly = (LinearLayout) findViewById(R.id.pwd_ly);
        relativeLayout = (RelativeLayout) findViewById(R.id.screen_lock_switch);
        sys_swich_iv = (ImageView) findViewById(R.id.sys_swich_iv);
        pass_change = (RelativeLayout) findViewById(R.id.password_change);
        boolean flag = "on".equals(SystemProperties.get("persist.sys.lockScreen"));
        mPassWord = getTargetContextPSW();
        Log.d(TAG, "pass word:" + mPassWord);
        Log.d("zhu_S","mPassWord = " + mPassWord);
        if (mPassWord.equals("")) {
            Log.d("zhu1_S","mPassWord = " + mPassWord);
            Settings.System.putString(getContentResolver(),"lock-screen","123456");
        }
        if (!" ".equals(mPassWord) && flag) {
            sys_swich_iv.setBackgroundResource(R.mipmap.on);
        } else {
            sys_swich_iv.setBackgroundResource(R.mipmap.off);
        }
        relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mPassWord = getTargetContextPSW();
                if ("on".equals(SystemProperties.get("persist.sys.lockScreen"))) {
                    SystemProperties.set("persist.sys.lockScreen", "off");
                    sys_swich_iv.setBackgroundResource(R.mipmap.off);
                } else {
                    /*if ("".equals(mPassWord)) {
                        //Toast.makeText(LockScreenActivity.this, getResources().getString(R.string.greneral_lock_screen_tip), Toast.LENGTH_SHORT).show();
                        return;
                    }*/
                    SystemProperties.set("persist.sys.lockScreen", "on");
                    sys_swich_iv.setBackgroundResource(R.mipmap.on);
                    Intent intent = new Intent("com.ctv.lockscreen.action.LockScreen");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        pass_change.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isShow(false);
            }
        });
    }

    /**
     * 显示和隐藏
     *
     * @param isShow
     */
    public void isShow(boolean isShow) {
        if(isShow){
            back_title.setText(getResources().getString(R.string.item_lock_screen));
            lockscreen_ly.setVisibility(View.VISIBLE);
            pwd_ly.setVisibility(View.GONE);
        }else{
            back_title.setText(getResources().getString(R.string.greneral_set_password));
            lockscreen_ly.setVisibility(View.GONE);
            pwd_ly.setVisibility(View.VISIBLE);
            mSetPassWord = getTargetContextPSW();
            if ("".equals(mSetPassWord)) {
                old_pwd.setVisibility(View.GONE);
                old_view.setVisibility(View.GONE);
                setNewPassWord(false);
            } else {
                old_pwd.setVisibility(View.VISIBLE);
                old_pwd.setVisibility(View.VISIBLE);
                setNewPassWord(true);
            }
        }
    }

    /**
     * 处理dialog
     *
     *
     */
    private void handleDialog() {
        isShow(false);
    }

    private void setNewPassWord(final boolean haveOldStr) {
        pwd_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (haveOldStr) {
                    mOldStr = et_old_pwd.getText().toString();
                    if (!mOldStr.equals(mSetPassWord)) {
                        Toast.makeText(LockScreenActivity.this, getResources().getString(R.string.greneral_password_tip_seven), Toast.LENGTH_LONG).show();
                        et_old_pwd.getText().clear();
                        et_first_pwd.getText().clear();
                        et_sec_pwd.getText().clear();
                        return;
                    }
                }
                mFirstStr = et_first_pwd.getText().toString();
                mSecStr = et_sec_pwd.getText().toString();
                Log.d(TAG, "zhu..firstStr:" + mFirstStr + "..secStr:" + mSecStr);
                if (mFirstStr.equals(mSecStr) && mFirstStr.length() == 6) {
                    if(mFirstStr.equals(Admin_Sec)){
                        Toast.makeText(LockScreenActivity.this, getResources().getString(R.string.greneral_password_tip_admin), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // save password...
                    setTargetContextPSW(mSecStr);
                    //mPWPreferences.edit().putString(PWKEY, mSecStr).commit();
                    Toast.makeText(LockScreenActivity.this, getResources().getString(R.string.greneral_password_tip_four), Toast.LENGTH_LONG).show();
                    // 进入锁屏状态
                    // keyInjection(KeyEvent.KEYCODE_CLEAR);
                    /*
                     * if
                     * ("on".equals(SystemProperties.get("persist.sys.lockScreen"
                     * ))) { CtvPictureManager.getInstance().disableBacklight();
                     * Settings.System.putInt(ctvContext.getContentResolver(),
                     * "isSeperateHear", 1); }
                     */
                    isShow(true);
                    return;
                } else if (!mFirstStr.equals(mSecStr)) {
                    Toast.makeText(LockScreenActivity.this, getResources().getString(R.string.greneral_password_tip_nine), Toast.LENGTH_SHORT).show();
                } else if (mFirstStr.length() != 6) {
                    Toast.makeText(LockScreenActivity.this, getResources().getString(R.string.greneral_password_tip_two), Toast.LENGTH_SHORT).show();
                }
                et_first_pwd.getText().clear();
                et_sec_pwd.getText().clear();
            }
        });
        pwd_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                  isShow(true);
            }
        });
    }
    private Context getTargetContext() {
        try {
            return createPackageContext("com.cultraview.settings", Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
    private String getTargetContextPSW() {
        Context context = getTargetContext();
        String psw = null;
        if(context == null){
            psw = Settings.System.getString(getContentResolver(),"lock-screen");
            Log.d("zhu_S","psw = " +psw);
            return  psw == null ? "":psw;
        }else{
            return getTargetContext().getSharedPreferences("lock-screen", Context.MODE_PRIVATE).getString("lock-screen","");
        }
    }

    private void setTargetContextPSW(String psw) {
//        if(mPWPreferences == null){
            Settings.System.putString(getContentResolver(),"lock-screen",psw);

//        }else{
//            mPWPreferences.edit().putString(PWKEY, psw).commit();
//            //return getTargetContext().getSharedPreferences("lock-screen", Context.MODE_PRIVATE).getString("lock-screen","");
//        }
    }
}

