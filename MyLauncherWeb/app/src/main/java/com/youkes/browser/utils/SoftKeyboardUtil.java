package com.youkes.browser.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by xuming on 2015/10/22.
 */
public class SoftKeyboardUtil {

    public static void hideSoftKeyboard(Activity act) {
        InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null ) {
            View localView = act.getCurrentFocus();
            if(localView != null && localView.getWindowToken() != null ) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }
    public static void isSoftKeyboard(Activity ac){
        InputMethodManager inputMethodManager = (InputMethodManager) ac.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
