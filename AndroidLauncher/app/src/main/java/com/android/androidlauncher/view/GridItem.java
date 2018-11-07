package com.android.androidlauncher.view;

import android.graphics.drawable.Drawable;

public class GridItem {
    private Drawable image;
    private String title;
    public GridItem() {
        super();
    }
    public Drawable getImage() {
        return image;
    }
    public void setImage(Drawable image) {
        this.image = image;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}


