package com.ctv.settings.language.holder;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.base.IBaseViewHolder;
import com.ctv.settings.language.adapter.KeyboardDailogAdapter;
import com.ctv.settings.utils.L;

import java.util.List;

/**
 * @Description: 输入法选择
 * @Author: wanghang
 * @CreateDate: 2019/9/28 11:20
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/9/28 11:20
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class InputMethodViewHolder implements IBaseViewHolder {
    private static final String TAG = "InputMethodViewHolder";

    private Activity ctvContext;
    private Handler mHandler;

    private ListView keyboard_lv;

    private String mLocaleCountry;

    private KeyboardDailogAdapter keyboardDailogAdapter;

    private List<InputMethodInfo> inputMethodInfoList;

    private String default_input_method;

    public InputMethodViewHolder(Activity activity) {
        this.ctvContext = activity;

        initUI(ctvContext);
    }

    public InputMethodViewHolder(Activity activity, Handler handler) {
        this.ctvContext = activity;
        this.mHandler = handler;

        initUI(ctvContext);
    }

    /**
     * 初始化UI
     *
     * @param activity
     */
    @Override
    public void initUI(Activity activity) {
        initBackTopLayout();
        initMainView();
    }

    /**
     * 初始化数据
     *
     * @param activity
     */
    @Override
    public void initData(Activity activity) {

    }

    /**
     * 初始化监听
     */
    @Override
    public void initListener() {

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
     * 返回布局设置
     */
    private void initBackTopLayout(){
        // 返回按钮
        ImageView backBtn = (ImageView) ctvContext.findViewById(R.id.back_btn);
        backBtn.setOnClickListener((view)->{
            ctvContext.finish();
        });

        // 设置title
        TextView backTitle = (TextView) ctvContext.findViewById(R.id.back_title);
        backTitle.setText(R.string.keyboard_setting);
    }

    /**
     * 初始化主体UI
     */
    private void initMainView(){
        Configuration conf = ctvContext.getResources().getConfiguration();
        mLocaleCountry = conf.locale.getCountry();
        L.d(TAG, "localeCountry, " + mLocaleCountry);
        keyboard_lv = (ListView) ctvContext.findViewById(R.id.keyboard_listView);
        InputMethodManager inputMethodManager = (InputMethodManager) ctvContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodInfoList = inputMethodManager.getInputMethodList();
        default_input_method = Settings.Secure.getString(ctvContext.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        keyboardDailogAdapter = new KeyboardDailogAdapter(ctvContext, inputMethodInfoList,
                default_input_method);
        keyboard_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        keyboard_lv.setAdapter(keyboardDailogAdapter);

        keyboard_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                keyboardDailogAdapter.setSelect(position);
                keyboardDailogAdapter.notifyDataSetChanged();
                String inputMethod = inputMethodInfoList.get(position).getId();
                Settings.Secure.putString(ctvContext.getContentResolver(),
                        Settings.Secure.DEFAULT_INPUT_METHOD, inputMethod != null ? inputMethod
                                : default_input_method);

                ctvContext.finish();
            }
        });
    }
}
