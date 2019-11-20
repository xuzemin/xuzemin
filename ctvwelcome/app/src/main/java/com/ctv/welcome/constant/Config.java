
package com.ctv.welcome.constant;

import android.os.Environment;
import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Config {
    public static final int AD_FIX_COUNT = 3;

    public static final int BUILDING_FIX_COUNT = 2;

    public static final int CAR_FIX_COUNT = 2;

    public static final int CATEGORY_CONTENT_NUM = 9;

    public static final int CLOTHING_FIX_COUNT = 2;

    public static final int COMPANY_FIX_COUNT = 6;

    public static final String CUSTOME = VIPApplication.getContext().getString(R.string.custom_zh);

    public static final String CUSTOM_CATEGORY_CONTENT_DIR_NAME = "category_module_custom";

    public static final String CUSTOM_CATEGORY_CONTENT_PATH = (Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/com.ctv.welcome/category_module_custom/");

    public static final int CUSTOM_CATEGORY_NUM = 18;

    public static final String CUSTOM_THEME_MODULE_PATH = (CUSTOM_CATEGORY_CONTENT_PATH + VIPApplication
            .getContext().getString(R.string.custom_zh));

    public static final boolean DEBUG = true;

    public static final int EDUCATION_FIX_COUNT = 3;

    public static final int GOVERMENT_FIX_COUNT = 2;

    public static final String INNER_STORAGE = "inner";

    public static final String INNER_STOREAGE_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath();

    public static final int MEDICAL_FIX_COUNT = 2;

    public static final int MENUFATURE_FIX_COUNT = 2;

    public static final String ROOT = (Environment.getExternalStorageDirectory() + "/");

    public static final String SAVE_FOLDER_NAME = VIPApplication.getContext().getString(
            R.string.vip_reception);

    public static final String SDBACKUPPATH = "vip_backup_pic";

    public static final String SDCARD = "sdcard";

    public static final String SIGNATURE_PICTURE = VIPApplication.getContext().getString(
            R.string.signature_picture);

    public static final String THEME_PICTURE = VIPApplication.getContext().getString(
            R.string.theme_picture);

    public static final int TOBACCO_FIX_COUNT = 2;

    public static final int TRAFFIC_FIX_COUNT = 2;

    public static final String UDISK = "usb";

    public static final String UDISK_PATH = "/mnt/usb/";

    public static final String USB_PATH = "mnt/usb";

    public static final int WELCOME_FIX_COUNT = 15;

    public static final float WINDOW_SCALE_RATIO = 0.8f;
}
