package com.ctv.settings.greneral;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LockScreenDialog extends Dialog {

    private static final String TAG = "LockScreenDialog";

    private final Context ctvContext;

    private LinearLayout lockscreen_ly;

    private RelativeLayout relativeLayout;

    private ImageView sys_swich_iv;

    private RelativeLayout pass_change;

    private View view;

    private String mPassWord;

    public LockScreenDialog(Context ctvContext) {
        super(ctvContext);
        this.ctvContext = ctvContext;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.general_custom_dialog_layout);
        setWindowStyle();
        findViews();
    }

    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = ctvContext.getResources();
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
        relativeLayout = (RelativeLayout) findViewById(R.id.screen_lock_switch);
        sys_swich_iv = (ImageView) findViewById(R.id.sys_swich_iv);
        pass_change = (RelativeLayout) findViewById(R.id.password_change);
        view = findViewById(R.id.view);
        boolean flag = "on".equals(SystemProperties.get("persist.sys.lockScreen"));
        mPassWord = ctvContext.getSharedPreferences("lock-screen", Context.MODE_WORLD_READABLE)
                .getString("password", " ");
        Log.d(TAG, "pass word:" + mPassWord);
        if (!" ".equals(mPassWord) && flag) {
            sys_swich_iv.setBackgroundResource(R.mipmap.on);
        } else {
            sys_swich_iv.setBackgroundResource(R.mipmap.off);
        }
        relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mPassWord = ctvContext.getSharedPreferences("lock-screen",
                        Context.MODE_WORLD_READABLE).getString("password", " ");
                if ("on".equals(SystemProperties.get("persist.sys.lockScreen"))) {
                    SystemProperties.set("persist.sys.lockScreen", "off");
                    sys_swich_iv.setBackgroundResource(R.mipmap.off);
                    /*
                     * pass_change.setVisibility(View.GONE);
                     * view.setVisibility(View.GONE);
                     */
                } else {
                    /*
                     * pass_change.setVisibility(View.VISIBLE);
                     * view.setVisibility(View.VISIBLE);
                     */
                    if (" ".equals(mPassWord)) {
                        Toast.makeText(ctvContext, ctvContext.getResources().getString(R.string.greneral_lock_screen_tip), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SystemProperties.set("persist.sys.lockScreen", "on");
                    sys_swich_iv.setBackgroundResource(R.mipmap.on);
                    Intent intent = new Intent("com.ctv.lockscreen.action.LockScreen");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctvContext.startActivity(intent);
                    //CtvPictureManager.getInstance().disableBacklight();
                    //Settings.System.putInt(ctvContext.getContentResolver(), "isSeperateHear", 1);
                    System.exit(0);
                }
            }
        });

        pass_change.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SetPWDDialog setPWDDialog = new SetPWDDialog(ctvContext);
                setPWDDialog.show();
                handleDialog(setPWDDialog);
            }
        });
    }

    /**
     * 显示和隐藏
     *
     * @param isShow
     */
    public void isShow(boolean isShow) {
        int visible = isShow ? View.VISIBLE : View.INVISIBLE;
        lockscreen_ly.setVisibility(visible);
    }

    /**
     * 处理dialog
     *
     * @param dialog
     */
    private void handleDialog(final Dialog dialog) {
        isShow(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                isShow(true);
            }
        });
    }
}

