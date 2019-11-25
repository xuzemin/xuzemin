
package com.ctv.welcome.util;

import android.app.ProgressDialog;
import android.content.Context;

public final class ProgressDialogUtil {
    private static ProgressDialog progressDialog;

    public static void show(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void dimissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void show(Context context, int res) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(res));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void setCanelable(boolean canelable) {
        progressDialog.setCancelable(canelable);
    }
}
