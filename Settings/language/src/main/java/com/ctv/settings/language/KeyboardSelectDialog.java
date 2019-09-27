
package com.ctv.settings.language;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ctv.settings.adapter.KeyboardDailogAdapter;

import java.util.List;

public class KeyboardSelectDialog extends Dialog {

    private static final String TAG = "LanguageSelectDialog";

    private final Context ctvContext;

    private ListView keyboard_lv;

    private String mLocaleCountry;

    private KeyboardDailogAdapter keyboardDailogAdapter;

    private List<InputMethodInfo> inputMethodInfoList;

    private String default_input_method;

    public KeyboardSelectDialog(Context ctvContext) {
        super(ctvContext);
        this.ctvContext = ctvContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.keyboard_dialog);
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
     * init compontent.
     */
    private void findViews() {
        setCanceledOnTouchOutside(true);

        Configuration conf = ctvContext.getResources().getConfiguration();
        mLocaleCountry = conf.locale.getCountry();
        Log.d(TAG, "localeCountry, " + mLocaleCountry);
        keyboard_lv = (ListView) findViewById(R.id.keyboard_lv);
        InputMethodManager inputMethodManager = (InputMethodManager) ctvContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodInfoList = inputMethodManager.getInputMethodList();
        default_input_method = Settings.Secure.getString(ctvContext.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        keyboardDailogAdapter = new KeyboardDailogAdapter(ctvContext, inputMethodInfoList,
                default_input_method);
        keyboard_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        keyboard_lv.setAdapter(keyboardDailogAdapter);

        keyboard_lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                keyboardDailogAdapter.setSelect(position);
                keyboardDailogAdapter.notifyDataSetChanged();
                String inputMethod = inputMethodInfoList.get(position).getId();
                Settings.Secure.putString(ctvContext.getContentResolver(),
                        Settings.Secure.DEFAULT_INPUT_METHOD, inputMethod != null ? inputMethod
                                : default_input_method);
                dismiss();
            }
        });
    }

}
