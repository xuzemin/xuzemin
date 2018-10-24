package com.android.androidlauncher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileHandle {
    private static FileHandle fileHandle;
//    private static String filename;
    private FileInputStream isfile;
    public static FileHandle getFileHandle() {
        if(fileHandle == null){
            fileHandle = new FileHandle();
        }
        return fileHandle;
    }

    public void readFile(String mfilename){
        try {
            isfile = new FileInputStream(new File(mfilename));
            int length = 0;
            byte[] content = new byte[2048];
            length = isfile.read(content,length,content.length-length);
            if(length != 0 ){
                MyConstant.CurrentNumber = 0;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
