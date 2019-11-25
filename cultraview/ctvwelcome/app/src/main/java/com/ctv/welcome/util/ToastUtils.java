
package com.ctv.welcome.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static String oldMsg;

    private static long oneTime = 0;

    private static Toast toast = null;

    private static long twoTime = 0;

    public static void showToast(Context context, int strRes) {
        String string = context.getResources().getString(strRes);
        if (toast == null) {
            toast = Toast.makeText(context, string, 1);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (!string.equals(oldMsg)) {
                oldMsg = string;
                toast.setText(string);
                toast.show();
            } else if (twoTime - oneTime > 0) {
                toast.show();
            }
        }
        oneTime = twoTime;
    }
}
