package com.hht.android.sdk.boardInfo;

/**
 * 关于当前编译版本信息，以及版本相关的系统变量提取。
 * Information about the current build, extracted from system properties.
 */
public class HHTBuild {
    public static final String UNKNOWN="UNKNOWN";
    public HHTBuild(){

    }

    /**
     * 板卡的型号，客户号。
     * Various strings.
     */
    static class BOARD{

    }

    /**
     * 各种版本。
     * Various version strings.
     */
    public static class VERSION{
        public static final String BUILD_VERSION="";
        public static final String PRODUCT_VERSION="";
    }

}
