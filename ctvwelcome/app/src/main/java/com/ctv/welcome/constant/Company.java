
package com.ctv.welcome.constant;

import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Company {
    public static final int[] ICON = new int[] {
            R.drawable.nomal_meet_icon, R.drawable.ic_product_release_icon,
            R.drawable.ic_brainstorm_icon, R.drawable.ldkc_icon,
            R.drawable.staff_communication_icon, R.drawable.annual_summary_icon
    };

    public static final int[] IMAGE = new int[] {
            R.drawable.nomal_meet, R.drawable.ic_product_release, R.drawable.ic_brainstorm,
            R.drawable.ldkc, R.drawable.staff_communication, R.drawable.annual_summary
    };

    public static final String[] MAINTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:59px;\" color=\"#dc7a0c\">"
                    + VIPApplication.getContext().getString(R.string.department_meeting)
                    + "</font>",
            "<font style=\"font-size:60px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.product_launch) + "</font>",
            "<font style=\"font-size:62px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.expansion_of_thinking)
                    + "</font>",
            "<font style=\"font-size:39px;\" color=\"#a85806\">"
                    + VIPApplication.getContext().getString(R.string.welcome_leaders_guide)
                    + "</font>",
            "<font style=\"font-size:63px;\" color=\"#13aad9\">"
                    + VIPApplication.getContext().getString(R.string.exchange) + "</font>",
            "<font style=\"font-size:60px;\" color=\"#a85806\">"
                    + VIPApplication.getContext().getString(R.string.annual_summary_report)
                    + "</font>"
    };

    public static final float[] MAINTITLE_X = new float[] {
            525.0f, 486.0f, 87.0f, 1084.0f, 657.0f, 516.0f
    };

    public static final float[] MAINTITLE_Y = new float[] {
            212.0f, 402.0f, 170.0f, 433.0f, 344.0f, 375.0f
    };

    public static final String[] SUBTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:25px;\" color=\"#dc7a0c\">"
                    + VIPApplication.getContext().getString(R.string.discuss_meeting_theme)
                    + "</font>",
            "<font style=\"font-size:26px;\" color=\"#000000\">"
                    + VIPApplication.getContext().getString(R.string.time_2018XXXX) + "</font>",
            "<font style=\"font-size:25px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.discussion) + "</font>",
            "<font style=\"font-size:20px;\" color=\"#a85806\">"
                    + VIPApplication.getContext().getString(R.string.come_company_guide)
                    + "</font>",
            "<font style=\"font-size:25px;\" color=\"#13aad9\">"
                    + VIPApplication.getContext().getString(R.string.talk_about_topics) + "</font>",
            "<font style=\"font-size:23px;\" color=\"#a85806\">"
                    + VIPApplication.getContext().getString(R.string.xx_company) + "</font>"
    };

    public static final float[] SUBTITLE_X = new float[] {
            1106.0f, 1032.0f, 87.0f, 1201.0f, 697.0f, 1162.0f
    };

    public static final float[] SUBTITLE_Y = new float[] {
            460.0f, 596.0f, 396.0f, 554.0f, 526.0f, 552.0f
    };

    public static final int[] TEXTS = new int[] {
            R.string.general_meeting, R.string.product_release, R.string.brainstorm,
            R.string.leaders_visit, R.string.staff_exchange, R.string.annual_summary
    };

    public static final int[] TITLES = new int[] {
            R.string.general_meeting, R.string.product_release, R.string.brainstorm,
            R.string.leaders_visit, R.string.staff_exchange, R.string.annual_summary
    };
}
