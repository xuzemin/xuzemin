
package com.ctv.welcome.constant;

import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Government {
    public static final int[] ICON = new int[] {
            R.drawable.ic_top_interview_icon, R.drawable.ic_news_icon
    };

    public static final int[] IMAGE = new int[] {
            R.drawable.ic_top_interview, R.drawable.ic_news
    };

    public static final String[] MAINTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:64px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.high_end_interview)
                    + "</font>",
            "<font style=\"font-size:49px;\" color=\"#fde839\">"
                    + VIPApplication.getContext().getString(R.string.data_news) + "</font>"
    };

    public static final float[] MAINTITLE_X = new float[] {
            650.0f, 462.0f
    };

    public static final float[] MAINTITLE_Y = new float[] {
            361.0f, 317.0f
    };

    public static final String[] SUBTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:28px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.leaders_interview) + "</font>",
            "<font style=\"font-size:30px;\" color=\"#fde839\">"
                    + VIPApplication.getContext().getString(R.string.analysis_market_data)
                    + "</font>"
    };

    public static final float[] SUBTITLE_X = new float[] {
            757.0f, 1003.0f
    };

    public static final float[] SUBTITLE_Y = new float[] {
            562.0f, 368.0f
    };

    public static final int[] TEXTS = new int[] {
            R.string.high_end_interview, R.string.data_news
    };

    public static final int[] TITLES = new int[] {
            R.string.high_end_interview, R.string.data_news
    };
}
