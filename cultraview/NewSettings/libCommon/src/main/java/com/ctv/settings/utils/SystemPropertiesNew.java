package com.ctv.settings.utils;

import android.os.SystemProperties;

/**
 * 系统属性获得和设值
 * @author wanghang
 * @date 2019/09/20
 */
public class SystemPropertiesNew {

    /**
     * 根据Key获取值.
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        return SystemProperties.get(key);
    }

    /**
     * 根据Key获取值.
     *
     * @param key
     * @param def
     * @return
     */
    public static String get(String key, String def){
        return SystemProperties.get(key, def);
    }


    /**
     * 根据Key获取Int值.
     *
     * @param key
     * @param def
     * @return
     */
    public static int getInt(String key, int def){
        return SystemProperties.getInt(key, def);
    }

    /**
     * 根据Key获取long值.
     *
     * @param key
     * @param def
     * @return
     */
    public static long getLong(String key, long def){
        return SystemProperties.getLong(key, def);
    }

    /**
     * 根据Key获取boolean值.
     *
     * @param key
     * @param def
     * @return
     */
    public static Boolean getBoolean(String key, boolean def){
        return SystemProperties.getBoolean(key, def);
    }

    /**
     * 根据给定的key和值设置属性
     *
     * @param key
     * @param val
     */
    public static void set(String key, String val){
        SystemProperties.set(key, val);
    }
}