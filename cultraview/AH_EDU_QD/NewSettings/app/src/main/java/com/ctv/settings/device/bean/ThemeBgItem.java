package com.ctv.settings.device.bean;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class ThemeBgItem {
    private String title;
    private Class<?> activity;
    private int imageResource;
    private String imageResourceName;

    public ThemeBgItem(String title, int imageResource, Class<?> activity) {
        this.title = title;
        this.activity = activity;
        this.imageResource = imageResource;
    }

    public ThemeBgItem(String title, String name, Class<?> activity) {
        this.title = title;
        this.activity = activity;
        this.imageResourceName = name;
    }

    public int getImageResource() {
        return imageResource;
    }
    public String getImageResourceName() {
        return imageResourceName;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Class<?> getActivity() {
        return activity;
    }

    public void setActivity(Class<?> activity) {
        this.activity = activity;
    }
}