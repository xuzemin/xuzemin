
package com.ctv.welcome.vo;

import android.view.View;
import com.ctv.welcome.constant.Config;
import com.ctv.welcome.constant.DishType;

public final class XZDish {
    private View layout;

    private String name;

    private String path;

    private DishType type;

    private XZDish(String path, DishType type, View layout) {
        this.path = path;
        this.type = type;
        this.layout = layout;
    }

    public static XZDish getDish(String path, DishType type, View layout) {
        return new XZDish(path, type, layout);
    }

    public static XZDish getLocalDish(View layout) {
        return new XZDish(Config.ROOT, DishType.LOCALE, layout);
    }

    public static String getLocalPath() {
        return Config.ROOT;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public View getLayout() {
        return this.layout;
    }

    public void setLayout(View layout) {
        this.layout = layout;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DishType getType() {
        return this.type;
    }

    public String toString() {
        return "XZDish{name='" + this.name + '\'' + ", path='" + this.path + '\'' + ", type="
                + this.type + ", layout=" + this.layout + '}';
    }
}
