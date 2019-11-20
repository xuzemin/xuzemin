
package com.ctv.welcome.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtil {
    private static final float BIG_OFFSET = 0.9f;

    private static final String KEY_SCREEN_HEIGHT = "screen_height";

    private static final String KEY_SCREEN_OFFSET = "screen_offset";

    private static final String KEY_SCREEN_WIDTH = "screen_width";

    private static final String PREFS_NAME = "mk_common_app_name";

    private static final float SMALL_OFFSET = 0.85f;

    private static final String TAG = "PreferencesUtil";

    private static SharedPreferences prefs;

    public static void init(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public static boolean isBigScreen() {
        return getScreenOffset() == BIG_OFFSET;
    }

    public static void bigScreen() {
        put(KEY_SCREEN_OFFSET, Float.valueOf(BIG_OFFSET));
    }

    public static void smallScreen() {
        put(KEY_SCREEN_OFFSET, Float.valueOf(SMALL_OFFSET));
    }

    public static float getScreenOffset() {
        return prefs.getFloat(KEY_SCREEN_OFFSET, SMALL_OFFSET);
    }

    public static void putScreenWidth(int width) {
        put(KEY_SCREEN_WIDTH, Integer.valueOf(width));
    }

    public static int getScreenWidth() {
        return prefs.getInt(KEY_SCREEN_WIDTH, 0);
    }

    public static void putScreenHeight(int height) {
        put(KEY_SCREEN_HEIGHT, Integer.valueOf(height));
    }

    public static int getScreenHeight() {
        return prefs.getInt(KEY_SCREEN_HEIGHT, 0);
    }

    private static void put(String key, Object value) {
        Class<?> cls = value.getClass();
        Editor editor = prefs.edit();
        if (cls == Boolean.class) {
            editor.putBoolean(key, Boolean.valueOf(value.toString()).booleanValue());
        } else if (cls == Long.class) {
            editor.putLong(key, Long.valueOf(value.toString()).longValue());
        } else if (cls == Integer.class) {
            editor.putInt(key, Integer.parseInt(value.toString()));
        } else if (cls == Float.class || cls == Double.class) {
            editor.putFloat(key, Float.parseFloat(value.toString()));
        } else {
            editor.putString(key, value.toString());
        }
        editor.commit();
    }
}
