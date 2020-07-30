package com.ctv.settings.language.holder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.settings.language.activity.LanguageSelectActivity;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.language.helper.LangInputHelper;
import com.ctv.settings.language.activity.InputMethodActivity;
import com.ctv.settings.language.R;
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

    private List<InputMethodInfo> inputMethodInfoList;

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
        //itemLanguageTxt.setText("" + LangInputHelper.getCountryName(activity));

        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodInfoList = inputMethodManager.getInputMethodList();

        new Thread(()->{
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.item_language) { // 语言
            L.d(TAG, "mOnClickListener 语言");
            mActivity.startActivity(new Intent(mActivity, LanguageSelectActivity.class));
        }
        if (id == R.id.item_keyboard) { // 键盘
            L.d(TAG, "mOnClickListener 键盘");
            mActivity.startActivity(new Intent(mActivity, InputMethodActivity.class));
        }
        if (id == R.id.item_input_method) { // 输入法设置
            L.d(TAG, "mOnClickListener 输入法设置");

            InputMethodInfo inputMethodInfoTmp = null;

            if (inputMethodInfoList != null && inputMethodInfoList.size() > 0){
                String default_input_method = Settings.Secure.getString(mActivity.getContentResolver(),
                        Settings.Secure.DEFAULT_INPUT_METHOD);
                for (InputMethodInfo inputMethodInfo:inputMethodInfoList){
                    if (default_input_method.equals(inputMethodInfo.getId())) {
                        inputMethodInfoTmp = inputMethodInfo;
                        break;
                    }
                }
            }

            if (inputMethodInfoTmp != null){
                String inputSetting = inputMethodInfoTmp.getSettingsActivity();
                Log.d(TAG, "---inputSetting:" + inputSetting);
                try {
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setClassName(inputMethodInfoTmp.getPackageName(), inputSetting);
//                    PackageManager pm = mActivity.getPackageManager(); // 获得PackageManager对象
//                    final Intent intent = pm.getLaunchIntentForPackage(inputMethodInfoTmp.getPackageName());
//                    mActivity.startActivity(intent);



                    String pkg = inputMethodInfoTmp.getPackageName();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName(pkg, pkg + ".SettingsActivity");
                    mActivity.startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, R.string.not_found_input, Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
