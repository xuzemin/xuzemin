package com.ctv.settings.language;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.helper.LangInputHelper;
import com.ctv.settings.utils.L;

import java.util.List;

/**
 * 语言和输入法UI
 * @author wanghang
 * @date 2019/09/18
 */
public class LanguageInpoutViewHolder extends BaseViewHolder implements View.OnClickListener{
    private final static String TAG = LanguageInpoutViewHolder.class.getSimpleName();
    private View mainLanguage;
    private View itemLanguage;
    private TextView itemLanguageTxt;
    private View itemKeyboard;
    private TextView itemKeyboardTxt;
    private View itemInputMethod;

    private Handler mHandler;

    public LanguageInpoutViewHolder(Activity activity) {
        super(activity);
    }

    public LanguageInpoutViewHolder(Activity activity, Handler handler) {
        super(activity);
        this.mHandler = handler;
    }

    /**
     * 初始化UI
     *
     * @param activity
     */
    @Override
    public void initUI(Activity activity) {
        mainLanguage = activity.findViewById(R.id.main_language);
        itemLanguage = activity.findViewById(R.id.item_language);
        itemLanguageTxt = (TextView) activity.findViewById(R.id.item_language_txt);
        itemKeyboard = activity.findViewById(R.id.item_keyboard);
        itemKeyboardTxt = (TextView) activity.findViewById(R.id.item_keyboard_txt);
        itemInputMethod = activity.findViewById(R.id.item_input_method);
    }

    /**
     * 初始化数据
     *
     * @param activity
     */
    @Override
    public void initData(Activity activity) {
        itemLanguageTxt.setText("" + LangInputHelper.getCountryName(activity));

        new Thread(()->{
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            List<InputMethodInfo> inputMethodInfoList = inputMethodManager.getInputMethodList();

            if (inputMethodInfoList != null && inputMethodInfoList.size() > 0){
                String default_input_method = Settings.Secure.getString(activity.getContentResolver(),
                        Settings.Secure.DEFAULT_INPUT_METHOD);
                for (InputMethodInfo inputMethodInfo:inputMethodInfoList){
                    if (default_input_method.equals(inputMethodInfo.getId())) {
                        if (mHandler != null){
                            mHandler.post(()->{
                                itemKeyboardTxt.setText(inputMethodInfo.loadLabel(activity.getPackageManager())
                                        + "");
                            });
                        }

                    }
                }
            }
        }
        ).start();


    }

    /**
     * 初始化监听
     */
    @Override
    public void initListener() {
        itemLanguage.setOnClickListener(this);
        itemKeyboard.setOnClickListener(this);
        itemInputMethod.setOnClickListener(this);
    }

    /**
     * 刷新指定view
     *
     * @param view
     */
    @Override
    public void refreshUI(View view) {

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

    /**
     * 显示和隐藏
     *
     * @param isShow
     */
    public void isShow(boolean isShow) {
        int visible = isShow ? View.VISIBLE : View.GONE;
        mainLanguage.setVisibility(visible);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.item_language) { // 语言
            L.d(TAG, "mOnClickListener 语言");
            final LanguageSelectDialog selectDialog = new LanguageSelectDialog(
                    mActivity);
            selectDialog.show();

//            handleDialog(selectDialog);
        }
        if (id == R.id.item_keyboard) { // 键盘
            L.d(TAG, "mOnClickListener 键盘");
            final KeyboardSelectDialog keyboadrSelectDialog = new KeyboardSelectDialog(
                    mActivity);
            keyboadrSelectDialog.show();
//            handleDialog(keyboadrSelectDialog);
        }
        if (id == R.id.item_input_method) { // 输入法设置
            L.d(TAG, "mOnClickListener 输入法设置");
        }
    }
}
