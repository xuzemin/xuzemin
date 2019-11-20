
package com.ctv.welcome.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Utils {
    public static View getView(Context context, int layoutId) {
        return LayoutInflater.from(context).inflate(layoutId, null);
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, 0).show();
    }

    public static int px2dp(Context context, int px) {
        return (int) (((double) (((float) px) / context.getResources().getDisplayMetrics().density)) + 0.5d);
    }

    public static int dp2px(Context context, int dp) {
        return (int) ((((float) dp) * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int getIdByCategoryName(String categoryName) {
        int[] categoryId = new int[] {
                R.string.welcome_module, R.string.company, R.string.government, R.string.medical,
                R.string.education, R.string.ad, R.string.traffic, R.string.car,
                R.string.manufacture, R.string.clothing, R.string.building, R.string.tobacco
        };
        for (int i = 0; i < categoryId.length; i++) {
            if (getCategoryNameById(categoryId[i]).equals(categoryName)) {
                return categoryId[i];
            }
        }
        return -1;
    }

    public static String getCategoryNameById(int id) {
        String name = VIPApplication.getContext().getString(R.string.company_zh);
        switch (id) {
            case R.string.welcome_module:
                return VIPApplication.getContext().getString(R.string.welcome_module_zh);
            case R.string.ad:
                return VIPApplication.getContext().getString(R.string.advertising_zh);
            case R.string.building:
                return VIPApplication.getContext().getString(R.string.building_zh);
            case R.string.car:
                return VIPApplication.getContext().getString(R.string.car_zh);
            case R.string.clothing:
                return VIPApplication.getContext().getString(R.string.clothing_zh);
            case R.string.company:
                return VIPApplication.getContext().getString(R.string.company_zh);
            case R.string.education:
                return VIPApplication.getContext().getString(R.string.education_zh);
            case R.string.government:
                return VIPApplication.getContext().getString(R.string.goverment_zh);
            case R.string.manufacture:
                return VIPApplication.getContext().getString(R.string.manufacture_zh);
            case R.string.medical:
                return VIPApplication.getContext().getString(R.string.medical_zh);
            case R.string.tobacco:
                return VIPApplication.getContext().getString(R.string.tobacco_zh);
            case R.string.traffic:
                return VIPApplication.getContext().getString(R.string.traffic_zh);
            default:
                return name;
        }
    }
}
