
package com.ctv.welcome.constant;

import com.ctv.welcome.R;

public final class Constants {
    public static final int FILE = 0;

    public static final int FOLDER = 1;

    public static final String[] IMAGE_FILTER = new String[] {
            "jpg", "jpeg", "png", "bmp"
    };

    public static final String VIPRECEPTION_STORAGE_PATH = "vipreception_storage_path";

    public static final String VIPRECEPTION_THRESHOLD = "vipreception_threshold";

    public static final String VIPRECEPTION_WINDOW_MODE = "vipreception_window_mode";

    public static final int[] mCategoryIcons = new int[] {
            R.drawable.ic_welcome_m,
            R.drawable.ic_company, R.drawable.ic_government, R.drawable.ic_medical,
            R.drawable.ic_education, R.drawable.ic_advertising, R.drawable.ic_traffic,
            R.drawable.ic_custom
    };

    public static final int[] mCategorySelIcons = new int[] {
            R.drawable.ic_welcome_selected_m,
            R.drawable.ic_company_selected, R.drawable.ic_government_selected,
            R.drawable.ic_medical_selected, R.drawable.ic_education_selected,
            R.drawable.ic_advertising_selected, R.drawable.ic_traffic_selected,
            R.drawable.ic_custom_selected
    };

    public static final int[] mCategoryTitles = new int[] {
            R.string.welcome_module,
            R.string.company, R.string.government, R.string.medical, R.string.education,
            R.string.ad, R.string.traffic,
             R.string.custom
    };

    public static final int[] mCustomIcons = new int[] {
            R.drawable.folder, R.drawable.folder
    };

    public static final int[] mCustomTitles = new int[] {
            R.string.custom_theme, R.string.signature
    };
}
