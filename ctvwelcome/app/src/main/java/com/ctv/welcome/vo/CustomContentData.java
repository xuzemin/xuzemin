
package com.ctv.welcome.vo;

public class CustomContentData {
    private String filename;

    private Object filepath;

    private boolean isDelete;

    public Object getFilepath() {
        return this.filepath;
    }

    public void setFilepath(Object filepath) {
        this.filepath = filepath;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isDelete() {
        return this.isDelete;
    }

    public void setDelete(boolean delete) {
        this.isDelete = delete;
    }
}
