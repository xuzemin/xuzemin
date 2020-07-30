package com.ctv.settings.device.bean;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class ThemeStyleItem {
    private String title;
    private Class<?> activity;
    private int imageResource;
    public ThemeStyleItem() {
    }

    public ThemeStyleItem(String title,int imageResource, Class<?> activity) {
        this.title = title;
        this.activity = activity;
        this.imageResource = imageResource;
    }

    public int getImageResource() {
        return imageResource;
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