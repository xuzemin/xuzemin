
package com.mphotool.whiteboard.view.menuviews;

import java.io.File;
import java.io.IOException;

public class HFile {
    public FileType fileType = FileType.UNKNOW;

    public boolean isSelect;

    public File mFile;

    public enum FileType {
        FOLDER, PDF, IMAGE, MRC,UNKNOW
    }

    public HFile(File file) {
        this.mFile = file;
        setFileType();
    }

    public boolean isImage() {
        if (this.fileType.equals(FileType.IMAGE)) {
            return true;
        }
        return false;
    }

    public boolean isFolder() {
        if (this.fileType.equals(FileType.FOLDER)) {
            return true;
        }
        return false;
    }

    public boolean isBZD() {
        if (this.fileType.equals(FileType.PDF)) {
            return true;
        }
        return false;
    }

    public boolean isAvailFile() {
        if (!(isBZD() || isFolder())) {
            if (!isImage()) {
                return false;
            }
        }
        return true;
    }

    public String getFileName() {
        return this.mFile.getName();
    }

    public String getFilePath() {
        return this.mFile.getPath();
    }

    public boolean isDirectory() {
        return this.mFile.isDirectory();
    }

    public File[] listFiles() {
        return this.mFile.listFiles();
    }

    public String getCanonicalPath() throws IOException {
        return this.mFile.getCanonicalPath();
    }

    public File getFile() {
        return this.mFile;
    }

    public File getParentFile() {
        return this.mFile.getParentFile();
    }

    public long getCreateTime() {
        return this.mFile.lastModified();
    }

    private FileType setFileType() {
        if (this.mFile.isDirectory()) {
            this.fileType = FileType.FOLDER;
        } else if (this.mFile.getName() != null && this.mFile.getName().length() > 0) {
            int dot = this.mFile.getName().lastIndexOf(46);
            if (!(dot == -1 || dot + 1 == this.mFile.getName().length() - 1)) {
                String name = this.mFile.getName()
                        .substring(dot + 1, this.mFile.getName().length()).toLowerCase();
                if (!(name.equals("jpg") || name.equals("bmp"))) {
                    if (!name.equals("png")) {
                        if (name.equals("pdf")) {
                            this.fileType = FileType.PDF;
                        }
                    }
                }
                this.fileType = FileType.IMAGE;
            }
        }
        return this.fileType;
    }

    public String getFileSize() {
        long length = this.mFile.length();
        StringBuilder stringBuilder;
        if (length >= 1048576) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(length / 1048576);
            stringBuilder.append("MB");
            return stringBuilder.toString();
        } else if (length >= 1024) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(length / 1024);
            stringBuilder.append("KB");
            return stringBuilder.toString();
        } else if (length >= 1024) {
            return "0KB";
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(length);
            stringBuilder2.append("B");
            return stringBuilder2.toString();
        }
    }

    public long getFileLength() {
        return this.mFile.length();
    }
}
