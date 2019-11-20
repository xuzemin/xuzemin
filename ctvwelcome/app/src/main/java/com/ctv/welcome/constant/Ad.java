
package com.ctv.welcome.constant;

import com.ctv.welcome.R;
import com.ctv.welcome.task.VIPApplication;

public final class Ad {
    public static final int[] ICON = new int[] {
            R.drawable.product_icon, R.drawable.ic_style_show_icon,
            R.drawable.ic_public_service_ads_icon
    };

    public static final int[] IMAGE = new int[] {
            R.drawable.product, R.drawable.ic_style_show, R.drawable.ic_public_service_ads
    };

    public static final String[] MAINTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:69px;\" color=\"#ff000c\">"
                    + VIPApplication.getContext().getString(R.string.product_promotion) + "</font>",
            "<font style=\"font-size:64px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.style_show) + "</font>",
            "<font style=\"font-size:60px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.public_service_ads)
                    + "</font>"
    };

    public static final float[] MAINTITLE_X = new float[] {
            657.0f, 728.0f, 722.0f
    };

    public static final float[] MAINTITLE_Y = new float[] {
            222.0f, 324.0f, 334.0f
    };

    public static final String[] SUBTITLE_HTMLTEXT = new String[] {
            "<font style=\"font-size:31px;\" color=\"#ff000c\">"
                    + VIPApplication.getContext().getString(R.string.company_product_promotion)
                    + "</font>",
            "<font style=\"font-size:30px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.theme_style_display)
                    + "</font>",
            "<font style=\"font-size:25px;\" color=\"#ffffff\">"
                    + VIPApplication.getContext().getString(R.string.public_interest) + "</font>"
    };

    public static final float[] SUBTITLE_X = new float[] {
            1007.0f, 1181.0f, 792.0f
    };

    public static final float[] SUBTITLE_Y = new float[] {
            467.0f, 550.0f, 504.0f
    };

    public static final int[] TEXTS = new int[] {
            R.string.product_promotion, R.string.style_show, R.string.public_service_ads
    };

    public static final int[] TITLES = new int[] {
            R.string.product_promotion, R.string.style_show, R.string.public_service_ads
    };
}
