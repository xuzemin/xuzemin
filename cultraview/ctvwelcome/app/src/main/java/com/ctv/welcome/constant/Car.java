
package com.ctv.welcome.constant;

import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Car {
    public static final int[] ICON = new int[] {
            R.drawable.motor_show_icon, R.drawable.car_conference_icon
    };

    public static final int[] IMAGE = new int[] {
            R.drawable.motor_show, R.drawable.car_conference
    };

    public static final String[] MAINTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:60px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.motor_show_zh) + "</font>",
            "<font style=\"font-size:60px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.car_conference_zh) + "</font>"
    };

    public static final float[] MAINTITLE_X = new float[] {
            1121.0f, 28.0f
    };

    public static final float[] MAINTITLE_Y = new float[] {
            91.0f, 102.0f
    };

    public static final String[] SUBTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:26px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.motor_show_2018) + "</font>",
            "<font style=\"font-size:26px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.xx_car_company) + "</font>"
    };

    public static final float[] SUBTITLE_X = new float[] {
            1239.0f, 28.0f
    };

    public static final float[] SUBTITLE_Y = new float[] {
            271.0f, 282.0f
    };

    public static final int[] TEXTS = new int[] {
            R.string.motor_show, R.string.car_conference
    };

    public static final int[] TITLES = new int[] {
            R.string.motor_show, R.string.car_conference
    };
}
