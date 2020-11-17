
package com.ctv.welcome.constant;

import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Tobacco {
    public static final int[] ICON = new int[] {
            R.drawable.tobacco_advertising_icon, R.drawable.tobacco_industry_analysis_icon
    };

    public static final int[] IMAGE = new int[] {
            R.drawable.tobacco_advertising, R.drawable.tobacco_industry_analysis
    };

    public static final String[] MAINTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:60px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.xxxx_advertising) + "</font>",
            "<font style=\"font-size:60px;\" color=\"#fde839\">"
                    + VIPApplication.getContext().getString(R.string.tobacco_industry_analysis_zh)
                    + "</font>"
    };

    public static final float[] MAINTITLE_X = new float[] {
            100.0f, 601.0f
    };

    public static final float[] MAINTITLE_Y = new float[] {
            264.0f, 378.0f
    };

    public static final String[] SUBTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:25px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.time_201801) + "</font>",
            "<font style=\"font-size:25px;\" color=\"#fde839\">"
                    + VIPApplication.getContext().getString(R.string.time_201812) + "</font>"
    };

    public static final float[] SUBTITLE_X = new float[] {
            100.0f, 947.0f
    };

    public static final float[] SUBTITLE_Y = new float[] {
            469.0f, 563.0f
    };

    public static final int[] TEXTS = new int[] {
            R.string.tobacco_advertising, R.string.tobacco_industry_analysis
    };

    public static final int[] TITLES = new int[] {
            R.string.tobacco_advertising, R.string.tobacco_industry_analysis
    };
}
