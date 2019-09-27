package com.ctv.settings.language;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ctv.settings.adapter.KeyboardDailogAdapter;
import com.ctv.settings.utils.L;

import java.util.List;

/**
 * 输入法设置
 * @author wanghang
 * @date 2019/09/23
 */
public class InputMethodActivity extends AppCompatActivity {

    private static final String TAG = "InputMethodActivity";

    private Context ctvContext;

    private ListView keyboard_lv;

    private String mLocaleCountry;

    private KeyboardDailogAdapter keyboardDailogAdapter;

    private List<InputMethodInfo> inputMethodInfoList;

    private String default_input_method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_method);

        this.ctvContext = this;

        initView();
    }

    /**
     * 初始化UI
     */
    private void initView(){
        Configuration conf = ctvContext.getResources().getConfiguration();
        mLocaleCountry = conf.locale.getCountry();
        L.d(TAG, "localeCountry, " + mLocaleCountry);
        keyboard_lv = (ListView) findViewById(R.id.keyboard_listView);
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

                finish();
            }
        });
    }

}
