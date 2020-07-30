package com.ctv.sourcemenu;

import android.widget.ImageView;
import android.widget.TextView;

public class SourceBean {
    private int imageid;
    private String textView;
    int position;

    public SourceBean(int imageView, String textView, int position) {
        this.imageid = imageView;
        this.textView = textView;
        this.position = position;
    }

    public String getText() {
        return textView;
    }

    public int getPosition() {
        return position;
    }

    public int getIamgeid() {
        return imageid;
    }

}
