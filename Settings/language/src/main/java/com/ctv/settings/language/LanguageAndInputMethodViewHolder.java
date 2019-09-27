
package com.ctv.settings.language;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.settings.adapter.InputMethodAdapter;

import java.util.List;


public class LanguageAndInputMethodViewHolder implements OnFocusChangeListener {

    private static final String TAG = "LanguageAndInput";

    private final Context ctvContext;

    private final Activity activity;

    private ListView imput_setting_lv;

    private FrameLayout language_setting_fl;

    private InputMethodAdapter inputMethodAdapter;

    private FrameLayout input_setting_fl;

    private FrameLayout fl_language_setting_bg;

    // get all input method
    List<InputMethodInfo> inputMethodInfoList;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    break;
                default:
                    break;
            }
        }
    };

    public LanguageAndInputMethodViewHolder(Context ctvContext) {
        this.ctvContext = ctvContext;
        activity = (Activity) ctvContext;
        initView();
        initAdapter();
        initFocus();
    }

    /**
     * initView(The function of the method)
     * 
     * @Title: initView
     * @Description: TODO
     */
    private void initView() {
        fl_language_setting_bg = (FrameLayout) activity.findViewById(R.id.fl_language_setting_bg);
        startAnimation();
        language_setting_fl = (FrameLayout) activity.findViewById(R.id.language_setting_fl);
        input_setting_fl = (FrameLayout) activity.findViewById(R.id.input_setting_fl);
        imput_setting_lv = (ListView) activity.findViewById(R.id.imput_setting_lv);
    }

    public void endAnimation() {
        // 开始动画
        fl_language_setting_bg.startAnimation(AnimationUtils.loadAnimation(ctvContext,
                R.anim.scale2));
    }

    public void startAnimation() {
        // 开始动画
        fl_language_setting_bg.startAnimation(AnimationUtils
                .loadAnimation(ctvContext, R.anim.scale));
        fl_language_setting_bg.setVisibility(View.VISIBLE);
    }

    /**
     * initAdapter(The function of the method)
     * 
     * @Title: initAdapter
     * @Description: TODO
     */
    private void initAdapter() {
        InputMethodManager inputMethodManager = (InputMethodManager) ctvContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodInfoList = inputMethodManager.getInputMethodList();
        if (inputMethodInfoList != null) {
            inputMethodAdapter = new InputMethodAdapter(ctvContext, inputMethodInfoList);
            imput_setting_lv.setAdapter(inputMethodAdapter);
        }
    }

    /**
     * initFocus(The function of the method)
     * 
     * @Title: initFocus
     * @Description: TODO
     */
    private void initFocus() {
        imput_setting_lv.setOnFocusChangeListener(this);
        language_setting_fl.setOnFocusChangeListener(this);
        input_setting_fl.setOnFocusChangeListener(this);
        if (imput_setting_lv.hasFocus()) {
            imput_setting_lv.setSelector(R.drawable.select_item_bg);
        } else {
            imput_setting_lv.setSelector(R.mipmap.unselect_item_bg);
        }
        imput_setting_lv.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "-----onItemSelected-" + position);
                if (activity.getCurrentFocus() == imput_setting_lv) {
                    inputMethodAdapter.setIndex(position);
                    inputMethodAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "-onNothingSelected-useless");
            }
        });
    }

    public void setClickListener(OnClickListener listener) {
        language_setting_fl.setOnClickListener(listener);
        input_setting_fl.setOnClickListener(listener);
    }

    public void setImputListener(OnItemClickListener listener) {
        imput_setting_lv.setOnItemClickListener(listener);
    }

    @Override
    public void onFocusChange(View view, boolean has_focus) {
        int id = view.getId();
        if (id == R.id.language_setting_fl) {
            Log.i(TAG, " onFocusChange language_imput_lv");
            if (has_focus) {
                ((TextView) language_setting_fl.getChildAt(0)).setTextColor(ctvContext
                        .getResources().getColor(R.color.white));
                ((ImageView) language_setting_fl.getChildAt(1))
                        .setBackgroundResource(R.mipmap.item_rigth_focus);
            } else {
                ((TextView) language_setting_fl.getChildAt(0)).setTextColor(ctvContext
                        .getResources().getColor(R.color.half_white));
                ((ImageView) language_setting_fl.getChildAt(1))
                        .setBackgroundResource(R.mipmap.item_rigth_unfocus);
            }
        } else if (id == R.id.input_setting_fl) {
            Log.i(TAG, " onFocusChange language_imput_lv");
            if (has_focus) {
                ((TextView) input_setting_fl.getChildAt(0)).setTextColor(ctvContext
                        .getResources().getColor(R.color.white));
                ((ImageView) input_setting_fl.getChildAt(1))
                        .setBackgroundResource(R.mipmap.item_rigth_focus);
            } else {
                ((TextView) input_setting_fl.getChildAt(0)).setTextColor(ctvContext
                        .getResources().getColor(R.color.half_white));
                ((ImageView) input_setting_fl.getChildAt(1))
                        .setBackgroundResource(R.mipmap.item_rigth_unfocus);
            }
        } else if (id == R.id.imput_setting_lv) {
            Log.i(TAG, " onFocusChange imput_setting_lv");
            if (has_focus) {
                imput_setting_lv.setSelector(R.drawable.select_item_bg);
            } else {
                imput_setting_lv.setSelector(R.mipmap.unselect_item_bg);
            }
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (activity.getCurrentFocus() == language_setting_fl) {
                    final LanguageSelectDialog selectDialog = new LanguageSelectDialog(ctvContext);
                    selectDialog.show();

                    handleDialog(selectDialog);
                } else if (activity.getCurrentFocus() == input_setting_fl) {
                    final KeyboardSelectDialog keyboadrSelectDialog = new KeyboardSelectDialog(
                            ctvContext);
                    keyboadrSelectDialog.show();

                    handleDialog(keyboadrSelectDialog);
                } else {
                    InputMethodInfo inputMethodInfo = inputMethodInfoList.get(imput_setting_lv
                            .getSelectedItemPosition());
                    String inputSetting = inputMethodInfo.getSettingsActivity();
                    Log.d(TAG, "---inputSetting:" + inputSetting);
                    try {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setClassName(inputMethodInfo.getPackageName(), inputSetting);
                        ctvContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ctvContext, R.string.not_found_input, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (activity.getCurrentFocus() == input_setting_fl) {
                    inputMethodAdapter.setIndex(0);
                    inputMethodAdapter.notifyDataSetChanged();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (activity.getCurrentFocus() == imput_setting_lv) {
                    if (imput_setting_lv.getSelectedItemPosition() == 0) {
                        Log.i(TAG, "---KEYCODE_DPAD_UP--etSelectedItemPosition()==0-");
                        inputMethodAdapter.setIndex(-1);
                        inputMethodAdapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 显示和隐藏
     * 
     * @param isShow
     */
    public void isShow(boolean isShow) {
        int visible = isShow ? View.VISIBLE : View.INVISIBLE;
        fl_language_setting_bg.setVisibility(visible);
    }

    /**
     * 处理对话框
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
