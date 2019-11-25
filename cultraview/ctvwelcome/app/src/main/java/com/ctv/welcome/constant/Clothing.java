
package com.ctv.welcome.constant;

import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Clothing {
    public static final int[] ICON = new int[] {
            R.drawable.clothing_new_conference_icon, R.drawable.apparel_industry_exchange_icon
    };

    public static final int[] IMAGE = new int[] {
            R.drawable.clothing_new_conference, R.drawable.apparel_industry_exchange
    };

    public static final String[] MAINTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:63px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.clothing_new_conference_zh)
                    + "</font>",
            "<font style=\"font-size:63px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.design_exchange) + "</font>"
    };

    public static final float[] MAINTITLE_X = new float[] {
            342.0f, 582.0f
    };

    public static final float[] MAINTITLE_Y = new float[] {
            242.0f, 581.0f
    };

    public static final String[] SUBTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:26px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.xx_company) + "</font>",
            "<font style=\"font-size:26px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.time_201812) + "</font>"
    };

    public static final float[] SUBTITLE_X = new float[] {
            624.0f, 731.0f
    };

    public static final float[] SUBTITLE_Y = new float[] {
            450.0f, 811.0f
    };

    public static final int[] TEXTS = new int[] {
            R.string.clothing_new_conference, R.string.apparel_industry_exchange
    };

    public static final int[] TITLES = new int[] {
            R.string.clothing_new_conference, R.string.apparel_industry_exchange
    };
}
