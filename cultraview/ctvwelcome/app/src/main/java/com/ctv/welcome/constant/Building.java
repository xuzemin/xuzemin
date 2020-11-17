
package com.ctv.welcome.constant;

import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Building {
    public static final int[] ICON = new int[] {
            R.drawable.program_exchange_meeting_icon, R.drawable.architectural_academic_forum_icon
    };

    public static final int[] IMAGE = new int[] {
            R.drawable.program_exchange_meeting, R.drawable.architectural_academic_forum
    };

    public static final String[] MAINTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:62px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.program_exchange_meeting_zh)
                    + "</font>",
            "<font style=\"font-size:62px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(
                            R.string.architectural_academic_forum_zh) + "</font>"
    };

    public static final float[] MAINTITLE_X = new float[] {
            666.0f, 674.0f
    };

    public static final float[] MAINTITLE_Y = new float[] {
            156.0f, 347.0f
    };

    public static final String[] SUBTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:26px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.xx_company) + "</font>",
            "<font style=\"font-size:26px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.xx_company) + "</font>"
    };

    public static final float[] SUBTITLE_X = new float[] {
            819.0f, 947.0f
    };

    public static final float[] SUBTITLE_Y = new float[] {
            336.0f, 558.0f
    };

    public static final int[] TEXTS = new int[] {
            R.string.program_exchange_meeting, R.string.architectural_academic_forum
    };

    public static final int[] TITLES = new int[] {
            R.string.program_exchange_meeting, R.string.architectural_academic_forum
    };
}
