package com.mphotool.whiteboard.folderchooser;

import java.io.File;

public class FileUtils {
    public File mFile;
    private String filePath;
    private String fileName;
    private long fileTime;
    private int fileType; // 0 pdf, 2 jpg/png/bmp, 3 mrc, 4 file

    public boolean isSelect;

    public FileUtils(File mfile){
        this.mFile = mfile;
    }

    public File getFile() {
        return this.mFile;
    }

    public File[] listFiles() {
        return this.mFile.listFiles();
    }

    public File getParentFile() {
        return this.mFile.getParentFile();
    }

    public void setFilePath(String path){
        this.filePath = path;
    }

    public String getFilePath(){
        return this.mFile.getPath();
    }

    public void setFileName(String name){
        this.fileName = name;
    }

    public String getFileName(){
        return this.mFile.getName();
    }

/*    public void setFileTime(long time){
        this.fileTime = time;
    }

    public long getFileTime(){
        return this.fileTime;
    }*/

    public void setFileType(int type){
        this.fileType = type;
    }

    public int getFileType(){
        return this.fileType;
    }

    public long getCreateTime() {
        return this.mFile.lastModified();
    }
}
