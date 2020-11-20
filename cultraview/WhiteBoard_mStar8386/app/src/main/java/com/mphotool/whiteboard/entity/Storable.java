package com.mphotool.whiteboard.entity;

import com.mphotool.whiteboard.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Storable {
    public static final long serialVersionUID = Constants.serialVersionUID;

    boolean readObject(InputStream inputStream) throws IOException, ClassNotFoundException;

    boolean writeObject(OutputStream outputStream) throws IOException;
}
