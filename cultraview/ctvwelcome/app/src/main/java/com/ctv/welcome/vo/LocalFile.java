
package com.ctv.welcome.vo;

public class LocalFile {
    private String fileName;

    private String filePath;

    private String filePathText = "";

    private int fileType;

    private String modifyTime;

    private String size;

    private String suffix;

    public LocalFile() {

    }
    public LocalFile(String filePath, String fileName, String suffix, int fileType,
            String filePathText) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.suffix = suffix;
        this.fileType = fileType;
        this.filePathText = filePathText;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public int getFileType() {
        return this.fileType;
    }

    public String getFilePathText() {
        return this.filePathText;
    }

    public void setFilePathText(String filePathText) {
        this.filePathText = filePathText;
    }

    public String getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
