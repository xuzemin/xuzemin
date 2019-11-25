
package com.ctv.welcome.constant;

import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Education {
    public static final int[] ICON = new int[] {
            R.drawable.ic_activities_icon, R.drawable.ic_parents_meeting_icon,
            R.drawable.ic_student_exchange_icon
    };

    public static final int[] IMAGE = new int[] {
            R.drawable.ic_activities, R.drawable.ic_parents_meeting, R.drawable.ic_student_exchange
    };

    public static final String[] MAINTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:62px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.xxx_activities) + "</font>",
            "<font style=\"font-size:65px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.welcome_parents) + "</font>",
            "<font style=\"font-size:62px;\" color=\"#fde839\">"
                    + VIPApplication.getContext().getString(R.string.student_exchange) + "</font>"
    };

    public static final float[] MAINTITLE_X = new float[] {
            699.0f, 625.0f, 87.0f
    };

    public static final float[] MAINTITLE_Y = new float[] {
            180.0f, 337.0f, 128.0f
    };

    public static final String[] SUBTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:28px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.class_activities) + "</font>",
            "<font style=\"font-size:28px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.grade_class) + "</font>",
            "<font style=\"font-size:21px;\" color=\"#fde839\">"
                    + VIPApplication.getContext().getString(R.string.class_carry_theme_exchange)
                    + "</font>"
    };

    public static final float[] SUBTITLE_X = new float[] {
            699.0f, 1189.0f, 287.0f
    };

    public static final float[] SUBTITLE_Y = new float[] {
            482.0f, 586.0f, 245.0f
    };

    public static final int[] TEXTS = new int[] {
            R.string.activities, R.string.parents_meeting, R.string.student_exchange
    };

    public static final int[] TITLES = new int[] {
            R.string.activities, R.string.parents_meeting, R.string.student_exchange
    };
}
