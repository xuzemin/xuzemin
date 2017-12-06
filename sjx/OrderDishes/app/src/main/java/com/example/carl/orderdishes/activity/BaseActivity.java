package com.example.carl.orderdishes.activity;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.carl.orderdishes.view.MyProgressDialog;

/**
 * Created by Carl on 2017/10/30.
 *
 */

public class BaseActivity extends FragmentActivity {
    private final static String LOG_TAG = "OrderDishes";
    private MyProgressDialog progressDialog;
    private boolean isLog = true;
    public void LOG_e(String jstr){
        if(isLog) {
            Log.e(LOG_TAG, jstr);
        }
    }
    public void showProgress() {
        try {
            if (progressDialog == null) {
                progressDialog = new MyProgressDialog(this);
            }
            if (!progressDialog.isShowing() && !this.isFinishing())
                progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgress() {
        try {
            if (progressDialog != null)
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
