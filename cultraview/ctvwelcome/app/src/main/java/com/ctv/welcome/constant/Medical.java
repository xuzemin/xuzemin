
package com.ctv.welcome.constant;

import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Medical {
    public static final int[] ICON = new int[] {
            R.drawable.ic_academic_exchange_icon, R.drawable.ic_research_report_icon
    };

    public static final int[] IMAGE = new int[] {
            R.drawable.ic_academic_exchange, R.drawable.ic_research_report
    };

    public static final String[] MAINTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:63px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.academic_exchange) + "</font>",
            "<font style=\"font-size:60px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.research_report) + "</font>"
    };

    public static final float[] MAINTITLE_X = new float[] {
            950.0f, 105.0f
    };

    public static final float[] MAINTITLE_Y = new float[] {
            234.0f, 264.0f
    };

    public static final String[] SUBTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:25px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.hospital_exchange) + "</font>",
            "<font style=\"font-size:26px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.xx_research_report)
                    + "</font>"
    };

    public static final float[] SUBTITLE_X = new float[] {
            1021.0f, 105.0f
    };

    public static final float[] SUBTITLE_Y = new float[] {
            436.0f, 462.0f
    };

    public static final int[] TEXTS = new int[] {
            R.string.academic_exchange, R.string.research_report
    };

    public static final int[] TITLES = new int[] {
            R.string.academic_exchange, R.string.research_report
    };
}
