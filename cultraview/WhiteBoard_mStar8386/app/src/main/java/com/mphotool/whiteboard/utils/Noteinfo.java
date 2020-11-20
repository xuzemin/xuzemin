package com.mphotool.whiteboard.utils;

import android.widget.ImageView;

import java.io.File;

public class Noteinfo {
    private File mFile;
    private String Name;
    private String PathName;
    private ImageView Img;

    public void setName(String name){
        this.Name = name;
    }

    public String getName(){
        return this.Name;
    }

    public void setFile(File file){
        this.mFile = file;
    }

    public File getmFile(){
        return this.mFile;
    }
    public void setPathName(String pathName){
        this.PathName = pathName;
    }

    public String getPathName(){
        return this.PathName;
    }
}