
package com.ctv.welcome.task;

import android.content.Context;

import androidx.core.os.EnvironmentCompat;

import dalvik.system.DexFile;
import java.io.File;

public class SystemPropertiesProxy {
    public static String getPro(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            return (String) c.getMethod("get", new Class[] {
                    String.class, String.class
            }).invoke(c, new Object[] {
                    key, EnvironmentCompat.MEDIA_UNKNOWN
            });
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        } catch (Throwable th) {
            return value;
        }
    }

    public static String get(Context context, String key) throws IllegalArgumentException {
        String ret = "";
        try {
            Class SystemProperties = context.getClassLoader().loadClass(
                    "android.os.SystemProperties");
            return (String) SystemProperties.getMethod("get", new Class[] {
                String.class
            }).invoke(SystemProperties, new Object[] {
                new String(key)
            });
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            return "";
        }
    }

    public static String get(Context context, String key, String def)
            throws IllegalArgumentException {
        String ret = def;
        try {
            Class SystemProperties = context.getClassLoader().loadClass(
                    "android.os.SystemProperties");
            return (String) SystemProperties.getMethod("get", new Class[] {
                    String.class, String.class
            }).invoke(SystemProperties, new Object[] {
                    new String(key), new String(def)
            });
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            return def;
        }
    }

    public static Integer getInt(Context context, String key, int def)
            throws IllegalArgumentException {
        Integer ret = Integer.valueOf(def);
        try {
            Class SystemProperties = context.getClassLoader().loadClass(
                    "android.os.SystemProperties");
            return (Integer) SystemProperties.getMethod("getInt", new Class[] {
                    String.class, Integer.TYPE
            }).invoke(SystemProperties, new Object[] {
                    new String(key), new Integer(def)
            });
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = Integer.valueOf(def);
            System.out.println(e.toString());
            return ret;
        }
    }

    public static Long getLong(Context context, String key, long def)
            throws IllegalArgumentException {
        Long ret = Long.valueOf(def);
        try {
            Class SystemProperties = context.getClassLoader().loadClass(
                    "android.os.SystemProperties");
            return (Long) SystemProperties.getMethod("getLong", new Class[] {
                    String.class, Long.TYPE
            }).invoke(SystemProperties, new Object[] {
                    new String(key), new Long(def)
            });
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            return Long.valueOf(def);
        }
    }

    public static Boolean getBoolean(Context context, String key, boolean def)
            throws IllegalArgumentException {
        Boolean ret = Boolean.valueOf(def);
        try {
            Class SystemProperties = context.getClassLoader().loadClass(
                    "android.os.SystemProperties");
            return (Boolean) SystemProperties.getMethod("getBoolean", new Class[] {
                    String.class, Boolean.TYPE
            }).invoke(SystemProperties, new Object[] {
                    new String(key), new Boolean(def)
            });
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            return Boolean.valueOf(def);
        }
    }

    public static void set(Context context, String key, String val) throws IllegalArgumentException {
        try {
            DexFile df = new DexFile(new File("/system/app/Settings.apk"));
            ClassLoader cl = context.getClassLoader();
            Class SystemProperties = Class.forName("android.os.SystemProperties");
            SystemProperties.getMethod("set", new Class[] {
                    String.class, String.class
            }).invoke(SystemProperties, new Object[] {
                    new String(key), new String(val)
            });
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
        }
    }

    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            c.getMethod("set", new Class[] {
                    String.class, String.class
            }).invoke(c, new Object[] {
                    key, value
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
