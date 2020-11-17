package com.ctv.sourcemenu;

import android.widget.ImageView;
import android.widget.TextView;

public class SourceBean {
    private int imageid;
    private String textView;
    int position;
    private String id_name;

    public SourceBean(int imageView, String textView, int position , String nameid) {
        this.imageid = imageView;
        this.textView = textView;
        this.position = position;
        id_name = nameid;
        }

    public String getText() {
        return textView;
    }

    public int getPosition() {
        return position;
    }
    public String getId_name(){
        return id_name;
    }

    public int getIamgeid() {
        return imageid;
    }

}
