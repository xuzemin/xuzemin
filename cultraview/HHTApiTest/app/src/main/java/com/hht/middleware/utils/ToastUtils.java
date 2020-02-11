package com.hht.middleware.utils;

import android.text.TextUtils;
import android.widget.Toast;

import com.hht.middleware.application.MiddleWareApplication;

/**
 * Author: chenhu
 * Time: 2019/12/13 15:09
 * Description do somethings
 */
public class ToastUtils {

    public static void showLongToast(String toast) {
        if (TextUtils.isEmpty(toast)) {
            return;
        }
        Toast.makeText(MiddleWareApplication.getInstance().getApplicationContext(),
                toast, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(String toast) {
        if (TextUtils.isEmpty(toast)) {
            return;
        }
        Toast.makeText(MiddleWareApplication.getInstance().getApplicationContext(),
                toast, Toast.LENGTH_SHORT).show();
    }
}
