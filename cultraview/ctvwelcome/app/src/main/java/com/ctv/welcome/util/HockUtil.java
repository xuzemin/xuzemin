
package com.ctv.welcome.util;

import android.os.Build.VERSION;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HockUtil {
    public static void hookWebView() {
        int sdkInt = VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            if (field.get(null) == null) {
                Method getProviderClassMethod;
                if (sdkInt > 22) {
                    getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass",
                            new Class[0]);
                } else if (sdkInt == 22) {
                    getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass",
                            new Class[0]);
                } else {
                    return;
                }
                getProviderClassMethod.setAccessible(true);
                Constructor<?> providerConstructor = ((Class) getProviderClassMethod.invoke(
                        factoryClass, new Object[0])).getConstructor(new Class[] {
                    Class.forName("android.webkit.WebViewDelegate")
                });
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true);
                    factoryClass.getDeclaredConstructor(new Class[0]).setAccessible(true);
                    field.set("sProviderInstance", providerConstructor.newInstance(new Object[] {
                            factoryClass.newInstance()
                    }));
                }
            }
        } catch (Throwable th) {
        }
    }
}
