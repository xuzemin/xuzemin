
package com.ctv.welcome.constant;

import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Traffic {
    public static final int[] ICON = new int[] {
            R.drawable.safety_common_sense_icon, R.drawable.traffic_sign_introduction_icon
    };

    public static final int[] IMAGE = new int[] {
            R.drawable.safety_common_sense, R.drawable.traffic_sign_introduction
    };

    public static final String[] MAINTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:66px;\" color=\"#188224\">"
                    + VIPApplication.getContext().getString(R.string.safety_knowledge_lecture)
                    + "</font>",
            "<font style=\"font-size:66px;\" color=\"#fde839\">"
                    + VIPApplication.getContext().getString(R.string.logo_introduced) + "</font>"
    };

    public static final float[] MAINTITLE_X = new float[] {
            28.0f, 423.0f
    };

    public static final float[] MAINTITLE_Y = new float[] {
            211.0f, 204.0f
    };

    public static final String[] SUBTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:26px;\" color=\"#188224\">"
                    + VIPApplication.getContext().getString(R.string.safety_knowledge) + "</font>",
            "<font style=\"font-size:26px;\" color=\"#fde839\">"
                    + VIPApplication.getContext().getString(R.string.introduced) + "</font>"
    };

    public static final float[] SUBTITLE_X = new float[] {
            28.0f, 423.0f
    };

    public static final float[] SUBTITLE_Y = new float[] {
            396.0f, 433.0f
    };

    public static final int[] TEXTS = new int[] {
            R.string.safety_common_sense, R.string.traffic_sign_introduction
    };

    public static final int[] TITLES = new int[] {
            R.string.safety_common_sense, R.string.traffic_sign_introduction
    };
}
