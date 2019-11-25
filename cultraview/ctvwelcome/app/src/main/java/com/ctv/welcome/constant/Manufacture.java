
package com.ctv.welcome.constant;

import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Manufacture {
    public static final int[] ICON = new int[] {
            R.drawable.manufactur_industry_analysis_icon, R.drawable.manufactur_environment_icon
    };

    public static final int[] IMAGE = new int[] {
            R.drawable.manufactur_industry_analysis, R.drawable.manufactur_environment
    };

    public static final String[] MAINTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:63px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.manufacture_industry_analysis)
                    + "</font>",
            "<font style=\"font-size:63px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.manufactur_environment_zh)
                    + "</font>"
    };

    public static final float[] MAINTITLE_X = new float[] {
            33.0f, 140.0f
    };

    public static final float[] MAINTITLE_Y = new float[] {
            211.0f, 467.0f
    };

    public static final String[] SUBTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:29px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.manufacture_industry)
                    + "</font>",
            "<font style=\"font-size:29px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.xx_company) + "</font>"
    };

    public static final float[] SUBTITLE_X = new float[] {
            33.0f, 140.0f
    };

    public static final float[] SUBTITLE_Y = new float[] {
            412.0f, 670.0f
    };

    public static final int[] TEXTS = new int[] {
            R.string.manufactur_industry_analysis, R.string.manufactur_environment
    };

    public static final int[] TITLES = new int[] {
            R.string.manufactur_industry_analysis, R.string.manufactur_environment
    };
}
