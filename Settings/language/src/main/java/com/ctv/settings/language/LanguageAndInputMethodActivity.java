
package com.ctv.settings.language;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ctv.settings.utils.ExitAppUtil;
import com.ctv.settings.utils.Tools;

public class LanguageAndInputMethodActivity extends Activity {

    protected static final String TAG = "LanguageAndInputMethodActivity";

    private LanguageAndInputMethodViewHolder languageAndInputMethodViewHolder;

    private LanguageAndInputMethodActivity ctvContext;

    private FrameLayout fl_language_setting_bg;

    private int height;

    private int width;

    private float x_point;

    private float y_point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ctvContext = LanguageAndInputMethodActivity.this;
        ExitAppUtil.getInstance().addActivity(LanguageAndInputMethodActivity.this);
//        Tools.setThemeByTime(ctvContext);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_and_inputmethod);
        fl_language_setting_bg = (FrameLayout) findViewById(R.id.fl_language_setting_bg);
        if (languageAndInputMethodViewHolder == null) {
            languageAndInputMethodViewHolder = new LanguageAndInputMethodViewHolder(ctvContext);
        }
        setkListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            initCountViewSize();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    private void initCountViewSize() {
        height = fl_language_setting_bg.getMeasuredHeight();
        width = fl_language_setting_bg.getMeasuredWidth();
        x_point = fl_language_setting_bg.getX();
        y_point = fl_language_setting_bg.getY();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (touchOutSide(event)) {
                Tools.keyInjection(KeyEvent.KEYCODE_BACK);
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean touchOutSide(MotionEvent event) {
        float down_point_x = event.getRawX();
        float down_point_y = event.getRawY();
        if (height == 0) {
            initCountViewSize();
        }
        if (down_point_x < x_point || down_point_y < y_point || (down_point_y > (height + y_point))
                || down_point_x > x_point + width) {
            return true;
        }
        return false;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (languageAndInputMethodViewHolder.onKeyDown(keyCode, event)) {
//            return true;
//        }
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                Log.i(TAG, "--onKeyDown:" + CtvSettings.isDirect);
//                if (CtvSettings.isDirect) {
//                    languageAndInputMethodViewHolder.endAnimation();
//                    Intent intent = new Intent();
//                    intent.setAction(Constants.CTVSETTINGS_ACTION);
//                    intent.putExtra("StartPageNumber", Constants.SETTING_PAGE_SETTING);
//                    intent.putExtra("gridViewSelect", Constants.SETTING_ITEM_INPUT_METHOD);
//                    startActivity(intent);
//                }
//                finish();
//                return true;
//            default:
//                break;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    private void setkListener() {
        languageAndInputMethodViewHolder.setClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.language_setting_fl) {
                    final LanguageSelectDialog selectDialog = new LanguageSelectDialog(
                            ctvContext);
                    selectDialog.show();

                    languageAndInputMethodViewHolder.isShow(false);
                    selectDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface di) {
                            selectDialog.dismiss();
                            languageAndInputMethodViewHolder.isShow(true);
                        }
                    });
                }
                if (id == R.id.input_setting_fl) {
                    final KeyboardSelectDialog keyboadrSelectDialog = new KeyboardSelectDialog(
                            ctvContext);
                    keyboadrSelectDialog.show();

                    languageAndInputMethodViewHolder.isShow(false);
                    keyboadrSelectDialog
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                @Override
                                public void onCancel(DialogInterface di) {
                                    keyboadrSelectDialog.dismiss();
                                    languageAndInputMethodViewHolder.isShow(true);
                                }
                            });
                }
            }
        });
        languageAndInputMethodViewHolder.setImputListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodInfo inputMethodInfo = languageAndInputMethodViewHolder.inputMethodInfoList
                        .get(position);
                String inputSetting = inputMethodInfo.getSettingsActivity();
                Log.d(TAG, "---inputSetting:" + inputSetting);
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName(inputMethodInfo.getPackageName(), inputSetting);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ctvContext, R.string.not_found_input, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
