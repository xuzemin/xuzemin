
package com.ctv.welcome.vo;

public class PicData {
    private String codePath;

    private String fileName;

    private String filePath;

    private boolean fileType;

    private String htmlText;

    private Long id;

    private float x;

    private float y;

    public PicData(Long id, String fileName, boolean fileType, String filePath, String codePath) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.codePath = codePath;
    }

    public PicData(Long id, String fileName, boolean fileType, String filePath, String htmlText,
            float x, float y, String codePath) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.htmlText = htmlText;
        this.x = x;
        this.y = y;
        this.codePath = codePath;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean getFileType() {
        return this.fileType;
    }

    public void setFileType(boolean fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getHtmlText() {
        return this.htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getCodePath() {
        return this.codePath;
    }

    public void setCodePath(String codePath) {
        this.codePath = codePath;
    }
}
